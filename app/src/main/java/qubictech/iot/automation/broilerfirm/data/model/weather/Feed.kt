package qubictech.iot.automation.broilerfirm.data.model.weather


import com.google.gson.annotations.SerializedName

data class Feed(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("entry_id")
    val entryId: Int?,
    @SerializedName("status")
    val status: Any?,
    @SerializedName("field1")
    val temp: Float?,
    @SerializedName("field2")
    val humidity: Float?,
    @SerializedName("field3")
    val device_1: Float?,
    @SerializedName("field4")
    val device_2: Float?,
    @SerializedName("field5")
    val device_3: Float?,
    @SerializedName("field6")
    val device_4: Float?,
    @SerializedName("field7")
    val device_5: Float?,
    @SerializedName("field8")
    val device_6: Float?
)