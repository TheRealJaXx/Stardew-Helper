package com.example.data.repository

import com.example.data.local.WalnutDao
import com.example.data.local.WalnutProgressEntity
import com.example.data.model.WalnutDatabase
import com.example.data.model.WalnutItem
import com.example.data.model.WalnutRegion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class WalnutUiItem(
    val item: WalnutItem,
    val foundCount: Int
) {
    val isFullyFound: Boolean get() = foundCount >= item.count
    val isPartiallyFound: Boolean get() = foundCount in 1 until item.count
}

class WalnutRepository(private val dao: WalnutDao) {

    val walnutsWithProgressFlow: Flow<List<WalnutUiItem>> = dao.getAllProgressFlow().map { progressList ->
        val progressMap = progressList.associateBy({ it.walnutId }, { it.foundCount })
        WalnutDatabase.ALL_WALNUTS.map { walnut ->
            val found = (progressMap[walnut.id] ?: 0).coerceIn(0, walnut.count)
            WalnutUiItem(item = walnut, foundCount = found)
        }
    }

    suspend fun setProgress(walnutId: String, newCount: Int) {
        val item = WalnutDatabase.ALL_WALNUTS.find { it.id == walnutId } ?: return
        val clamped = newCount.coerceIn(0, item.count)
        if (clamped == 0) {
            dao.deleteProgress(walnutId)
        } else {
            dao.insertOrUpdate(WalnutProgressEntity(walnutId = walnutId, foundCount = clamped))
        }
    }

    suspend fun toggleCompleted(walnutId: String, currentFound: Int) {
        val item = WalnutDatabase.ALL_WALNUTS.find { it.id == walnutId } ?: return
        if (currentFound >= item.count) {
            dao.deleteProgress(walnutId)
        } else {
            dao.insertOrUpdate(WalnutProgressEntity(walnutId = walnutId, foundCount = item.count))
        }
    }

    suspend fun markRegion(region: WalnutRegion, markFound: Boolean) {
        val regionItems = WalnutDatabase.ALL_WALNUTS.filter { it.region == region }
        if (markFound) {
            val entities = regionItems.map {
                WalnutProgressEntity(walnutId = it.id, foundCount = it.count)
            }
            dao.insertAll(entities)
        } else {
            regionItems.forEach { dao.deleteProgress(it.id) }
        }
    }

    suspend fun resetAll() {
        dao.clearAll()
    }
}
