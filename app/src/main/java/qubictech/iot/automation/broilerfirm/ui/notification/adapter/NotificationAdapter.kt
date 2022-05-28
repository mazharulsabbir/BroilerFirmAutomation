package qubictech.iot.automation.broilerfirm.ui.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_notification.view.*
import qubictech.iot.automation.broilerfirm.R
import qubictech.iot.automation.broilerfirm.data.model.Log

class NotificationAdapter :
    ListAdapter<Log, NotificationAdapter.NotificationHolder>(DiffUtilCallback) {

    class NotificationHolder constructor(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(log: Log?) {
            view.notification_name.text = log?.name
            view.notification_date.text = log?.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        return NotificationHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_notification,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        holder.bindView(getItem(position))
    }
}

object DiffUtilCallback : DiffUtil.ItemCallback<Log>() {
    override fun areItemsTheSame(oldItem: Log, newItem: Log): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Log, newItem: Log): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.name == newItem.name &&
                oldItem.time == newItem.time
    }

}