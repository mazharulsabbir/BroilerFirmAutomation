package tarmsbd.iot.automation.broilerfirm.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import tarmsbd.iot.automation.broilerfirm.data.repo.MainRepo
import tarmsbd.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase

class MainViewModel : ViewModel() {
    fun updateDeviceStatus(deviceId: Int) = MyFirebaseDatabase.updateDeviceStatus(deviceId)

    val getWeatherData = MainRepo.getWeatherData
}