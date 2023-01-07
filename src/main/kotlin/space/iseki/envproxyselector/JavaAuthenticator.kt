package space.iseki.envproxyselector

import java.net.Authenticator
import java.net.InetAddress
import java.net.PasswordAuthentication
import java.net.URL

class JavaAuthenticator internal constructor(
    private val parentAuthenticator: Authenticator?,
    private val selector: Selector,
) : Authenticator() {

    private fun select(scheme: String?, host: String?, port: Int?): PasswordAuthentication? {
        if (scheme == null) return null
        if (host == null) return null
        val port = port ?: scheme.schemaDefaultPort ?: return null
        val info = selector.selectByScheme(scheme) ?: return null
        if (info.port != port || info.host != host) return null
        return info.auth?.toPasswordAuthentication()
    }

    override fun requestPasswordAuthenticationInstance(
        host: String?,
        addr: InetAddress?,
        port: Int,
        protocol: String?,
        prompt: String?,
        scheme: String?,
        url: URL?,
        reqType: RequestorType?,
    ): PasswordAuthentication? {
        select(scheme, host, port)?.let { return it }
        return parentAuthenticator?.requestPasswordAuthenticationInstance(
            host,
            addr,
            port,
            protocol,
            prompt,
            scheme,
            url,
            reqType
        )
    }
}