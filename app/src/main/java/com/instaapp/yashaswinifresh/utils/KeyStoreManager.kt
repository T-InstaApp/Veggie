package com.instaapp.yashaswinifresh.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import android.util.Base64
import javax.crypto.spec.GCMParameterSpec

object KeyStoreManager {
    private const val KEY_ALIAS = "PaymentToken"

    fun saveApiKey(context: Context, apiKey: String) {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, keyStore.getKey(KEY_ALIAS, null))

        // Use a secure method to generate a random IV for GCM
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val encryptedApiKey = cipher.doFinal(apiKey.toByteArray())

        // Concatenate IV and encrypted data before storing
        val encryptedDataWithIV = ByteArray(iv.size + encryptedApiKey.size)
        System.arraycopy(iv, 0, encryptedDataWithIV, 0, iv.size)
        System.arraycopy(encryptedApiKey, 0, encryptedDataWithIV, iv.size, encryptedApiKey.size)

        PreferenceProvider(context).setStringValue(
            Base64.encodeToString(encryptedDataWithIV, Base64.DEFAULT),
            PreferenceKey.PAYMENT_TOKEN
        )
    }

    fun getApiKey(context: Context): String? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Retrieve stored encrypted data with IV
        val encryptedDataWithIV =
            Base64.decode(
                PreferenceProvider(context).getStringValue(PreferenceKey.PAYMENT_TOKEN),
                Base64.DEFAULT
            )

        // Extract IV from the stored data
        val iv = ByteArray(12)
        System.arraycopy(encryptedDataWithIV, 0, iv, 0, iv.size)

        // Initialize cipher with GCM parameters
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, keyStore.getKey(KEY_ALIAS, null), gcmParameterSpec)

        // Decrypt the data excluding the IV
        val decryptedApiKey = cipher.doFinal(
            encryptedDataWithIV, iv.size, encryptedDataWithIV.size - iv.size
        )

        return String(decryptedApiKey)
    }

    private fun generateKey() {

        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(false) // Disable randomized encryption for GCM
            .build()
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
}
