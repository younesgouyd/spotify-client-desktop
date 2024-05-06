package dev.younesgouyd.apps.spotifyclient.desktop.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LazilyLoadedItems<ItemType, OffsetType : Offset>(
    private val coroutineScope: CoroutineScope,
    private val load: suspend (OffsetType) -> Page<ItemType, OffsetType>,
    initialOffset: OffsetType
) {
    private val _items: MutableStateFlow<List<ItemType>> = MutableStateFlow(emptyList())
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var nextOffset: OffsetType? = initialOffset

    val items: StateFlow<List<ItemType>> get() = _items.asStateFlow()
    val loading: StateFlow<Boolean> get() = _loading.asStateFlow()

    fun loadMore() {
        if (!_loading.value) {
            nextOffset?.let { nextOffsetNotNull ->
                coroutineScope.launch {
                    _loading.value = true
                    val result = load(nextOffsetNotNull)
                    nextOffset = result.nextOffset
                    _items.update { it + result.items }
                    _loading.value = false
                }
            }
        }
    }

    data class Page<ItemType, OffsetType : Offset>(
        val nextOffset: OffsetType?,
        val items: List<ItemType>
    )
}