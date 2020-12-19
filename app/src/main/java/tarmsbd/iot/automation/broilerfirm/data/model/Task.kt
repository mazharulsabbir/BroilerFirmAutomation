package tarmsbd.iot.automation.broilerfirm.data.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Task(
    val id: String?,
    val name: String?,
    val date: Long?,
    val createdAt: Long?,
    val isCompleted: Boolean?
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