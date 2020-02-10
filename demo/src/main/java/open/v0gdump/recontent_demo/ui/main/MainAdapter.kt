package open.v0gdump.recontent_demo.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import open.v0gdump.recontent_demo.model.Item
import open.v0gdump.recontent_demo.model.ItemDiffUtilCallback
import open.v0gdump.recontent_demo.model.ItemImage
import open.v0gdump.recontent_demo.model.ItemText
import open.v0gdump.recontent_demo.ui.main.viewHolder.ImageItemViewHolder
import open.v0gdump.recontent_demo.ui.main.viewHolder.TextItemViewHolder
import open.v0gdump.recontent_demo.ui.main.viewHolder.ViewHolder

class MainAdapter(
    data: List<Item>
) : RecyclerView.Adapter<ViewHolder>() {

    private var mData = data.toMutableList()
    var data: List<Item>
        get() = mData
        set(value) {

            Log.d("Test data", "Test")

            val diffUtilCallback =
                ItemDiffUtilCallback(
                    mData,
                    value
                )
            val diffResult = DiffUtil.calculateDiff(diffUtilCallback, false)

            mData = value.toMutableList()

            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ItemText -> 0
            is ItemImage -> 1
            else -> throw RuntimeException("Unknown viewType!")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            0 -> TextItemViewHolder(
                inflater,
                parent
            )
            1 -> ImageItemViewHolder(
                inflater,
                parent
            )
            else -> throw RuntimeException("Unknown viewType!")
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContent(data[position])
    }
}