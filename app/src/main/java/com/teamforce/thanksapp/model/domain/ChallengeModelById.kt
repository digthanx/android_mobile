package com.teamforce.thanksapp.model.domain

import java.io.FileDescriptor

data class ChallengeModelById(
    val id: Int?,
    val name: String?,
    val photo: String?,
    val updated_at: String?,
    val states: List<String?>?,
    val description: String,
    val creator_id: Int?,
    val parameters: List<ParameterModel>?,
    val end_at: String,
    val approved_reports_amount: Int?,
    val status: String?,
    val is_new_reports: Boolean?,
    val winners_count: Int,
    val awardees: Int,
    val creator_organization_id: Int,
    val fund: Int,
    val creator_name: String?,
    val creator_surname: String?,
    val creator_photo: String?,
    val creator_tg_name: String,
    val active: Boolean,
    val completed: Boolean,
    val remaining_top_places: Int
)
