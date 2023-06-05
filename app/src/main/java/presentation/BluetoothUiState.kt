package presentation

import domain.chat.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices:List<BluetoothDevice> = emptyList(),
    val pairedDevices:List<BluetoothDevice> = emptyList(),
)
