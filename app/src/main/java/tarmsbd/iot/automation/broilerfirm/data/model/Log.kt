package tarmsbd.iot.automation.broilerfirm.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Log(
    val id: String?,
    val time: String?,
    val name: String?
) {
    constructor() : this(null, null, null)

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "time" to time,
            "name" to name
        )
    }
}