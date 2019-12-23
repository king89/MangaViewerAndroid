package com.king.mangaviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.king.mangaviewer.R
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.GlideImageHelper
import com.king.mangaviewer.util.glide.CropImageTransformation

open class LocalMangaMenuItemAdapter(
    listener: MangaMenuAdapterListener? = null,
    private val onSelectedChangeListener: OnSelectedChangeListener? = null) :
    MangaMenuItemAdapter(listener) {

    var selectableMode = false
        private set

    private val selectedMap: MutableMap<Int, Boolean> = hashMapOf()

    override fun createDataViewHolder(parent: ViewGroup): RecyclerViewHolders {
        return createHolder(parent)
    }

    fun toggleSelectableMode() {
        selectableMode = selectableMode.not()
        if (!selectableMode) {
            selectedMap.clear()
        }
        notifyDataSetChanged()
        notifySelectedChange()
    }

    private fun toggleSelected(position: Int) {
        val value = selectedMap[position] ?: false

        if (value) {
            selectedMap.remove(position)
        } else {
            selectedMap[position] = true
        }

        notifySelectedChange()
    }

    private fun notifySelectedChange() {
        onSelectedChangeListener?.onChange(
            selectedMap.filter { it.value }
                .map { getItem(it.key) }
        )
    }

    inner class LocalDataViewHolder(itemView: View) : DataViewHolder(itemView) {

        val checkBox by lazy { itemView.findViewById(R.id.checkBox) as CheckBox }

        override fun onBind(item: MangaMenuItem, listener: MangaMenuAdapterListener?) {
            this.also { holder ->
                GlideImageHelper.getMenuCover(holder.imageView, item, CropImageTransformation())
                    .subscribe()
                    .apply { holder.disposable.add(this) }

                val title = item.title
                holder.textView.text = title
                holder.itemView.setOnClickListener {
                    if (selectableMode) {
                        checkBox.toggle()
                        toggleSelected(adapterPosition)
                    } else {
                        listener?.onItemClicked(holder.imageView, item)
                    }
                }
                holder.itemView.setOnLongClickListener {
                    toggleSelected(adapterPosition)
                    toggleSelectableMode()
                    true
                }
                holder.setSelectable(selectableMode, selectedMap[adapterPosition] ?: false)
            }

        }

        fun setSelectable(selectable: Boolean, selected: Boolean) {
            if (selectable) {
                checkBox.visibility = View.VISIBLE
            } else {
                checkBox.visibility = View.GONE
            }
            checkBox.isChecked = selected
        }
    }

    protected fun createHolder(parent: ViewGroup): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_local_manga_menu_item,
            parent, false)
        return LocalDataViewHolder(layoutView)
    }

    interface OnSelectedChangeListener {
        fun onChange(menuList: List<MangaMenuItem>)
    }
}
