package space.iseki.envproxyselector.rule

import java.net.URI

internal fun parseNoProxyRule(rule: String): NoProxyRule? {
    parseIPCidr(rule)?.let { (ip, n) -> return IPCidrRule(ip, n) }
    parseIPPort(rule)?.let { (ip, port) -> return IPPortRule(ip, port) }
    parseIP(rule)?.let { return IPPortRule(it, 0) }
    parseHostPort(rule)?.let { return HostPortRule(it.first, it.second) }
    return null
}

private fun portOfUri(uri: URI) = if (uri.port != -1) uri.port else when (uri.scheme) {
    "http" -> 80
    "https" -> 443
    else -> 0
}

internal fun parseIP(s: String) = parseIPv6(s) ?: parseIPv4(s)

internal fun parseIPv4(s: String): ByteArray? {
    val chunks = s.split('.', limit = 4).takeIf { it.size == 4 } ?: return null
    return ByteArray(4) { i -> chunks[i].toIntOrNull()?.takeIf { it in 0..255 }?.toByte() ?: return null }
}

internal fun parseIPv6(s: String): ByteArray? {
    val chunks = s.split(':', limit = 10).takeIf { it.size > 2 }?.let {
        // remove surrounding empty chunk, for "::1"
        when {
            it[0] == "" && it[it.lastIndex] == "" -> it.subList(1, it.size - 1)
            it[0] == "" -> it.subList(1, it.size)
            it[it.lastIndex] == "" -> it.subList(0, it.size - 1)
            else -> it
        }
    }?.takeIf { it.size <= 8 } ?: return null
    val r = ByteArray(16)
    var p = 0
    var abbr = false
    for (chunk in chunks) {
        if (chunk == "") {
            if (abbr) return null
            abbr = true
            p += 8 - chunks.size + 1
            continue
        }
        val n = chunk.toIntOrNull(16)?.takeIf { it in 0..0xffff } ?: return null
        r[p * 2] = (n shr 8).toByte()
        r[p * 2 + 1] = n.toByte()
        p++
    }
    return r.takeIf { p == 8 }
}

internal fun parseIPCidr(s: String): Pair<ByteArray, Int>? {
    val idx = s.lastIndexOf('/').takeIf { it > -1 && it < s.lastIndex } ?: return null
    val ip = parseIP(s.substring(0 until idx)) ?: return null
    val num = s.substring(idx + 1).toIntOrNull()?.takeIf {
        // check cidr number in valid range
        when (ip.size) {
            4 -> it in 0..32
            16 -> it in 0..128
            else -> error("bad ip")
        }
    } ?: return null
    ip.mask(num)
    return ip to num
}

internal fun parseIPPort(s: String): Pair<ByteArray, Int>? {
    if (s.isEmpty()) return null
    val i = s.lastIndexOf(':').takeIf { it > -1 && it < s.lastIndex } ?: return null
    val port = s.substring(i + 1).toIntOrNull()?.takeIf { it in 0..0xffff } ?: return null
    val h = s.substring(0 until i)
    if (h.isEmpty()) return null
    if (h[0] == '[') {
        // ipv6
        val ip = parseIPv6(h.substring(1 until h.lastIndex)) ?: return null
        return ip to port
    } else {
        // ipv4
        val ip = parseIPv4(h) ?: return null
        return ip to port
    }
}

internal fun parseHostPort(s: String): Pair<String, Int>? {
    val i = s.lastIndexOf(':')
    if (i > -1 && i != s.lastIndex) {
        val port = s.substring(i + 1).toIntOrNull()?.takeIf { i in 1..0xffff } ?: return null
        return s.substring(0 until i) to port
    }
    return s to 0
}
