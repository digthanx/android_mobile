package com.teamforce.thanksapp.data.response

data class AuthResponse(
    val status: String,
    val organizations: List<Organization>?
){
    data class Organization(
        val user_id: Int,
        val organization_id: Int,
        val organization_name: String,
        val organization_photo: String?
    )
}
