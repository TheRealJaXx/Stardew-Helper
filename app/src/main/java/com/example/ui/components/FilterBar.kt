package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.WalnutCategory
import com.example.data.model.WalnutRegion
import com.example.ui.FilterStatus
import com.example.ui.SortOrder
import com.example.ui.theme.IslandGreen
import com.example.ui.theme.WalnutGoldDark

@Composable
fun FilterBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedRegion: WalnutRegion?,
    onRegionSelected: (WalnutRegion?) -> Unit,
    selectedCategory: WalnutCategory?,
    onCategorySelected: (WalnutCategory?) -> Unit,
    filterStatus: FilterStatus,
    onFilterStatusSelected: (FilterStatus) -> Unit,
    sortOrder: SortOrder,
    onSortOrderSelected: (SortOrder) -> Unit,
    regionProgress: Map<WalnutRegion, Pair<Int, Int>>,
    totalWalnutCount: Int,
    unfoundCount: Int,
    foundCount: Int,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search Input & Sort Menu Trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_walnuts_input"),
                placeholder = { Text("Search location, coords, puzzle...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChanged("") },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WalnutGoldDark,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Sort Menu Button
            FilterChip(
                selected = sortOrder != SortOrder.RECOMMENDED,
                onClick = { sortMenuExpanded = true },
                label = {
                    Text(
                        text = when (sortOrder) {
                            SortOrder.RECOMMENDED -> "Order"
                            SortOrder.REGION -> "Region"
                            SortOrder.VALUE -> "Value"
                            SortOrder.NAME -> "A-Z"
                        },
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Sort order",
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.testTag("sort_menu_chip")
            )

            DropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
            ) {
                SortOrder.values().forEach { order ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = order.label,
                                fontWeight = if (order == sortOrder) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onSortOrderSelected(order)
                            sortMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Status Filter Row (All, Unfound, Found) + Category dropdown
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = filterStatus == FilterStatus.ALL,
                onClick = { onFilterStatusSelected(FilterStatus.ALL) },
                label = { Text("All ($totalWalnutCount)") },
                modifier = Modifier.testTag("filter_status_all")
            )

            FilterChip(
                selected = filterStatus == FilterStatus.UNFOUND,
                onClick = { onFilterStatusSelected(FilterStatus.UNFOUND) },
                label = { Text("Unfound ($unfoundCount)") },
                modifier = Modifier.testTag("filter_status_unfound")
            )

            FilterChip(
                selected = filterStatus == FilterStatus.FOUND,
                onClick = { onFilterStatusSelected(FilterStatus.FOUND) },
                label = { Text("Found ($foundCount)") },
                modifier = Modifier.testTag("filter_status_found")
            )

            // Category Filter Chip
            FilterChip(
                selected = selectedCategory != null,
                onClick = { categoryMenuExpanded = true },
                label = {
                    Text(
                        text = selectedCategory?.displayName ?: "Category: All"
                    )
                },
                modifier = Modifier.testTag("category_filter_chip")
            )

            DropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { categoryMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Categories") },
                    onClick = {
                        onCategorySelected(null)
                        categoryMenuExpanded = false
                    }
                )
                WalnutCategory.values().forEach { cat ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = cat.displayName,
                                fontWeight = if (cat == selectedCategory) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onCategorySelected(cat)
                            categoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Region Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = selectedRegion == null,
                onClick = { onRegionSelected(null) },
                label = { Text("All Regions") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("region_chip_all")
            )

            WalnutRegion.values().forEach { region ->
                val progress = regionProgress[region] ?: (0 to 0)
                val isCompleted = progress.first >= progress.second && progress.second > 0

                FilterChip(
                    selected = selectedRegion == region,
                    onClick = {
                        if (selectedRegion == region) onRegionSelected(null) else onRegionSelected(region)
                    },
                    label = {
                        Text(
                            text = "${region.iconLabel} ${region.displayName} (${progress.first}/${progress.second})"
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (isCompleted) IslandGreen.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("region_chip_${region.name.lowercase()}")
                )
            }
        }
    }
}
