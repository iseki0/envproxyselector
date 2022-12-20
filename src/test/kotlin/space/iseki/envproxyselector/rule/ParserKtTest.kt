package space.iseki.envproxyselector.rule

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertContentEquals

private val ipv4tc = listOf(
    byteArrayOf(1, 2, 3, 4) to "1.2.3.4",
    null to "1.2.3.4.5",
    null to "1.2.3.",
    null to "1.2..3..",
    null to "1.2.1.2222",
)


private val ipv6tc = listOf(
    byteArrayOf(0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8) to "1:2:3:4:5:6:7:8",
    byteArrayOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2) to "1::2",
    byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1) to "::1",
    byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0xff.toByte(), 1) to "::ff01",
    null to "::1::::",
    null to "::12345",
)

private val cidrTc = listOf(
    byteArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) to 8 to "01ff:2:3::/8",
    byteArrayOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) to 16 to "1:2:3::/16",
    byteArrayOf(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) to 16 to "1::/16",
    byteArrayOf(0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8) to 128 to "1:2:3:4:5:6:7:8/128",
    byteArrayOf(1, 2, 3, 4) to 32 to "1.2.3.4/32",
    byteArrayOf(1, 0, 0, 0) to 8 to "1.2.3.4/8",
    byteArrayOf(1, 0, 0, 0) to 8 to "1.0.0.0/8",
    null to "1:2:3:4:5:6:7:8/1280",
    null to "1.1.1.1/33",
)

private val ipPortTc = listOf(
    byteArrayOf(1, -1, 0, 2, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) to 8 to "[01ff:2:3::]:8",
    null to "[01ff:2:3::]:888888",
    null to "[01ff:2:3::]:-1",
    byteArrayOf(1, 2, 3, 4) to 32 to "1.2.3.4:32",
    null to "1.2.3.4:3266666",
    null to "1.2.3.4:32a",
)

class ParserKtTest {


    @Test
    fun parseIPv6() {
        ipv6tc.forEachIndexed { index, (a, b) -> assertContentEquals(a,
            parseIPv6(b), "$index. $b") }
    }

    @Test
    fun parseIP() {
        (ipv4tc + ipv6tc).forEachIndexed { index, (a, b) -> assertContentEquals(a,
            parseIP(b), "$index. $b") }
    }

    @Test
    fun parseIPv4() {
        ipv4tc.forEachIndexed { index, (a, b) -> assertContentEquals(a,
            parseIPv4(b), "$index. $b") }
    }

    @Test
    fun parseIPCidr() {
        cidrTc.forEachIndexed { index, (a, b) ->
            val r = parseIPCidr(b)
            if (a == null) {
                assertNull(r, "index: $index")
            } else {
                assertEquals(a.second, r?.second)
                assertContentEquals(a.first, r?.first)
            }
        }
    }

    @Test
    fun parseIPPort() {
        ipPortTc.forEachIndexed { index, (a, b) ->
            val r = parseIPPort(b)
            if (a == null) {
                assertNull(r, "index: $index")
            } else {
                assertEquals(a.second, r?.second)
                assertContentEquals(a.first, r?.first)
            }
        }
    }
}