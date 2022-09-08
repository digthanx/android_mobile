package com.teamforce.thanksapp.model.domain

data class TagModel(
    val id: Int,
    val name: String,
    var enabled: Boolean = false
)
