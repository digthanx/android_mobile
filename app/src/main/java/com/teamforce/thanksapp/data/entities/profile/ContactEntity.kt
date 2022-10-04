package com.teamforce.thanksapp.data.entities.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class ContactEntity(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("contact_type")
    val contact_type: String,
    var contact_id: String
): Parcelable
