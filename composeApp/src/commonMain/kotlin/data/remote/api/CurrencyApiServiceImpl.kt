package data.remote.api

import domain.CurrencyApiService
import domain.PreferencesRepository
import domain.model.ApiResponse
import domain.model.Currency
import domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import util.APIKEY

class CurrencyApiServiceImpl(
    private val preferences: PreferencesRepository
) : CurrencyApiService {
    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = APIKEY
        const val FLAG_URL_TEMPLATE = "https://flagsapi.com/{countryCode}/flat/64.png"

        private val currencyToCountryCodeMap = mapOf(
            "USD" to "US", "EUR" to "EU", "GBP" to "GB", "INR" to "IN", "JPY" to "JP",
            "AUD" to "AU", "CAD" to "CA", "AED" to "AE", "CHF" to "CH", "CNY" to "CN",
            "NZD" to "NZ", "SGD" to "SG", "HKD" to "HK", "SEK" to "SE", "KRW" to "KR",
            "NOK" to "NO", "DKK" to "DK", "ZAR" to "ZA", "BRL" to "BR", "RUB" to "RU",
            "MXN" to "MX", "MYR" to "MY", "THB" to "TH", "IDR" to "ID", "PHP" to "PH",
            "TRY" to "TR", "PLN" to "PL", "HUF" to "HU", "CZK" to "CZ", "ILS" to "IL",
            "SAR" to "SA", "QAR" to "QA", "KWD" to "KW", "BHD" to "BH", "OMR" to "OM",
            "VND" to "VN", "EGP" to "EG", "ARS" to "AR", "CLP" to "CL", "COP" to "CO",
            "PEN" to "PE", "PKR" to "PK", "BDT" to "BD", "LKR" to "LK", "NGN" to "NG",
            "GHS" to "GH", "KES" to "KE", "UGX" to "UG", "TZS" to "TZ", "MAD" to "MA",
            "DZD" to "DZ", "TND" to "TN", "XAF" to "CM", "XOF" to "SN", "XCD" to "AG",
            "BBD" to "BB", "BMD" to "BM", "BSD" to "BS", "KYD" to "KY", "TTD" to "TT",
            "JMD" to "JM", "BZD" to "BZ", "FJD" to "FJ", "PGK" to "PG", "WST" to "WS",
            "TOP" to "TO", "MVR" to "MV", "MNT" to "MN", "MMK" to "MM", "KHR" to "KH",
            "LAK" to "LA", "KZT" to "KZ", "UZS" to "UZ", "GEL" to "GE", "AMD" to "AM",
            "AZN" to "AZ", "MOP" to "MO", "TWD" to "TW", "LBP" to "LB", "JOD" to "JO",
            "SYP" to "SY", "IQD" to "IQ", "AFN" to "AF", "YER" to "YE", "SDG" to "SD",
            "SOS" to "SO", "ETB" to "ET", "MGA" to "MG", "ZMW" to "ZM", "BWP" to "BW"
        )

        private fun getCountryName(countryCode: String): String {
            val countryNames = mapOf(
                "US" to "United States", "EU" to "European Union", "GB" to "United Kingdom",
                "IN" to "India", "JP" to "Japan", "AU" to "Australia", "CA" to "Canada",
                "AE" to "United Arab Emirates", "CH" to "Switzerland", "CN" to "China",
                "NZ" to "New Zealand", "SG" to "Singapore", "HK" to "Hong Kong",
                "SE" to "Sweden", "KR" to "South Korea", "NO" to "Norway", "DK" to "Denmark",
                "ZA" to "South Africa", "BR" to "Brazil", "RU" to "Russia", "MX" to "Mexico",
                "MY" to "Malaysia", "TH" to "Thailand", "ID" to "Indonesia", "PH" to "Philippines",
                "TR" to "Turkey", "PL" to "Poland", "HU" to "Hungary", "CZ" to "Czech Republic",
                "IL" to "Israel", "SA" to "Saudi Arabia", "QA" to "Qatar", "KW" to "Kuwait",
                "BHD" to "Bahrain", "OMR" to "Oman", "VND" to "Vietnam", "EG" to "Egypt",
                "AR" to "Argentina", "CL" to "Chile", "CO" to "Colombia", "PE" to "Peru",
                "PK" to "Pakistan", "BD" to "Bangladesh", "LK" to "Sri Lanka", "NG" to "Nigeria",
                "GH" to "Ghana", "KE" to "Kenya", "UG" to "Uganda", "TZ" to "Tanzania",
                "MA" to "Morocco", "DZ" to "Algeria", "TN" to "Tunisia", "CM" to "Cameroon",
                "SN" to "Senegal", "AG" to "Antigua and Barbuda", "BB" to "Barbados",
                "BM" to "Bermuda", "BS" to "Bahamas", "KY" to "Cayman Islands",
                "TT" to "Trinidad and Tobago", "JM" to "Jamaica", "BZ" to "Belize",
                "FJ" to "Fiji", "PG" to "Papua New Guinea", "WS" to "Samoa", "TO" to "Tonga",
                "MV" to "Maldives", "MN" to "Mongolia", "MM" to "Myanmar", "KH" to "Cambodia",
                "LA" to "Laos", "KZ" to "Kazakhstan", "UZ" to "Uzbekistan", "GE" to "Georgia",
                "AM" to "Armenia", "AZ" to "Azerbaijan", "MO" to "Macau", "TW" to "Taiwan",
                "LB" to "Lebanon", "JO" to "Jordan", "SY" to "Syria", "IQ" to "Iraq",
                "AF" to "Afghanistan", "YE" to "Yemen", "SD" to "Sudan", "SO" to "Somalia",
                "ET" to "Ethiopia", "MG" to "Madagascar", "ZM" to "Zambia", "BW" to "Botswana"
            )
            return countryNames[countryCode] ?: "Unknown"
        }
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(DefaultRequest) {
            headers {
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            if (response.status.value == 200) {
                println("API RESPONSE :${response.body<String>()}")
                val apiResponse = Json.decodeFromString<ApiResponse>(response.body())
//                val availableCurrencyCodes = apiResponse.data.keys.filter {
//                    CurrencyCode.entries
//                        .map { code -> code.name }
//                        .toSet()
//                        .contains(it)
//                }
//                val availableCurrencies = apiResponse.data.values
//                    .filter { currency -> availableCurrencyCodes.contains(currency.code) }
                val availableCurrencies = apiResponse.data.values.map { currency ->
                    val countryCode = currencyToCountryCodeMap[currency.code]
//                    countryCode?.let {
//                        val flagUrl = FLAG_URL_TEMPLATE.replace("{countryCode}", it)
//                        currency.copy(country = getCountryName(it), flagUrl = flagUrl)
//                    }
                    val countryName = countryCode?.let { getCountryName(it) } ?: "Unknown"
                    val flagUrlApi = countryCode?.let { FLAG_URL_TEMPLATE.replace("{countryCode}", it) }
                        ?: "" //UNKNOWN FLAG
                    println("FLAG URL $flagUrlApi")
                    Currency().apply {
                        _id = currency._id
                        code = currency.code
                        value = currency.value
                        country = countryName
                        flagUrl = flagUrlApi
                    }

                }

                //persist a timestamp
                val lastUpdated = apiResponse.meta.lastUpdatedAt
                println("last updated $lastUpdated")
                preferences.saveLastUpdated(lastUpdated)
                RequestState.Success(data = availableCurrencies)
            } else {
                RequestState.Error(message = "HTTP Error Code :${response.status}")
            }
        } catch (e: IOException) {
            RequestState.Error(message = "Network Error: ${e.message}")
        } catch (e: SerializationException) {
            RequestState.Error(message = "Serialization Error: ${e.message}")
        } catch (e: Exception) {
            RequestState.Error(message = "Unexpected Error: ${e.message}")
        }
    }
}