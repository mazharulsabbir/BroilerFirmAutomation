package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_devices.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.Device
import tarmsbd.iot.automation.broilerfirm.data.model.DeviceData
import tarmsbd.iot.automation.broilerfirm.ui.main.adapter.DeviceAdapter
import tarmsbd.iot.automation.broilerfirm.ui.main.viewmodel.MainViewModel
import tarmsbd.iot.automation.broilerfirm.utils.OnItemClickListener
import tarmsbd.iot.automation.broilerfirm.utils.Status
import java.util.*
import java.util.logging.Logger

private const val TAG = "DevicesFragment"

class DevicesFragment : Fragment(R.layout.fragment_devices) {
    private lateinit var mainViewModel: MainViewModel

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.getCurrentUserData.observe(viewLifecycleOwner, Observer {
            Logger.getLogger(TAG).warning("User Data: ${it.data}")
            if (it.status == Status.SUCCESS) {
                greetings.text = "Good Day,\n${it.data}"
            } else {
                Log.d(TAG, "onViewCreated: Failed to load data!")
            }
        })

        mainViewModel.getDevicesData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    Log.d(TAG, "onViewCreated: DONE!")
                    resource.data?.let { deviceData ->
                        val devices = mutableListOf<Device>()
                        deviceData.map {
                            devices.add(it.device)
                        }
                        Logger.getLogger(TAG).warning("Data: $devices")

                        setupView(deviceData, devices)
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
        })

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

    private fun setupView(deviceData: List<DeviceData>, devices: MutableList<Device>) {
        val deviceAdapter = DeviceAdapter(requireContext())
        deviceAdapter.submitList(devices)
        deviceAdapter.onItemClickListener(object :
            OnItemClickListener {
            override fun onItemClicked(p: Int) {
                devices[p].status = !devices[p].status!!
                devices[p].on_off_time = Date().time

                mainViewModel.updateDeviceStatus(deviceData[p])
                    .observe(viewLifecycleOwner,
                        Observer { device ->
                            devices[p].status = device?.data?.status!!
                            deviceAdapter.notifyItemChanged(p)
                        })
            }
        })

        // setup recycler view
        recycler_view.apply {
            this.hasFixedSize()
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = deviceAdapter
        }
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
            tempEntry, "Temperature (°C)"
        )

        dataset1.color = R.color.red

        val dataset2 = LineDataSet(
            humidityEntry, "Humidity (%)"
        )

        val dataSets = mutableListOf<ILineDataSet>(
            dataset1,
            dataset2
        )

        val lineData = LineData(
            dataSets
        )

        val description = Description()
        description.text = "Temperature and Humidity data"
        description.setPosition(0f, 0f)

        line_chart_temp_humidity.data = lineData
        line_chart_temp_humidity.description = description
        line_chart_temp_humidity.invalidate() // refresh data
    }
}