package qubictech.iot.automation.broilerfirm.data.repo

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import qubictech.iot.automation.broilerfirm.data.model.*
import qubictech.iot.automation.broilerfirm.utils.Resource
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

private const val TAG = "MyFirebaseDatabase"
private const val pattern = "dd/MM/yyyy hh:mm:ss"

object MyFirebaseDatabase {
    private val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    val currentUserData = MutableLiveData<Resource<Any?>>()
    val currentDeviceData = MutableLiveData<Resource<List<DeviceData>?>>()
    private val updateDeviceStatus = MutableLiveData<Resource<Device>>()

    @SuppressLint("SimpleDateFormat")
    fun Long.convertedDateTime(): String = SimpleDateFormat(pattern).format(this)

    init {
        Logger.getLogger("MyFirebaseDatabase")
            .warning("==============Created=============")
        getCurrentData()
        getUserInfo()
    }

    private fun getCurrentData() {
        user?.let {
            currentDeviceData.value = Resource.loading(null)

            val mRef = ref.child("user/${it.uid}/firm_data/devices")

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    currentDeviceData.value = Resource.error(null, p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val devices = mutableListOf<DeviceData>()
                    p0.children.map { snapshot ->
                        val key = snapshot.key
                        val device = snapshot.getValue(Device::class.java)

                        if (device != null && key != null) {
                            devices.add(DeviceData(key, device))
                        }
                    }
                    currentDeviceData.value = Resource.success(devices)
                }
            })
        }
    }

    private fun getUserInfo() {
        user?.let {
            currentUserData.value = Resource.loading(null)

            val mRef = ref.child("user/${it.uid}/info/name")

            mRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    currentUserData.value = Resource.error(null, p0.message)
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val name = p0.value
                    currentUserData.value = Resource.success(name)
                }
            })
        }
    }

    fun updateDeviceStatus(device: DeviceData): Resource<Device>? {
        updateDevice(device)
        return updateDeviceStatus.value
    }

    private fun updateDevice(device: DeviceData) {
        user?.let { firebaseUser ->
            updateDeviceStatus.value = Resource.loading(null)

            val mRef = ref.child("user/${firebaseUser.uid}/firm_data/devices/${device.key}")
            mRef.updateChildren(device.device.toMap())
                .addOnCompleteListener { task ->
                    if (task.isComplete) {
                        val notificationRef = ref.child("user/${firebaseUser.uid}/notifications")
                        val mName =
                            if (device.device.status!!) "${device.device.name} is turned on" else "${device.device.name} is turned off"

                        val notification = Log(
                            id = notificationRef.push().key,
                            time = Date().time.convertedDateTime(),
                            name = mName
                        )

                        notificationRef.push().updateChildren(notification.toMap())

                        updateDeviceStatus.value = Resource.success(device.device)
                    } else if (task.isCanceled) {
                        task.exception?.message?.let {
                            updateDeviceStatus.value = Resource.error(null, it)
                        }
                    }
                }
        }
    }

    fun getDht11Data(records: (List<Dht11Data?>) -> Unit) {
        user?.let { firebaseUser ->
            val mRef = ref.child("user/${firebaseUser.uid}/firm_data/dht11")
            mRef
                .limitToLast(15)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChildren()) {
                            val dht11SensorData = mutableListOf<Dht11Data?>()
                            snapshot.children.forEach { record ->
                                val data = record.getValue(Dht11Data::class.java)
                                try {
                                    data?.timestamp = record?.key?.toLong()
                                } catch (e: NumberFormatException) {
                                    e.printStackTrace()
                                }
                                dht11SensorData.add(data)
                            }

                            records(dht11SensorData)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        Log.d(TAG, "onCancelled: ${p0.message}")
                    }
                })

        }
    }

    fun getWaterLevelSensorData(records: (WaterLevelSensor?) -> Unit) {
        user?.let { firebaseUser ->
            val mRef = ref.child("user/${firebaseUser.uid}/firm_data/water-level")
            mRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        records(snapshot.getValue(WaterLevelSensor::class.java))
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG, "onCancelled: ${p0.message}")
                }
            })
        }
    }

    fun addNewTask(taskReminder: TaskReminder): Task<Void>? {
        user?.let { firebaseUser ->
            updateDeviceStatus.value = Resource.loading(null)

            val mRef = ref.child("user/${firebaseUser.uid}/task")

            return if (taskReminder.id == null) {
                val key = mRef.push().key!!
                taskReminder.id = key
                mRef.child(key).setValue(taskReminder.toMap())
            } else mRef.child(taskReminder.id!!).updateChildren(taskReminder.toMap())

        }
        return null
    }

    fun getAllTask(task: (List<TaskReminder>) -> Unit) {
        user?.let { firebaseUser ->
            updateDeviceStatus.value = Resource.loading(null)

            val mRef = ref.child("user/${firebaseUser.uid}/task")
            mRef.orderByChild("date").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        val taskList = mutableListOf<TaskReminder>()
                        snapshot.children.forEach {
                            val taskFromSnapshot = it.getValue(TaskReminder::class.java)
                            taskFromSnapshot?.let { mTask -> taskList.add(mTask) }
                        }
                        task(taskList)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, "onCancelled: ", p0.toException())
                }
            })
        }
    }
}