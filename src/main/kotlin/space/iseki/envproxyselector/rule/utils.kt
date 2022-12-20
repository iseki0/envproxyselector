package space.iseki.envproxyselector.rule

import kotlin.experimental.and

internal fun ByteArray.mask(n: Int) {
    for (i in n / 8..lastIndex) {
        this[i] = 0
    }
    if (n / 8 !in indices) return
    this[n / 8] = this[n / 8] and ((1 shl n % 8).toByte())
}
