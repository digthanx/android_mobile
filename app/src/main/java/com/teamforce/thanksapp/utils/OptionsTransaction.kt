package com.teamforce.thanksapp.utils

import androidx.navigation.NavOptions
import com.teamforce.thanksapp.R

class OptionsTransaction {
    val optionForProfileFragment = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
        .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
        .setPopEnterAnim(androidx.appcompat.R.anim.abc_slide_in_bottom)
        .setPopExitAnim(R.anim.bottom_in)
        .build()

    val optionForTransaction = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
        .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
        .setPopEnterAnim(com.google.android.material.R.anim.abc_fade_in)
        .setPopExitAnim(com.google.android.material.R.anim.abc_fade_out)
        .build()

    val optionForResult = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.bottom_out)
        .setPopEnterAnim(com.google.android.material.R.anim.abc_fade_in)
        .setPopExitAnim(com.google.android.material.R.anim.abc_fade_out)
        .build()

    val optionForAdditionalInfoFeedFragment = NavOptions.Builder()
        .setEnterAnim(androidx.transition.R.anim.abc_grow_fade_in_from_bottom)
        .setExitAnim(androidx.transition.R.anim.abc_shrink_fade_out_from_bottom)
        .setPopEnterAnim(com.google.android.material.R.anim.abc_fade_in)
        .setPopExitAnim(com.google.android.material.R.anim.abc_fade_out)
        .build()
}