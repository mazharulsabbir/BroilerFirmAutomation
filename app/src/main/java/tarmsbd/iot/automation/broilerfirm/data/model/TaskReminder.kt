package tarmsbd.iot.automation.broilerfirm.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class TaskReminder(
    var id: String? = "Task",
    val name: String? = "No Name",
    val date: String? = "",
    val createdAt: Long? = Date().time,
    val isCompleted: Boolean? = false
) {
    constructor() : this(null, null, null, null, null)

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "date" to date,
            "createdAt" to createdAt,
            "isCompleted" to isCompleted
        )
    }
}