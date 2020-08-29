package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.fragment_uses.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.ChartDataModel
import java.util.logging.Logger

private const val TAG = "UsesFragment"

class UsesFragment : Fragment(R.layout.fragment_uses) {

    private val lineDataSets: MutableList<ChartDataModel> = mutableListOf(
        ChartDataModel(20f, 30f),
        ChartDataModel(40f, 50f),
        ChartDataModel(50f, 60f)
    )

    private val dataSets: MutableList<ChartDataModel> = mutableListOf(
        ChartDataModel(5f, 30f),
        ChartDataModel(10f, 32f),
        ChartDataModel(15f, 30f),
        ChartDataModel(20f, 33f)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lineData = mutableListOf<Entry>()
        val barData = mutableListOf<BarEntry>()

        dataSets.map {
            Logger.getLogger(TAG).warning(it.toString())
            barData.add(BarEntry(it.valueX, it.valueY))
        }

        lineDataSets.map {
            Logger.getLogger(TAG).warning(it.toString())
            lineData.add(Entry(it.valueX, it.valueY))
        }

        lineChartTemperature(lineData)
        barChartLight(barData)
    }

    private fun lineChartTemperature(data: List<Entry>) {
        try {
            val lineDataSet = LineDataSet(data, "Temperature")
            lineDataSet.addEntry(data[0])
            lineDataSet.color = R.color.colorAccent

            val lineData = LineData(lineDataSet)
            lineData.notifyDataChanged()

            line_chart_temp.data = lineData
            line_chart_temp.notifyDataSetChanged()
            line_chart_temp.invalidate() // refresh data set
//            line_chart_temp.moveViewTo(data[0].x, data[0].y, YAxis.AxisDependency.RIGHT)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun barChartLight(data: List<BarEntry>) {
        try {
            // light data
            val barDataSet = BarDataSet(data, "Light")
            barDataSet.color = R.color.colorPrimaryDark

            val barData = BarData(barDataSet)
            bar_chart_light_uses.data = barData
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}