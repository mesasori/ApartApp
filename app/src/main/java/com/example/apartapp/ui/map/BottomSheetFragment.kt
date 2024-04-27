package com.example.apartapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apartapp.R
import com.example.apartapp.data.BottomSheetData
import com.example.apartapp.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment(bottomSheetData: BottomSheetData) : BottomSheetDialogFragment() {
    private val _bottomSheetData = bottomSheetData
    private lateinit var binding: FragmentBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.bind(
            inflater.inflate(
                R.layout.fragment_bottom_sheet,
                container,
                false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvTitle.text = _bottomSheetData.title
            tvDescription.text = _bottomSheetData.description
            tvCoordinates.text = _bottomSheetData.coordinates
        }
    }

    companion object {
        const val TAG = "BottomSheetDialog"
    }
}
