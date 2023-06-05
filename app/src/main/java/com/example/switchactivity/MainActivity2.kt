package com.example.switchactivity


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import data.chat.AndroidBluetoothController
import presentation.BluetoothComponnentCard
import java.util.UUID


class MainActivity2 : AppCompatActivity() {
    private lateinit var scanButton: Button
    private lateinit var stopScanButton: Button
    private lateinit var blController: AndroidBluetoothController
    private var btPermission = false
    private val cont = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)



        blController = AndroidBluetoothController(this)

        scanButton = findViewById(R.id.scanButton)
        scanButton.setOnClickListener {
            Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show()
            val bluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "This device doesn't support bluetooth", Toast.LENGTH_LONG)
                    .show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                } else {
                    bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)
                }
            }

            val containerLayout = findViewById<LinearLayout>(R.id.BluetoothDevicesContainer)
//            blController.startDiscovery()
//            val devices = blController.pairedDevices.value
            val devices = bluetoothAdapter?.bondedDevices?.iterator()
//            bluetoothAdapter.m
            Log.println(Log.WARN, "devicesScanned", "$devices")
//            val getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null)
//            val uuids = getUuidsMethod.invoke(bluetoothAdapter, null) as Array<ParcelUuid>
            val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

            if (devices != null) {
                for (el in devices) {
                    Log.println(Log.WARN, "devicesScanned", "${el?.name} ===> ${el.address}")

                    containerLayout.addView(
                        ComposeView(this).apply {
                            setContent {
                                BluetoothComponnentCard(
                                    name = el?.name,
                                    address = el.address,
                                    myUUID = myUUID,
                                    bluetoothAdapter = bluetoothAdapter,
                                    device = el,
                                    context = cont
                                )
                            }
                        }
                    )
                }
            }





        }
    }



    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val bluetoothManager = getSystemService(BluetoothManager::class.java)
            val bluetoothAdapter = bluetoothManager?.adapter
            btPermission = true
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                btActivityResultLauncher.launch(enableBtIntent)
            } else {
                btScan()
            }
        } else {
            btPermission = false
        }

    }
    private val btActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            btScan()
        }

    }

    private fun btScan() {
        Toast.makeText(this, "scann", Toast.LENGTH_LONG).show()
    }

    private fun switchActivity(){
        var switcActivityIntent = Intent(this , MainActivity2::class.java)
        startActivity(switcActivityIntent)
    }


}