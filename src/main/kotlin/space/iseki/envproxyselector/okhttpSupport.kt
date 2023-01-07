package space.iseki.envproxyselector

import okhttp3.*

class OkHttpProxyAuthenticator(private val selector: Selector) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 307) return null
        if (response.request.header("Proxy-Authorization") != null) return null
        val auth = selector.selectByScheme(response.request.url.scheme)?.auth ?: return null
        return response.request.newBuilder().header("Proxy-Authorization", auth.okhttpCredential()).build()
    }
}

fun AuthInfo.okhttpCredential() = Credentials.basic(username, password)
