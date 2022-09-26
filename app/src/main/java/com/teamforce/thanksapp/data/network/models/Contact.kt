package com.teamforce.thanksapp.data.network.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("contact_type")
    val contact_type: String,
    var contact_id: String
): Parcelable
