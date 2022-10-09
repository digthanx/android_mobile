package com.teamforce.thanksapp.presentation.fragment.historyScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentHistoryListBinding
import com.teamforce.thanksapp.presentation.viewmodel.HistoryListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryListFragment : Fragment(R.layout.fragment_history_list) {

    private val binding: FragmentHistoryListBinding by viewBinding()
    private val viewModel: HistoryListViewModel by viewModels()
    private var listAdapter: HistoryPageAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sentOnly = arguments?.getInt(SENT_ONLY_KEY)
        val receivedOnly = arguments?.getInt(RECEIVED_ONLY)

        listAdapter = HistoryPageAdapter(viewModel.getUsername()!!)

        binding.list.apply {
            this.adapter = listAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.refreshLayout.setOnRefreshListener {
            listAdapter?.refresh()
            binding.refreshLayout.isRefreshing = false
        }

        lifecycleScope.launch {
            viewModel.getHistory(
                sentOnly!!,
                receivedOnly!!
            ).collect() {
                listAdapter?.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        binding.list.adapter = null
        listAdapter = null
        super.onDestroyView()
    }

    companion object {
        private const val SENT_ONLY_KEY = "sent_only"
        private const val RECEIVED_ONLY = "received_only"

        @JvmStatic
        fun newInstance(
            sentOnly: Int,
            receivedOnly: Int
        ) = HistoryListFragment().apply {
            arguments = Bundle().apply {
                putInt(SENT_ONLY_KEY, sentOnly)
                putInt(RECEIVED_ONLY, receivedOnly)
            }
        }
    }
}