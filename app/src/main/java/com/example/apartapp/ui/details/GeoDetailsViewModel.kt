package com.example.apartapp.ui.details

import androidx.lifecycle.ViewModel
import com.example.apartapp.data.GeoObjectHolder
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.mapkit.uri.UriObjectMetadata

data class DetailsDialogUiState(
    val title: String,
    val descriptionText: String,
    val location: Point,
    val uri: String?
)

class GeoDetailsViewModel : ViewModel() {
    fun uiState(): DetailsDialogUiState {
        val geoObject = GeoObjectHolder.tappedGeo

        val toponymAddress =
            geoObject?.metadataContainer?.getItem(ToponymObjectMetadata::class.java)?.address
        val postalCode = toponymAddress?.postalCode

        val street = toponymAddress?.components?.firstOrNull {
            it.kinds.contains(Address.Component.Kind.STREET)
        }?.name

        val house = toponymAddress?.components?.firstOrNull {
            it.kinds.contains(Address.Component.Kind.HOUSE)
        }?.name

        val province = toponymAddress?.components?.firstOrNull {
            it.kinds.contains(Address.Component.Kind.PROVINCE)
        }?.name ?: "Undefined"

        val country = toponymAddress?.components?.firstOrNull {
            it.kinds.contains(Address.Component.Kind.COUNTRY)
        }?.name

        val city = toponymAddress?.components?.firstOrNull {
            it.kinds.contains(Address.Component.Kind.LOCALITY)
        }?.name ?: "Unknown place"

        val title = if (house == null) {
            street ?: province
        } else "$street $house"

        val description = if (postalCode == null) {
            city
        } else "$city: $postalCode"


        val uri =
            geoObject?.metadataContainer?.getItem(UriObjectMetadata::class.java)?.uris?.firstOrNull()

        return DetailsDialogUiState(
            title = title,
            descriptionText = description,
            location = geoObject?.geometry?.firstOrNull()?.point ?: Point(0.0, 0.0),
            uri = uri?.value
        )
    }
}
