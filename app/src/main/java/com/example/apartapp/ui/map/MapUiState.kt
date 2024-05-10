package com.example.apartapp.ui.map

import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.geometry.Point

data class MapUiState(
    val query: String = "",
    val searchState: SearchState = SearchState.Off,
    val suggestState: SuggestState = SuggestState.Off
)

sealed interface SearchState {
    data object Off : SearchState
    data object Loading : SearchState
    data object Error : SearchState
    data class Success(
        val items: List<SearchResponseItem>
    ) : SearchState
}

sealed interface SuggestState {
    data object Off : SuggestState
    data object Loading : SuggestState
    data object Error : SuggestState
    data class Success(
        val items: List<SuggestHolderItem>
    ) : SuggestState
}

data class SearchResponseItem(
    val point: Point,
    val geoObject: GeoObject?
)
