package qubictech.iot.automation.broilerfirm.data.model

data class WaterLevelSensor(val level: String?, val value: Int?) {
    constructor() : this(null, null)
}
