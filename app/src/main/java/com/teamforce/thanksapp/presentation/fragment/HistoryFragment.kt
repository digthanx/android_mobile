package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.model.domain.HistoryModel
import com.teamforce.thanksapp.presentation.adapter.HistoryAdapter
import com.teamforce.thanksapp.presentation.viewmodel.HistoryViewModel
import com.teamforce.thanksapp.utils.UserDataRepository

class HistoryFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: HistoryViewModel
    private lateinit var recyclerView: RecyclerView

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

        UserDataRepository.getInstance()?.token?.let {
            UserDataRepository.getInstance()?.username?.let { it1 ->
                viewModel.loadTransactionsList(
                    it,
                    it1
                )
            }
        }
        viewModel.transactions.observe(
            viewLifecycleOwner,
            Observer {
                recyclerView.adapter =
                    UserDataRepository.getInstance()?.username?.let { it1 ->
                        val list: ArrayList<HistoryModel> = ArrayList<HistoryModel>()
                        for (i in 0 until it.size()) {
                            val model = it.get(it.keyAt(i))
                            if (model != null) {
                                list.add(model)
                            }
                        }
                        HistoryAdapter(it1, list.asReversed(), this)
                    }
            }
        )
        viewModel.transactionsLoadingError.observe(
            viewLifecycleOwner,
            Observer {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        )
    }

    override fun onClick(v: View?) {}

    companion object {

        const val TAG = "HistoryFragment"

        @JvmStatic
        fun newInstance() = HistoryFragment()
    }
}
