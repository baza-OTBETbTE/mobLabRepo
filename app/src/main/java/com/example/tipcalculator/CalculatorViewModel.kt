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


}