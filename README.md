# It is incomplete.(08.01.2017 ~ )



# [Android] Firebase Auth - GitHub SignIn/SignOut Sample

## Introduction

This is a sample app about how to authenticate with Firebase using GitHub account by integrating GitHub authentication into your app.

- Sample App Info.

````
minSdkVersion 19
targetSdkVersion 25
````

## Demo


## Getting Started

- [Add Firebase to Your Android Project](https://firebase.google.com/docs/android/setup)
    - Check prerequisites
    - Add Firebase to your app
    - Add the SDK

build.gradle
````
buildscript {
    // ...
    dependencies {
        // ...
        classpath 'com.google.gms:google-services:3.1.0'
    }
}
````
app/build.gradle
````
apply plugin: 'com.android.application'

android {
  // ...
}

dependencies {
  // ...

  // Firebase
  compile 'com.google.firebase:firebase-core:10.0.1'

  // Firebase Auth
  compile 'com.google.firebase:firebase-auth:10.0.1'

  // Using the OkHttp Library for HTTP Networking
  compile 'com.squareup.okhttp3:okhttp:3.8.1'
}

// ADD THIS AT THE BOTTOM
apply plugin: 'com.google.gms.google-services'
````

## Authenticate with Firebase

#### 1. [Register your app](https://github.com/settings/applications/new) as a developer application on GitHub
- set <b><i>Authorization callback URL</i></b> :  (e.g. yourname://authorization-callback.url)

![](/image/New_OAuth_Application.png)

- Get your app's OAuth 2.0 <b><i>Client ID</i></b> and <b><i>Client Secret</i></b>.

![](/image/OAuth_Application_Settings.png)

#### 3. Generate new token
- get your <b><i>Personal access tokens</i></b>

![](/image/Personal_Access_Tokens.png)

![](/image/New_personal_access_token.png)

![](/image/Personal_Access_Tokens_Copy.png)

#### 4. Enable GitHub authentication in the Firebase Console
- In the Firebase console, open the Auth Section
- On the Sign in method tab, enable the GitHub sign-in method and specify the OAuth 2.0 Client ID and Client Secret you got from GitHub.
- We are not going to use Firebase authorization Callback URL. Make your own callback url.<br/>
(e.g. yourname://authorization-callback.url)<br/>
(But, Make sure your url is set as your Authorization callback URL in yout app's settings page on your GitHub app's config.)

![](/image/Firebase_console_auth.png)

![](/image/Firebase_console_setting.png)

#### 5. Integrate GitHub authentication<br/>
- Integrate GitHub authentication into your app by following the [developer's documentation](https://developer.github.com/apps/building-integrations/setting-up-and-registering-oauth-apps/about-authorization-options-for-oauth-apps/).<br/>(<b>Use the web application flow</b>)





- Set up an intent filter in your app to handle the OAuth 2.0 callback from GitHub.<br/>(At the end of the GitHub sign-in flow, you will receive an OAuth 2.0 access token.)

#### 2.  

