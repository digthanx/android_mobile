package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentFeedBinding
import com.teamforce.thanksapp.presentation.adapter.feed.PagerAdapter
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.ViewLifecycleDelegate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val binding: FragmentFeedBinding by viewBinding()
    private val pagerAdapter by ViewLifecycleDelegate {PagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)}
    private var mediator: TabLayoutMediator? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewPager.adapter = pagerAdapter

            mediator = TabLayoutMediator(tabGroup, viewPager) { tab, pos ->
                when (pos) {
                    0 -> tab.text = getString(R.string.allEvent)
                    1 -> tab.text = getString(R.string.thanks)
                    2 -> tab.text = getString(R.string.winners)
                    3 -> tab.text = getString(R.string.challenges)
                }
            }
            mediator?.attach()
        }

//        val tabTitle = listOf("Все", "Спасибки", "Победители", "Челленджи")
//        setCustomViewForTabItem(tabTitle)

        binding.profile.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_profileGraph, null,
                OptionsTransaction().optionForProfileFragment
            )
        }
    }

    // Попытка сделать кастомную вертску для tabItem результат так себе пока что
    // tabItem не поддаются установке разной ширины для каждого tabItem
//    private fun setCustomViewForTabItem(tabTitles: List<String>){
//        // Iterate over all tabs and set the custom view
//        for (i in 0 until binding.tabGroup.tabCount) {
//            val tab: TabLayout.Tab? = binding.tabGroup.getTabAt(i)
//            tab?.setCustomView(setFieldCustomTabItem(i, tabTitles))
//        }
//    }
//
//    private fun setFieldCustomTabItem(position: Int, tabTitles: List<String>): View{
//        val v: View = LayoutInflater.from(context).inflate(com.teamforce.thanksapp.R.layout.tab_item_for_pager, null)
//        val tv = v.findViewById<View>(com.teamforce.thanksapp.R.id.textViewInTabItem) as TextView
//        tv.setText(tabTitles.get(position))
//        return v
//    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

}