package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    val binding: FragmentStatisticsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entries = mutableListOf<PieEntry>()
        for(i in 0..3) {
            entries.add(PieEntry(25f))
        }

        val dataset = PieDataSet(entries, null)

        val colors = listOf(
            requireContext().getColor(R.color.general_brand),
            ColorTemplate.JOYFUL_COLORS[0],
            ColorTemplate.MATERIAL_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[0])
        dataset.colors = colors
        val pieData = PieData(dataset)
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.holeRadius = 60f
        binding.pieChart.transparentCircleRadius = binding.pieChart.holeRadius
        binding.pieChart.description = Description().apply { text = "" }

        binding.pieChart.legend.apply {
            this.isEnabled= false
        }
        binding.pieChart.centerText = "%"
        binding.pieChart.setCenterTextSize(40f)
        binding.pieChart.setDrawEntryLabels(false)
        binding.pieChart.data = pieData.apply { setDrawValues(false) }
    }

}