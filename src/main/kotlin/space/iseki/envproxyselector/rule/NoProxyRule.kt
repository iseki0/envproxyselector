package space.iseki.envproxyselector.rule

interface NoProxyRule {
    fun of(host: String, port: Int): Boolean
}
