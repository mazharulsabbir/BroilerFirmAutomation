package qubictech.iot.automation.broilerfirm.data.model.weather


import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("channel")
    val channel: Channel?,
    @SerializedName("feeds")
    val feeds: List<Feed>?
)