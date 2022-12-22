package space.iseki.envproxyselector.rule

data class HostPortRule(val host: String, val port: Int) : NoProxyRule {
    init {
        check(port in 0..65535)
        check(host.isNotEmpty())
    }

    override fun of(host: String, port: Int): Boolean =
        host.endsWith(this.host.removePrefix("*")) && (this.port == 0 || this.port == port)
}
