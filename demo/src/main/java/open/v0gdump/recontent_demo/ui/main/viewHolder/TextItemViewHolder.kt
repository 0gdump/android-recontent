package open.v0gdump.recontent_demo.ui.main.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_text.view.*
import open.v0gdump.recontent_demo.R
import open.v0gdump.recontent_demo.model.Item
import open.v0gdump.recontent_demo.model.ItemText

class TextItemViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup
) : ViewHolder(inflater, parent, R.layout.item_text) {

    override fun setContent(item: Item) {
        if (item !is ItemText) {
            throw RuntimeException("Incompatible type!")
        }

        itemView.textView.text = item.content
    }
}