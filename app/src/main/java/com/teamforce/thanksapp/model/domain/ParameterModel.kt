package com.teamforce.thanksapp.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParameterModel(
    val id: Int,
    val value: Int,
    val is_calc: Boolean?
): Parcelable
