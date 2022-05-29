package qubictech.iot.automation.broilerfirm.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class Device(
    var id: String?,
    var name: String?,
    var status: Boolean? = false,
    var on_off_time: Long? = null,
    var total_used_time: Long? = null
) : Parcelable {
    constructor() : this(null, null, null, null, null) // required for firebase database

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "status" to status,
            "on_off_time" to on_off_time,
            "total_used_time" to total_used_time
        )
    }
}