package com.example.apartapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Apart(
    val imageUrl: String,
    val cost: Int,
    val address: String,
    val rooms: Int? = null,
    val floor: Int? = null,
    val floorMax: Int? = null,
    val area: Int? = null,
    val metroStation: String? = null,
)

class ApartsViewModel: ViewModel() {
    private val _aparts = MutableStateFlow(mutableListOf<Apart>())

    val aparts: StateFlow<List<Apart>> = _aparts.asStateFlow()


    init { //
        addApart(Apart(
            imageUrl = "https://shorturl.at/pFKTY",
            cost = 200000,
            address = "Москва, САО, р-н Хорошевский, Хорошевское ш., 25Ак1",
            rooms = 2,
            floor = 10,
            floorMax = 15,
            area = 85,
            metroStation = "Полежаевская"
        ))
        addApart(Apart(
            imageUrl = "https://shorturl.at/pFKTY",
            cost = 200000,
            address = "Москва, САО, р-н Хорошевский, Хорошевское ш., 25Ак1",
            rooms = 2,
            floor = 10,
            floorMax = 15,
            area = 85,
            metroStation = "Полежаевская"
        ))
        addApart(Apart(
            imageUrl = "https://shorturl.at/pFKTY",
            cost = 200000,
            address = "Москва, САО, р-н Хорошевский, Хорошевское ш., 25Ак1",
            rooms = 2,
            floor = 10,
            floorMax = 15,
            area = 85,
            metroStation = "Полежаевская"
        ))
    }

    fun addApart(apart: Apart) {
        _aparts.update {
            it.add(apart)
            it
        }
    }
}