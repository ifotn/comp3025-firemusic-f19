package com.infrontofthenet.firemusic

class Artist {
    var artistId: String? = null
    var artistName: String? = null
    var artistGenre: String? = null

    // required empty constructor - we may need to re-enable this later
    //constructor() {
    //}

    // 1st overload w/all properties passed in
    constructor(artistId: String, artistName: String, artistGenre: String) {
        this.artistId = artistId
        this.artistName = artistName
        this.artistGenre = artistGenre
    }
}