package presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.CurrencyApiService
import domain.PreferencesRepository
import domain.model.RateStatus
import domain.model.RequestState
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    init {
        screenModelScope.launch {
            fetchNewRates()
        }
    }

    fun sendEvent(event: HomeUiEvent){
        when(event){
            HomeUiEvent.RefreshRates ->{
                screenModelScope.launch{
                    fetchNewRates()
                }
            }
        }
    }

    private suspend fun fetchNewRates() {
        _rateStatus.value = RateStatus.Idle
//        try {
//            api.getLatestExchangeRates()
//            getRateStatus()
//        } catch (e: Exception) {
//            println(e.message)
//        }
        try {
            val result = api.getLatestExchangeRates()
            if (result is RequestState.Success) {
                // Data is fresh if the API call succeeded
                _rateStatus.value = RateStatus.Fresh
            } else if (result is RequestState.Error){
                _rateStatus.value = RateStatus.Stale
            }
        } catch (e: Exception) {
            println(e.message)
            _rateStatus.value = RateStatus.Stale
        }
    }

//    private suspend fun getRateStatus() {
//        _rateStatus.value = if (preferences.isDataFresh(
//                currentTimestamp = Clock.System.now().toEpochMilliseconds()
//            )
//        ) RateStatus.Fresh
//        else RateStatus.Stale
//    }
}