package qubictech.iot.automation.broilerfirm.data.model

data class Dht11Data(
    val heat_index: Float?,
    val humidity: Int?,
    val temp_c: Float?,
    val temp_f: Float?,
    var timestamp: Long?
) {
    constructor() : this(null, null, null, null, null)
}