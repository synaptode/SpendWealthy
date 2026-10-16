package com.spendwealthy.app.domain.service

import com.spendwealthy.app.domain.model.Money
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class ConsensusRequest(
    val id: String,
    val requesterName: String,
    val amount: Money,
    val purpose: String,
    val impactReport: ImpactReport,
    val approvalsRequired: Int,
    val currentApprovals: Int,
    val status: ConsensusStatus,
    val createdAt: Instant
)

enum class ConsensusStatus { PENDING, APPROVED, REJECTED }

class FamilyConsensusService {
    private val _requests = mutableListOf<ConsensusRequest>()
    val requests: List<ConsensusRequest> get() = _requests

    fun createRequest(
        requesterName: String,
        amount: Money,
        purpose: String,
        impact: ImpactReport,
        requiredApprovals: Int
    ): ConsensusRequest {
        val request = ConsensusRequest(
            id = "req_${Clock.System.now().epochSeconds}",
            requesterName = requesterName,
            amount = amount,
            purpose = purpose,
            impactReport = impact,
            approvalsRequired = requiredApprovals,
            currentApprovals = 0,
            status = ConsensusStatus.PENDING,
            createdAt = Clock.System.now()
        )
        _requests.add(0, request)
        return request
    }

    fun castVote(requestId: String, isApproved: Boolean): ConsensusRequest? {
        val index = _requests.indexOfFirst { it.id == requestId }
        if (index == -1) return null
        
        val request = _requests[index]
        if (request.status != ConsensusStatus.PENDING) return request
        
        val newApprovals = if (isApproved) request.currentApprovals + 1 else request.currentApprovals
        val newStatus = when {
            !isApproved -> ConsensusStatus.REJECTED
            newApprovals >= request.approvalsRequired -> ConsensusStatus.APPROVED
            else -> ConsensusStatus.PENDING
        }
        
        val updated = request.copy(currentApprovals = newApprovals, status = newStatus)
        _requests[index] = updated
        return updated
    }
}
