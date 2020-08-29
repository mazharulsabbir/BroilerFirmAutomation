package tarmsbd.iot.automation.broilerfirm.data.repo

import androidx.lifecycle.liveData
import tarmsbd.iot.automation.broilerfirm.data.api.RetrofitBuilder
import tarmsbd.iot.automation.broilerfirm.utils.Resource

object MainRepo {
    private val retrofitBuilder = RetrofitBuilder
    val getWeatherData = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(retrofitBuilder.weatherApiService.getWeatherData()))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.error(null, "Failed to load data"))
        }
    }
}