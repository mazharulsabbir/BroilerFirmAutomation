package qubictech.iot.automation.broilerfirm.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_device.view.*
import qubictech.iot.automation.broilerfirm.R
import qubictech.iot.automation.broilerfirm.data.model.Device
import qubictech.iot.automation.broilerfirm.utils.OnItemClickListener
import java.text.SimpleDateFormat

class DeviceAdapter(private val context: Context?) :
    ListAdapter<Device, DeviceAdapter.DeviceHolder>(DiffUtilCallback()) {
    private var onItemClickListener: OnItemClickListener? = null

    public fun onItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Device>() {
        override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
            return oldItem.status == newItem.status && oldItem.name == newItem.name && oldItem.on_off_time == newItem.on_off_time
        }
    }

    class DeviceHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.card
        fun bind(device: Device?, context: Context?) {
            view.device_name.text = device?.name
            device?.status?.let { status ->
                view.device_status.text = if (status) "ON" else "OFF"
                view.imageView.visibility = if (status) View.VISIBLE else View.INVISIBLE
                if (status) view.card.setCardBackgroundColor(view.resources.getColor(R.color.colorPrimary))
            }
            view.on_off_time.text = device?.on_off_time?.let { dateTimeConverter(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val layoutInflater = LayoutInflater.from(context)
        return DeviceHolder(
            layoutInflater.inflate(R.layout.item_device, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        holder.bind(getItem(position), context)
        holder.card.setOnClickListener {
            onItemClickListener?.let {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) it.onItemClicked(holder.adapterPosition)
            }
        }
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        fun dateTimeConverter(long: Long): String {
            val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a")
            return simpleDateFormat.format(long)
        }
    }
}