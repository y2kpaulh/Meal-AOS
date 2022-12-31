package com.echadworks.meal.model

import com.google.gson.annotations.SerializedName

data class Plan (
    @SerializedName("day"   ) var day   : String? = null,
    @SerializedName("book"  ) var book  : String? = null,
    @SerializedName("fChap" ) var fChap : Int?    = null,
    @SerializedName("fVer"  ) var fVer  : Int?    = null,
    @SerializedName("lChap" ) var lChap : Int?    = null,
    @SerializedName("lVer"  ) var lVer  : Int?    = null
)