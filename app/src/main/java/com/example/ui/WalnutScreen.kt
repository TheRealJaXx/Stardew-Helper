package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WalnutDatabase
import com.example.ui.components.FilterBar
import com.example.ui.components.LeoParrotSheet
import com.example.ui.components.ProgressBanner
import com.example.ui.components.RecountNutsDialog
import com.example.ui.components.WalnutCard
import com.example.ui.theme.IslandGreen
import com.example.ui.theme.VolcanoRed
import com.example.ui.theme.WalnutGold
import com.example.ui.theme.WalnutGoldDark
import com.example.ui.theme.WoodBrownDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalnutScreen(
    viewModel: WalnutViewModel,
    modifier: Modifier = Modifier
) {
    val walnuts by viewModel.filteredWalnuts.collectAsState()
    val allWalnuts by viewModel.rawWalnuts.collectAsState()
    val totalFound by viewModel.totalFoundCount.collectAsState()
    val regionProgress by viewModel.regionProgress.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val expandedId by viewModel.expandedWalnutId.collectAsState()

    val showParrotSheet by viewModel.showParrotSheet.collectAsState()
    val showRecountDialog by viewModel.showRecountDialog.collectAsState()
    val showResetDialog by viewModel.showResetDialog.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val unfoundCount = allWalnuts.count { !it.isFullyFound }
    val foundCount = allWalnuts.count { it.isFullyFound }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🌰",
                            fontSize = 22.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Stardew Helper",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ginger Island Golden Walnuts Guide",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .testTag("walnut_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Progress Banner Header
                item(key = "progress_banner") {
                    ProgressBanner(
                        totalFound = totalFound,
                        milestones = viewModel.milestones,
                        onOpenParrotSheet = { viewModel.setParrotSheetVisible(true) },
                        onOpenRecountDialog = { viewModel.setRecountDialogVisible(true) },
                        onOpenResetDialog = { viewModel.setResetDialogVisible(true) }
                    )
                }

                // Region Quick-Complete / Action Row (if a specific region is filtered)
                if (selectedRegion != null) {
                    item(key = "region_quick_actions") {
                        val reg = selectedRegion!!
                        val progress = regionProgress[reg] ?: (0 to 0)
                        val isAllFound = progress.first >= progress.second && progress.second > 0

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = reg.iconLabel, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${reg.displayName} (${progress.first}/${progress.second})",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.markRegionComplete(reg, !isAllFound) },
                                    modifier = Modifier.testTag("toggle_region_button")
                                ) {
                                    Text(
                                        text = if (isAllFound) "Unmark All" else "Mark All Done",
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isAllFound) MaterialTheme.colorScheme.onSurfaceVariant else IslandGreen
                                    )
                                }
                            }
                        }
                    }
                }

                // Filters and Search Bar
                item(key = "filter_bar") {
                    FilterBar(
                        searchQuery = searchQuery,
                        onSearchQueryChanged = viewModel::onSearchQueryChanged,
                        selectedRegion = selectedRegion,
                        onRegionSelected = viewModel::onRegionSelected,
                        selectedCategory = selectedCategory,
                        onCategorySelected = viewModel::onCategorySelected,
                        filterStatus = filterStatus,
                        onFilterStatusSelected = viewModel::onFilterStatusSelected,
                        sortOrder = sortOrder,
                        onSortOrderSelected = viewModel::onSortOrderSelected,
                        regionProgress = regionProgress,
                        totalWalnutCount = WalnutDatabase.TOTAL_WALNUTS_COUNT,
                        unfoundCount = unfoundCount,
                        foundCount = foundCount
                    )
                }

                // Empty state if no results match
                if (walnuts.isEmpty()) {
                    item(key = "empty_state") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "No walnuts match your filter",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Try clearing search or changing the region filter.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedButton(
                                    onClick = {
                                        viewModel.onSearchQueryChanged("")
                                        viewModel.onRegionSelected(null)
                                        viewModel.onCategorySelected(null)
                                        viewModel.onFilterStatusSelected(FilterStatus.ALL)
                                    },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    }
                }

                // Walnut Cards
                items(
                    items = walnuts,
                    key = { it.item.id }
                ) { uiItem ->
                    WalnutCard(
                        uiItem = uiItem,
                        isExpanded = expandedId == uiItem.item.id,
                        onCardClick = { viewModel.toggleCardExpansion(uiItem.item.id) },
                        onToggleCompleted = { viewModel.toggleWalnutFound(uiItem) },
                        onIncrement = { viewModel.incrementWalnutCount(uiItem) },
                        onDecrement = { viewModel.decrementWalnutCount(uiItem) }
                    )
                }

                item(key = "footer_spacing") {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Leo's Parrot Bottom Sheet
    if (showParrotSheet) {
        LeoParrotSheet(
            sheetState = sheetState,
            allWalnuts = allWalnuts,
            onDismiss = { viewModel.setParrotSheetVisible(false) }
        )
    }

    // Recount Nuts Dialog
    if (showRecountDialog) {
        RecountNutsDialog(
            onDismiss = { viewModel.setRecountDialogVisible(false) }
        )
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setResetDialogVisible(false) },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = VolcanoRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset All Progress?")
                }
            },
            text = {
                Text("This will uncheck all 130 Golden Walnuts in your checklist. Are you sure you want to start fresh?")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetAllProgress() },
                    colors = ButtonDefaults.buttonColors(containerColor = VolcanoRed),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Reset All")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.setResetDialogVisible(false) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
