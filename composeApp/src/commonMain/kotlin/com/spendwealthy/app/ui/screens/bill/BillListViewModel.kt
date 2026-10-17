package com.spendwealthy.app.ui.screens.bill

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.spendwealthy.app.core.architecture.UiState
import com.spendwealthy.app.domain.model.BillReminder
import com.spendwealthy.app.domain.model.BillStatus
import com.spendwealthy.app.domain.model.Wallet
import com.spendwealthy.app.domain.repository.BillRepository
import com.spendwealthy.app.domain.repository.WalletRepository
import com.spendwealthy.app.domain.usecase.MarkBillPaidUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class BillListState(
    val bills: List<BillReminder> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false
) : UiState

class BillListViewModel(
    private val billRepository: BillRepository,
    private val walletRepository: WalletRepository,
    private val markBillPaidUseCase: MarkBillPaidUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(BillListState())
    val uiState: StateFlow<BillListState> = _uiState.asStateFlow()

    init {
        loadBills()
    }

    private fun loadBills() {
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            combine(
                billRepository.getBillReminders(),
                walletRepository.getWallets()
            ) { bills, wallets -> bills to wallets }.collectLatest { (bills, wallets) ->
                _uiState.value = _uiState.value.copy(
                    bills = bills.sortedWith(compareBy<BillReminder> { statusPriority(statusOf(it)) }.thenBy { it.dueDate }),
                    wallets = wallets,
                    isLoading = false
                )
            }
        }
    }

    fun markPaid(billId: String, walletId: String? = null) {
        val targetWalletId = walletId ?: _uiState.value.wallets.firstOrNull()?.id
        if (targetWalletId == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Tambahkan dompet terlebih dahulu untuk membayar tagihan.")
            return
        }
        screenModelScope.launch {
            try {
                markBillPaidUseCase(billId, targetWalletId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Gagal menandai tagihan.")
            }
        }
    }

    fun statusOf(bill: BillReminder): BillStatus {
        if (!bill.isEnabled) return BillStatus.PAID
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return when {
            bill.dueDate < today -> BillStatus.OVERDUE
            bill.dueDate <= today.plus(DatePeriod(days = 3)) -> BillStatus.DUE_SOON
            else -> BillStatus.UPCOMING
        }
    }

    private fun statusPriority(status: BillStatus): Int {
        return when (status) {
            BillStatus.OVERDUE -> 0
            BillStatus.DUE_SOON -> 1
            BillStatus.UPCOMING -> 2
            BillStatus.PAID -> 3
            BillStatus.SKIPPED -> 4
        }
    }
}
