package com.example.apartapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


enum class NavbarItemEnum(val itemName: String) {
    APARTS("Aparts"),
    PLACES("Places"),
}

data class Place(
    val name: String,
    val address: String,
    val frequency: Int,
)

class MainViewModel: ViewModel() {
    /** Only for Navbar components, currently displayed Screen defined in currentScreen */
    private val _navItems = MutableStateFlow(NavbarItemEnum.PLACES)
    private val _placeItems = MutableStateFlow(mutableListOf<Place>())

    val navItems: StateFlow<NavbarItemEnum> = _navItems.asStateFlow()
    val placeItems: StateFlow<List<Place>> = _placeItems.asStateFlow()

    fun addPlace(place: Place) {
        _placeItems.update {
            it.add(place)
            it
        }
    }

    init {
        addPlace(Place("Title", "Address", 8))
        addPlace(Place("Title1", "Address", 8))
        addPlace(Place("Title2", "Address", 8))
    }

    fun removePlace(index: Int) {
        _placeItems.update {
            it.removeAt(index)
            it
        }
    }

    fun setSelectedNavItem(navItem: NavbarItemEnum) {
        _navItems.update { navItem }
    }
}