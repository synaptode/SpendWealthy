package com.spendwealthy.app.data.repository

import com.spendwealthy.app.domain.model.Family
import com.spendwealthy.app.domain.model.FamilyMember
import com.spendwealthy.app.domain.model.FamilyRole
import com.spendwealthy.app.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class MemoryFamilyRepository : FamilyRepository {
    private val _family = MutableStateFlow<Family?>(null)

    init {
        // Initial mock data
        _family.value = Family(
            id = "fam_1",
            name = "The Wealthy Family",
            members = listOf(
                FamilyMember("1", "Rakha", "rakha@wealthy.com", FamilyRole.ADMIN, null),
                FamilyMember("2", "Sarah", "sarah@wealthy.com", FamilyRole.MEMBER, null)
            ),
            sharedWalletIds = listOf("w1"),
            createdAt = Clock.System.now()
        )
    }

    override fun getFamily(): Flow<Family?> = _family

    override suspend fun joinFamily(familyId: String, member: FamilyMember) {
        val current = _family.value
        if (current?.id == familyId) {
            _family.value = current.copy(members = current.members + member)
        }
    }

    override suspend fun inviteMember(email: String) {
        // Mock invitation logic
    }

    override suspend fun createFamily(name: String) {
        _family.value = Family(
            id = "fam_${Clock.System.now().toEpochMilliseconds()}",
            name = name,
            members = emptyList(),
            sharedWalletIds = emptyList(),
            createdAt = Clock.System.now()
        )
    }
}
