# [Android] Firebase Auth - GitHub SignIn/SignOut Sample

## Introduction

This is a sample app about how to authenticate with Firebase using GitHub account by integrating GitHub authentication into your app.

- Sample App Info.

````Gradle
minSdkVersion 19
targetSdkVersion 25
````

## Demo

![](/image/signinout_app1.png)
![](/image/signinout_app2.png)
![](/image/signinout_app3.png)
![](/image/signinout_app4.png)

## Getting Started

- [Add Firebase to Your Android Project](https://firebase.google.com/docs/android/setup)
    - Check prerequisites
    - Add Firebase to your app
    - Add the SDK

build.gradle
````Gradle
buildscript {
    // ...
    dependencies {
        // ...
        classpath 'com.google.gms:google-services:3.1.0'
    }
}
````
app/build.gradle
````Gradle
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

  // Using the Glide Library for Image loading
  compile 'com.github.bumptech.glide:glide:3.7.0'
}

// ADD THIS AT THE BOTTOM
apply plugin: 'com.google.gms.google-services'
````

## Authenticate with Firebase

#### 1. [Register your app](https://github.com/settings/applications/new) as a developer application on GitHub
- set <b><i>Authorization callback URL</i></b> :  (e.g. yourscheme://authorization-callback.url)

![](/image/New_OAuth_Application.png)

- Get your app's OAuth 2.0 <b><i>Client ID</i></b> and <b><i>Client Secret</i></b>.

![](/image/OAuth_Application_Settings.png)

#### 2. Generate new token
- get your <b><i>Personal access tokens</i></b>

![](/image/Personal_Access_Tokens.png)

![](/image/New_personal_access_token.png)

![](/image/Personal_Access_Tokens_Copy.png)

#### 3. Enable GitHub authentication in the Firebase Console
- In the Firebase console, open the Auth Section
- On the Sign in method tab, enable the GitHub sign-in method and specify the OAuth 2.0 Client ID and Client Secret you got from GitHub.
- We are not going to use Firebase authorization Callback URL. Make your own callback url.<br/>
(e.g. yourscheme://authorization-callback.url)<br/>
(But, Make sure your url is set as your Authorization callback URL in yout app's settings page on your GitHub app's config.)

![](/image/Firebase_console_auth.png)

![](/image/Firebase_console_setting.png)

#### 4. Integrate GitHub authentication<br/>
- Integrate GitHub authentication into your app by following the [developer's documentation](https://developer.github.com/apps/building-integrations/setting-up-and-registering-oauth-apps/about-authorization-options-for-oauth-apps/).<br/>

- Set up an intent filter in your app to handle the OAuth 2.0 callback from GitHub.<br/>(At the end of the GitHub sign-in flow, you will receive an OAuth 2.0 access token.)

````xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ...>

    <uses-permission android:name="android.permission.INTERNET" />

    <application ...>
        
        <activity
            android:name=".SignInOutActivity"
            android:launchMode="singleTask">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity
            android:name=".RedirectedActivity"
            android:theme="@android:style/Theme.Translucent.NoTitleBar">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data android:scheme="yourscheme" />
            </intent-filter>
        </activity>
        
    </application>

</manifest>
````

- Use the <b>web application flow</b>

  1)  Users are redirected to request their GitHub identity
  ````Java
    // SignInOutActivity

    private void signIn() {

        // 1. Users are redirected to request their GitHub identity
        // GET http://github.com/login/oauth/authorize
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("github.com")
                .addPathSegment("login")
                .addPathSegment("oauth")
                .addPathSegment("authorize")
                .addQueryParameter("client_id", getString(R.string.github_client_id))
                .addQueryParameter("redirect_uri", getString(R.string.github_redirect_url))
                .addQueryParameter("state", getRandomString())
                .addQueryParameter("scope", "user:email")
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(httpUrl.toString()));
        startActivityForResult(intent, StatusCode.REQUEST_GITHUB_REDIRECT);
    }
  ````
  2) Users are redirected back to your site by GitHub
  ````Java
    // RedirectedActivity

    public class RedirectedActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // 2. Users are redirected back to your site by GitHub
            Uri uri = getIntent().getData();

            // Called after the GitHub server redirect us to GITHUB_REDIRECT_URL
            if (uri != null && uri.toString().startsWith(getString(R.string.github_redirect_url))) {

                Intent intent = new Intent(this, MainActivity.class);

                intent.putExtra("code", uri.getQueryParameter("code"));
                intent.putExtra("state", uri.getQueryParameter("state"));

                startActivity(intent);

                finish();
            }
        }
    }

  ````
  3) Your GitHub App accesses the API with the user's access token
  ````Java
  // SignInOutActivity

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {

            String code = intent.getStringExtra("code");
            String state = intent.getStringExtra("state");

            if (code != null && state != null) {

                Log.d("RedirectedActivity", "code != null && state != null");

                // POST https://github.com/login/oauth/access_token
                sendPost(code, state);
            }
        }
    }

    private void sendPost(String code, String state) {

        Log.d("GitHubSignInOutActivity", "sendPost()");

        OkHttpClient okHttpClient = new OkHttpClient();

        FormBody form = new FormBody.Builder()
                .add("client_id", getString(R.string.github_client_id))
                .add("client_secret", getString(R.string.github_client_secret))
                .add("code", code)
                .add("redirect_uri", getString(R.string.github_redirect_url))
                .add("state", state)
                .build();

        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(form)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                // onFailure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // e.g. Response form : access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
                String responseBody = response.body().string();
                String[] splittedBody = responseBody.split("=|&");

                if (splittedBody[0].equalsIgnoreCase("access_token")) {

                    signInWithToken(splittedBody[1]);
                } 
            }
        });
    }

    // 3. Use the access token to access the API
    // After a user successfully signs in with GitHub, 
    // exchange the OAuth 2.0 access token for a Firebase credential, 
    // and authenticate with Firebase using the Firebase credential
    private void signInWithToken(String token) {

        // credential object from the token
        AuthCredential credential = GithubAuthProvider.getCredential(token);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Success
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // onFailure
                    }
                });
    }

  ````
  If the call to signInWithCredential succeeds, you can use the getCurrentUser method to get the user's account data.



