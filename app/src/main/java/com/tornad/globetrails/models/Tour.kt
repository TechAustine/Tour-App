package com.tornad.globetrails.models

class Tour {
    var id: String = ""
    var imageUrl: String = ""
    var tourName: String = ""
    var hotel: String = ""
    var days: Int = 0
    var nights: Int = 0
    var charges: Double = 0.0
    var includedInPackage: List<String> = listOf()
    var excludedFromPackage: List<String> = listOf()

    constructor(id: String, imageUrl: String, tourName: String, hotel: String, days: Int, nights: Int, charges: Double, includedInPackage: String, excludedFromPackage: String)
    {
        this.id = id
        this.imageUrl = imageUrl
        this.tourName = tourName
        this.hotel = hotel
        this.days = days
        this.nights = nights
        this.charges = charges
        this.includedInPackage = includedInPackage.split(",").map { it.trim() }
        this.excludedFromPackage = excludedFromPackage.split(",").map { it.trim() }
    }
    constructor()
}