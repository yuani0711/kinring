package org.yuani.kinring.platform

import org.yuani.kinring.sharing.SecureRandomSource
import java.security.SecureRandom

/** Random bytes from the platform's `java.security.SecureRandom`. */
class JavaSecureRandom : SecureRandomSource {
    private val generator = SecureRandom()

    override fun nextBytes(size: Int): ByteArray = ByteArray(size).also(generator::nextBytes)
}
