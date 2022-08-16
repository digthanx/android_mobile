package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.databinding.FragmentFeedBinding
import com.teamforce.thanksapp.databinding.FragmentTransactionResultBinding
import com.teamforce.thanksapp.model.domain.HistoryModel
import com.teamforce.thanksapp.presentation.adapter.FeedAdapter
import com.teamforce.thanksapp.presentation.adapter.HistoryAdapter
import com.teamforce.thanksapp.presentation.viewmodel.FeedViewModel
import com.teamforce.thanksapp.utils.UserDataRepository


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private var viewModel: FeedViewModel = FeedViewModel()
    private var recyclerView = binding.feedRv
    private val progressBar = binding.progressBar
    private val chipGroup = binding.chipGroup

    private val username: String = UserDataRepository.getInstance()?.username.toString()


    private var allTFeedsList: List<FeedResponse> = emptyList()
    private var mineFeedsList: List<FeedResponse> = emptyList()
    private var publicFeedsList: List<FeedResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initViewModel()
        inflateRecyclerView()
        getListsFromDb()
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            refreshRecyclerView(checkedId)
        }
    }

    private fun getListsFromDb(){
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
        viewModel.allFeeds.observe(
            viewLifecycleOwner,
            Observer {
                allTFeedsList = it!!
                if (chipGroup.checkedChipId == R.id.chipAll) {
                    (recyclerView.adapter as FeedAdapter).submitList(allTFeedsList)
                }
            }
        )
        viewModel.myFeeds.observe(
            viewLifecycleOwner,
            Observer {
                mineFeedsList = it
                if (chipGroup.checkedChipId == R.id.chipReceived) {
                    (recyclerView.adapter as FeedAdapter).submitList(mineFeedsList)
                }
            }
        )

        viewModel.publicFeeds.observe(
            viewLifecycleOwner,
            Observer {
                publicFeedsList = it
                if (chipGroup.checkedChipId == R.id.chipSent) {
                    (recyclerView.adapter as FeedAdapter).submitList(publicFeedsList)
                }
            }
        )

        viewModel.feedsLoadingError.observe(
            viewLifecycleOwner,
            Observer {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                Log.d("Token", " Ошибка лял ял ля  ${it}")
            }
        )
    }

    private fun inflateRecyclerView(){
        recyclerView.itemAnimator = DefaultItemAnimator()
        UserDataRepository.getInstance()?.token?.let{ token ->
            UserDataRepository.getInstance()?.username?.let { username ->
                viewModel.loadFeedsList(token = token, user = username)
            }
        }
    }

    private fun refreshRecyclerView(checkedId: Int) {
        val feeds: List<FeedResponse> = when (checkedId) {
            R.id.chipAllEvent -> allTFeedsList
            R.id.chipMineEvent -> mineFeedsList
            R.id.chipPublicEvent -> publicFeedsList
            else -> {
                Toast.makeText(requireContext(), "Wrong chip", Toast.LENGTH_LONG).show()
                emptyList()
            }
        }
        (recyclerView.adapter as FeedAdapter).submitList(feeds)
    }

}