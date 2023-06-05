package responses

import com.google.gson.annotations.SerializedName


data class Elements (

  @SerializedName("distance" ) var distance : Distance? = Distance(),
  @SerializedName("duration" ) var duration : Duration? = Duration(),
  @SerializedName("status"   ) var status   : String?   = null

)