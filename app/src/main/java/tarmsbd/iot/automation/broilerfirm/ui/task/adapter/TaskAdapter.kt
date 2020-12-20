package tarmsbd.iot.automation.broilerfirm.ui.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.Task

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskHolder>(DiffUtilCallback) {
    class TaskHolder constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(task: Task) {
            view.task_title.text = task.name
            view.task_subtitle.text = task.date.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        return TaskHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_task,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bindView(getItem(position))
    }
}

object DiffUtilCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.name == newItem.name &&
                oldItem.createdAt == newItem.createdAt &&
                oldItem.date == newItem.date &&
                oldItem.isCompleted == newItem.isCompleted
    }

}