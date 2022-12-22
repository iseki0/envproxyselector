package space.iseki.envproxyselector.rule

class IPPortRule(private val ip: ByteArray, private val port: Int) : NoProxyRule {
    override fun of(host: String, port: Int): Boolean {
        if (this.port != 0 && this.port != port) return false
        val ip = parseIP(host) ?: return false
        if (ip.size != this.ip.size) {
            return false
        }
        return ip.contentEquals(this.ip)
    }

}
