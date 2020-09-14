package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import tarmsbd.iot.automation.broilerfirm.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}