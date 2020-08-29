package tarmsbd.iot.automation.broilerfirm.data.repo

import androidx.lifecycle.liveData
import kotlinx.coroutines.delay
import tarmsbd.iot.automation.broilerfirm.data.api.RetrofitBuilder
import tarmsbd.iot.automation.broilerfirm.utils.Resource

object MainRepo {
    private val retrofitBuilder = RetrofitBuilder
    val getWeatherData = liveData {
        emit(Resource.loading(null))
        try {
            while (true){
                emit(Resource.success(retrofitBuilder.weatherApiService.getWeatherData()))
                delay(6000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.error(null, "Failed to load data"))
        }
    }
}