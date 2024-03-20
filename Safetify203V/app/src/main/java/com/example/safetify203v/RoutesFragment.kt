package com.example.safetify203v

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.PriorityQueue


class RoutesFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private val TAG = "RoutesActivity"

    private lateinit var mMap: GoogleMap
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    private val GEOFENCE_RADIUS = 200f
    private val GEOFENCE_ID = "SOME_GEOFENCE_ID"

    private val FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    private lateinit var autocompleteFragment: AutocompleteSupportFragment

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Define a member variable to store the polyline
    private var polyline: Polyline? = null

    // Define a member variable to store the marker for destination
    private var destinationMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_routes, container, false)

        // Initialize Places
        Places.initialize(requireContext(), getString(R.string.google_map_api_key))

        // Find the AutocompleteSupportFragment using childFragmentManager
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Set up AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(requireContext(), "Some Error in Search", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(p0: Place) {
                val latLng = p0.latLng

                // Pop up 3 buttons: Balanced, Shortest, Safest
                val builder = AlertDialog.Builder(requireContext())
                val view = layoutInflater.inflate(R.layout.routes_selection, null)

                builder.setView(view)
                val dialog = builder.create()

                // Upon Clicking Balanced Route
                view.findViewById<Button>(R.id.btnBalanced).setOnClickListener{

                    dialog.dismiss()
                }

                // Upon Clicking Shortest Route
                view.findViewById<Button>(R.id.btnShortest).setOnClickListener{
                    showDirectionsToDestination(latLng)
                    dialog.dismiss()
                }

                // Upon Clicking Safest Route
                view.findViewById<Button>(R.id.btnSafest).setOnClickListener{

                    dialog.dismiss()
                }

                if (dialog.window != null){
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                }
                dialog.show()

                //showDirectionsToDestination(latLng)
            }
        })

        // Find and initialize the map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize geofencing client and helper
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geofenceHelper = GeofenceHelper(requireContext())

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


        // Inflate the layout for this fragment
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the last known location from Fused Location Provider
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        // Create a LatLng object from the last known location
                        val currentLatLng = LatLng(location.latitude, location.longitude)

                        // Move the camera to the user's current location
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                    }
                }
        } else {
            // If location permission is not granted, request it
            requestLocationPermission()
        }

        enableUserLocation()

        mMap.setOnMapLongClickListener(this)

        // Find the location button view
        val locationButton = (requireView().findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
        val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams

        // Modify the layout parameters to change the position of the location button
        // For example, to position it at the bottom right corner with margins
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
        rlp.setMargins(0, 360, 10, 0)

    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            // Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_ACCESS_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We have the permission
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap.isMyLocationEnabled = true
            } else {
                // We do not have the permission..
            }
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to fetch directions
                Toast.makeText(requireContext(), "Can get direction...", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Unable to fetch location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We have the permission
                Toast.makeText(requireContext(), "You can add Geofence...", Toast.LENGTH_SHORT).show()
            } else {
                // We do not have the permission..
                Toast.makeText(requireContext(), "Background location access is necessary for Geofence to trigger...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            // We need background permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                handleMapLongClick(latLng)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    // We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
                } else {
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACKGROUND_LOCATION_ACCESS_REQUEST_CODE)
                }
            }
        } else {
            handleMapLongClick(latLng)
        }
    }

    private fun handleMapLongClick(latLng: LatLng) {
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng, GEOFENCE_RADIUS)
        addGeofence(latLng, GEOFENCE_RADIUS)
    }

    private fun addGeofence(latLng: LatLng, radius: Float) {
        val geofence = geofenceHelper.getGeofence(
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER)
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getPendingIntent()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent!!)
            .addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")
            }
            .addOnFailureListener { e ->
                val errorMessage = geofenceHelper.getErrorString(e)
                Log.d(TAG, "onFailure: $errorMessage")
            }
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, radius: Float) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        mMap.addCircle(circleOptions)
    }

    private fun zoomOnMap(latLng: LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 18f)
        mMap?.animateCamera(newLatLngZoom)
    }


    // Start building safe routes

    // Method to fetch and display directions
    private fun showDirectionsToDestination(destination: LatLng) {
        if (hasLocationPermission()) {
            Log.d(TAG, "Ran showDirectionsToDestination")
            fetchDirections(destination)
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun fetchDirections(destination: LatLng) {
         // Implement Retrofit service to fetch directions
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the last known location from Fused Location Provider
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        // Create a LatLng object from the last known location
                        val origin = LatLng(location.latitude, location.longitude)
                        val apiKey = getString(R.string.google_map_api_key)

                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://maps.googleapis.com/maps/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val service = retrofit.create(DirectionsService::class.java)

                        val call = service.getDirections(
                            origin = "${origin.latitude},${origin.longitude}",
                            destination = "${destination.latitude},${destination.longitude}",
                            mode = "walking",
                            apiKey = apiKey
                        )

                        call.enqueue(object : Callback<DirectionsResponse> {
                            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                                if (response.isSuccessful) {
                                    val directionsResponse = response.body()
                                    Log.d(TAG, " fetchDirections: successful response: $directionsResponse")
                                    directionsResponse?.let { processDirections(it) }
                                } else {
                                    // Handle unsuccessful response
                                    val errorBody = response.errorBody()?.string()
                                    Log.e(TAG, "Error response: ${response.code()}, $errorBody")
                                    // Handle error based on the HTTP status code and error message
                                }
                            }

                            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                                Log.d(TAG, "fetchDirections: call failed")
                            }
                        })
                    }
                }
        } else {
            // If location permission is not granted, request it
            requestLocationPermission()
        }
    }

    private fun processDirections(directionsResponse: DirectionsResponse) {Log.d(TAG, "    Successful call to processDirections")
        val points = directionsResponse.routes.firstOrNull()?.overview_polyline?.points
        points?.let { decodedPoints ->
            val decodedLatLngPoints = decodePolylinePoints(decodedPoints)
            drawPolyline(decodedLatLngPoints)
        }
    }

    // Decode polyline points retrieve by Google API
    private fun decodePolylinePoints(encodedPath: String): List<LatLng> {
        val polyLine = ArrayList<LatLng>()
        var index = 0
        val len = encodedPath.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encodedPath[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encodedPath[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val decodedLat = lat.toDouble() / 1E5
            val decodedLng = lng.toDouble() / 1E5
            val latLng = LatLng(decodedLat, decodedLng)
            polyLine.add(latLng)
        }
        return polyLine
    }

    @SuppressLint("MissingPermission")
    private fun drawPolyline(decodedLatLngPoints: List<LatLng>) {
        // Create PolylineOptions to draw the polyline
        Log.d(TAG, "    Successful call to drawPolyline")
        val polylineOptions = PolylineOptions().apply {
            addAll(decodedLatLngPoints)
            color(Color.BLUE)
            width(8f)
        }
        // Remove any existing polyline from the map
        polyline?.remove()

        // Add the new polyline to the map
        polyline = mMap.addPolyline(polylineOptions)

        // Move camera to fit both origin and destination along with the polyline
        val builder = LatLngBounds.Builder()
        destinationMarker?.position?.let { builder.include(it) }

        if (hasLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        builder.include(LatLng(it.latitude, it.longitude))
                        val bounds = builder.build()
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    }
                }
        } else {
            requestLocationPermission()
        }
    }
    private interface DirectionsService {
        @GET("directions/json")
        fun getDirections(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("mode") mode: String,
            @Query("key") apiKey: String
        ): Call<DirectionsResponse>
    }
}

