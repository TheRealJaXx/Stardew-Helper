package com.example

import com.example.data.model.WalnutDatabase
import com.example.data.model.WalnutRegion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testTotalWalnutsIsExactly130() {
        val total = WalnutDatabase.ALL_WALNUTS.sumOf { it.count }
        assertEquals("Total Golden Walnuts in Ginger Island must be exactly 130", 130, total)
        assertEquals(130, WalnutDatabase.TOTAL_WALNUTS_COUNT)
    }

    @Test
    fun testRegionBreakdown() {
        val east = WalnutDatabase.ALL_WALNUTS.filter { it.region == WalnutRegion.EAST }.sumOf { it.count }
        val north = WalnutDatabase.ALL_WALNUTS.filter { it.region == WalnutRegion.NORTH }.sumOf { it.count }
        val west = WalnutDatabase.ALL_WALNUTS.filter { it.region == WalnutRegion.WEST }.sumOf { it.count }
        val south = WalnutDatabase.ALL_WALNUTS.filter { it.region == WalnutRegion.SOUTH }.sumOf { it.count }
        val volcano = WalnutDatabase.ALL_WALNUTS.filter { it.region == WalnutRegion.VOLCANO }.sumOf { it.count }
        val general = WalnutDatabase.ALL_WALNUTS.filter { it.region == WalnutRegion.GENERAL }.sumOf { it.count }

        assertEquals(11, east)
        assertEquals(30, north)
        assertEquals(52, west)
        assertEquals(12, south)
        assertEquals(19, volcano)
        assertEquals(6, general)
        assertEquals(130, east + north + west + south + volcano + general)
    }

    @Test
    fun testUniqueIdsAndOrdering() {
        val ids = WalnutDatabase.ALL_WALNUTS.map { it.id }
        assertEquals("All walnut IDs must be unique", ids.distinct().size, ids.size)

        val orders = WalnutDatabase.ALL_WALNUTS.map { it.orderIndex }
        assertEquals("Order indices should be 1..N", (1..WalnutDatabase.ALL_WALNUTS.size).toList(), orders)
    }

    @Test
    fun testUnhintedWalnutsCount() {
        val unhinted = WalnutDatabase.ALL_WALNUTS.filter { it.isUnhinted }
        val unhintedSum = unhinted.sumOf { it.count }
        assertEquals("There are exactly 7 unhinted walnuts in Stardew Valley", 7, unhintedSum)
    }
}
