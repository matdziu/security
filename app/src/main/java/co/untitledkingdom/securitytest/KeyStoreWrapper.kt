package co.untitledkingdom.securitytest

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.security.auth.x500.X500Principal


/**
 * Created by dziubek on 09/02/2018.
 */
class KeyStoreWrapper(private val context: Context) {

    fun getAndroidKeyStoreAsymmetricKey(alias: String): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        return if (!keyStore.containsAlias(alias)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                initGeneratorWithKeyGenParameterSpec(keyPairGenerator, alias)
            } else {
                initGeneratorWithKeyPairGeneratorSpec(keyPairGenerator, alias)
            }
            keyPairGenerator.generateKeyPair()
        } else {
            val entry = keyStore.getEntry(alias, null)
            val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
            val publicKey = keyStore.getCertificate(alias).publicKey
            KeyPair(publicKey, privateKey)
        }
    }

    @Suppress("DEPRECATION")
    private fun initGeneratorWithKeyPairGeneratorSpec(keyPairGenerator: KeyPairGenerator, alias: String) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.YEAR, 100)

        val builder = KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSerialNumber(BigInteger.ONE)
                .setSubject(X500Principal("CN=$alias CA Certificate"))
                .setStartDate(startDate.time)
                .setEndDate(endDate.time)

        keyPairGenerator.initialize(builder.build())
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun initGeneratorWithKeyGenParameterSpec(keyPairGenerator: KeyPairGenerator, alias: String) {
        val builder = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
        keyPairGenerator.initialize(builder.build())
    }
}