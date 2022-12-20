package space.iseki.envproxyselector.rule

data class IPCidrRule(val ip: ByteArray, val cidrNumber: Int) : NoProxyRule {
    init {
        check((cidrNumber in 0..32 && ip.size == 4) || (cidrNumber in 0..128 && ip.size == 16))
    }

    override fun of(host: String, port: Int): Boolean {
        val ip = parseIP(host) ?: return false
        if (ip.size != this.ip.size) {
            return false
        }
        ip.mask(cidrNumber)
        return ip.contentEquals(this.ip)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IPCidrRule

        if (!ip.contentEquals(other.ip)) return false
        if (cidrNumber != other.cidrNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ip.contentHashCode()
        result = 31 * result + cidrNumber.hashCode()
        return result
    }

}