// Performing Safest Route option

// Define a class for the Vertex
data class Vertex<T>(val data: T)
data class Edge<T>(val source: Vertex<T>, val destination: Vertex<T>, val weight: Double)

class Graph<T> {
    val adjacencyList: MutableMap<Vertex<T>, MutableList<Edge<T>>> = mutableMapOf()

    fun addEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double) {
        val edge = Edge(source, destination, weight)
        adjacencyList.computeIfAbsent(source) { mutableListOf() }.add(edge)
    }

    fun getEdges(vertex: Vertex<T>): List<Edge<T>>? = adjacencyList[vertex]
}

fun <T> dijkstra(graph: Graph<T>, start: Vertex<T>, end: Vertex<T>): List<Vertex<T>> {
    val distances = mutableMapOf<Vertex<T>, Double>().withDefault { Double.MAX_VALUE }
    distances[start] = 0.0

    val priorityQueue = PriorityQueue<Pair<Vertex<T>, Double>>(compareBy { it.second })
    priorityQueue.add(Pair(start, 0.0))

    val visited = mutableSetOf<Vertex<T>>()
    val prev = mutableMapOf<Vertex<T>, Vertex<T>?>()

    while (priorityQueue.isNotEmpty()) {
        val (currentVertex, _) = priorityQueue.poll()
        if (currentVertex == end) break
        if (visited.contains(currentVertex)) continue
        visited.add(currentVertex)

        graph.getEdges(currentVertex)?.forEach { edge ->
            if (!visited.contains(edge.destination)) {
                val newDist = distances.getValue(currentVertex) + edge.weight
                if (newDist < distances.getValue(edge.destination)) {
                    distances[edge.destination] = newDist
                    prev[edge.destination] = currentVertex
                    priorityQueue.add(Pair(edge.destination, newDist))
                }
            }
        }
    }

    // Reconstruct the path from 'start' to 'end'
    val path = mutableListOf<Vertex<T>>()
    var at: Vertex<T>? = end
    while (at != null) {
        path.add(at)
        at = prev[at]
    }
    path.reverse()
    return path
}
