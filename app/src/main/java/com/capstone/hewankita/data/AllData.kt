package com.capstone.hewankita.data

data class AllData(

    //Data User
    var Type: String? = null,
    var Username: String? = null,
    var Email: String? = null,
    var Address: String? = null,
    var Phone: String? = null,

    //Data Service
    var Outlet: String? = null,
    var Date: String? = null,
    var Time: String? = null,
    var CheckIn: String? = null,
    var CheckOut: String? = null,
    var TimeOfArrival: String? = null,

    //Data Tips
    var Judul: String? = null,
    var Deskripsi: String? = null,
    var TipsImage: String? = null,
    var TipsUrl: String? = null
)

