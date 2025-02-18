package presentation.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.headerColor
import com.example.compose.staleColor
import currencyrateapp.composeapp.generated.resources.Res
import currencyrateapp.composeapp.generated.resources.currency
import currencyrateapp.composeapp.generated.resources.refresh
import currencyrateapp.composeapp.generated.resources.swap
import domain.model.Currency
import domain.model.RateStatus
import domain.model.RequestState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import presentation.screen.CurrencyType
import util.displayCurrentDateTime


@Composable
fun HomeHeader(
    status: RateStatus,
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    amount: Double,
    onSwitchClick: () -> Unit,
    onRatesRefresh: () -> Unit,
    onAmountChange: (Double) -> Unit,
    onCurrencySelected: (CurrencyType, Currency) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(headerColor)
            .padding(all = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        RateStatus(status = status, onRatesRefresh = onRatesRefresh)
        Spacer(modifier = Modifier.height(24.dp))
        CurrencyInputs(source, target, onSwitchClick, onCurrencySelected)
        Spacer(modifier = Modifier.height(24.dp))
        AmountInput(amount, onAmountChange)
    }
}

@Composable
fun RateStatus(status: RateStatus, onRatesRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(Res.drawable.currency),
                contentDescription = "Exchange",
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = displayCurrentDateTime(), color = Color.White)
                Text(
                    text = status.title,
                    color = status.color,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        }
        if (status == RateStatus.Stale) {
            IconButton(onClick = onRatesRefresh) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.refresh),
                    contentDescription = "refresh",
                    tint = staleColor
                )
            }
        }
    }
}

@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick: () -> Unit,
    onCurrencySelected: (CurrencyType, Currency) -> Unit
) {
    var animationStarted by remember { mutableStateOf(false) }
    val animationRotation by animateFloatAsState(
        targetValue = if (animationStarted) 180f else 0f,
        animationSpec = tween(500)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyView(
            placeholder = "from",
            currency = source,
            onCLick = {
                if (source.isSuccess()) {
                    onCurrencySelected(CurrencyType.SOURCE, source.getSuccessData())
                }
            }
        )
        Spacer(modifier = Modifier.height(14.dp))
        IconButton(
            modifier = Modifier.padding(top = 24.dp).graphicsLayer {
                rotationY = animationRotation
            },
            onClick = {
                animationStarted = !animationStarted
                onSwitchClick()
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.swap),
                contentDescription = "Switch",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        CurrencyView(
            placeholder = "to",
            currency = target,
            onCLick = {
                if (target.isSuccess()) {
                    onCurrencySelected(
                        CurrencyType.TARGET,
                        target.getSuccessData()
                    ) // Pass the Currency object
                }
            }
        )
    }

}

@Composable
fun RowScope.CurrencyView(
    placeholder: String,
    currency: RequestState<Currency>,
    onCLick: () -> Unit
) {

    Column(modifier = Modifier.weight(1f)) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = placeholder,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(size = 8.dp))
                .background(Color.White.copy(0.5f))
                .height(54.dp)
                .clickable { onCLick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (currency.isSuccess()) {
                val currencyData = currency.getSuccessData()
                println("CURRENCY DATA :$currencyData")
                val painter = currencyData.flagUrl?.let { asyncPainterResource(it) }
                println("CURRENCY DATA FLAG :$painter")
                if (painter != null) {
                    KamelImage(
                        { painter },
                        contentDescription = "country flag",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currencyData.code,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = Color.White
                )
            } else {
                Text(
                    text = "Loading...",
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AmountInput(
    amount: Double,
    onAmountChange: (Double) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(size = 8.dp))
            .animateContentSize()
            .height(54.dp),
        value = amount.toString(),
        onValueChange = {
            it.toDoubleOrNull()?.let { newAmount ->
                onAmountChange(newAmount)
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(0.05f),
            unfocusedContainerColor = Color.White.copy(0.05f),
            disabledContainerColor = Color.White.copy(0.05f),
            errorContainerColor = Color.White.copy(0.05f),
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.White
        ),
        textStyle = TextStyle(
            color = Color.White,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )

}