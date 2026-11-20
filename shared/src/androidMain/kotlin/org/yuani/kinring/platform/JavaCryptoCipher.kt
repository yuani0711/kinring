package org.yuani.kinring.platform

import org.yuani.kinring.crypto.AuthenticatedCipher
import org.yuani.kinring.crypto.AuthenticatedCipher.Companion.KEY_SIZE
import org.yuani.kinring.crypto.AuthenticatedCipher.Companion.NONCE_SIZE
import org.yuani.kinring.crypto.AuthenticatedCipher.Companion.TAG_SIZE
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class JavaCryptoCipher : AuthenticatedCipher {
    override suspend fun seal(
        key: ByteArray,
        nonce: ByteArray,
        plaintext: ByteArray,
        associatedData: ByteArray,
    ): ByteArray = cipher(Cipher.ENCRYPT_MODE, key, nonce, associatedData).doFinal(plaintext)

    override suspend fun open(
        key: ByteArray,
        nonce: ByteArray,
        sealed: ByteArray,
        associatedData: ByteArray,
    ): ByteArray? {
        if (sealed.size < TAG_SIZE) return null
        return try {
            cipher(Cipher.DECRYPT_MODE, key, nonce, associatedData).doFinal(sealed)
        } catch (wrongKeyOrAltered: AEADBadTagException) {
            null
        }
    }

    private fun cipher(mode: Int, key: ByteArray, nonce: ByteArray, associatedData: ByteArray): Cipher {
        require(key.size == KEY_SIZE) { "Key must be $KEY_SIZE bytes, was ${key.size}." }
        require(nonce.size == NONCE_SIZE) { "Nonce must be $NONCE_SIZE bytes, was ${nonce.size}." }
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(mode, SecretKeySpec(key, ALGORITHM), GCMParameterSpec(TAG_SIZE * Byte.SIZE_BITS, nonce))
            updateAAD(associatedData)
        }
    }

    private companion object {
        const val ALGORITHM = "AES"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
