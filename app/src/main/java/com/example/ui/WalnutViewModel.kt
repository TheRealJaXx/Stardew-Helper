package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.WalnutCategory
import com.example.data.model.WalnutDatabase
import com.example.data.model.WalnutItem
import com.example.data.model.WalnutRegion
import com.example.data.repository.WalnutRepository
import com.example.data.repository.WalnutUiItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FilterStatus {
    ALL,
    UNFOUND,
    FOUND
}

enum class SortOrder(val label: String) {
    RECOMMENDED("Recommended Walkthrough"),
    REGION("By Region"),
    VALUE("Highest Walnut Value"),
    NAME("Alphabetical")
}

data class WalnutFilterCriteria(
    val query: String,
    val region: WalnutRegion?,
    val category: WalnutCategory?,
    val status: FilterStatus,
    val sort: SortOrder
)

data class IslandMilestone(
    val requiredNuts: Int,
    val title: String,
    val reward: String,
    val icon: String
)

class WalnutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WalnutRepository = WalnutRepository(
        AppDatabase.getInstance(application).walnutDao()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRegion = MutableStateFlow<WalnutRegion?>(null)
    val selectedRegion: StateFlow<WalnutRegion?> = _selectedRegion.asStateFlow()

    private val _selectedCategory = MutableStateFlow<WalnutCategory?>(null)
    val selectedCategory: StateFlow<WalnutCategory?> = _selectedCategory.asStateFlow()

    private val _filterStatus = MutableStateFlow(FilterStatus.ALL)
    val filterStatus: StateFlow<FilterStatus> = _filterStatus.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.RECOMMENDED)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _expandedWalnutId = MutableStateFlow<String?>(null)
    val expandedWalnutId: StateFlow<String?> = _expandedWalnutId.asStateFlow()

    private val _showParrotSheet = MutableStateFlow(false)
    val showParrotSheet: StateFlow<Boolean> = _showParrotSheet.asStateFlow()

    private val _showRecountDialog = MutableStateFlow(false)
    val showRecountDialog: StateFlow<Boolean> = _showRecountDialog.asStateFlow()

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()

    val rawWalnuts: StateFlow<List<WalnutUiItem>> = repository.walnutsWithProgressFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WalnutDatabase.ALL_WALNUTS.map { WalnutUiItem(it, 0) }
        )

    val totalFoundCount: StateFlow<Int> = rawWalnuts.combine(_searchQuery) { list, _ ->
        list.sumOf { it.foundCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val regionProgress: StateFlow<Map<WalnutRegion, Pair<Int, Int>>> = rawWalnuts.combine(_searchQuery) { list, _ ->
        WalnutRegion.values().associateWith { region ->
            val regionItems = list.filter { it.item.region == region }
            val found = regionItems.sumOf { it.foundCount }
            val total = regionItems.sumOf { it.item.count }
            found to total
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val filterCriteria = combine(
        _searchQuery,
        _selectedRegion,
        _selectedCategory,
        _filterStatus,
        _sortOrder
    ) { query, region, category, status, sort ->
        WalnutFilterCriteria(query, region, category, status, sort)
    }

    val filteredWalnuts: StateFlow<List<WalnutUiItem>> = combine(
        rawWalnuts,
        filterCriteria
    ) { list, criteria ->
        var result = list

        // Filter by Region
        if (criteria.region != null) {
            result = result.filter { it.item.region == criteria.region }
        }

        // Filter by Category
        if (criteria.category != null) {
            result = result.filter { it.item.category == criteria.category }
        }

        // Filter by Status
        result = when (criteria.status) {
            FilterStatus.ALL -> result
            FilterStatus.UNFOUND -> result.filter { !it.isFullyFound }
            FilterStatus.FOUND -> result.filter { it.isFullyFound }
        }

        // Filter by Search Query
        if (criteria.query.isNotBlank()) {
            val q = criteria.query.trim().lowercase()
            result = result.filter { uiItem ->
                val it = uiItem.item
                it.name.lowercase().contains(q) ||
                        it.locationDescription.lowercase().contains(q) ||
                        it.instructions.lowercase().contains(q) ||
                        it.coordinates?.lowercase()?.contains(q) == true ||
                        it.parrotHint?.lowercase()?.contains(q) == true ||
                        it.requirements.any { req -> req.lowercase().contains(q) } ||
                        it.category.displayName.lowercase().contains(q)
            }
        }

        // Sort Order
        when (criteria.sort) {
            SortOrder.RECOMMENDED -> result.sortedBy { it.item.orderIndex }
            SortOrder.REGION -> result.sortedWith(compareBy({ it.item.region.ordinal }, { it.item.orderIndex }))
            SortOrder.VALUE -> result.sortedByDescending { it.item.count }
            SortOrder.NAME -> result.sortedBy { it.item.name }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val milestones: List<IslandMilestone> = listOf(
        IslandMilestone(1, "Leo's Hut Unlocked", "Feed first walnut to parrot to wake Leo", "🦜"),
        IslandMilestone(10, "Turtle Moved (North)", "Unlocks Dig Site and Volcano path", "🐢"),
        IslandMilestone(10, "Turtle Moved (West)", "Unlocks Island Farm and Western shores", "🌾"),
        IslandMilestone(20, "Island Farmhouse", "Sleep on island, kitchen, cellar access", "🏡"),
        IslandMilestone(20, "Island Resort Restored", "Opens Pirate Cove, unlocks star beach", "🍹"),
        IslandMilestone(100, "Mr. Qi's Walnut Room", "Special orders board, Qi Gems & shop", "💎"),
        IslandMilestone(130, "Island Perfection!", "All 130 Walnuts Found - Stardew Mastery", "🏆")
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onRegionSelected(region: WalnutRegion?) {
        _selectedRegion.value = region
    }

    fun onCategorySelected(category: WalnutCategory?) {
        _selectedCategory.value = category
    }

    fun onFilterStatusSelected(status: FilterStatus) {
        _filterStatus.value = status
    }

    fun onSortOrderSelected(order: SortOrder) {
        _sortOrder.value = order
    }

    fun toggleCardExpansion(walnutId: String) {
        _expandedWalnutId.value = if (_expandedWalnutId.value == walnutId) null else walnutId
    }

    fun toggleWalnutFound(uiItem: WalnutUiItem) {
        viewModelScope.launch {
            repository.toggleCompleted(uiItem.item.id, uiItem.foundCount)
        }
    }

    fun incrementWalnutCount(uiItem: WalnutUiItem) {
        viewModelScope.launch {
            repository.setProgress(uiItem.item.id, uiItem.foundCount + 1)
        }
    }

    fun decrementWalnutCount(uiItem: WalnutUiItem) {
        viewModelScope.launch {
            repository.setProgress(uiItem.item.id, uiItem.foundCount - 1)
        }
    }

    fun markRegionComplete(region: WalnutRegion, markFound: Boolean) {
        viewModelScope.launch {
            repository.markRegion(region, markFound)
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAll()
            _showResetDialog.value = false
        }
    }

    fun setParrotSheetVisible(visible: Boolean) {
        _showParrotSheet.value = visible
    }

    fun setRecountDialogVisible(visible: Boolean) {
        _showRecountDialog.value = visible
    }

    fun setResetDialogVisible(visible: Boolean) {
        _showResetDialog.value = visible
    }
}
