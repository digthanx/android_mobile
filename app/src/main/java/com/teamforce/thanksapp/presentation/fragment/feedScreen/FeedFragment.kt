package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentFeedBinding
import com.teamforce.thanksapp.presentation.adapter.feed.PagerAdapter
import com.teamforce.thanksapp.presentation.viewmodel.feed.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val binding: FragmentFeedBinding by viewBinding()
    private val viewModel: FeedViewModel by viewModels()
    private var pagerAdapter: PagerAdapter? = null
    private var mediator: TabLayoutMediator? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            pagerAdapter = PagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            viewPager.adapter = pagerAdapter

            mediator = TabLayoutMediator(tabGroup, viewPager) { tab, pos ->
                when (pos) {
                    0 -> tab.text = getString(R.string.allEvent)
                    1 -> tab.text = getString(R.string.mineEvent)
                    2 -> tab.text = getString(R.string.publicEvent)
                }
            }
            mediator?.attach()
        }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        pagerAdapter = null
        super.onDestroyView()
    }


//    private lateinit var navController: NavController
//    private lateinit var swipeToRefresh: SwipeRefreshLayout
//    private var allFeedsList: List<FeedResponse> = emptyList()
//    private var mineFeedsList: List<FeedResponse> = emptyList()
//    private var publicFeedsList: List<FeedResponse> = emptyList()
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initView()
//        swipeToRefresh.isRefreshing = true
//        inflateRecyclerView()
//        getListsFromDb()
//        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
//            refreshRecyclerView(checkedId)
//        }
//        binding.profile.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_feedFragment_to_profileGraph, null,
//                OptionsTransaction().optionForProfileFragment
//            )
//        }
//        swipeToRefresh.setOnRefreshListener {
//            inflateRecyclerView()
//            swipeToRefresh.isRefreshing = false
//        }
//        //  displaySnack()
//    }
//
//    private fun displaySnack() {
//        binding.notify.setOnClickListener {
//            val snack = Snackbar.make(
//                binding.root,
//                requireContext().resources.getString(R.string.joke),
//                Snackbar.LENGTH_LONG
//            )
//            snack.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
//                override fun onShown(transientBottomBar: Snackbar?) {
//                    super.onShown(transientBottomBar)
//                }
//
//                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
//                    super.onDismissed(transientBottomBar, event)
//                }
//            })
//            snack.setTextMaxLines(3)
//                .setTextColor(context?.getColor(R.color.white)!!)
//                .setAction(context?.getString(R.string.OK)!!) {
//                    snack.dismiss()
//                }
//            snack.show()
//
//        }
//    }
//
//
//    private fun initView() {
//        swipeToRefresh = binding.swipeRefreshLayout
//        swipeToRefresh.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
//        navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.feedFragment,
//                R.id.balanceFragment,
//                R.id.historyFragment
//            )
//        )
//        val toolbar = binding.toolbar
//        val collapsingToolbar = binding.collapsingToolbar
//        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
//        val feedAdapter =
//            FeedAdapter(viewModel.userDataRepository.getUserName().toString().trim(), requireContext())
//        feedAdapter.stateRestorationPolicy =
//            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//        binding.feedRv.adapter = feedAdapter
//        (binding.feedRv.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false
//        feedAdapter.likeClickListener = { mapReaction, position ->
//            viewModel.pressLike(mapReaction)
//            viewModel.isLoadingLikes.observe(viewLifecycleOwner) {
//                if (!it) inflateRecyclerView()
//            }
//        }
//        feedAdapter.dislikeClickListener = { mapReaction, position ->
//            viewModel.pressLike(mapReaction)
//            viewModel.isLoadingLikes.observe(viewLifecycleOwner) {
//                if (!it) inflateRecyclerView()
//            }
//        }
//    }
//
//
//    private fun inflateRecyclerView() {
//            viewModel.loadFeedsList()
//    }
//
//    private fun refreshRecyclerView(checkedId: Int) {
//        val feeds: List<FeedResponse> = when (checkedId) {
//            R.id.chipAllEvent -> allFeedsList
//            R.id.chipMineEvent -> mineFeedsList
//            R.id.chipPublicEvent -> publicFeedsList
//            else -> {
//                Toast.makeText(requireContext(), "Wrong chip", Toast.LENGTH_LONG).show()
//                emptyList()
//            }
//        }
//        // Log.d("Token", "Feeds - ${feeds}")
//        (binding.feedRv.adapter as FeedAdapter).submitList(feeds)
//    }
//
//    private fun getListsFromDb() {
//        viewModel.isLoading.observe(
//            viewLifecycleOwner,
//            Observer { isLoading ->
//                if (isLoading) {
//                    binding.feedRv.visibility = View.VISIBLE
//                    swipeToRefresh.isRefreshing = true
//                } else {
//                    binding.feedRv.visibility = View.VISIBLE
//                    swipeToRefresh.isRefreshing = false
//                }
//            }
//        )
//        viewModel.allFeeds.observe(
//            viewLifecycleOwner,
//            Observer {
//                allFeedsList = it!!
//                if (binding.chipGroup.checkedChipId == R.id.chipAllEvent) {
//                    (binding.feedRv.adapter as FeedAdapter).submitList(allFeedsList)
//                }
//            }
//        )
//        viewModel.myFeeds.observe(
//            viewLifecycleOwner,
//            Observer {
//                mineFeedsList = it
//                if (binding.chipGroup.checkedChipId == R.id.chipMineEvent) {
//                    (binding.feedRv.adapter as FeedAdapter).submitList(mineFeedsList)
//                }
//            }
//        )
//
//        viewModel.publicFeeds.observe(
//            viewLifecycleOwner,
//            Observer {
//                publicFeedsList = it
//                if (binding.chipGroup.checkedChipId == R.id.chipPublicEvent) {
//                    (binding.feedRv.adapter as FeedAdapter).submitList(publicFeedsList)
//                }
//            }
//        )
//
//        viewModel.feedsLoadingError.observe(
//            viewLifecycleOwner,
//            Observer {
//                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
//            }
//        )
//
//    }


}