package com.spendwealthy.app.data.gateway

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.spendwealthy.app.domain.gateway.GatewayResult
import com.spendwealthy.app.domain.gateway.SecureStorage
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidSecureStorage(
    context: Context
) : SecureStorage {

    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override suspend fun putString(key: String, value: String): GatewayResult<Unit> {
        return runCatching {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val encrypted = cipher.doFinal(value.encodeToByteArray())
            val storedValue = "${cipher.iv.toBase64()}:${encrypted.toBase64()}"
            preferences.edit().putString(key, storedValue).apply()
            GatewayResult.Success(Unit)
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal menyimpan data aman.", true)
        }
    }

    override suspend fun getString(key: String): GatewayResult<String?> {
        return runCatching {
            val storedValue = preferences.getString(key, null)
                ?: return GatewayResult.Success(null)
            val parts = storedValue.split(":")
            if (parts.size != 2) return GatewayResult.Failure("Format secure storage tidak valid.", recoverable = false)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateSecretKey(),
                GCMParameterSpec(GCM_TAG_LENGTH_BITS, parts[0].fromBase64())
            )
            val decrypted = cipher.doFinal(parts[1].fromBase64()).decodeToString()
            GatewayResult.Success(decrypted)
        }.getOrElse { error ->
            GatewayResult.Failure(error.message ?: "Gagal membaca data aman.", true)
        }
    }

    override suspend fun remove(key: String): GatewayResult<Unit> {
        preferences.edit().remove(key).apply()
        return GatewayResult.Success(Unit)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
        if (existingKey != null) return existingKey

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)

    private fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEY_ALIAS = "spendwealthy_secure_storage_key"
        const val PREFERENCES_NAME = "spendwealthy_secure_storage"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val GCM_TAG_LENGTH_BITS = 128
    }
}
