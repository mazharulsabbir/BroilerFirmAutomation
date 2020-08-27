package tarmsbd.iot.automation.broilerfirm.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Device(
    var id: Int?,
    var name: String?,
    var icon: Any?,
    var status: Boolean? = false,
    var on_off_time: Long? = null,
    var total_used_time: Long? = null
) {
    constructor() : this(null, null, null, null, null, null) // required for firebase database

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "icon" to icon,
            "status" to status,
            "on_off_time" to on_off_time,
            "total_used_time" to total_used_time
        )
    }
}