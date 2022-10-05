package com.teamforce.thanksapp.data.response

data class GetChallengeParticipantsResponse(
    val data: List<Participant>
){
    data class Participant(
        val participant_id: Int,
        val participant_photo: String?,
        val participant_name: String,
        val participant_surname: String,
        val report_created_at: String,
        val report_text: String,
        val report_photo: String?,
        val report_id: Int
    )
}
