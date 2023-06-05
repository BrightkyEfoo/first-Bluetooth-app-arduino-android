package responses

import com.google.gson.annotations.SerializedName


data class DistanceResponse (

  @SerializedName("destination_addresses" ) var destinationAddresses : ArrayList<String> = arrayListOf(),
  @SerializedName("origin_addresses"      ) var originAddresses      : ArrayList<String> = arrayListOf(),
  @SerializedName("rows"                  ) var rows                 : ArrayList<Rows>   = arrayListOf(),
  @SerializedName("status"                ) var status               : String?           = null

)