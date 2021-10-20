package br.com.source.view.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily

@Composable
fun Font(name: String, res: String, weight: FontWeight, style: FontStyle): Font =
    androidx.compose.ui.text.platform.Font("font/$res.ttf", weight, style)

object Fonts {

    @Composable
    fun balooBhai2() = FontFamily(
        Font(
            "Baloo Bhai",
            "BalooBhai2-ExtraBold",
            FontWeight.ExtraBold,
            FontStyle.Normal
        ),
    )

    @Composable
    fun roboto() = FontFamily(
        Font(
            "Roboto",
            "Roboto-Black",
            FontWeight.Black,
            FontStyle.Normal
        ),
        Font(
            "Roboto",
            "Roboto-Light",
            FontWeight.Light,
            FontStyle.Normal
        ),

        Font(
            "Roboto",
            "Roboto-Medium",
            FontWeight.Medium,
            FontStyle.Normal
        ),
        Font(
            "Roboto",
            "Roboto-Regular",
            FontWeight.Normal,
            FontStyle.Normal
        ),
    )

    @Composable
    fun robotoMono() = FontFamily(
        Font(
            "Roboto Mono",
            "RobotoMono-Bold",
            FontWeight.Bold,
            FontStyle.Normal
        ),
        Font(
            "Roboto Mono",
            "RobotoMono-Regular",
            FontWeight.Normal,
            FontStyle.Normal
        ),
    )

}