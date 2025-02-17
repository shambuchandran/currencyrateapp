package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.MongoRepository
import domain.PreferencesRepository
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val api: CurrencyApiService,
    private val mongoDb: MongoRepository
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrency :List<Currency> = _allCurrencies

    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    init {
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                    readSourceCurrency()
                    readTargetCurrency()
                }
            }
        }
    }

    fun readSourceCurrency(){
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode }
                if (selectedCurrency != null){
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency)
                }else{
                    _sourceCurrency.value = RequestState.Error(message = "unable find")
                }
            }
        }
    }
    fun readTargetCurrency(){
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode }
                if (selectedCurrency != null){
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency)
                }else{
                    _targetCurrency.value = RequestState.Error(message = "unable find")
                }
            }
        }
    }

    private suspend fun fetchNewRates() {
        _rateStatus.value = RateStatus.Idle
        try {
            val localCache = mongoDb.readCurrencyData().first()
            if (localCache.isSuccess() && localCache.getSuccessData().isNotEmpty()) {
                _allCurrencies.addAll(localCache.getSuccessData())
                _rateStatus.value = RateStatus.Fresh
                printCurrencyData(_allCurrencies)
                return
            }
            val currentTimeMillis = Clock.System.now().toEpochMilliseconds()
            if (preferences.isDataFresh(currentTimeMillis)) {
                _rateStatus.value = RateStatus.Fresh
                return
            }
            val result = api.getLatestExchangeRates()
            if (result.isSuccess()) {
                mongoDb.cleanUp()
                result.getSuccessData().forEach {
                    mongoDb.insertCurrencyData(it)
                }
                _allCurrencies.addAll(result.getSuccessData())
                _rateStatus.value = RateStatus.Fresh
            } else {
                _rateStatus.value = RateStatus.Stale
            }
        } catch (e: Exception) {
            println(e.message)
            _rateStatus.value = RateStatus.Stale
        }
    }
    private fun printCurrencyData(currencies: List<Currency>) {
        println("Currencies in Realm:")
        currencies.forEach { currency ->
            println("Code: ${currency.code}, Value: ${currency.value}, Country: ${currency.country}, Flag URL: ${currency.flagUrl}")
        }
    }
}