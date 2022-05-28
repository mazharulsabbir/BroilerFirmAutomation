package qubictech.iot.automation.broilerfirm.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import qubictech.iot.automation.broilerfirm.data.model.DeviceData
import qubictech.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase

class MainViewModel : ViewModel() {
    fun updateDeviceStatus(device: DeviceData) = liveData {
        try {
            emit(MyFirebaseDatabase.updateDeviceStatus(device))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val getCurrentUserData = MyFirebaseDatabase.currentUserData
    val getDevicesData = MyFirebaseDatabase.currentDeviceData
}