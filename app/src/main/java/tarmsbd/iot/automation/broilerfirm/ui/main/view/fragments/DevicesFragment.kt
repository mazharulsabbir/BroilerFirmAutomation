package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_devices.*
import tarmsbd.iot.automation.broilerfirm.utils.OnItemClickListener
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.utils.Status
import tarmsbd.iot.automation.broilerfirm.data.model.Temp
import tarmsbd.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase
import tarmsbd.iot.automation.broilerfirm.ui.main.adapter.DeviceAdapter
import java.util.logging.Logger

private const val TAG = "DevicesFragment"

class DevicesFragment : Fragment(R.layout.fragment_devices) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyFirebaseDatabase.getWeatherData { status: Status, temperature: Temp? ->
            if (status == Status.SUCCESS) {
                temp.text = temperature?.f_temp.toString()
                temp_unit.text = "°C"
            } else Toast.makeText(requireContext(), "Failed to load data!", Toast.LENGTH_SHORT)
                .show()
        }

        MyFirebaseDatabase.getUserInfo { status, name ->
            Logger.getLogger(TAG).warning("User Data: $name")
            if (status == Status.SUCCESS) {
                greetings.text = "Good Day,\n$name"
            } else Toast.makeText(requireContext(), "Failed to load data!", Toast.LENGTH_SHORT)
                .show()
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
                    Toast.makeText(requireContext(), "Failed to load data!", Toast.LENGTH_SHORT)
                        .show()
                }

                else->{

                }
            }
        }
    }
}