package ru.mirea.tsybulko.mieraproject.ui.fileworks



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentFileWorksBinding
import java.io.FileInputStream
import java.io.IOException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class FileWorksFragment : Fragment() {
    private lateinit var binding: FragmentFileWorksBinding
    //var key: SecretKey? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootFragmentView = inflater.inflate(R.layout.fragment_file_works, container, false)
        binding = FragmentFileWorksBinding.bind(rootFragmentView)
        binding.buttonGet.setOnClickListener { binding.textViewGet.text = getTextFromFile() }


        binding.buttonEncrypt.setOnClickListener {
            val dialog = FileEncryptionDialog()
                dialog.show(activity!!.supportFragmentManager, "mirea")
        }


        return rootFragmentView
    }

    private fun getTextFromFile(): String? {
        var fin: FileInputStream? = null
        try {
            fin = activity!!.openFileInput(
                binding.editTextTextPersonName2.text.toString()
            )
            val bytes = ByteArray(fin.available())
            fin.read(bytes)
            return decryptMsg(bytes, key).toString()
        } catch (exc: IOException) {
            Toast.makeText(context, exc.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                fin?.close()
            } catch (ex: IOException) {
                Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
            }
        }
        return null
    }

    companion object {
        lateinit var key: SecretKey

        fun generateKey(): SecretKey? {
            return try {
                val sr: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
                sr.setSeed("any data used as random seed".toByteArray())
                val kg: KeyGenerator = KeyGenerator.getInstance("AES")
                kg.init(256, sr)
                key = SecretKeySpec(kg.generateKey().encoded, "AES")
                key

            } catch (e: java.lang.Exception) {
                throw java.lang.RuntimeException(e)
            }
        }

        fun encryptMsg(message: String, secret: SecretKey?): ByteArray? {
            var cipher: Cipher? = null
            return try {
                cipher = Cipher.getInstance("AES")
                cipher.init(Cipher.ENCRYPT_MODE, secret)
                cipher.doFinal(message.toByteArray())
            } catch (e: java.lang.Exception) {
                throw RuntimeException(e)
            }
        }

        fun decryptMsg(cipherText: ByteArray?, secret: SecretKey?): String? {
            return try {
                val cipher: Cipher = Cipher.getInstance("AES")
                cipher.init(Cipher.DECRYPT_MODE, secret)
                String(cipher.doFinal(cipherText))
            } catch (e: java.lang.Exception) {
                throw RuntimeException(e)
            }
        }
    }
}
