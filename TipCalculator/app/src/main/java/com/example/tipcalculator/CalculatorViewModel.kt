package com.example.tipcalculator

data class CalculatorState(
    val orderAmount: String = "",
    val dishesCount: String = "",
    val tipPercent: Int = 15,
    val discountPercent: Int = 3
)