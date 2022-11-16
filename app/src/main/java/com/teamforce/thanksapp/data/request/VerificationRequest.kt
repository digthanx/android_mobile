package com.teamforce.thanksapp.data.request

data class VerificationRequest(
    val type: String = "authcode",
    var code: String,
    val organization_id: Int?
)
