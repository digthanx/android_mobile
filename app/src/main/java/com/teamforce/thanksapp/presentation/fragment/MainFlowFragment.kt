package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMainFlowBinding
import com.teamforce.thanksapp.utils.OptionsTransaction


class MainFlowFragment : BaseFlowFragment(
    R.layout.fragment_main_flow, R.id.nav_host_fragment_main
) {

    // private val binding by viewBinding(FragmentMainFlowFragmentBinding::bind)

    private var _binding: FragmentMainFlowBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainFlowBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun setupNavigation(navController: NavController) {
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.background = null


        binding.fab.setOnClickListener {
            navController.navigate(R.id.transaction_graph, null, OptionsTransaction().optionForTransaction2)
        }

        binding.bottomNavigation.menu.getItem(0).isChecked = true
        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed_graph -> {
                    navController.navigate(R.id.feed_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemSelectedListener true
                }
                R.id.balance_graph -> {
                    navController.navigate(R.id.balance_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemSelectedListener true
                }
//                R.id.transaction_graph -> {
//                    navController.navigate(R.id.transaction_graph, null, OptionsTransaction().optionForTransaction2)
//                    return@OnItemSelectedListener true
//                }
                R.id.history_graph -> {
                    navController.navigate(R.id.history_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemSelectedListener true
                }
                R.id.challenge_graph -> {
                    navController.navigate(R.id.challenge_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemSelectedListener true
                }
            }
            true
        })

        binding.bottomNavigation.setOnItemReselectedListener(NavigationBarView.OnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.feed_graph -> {
                    navController.navigate(R.id.feed_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemReselectedListener
                }
                R.id.balance_graph -> {
                    navController.navigate(R.id.balance_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemReselectedListener
                }
                R.id.transaction_graph -> {
                    navController.navigate(R.id.transaction_graph, null, OptionsTransaction().optionForTransaction2)
                    return@OnItemReselectedListener
                }
                R.id.history_graph -> {
                    navController.navigate(R.id.history_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemReselectedListener
                }
                R.id.challenge_graph -> {
                    navController.navigate(R.id.challenge_graph, null, OptionsTransaction().optionForTransaction)
                    return@OnItemReselectedListener
                }
            }

        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if( destination.id == R.id.transactionFragment ||
                destination.id == R.id.someonesProfileFragment ||
                destination.id == R.id.additionalInfoFeedItemFragment ||
                destination.id == R.id.editProfileBottomSheetFragment ||
                destination.id == R.id.createChallengeFragment ||
                destination.id == R.id.detailsMainChallengeFragment ||
                destination.id == R.id.createReportFragment){
                hideBottomNavigation()
            }else{
                showBottomNavigation()
            }
        }
    }

    private fun hideBottomNavigation(){
        binding.bottomNavigation.visibility = View.GONE
        binding.bottomAppBar.visibility = View.GONE
        binding.fab.hide()
    }

    private fun showBottomNavigation(){
        binding.bottomNavigation.visibility = View.VISIBLE
        binding.bottomAppBar.visibility = View.VISIBLE
        binding.fab.show()

    }


//    override fun profilePage() {
//        val headerView = binding.navView.getHeaderView(0)
//        val headerLinearLayout = headerView.findViewById<LinearLayout>(R.id.profileFragment)
//        //val headerLinearLayout = headerView.findViewById<Button>(R.id.btn_out_account)
//        headerLinearLayout.setOnClickListener{
//            Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show();
//            activityNavController().navigateSafely(R.id.profileFragment)
//        }
//    }


}

