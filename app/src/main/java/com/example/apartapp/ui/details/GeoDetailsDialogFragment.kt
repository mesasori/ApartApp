package com.example.apartapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.apartapp.R
import com.example.apartapp.data.GeoObjectHolder
import com.example.apartapp.databinding.PlaceInfoDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yandex.mapkit.geometry.Point

class GeoDetailsDialogFragment() : BottomSheetDialogFragment() {
    private lateinit var binding: PlaceInfoDialogBinding

    private val viewModel: GeoDetailsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PlaceInfoDialogBinding.bind(
            inflater.inflate(
                R.layout.place_info_dialog,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()

        binding.apply {
            buttonAddPlace.setOnClickListener {
                GeoObjectHolder.selectedGeo = GeoObjectHolder.tappedGeo

                dialog?.dismiss()
            }
        }
    }

    private fun setUpViews() {
        viewModel.uiState().let { data ->
            binding.apply {
                tvTitle.text = data.title
                tvDescription.text = data.descriptionText
                tvCoordinates.text = getCoordinatesByPoint(data.location)
            }
        }

    }

    private fun getCoordinatesByPoint(point: Point) =
        "Coordinates: ${point.latitude} ${point.longitude}"

    companion object {
        const val TAG = "1"
    }
}
