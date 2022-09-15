package com.teamforce.thanksapp.presentation.fragment.historyScreen

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentHistoryBinding
import com.teamforce.thanksapp.model.domain.HistoryModel
import com.teamforce.thanksapp.presentation.adapter.HistoryAdapter
import com.teamforce.thanksapp.presentation.viewmodel.HistoryViewModel
import com.teamforce.thanksapp.utils.UserDataRepository


class HistoryFragment : Fragment() {


    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private lateinit var viewModel: HistoryViewModel
    private lateinit var recyclerView: RecyclerView
    private val tabGroup: TabLayout by lazy { binding.tabGroup }
    private var allTransactionsList: List<HistoryModel> = emptyList()
    private var receivedTransactionsList: List<HistoryModel> = emptyList()
    private var sentTransactionsList: List<HistoryModel> = emptyList()


    private val username: String = UserDataRepository.getInstance()?.username.toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.balanceFragment, R.id.feedFragment, R.id.transactionFragment, R.id.historyFragment))
        val toolbar = binding.toolbar
        val collapsingToolbar = binding.collapsingToolbar
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
        initViews()
        loadDataFromServer()
        setDataWithChip(view)

        val optionForProfileFragment = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
            .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
            .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
            .setPopExitAnim(R.anim.bottom_in)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()
        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.action_historyFragment_to_profileGraph, null, optionForProfileFragment )
        }
    }

    private fun initViews() {
        viewModel = HistoryViewModel()
        viewModel.initViewModel()
        swipeToRefresh = binding.swipeRefreshLayout
        swipeToRefresh.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        recyclerView = binding.historyRv
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager =  object : LinearLayoutManager(context){
            override fun canScrollVertically(): Boolean { return false }
        }


        recyclerView.adapter = HistoryAdapter(username, requireContext(), viewModel)

        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    recyclerView.visibility = View.GONE
                    swipeToRefresh.isRefreshing = true
                } else {
                    recyclerView.visibility = View.VISIBLE
                    swipeToRefresh.isRefreshing = false
                }
            }
        )

        tabGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                refreshRecyclerView(tab?.position?:0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        swipeToRefresh.setOnRefreshListener {
            loadDataFromServer()
            swipeToRefresh.isRefreshing = false
        }
    }

    private fun loadDataFromServer(){
        UserDataRepository.getInstance()?.token?.let { token ->
            UserDataRepository.getInstance()?.username?.let { username ->
                viewModel.loadTransactionsList(
                    token,
                    username
                )
            }
        }
    }

    private fun setDataWithChip(view: View){
        viewModel.allTransactions.observe(
            viewLifecycleOwner,
            Observer {
                allTransactionsList = sparseArrayToReversedList(it)
                if (tabGroup.selectedTabPosition == 0) {
                    (recyclerView.adapter as HistoryAdapter).submitList(allTransactionsList)
                }
            }
        )
        viewModel.receivedTransactions.observe(
            viewLifecycleOwner,
            Observer {
                receivedTransactionsList = sparseArrayToReversedList(it)
                if (tabGroup.selectedTabPosition == 1) {
                    (recyclerView.adapter as HistoryAdapter).submitList(receivedTransactionsList)
                }
            }
        )

        viewModel.sentTransactions.observe(
            viewLifecycleOwner,
            Observer {
                sentTransactionsList = sparseArrayToReversedList(it)
                if (tabGroup.selectedTabPosition == 2) {
                    (recyclerView.adapter as HistoryAdapter).submitList(sentTransactionsList)
                }
            }
        )

        viewModel.transactionsLoadingError.observe(
            viewLifecycleOwner,
            Observer {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d("Token", " Ошибка в HistoryViewModel ля ля ля   $it")
            }
        )

        viewModel.cancelTransaction.observe(
            viewLifecycleOwner,
            Observer {
                Snackbar.make(view, requireContext().resources.getString(R.string.successfulCancel), Snackbar.LENGTH_LONG)
                    .setBackgroundTint(context?.getColor(R.color.minor_success)!!)
                    .setTextColor(context?.getColor(R.color.white)!!)
                    .show()
            }
        )
    }



    private fun refreshRecyclerView(checkedId: Int) {
        val transactions: List<HistoryModel> = when (checkedId) {
            0 -> allTransactionsList
            1 -> receivedTransactionsList
            2 -> sentTransactionsList
            else -> {
                allTransactionsList
            }
        }
        (recyclerView.adapter as HistoryAdapter).submitList(transactions)
    }

    private fun sparseArrayToReversedList(transactions: SparseArray<HistoryModel>): List<HistoryModel> {
        val list: ArrayList<HistoryModel> = ArrayList()
        for (i in 0 until transactions.size()) {
            val model = transactions.get(transactions.keyAt(i))
            if (model != null) {
                list.add(model)
            }
        }
        return list.asReversed()
    }



    companion object {

        const val TAG = "HistoryFragment"

    }
}
