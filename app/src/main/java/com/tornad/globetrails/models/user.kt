package com.tornad.globetrails.models

class user {
    var firstname : String = ""
    var lastname : String = ""
    var country : String = ""
    var email : String = ""
    var password : String = ""
    var uid : String = ""

    constructor(firstname: String, lastname: String, country: String, email: String, password: String, uid: String)
    {
        this.firstname = firstname
        this.lastname = lastname
        this.country = country
        this.email = email
        this.password = password
        this.uid = uid
    }
    constructor()
}