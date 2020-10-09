package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import tarmsbd.iot.automation.broilerfirm.R

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
    }
}