package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tipcalculator.ui.theme.TipCalculatorTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TipCalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipCalculatorPreview() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        TipCalculatorScreen(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun TipSlider(
    sliderPosition: Float,
    onPositionChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    state: CalculatorState,
    viewModel: CalculatorViewModel
) {

    Row(modifier) {
        Text(text = "Чаевые:")
    }
    Row(modifier) {
        Slider(
            valueRange = 0f..25f,
            value = state.tipPercent.toFloat(),
            onValueChange = { viewModel.onTipPercentChanged(it.toInt()) },
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF3949AB),
                activeTrackColor = Color(0xFF3F51B5),
                inactiveTrackColor = Color(0xFFE8EAF3),
                inactiveTickColor = Color(0xFF3949AB),
                activeTickColor = Color(0xFF3949AB)
            )
        )
    }
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "0", style = MaterialTheme.typography.headlineSmall)
        Text(text = "25", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun DiscountRadioGroup(selectedDiscount: Int) {
    val discountOptions = listOf(3, 5, 7, 10)
    Text(
        text = "Скидка:",
        style = MaterialTheme.typography.headlineSmall
    )
    discountOptions.forEach { discount ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RadioButton(
                selected = selectedDiscount == discount,
                onClick = null
            )
            Text("$discount%", fontSize = 16.sp, modifier = Modifier.padding(4.dp))
        }
    }
}

@Composable
fun TipCalculatorScreen(
    viewModel: CalculatorViewModel = viewModel(),
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    var sliderPosition by remember { mutableFloatStateOf(20f) }
    val handlePositionChange = { position: Float ->
        sliderPosition = position
    }
    Column(
        modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Сумма заказа:",
            )
            TextField(
                value = state.orderAmount,
                onValueChange = viewModel::onOrderAmountChanged,
                singleLine = true,
                modifier = Modifier
                    .width(200.dp)
                    .padding(start = 30.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(
            modifier = Modifier.padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Количество блюд:"
            )
            TextField(
                value = state.dishesCount,
                onValueChange = viewModel::onDishesCountChanged,
                singleLine = true,
                modifier = Modifier
                    .width(75.dp)
                    .padding(start = 15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        TipSlider(
            sliderPosition = sliderPosition,
            onPositionChange = handlePositionChange,
            modifier = Modifier.padding(horizontal = 10.dp),
            state = state,
            viewModel = viewModel
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            DiscountRadioGroup(
                selectedDiscount = state.discountPercent
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 30.dp)
        ) {
            Text(
                text = "Итоговая сумма к оплате: ",
            )
            Text(
                text = viewModel.totalToPay,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}