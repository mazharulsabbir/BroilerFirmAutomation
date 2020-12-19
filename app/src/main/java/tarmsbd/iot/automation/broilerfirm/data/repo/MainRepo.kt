package tarmsbd.iot.automation.broilerfirm.data.repo

import android.content.Context
import androidx.lifecycle.liveData
import androidx.preference.PreferenceManager
import kotlinx.coroutines.delay
import tarmsbd.iot.automation.broilerfirm.data.api.RetrofitBuilder
import tarmsbd.iot.automation.broilerfirm.utils.Resource
import java.util.logging.Logger

private const val TAG = "MainRepo"

object MainRepo {
    private val retrofitBuilder = RetrofitBuilder

    fun getWeatherData(context: Context) = liveData {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
//        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, _ ->
//            val mTempHumiditySync = sharedPreferences.getBoolean("sync", true)
//            val mTempHumiditySyncTime = sharedPreferences.getString("sync_time", "5").toString()
//
//            Logger.getLogger(TAG)
//                .warning("Bool $mTempHumiditySync > Time: $mTempHumiditySyncTime")
//        }
        emit(Resource.loading(null))
        try {
            var mTempHumiditySync: Boolean = true

            do {
                if (mTempHumiditySync)
                    emit(Resource.success(retrofitBuilder.weatherApiService.getWeatherData()))

                mTempHumiditySync = sharedPreferences.getBoolean("sync", true)
                val mTempHumiditySyncTime = sharedPreferences.getString("sync_time", "5").toString()

                val delay = mTempHumiditySyncTime
                    .replace("Minutes", "")
                    .trim().toLong() * 1000

                Logger.getLogger(TAG)
                    .warning("Sync $mTempHumiditySync > Time: $mTempHumiditySyncTime")

                delay(delay)
            } while (true)

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.error(null, "Failed to load data"))
        }
    }
}