package space.iseki.envproxyselector.rule

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class IPCidrRuleTest{

    @Test
    fun testIPv4(){
        val rule = IPCidrRule(parseIP("10.0.0.0")!!, 8)
        assertTrue(rule.of("10.1.0.0", 0))
        assertTrue(rule.of("10.1.1.1", 0))
        assertFalse(rule.of("11.1.1.1", 0))
    }

    @Test
    fun testIPv6(){
        val rule = IPCidrRule(parseIP("ffff::1")!!, 16)
        assertTrue(rule.of("ffff::2", 1))
        assertTrue(rule.of("ffff:2::2", 1))
        assertFalse(rule.of("fffa:2::2", 1))
    }
}
