package ru.mirea.tsybulko.mieraproject.ui.restaurant

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.mirea.tsybulko.mieraproject.databinding.FragmentRestaurantBinding


class RestaurantFragment : Fragment() {
    private lateinit var binding: FragmentRestaurantBinding

    private lateinit var mapView: MapView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRestaurantBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables", "InlinedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView
        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        mapView.setZoomRounding(true)
        mapView.setMultiTouchControls(true)

        checkAndRequestPermissions(
            arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )


        val locationNewOverlay =

        mapView.overlays.apply {
            add(MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView).apply {
                enableMyLocation()
                runOnFirstFix {
                    try {
                        this@RestaurantFragment.activity!!.runOnUiThread {
                            val latitude = this.myLocation.latitude
                            val longitude = this.myLocation.longitude
                            mapView.controller.apply {
                                setZoom(1.0)
                                setCenter(GeoPoint(latitude, longitude))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
            add(
                CompassOverlay(
                    requireContext(), InternalCompassOrientationProvider(requireContext()), mapView
                ).apply { enableCompass() }
            )
            add(
                ScaleBarOverlay(mapView).apply {
                    setCentred(true)
                    setScaleBarOffset(
                        mapView.context.resources.displayMetrics.widthPixels / 2, 10
                    )
                }
            )
            add(
                Marker(mapView).apply {
                    position = GeoPoint(55.794229, 37.700772)
                    setOnMarkerClickListener { _, mapView ->
                        Toast.makeText(mapView.context, "MIREA", Toast.LENGTH_SHORT).show()
                        true
                    }
                    icon = resources.getDrawable(org.osmdroid.library.R.drawable.osm_ic_follow_me_on)
                    title = "MIREA"
                }
            )
            add(
                Marker(mapView).apply {
                    position = GeoPoint(55.772151, 37.619540)
                    setOnMarkerClickListener { _, mapView ->
                        Toast.makeText(mapView.context, "Ra'men", Toast.LENGTH_SHORT).show()
                        true
                    }
                    icon = resources.getDrawable(org.osmdroid.library.R.drawable.osm_ic_follow_me_on)
                    title = "Ra'men"
                }
            )
            add(
                Marker(mapView).apply {
                    position = GeoPoint(55.776497, 37.658118)
                    setOnMarkerClickListener { _, mapView ->
                        Toast.makeText(mapView.context, "Kfc on Komsomolskaya", Toast.LENGTH_SHORT).show()
                        true
                    }
                    icon = resources.getDrawable(org.osmdroid.library.R.drawable.osm_ic_follow_me_on)
                    title = "Kfc on Komsomolskaya"
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        Configuration.getInstance()
            .save(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        mapView.onPause()
    }

    private fun checkAndRequestPermissions(permissionsToCheck: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissionsToCheck) {
            if (ContextCompat.checkSelfPermission(
                    this.context!!,
                    permission
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty())
            requestPermissions(permissionsToRequest.toTypedArray(), 200)
    }
}