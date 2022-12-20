package space.iseki.envproxyselector.rule

data class IPPortRule(val ip: ByteArray, val port: Int) : NoProxyRule {
    override fun of(host: String, port: Int): Boolean {
        if (this.port != 0 && this.port != port) return false
        val ip = parseIP(host) ?: return false
        if (ip.size != this.ip.size) {
            return false
        }
        return ip.contentEquals(this.ip)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IPPortRule

        if (!ip.contentEquals(other.ip)) return false
        if (port != other.port) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ip.contentHashCode()
        result = 31 * result + port
        return result
    }

}
