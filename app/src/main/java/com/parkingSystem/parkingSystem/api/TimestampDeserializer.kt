package com.parkingSystem.parkingSystem.api

import com.google.gson.*
import com.parkingSystem.parkingSystem.api.FirebaseTimestamp
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class TimestampDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {
        return try {
            when {
                // Nếu là string thông thường
                json?.isJsonPrimitive == true -> {
                    json.asString
                }
                // Nếu là Firebase Timestamp object
                json?.isJsonObject == true -> {
                    val obj = json.asJsonObject

                    // Lấy giá trị _seconds
                    val seconds = when {
                        obj.has("_seconds") -> obj.get("_seconds").asLong
                        obj.has("seconds") -> obj.get("seconds").asLong
                        else -> 0L
                    }

                    // Convert sang Date
                    val date = Date(seconds * 1000)

                    // Format theo định dạng Việt Nam
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    outputFormat.format(date)
                }
                else -> "N/A"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
}