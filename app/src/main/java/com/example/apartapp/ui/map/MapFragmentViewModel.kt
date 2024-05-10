package com.example.apartapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.SuggestItem
import com.yandex.mapkit.search.SuggestOptions
import com.yandex.mapkit.search.SuggestResponse
import com.yandex.mapkit.search.SuggestSession
import com.yandex.mapkit.search.SuggestType
import com.yandex.runtime.Error
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class MapFragmentViewModel : ViewModel() {
    private val query = MutableStateFlow("")
    private val searchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    private val suggestSession: SuggestSession = searchManager.createSuggestSession()
    private val region = MutableStateFlow<VisibleRegion?>(null)
    private var searchSession: Session? = null

    private val searchState = MutableStateFlow<SearchState>(SearchState.Off)
    private val suggestState = MutableStateFlow<SuggestState>(SuggestState.Off)

    @OptIn(FlowPreview::class)
    private val throttledRegion = region.debounce(1.seconds)


    val uiState: StateFlow<MapUiState> = combine(
        query,
        searchState,
        suggestState,
    ) { query, searchState, suggestState ->
        MapUiState(
            query,
            searchState,
            suggestState,
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, MapUiState())

    private val searchSessionListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val obj = response.collection.children.firstOrNull()?.obj
            val items = response.collection.children.mapNotNull {
                val point = it.obj?.geometry?.firstOrNull()?.point ?: return@mapNotNull null
                SearchResponseItem(point, obj)
            }

            searchState.value = SearchState.Success(
                items
            )
        }

        override fun onSearchError(p0: com.yandex.runtime.Error) {
            searchState.value = SearchState.Error
        }
    }

    private val suggestSessionListener = object : SuggestSession.SuggestListener {
        override fun onResponse(response: SuggestResponse) {
            val suggestItems = response.items.take(SUGGEST_LIMIT)
                .map {
                    SuggestHolderItem(
                        title = it.title,
                        subtitle = it.subtitle,
                    ) {
                        setQueryText(it.displayText ?: "")
                        if (it.action == SuggestItem.Action.SEARCH) {
                            val uri = it.uri
                            if (uri != null) {
                                submitUriSearch(uri)
                            } else {
                                startSearch(it.searchText)
                            }
                        }
                    }
                }
            suggestState.value = SuggestState.Success(suggestItems)
        }

        override fun onError(p0: Error) {
            suggestState.value = SuggestState.Error
        }

    }

    fun subscribeForSuggest(): Flow<*> {
        return combine(
            query,
            searchState,
        ) { query, searchState ->
            if (query.isNotEmpty() && searchState == SearchState.Off && region.value != null) {
                submitSuggest(
                    query,
                    BoundingBox(region.value!!.bottomLeft, region.value!!.topRight)
                )
            }
        }
    }

    fun subscribeForSearch(): Flow<*> {
        return throttledRegion.filter { it != null }
            .filter { searchState.value is SearchState.Success }
            .mapNotNull { it }
            .onEach { region ->
                searchSession?.let {
                    it.setSearchArea(VisibleRegionUtils.toPolygon(region))
                    it.resubmit(searchSessionListener)
                    searchState.value = SearchState.Loading
                }
            }
    }

    fun setQueryText(value: String) {
        query.value = value
    }

    fun setVisibleRegion(region: VisibleRegion) {
        this.region.value = region
    }

    fun startSearch(searchText: String? = null) {
        val text = searchText ?: query.value
        if (query.value.isEmpty()) return

        val region = region.value?.let {
            VisibleRegionUtils.toPolygon(it)
        } ?: return

        submitSearch(text, region)
    }

    fun reset() {
        searchSession?.cancel()
        searchSession = null
        searchState.value = SearchState.Off
        suggestSession.reset()
        suggestState.value = SuggestState.Off
        query.value = ""
    }

    private fun submitSearch(query: String, geometry: Geometry) {
        searchSession?.cancel()
        searchSession = searchManager.submit(
            query,
            geometry,
            SearchOptions().apply {
                resultPageSize = 32
            },
            searchSessionListener
        )
        searchState.value = SearchState.Loading
    }

    private fun submitSuggest(
        query: String,
        box: BoundingBox,
        options: SuggestOptions = SuggestOptions().setSuggestTypes(
            SuggestType.GEO.value
                    or SuggestType.BIZ.value
                    or SuggestType.TRANSIT.value
        ),
    ) {
        suggestSession.suggest(query, box, options, suggestSessionListener)
        suggestState.value = SuggestState.Loading
    }

    private fun submitUriSearch(uri: String) {
        searchSession?.cancel()
        searchSession = searchManager.searchByURI(
            uri,
            SearchOptions(),
            searchSessionListener
        )
        searchState.value = SearchState.Loading
    }

    companion object {
        const val SUGGEST_LIMIT = 10
    }
}
