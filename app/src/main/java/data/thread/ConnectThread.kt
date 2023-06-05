package data.thread

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectThread(
    private var device: BluetoothDevice,
    private var bluetoothAdapter: BluetoothAdapter?,
    private var myUIDD: UUID,
) : Thread() {

    val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(myUIDD)
    }

    public override fun start() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()
        mmSocket?.connect()
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e("thread ble", "Could not close the client socket", e)
        }
    }
}