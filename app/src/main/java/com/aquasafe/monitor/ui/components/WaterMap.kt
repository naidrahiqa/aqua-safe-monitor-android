package com.aquasafe.monitor.ui.components

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.aquasafe.monitor.model.TestLocation

/**
 * OpenStreetMap (osmdroid) wrapper — menampilkan pin lokasi uji.
 * Long-press pada peta memicu [onLongPress] dengan koordinat terpilih.
 */
@Composable
fun WaterMap(
    locations: List<TestLocation>,
    center: GeoPoint = GeoPoint(-6.5833, 110.6667),
    zoom: Double = 13.0,
    modifier: Modifier = Modifier,
    onLongPress: ((GeoPoint) -> Unit)? = null,
) {
    val context = LocalContext.current
    Configuration.getInstance().userAgentValue = context.packageName

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(zoom)
            controller.setCenter(center)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    DisposableEffect(locations) {
        mapView.overlays.removeAll { it is Marker }
        locations.forEach { location ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(location.lat, location.lng)
                title = location.name
                snippet = "WQI ${location.wqiScore.toInt()}/100 — ${location.status}"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)
            }
            mapView.overlays.add(marker)
        }
        onDispose { mapView.overlays.removeAll { it is Marker } }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            if (onLongPress != null) {
                view.setOnLongClickListener { longPress ->
                    val point = GeoPoint(
                        view.projection.fromPixels(longPress.x.toInt(), longPress.y.toInt()).latitude,
                        view.projection.fromPixels(longPress.x.toInt(), longPress.y.toInt()).longitude,
                    )
                    onLongPress(point)
                    true
                }
            }
        },
    )
}