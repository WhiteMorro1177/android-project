package ru.mirea.tsybulko.mieraproject

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.mirea.tsybulko.mieraproject.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding

    private lateinit var mAuth: FirebaseAuth
    private val logTag: String = "AuthenticationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions(arrayOf(Manifest.permission.INTERNET))

        mAuth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            createAccount(
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString()
            )
        }

        binding.verifyButton.apply {
            isEnabled = false
            setOnClickListener { sendEmailVerification() }
        }

        binding.signInButton.setOnClickListener {
            signIn(
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString()
            )
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(logTag, "createAccount: $email")
        if (!validateForm()) {
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful) {
                Log.d(logTag, "createUserWithEmail: success")
                updateUI(mAuth.currentUser)
            } else {
                Log.w(
                    logTag, "createUserWithEmail: failure",
                    task.exception
                )
                Toast.makeText(
                    this@AuthenticationActivity, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
                updateUI(null)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            if (user.isEmailVerified) startActivity(Intent(this, MainActivity::class.java))

            binding.textView2.text = getString(
                R.string.email_password_status_fmt,
                user.email,
                user.isEmailVerified
            )
            binding.textView.text = getString(R.string.firebase_status_fmt, user.uid)
            binding.signInButton.text = "Sign out"
            binding.signInButton.setOnClickListener {
                signOut()
            }

            binding.registerButton.isEnabled = false
            binding.verifyButton.isEnabled = !user.isEmailVerified
        } else {
            binding.textView2.setText(R.string.signed_out)
            binding.textView.text = null
            binding.signInButton.text = "Sign in"
            binding.signInButton.setOnClickListener {
                signIn(
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            }

            binding.registerButton.isEnabled = true
        }
    }

    private fun signIn(email: String, password: String) {
        Log.d(logTag, "signIn: $email")
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this)
        { task ->
            if (task.isSuccessful) {
                Log.d(logTag, "signInWithEmail: success")

                val user = mAuth.currentUser
                if (!user!!.isEmailVerified) binding.verifyButton.isEnabled = true
                else startActivity(Intent(this, MainActivity::class.java))

                updateUI(user)
            } else {
                Log.w(logTag, "signInWithEmail: failure", task.exception)
                Toast.makeText(
                    this@AuthenticationActivity, "Authentication failed.",
                    Toast.LENGTH_SHORT
                ).show()
                updateUI(null)
            }

            if (!task.isSuccessful) {
                binding.textView2.setText(R.string.auth_failed)
            }
        }
    }

    private fun signOut() {
        mAuth.signOut()
        binding.verifyButton.isEnabled = false
        updateUI(null)
    }

    private fun sendEmailVerification() {
        binding.verifyButton.isEnabled = false
        val user = mAuth.currentUser
        user!!.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                binding.verifyButton.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@AuthenticationActivity,
                        "Verification email sent to " + user.email,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e(logTag, "sendEmailVerification", task.exception)
                    Toast.makeText(
                        this@AuthenticationActivity,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun validateForm(): Boolean {
        val email: String = binding.editTextEmail.text.toString()
        val password: String = binding.editTextPassword.text.toString()
        if (email.isEmpty()) {
            binding.editTextEmail.error = "Required."
            return false
        }
        if (password.isEmpty()) {
            binding.editTextPassword.error = "Required."
            return false
        }
        if (password.length < 6) {
            binding.editTextPassword.error = "Password should be at least 6 characters."
            return false
        }
        binding.editTextEmail.error = null
        binding.editTextPassword.error = null
        return true
    }

    private fun checkAndRequestPermissions(permissionsToCheck: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissionsToCheck) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty())
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 200)
    }
}