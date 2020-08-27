package tarmsbd.iot.automation.broilerfirm.data.model

data class Temp(var c_temp: Float?, var f_temp: Float?, var humidity: Float?, var time: Long?) {
    constructor() : this(null, null, null, null) // required to read data from firebase
}