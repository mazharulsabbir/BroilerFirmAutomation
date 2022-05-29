package qubictech.iot.automation.broilerfirm

import android.app.Application
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "BroilerFarmAutomation"

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: Application is running...!")
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun onTerminate() {
        Log.i(TAG, "onTerminate: Application is terminated...!")
        super.onTerminate()
    }
}