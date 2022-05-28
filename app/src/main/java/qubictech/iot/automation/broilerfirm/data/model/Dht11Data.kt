package qubictech.iot.automation.broilerfirm.data.model

data class Dht11Data(
    val heat_index: Double?,
    val humidity: Int?,
    val temp_c: Double?,
    val temp_f: Double?,
    var timestamp: Long?
) {
    constructor() : this(null, null, null, null, null)
}