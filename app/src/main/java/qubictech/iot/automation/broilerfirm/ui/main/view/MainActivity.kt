package qubictech.iot.automation.broilerfirm.ui.main.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import qubictech.iot.automation.broilerfirm.R
import qubictech.iot.automation.broilerfirm.ui.main.adapter.FragmentsContainer
import qubictech.iot.automation.broilerfirm.ui.main.view.fragments.DevicesFragment
import qubictech.iot.automation.broilerfirm.ui.main.view.fragments.SettingsFragment
import qubictech.iot.automation.broilerfirm.ui.task.view.fragments.TaskFragment
import qubictech.iot.automation.broilerfirm.ui.notification.view.NotificationActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val fragments = mutableListOf(
        DevicesFragment(),
        TaskFragment(),
        SettingsFragment()
    )

    private var fragmentsContainer: FragmentsContainer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        fragmentsContainer = FragmentsContainer(supportFragmentManager, fragments)
        container.apply {
            this.adapter = fragmentsContainer
            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> bottom_nav.selectedItemId = R.id.menu_devices
                        1 -> bottom_nav.selectedItemId = R.id.menu_task
                        2 -> bottom_nav.selectedItemId = R.id.menu_settings
                    }
                }
            })
        }
        bottom_nav.apply {
            this.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_devices -> {
                        container.currentItem = 0
                        fragmentsContainer?.notifyDataSetChanged()
                    }
                    R.id.menu_task -> {
                        container.currentItem = 1
                        fragmentsContainer?.notifyDataSetChanged()
                    }
                    R.id.menu_settings -> {
                        container.currentItem = 2
                        fragmentsContainer?.notifyDataSetChanged()
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Make sure you use the same channel id ("default" in this case)
            // while creating notifications.
            val channel = NotificationChannel(
                resources.getString(R.string.default_notification_channel_id),
                "Broiler Farm Automation",
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "Default Notification Channel"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_notification) {
            Log.d(TAG, "onOptionsItemSelected: Navigate to notification activity")
            startActivity(Intent(this, NotificationActivity::class.java))
        } else {
            Log.d(TAG, "onOptionsItemSelected: ${item.title}")
        }
        return super.onOptionsItemSelected(item)
    }
}