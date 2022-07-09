package com.teamforce.thanksapp.data.request

data class AuthorizationRequest(
    val type: String = "authorize",
    var login: String
)
