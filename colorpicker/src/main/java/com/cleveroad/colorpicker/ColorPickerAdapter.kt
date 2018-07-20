package com.cleveroad.colorpicker

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cleveroad.bootstrap.kotlin_core.adapters.BaseRecyclerViewAdapter
import java.lang.ref.WeakReference

class ColorPickerAdapter(
        context: Context,
        data: List<CircleProperty>,
        private val listener: WeakReference<OnSelectedColorListener>,
        private val selectionMode: Boolean = false) :
        BaseRecyclerViewAdapter<CircleProperty, ColorPickerAdapter.ColorPickerViewHolder>(context, data), OnCircleItemListener {

    companion object {
        private const val DEFAULT_POSITION = -1
    }

    private var currentSelected = DEFAULT_POSITION

    override fun onClickItem(position: Int) {
        if (selectionMode) setSelectedItem(position)
        listener.get()?.onSelectedColor(getItem(position).colorCircle)
    }

    private fun setSelectedItem(position: Int) {
        if (currentSelected != DEFAULT_POSITION) {
            getItem(position).selected = true
            notifyItemChanged(position)
            getItem(currentSelected).selected = false
            notifyItemChanged(currentSelected)
        } else {
            getItem(position).selected = true
            notifyItemChanged(position)
        }
        currentSelected = position
    }

    override fun onBindViewHolder(holder: ColorPickerViewHolder, position: Int) = holder.setItem(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ColorPickerViewHolder.newInstance(LayoutInflater.from(parent.context), parent, this)

    class ColorPickerViewHolder internal constructor(view: View, private val listener: OnCircleItemListener) : RecyclerView.ViewHolder(view) {
        private val cvCircle: CircleView = view.findViewById(R.id.cvCircle)

        companion object {
            internal fun newInstance(layoutInflater: LayoutInflater, parent: ViewGroup, listener: OnCircleItemListener) =
                    ColorPickerViewHolder(layoutInflater.inflate(R.layout.item_color, parent, false), listener)
        }

        fun setItem(circleProperty: CircleProperty) {
            with(circleProperty) {
                cvCircle.apply {
                    setOnClickListener { listener.onClickItem(adapterPosition) }
                    colorLap = colorCircle
                    colorBorderline = colorBorder
                    selection = selected
                }
            }
        }
    }
}