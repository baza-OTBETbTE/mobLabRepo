package com.example.tipcalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat

data class CalculatorState(
    val orderAmount: String = "",
    val dishesCount: String = "",
    val tipPercent: Int = 10,
    val discountPercent: Int = 3
)

class CalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state

    fun onOrderAmountChanged(amount: String) {
        _state.update { it.copy(orderAmount = amount) }
    }

    fun onDishesCountChanged(count: String) {
        val discount = calculateDiscount(count)
        _state.update { it.copy(dishesCount = count, discountPercent = discount) }
    }

    fun onTipPercentChanged(percent: Int) {
        _state.update { it.copy(tipPercent = percent) }
    }

    private fun calculateDiscount(countStr: String): Int {
        val count = countStr.toIntOrNull()
        return when {
            count == null -> 3
            count in 1..2 -> 3
            count in 3..5 -> 5
            count in 6..10 -> 7
            count > 10 -> 10
            else -> 3
        }
    }

    val totalToPay: String
        get() {
            val amount = _state.value.orderAmount.toDoubleOrNull()
            val dishes = _state.value.dishesCount.toIntOrNull()
            if (amount == null || dishes == null || amount <= 0 || dishes <= 0)
                return "—"

            val tip = _state.value.tipPercent
            val discount = _state.value.discountPercent

            // Сумма скидки
            val discountAmount = amount * discount / 100.0
            val discounted = amount - discountAmount
            val tipAmount = discounted * tip / 100.0
            val total = discounted + tipAmount

            val formatter = DecimalFormat("#.##")
            return formatter.format(total)
        }

}