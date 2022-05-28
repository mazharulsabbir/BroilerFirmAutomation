package qubictech.iot.automation.broilerfirm.data.model.weather


import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName("latitude")
    val latitude: String?,
    @SerializedName("longitude")
    val longitude: String?,
    @SerializedName("name")
    val name: String?
)