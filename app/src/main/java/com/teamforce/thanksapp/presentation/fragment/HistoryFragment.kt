package com.teamforce.thanksapp.presentation.fragment

import android.animation.*
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.model.domain.HistoryModel
import com.teamforce.thanksapp.presentation.adapter.HistoryAdapter
import com.teamforce.thanksapp.presentation.viewmodel.HistoryViewModel
import com.teamforce.thanksapp.utils.UserDataRepository


class HistoryFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var recyclerView: RecyclerView
    private var allTransactionsList: List<HistoryModel> = emptyList()
    private var receivedTransactionsList: List<HistoryModel> = emptyList()
    private var sentTransactionsList: List<HistoryModel> = emptyList()

    // При первом входе в аккаунт на телефоне username почему то null
    // Понять почему он null и исправить это
    private val username: String = UserDataRepository.getInstance()?.username.toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = HistoryViewModel()
        viewModel.initViewModel()
        initViews(view)
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.history_rv)
        recyclerView.itemAnimator = DefaultItemAnimator()
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroup)
        Log.d("Token", "Username ${username}")

        UserDataRepository.getInstance()?.token?.let { token ->
            UserDataRepository.getInstance()?.username?.let { username ->
                viewModel.loadTransactionsList(
                    token,
                    username
                )
            }
        }
        recyclerView.adapter = HistoryAdapter(username, this)

        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        )
        viewModel.allTransactions.observe(
            viewLifecycleOwner,
            Observer {
                allTransactionsList = sparseArrayToReversedList(it)
                if (chipGroup.checkedChipId == R.id.chipAll) {
                    (recyclerView.adapter as HistoryAdapter).submitList(allTransactionsList)
                }
            }
        )
        viewModel.receivedTransactions.observe(
            viewLifecycleOwner,
            Observer {
                receivedTransactionsList = sparseArrayToReversedList(it)
                if (chipGroup.checkedChipId == R.id.chipReceived) {
                    (recyclerView.adapter as HistoryAdapter).submitList(receivedTransactionsList)
                }
            }
        )

        viewModel.sentTransactions.observe(
            viewLifecycleOwner,
            Observer {
                sentTransactionsList = sparseArrayToReversedList(it)
                if (chipGroup.checkedChipId == R.id.chipSent) {
                    (recyclerView.adapter as HistoryAdapter).submitList(sentTransactionsList)
                }
            }
        )

        viewModel.transactionsLoadingError.observe(
            viewLifecycleOwner,
            Observer {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            refreshRecyclerView(checkedId)
        }
    }

    private fun refreshRecyclerView(checkedId: Int) {
        val transactions: List<HistoryModel> = when (checkedId) {
            R.id.chipAll -> allTransactionsList
            R.id.chipReceived -> receivedTransactionsList
            R.id.chipSent -> sentTransactionsList
            else -> {
                Toast.makeText(requireContext(), "Wrong chip", Toast.LENGTH_LONG).show()
                emptyList()
            }
        }
        (recyclerView.adapter as HistoryAdapter).submitList(transactions)
    }

    private fun sparseArrayToReversedList(transactions: SparseArray<HistoryModel>): List<HistoryModel> {
        val list: ArrayList<HistoryModel> = ArrayList<HistoryModel>()
        for (i in 0 until transactions.size()) {
            val model = transactions.get(transactions.keyAt(i))
            if (model != null) {
                list.add(model)
            }
        }
        return list.asReversed()
    }

    override fun onClick(v: View?) {
        Log.d("Token", "Click on RV")
        val expInfo = v?.findViewById<View>(R.id.expanded_info_transaction)
        val standardGroup = v?.findViewById<View>(R.id.standard_group)
        if (standardGroup?.visibility == View.VISIBLE) {
            if (expInfo != null) {
                crossfade(expInfo, standardGroup)
            }

        } else {
            if (expInfo != null && standardGroup != null) {
                crossfade(standardGroup, expInfo)
            }
        }
    }

    fun crossfade(inView: View, outView: View) {
        // TODO доделать анимацию, чтобы список плавно возращался
        var inAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        var outAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, 0f)

        inAnimator.addUpdateListener { animator ->
            inView.alpha = animator.animatedValue as Float
        }
        outAnimator.addUpdateListener { animator ->
            outView.alpha = animator.animatedValue as Float
        }

        var set: AnimatorSet = AnimatorSet()
        set.playTogether(outAnimator, inAnimator)
        set.setDuration(300)

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                inView.visibility = View.VISIBLE

            }

            override fun onAnimationEnd(animation: Animator?) {
                outView.visibility = View.GONE
            }
        })
        set.start()
    }


    companion object {

        const val TAG = "HistoryFragment"

        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}
