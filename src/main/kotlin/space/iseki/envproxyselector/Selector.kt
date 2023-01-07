package space.iseki.envproxyselector

import space.iseki.envproxyselector.rule.parseNoProxyRule
import java.io.IOException
import java.net.*

class Selector : ProxySelector() {

    private val noProxyRules = env("no_proxy", "NO_PROXY")
        ?.splitToSequence(";")
        ?.filter { it.isNotBlank() }
        ?.mapNotNull { runCatching { parseNoProxyRule(it.trim()) }.getOrNull() }
        ?.toList()
        ?: emptyList()

    private val httpProxyInfo = env("http_proxy", "HTTP_PROXY")?.parseProxyInfoOrNull()
    private val httpsProxyInfo = env("https_proxy", "HTTPS_PROXY")?.parseProxyInfoOrNull()
    private val allProxyInfo = env("all_proxy", "ALL_PROXY")?.parseProxyInfoOrNull()

    internal fun selectByScheme(scheme: String) = when (scheme) {
        "http" -> httpProxyInfo
        "https" -> httpsProxyInfo
        else -> httpProxyInfo
    } ?: allProxyInfo

    internal fun selectProxyByUri(uri: URI): ProxyInfo? =
        selectByScheme(uri.scheme)?.takeIf { noProxyRules.none { it.of(uri.host, uri.resolvePort) } }

    override fun select(uri: URI): List<Proxy> =
        selectProxyByUri(uri)?.toJavaProxy()?.let { listOf(it) } ?: emptyList()

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {}

    fun authenticator(parent: Authenticator? = null): Authenticator = JavaAuthenticator(parent, this)
}

private val URI.resolvePort: Int
    get() = port.takeIf { it != -1 } ?: when (scheme) {
        "http" -> 80
        "https" -> 443
        else -> 80
    }

private fun env(vararg names: String): String? = names.firstNotNullOfOrNull { System.getenv(it) }
