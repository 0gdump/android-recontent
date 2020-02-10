package open.v0gdump.recontent_demo.ui.main.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_image.view.*
import open.v0gdump.recontent_demo.R
import open.v0gdump.recontent_demo.model.Item
import open.v0gdump.recontent_demo.model.ItemImage

class ImageItemViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup
) : ViewHolder(inflater, parent, R.layout.item_image) {

    override fun setContent(item: Item) {
        if (item !is ItemImage) {
            throw RuntimeException("Incompatible type!")
        }

        Glide
            .with(itemView)
            .load(item.source)
            .into(itemView.imageView)
    }
}