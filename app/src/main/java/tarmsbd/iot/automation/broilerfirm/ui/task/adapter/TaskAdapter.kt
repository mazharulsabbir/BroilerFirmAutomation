package tarmsbd.iot.automation.broilerfirm.ui.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.TaskReminder
import tarmsbd.iot.automation.broilerfirm.utils.OnItemClickListener

class TaskAdapter : ListAdapter<TaskReminder, TaskAdapter.TaskHolder>(DiffUtilCallback) {
    private var onItemClickListener: OnItemClickListener? = null

    class TaskHolder constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        val root: ConstraintLayout = view.root_layout

        fun bindView(taskReminder: TaskReminder) {
            view.task_title.text = taskReminder.name
            view.task_subtitle.text = taskReminder.date.toString()
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
        holder.root.setOnClickListener {
            onItemClickListener?.let {
                if(RecyclerView.NO_POSITION != holder.adapterPosition){
                    it.onItemClicked(position)
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }
}

object DiffUtilCallback : DiffUtil.ItemCallback<TaskReminder>() {
    override fun areItemsTheSame(oldItem: TaskReminder, newItem: TaskReminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskReminder, newItem: TaskReminder): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.name == newItem.name &&
                oldItem.createdAt == newItem.createdAt &&
                oldItem.date == newItem.date &&
                oldItem.isCompleted == newItem.isCompleted
    }

}