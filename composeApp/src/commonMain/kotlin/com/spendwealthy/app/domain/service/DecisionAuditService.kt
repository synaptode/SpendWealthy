package com.spendwealthy.app.domain.service

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class DecisionAuditLog(
    val id: String,
    val timestamp: Instant,
    val engineName: String,
    val decision: String,
    val inputs: Map<String, String>,
    val reasoning: List<String>,
    val confidence: Double
)

class DecisionAuditService {
    private val _logs = mutableListOf<DecisionAuditLog>()
    val logs: List<DecisionAuditLog> get() = _logs

    fun logDecision(
        engineName: String,
        decision: String,
        inputs: Map<String, String>,
        reasoning: List<String>,
        confidence: Double
    ) {
        _logs.add(0, DecisionAuditLog(
            id = "audit_${Clock.System.now().epochSeconds}_${engineName}",
            timestamp = Clock.System.now(),
            engineName = engineName,
            decision = decision,
            inputs = inputs,
            reasoning = reasoning,
            confidence = confidence
        ))
        
        // Trim logs to keep memory footprint low (Principal-level memory management)
        if (_logs.size > 100) {
            _logs.removeAt(_logs.size - 1)
        }
    }

    fun getLogsForEngine(engineName: String): List<DecisionAuditLog> {
        return _logs.filter { it.engineName == engineName }
    }
}
