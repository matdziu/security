package co.untitledkingdom.securitytest

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.decryptedTextView
import kotlinx.android.synthetic.main.activity_main.loadButton
import kotlinx.android.synthetic.main.activity_main.saveButton
import kotlinx.android.synthetic.main.activity_main.toEncryptEditText

class MainActivity : AppCompatActivity() {

    private val messageKey = "messageKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cipherWrapper = CipherWrapper(KeyStoreWrapper(this))

        saveButton.setOnClickListener {
            val messageToEncrypt = toEncryptEditText.text.toString()
            val encryptedMessage = cipherWrapper.encrypt(messageToEncrypt)
            saveToSharedPrefs(encryptedMessage)
            toEncryptEditText.setText("")
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

        loadButton.setOnClickListener {
            val encryptedMessage = loadFromSharedPrefs()
            val decryptedMessage = cipherWrapper.decrypt(encryptedMessage)
            decryptedTextView.text = decryptedMessage
            Toast.makeText(this, "Loaded", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveToSharedPrefs(message: String) {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString(messageKey, message)
            commit()
        }
    }

    private fun loadFromSharedPrefs(): String {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
        return sharedPrefs.getString(messageKey, "")
    }
}