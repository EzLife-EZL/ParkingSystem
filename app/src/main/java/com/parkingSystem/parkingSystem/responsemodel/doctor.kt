package com.parkingSystem.parkingSystem.responsemodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName

// Data classes từ BE
data class Park(
    val park_id: String = "",
    val park_name: String = "",
    val price: Double = 0.0,
    val type_vehicle: String = "",
    val slots: List<Slot> = emptyList(),
    val address: String = ""
)

data class Slot(
    val slotName: String = "",
    val slot_id: String = "",
    val isBooked: Boolean = false,
    @SerializedName("pos_X")
    val pos_X: Int = 0,
    @SerializedName("pos_Y")
    val pos_Y: Int = 0
)

// Enum cho trạng thái hiển thị
enum class SpotStatus {
    AVAILABLE,
    OCCUPIED,
    BLOCKED
}


// Search period data class
data class SearchPeriod(
    @SerializedName("from")
    val from: String,

    @SerializedName("to")
    val to: String,

    @SerializedName("numberOfDays")
    val numberOfDays: Int
)

// Available slot for each day
data class AvailableSlot(
    @SerializedName("date")
    val date: String,

    @SerializedName("dayOfWeek")
    val dayOfWeek: Int, // 0 = Sunday, 1 = Monday, etc.

    @SerializedName("dayName")
    val dayName: String, // "Sunday", "Monday", etc.

    @SerializedName("displayDate")
    val displayDate: String, // "Saturday, June 28, 2025"

    @SerializedName("slots")
    val slots: List<TimeSlot>,

    @SerializedName("totalSlots")
    val totalSlots: Int
)

// Individual time slot
data class TimeSlot(
    @SerializedName("workingHourId")
    val workingHourId: String, // "7-7-30"

    @SerializedName("time")
    val time: String, // "07:30"

    @SerializedName("hour")
    val hour: Int, // 7

    @SerializedName("minute")
    val minute: Int, // 30

    @SerializedName("displayTime")
    val displayTime: String // "7:30 AM"
)

// Extension functions for convenience
@RequiresApi(Build.VERSION_CODES.O)
fun AvailableSlot.toLocalDate(): java.time.LocalDate? {
    return try {
        java.time.LocalDate.parse(this.date)
    } catch (e: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun TimeSlot.toLocalTime(): java.time.LocalTime? {
    return try {
        java.time.LocalTime.of(this.hour, this.minute)
    } catch (e: Exception) {
        null
    }
}

// Helper function to check if a slot is available
@RequiresApi(Build.VERSION_CODES.O)
fun TimeSlot.isAvailable(): Boolean {
    val currentTime = java.time.LocalTime.now()
    val currentDate = java.time.LocalDate.now()

    // Add your business logic here
    return true
}

data class CreateSlotEnvelope(
    val path: String,          // example: "park"
    val data: SlotData
)

data class SlotData(
    @SerializedName("park_name") val park_name: String,
    val address: String,
    @SerializedName("type_vehicle") val type_vehicle: String,
    val price: Double,
    val slots: List<SlotDto>
)

data class SlotDto(
    val slotName: String,
    @SerializedName("pos_X") val pos_X: Int,
    @SerializedName("pos_Y") val pos_Y: Int,
    val isBooked: Boolean
)

data class ParkingOverview(
    val parkedVehicles: Int = 0,
    val availableSpots: Int = 0,
    val totalSpots: Int = 0,
    val occupancyRate: Double = 0.0
)


data class RevenueReport(
    val currentPeriod: PeriodData,
    val previousPeriod: PeriodData,
    val growthPercentage: Double,
    val isPositiveGrowth: Boolean,
    val comparisonText: String
)

data class PeriodData(
    val amount: Double,
    val label: String
)

data class RevenueResponse(
    val labels: List<String>,
    val series: List<RevenueVehicleType>
)
data class RevenueVehicleType(
    val typeVehicle: String,
    val currentMonth: Double,
    val lastMonth: Double,
    val growth: Double,
    val totalBookings: Int,
    val paidBookings: Int,
    val months: List<Double>
)