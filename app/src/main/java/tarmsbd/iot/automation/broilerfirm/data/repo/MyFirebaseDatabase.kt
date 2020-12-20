package tarmsbd.iot.automation.broilerfirm.data.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import tarmsbd.iot.automation.broilerfirm.data.model.Device
import tarmsbd.iot.automation.broilerfirm.data.model.DeviceData
import tarmsbd.iot.automation.broilerfirm.data.model.TaskReminder
import tarmsbd.iot.automation.broilerfirm.utils.Resource
import java.util.logging.Logger

private const val TAG = "MyFirebaseDatabase"

object MyFirebaseDatabase {
    private val ref: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    val currentUserData = MutableLiveData<Resource<Any?>>()
    val currentDeviceData = MutableLiveData<Resource<List<DeviceData>?>>()
    private val updateDeviceStatus = MutableLiveData<Resource<Device>>()

    init {
        Logger.getLogger("MyFirebaseDatabase").warning("==============Created=============")
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
                        updateDeviceStatus.value = Resource.success(device.device)
                    } else if (task.isCanceled) {
                        task.exception?.message?.let {
                            updateDeviceStatus.value = Resource.error(null, it)
                        }
                    }
                }
        }
    }

    fun addNewTask(taskReminder: TaskReminder): Task<Void>? {
        user?.let { firebaseUser ->
            updateDeviceStatus.value = Resource.loading(null)

            val mRef = ref.child("user/${firebaseUser.uid}/task")
            val key = mRef.push().key!!
            taskReminder.id = key

            return mRef.child(key).setValue(taskReminder.toMap())
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