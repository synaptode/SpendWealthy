package com.spendwealthy.app.domain.repository

import com.spendwealthy.app.domain.model.Family
import com.spendwealthy.app.domain.model.FamilyMember
import kotlinx.coroutines.flow.Flow

interface FamilyRepository {
    fun getFamily(): Flow<Family?>
    suspend fun joinFamily(familyId: String, member: FamilyMember)
    suspend fun inviteMember(email: String)
    suspend fun createFamily(name: String)
}
