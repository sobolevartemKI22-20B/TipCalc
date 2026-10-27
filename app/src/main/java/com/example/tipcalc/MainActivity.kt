package com.example.tipcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TipCalculatorScreen()
            }
        }
    }
}

@Composable
fun TipCalculatorScreen() {
    var amountText by remember { mutableStateOf("1000") }
    var dishesText by remember { mutableStateOf("4") }
    var tipPercent by remember { mutableIntStateOf(10) }
    var showTotal by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val dishes = dishesText.toIntOrNull() ?: 0

    val tip = calculateTip(amount, tipPercent)
    val discount = calculateDiscount(amount, dishes)
    val total = calculateTotal(amount, tip, discount)
    val discountOptions = listOf(
        Triple("1–2 блюда — 3%", 3, dishes in 1..2),
        Triple("3–5 блюд — 5%", 5, dishes in 3..5),
        Triple("6–10 блюд — 7%", 7, dishes in 6..10),
        Triple("Более 10 блюд — 10%", 10, dishes > 10)
    )
    discountOptions.forEach { (label, percent, selected) ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected,
                onClick = null
            )
            Text(label)
        }
    }

    val discountPercent = when {
        dishes <= 0 -> 0
        dishes <= 2 -> 3
        dishes <= 5 -> 5
        dishes <= 10 -> 7
        else -> 10
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Калькулятор чаевых",
                fontSize = 24.sp
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    showTotal = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                label = { Text("Сумма заказа") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = dishesText,
                onValueChange = {
                    dishesText = it
                    showTotal = false
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = { Text("Количество блюд") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Чаевые: $tipPercent%")

            Slider(
                value = tipPercent.toFloat(),
                onValueChange = {
                    tipPercent = it.toInt()
                    showTotal = false
                },
                valueRange = 0f..25f,
                steps = 4,
                onValueChangeFinished = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Чаевые: %.2f".format(
                                calculateTip(
                                    amountText.toDoubleOrNull() ?: 0.0,
                                    tipPercent
                                )
                            )
                        )
                    }
                }

            )

            Text("Скидка за количество блюд")

            listOf(
                "1–2 блюда — 3%",
                "3–5 блюд — 5%",
                "6–10 блюд — 7%",
                "Более 10 блюд — 10%"
            ).forEach { label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = false,
                        onClick = null
                    )
                    Text(label)
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Скидка") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Итого")
            }
        }
    }
}

fun calculateTip(amount: Double, percent: Int): Double {
    return amount * percent / 100.0
}

fun calculateDiscount(amount: Double, dishes: Int): Double {
    val discountPercent = when {
        dishes <= 0 -> 0
        dishes <= 2 -> 3
        dishes <= 5 -> 5
        dishes <= 10 -> 7
        else -> 10
    }

    return amount * discountPercent / 100.0
}

fun calculateTotal(
    amount: Double,
    tip: Double,
    discount: Double
): Double {
    return amount + tip - discount
}

@Preview(showSystemUi = true)
@Composable
fun TipCalculatorPreview() {
    MaterialTheme {
        TipCalculatorScreen()
    }
}