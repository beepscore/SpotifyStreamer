# Purpose
Spotify Streamer is an Android app music streaming client, uses Spotify.

# References

## Udacity "Android nanodegree" course
<https://www.udacity.com/course/android-developer-nanodegree--nd801>  

Project 1: Spotify Streamer
<https://www.udacity.com/course/viewer#!/c-ud853/l-4353948561/m-4328734595>

## Android device
Nexus 4 running Android 5.1.1

## Spotify Web API for Android
"This project is a wrapper for the Spotify Web API.
It uses Retrofit to create Java interfaces from API endpoints."
<https://github.com/kaaes/spotify-web-api-android.git>

## Picasso
"A powerful image downloading and caching library for Android"
<https://square.github.io/picasso>

# Results

## Spotify Web API for Android

### Methods
<https://github.com/kaaes/spotify-web-api-android/blob/master/src/main/java/kaaes/spotify/webapi/android/SpotifyService.java>
Use wrapper methods to get info from Spotify. e.g.

    spotifyService.searchArtists(artistName)

    getArtistTopTrack(artistId, options)

The wrapper builds uri requests and parses json results into Java objects.

### Model objects
<https://github.com/kaaes/spotify-web-api-android/tree/master/src/main/java/kaaes/spotify/webapi/android/models>
The wraapper defines and returns Java Model objects such as ArtistPager, Artist, Image.

### Add to project
Compiled .aar
spotify-web-api-android-0.1.0.aar
Add to project by adding .aar to libs and entering 3 app/build.gradle dependencies.

## Picasso
Add to project via app/build.gradle dependencies.
