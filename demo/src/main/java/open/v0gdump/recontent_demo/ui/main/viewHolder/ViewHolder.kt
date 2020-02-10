package open.v0gdump.recontent_demo.ui.main.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import open.v0gdump.recontent_demo.model.Item

abstract class ViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    layoutRes: Int
) : RecyclerView.ViewHolder(inflater.inflate(layoutRes, parent, false)) {

    abstract fun setContent(item: Item)
}