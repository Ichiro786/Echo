package dev.brahmkshatriya.echo.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.brahmkshatriya.echo.data.extensions.OfflineExtension
import dev.brahmkshatriya.echo.data.models.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val offlineExtension: OfflineExtension
) : ViewModel() {

    private val _searchResults: MutableStateFlow<Map<String, Flow<PagingData<MediaItem>>>> =
        MutableStateFlow(emptyMap())

    val searchResults = _searchResults.asStateFlow()
    suspend fun search(query: String) {
        _searchResults.value = offlineExtension.search(query)
    }
}