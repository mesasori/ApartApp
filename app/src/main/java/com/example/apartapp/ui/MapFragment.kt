package com.example.apartapp.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.apartapp.R
import com.example.apartapp.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectInspectionMetadata
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.map.PlacemarkMapObject
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
    private var placemarkMapObject: PlacemarkMapObject? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // map objects initialization
        mapWindow = binding.mapView.mapWindow
        map = mapWindow.map
        mapObjectCollection = map.mapObjects.addCollection()
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        //imageProvider = ImageProvider.fromResource(requireActivity(), R.drawable.ic_placemark)

        with(map.cameraPosition) {
            map.move(
                CameraPosition(Point(55.756538, 37.632592), 10f, azimuth, tilt),
                MOVE_ANIMATION,
                null
            )
        }

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

        searchSession = searchManager.submit(point, DEFAULT_ZOOM.toInt(), SearchOptions(), searchListener)

        true
    }

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
            // Log about random place on map
            map.deselectGeoObject()
            deletePlacemark()
            Log.d("inputListener", "latitude: ${point.latitude}, longitude: ${point.longitude}")
        }

        override fun onMapLongTap(map: Map, point: Point) {
            // After long tap showing some info about object (latitude and longitude)
            map.deselectGeoObject()
            changePlacemark(point)

            Log.d("info about long tap", "${point.latitude} ${point.longitude}")
        }
    }

    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val geoObject = response.collection.children.mapNotNull { it.obj }[0]
            val name = geoObject.name
            val point = geoObject.geometry[0].point!!
            val pointLatitude = point.latitude
            val pointLongitude = point.longitude
            val description = geoObject.descriptionText

            Log.d("OBJECT NAME", name.toString())
            Log.d("OBJECT POINT LATITUDE", pointLatitude.toString())
            Log.d("OBJECT POINT LONGITUDE", pointLongitude.toString())
            Log.d("OBJECT DESCRIPTION", description.toString())

            val metadata = geoObject.metadataContainer
            Log.d("METADATA", metadata.toString())

            val toponymAddress: Address = metadata.getItem(ToponymObjectMetadata::class.java).address
            val postalCode = toponymAddress.postalCode ?: "null"
            val additionalInfo = toponymAddress.additionalInfo ?: "null"
            val countryCode = toponymAddress.countryCode ?: "null"
            val formattedAddress = toponymAddress.formattedAddress ?: "null"

            Log.d("HUETA", "postalCode: $postalCode, additionalInfo: $additionalInfo, countryCode: $countryCode, \n formattedAddress: $formattedAddress")

        }

        override fun onSearchError(error: Error) {
            Log.d("ERROR", error.toString())
        }

    }

    private fun deletePlacemark() {
        if (placemarkMapObject != null) {
            mapObjectCollection.clear()
            placemarkMapObject = null
        }
    }

    private fun changePlacemark(point: Point) {
        if (placemarkMapObject == null) {
            val bitmap = getBitmapFromVectorDrawable(requireActivity(), R.drawable.baseline_location_pin_24)
            placemarkMapObject = mapObjectCollection.addPlacemark(
                point,
                ImageProvider.fromBitmap(bitmap),
                IconStyle().apply {
                    //anchor = PointF(0.5f, 1.0f)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        private const val ZOOM_STEP = 1.0f
        private const val DEFAULT_ZOOM: Float = 12f
        private const val PLACEMARK_SCALE = 1.5f

        private val MOVE_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
        private val ZOOM_ANIMATION = Animation(Animation.Type.LINEAR, 0.2f)
    }


}