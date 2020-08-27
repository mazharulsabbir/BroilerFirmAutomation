package tarmsbd.iot.automation.broilerfirm.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_main.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.ui.main.adapter.FragmentsContainer
import tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments.DevicesFragment
import tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments.SettingsFragment
import tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments.UsesFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val fragments = mutableListOf(
        DevicesFragment(),
        UsesFragment(),
        SettingsFragment()
    )

    private var fragmentsContainer: FragmentsContainer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        0 -> bottom_nav.selectedItemId = R.id.devices
                        1 -> bottom_nav.selectedItemId = R.id.uses
                        2 -> bottom_nav.selectedItemId = R.id.settings
                    }
                }
            })
        }
        bottom_nav.apply {
            this.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.devices -> {
                        container.currentItem = 0
                        fragmentsContainer?.notifyDataSetChanged()
                    }
                    R.id.uses -> {
                        container.currentItem = 1
                        fragmentsContainer?.notifyDataSetChanged()
                    }
                    R.id.settings -> {
                        container.currentItem = 2
                        fragmentsContainer?.notifyDataSetChanged()
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
        }
    }
}