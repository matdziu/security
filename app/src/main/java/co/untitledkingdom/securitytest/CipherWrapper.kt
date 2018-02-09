package co.untitledkingdom.securitytest

import android.util.Base64
import java.security.KeyPair
import javax.crypto.Cipher

/**
 * Created by dziubek on 09/02/2018.
 */
class CipherWrapper(keyStoreWrapper: KeyStoreWrapper) {

    private val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    private val masterKeyAlias: String = "MASTER_KEY"

    private val keyPair: KeyPair = keyStoreWrapper.getAndroidKeyStoreAsymmetricKey(masterKeyAlias)

    fun encrypt(data: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.public)
        val bytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decrypt(data: String): String {
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private)
        val encryptedData = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }
}