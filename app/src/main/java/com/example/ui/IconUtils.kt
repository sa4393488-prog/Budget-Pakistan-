package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object IconUtils {
    fun getIconByName(iconName: String): ImageVector {
        return when (iconName) {
            "ShoppingCart" -> Icons.Default.ShoppingCart
            "FlashOn" -> Icons.Default.FlashOn
            "Home" -> Icons.Default.Home
            "LocalGasStation" -> Icons.Default.LocalGasStation
            "Restaurant" -> Icons.Default.Restaurant
            "School" -> Icons.Default.School
            "VolunteerActivism" -> Icons.Default.VolunteerActivism
            "AccountBalance" -> Icons.Default.AccountBalance
            "LocalHospital" -> Icons.Default.LocalHospital
            "AttachMoney" -> Icons.Default.AttachMoney
            "Work" -> Icons.Default.Work
            "TrendingUp" -> Icons.AutoMirrored.Filled.TrendingUp
            "Receipt" -> Icons.Default.Receipt
            else -> Icons.Default.Category
        }
    }

    fun formatPkr(amount: Double, symbol: String = "₨"): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "PK"))
        formatter.maximumFractionDigits = 0
        val formattedNumber = formatter.format(amount)
        return "$symbol $formattedNumber"
    }

    fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd MMM, yyyy - hh:mm a", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
