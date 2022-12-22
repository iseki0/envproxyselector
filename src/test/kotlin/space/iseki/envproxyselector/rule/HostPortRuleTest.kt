package space.iseki.envproxyselector.rule

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class HostPortRuleTest {

    @Test
    fun testHostPort() {
        val rule = HostPortRule("example.com", 3333)
        assertTrue(rule.of("example.com", 3333))
        assertTrue(rule.of("www.example.com", 3333))
        assertFalse(rule.of("example.com", 0))
        assertFalse(rule.of("example.org", 3333))
    }

    @Test
    fun testHostOnly(){
        val rule = HostPortRule("example.com", 0)
        assertTrue(rule.of("example.com", 3333))
        assertTrue(rule.of("example.com", 80))
        assertTrue(rule.of("www.example.com", 80))
        assertFalse(rule.of("www.example.org", 80))
    }
}