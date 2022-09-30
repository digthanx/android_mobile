package com.teamforce.thanksapp.model.domain

data class ChallengeModel(
    val id: Int?,
    val name: String?,
    val photo: String?,
    val update_at: String?,
    val states: List<String?>?,
    val start_balance: Int?,
    val creator_id: Int?,
    val parameters: List<ParameterModel>?,
    val approved_reports_amount: Int?,
    val status: String?,
    val is_new_reports: Boolean?,
    val prize_size: Int?,
    val awardees: Int?
)
