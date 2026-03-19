package com.example.rokidsettingshub.debug

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PairDeviceCommandTest {

    @Test
    fun blankAddressDoesNotDispatchPairing() {
        val pairedAddresses = mutableListOf<String>()
        val command = PairDeviceCommand { address ->
            pairedAddresses += address
            true
        }

        val paired = command.execute("   ")

        assertFalse(paired)
        assertTrue(pairedAddresses.isEmpty())
    }

    @Test
    fun trimmedAddressDispatchesPairing() {
        val pairedAddresses = mutableListOf<String>()
        val command = PairDeviceCommand { address ->
            pairedAddresses += address
            true
        }

        val paired = command.execute(" AC:15:C0:04:8D:A7 ")

        assertTrue(paired)
        assertEquals(listOf("AC:15:C0:04:8D:A7"), pairedAddresses)
    }

    @Test
    fun blankQueryDoesNotDispatchScanAndPair() {
        val queries = mutableListOf<String>()
        val command = ScanAndPairDeviceCommand { query ->
            queries += query
        }

        val started = command.execute("   ")

        assertFalse(started)
        assertTrue(queries.isEmpty())
    }

    @Test
    fun trimmedQueryDispatchesScanAndPair() {
        val queries = mutableListOf<String>()
        val command = ScanAndPairDeviceCommand { query ->
            queries += query
        }

        val started = command.execute("  j09  ")

        assertTrue(started)
        assertEquals(listOf("j09"), queries)
    }

    @Test
    fun matcherMatchesByNameIgnoringCase() {
        val matcher = BluetoothDebugMatcher("j09")

        assertTrue(matcher.matches(name = "J09 Remote", address = "AA:BB:CC:DD:EE:FF"))
    }

    @Test
    fun matcherMatchesByAddressIgnoringCase() {
        val matcher = BluetoothDebugMatcher("ac:15:c0")

        assertTrue(matcher.matches(name = "Unknown", address = "AC:15:C0:04:8D:A7"))
    }

    @Test
    fun matcherRejectsNonMatchingDevice() {
        val matcher = BluetoothDebugMatcher("j09")

        assertFalse(matcher.matches(name = "Keyboard", address = "AA:BB:CC:DD:EE:FF"))
    }
}
