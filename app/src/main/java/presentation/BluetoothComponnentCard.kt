package presentation

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.switchactivity.MainActivity2
import com.example.switchactivity.MainActivity3
import data.thread.ConnectThread
import java.util.UUID


@Composable
fun BluetoothComponnentCard(
    name: String?,
    address: String,
    device: BluetoothDevice,
    bluetoothAdapter : BluetoothAdapter,
    myUUID : UUID,
    context : Context
) {
    Button(
        onClick = {
            var switcActivityIntent = Intent(context , MainActivity3::class.java)
            switcActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            switcActivityIntent.putExtra("device" , device)
            switcActivityIntent.putExtra("myUUID" , myUUID)

            context.startActivity(switcActivityIntent)


        }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Magenta
        ), shape = RoundedCornerShape(0.dp), contentPadding = PaddingValues(0.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, color = Color.Transparent)
                .padding(3.dp)
                .background(color = Color.hsl(136f, 0.71f, 0.46f), shape = RoundedCornerShape(5.dp))
                .padding(5.dp)
        ) {

            Text(text = "Appareil", modifier = Modifier.padding(horizontal = 0.dp, vertical = 3.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.hsl(136f, 0.64f, 0.38f),
                        shape = RoundedCornerShape(2.dp)
                    )
                    .padding(5.dp)
            ) {
                Text(text = "$name", color = Color.White, fontSize = 15.sp)
                Row {
                    Text(text = "RSSI: 0", color = Color.White)
                    Spacer(Modifier.weight(1f))
                    Text(text = "ID: $address", color = Color.White)
                }
            }
        }
    }
}


