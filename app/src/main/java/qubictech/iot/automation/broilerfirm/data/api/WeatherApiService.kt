package qubictech.iot.automation.broilerfirm.data.api

import qubictech.iot.automation.broilerfirm.data.model.weather.WeatherData
import qubictech.iot.automation.broilerfirm.utils.Constraints.Companion.API_KEY
import qubictech.iot.automation.broilerfirm.utils.Constraints.Companion.CHANNEL_ID
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApiService {
    @GET("/channels/{channel_id}/feeds.json")
    suspend fun getWeatherData(
        @Path("channel_id") channel_id: Int? = CHANNEL_ID,
        @Query("api_key") api_key: String? = API_KEY,
        @Query("results") result: Int? = 10
    ): WeatherData
}