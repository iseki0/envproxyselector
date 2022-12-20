package space.iseki.envproxyselector

import space.iseki.envproxyselector.rule.parseNoProxyRule
import java.io.IOException
import java.net.*

class Selector : ProxySelector() {

    private val noProxyRules = (System.getenv("no_proxy") ?: System.getenv("NO_PROXY"))
        ?.splitToSequence(";")
        ?.filter { it.isNotBlank() }
        ?.mapNotNull { runCatching { parseNoProxyRule(it.trim()) }.getOrNull() }
        ?.toList()
        ?: emptyList()

    private val httpProxyEnv = System.getenv("http_proxy") ?: System.getenv("HTTP_PROXY")
    private val httpProxy = httpProxyEnv?.let(::parseProxy)
    private val httpProxyAuth = httpProxyEnv?.let(::parseAuthentication)

    private val httpsProxyEnv = System.getenv("https_proxy") ?: System.getenv("HTTPS_PROXY")
    private val httpsProxy = httpsProxyEnv?.let(::parseProxy)
    private val httpsProxyAuth = httpsProxyEnv?.let(::parseAuthentication)

    private val allProxyEnv = System.getenv("all_proxy") ?: System.getenv("ALL_PROXY")
    private val allProxy = allProxyEnv?.let(::parseProxy)
    private val allProxyAuth = allProxyEnv?.let(::parseAuthentication)

    override fun select(uri: URI): List<Proxy> {
        val candidate = when (uri.scheme) {
            "http" -> httpProxy ?: allProxy
            "https" -> httpsProxy ?: allProxy
            else -> httpProxy ?: allProxy
        } ?: return emptyList()
        val port = if (uri.port > -1) uri.port else when (uri.scheme) {
            "http" -> 80
            "https" -> 443
            else -> 80
        }
        if (noProxyRules.any { it.of(uri.host, port) }) return emptyList()
        return listOf(candidate)
    }

    fun authenticator(): Authenticator = object : Authenticator() {

        override fun getPasswordAuthentication(): PasswordAuthentication? = when (requestingScheme) {
            "http" -> httpProxyAuth ?: allProxyAuth
            "https" -> httpsProxyAuth ?: allProxyAuth
            else -> httpProxyAuth ?: allProxyAuth
        }

    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {}
}


internal fun parseAuthentication(s: String): PasswordAuthentication? {
    if (s.isBlank()) return null
    val uri = runCatching { URI.create(s) }.getOrNull() ?: return null
    val (username, password) = uri.userInfo?.split(':', limit = 2)?.plus("") ?: return null
    return PasswordAuthentication(username, password.toCharArray())
}

internal fun parseProxy(s: String): Proxy? {
    if (s.isBlank()) return null
    val uri = runCatching { URI.create(s) }.getOrNull() ?: return null
    val type = when (uri.scheme) {
        "http" -> Proxy.Type.HTTP
        "socks5", "socks5h" -> Proxy.Type.SOCKS
        else -> return null
    }
    val port = uri.port.takeIf { it > -1 } ?: when (uri.scheme) {
        "http" -> 80
        "socks5", "socks5h" -> 1080
        else -> return null
    }
    return Proxy(type, InetSocketAddress.createUnresolved(uri.host, port))
}