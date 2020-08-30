package tarmsbd.iot.automation.broilerfirm.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tarmsbd.iot.automation.broilerfirm.data.model.weather.WeatherData
import tarmsbd.iot.automation.broilerfirm.utils.Constraints

interface WeatherApiService {
    @GET("/channels/{channel_id}/feeds.json")
    suspend fun getWeatherData(
        @Path("channel_id") channel_id: Int? = Constraints.CHANNEL_ID,
        @Query("api_key") api_key: String? = Constraints.API_KEY,
        @Query("results") result: Int? = 10
    ): WeatherData
}