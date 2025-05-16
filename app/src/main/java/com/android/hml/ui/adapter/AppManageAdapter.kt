package com.android.hml.ui.adapter

import android.view.ViewGroup
import com.android.hml.service.ConfigManager
import com.android.hml.ui.view.AppItemView

class AppManageAdapter(
    private val onItemClickListener: (String) -> Unit
) : AppSelectAdapter() {

    inner class ViewHolder(view: AppItemView) : AppSelectAdapter.ViewHolder(view) {
        init {
            view.setOnClickListener {
                onItemClickListener.invoke(filteredList[absoluteAdapterPosition])
            }
        }

        override fun bind(packageName: String) {
            (itemView as AppItemView).let {
                it.load(packageName)
                it.showEnabled = ConfigManager.isHideEnabled(packageName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AppItemView(parent.context, false)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(view)
    }
}
