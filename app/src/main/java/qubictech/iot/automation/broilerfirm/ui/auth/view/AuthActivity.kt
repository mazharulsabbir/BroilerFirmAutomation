package qubictech.iot.automation.broilerfirm.ui.auth.view

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*
import qubictech.iot.automation.broilerfirm.R
import qubictech.iot.automation.broilerfirm.ui.main.view.MainActivity


class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val mUser = FirebaseAuth.getInstance().currentUser
        mUser?.let {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish() // close the activity instantly
        }

        login.setOnClickListener {
            val mEmail = email.editText?.text.toString()
            val mPassword = password.editText?.text.toString()

            if (mEmail.isValidEmail(email) && mPassword.isValidPassword(password)) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Logging in, Please wait..")
                progressDialog.show()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish() // close the activity instantly
                        } else {
                            progressDialog.cancel()

                            val dialog = AlertDialog.Builder(this)
                                .setTitle("Login error")
                                .setMessage(it.exception?.localizedMessage)
                                .setPositiveButton("Okay") { dialogInterface, _ ->
                                    dialogInterface.dismiss()
                                }
                                .create()
                            dialog.show()
                        }

                    }
            }
        }
    }

    private fun String.isValidEmail(til: TextInputLayout): Boolean {
        return when {
            this.isEmpty() -> {
                til.error = "Email is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(this)
                .matches() -> {
                til.error = "Email is incorrect. Enter valid email"
                false
            }
            else -> {
                til.error = null
                true
            }
        }
    }

    private fun String.isValidPassword(til: TextInputLayout): Boolean {
        return when {
            this.isEmpty() -> {
                til.error = "Password is required"
                false
            }
            this.length < 6 -> {
                til.error = "Password should be at least 6+ chars"
                false
            }
            else -> {
                til.error = null
                true
            }
        }
    }
}