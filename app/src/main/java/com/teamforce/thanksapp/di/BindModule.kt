package com.teamforce.thanksapp.di

import com.teamforce.thanksapp.data.repository.ChallengeRepositoryImpl
import com.teamforce.thanksapp.data.repository.ProfileRepositoryImpl
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.domain.repositories.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BindModule {

    @Binds
    fun bindProfileRepository(profileRepositoryImpl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    fun bindChallengeRepository(challengeRepositoryImpl: ChallengeRepositoryImpl): ChallengeRepository

}