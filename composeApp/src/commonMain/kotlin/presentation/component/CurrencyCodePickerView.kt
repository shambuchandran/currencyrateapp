package presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.primaryColor
import com.example.compose.surfaceColor
import com.example.compose.textColor
import domain.model.Currency
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun CurrencyCodePickerView(
    code: Currency,
    selectedCurrency: Currency,
    onSelect: (Currency) -> Unit
) {
    val isSelected = code.code == selectedCurrency.code
    val saturation = remember { Animatable(if (isSelected) 1f else 0f) }
    LaunchedEffect(isSelected) {
        saturation.animateTo(if (isSelected) 1f else 0f)
    }
    val colormatrix = remember(saturation.value) {
        ColorMatrix().apply {
            setToSaturation(saturation.value)
        }
    }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f,
        animationSpec = tween(300)
    )
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(size = 8.dp))
            .clickable { onSelect(code) }.padding(all = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            val painter = code.flagUrl?.let { asyncPainterResource(it) }
            if (painter != null) {
                KamelImage(
                    { painter },
                    contentDescription = "country flag",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.colorMatrix(colormatrix)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.alpha(animatedAlpha),
                text = code.code,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        CurrencyCodeSelector(isSelected)
    }
}

@Composable
fun CurrencyCodeSelector(isSelected: Boolean = false) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else textColor.copy(0.1f),
        animationSpec = tween(300)
    )
    Box(
        modifier = Modifier.size(18.dp).clip(CircleShape).background(animatedColor),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Icons.Default.Check,
                contentDescription = "Check icon",
                tint = surfaceColor
            )
        }

    }

}