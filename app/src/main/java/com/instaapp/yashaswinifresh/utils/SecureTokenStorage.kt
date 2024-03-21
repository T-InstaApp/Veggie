package com.instaapp.yashaswinifresh.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.Key
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey

class SecureTokenStorage(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)

    private val keyAlias = "my_token_key"

    private fun generateKey2() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyProtection = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true) // Require user authentication for key usage
            .build()
        keyGenerator.init(keyProtection)
        keyGenerator.generateKey()
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyProtection = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true) // Require user authentication for key usage
            .build()
        keyGenerator.init(keyProtection)
        return keyGenerator.generateKey()
    }



    private fun getKey(): Key? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(keyAlias, null) // null as password is not applicable to Keystore-backed keys
    }

    fun encryptToken(token: String): Boolean {
        val key = getKey() ?: generateKey()

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

        // Generate a random IV
        val iv = generateRandomIV(cipher.blockSize)

        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))

        val encryptedToken = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        val editor = sharedPreferences.edit()
        editor.putString("encrypted_token", Base64.encodeToString(encryptedToken, Base64.NO_WRAP))
        editor.putString("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
        return editor.commit()
    }


    private fun generateRandomIV(size: Int): ByteArray {
        val iv = ByteArray(size)
        SecureRandom().nextBytes(iv)
        return iv
    }

    fun encryptToken2(token: String): Boolean {
        val key = getKey() ?: generateKey()

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        //cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv // Store the initialization vector for decryption
        val encryptedToken = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        val editor = sharedPreferences.edit()
        editor.putString("encrypted_token", Base64.encodeToString(encryptedToken, Base64.NO_WRAP))
        editor.putString("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
        return editor.commit()
    }

    fun decryptToken2(): String? {
        val encryptedTokenString = sharedPreferences.getString("encrypted_token", null) ?: return null
        val ivString = sharedPreferences.getString("iv", null) ?: return null

        val key = getKey() ?: return null

        val encryptedToken = Base64.decode(encryptedTokenString, Base64.NO_WRAP)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return String(cipher.doFinal(encryptedToken), Charsets.UTF_8)
    }

    fun decryptToken(): String? {
        val encryptedTokenString = sharedPreferences.getString("encrypted_token", null) ?: return null
        val ivString = sharedPreferences.getString("iv", null) ?: return null

        val key = getKey() ?: return null

        val encryptedToken = Base64.decode(encryptedTokenString, Base64.NO_WRAP)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        return String(cipher.doFinal(encryptedToken), Charsets.UTF_8)
    }
}