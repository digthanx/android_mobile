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
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.databinding.FragmentFeedBinding
import com.teamforce.thanksapp.presentation.adapter.FeedAdapter
import com.teamforce.thanksapp.presentation.viewmodel.FeedViewModel
import com.teamforce.thanksapp.utils.UserDataRepository


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private var viewModel: FeedViewModel = FeedViewModel()

    private val username: String = UserDataRepository.getInstance()?.username.toString()

    private lateinit var navController: NavController
    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private var allFeedsList: List<FeedResponse> = emptyList()
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
        initView()
        inflateRecyclerView()
        getListsFromDb()
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            refreshRecyclerView(checkedId)
        }
        val optionForProfileFragment = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
            .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
            .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
            .setPopExitAnim(R.anim.bottom_in)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()
        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_profileFragment, null, optionForProfileFragment )
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
            R.id.chipAllEvent -> {
                allFeedsList
            }
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
                    binding.chipAllEvent.isChecked = true
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