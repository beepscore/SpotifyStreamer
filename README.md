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
<https://github.com/kaaes/spotify-web-api-android>
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

## SpotifyHelper
App had SpotifyHelper.java to build uri for requests to Spotify.
Instead of using SpotifyHelper, app uses Spotify api wrapper library methods.
Deleted unused SpotifyHelper and SpotifyHelperTest.
Tagged git commit for reference uri building example.

## 2 pane master/detail layout for tablet
Use layout-sw400dp to show 2 panes on Nexus 7.
On Nexus 7, naming directory layout-sw600dp shows only 1 pane.

---

## Issues/Questions
Note: Normally I record/manage issues into bug tracker like jira or github issues.
For Udacity class, put them in README.md so they will survive cloning repo and be easily visible to others.

### Stage 1 Issues/Questions

#### bug(fixed): Navigate from tracks "up" to artists loses artist list
Navigate "up" correctly navigated to artists, but artists list was empty.
Fixed by setting ArtistsActivity launchmode singleTop.
I don't know if this is considered ok practice or a hack.

##### References
http://developer.android.com/guide/topics/resources/runtime-changes.html#RetainingAnObject
http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html

https://discussions.udacity.com/t/how-to-test-out-if-youre-implementing-parcelable-correctly/25172/2
https://discussions.udacity.com/t/bug-artist-list-activity-is-destroyed-on-navigating-up-but-not-back/21076/2
https://discussions.udacity.com/t/how-to-save-intent-extras/21024
https://discussions.udacity.com/t/handling-rotation/22391/13
https://discussions.udacity.com/t/handling-device-rotation-the-easy-way/24344/1

###### singleTop
https://discussions.udacity.com/t/how-to-save-intent-extras/21024/4
https://developer.android.com/reference/android/app/Activity.html

#### bug(mostly fixed): Navigate from tracks "back" to artists re-shows keyboard
Navigate "back" shows previous search field and artists list as desired but re-shows keyboard.
This used to happen every time.
Added searchView.clearFocus. Bug still happens occasionally. Timing dependent?

#### onCreate vs onCreateView
Is it considered best practice to do as much work as possible in onCreate?
Answer:
Do non-view work in onCreate, view work in onCreateView.
Reference from reviewer
http://stackoverflow.com/questions/28929637/difference-and-uses-of-oncreate-oncreateview-and-onactivitycreated-in-fra

---

### Stage 2 Issues/Questions

##### Additional Reviewer Comments
Well done with your app. You have a minor fix and I've left you some tips.

#### runnable
Does fragment onCreate run on UI thread, so its a good place to create Handler for UI updates?
I read several references, still don't completely understand Runnable and Handler.

##### Answer
onCreate runs before onCreateView and it does run on the main thread of your app. It is a good place to create a handler.

#### use of service
PlayerFragment is able to get info from the service.
Do you have any suggestions to improve the way the app uses its service?

##### Answer
You can try and implementing a now playing feature button in the action bar. Your service can keep the song alive even after you exit the player fragment. This gives a chance to bring up which song is being currently played.

#### next/previous
The app passes entire tracks list to PlayerFragment in order to make next/previous easier to implement.
I didn't want to prematurely optimize, but wonder if there's a less memory intensive solution.

##### Answer
In this case its ok since its only 10 songs. For longer lists, its better to store the songs in a content provider and just pass in the index in a list
