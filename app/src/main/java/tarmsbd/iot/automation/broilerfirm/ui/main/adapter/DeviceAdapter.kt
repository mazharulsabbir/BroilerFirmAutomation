package tarmsbd.iot.automation.broilerfirm.ui.main.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_device.view.*
import tarmsbd.iot.automation.broilerfirm.utils.OnItemClickListener
import tarmsbd.iot.automation.broilerfirm.R
import tarmsbd.iot.automation.broilerfirm.data.model.Device

private const val ITEM_ENABLED = 1
private const val ITEM_DISABLED = 0

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
            return oldItem.status == newItem.status && oldItem.name == newItem.name
        }
    }

    class DeviceHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val card = view.card
        fun bind(device: Device?, context: Context?) {
            view.device_name.text = device?.name
            context?.let {
                Glide.with(it).load(device?.icon).error(R.drawable.ic_loader_outline)
                    .into(view.device_icon)
            }
            device?.status?.let { status ->
                if (status) {
                    context?.resources?.getColor(R.color.colorPrimary)?.let {
                        view.device_icon.setColorFilter(
                            it
                        )
                    }
                } else {
                    view.device_icon.setColorFilter(
                        Color.parseColor("#FF000000")
                    )
                }
                view.device_status.text = if (status) "ON" else "OFF"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceHolder {
        val layoutInflater = LayoutInflater.from(context)
        return if (viewType == ITEM_ENABLED) {
            DeviceHolder(
                layoutInflater.inflate(R.layout.item_device_active, parent, false)
            )
        } else {
            DeviceHolder(
                layoutInflater.inflate(R.layout.item_device, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: DeviceHolder, position: Int) {
        holder.bind(getItem(position), context)
        holder.card.setOnClickListener {
            onItemClickListener?.let {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) it.onItemClicked(holder.adapterPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).status!!) ITEM_ENABLED else ITEM_DISABLED
    }
}