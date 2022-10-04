package com.teamforce.thanksapp.domain.models.profile

data class ProfileModel(
    val username: String,
    val profile: ProfileBeanModel
)

data class ProfileBeanModel(
    val id: String,
    val contacts: List<ContactModel>,
    val organization: String?,
    val department: String?,
    val tgId: String?,
    val tgName: String?,
    val photo: String?,
    val hiredAt: String?,
    val surname: String?,
    val firstname: String?,
    val middleName: String?,
    val nickname: String?,
    val jobTitle: String?
)

data class ContactModel(
    val id: Int?,
    val contactType: String,
    var contactId: String
)