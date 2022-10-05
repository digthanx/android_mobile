package com.teamforce.thanksapp.domain.mappers.proflle

import com.teamforce.thanksapp.data.entities.profile.ContactEntity
import com.teamforce.thanksapp.data.entities.profile.ProfileBeanEntity
import com.teamforce.thanksapp.data.entities.profile.ProfileEntity
import com.teamforce.thanksapp.domain.models.profile.ContactModel
import com.teamforce.thanksapp.domain.models.profile.ProfileBeanModel
import com.teamforce.thanksapp.domain.models.profile.ProfileModel
import javax.inject.Inject

class ProfileMapper @Inject constructor(
    private val profileBeanMapper: ProfileBeanMapper
) {
    fun map(from: ProfileEntity): ProfileModel {
        return ProfileModel(
            username = from.username ?: "Unknown",
            profile = profileBeanMapper.map(from.profile)
        )
    }
}

class ProfileBeanMapper @Inject constructor(
    private val contactMapper: ContactMapper
) {
    fun map(from: ProfileBeanEntity): ProfileBeanModel {
        return ProfileBeanModel(
            id = from.id,
            contacts = contactMapper.mapList(from.contacts),
            organization = from.organization,
            department = from.department,
            tgId = from.tgId,
            tgName = from.tgName,
            photo = from.photo,
            hiredAt = from.hiredAt,
            surname = from.surname,
            firstname = from.firstname,
            middleName = from.middlename,
            nickname = from.nickname,
            jobTitle = from.jobTitle
        )
    }
}

class ContactMapper @Inject constructor() {
    private fun map(from: ContactEntity): ContactModel {
        return ContactModel(
            id = from.id,
            contactType = from.contact_type,
            contactId = from.contact_id
        )
    }

    fun mapList(from: List<ContactEntity>): List<ContactModel> {
        return from.map {
            map(it)
        }
    }
}