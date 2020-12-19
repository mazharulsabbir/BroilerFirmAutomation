package tarmsbd.iot.automation.broilerfirm.ui.main.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_task.*
import tarmsbd.iot.automation.broilerfirm.R

class TaskFragment:Fragment(R.layout.fragment_task) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // todo: get task from firebase
        recycler_view_task.apply {

        }
    }
}