package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMainFlowBinding


class MainFlowFragment : BaseFlowFragment (
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
       // binding.bottomNavigation.setupWithNavController(navController)
//        binding.navView.setupWithNavController(navController)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.balanceFragment, R.id.feedFragment, R.id.transactionFragment, R.id.historyFragment))
        val toolbar = binding.toolbar
        val collapsingToolbar = binding.collapsingToolbar
        collapsingToolbar.setupWithNavController(toolbar, navController, appBarConfiguration)

        // Неизвестно, можно ли так делать вкупе с тем, что я вручную все внизу описал, будем тестить
        binding.bottomNavigation.setupWithNavController(navController)



        val option = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        val optionForTransaction = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
            .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        val optionForProfileFragment = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
            .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
            .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
            .setPopExitAnim(R.anim.slide_up)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        binding.profile.setOnClickListener{
            navController.navigate(R.id.profileFragment, null, optionForProfileFragment)
        }

        binding.bottomNavigation.menu.getItem(1).isChecked = true
        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.balanceFragment -> {
                    navController.navigate(R.id.balanceFragment, null, option)
                    return@OnItemSelectedListener true
                }
                R.id.feedFragment -> {
                    navController.navigate(R.id.feedFragment, null, option)
                    return@OnItemSelectedListener true
                }
                R.id.transactionFragment -> {
                    navController.navigate(R.id.transactionFragment, null, optionForTransaction)
                    return@OnItemSelectedListener true
                }
                R.id.historyFragment -> {
                    navController.navigate(R.id.historyFragment, null, option)
                    return@OnItemSelectedListener true
                }
            }
            true
        })

        binding.bottomNavigation.setOnItemReselectedListener(NavigationBarView.OnItemReselectedListener{ item ->
            return@OnItemReselectedListener
        })
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

