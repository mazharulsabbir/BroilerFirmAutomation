package qubictech.iot.automation.broilerfirm.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import qubictech.iot.automation.broilerfirm.R
import qubictech.iot.automation.broilerfirm.data.model.Device

class AddEditDeviceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_device)

        val device = intent.getParcelableExtra<Device>(EXTRA_DEVICE)
        device?.let {

        }
    }

    companion object {
        private const val TAG = "AddEditDeviceActivity"
        const val EXTRA_DEVICE = "qubictech.iot.automation.broilerfirm.ui.main.view.EXTRA_DEVICE"
    }
}