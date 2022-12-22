package space.iseki.envproxyselector.rule

class HostPortRule(private val host: String, private val port: Int) : NoProxyRule {
    init {
        check(port in 0..65535)
        check(host.isNotEmpty())
    }

    override fun of(host: String, port: Int): Boolean =
        host.endsWith(this.host.removePrefix("*")) && (this.port == 0 || this.port == port)
}
