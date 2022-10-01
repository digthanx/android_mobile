package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentChallengesBinding
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.presentation.adapter.ChallengeAdapter
import com.teamforce.thanksapp.presentation.viewmodel.ChallengesViewModel
import com.teamforce.thanksapp.utils.OptionsTransaction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChallengesFragment : Fragment(R.layout.fragment_challenges) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentChallengesBinding by viewBinding()
    private val viewModel: ChallengesViewModel by viewModels()

    private val navController: NavController by lazy { findNavController() }

    private var allChallenges: List<ChallengeModel> = listOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        loadChallenges()
        loadingChallenge()
        updatingChallenges()

        binding.createBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_challengesFragment_to_createChallengeFragment,
                null,
                OptionsTransaction().optionForEditProfile
            )

        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadChallenges()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initView() {
        binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.feedFragment,
                R.id.balanceFragment,
                R.id.historyFragment,
                R.id.challenge_graph
            )
        )
        val toolbar = binding.toolbar
        val collapsingToolbar = binding.collapsingToolbar
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
        binding.challengeRv.adapter = ChallengeAdapter()
    }

    private fun loadChallenges(){
        viewModel.loadChallenges()
    }

    private fun loadingChallenge(){
        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    binding.challengeRv.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.isRefreshing = true
                } else {
                    binding.challengeRv.visibility = View.VISIBLE
                    binding.swipeRefreshLayout.isRefreshing = false
                   // (binding.challengeRv.adapter as ChallengeAdapter).submitList(allChallenges)

                }
            }
        )
    }

    private fun updatingChallenges(){

        viewModel.challenges.observe(
            viewLifecycleOwner,
            Observer {
                allChallenges = it!!
                if (binding.chipGroup.checkedChipId != R.id.chipAllChallenge) {
                    Log.d("Token", " Challenges ${allChallenges}")
                    (binding.challengeRv.adapter as ChallengeAdapter).submitList(allChallenges)
                }
                (binding.challengeRv.adapter as ChallengeAdapter).submitList(it)
            }
        )

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
    }



}