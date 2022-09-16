package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.databinding.FragmentFeedBinding
import com.teamforce.thanksapp.presentation.adapter.FeedAdapter
import com.teamforce.thanksapp.presentation.viewmodel.FeedViewModel
import com.teamforce.thanksapp.utils.OptionsTransaction
import com.teamforce.thanksapp.utils.UserDataRepository


class FeedFragment : Fragment(R.layout.fragment_feed) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentFeedBinding by viewBinding()

    private var viewModel: FeedViewModel = FeedViewModel()

    private val username: String = UserDataRepository.getInstance()?.username.toString()

    private lateinit var navController: NavController
    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private var allFeedsList: List<FeedResponse> = emptyList()
    private var mineFeedsList: List<FeedResponse> = emptyList()
    private var publicFeedsList: List<FeedResponse> = emptyList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        swipeToRefresh.isRefreshing = true
        inflateRecyclerView()
        getListsFromDb()
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            refreshRecyclerView(checkedId)
        }
        binding.profile.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_profileGraph, null,
                OptionsTransaction().optionForProfileFragment )
        }
        swipeToRefresh.setOnRefreshListener {
            inflateRecyclerView()
            swipeToRefresh.isRefreshing = false
        }
    }

    private fun initView(){
        swipeToRefresh = binding.swipeRefreshLayout
        swipeToRefresh.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.balanceFragment, R.id.feedFragment, R.id.transactionFragment, R.id.historyFragment))
        val toolbar = binding.toolbar
        val collapsingToolbar = binding.collapsingToolbar
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
        viewModel.initViewModel()
        binding.feedRv.adapter = FeedAdapter(username, requireContext())
    }


    private fun inflateRecyclerView(){
        UserDataRepository.getInstance()?.token?.let{ token ->
            UserDataRepository.getInstance()?.username?.let { username ->
                viewModel.loadFeedsList(token = token, user = username)
            }
        }
    }

    private fun refreshRecyclerView(checkedId: Int) {
        val feeds: List<FeedResponse> = when (checkedId) {
            R.id.chipAllEvent -> allFeedsList
            R.id.chipMineEvent -> mineFeedsList
            R.id.chipPublicEvent -> publicFeedsList
            else -> {
                Toast.makeText(requireContext(), "Wrong chip", Toast.LENGTH_LONG).show()
                emptyList()
            }
        }
       // Log.d("Token", "Feeds - ${feeds}")
        (binding.feedRv.adapter as FeedAdapter).submitList(feeds)
    }

    private fun getListsFromDb(){
        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    binding.feedRv.visibility = View.GONE
                    swipeToRefresh.isRefreshing = true
                } else {
                    binding.feedRv.visibility = View.VISIBLE
                    swipeToRefresh.isRefreshing = false
                }
            }
        )
        viewModel.allFeeds.observe(
            viewLifecycleOwner,
            Observer {
                allFeedsList = it!!
                Log.d("Token", "allFeeds ${allFeedsList}")
                if (binding.chipGroup.checkedChipId == R.id.chipAllEvent) {
                    (binding.feedRv.adapter as FeedAdapter).submitList(allFeedsList)
                }
            }
        )
        viewModel.myFeeds.observe(
            viewLifecycleOwner,
            Observer {
                mineFeedsList = it
               Log.d("Token", "mineFeeds ${mineFeedsList}")
                if (binding.chipGroup.checkedChipId == R.id.chipMineEvent) {
                    (binding.feedRv.adapter as FeedAdapter).submitList(mineFeedsList)
                }
            }
        )

        viewModel.publicFeeds.observe(
            viewLifecycleOwner,
            Observer {
                publicFeedsList = it
                Log.d("Token", "publicFeeds ${publicFeedsList}")
                if (binding.chipGroup.checkedChipId == R.id.chipPublicEvent) {
                    (binding.feedRv.adapter as FeedAdapter).submitList(publicFeedsList)
                }
            }
        )

        viewModel.feedsLoadingError.observe(
            viewLifecycleOwner,
            Observer {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                //Log.d("Token", " Ошибка лял ял ля  ${it}")
            }
        )

    }




}