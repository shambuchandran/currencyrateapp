package presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.surfaceColor
import com.example.compose.textColor
import domain.model.Currency

@Composable
fun CurrencyPickerDialog(
    currencies: List<Currency>,
    selectedCurrency: Currency,
    onPositiveClick: (Currency) -> Unit,
    onDismiss: () -> Unit
) {

    var searchQuery by remember(Currency) { mutableStateOf("") }
    var displayedCurrencies by remember { mutableStateOf(currencies) }
    var currentlySelectedCurrency by remember { mutableStateOf(selectedCurrency) }

    AlertDialog(
        containerColor = surfaceColor,
        title = {
            Text(text = "Select a currency", color = textColor)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(size = 99.dp)),
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query.uppercase()
                        displayedCurrencies = if (query.isNotEmpty()) {
                            currencies.filter {
                                it.code.contains(query.uppercase()) || it.country?.contains(
                                    query,
                                    true
                                ) == true
                            } // Filter based on code OR country
                        } else {
                            currencies
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search here",
                            color = textColor.copy(0.38f),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = textColor,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textColor.copy(0.1f),
                        unfocusedContainerColor = textColor.copy(0.1f),
                        disabledContainerColor = textColor.copy(0.1f),
                        errorContainerColor = textColor.copy(0.1f),
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                AnimatedContent(
                    targetState = displayedCurrencies,
                ) { availableCurrencies ->
                    if (availableCurrencies.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = displayedCurrencies,
                                key = { it._id.toHexString() }) { currency ->
                                CurrencyCodePickerView(
                                    code = currency,
                                    selectedCurrency = currentlySelectedCurrency,
                                    onSelect = { currentlySelectedCurrency = currency }
                                )
                            }
                        }

                    } else {
                        ErrorScreen(modifier = Modifier.height(250.dp))
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = MaterialTheme.colorScheme.outline)
            }
        },
        confirmButton = {
            TextButton(onClick = { onPositiveClick(currentlySelectedCurrency) }) {
                Text(text = "Confirm", color = MaterialTheme.colorScheme.outline)
            }
        }

    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, message: String? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message ?: "No data", textAlign = TextAlign.Center)
    }
}