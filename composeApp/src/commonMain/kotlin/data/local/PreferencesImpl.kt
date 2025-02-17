package data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import domain.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class PreferencesImpl(settings: Settings) : PreferencesRepository {
    companion object {
        const val TIMESTAMP_KEY = "lastUpdated"
        const val SOURCE_CURRENT_KEY = "sourceCurrency"
        const val TARGET_CURRENT_KEY = "targetCurrency"
    }

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()
    override suspend fun saveLastUpdated(lastUpdated: String) {
//        flowSettings.putLong(
//            key = TIMESTAMP_KEY,
//            value = Instant.parse(lastUpdated).toEpochMilliseconds()
//        )
        try {
            val timestampMillis = Instant.parse(lastUpdated).toEpochMilliseconds()
            flowSettings.putLong(key = TIMESTAMP_KEY, value = timestampMillis)
        } catch (e: Exception) {
            println("Error parsing timestamp: ${e.message}")
        }
    }

    override suspend fun isDataFresh(currentTimestamp: Long): Boolean {
        val savedTimeStamp = flowSettings.getLong(key = TIMESTAMP_KEY, defaultValue = 0L)
        if (savedTimeStamp == 0L) return false // No previous data
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000 // 24 hours

        // Ensure data is both from the same calendar day and within the last 24 hours
        val isWithin24Hours = (currentTimestamp - savedTimeStamp) < twentyFourHoursInMillis
        val savedDate = Instant.fromEpochMilliseconds(savedTimeStamp)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val currentDate = Instant.fromEpochMilliseconds(currentTimestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date
        val isSameDay = savedDate == currentDate

        return isSameDay && isWithin24Hours
    }

    override suspend fun saveSourceCurrencyCode(code: String) {
        flowSettings.putString(key = SOURCE_CURRENT_KEY, value = code)
    }

    override suspend fun saveTargetCurrencyCode(code: String) {
        flowSettings.putString(key = TARGET_CURRENT_KEY, value = code)
    }

    override fun readSourceCurrencyCode(): Flow<String> {
        return flowSettings.getStringFlow(
            key = SOURCE_CURRENT_KEY,
            defaultValue = "USD"
        )
    }

    override fun readTargetCurrencyCode(): Flow<String> {
        return flowSettings.getStringFlow(
            key = TARGET_CURRENT_KEY,
            defaultValue = "EUR"
        )
    }
}