package qubictech.iot.automation.broilerfirm.ui.main.view.fragments

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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_devices.*
import qubictech.iot.automation.broilerfirm.R
import qubictech.iot.automation.broilerfirm.data.model.Device
import qubictech.iot.automation.broilerfirm.data.model.DeviceData
import qubictech.iot.automation.broilerfirm.data.model.Dht11Data
import qubictech.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase.getDht11Data
import qubictech.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase.getWaterLevelSensorData
import qubictech.iot.automation.broilerfirm.ui.main.adapter.DeviceAdapter
import qubictech.iot.automation.broilerfirm.ui.main.viewmodel.MainViewModel
import qubictech.iot.automation.broilerfirm.utils.OnItemClickListener
import qubictech.iot.automation.broilerfirm.utils.Status
import java.util.*
import java.util.logging.Logger

private const val TAG = "DevicesFragment"

@SuppressLint("SetTextI18n")
class DevicesFragment : Fragment(R.layout.fragment_devices) {
    private lateinit var mainViewModel: MainViewModel

    override fun onResume() {
        super.onResume()
        FirebaseAuth.getInstance().currentUser?.let {
            greetings.text = "Good Day,"
            username.text = it.displayName
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        FirebaseAuth.getInstance().currentUser?.let {
            greetings.text = "Good Day,"
            username.text = it.displayName
        }

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

        getDht11Data {
            Log.d(TAG, "onViewCreated: getDht11Data-> ${it.size}")
            setupLineChartData(it)
        }

        getWaterLevelSensorData {
            Log.d(TAG, "onViewCreated: getWaterLevelSensorData-> $it")
        }
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
                            devices[p].status = device?.data?.status ?: false
                            deviceAdapter.notifyItemChanged(p)
                        }
                    )
            }
        })

        // setup recycler view
        recycler_view.apply {
            this.hasFixedSize()
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = deviceAdapter
        }
    }

    private fun setupLineChartData(records: List<Dht11Data?>) {
        line_chart_temp_humidity.visibility = View.VISIBLE
        temp.text = "${records.last()?.temp_c ?: 0}"
        humidity_text.text = "Humidity ${records.last()?.humidity ?: 0}"

        val tempEntry = mutableListOf<Entry>()
        val humidityEntry = mutableListOf<Entry>()

        records.forEachIndexed { index, record ->
            record?.let { data ->
                tempEntry.add(Entry(index.toFloat(), data.temp_c ?: 0F))
                humidityEntry.add(Entry(index.toFloat(), data.humidity?.toFloat() ?: 0F))
            }
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
        lineData.setValueTextColor(R.color.colorPrimary)

        val description = Description()
        description.text = "Temperature and Humidity data"
        description.textColor = resources.getColor(R.color.colorPrimary)
        description.setPosition(0f, 0f)

        val legend = line_chart_temp_humidity.legend
        legend.textColor = resources.getColor(R.color.colorPrimary)

        line_chart_temp_humidity.axisLeft.axisMinimum = 0F
        line_chart_temp_humidity.axisRight.axisMinimum = 0F

        line_chart_temp_humidity.data = lineData
        line_chart_temp_humidity.description = description
        line_chart_temp_humidity.invalidate() // refresh data
    }
}