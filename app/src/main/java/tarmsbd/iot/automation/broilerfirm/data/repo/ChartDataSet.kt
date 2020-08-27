package tarmsbd.iot.automation.broilerfirm.data.repo

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import tarmsbd.iot.automation.broilerfirm.data.model.ChartDataModel

object ChartDataSet {
    private val dataSets: MutableList<ChartDataModel> = mutableListOf(
        ChartDataModel(20f, 30f),
        ChartDataModel(40f, 50f),
        ChartDataModel(50f, 60f),
        ChartDataModel(20f, 23f),
        ChartDataModel(27f, 52f),
        ChartDataModel(24f, 34f)
    )

    private var entries: MutableList<Entry>? = null
    private var lightData: MutableList<BarEntry>? = null

    fun lineChartEntries(): MutableList<Entry>? {
        dataSets.map { data ->
            entries?.add(Entry(data.valueX, data.valueY))
        }
        return entries
    }

    fun barChartEntries(): MutableList<BarEntry>? {
        dataSets.map { data ->
            lightData?.add(BarEntry(data.valueX, data.valueY))
        }
        return lightData
    }

}