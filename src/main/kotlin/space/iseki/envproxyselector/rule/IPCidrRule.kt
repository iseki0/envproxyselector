package space.iseki.envproxyselector.rule

class IPCidrRule(private val ip: ByteArray, private val cidrNumber: Int) : NoProxyRule {
    init {
        check((cidrNumber in 0..32 && ip.size == 4) || (cidrNumber in 0..128 && ip.size == 16))
        ip.mask(cidrNumber)
    }

    override fun of(host: String, port: Int): Boolean {
        val ip = parseIP(host) ?: return false
        if (ip.size != this.ip.size) {
            return false
        }
        ip.mask(cidrNumber)
        return ip.contentEquals(this.ip)
    }

}
