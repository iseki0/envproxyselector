package space.iseki.envproxyselector

import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.net.URI

data class ProxyInfo(
    val schema: String,
    val port: Int,
    val host: String,
    val auth: AuthInfo?,
)

internal val ProxyInfo.proxyType: Proxy.Type?
    get() = when (schema) {
        "http" -> Proxy.Type.HTTP
        "socks5", "socks5h" -> Proxy.Type.SOCKS
        else -> null
    }

internal fun ProxyInfo.toJavaProxy(): Proxy? {
    return Proxy(
        proxyType ?: return null,
        InetSocketAddress.createUnresolved(host, port),
    )
}

class AuthInfo(
    val username: String,
    val password: String,
)

internal fun AuthInfo.toPasswordAuthentication() = PasswordAuthentication(username, password.toCharArray())

internal fun String.parseProxyInfoOrNull(): ProxyInfo? = runCatching { URI.create(this) }.getOrNull()?.let { uri ->
    ProxyInfo(
        schema = uri.scheme,
        port = uri.port.takeIf { it != -1 } ?: uri.scheme.schemaDefaultPort ?: return null,
        host = uri.host,
        auth = uri.authInfoOrNull(),
    )
}

internal val String.schemaDefaultPort: Int?
    get() = when (this) {
        "http" -> 80
        "https" -> 443
        "socks5", "socks5h" -> 1080
        else -> null
    }

private fun URI.authInfoOrNull() = userInfo
    ?.takeIf { it.isNotBlank() }
    ?.split(':', limit = 2)
    ?.let { AuthInfo(it[0], it.getOrNull(1).orEmpty()) }
