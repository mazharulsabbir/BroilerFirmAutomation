package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_devices.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase
import tarmsbd.iot.automation.broilerfirm.ui.main.adapter.DeviceAdapter
import tarmsbd.iot.automation.broilerfirm.ui.main.viewmodel.MainViewModel
import tarmsbd.iot.automation.broilerfirm.utils.OnItemClickListener
import tarmsbd.iot.automation.broilerfirm.utils.Status
import java.util.logging.Logger

private const val TAG = "DevicesFragment"

class DevicesFragment : Fragment(R.layout.fragment_devices) {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyFirebaseDatabase.getUserInfo { status, name ->
            Logger.getLogger(TAG).warning("User Data: $name")
            if (status == Status.SUCCESS) {
                greetings.text = "Good Day,\n$name"
            } else {
                Log.d(TAG, "onViewCreated: Failed to load data!")
//                Toast.makeText(requireContext(), "Failed to load data!", Toast.LENGTH_SHORT)
//                    .show()
            }
        }

        MyFirebaseDatabase.getCurrentData { status, devices ->
            when (status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "onViewCreated: DONE!")
                    Logger.getLogger(TAG).warning("Data: $devices")

                    devices?.let {
                        val deviceAdapter = DeviceAdapter(requireContext())
                        deviceAdapter.submitList(devices)
                        deviceAdapter.onItemClickListener(object :
                            OnItemClickListener {
                            override fun onItemClicked(p: Int) {
                                devices[p].status = !devices[p].status!!
                                deviceAdapter.notifyItemChanged(p)
                            }
                        })

                        recycler_view.apply {
                            this.hasFixedSize()
                            this.layoutManager = GridLayoutManager(requireContext(), 2)
                            this.adapter = deviceAdapter
                        }
                    }
                }

                Status.FAILED -> {
                    Log.d(TAG, "onViewCreated: FAILED!")
//                    Toast.makeText(requireContext(), "Failed to load data!", Toast.LENGTH_SHORT)
//                        .show()
                }

                else -> {

                }
            }
        }

        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainViewModel.getWeatherData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    line_chart_temp_humidity.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    resource.data?.let { data ->
                        Logger.getLogger(TAG).warning("Weather Data: $data") // print the data

                        val tempData = mutableListOf<Float>()
                        val humidityData = mutableListOf<Float>()
                        data.feeds?.map {
                            it.temp?.let { temp -> tempData.add(temp) }
                            it.humidity?.let { humidity -> humidityData.add(humidity) }
                        }

                        setupLineChartData(tempData, humidityData)
                    }
                }
                Status.FAILED -> {
                    line_chart_temp_humidity.visibility = View.GONE
                }
            }

        })
    }

    private fun setupLineChartData(tempData: List<Float?>, humidity: List<Float?>) {
        line_chart_temp_humidity.visibility = View.VISIBLE
        temp.text = tempData.last().toString()

        var index = 5
        val tempEntry = mutableListOf<Entry>()
        for (temp in tempData) {
            temp?.let {
                Entry(
                    index.toFloat(),
                    it
                )
            }?.let {
                tempEntry.add(
                    it
                )
            }

            index += 5
        }

        index = 5
        val humidityEntry = mutableListOf<Entry>()
        for (humidity1 in humidity) {
            humidity1?.let {
                Entry(
                    index.toFloat(),
                    it
                )
            }?.let {
                humidityEntry.add(
                    it
                )
            }

            index += 5
        }
        val dataset1 = LineDataSet(
            tempEntry, "Temperature"
        )

        dataset1.color = android.R.color.holo_red_light

        val dataset2 = LineDataSet(
            humidityEntry, "Humidity"
        )

        val dataSets = mutableListOf<ILineDataSet>(
            dataset1,
            dataset2
        )

        val lineData = LineData(
            dataSets
        )

        line_chart_temp_humidity.data = lineData
        line_chart_temp_humidity.invalidate() // refresh data
    }
}