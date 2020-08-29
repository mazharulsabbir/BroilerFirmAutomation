package tarmsbd.iot.automation.broilerfirm.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import tarmsbd.iot.automation.broilerfirm.data.model.Device
import tarmsbd.iot.automation.broilerfirm.utils.Status

object MyFirebaseDatabase {
    private val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun getCurrentData(data: (Status, List<Device>?) -> Unit) {
        user?.let {
            data(Status.LOADING, null)

            val mRef = ref.child("user/${it.uid}/firm_data/devices")

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    data(Status.FAILED, null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val devices = mutableListOf<Device>()
                    p0.children.map { snapshot ->
                        val device = snapshot.getValue(Device::class.java)
                        if (device != null) {
                            devices.add(device)
                        }
                    }
                    data(Status.SUCCESS, devices)
                }
            })
        }
    }

    fun getUserInfo(data: (Status, Any?) -> Unit) {
        user?.let {
            data(Status.LOADING, null)

            val mRef = ref.child("user/${it.uid}/info/name")

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    data(Status.FAILED, null)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val name = p0.value
                    data(Status.SUCCESS, name)
                }
            })
        }
    }

    fun updateDeviceStatus(deviceId: Int){

    }
}