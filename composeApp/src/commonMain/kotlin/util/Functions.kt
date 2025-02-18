package util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import currencyrateapp.composeapp.generated.resources.Res
import currencyrateapp.composeapp.generated.resources.bebas_neue_regular
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.Font

fun displayCurrentDateTime(): String {
    val currentTimestamp = Clock.System.now()
    val date = currentTimestamp.toLocalDateTime(TimeZone.currentSystemDefault())

    val dayOfMonth = date.dayOfMonth
    val month = date.month.toString().lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.lowercase() else it.toString() }
    val year = date.year
    val suffix = when{
        dayOfMonth in 11..13 -> "th"
        dayOfMonth % 10 == 1-> "st"
        dayOfMonth % 10 == 2-> "nd"
        dayOfMonth % 10 == 3-> "rd"
        else -> "th"
    }
    return "$dayOfMonth$suffix $month, $year."
}

fun calculateExchangeRates(source:Double,target:Double):Double{
    return target/source
}
fun convert(amount:Double,exchangeRates:Double):Double{
    return amount*exchangeRates
}

@Composable
fun GetBebasFontFamily() = FontFamily(Font(Res.font.bebas_neue_regular))
