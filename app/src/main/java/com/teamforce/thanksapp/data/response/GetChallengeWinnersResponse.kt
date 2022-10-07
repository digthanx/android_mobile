package com.teamforce.thanksapp.data.response

data class GetChallengeWinnersResponse(
    val data: List<Winner>
){
    data class Winner(
        val participant_id: Int,
        val participant_photo: String?,
        val awarded_at: String,
        val participant_name: String?,
        val participant_surname: String?,
        val nickname: String?,
        val total_received: Int?,

    )
}
