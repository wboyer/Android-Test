Android-Test
========

This is an Android app for miscellaneous prototyping.  Currently it demonstrates the following things:

- HLS video playback using [VideoView](http://developer.android.com/reference/android/widget/VideoView.html).
- Simple use of [ListView](http://developer.android.com/reference/android/widget/ListView.html) and [ArrayAdapter](http://developer.android.com/reference/android/widget/ArrayAdapter.html) to display video playlists, allow their items to be clicked, and indicate which of their items are the ones currently playing.
- Basic handling of the Android activity/fragment [life-cycle](http://developer.android.com/training/basics/activity-lifecycle/index.html).
- [AndroidHttpClient](http://developer.android.com/reference/android/net/http/AndroidHttpClient.html) and [AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html) to fetch asynchronously via HTTP.

Note that this app relies on an abstract representation of "media catalogs" that is defined by [media-catalog](https://github.com/wboyer/media-catalog).  It also requires a library that provides a concrete catalog implementation.  That library isn't part of this project, which means that you won't be able to build and run the app from this repo alone.  I can mail you the .jar you need.

