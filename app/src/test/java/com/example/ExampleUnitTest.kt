package com.example

import com.example.data.model.CITY_MAP_PRESETS
import com.example.data.model.DriverProfile
import com.example.data.model.calculateBimonetaryFareBreakdown
import com.example.data.model.calculateDistanceKm
import com.example.ui.components.MapProviderConfig
import com.example.ui.components.MapProviderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun googleMapsPreviewIsDefaultProviderAndSwitchableToMapbox() {
        MapProviderConfig.activeProvider = MapProviderType.GOOGLE_MAPS_PREVIEW
        assertEquals(MapProviderType.GOOGLE_MAPS_PREVIEW, MapProviderConfig.activeProvider)
        assertEquals("Google Maps", MapProviderConfig.activeProvider.displayName)

        MapProviderConfig.activeProvider = MapProviderType.MAPBOX_PREVIEW
        assertEquals("Mapbox", MapProviderConfig.activeProvider.displayName)
        MapProviderConfig.activeProvider = MapProviderType.GOOGLE_MAPS_PREVIEW
    }

    @Test
    fun calculateDistanceAndBimonetaryFareForMotoAndTaxiDrivers() {
        val origin = CITY_MAP_PRESETS.first { it.name == "Centro de Calabozo" }
        val nearDest = CITY_MAP_PRESETS.first { it.name == "Parque Rómulo Gallegos" }
        val farDest = CITY_MAP_PRESETS.first { it.name == "Represa Generoso Campilongo" }

        val shortDistKm = calculateDistanceKm(origin.lat, origin.lng, nearDest.lat, nearDest.lng)
        val longDistKm = calculateDistanceKm(origin.lat, origin.lng, farDest.lat, farDest.lng)

        assertTrue("Short distance should be within 6 km base", shortDistKm in 0.8..5.0)
        assertTrue("Long distance to Represa should exceed 6 km base", longDistKm > 6.5)

        val bcvRate = 886.23

        val motoDriver = DriverProfile(
            id = "drv_moto_1",
            name = "Simón Bolívar",
            vehicleType = "moto",
            baseFareUsd = 1.50,
            baseIncludedKm = 6.0,
            ratePerKmUsd = 0.13,
            minimumFareUsd = 1.50,
            nightBaseFareUsd = 2.00,
            nightRatePerKmUsd = 0.20
        )

        val taxiDriver = DriverProfile(
            id = "drv_taxi_1",
            name = "Carlos Rodríguez",
            vehicleType = "taxi_economico",
            baseFareUsd = 3.00,
            baseIncludedKm = 5.0,
            ratePerKmUsd = 0.45,
            minimumFareUsd = 3.00,
            nightBaseFareUsd = 4.00,
            nightRatePerKmUsd = 0.60
        )

        // 1. Short trip for Moto (within 6 km included base)
        val motoShort = calculateBimonetaryFareBreakdown(motoDriver, shortDistKm, bcvRate, isNightShift = false)
        assertEquals(0.0, motoShort.extraDistanceKm, 0.001)
        assertEquals(1.50, motoShort.totalUsd, 0.001)
        assertEquals(1329.35, motoShort.totalBs, 0.05)

        // 2. Long trip for Moto (exceeds 6 km included base)
        val motoLongDay = calculateBimonetaryFareBreakdown(motoDriver, 10.0, bcvRate, isNightShift = false)
        assertEquals(4.0, motoLongDay.extraDistanceKm, 0.001)
        assertEquals(0.52, motoLongDay.extraDistanceChargeUsd, 0.001)
        assertEquals(2.02, motoLongDay.totalUsd, 0.001)

        // 3. Night shift trip for Taxi Económico (exceeds 5 km included base)
        val taxiLongNight = calculateBimonetaryFareBreakdown(taxiDriver, 8.0, bcvRate, isNightShift = true)
        assertEquals(3.0, taxiLongNight.extraDistanceKm, 0.001)
        assertEquals(1.80, taxiLongNight.extraDistanceChargeUsd, 0.001)
        assertEquals(5.80, taxiLongNight.totalUsd, 0.001)
        assertEquals(5140.13, taxiLongNight.totalBs, 0.05)
    }
}
