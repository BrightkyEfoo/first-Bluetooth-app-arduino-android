package com.example.switchactivity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapsInitializer.Renderer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import data.thread.ConnectThread
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.wait
import org.json.JSONObject
import responses.DistanceResponse
import java.io.IOException
import java.util.UUID


class MainActivity3 : AppCompatActivity(), OnMapReadyCallback, OnMapsSdkInitializedCallback,
    LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var respDistance: DistanceResponse

    //    private lateinit var tvGpsLocation: TextView
    private val locationPermissionCode = 2
    private var destMarker: Marker? = null
    private var startMarker: Marker? = null
    private lateinit var destination: MarkerOptions
    private lateinit var thread: ConnectThread
    private lateinit var mmSocket: BluetoothSocket
    private lateinit var mMap: GoogleMap
    private lateinit var btData: JSONObject
    private var isResponseAvailable: Boolean = false
    private var responseStr = ""
    private var distVkm = ""
    private var distMkm = ""
    private var can = false

    private var canUpdateDV = false

    private lateinit var distanceMaxView: EditText
    private lateinit var distanceVoulueView: EditText
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main3)
        setContentView(R.layout.activity_main3)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        MapsInitializer.initialize(applicationContext, Renderer.LATEST, this)



        if (intent == null) return

        distanceMaxView = findViewById<EditText>(R.id.distanceMax)
        distanceVoulueView = findViewById<EditText>(R.id.distanceVoulue)
        resultView = findViewById<TextView>(R.id.resultView)

        var device = intent.getParcelableExtra<BluetoothDevice>("device")
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter
        var myUUID = intent.getSerializableExtra("myUUID") as UUID
        thread = ConnectThread(device!!, bluetoothAdapter, myUUID)
        thread.start()
        mmSocket = thread.mmSocket!!
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener {
            if (destMarker == null) {
                destMarker = mMap.addMarker(MarkerOptions().position(it).title("arrivee"))
            } else {
                destMarker!!.position = it
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(it))

            senDataToHC05("POSITION_DEMAND")
            val btMsg = readDataFromHC05()
            println(btMsg)

            val response =
                getHttp("https://maps.googleapis.com/maps/api/distancematrix/json?destinations=${destMarker?.position?.latitude},${destMarker?.position?.longitude}&origins=${startMarker?.position?.latitude},${startMarker?.position?.longitude}&key=AIzaSyC18L8pZJlrJimHPd0CwkD_CmxLda1A8ys")

            isResponseAvailable = false
            println("second response $responseStr")

            while (!canUpdateDV) {
                println("wait for scree")
                Thread.sleep(200)
            }
            canUpdateDV = false
            println("distance voulue : $distVkm")
            if (distVkm == null) {
                distanceVoulueView.setText("impossible")
            } else {
                distanceVoulueView.setText(distVkm)
            }
            distanceMaxView.setText(distMkm)
            if (can) {
                resultView.text = "Bon voyage!"
                resultView.setBackgroundColor(android.graphics.Color.parseColor("#09e669"))
            } else {
                resultView.text = "Vous ne pouvez pas voyager!"
                resultView.setBackgroundColor(android.graphics.Color.parseColor("#f53f0c"))
            }

        }
    }

    private fun senDataToHC05(msg: String) {
//        println(thread.mmSocket?.outputStream.)
        thread.mmSocket?.outputStream?.write(msg.toByteArray())
    }

    private fun readDataFromHC05(): String {
//        btData = ""
        isResponseAvailable = false
        println("input : ${thread.mmSocket?.inputStream?.available()}")

        if (thread.mmSocket?.inputStream != null) {
            while (thread.mmSocket?.inputStream!!.available() <= 0) {
                println("waitbt")
            }
            if (thread.mmSocket?.inputStream!!.available() > 0) {
                var msgBt = ""
                var bufSize = thread.mmSocket?.inputStream!!.available()

                for (i in 1..bufSize) {
                    msgBt += Char(thread.mmSocket?.inputStream!!.read())
                }

                println(msgBt)

                btData = JSONObject(msgBt)
                try {
                    val latStr = btData["lat"] as Double
                    val lngStr = btData["lng"] as Double
                    val startPos = LatLng(latStr, lngStr)
                    if (startMarker == null) {
                        startMarker =
                            mMap.addMarker(MarkerOptions().position(startPos).title("depart"))
                    } else {
                        startMarker!!.position = startPos
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }
                isResponseAvailable = true
                return msgBt
            }

        }
        return "err bt";

    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    fun getCarLocation() {

    }

    fun calDist(pos1: LatLng, pos2: LatLng) {

    }

    override fun onLocationChanged(location: Location) {
//        tvGpsLocation = findViewById(R.id.textView)
//        tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
        val p = LatLng(location.latitude, location.longitude)
//        mMap.addMarker(MarkerOptions().position(p).title("ma position"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(p))
        println("lat : ${location.latitude} long : ${location.longitude}")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }

    @Throws(IOException::class)
    fun getHttp(url: String): String {
        val client: OkHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i("response", "recieved response from server")
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("HTTP ERROR", "SOMETHING WENT WRONG")
                    } else {
                        val body = response.body?.string()
                        Log.e("HTTP SUCCESS 200", "body response$body")
                        responseStr = body ?: ""
                        isResponseAvailable = true
                        val gson = Gson()
//
                        respDistance = gson.fromJson(responseStr, DistanceResponse::class.java)
                        println(respDistance.status)
                        if (respDistance.rows[0].elements[0].status == "ZERO_RESULTS") {
//                            Toast.makeText(
//                                this@MainActivity3,
//                                "Position d'arrivee innaccessible",
//                                Toast.LENGTH_LONG
//                            ).show()
                            println("route impossible")
                        } else {
                            senDataToHC05("DISTANCE_TRAVEL:${respDistance.rows[0].elements[0].distance?.value.toString()}")
                            val distHC05 = readDataFromHC05()
//                         println(btData["can"])
                            println(distHC05)
                            println(btData)
                            println(btData["distance"])
                            distMkm = "${btData["distance"]}"
                            try {
                                val canInt = btData["can"] as Int
                                can = canInt == 1
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


//                        can = btData["can"] == "1"

                            distVkm =
                                "${respDistance.rows[0].elements[0].distance?.value?.div(1000.0)}"
                        }

                        canUpdateDV = true
                    }

                }
            }

        })
        while (!isResponseAvailable) {
            println("wait")
        }
        return responseStr
    }

}