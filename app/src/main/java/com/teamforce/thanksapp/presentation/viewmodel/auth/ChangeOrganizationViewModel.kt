package com.teamforce.thanksapp.presentation.viewmodel.auth

import com.teamforce.thanksapp.domain.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ChangeOrganizationViewModel constructor(
    private val profileRepository: ProfileRepository
) {

}