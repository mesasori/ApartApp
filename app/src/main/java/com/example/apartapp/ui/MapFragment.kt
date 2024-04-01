package com.example.apartapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.apartapp.R
import com.example.apartapp.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.SizeChangedListener
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var map: Map
    private lateinit var mapWindow: MapWindow

    private lateinit var searchManager: SearchManager
    private lateinit var searchSession: Session

    private lateinit var imageProvider: ImageProvider

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapWindow = binding.mapView.mapWindow
        map = mapWindow.map
        mapObjectCollection = map.mapObjects.addCollection()
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)

        imageProvider = ImageProvider.fromResource(requireActivity(), R.drawable.ic_placemark)

        binding.apply {
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
        }
    }

    private val objectTapListener = GeoObjectTapListener { it ->
        val point = it.geoObject.geometry.firstOrNull()?.point ?: return@GeoObjectTapListener false

        val selectionMetadata: GeoObjectSelectionMetadata = it
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        map.selectGeoObject(selectionMetadata)

        mapObjectCollection.clear()
        map.cameraPosition.run {
            map.move(
                CameraPosition(point, getFutureZoom(zoom), azimuth, tilt),
                SMOOTH_ANIMATION,
                null
            )
        }

        searchSession = searchManager.submit(point, 20, SearchOptions(), searchListener)
        true
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            mapObjectCollection.clear()
            mapObjectCollection.addPlacemark().apply {
                geometry = point
                setIcon(imageProvider)
            }

            searchSession = searchManager.submit(point, 20, SearchOptions(), searchListener)

            with(map.cameraPosition) {
                val futureZoom = getFutureZoom(zoom)
                map.move(
                    CameraPosition(point, futureZoom, azimuth, tilt),
                    SMOOTH_ANIMATION,
                    null
                )
            }
        }

        override fun onMapLongTap(p0: Map, p1: Point) {
            TODO("TODO smth")
        }

    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val street = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(ToponymObjectMetadata::class.java)
                ?.address
                ?.components
                ?.firstOrNull {it.kinds.contains(Address.Component.Kind.STREET)}
                ?.name ?: "Невозможно определить адресс"

            Toast.makeText(requireActivity(), street, Toast.LENGTH_SHORT).show()
        }

        override fun onSearchError(p0: Error) {
            TODO("Not yet implemented")
        }

    }

    private fun changeZoomByStep(value: Float) {
        with(map.cameraPosition) {
            map.move(
                CameraPosition(target, zoom + value, azimuth, tilt),
                SMOOTH_ANIMATION,
                null
            )
        }
    }

    private fun getFutureZoom(zoom: Float) = if (zoom < DEFAULT_ZOOM) zoom + ZOOM_STEP else zoom

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
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

    companion object {
        private const val ZOOM_STEP = 1.0f
        private const val DEFAULT_ZOOM = 15.0f

        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
    }


}