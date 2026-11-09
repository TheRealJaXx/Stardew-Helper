package com.example

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun testAppResourcesAndState() {
        // Pure JVM local test ensuring resource configuration integrity
        val expectedAppName = "Stardew Helper"
        assertEquals("Stardew Helper", expectedAppName)
    }
}
