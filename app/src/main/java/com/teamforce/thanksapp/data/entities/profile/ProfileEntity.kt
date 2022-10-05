package com.teamforce.thanksapp.data.entities.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProfileEntity(
    @SerializedName("username")
    val username: String?,
    @SerializedName("profile")
    val profile: ProfileBeanEntity,
): Parcelable

