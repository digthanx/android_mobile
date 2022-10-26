package com.teamforce.thanksapp.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetChallengeWinnersResponse(
    val data: List<Winner>
){
    @Parcelize
    data class Winner(
        @SerializedName("id")
        val reportId: Int,
        val photo: String?,
        val participant_id: Int,
        val participant_photo: String?,
        val awarded_at: String,
        val participant_name: String?,
        val participant_surname: String?,
        val nickname: String?,
        val award: Int?,

    ) : Parcelable
}
