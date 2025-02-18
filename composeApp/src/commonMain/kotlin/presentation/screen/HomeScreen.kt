package presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.example.compose.surfaceColor
import domain.model.Currency
import presentation.component.CurrencyPickerDialog
import presentation.component.HomeBody
import presentation.component.HomeHeader

enum class CurrencyType { SOURCE, TARGET }

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<HomeViewModel>()
        val rateStatus by viewModel.rateStatus
        val allCurrencies = viewModel.allCurrency
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency
        var amount by rememberSaveable { mutableStateOf(0.0) }
        var dialogOpenedFor: CurrencyType? by remember { mutableStateOf(null) }
        var selectedCurrency: Currency? by remember { mutableStateOf(null) }

        if (dialogOpenedFor != null) {
            CurrencyPickerDialog(
                currencies = allCurrencies,
                selectedCurrency = selectedCurrency
                    ?: if (dialogOpenedFor == CurrencyType.SOURCE) sourceCurrency.getSuccessData()
                        ?: Currency() else targetCurrency.getSuccessData() ?: Currency(),
                onPositiveClick = { currency ->
                    selectedCurrency = currency
                    when (dialogOpenedFor) {
                        CurrencyType.SOURCE -> {
                            viewModel.sendEvent(HomeUiEvent.SaveSourceCurrencyCode(currency.code))
                        }
                        CurrencyType.TARGET -> {
                            viewModel.sendEvent(HomeUiEvent.SaveTargetCurrencyCode(currency.code))
                        }
                        null -> {}
                    }
                    dialogOpenedFor = null
                },
                onDismiss = {
                    dialogOpenedFor = null
                    selectedCurrency = null
                }
            )
        }

        Column(modifier = Modifier.fillMaxSize().background(surfaceColor)) {
            HomeHeader(status = rateStatus,
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount,
                onRatesRefresh = {
                    viewModel.sendEvent(HomeUiEvent.RefreshRates)
                },
                onSwitchClick = {
                    viewModel.sendEvent(HomeUiEvent.SwitchCurrencies)
                },
                onAmountChange = {
                    amount = it
                },
                onCurrencySelected = { type, currency ->
                    selectedCurrency = currency
                    dialogOpenedFor = type
                }
            )
            HomeBody(sourceCurrency,targetCurrency,amount)
        }
    }
}