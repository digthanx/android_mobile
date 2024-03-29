package com.teamforce.thanksapp.presentation.fragment.balanceScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentBalanceBinding
import com.teamforce.thanksapp.presentation.viewmodel.BalanceViewModel
import com.teamforce.thanksapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val viewModel: BalanceViewModel by viewModels()
    private val sharedViewModel: NotificationSharedViewModel by activityViewModels()

    private lateinit var count: TextView
    private lateinit var distributed: TextView
    private lateinit var leastCount: TextView
    private lateinit var leastDistribute: TextView
    private lateinit var cancelled: TextView
    private lateinit var frozen: TextView
    private lateinit var willBurn: TextView
    private val wholeScreen: LinearLayout by lazy { binding.wholeScreen }
    private lateinit var swipeToRefresh: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.feedFragment,
                R.id.balanceFragment,
                R.id.historyFragment
            )
        )
        val toolbar = binding.toolbar
        val collapsingToolbar = binding.collapsingToolbar
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
        initViews(view)
        loadBalanceData()
        setBalanceData()
        // displaySnack()
        viewModel.isLoading.observe(
            viewLifecycleOwner
        ) { isLoading ->
            if (isLoading) {
                swipeToRefresh.isRefreshing = true
                wholeScreen.visibility = View.GONE
            } else {
                wholeScreen.visibility = View.VISIBLE
                swipeToRefresh.isRefreshing = false
            }
        }

        swipeToRefresh.setOnRefreshListener {
            loadBalanceData()
            swipeToRefresh.isRefreshing = false
        }

        binding.profile.setOnClickListener {
            findNavController().navigate(
                R.id.action_balanceFragment_to_profileGraph,
                null, OptionsTransaction().optionForProfileFragment
            )
        }

        binding.notifyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_balanceFragment_to_notificationFragment)
        }

        sharedViewModel.state.observe(viewLifecycleOwner) { notificationsCount ->
            if (notificationsCount == 0) {
                binding.apply {
                    activeNotifyLayout.invisible()
                    notify.visible()
                }
            } else {
                binding.apply {
                    activeNotifyLayout.visible()
                    notify.invisible()
                    notifyBadge.text = notificationsCount.toString()
                }
            }
        }

    }


    private fun loadBalanceData() {
        viewModel.loadUserBalance()
    }

    private fun setBalanceData() {
        viewModel.balance.observe(viewLifecycleOwner) {
            count.text = it.income.amount.toString()
            distributed.text =
                String.format(getString(R.string.spaceWithContent), it.income.sended.toString())
            leastCount.text = it.distribute.amount.toString()
            leastDistribute.text = String.format(
                getString(R.string.spaceWithContent),
                it.distribute.sended.toString()
            )
            cancelled.text = String.format(
                getString(R.string.spaceWithContent),
                it.distribute.cancelled.toString()
            )
            frozen.text =
                String.format(getString(R.string.spaceWithContent), it.income.frozen.toString())
            Log.d("Token", "Распределено ${it.distribute}")
            try {
                val dateTime: LocalDate =
                    LocalDate.parse(it.distribute.expireDate, DateTimeFormatter.ISO_DATE)
                val from = LocalDate.now()
                val result: Long = ChronoUnit.DAYS.between(from, dateTime)
                val text = result.toString()
                if (isOne(text) && text.length == 1) {
                    willBurn.text = String.format(getString(R.string.will_burn_after), text)
                } else if (isOne(text) && isNotTen(text)) {
                    willBurn.text = String.format(getString(R.string.will_burn_after), text)
                } else if (text.length == 1 && (isTwo(text) || isThree(text) || isFour(text))) {
                    willBurn.text = String.format(getString(R.string.will_burn_after_2), text)
                } else if ((isTwo(text) || isThree(text) || isFour(text)) && isNotTen(text)) {
                    willBurn.text = String.format(getString(R.string.will_burn_after_2), text)
                } else {
                    willBurn.text = String.format(getString(R.string.will_burn_after_3), text)
//                    willBurn.text = getString(R.string.will_burn_today)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e.fillInStackTrace())
            }
        }
    }

    private fun isNotTen(text: String): Boolean {
        return text.length > 1 && text[text.length - 2] != "1".first()
    }

    private fun isOne(text: String): Boolean {
        return text[text.length - 1] == "1".first()
    }

    private fun isTwo(text: String): Boolean {
        return text[text.length - 1] == "2".first()
    }

    private fun isThree(text: String): Boolean {
        return text[text.length - 1] == "3".first()
    }

    private fun isFour(text: String): Boolean {
        return text[text.length - 1] == "4".first()
    }

    private fun initViews(view: View) {
        count = binding.countValueTv
        distributed = binding.distributedValueTv
        leastCount = binding.leastCount
        leastDistribute = binding.leastValueTv
        cancelled = binding.cancelledValueTv
        frozen = binding.frozenValueTv
        willBurn = binding.willBurnTv
        swipeToRefresh = binding.swipeRefreshLayout
        swipeToRefresh.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
    }

    companion object {

        const val TAG = "BalanceFragment"

        @JvmStatic
        fun newInstance() = BalanceFragment()
    }
}