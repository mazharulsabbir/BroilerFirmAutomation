package tarmsbd.iot.automation.broilerfirm.data.model.weather


import com.google.gson.annotations.SerializedName

data class Feed(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("entry_id")
    val entryId: Int?,
    @SerializedName("status")
    val status: Any?
)