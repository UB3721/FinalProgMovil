package com.df.base.ui.navigation

import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

inline fun <reified T : Parcelable> nonNullableNavType(
    serializer: KSerializer<T>, // Recibe un serializador explícito
    json: Json = Json
): NavType<T> = object : NavType<T>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): T {
        return bundle.getParcelable(key) ?: error("Argument $key is missing in the bundle.")
    }

    override fun parseValue(value: String): T {
        return json.decodeFromString(serializer, value) // Usa el serializador explícito
    }

    override fun serializeAsValue(value: T): String {
        return json.encodeToString(serializer, value) // Usa el serializador explícito
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putParcelable(key, value)
    }
}

