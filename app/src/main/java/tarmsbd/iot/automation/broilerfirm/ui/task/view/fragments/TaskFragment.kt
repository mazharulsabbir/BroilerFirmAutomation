package tarmsbd.iot.automation.broilerfirm.ui.task.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.Task
import tarmsbd.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase
import tarmsbd.iot.automation.broilerfirm.ui.task.adapter.TaskAdapter
import tarmsbd.iot.automation.broilerfirm.ui.task.viewmodel.TaskViewModel
import java.util.*
import java.util.logging.Logger

private const val TAG = "TaskFragment"

class TaskFragment : Fragment(R.layout.fragment_task) {
    private lateinit var taskViewModel: TaskViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val taskAdapter = TaskAdapter()

        MyFirebaseDatabase.getAllTask {
            Logger.getLogger(TAG).warning("Task: $it")
            taskAdapter.submitList(it)
        }

        add_new_task.setOnClickListener {
            MyFirebaseDatabase.addNewTask(
                Task(
                    name = "Task ${Date().time}",
                    date = Date().time,
                    isCompleted = false
                )
            )
        }

        recycler_view_task.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }
}