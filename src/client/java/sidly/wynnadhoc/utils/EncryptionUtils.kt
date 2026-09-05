package sidly.wynnadhoc.utils

import sidly.wynnadhoc.WynnAdhocClient
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import java.util.*

object EncryptionUtils {
    private const val ALGORITHM = "RSA"

    private val knownUsersMap = mutableMapOf<String, PublicKey>()

    private val keyPair: KeyPair by lazy {
        generateKeyPair()
    }

    val defaultKey: KeyPair by lazy {
        generateKeyPair(WynnAdhocClient.MOD_ID)
    }

    private fun generateKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(ALGORITHM)
        generator.initialize(1024)
        return generator.generateKeyPair()
    }

    fun getPublicKey(): PublicKey {
        return keyPair.public
    }

    fun getPrivateKey(): PrivateKey {
        return keyPair.private
    }

    fun publicKeyToBase64(key: PublicKey): String {
        return Base64.getEncoder().encodeToString(key.encoded)
    }

    fun publicKeyFromBase64(encoded: String): PublicKey {
        val bytes = Base64.getDecoder().decode(encoded)
        val spec = X509EncodedKeySpec(bytes)
        return KeyFactory.getInstance(ALGORITHM).generatePublic(spec)
    }

    fun getPublicKey(playerName: String): PublicKey? {
        return knownUsersMap[playerName.lowercase()]
    }

    fun setPublicKey(playerName: String, publicKey: String) {
        return knownUsersMap.set(playerName.lowercase(), publicKeyFromBase64(publicKey))
    }

    fun removeKey(playerName: String) {
        knownUsersMap.remove(playerName.lowercase())
    }

    /**
     * Generates the same RSA keypair for the same input string.
     * DO NOT use this for long-term identity keys.
     */
    private fun generateKeyPair(seed: String): KeyPair {
        val hash = MessageDigest.getInstance("SHA-256").digest(seed.toByteArray(StandardCharsets.UTF_8))
        val random = DeterministicSecureRandom(hash)
        val generator = KeyPairGenerator.getInstance(ALGORITHM)
        generator.initialize(1024, random)
        return generator.generateKeyPair()
    }

    private class DeterministicSecureRandom(
        private val seed: ByteArray
    ) : SecureRandom() {

        private var position = 0

        override fun nextBytes(bytes: ByteArray) {
            var offset = 0

            while (offset < bytes.size) {
                val block = MessageDigest.getInstance("SHA-256")
                    .digest(
                        seed + byteArrayOf(
                            (position ushr 24).toByte(),
                            (position ushr 16).toByte(),
                            (position ushr 8).toByte(),
                            position.toByte()
                        )
                    )

                val length = minOf(block.size, bytes.size - offset)

                System.arraycopy(
                    block,
                    0,
                    bytes,
                    offset,
                    length
                )

                offset += length
                position++
            }
        }
    }
}