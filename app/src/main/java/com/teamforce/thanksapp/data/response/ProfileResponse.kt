package com.teamforce.thanksapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.teamforce.thanksapp.data.network.models.Contact
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileResponse(
    @SerializedName("username")
    val username: String?,
    @SerializedName("profile")
    val profile: ProfileBean,
):Parcelable {
    @Parcelize
    data class ProfileBean(
        @SerializedName("id")
        val id: String,
        @SerializedName("contacts")
        val contacts: List<Contact>,
        @SerializedName("organization")
        val organization: String?,
        @SerializedName("department")
        val department: String?,
        @SerializedName("tg_id") val tgId: String?,
        @SerializedName("tg_name") val tgName: String?,
        @SerializedName("photo") val photo: String?,
        @SerializedName("hired_at") val hiredAt: String?,
        @SerializedName("surname") val surname: String?,
        @SerializedName("first_name") val firstname: String?,
        @SerializedName("middle_name") val middlename: String?,
        @SerializedName("nickname") val nickname: String?,
        @SerializedName("job_title") val jobTitle: String?
    ): Parcelable


}

