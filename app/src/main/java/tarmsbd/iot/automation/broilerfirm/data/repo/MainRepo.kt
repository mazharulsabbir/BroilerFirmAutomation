package tarmsbd.iot.automation.broilerfirm.data.repo

import androidx.lifecycle.liveData
import tarmsbd.iot.automation.broilerfirm.data.api.RetrofitBuilder
import tarmsbd.iot.automation.broilerfirm.utils.Resource
import tarmsbd.iot.automation.broilerfirm.utils.Status

object MainRepo {
    private val retrofitBuilder = RetrofitBuilder
    val getWeatherData = liveData {
        emit(Resource(Status.LOADING, null, null))
        try {
            emit(Resource(Status.SUCCESS, retrofitBuilder.weatherApiService.getWeatherData(), null))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource(Status.FAILED, retrofitBuilder.weatherApiService.getWeatherData(), null))

        }
    }
}