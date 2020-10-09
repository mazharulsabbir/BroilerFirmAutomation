package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.UserProfileChangeRequest
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.ui.auth.view.AuthActivity

private const val TAG = "SettingsFragment"

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val user = FirebaseAuth.getInstance().currentUser
        val username = findPreference<EditTextPreference>("username")
        username?.summary = user?.displayName
        username?.setOnPreferenceChangeListener { _, newValue ->

            val profileUpdates =
                UserProfileChangeRequest.Builder()
                    .setDisplayName(newValue.toString())
                    .build()

            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task: Task<Void> ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "User profile updated.", Toast.LENGTH_SHORT).show()
                        username.summary = newValue.toString()
                    }
                }
            return@setOnPreferenceChangeListener true
        }

        val emailAddress = findPreference<Preference>("email_address")
        emailAddress?.summary = user?.email
        emailAddress?.setOnPreferenceChangeListener { _, newValue ->
            FirebaseAuth.getInstance().currentUser
                ?.updateEmail(newValue.toString())
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        emailAddress.summary = newValue.toString()
                        Toast.makeText(context, "Email Changed!", Toast.LENGTH_SHORT).show()
                    } else {
                        if (it.exception is FirebaseAuthRecentLoginRequiredException) {
                            Toast.makeText(
                                context,
                                "Please login again and retry to change email address",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                context,
                                it.exception?.localizedMessage,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        it.exception?.printStackTrace()

                    }
                }

            return@setOnPreferenceChangeListener true
        }

        val changePassword = findPreference<Preference>("request_password_change")
        changePassword?.setOnPreferenceChangeListener { _, newValue ->
            FirebaseAuth.getInstance().currentUser
                ?.updatePassword(newValue.toString())
                ?.addOnCompleteListener {
                    if (it.isSuccessful)
                        Toast.makeText(context, "Password Changed!", Toast.LENGTH_SHORT).show()
                    else {
                        it.exception?.printStackTrace()
                        Toast.makeText(context, it.exception?.localizedMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            return@setOnPreferenceChangeListener true
        }

        val logout = findPreference<Preference>("logout")
        logout?.setOnPreferenceClickListener {

            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, AuthActivity::class.java))
            requireActivity().finish()

            return@setOnPreferenceClickListener true
        }
    }
}