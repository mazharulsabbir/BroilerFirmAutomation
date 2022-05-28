package qubictech.iot.automation.broilerfirm.ui.task.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task.*
import qubictech.iot.automation.broilerfirm.R
import qubictech.iot.automation.broilerfirm.data.repo.MyFirebaseDatabase
import qubictech.iot.automation.broilerfirm.ui.task.adapter.TaskAdapter
import qubictech.iot.automation.broilerfirm.ui.task.view.AddEditTaskActivity
import qubictech.iot.automation.broilerfirm.ui.task.viewmodel.TaskViewModel
import qubictech.iot.automation.broilerfirm.utils.OnItemClickListener
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

            taskAdapter.setOnItemClickListener(object :OnItemClickListener{
                override fun onItemClicked(p: Int) {
                    val updateTaskIntent = Intent(requireContext(),AddEditTaskActivity::class.java)
                    updateTaskIntent.putExtra("update_task_intent_extra_task",it[p])
                    startActivity(updateTaskIntent)
                }
            })
        }

        add_new_task.setOnClickListener {
            startActivity(Intent(requireContext(),AddEditTaskActivity::class.java))
        }

        recycler_view_task.apply {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }
}