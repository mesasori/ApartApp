@file:Suppress("TooManyFunctions")

package com.example.apartapp.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.apartapp.R
import com.example.apartapp.data.GeoObjectHolder
import com.example.apartapp.databinding.FragmentMapBinding
import com.example.apartapp.ui.details.GeoDetailsDialogFragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MapFragment : Fragment() {

    private val viewModel: MapFragmentViewModel by viewModels()

    // binding
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var map: Map
    private lateinit var mapWindow: MapWindow

    // Search Session
    private lateinit var searchManager: SearchManager
    private lateinit var searchSession: Session

    // Text search
    private lateinit var editQueryTextWatcher: TextWatcher

    // Recycler View
    private val suggestAdapter = SuggestListAdapter()

    private var placemarkMapObject: PlacemarkMapObject? = null

    private val objectTapListener = GeoObjectTapListener { it ->
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener false
        deletePlacemark()

        val selectionMetadata: GeoObjectSelectionMetadata = it
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)

        map.selectGeoObject(selectionMetadata)
        map.cameraPosition.run {
            map.move(
                CameraPosition(point, getFutureZoom(zoom), azimuth, tilt),
                MOVE_ANIMATION,
                null
            )
        }
        Log.d("TAP INFO", "poi tap")

        val searchSessionZoom = getSearchSessionZoom(currentZoom = map.cameraPosition.zoom)

        searchSession =
            searchManager.submit(point, searchSessionZoom, SearchOptions(), searchListener)
        true
    }

    private val mapObjectTapListener = MapObjectTapListener { mapObject, point ->
        GeoObjectHolder.tappedGeo = (mapObject.userData as GeoObject)

        showGeoDetailsDialogFragment()

        true
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            map.deselectGeoObject()
            deletePlacemark()
            Log.d(
                "TAP INFO",
                "simple tap with latitude: ${point.latitude} and longitude: ${point.longitude}"
            )
        }

        override fun onMapLongTap(map: Map, point: Point) {
            map.deselectGeoObject()
            changePlacemark(point)
            Log.d(
                "TAP INFO",
                "long tap with latitude: ${point.latitude} and longitude: ${point.longitude}"
            )

            val searchSessionZoom = getSearchSessionZoom(currentZoom = map.cameraPosition.zoom)
            searchSession =
                searchManager.submit(point, searchSessionZoom, SearchOptions(), searchListener)
        }
    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            GeoObjectHolder.tappedGeo = response.collection.children.firstOrNull()?.obj

            showGeoDetailsDialogFragment()
        }

        override fun onSearchError(error: Error) {
            Log.d("ERROR", error.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMapView()
        viewModel.setVisibleRegion(map.visibleRegion)

        binding.apply {
            listSuggests.adapter = suggestAdapter
            btnZoomPlus.setOnClickListener {
                changeZoomByStep(ZOOM_STEP)
                Log.d("toaster", "zoom in")
            }

            btnZoomMinus.setOnClickListener {
                changeZoomByStep(-ZOOM_STEP)
                Log.d("toaster", "zoom out")
            }

            map.addTapListener(objectTapListener)
            map.addInputListener(inputListener)

            buttonSearch.setOnClickListener {
                viewModel.startSearch()
            }

            buttonReset.setOnClickListener {
                viewModel.reset()
            }

            editQueryTextWatcher = editQuery.doAfterTextChanged { text ->
                if (text.toString() == viewModel.uiState.value.query) return@doAfterTextChanged
                viewModel.setQueryText(text.toString())
            }

            editQuery.setOnEditorActionListener { _, _, _ ->
                viewModel.startSearch()
                true
            }
        }

        viewModel.uiState.flowWithLifecycle(lifecycle).onEach {
            suggestAdapter.items = (it.suggestState as? SuggestState.Success)?.items ?: emptyList()

            val searchItems = (it.searchState as? SearchState.Success)?.items ?: emptyList()
            Log.d("Search Items", searchItems.toString())

            updateSearchResponsePlacemarks(searchItems)

            binding.apply {
                editQuery.apply {
                    if (text.toString() != it.query) {
                        removeTextChangedListener(editQueryTextWatcher)
                        setText(it.query)
                        addTextChangedListener(editQueryTextWatcher)
                    }
                }
                buttonSearch.isEnabled =
                    it.query.isNotEmpty() && it.searchState == SearchState.Off
                buttonReset.isEnabled =
                    it.query.isNotEmpty() || it.searchState !is SearchState.Off

                editQuery.isEnabled = it.searchState is SearchState.Off
            }
        }.launchIn(lifecycleScope)

        viewModel.subscribeForSuggest().flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
        viewModel.subscribeForSearch().flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun setupMapView() {
        mapWindow = binding.mapView.mapWindow
        map = mapWindow.map
        mapObjectCollection = map.mapObjects
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)

        with(map.cameraPosition) {
            map.move(
                CameraPosition(Point(START_LATITUDE, START_LONGITUDE), START_ZOOM, azimuth, tilt),
                MOVE_ANIMATION,
                null
            )
        }
    }

    private fun deletePlacemark() {
        if (placemarkMapObject != null) {
            mapObjectCollection.clear()
            placemarkMapObject = null
        }
    }

    private fun updateSearchResponsePlacemarks(items: List<SearchResponseItem>) {
        map.mapObjects.clear()
        val bitmap =
            getBitmapFromVectorDrawable(requireActivity(), R.drawable.baseline_location_pin_24)
        items.forEach {
            map.mapObjects.addPlacemark().apply {
                geometry = it.point
                setIcon(
                    ImageProvider.fromBitmap(bitmap),
                    IconStyle().apply { scale = PLACEMARK_SCALE })
                addTapListener(mapObjectTapListener)
                userData = it.geoObject
            }
        }

    }

    @Suppress("DEPRECATION")
    private fun changePlacemark(point: Point) {
        if (placemarkMapObject == null) {
            val bitmap =
                getBitmapFromVectorDrawable(requireActivity(), R.drawable.baseline_location_pin_24)
            placemarkMapObject = mapObjectCollection.addPlacemark(
                point,
                ImageProvider.fromBitmap(bitmap),
                IconStyle().apply {
                    scale = PLACEMARK_SCALE
                }
            ).apply {
                isDraggable = true
            }
        } else placemarkMapObject!!.geometry = point
    }

    private fun changeZoomByStep(value: Float) {
        with(map.cameraPosition) {
            map.move(
                CameraPosition(target, zoom + value, azimuth, tilt),
                ZOOM_ANIMATION,
                null
            )
        }
    }

    private fun getFutureZoom(zoom: Float) = if (zoom < DEFAULT_ZOOM) zoom + ZOOM_STEP else zoom

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this.requireActivity())
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        lateinit var bitmap: Bitmap
        drawable?.let {
            bitmap = Bitmap.createBitmap(
                it.intrinsicWidth,
                it.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
        }
        return bitmap
    }

    private fun getSearchSessionZoom(currentZoom: Float): Int {
        return if (currentZoom >= START_ZOOM) ADDRESS_ZOOM.toInt() else currentZoom.toInt()
    }

    private fun showGeoDetailsDialogFragment() {
        val geoDetailsDialogFragment = GeoDetailsDialogFragment()
        geoDetailsDialogFragment.show(
            this@MapFragment.parentFragmentManager,
            GeoDetailsDialogFragment.TAG
        )
    }

    companion object {
        private const val ZOOM_STEP = 1.0f
        private const val START_ZOOM = 10f
        private const val DEFAULT_ZOOM: Float = 13f
        private const val ADDRESS_ZOOM: Float = 16f

        private const val PLACEMARK_SCALE = 1.5f

        private const val START_LATITUDE = 55.756538
        private const val START_LONGITUDE = 37.632592

        private val MOVE_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
        private val ZOOM_ANIMATION = Animation(Animation.Type.LINEAR, 0.2f)
    }
}
