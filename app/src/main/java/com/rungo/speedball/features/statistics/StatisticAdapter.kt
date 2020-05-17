package com.rungo.speedball.features.statistics

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rungo.speedball.data.model.*
import com.rungo.speedball.databinding.ItemStatisticBinding
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat

class StatisticAdapter : RecyclerView.Adapter<StatisticAdapter.ViewHolder>() {

    private var list: List<Result> = listOf()
    private var speedUnit: Boolean = false

    fun setList(list: List<Result>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setSpeedUnit(speedUnit: Boolean) {
        this.speedUnit = speedUnit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStatisticBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    private fun setSpeed(context: Context, speed: Int): String {
        val speedType = getSpeedType(speed)
        val currentSpeed: Any = if (speedUnit) {
            speed.div(1.6)
        } else {
            speed
        }

        return String.format("$currentSpeed %s - %s", getSpeedUnit(context, speedUnit), getSpeedStatus(context, speedType))
    }

    inner class ViewHolder(private var binding: ItemStatisticBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy г., HH:mm")

            binding.tvSpeedState.apply {
                text = setSpeed(context, result.speed)
            }

            binding.tvDateTime.text = result.date.format(formatter)
            binding.tvEmoji.text = getSpeedEmoji(getSpeedType(result.speed))
        }
    }
}