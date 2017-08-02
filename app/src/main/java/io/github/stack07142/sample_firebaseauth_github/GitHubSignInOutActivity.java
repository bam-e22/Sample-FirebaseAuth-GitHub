package io.github.stack07142.sample_firebaseauth_github;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.math.BigInteger;
import java.util.Random;

import okhttp3.HttpUrl;

public class GitHubSignInOutActivity extends Activity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        int requestCode = bundle.getInt(StatusCode.REQUEST_CODE, StatusCode.NONE);

        // Sign In
        if (requestCode == StatusCode.REQUEST_GITHUB_SIGNIN) {

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
        // Sign Out
        else if (requestCode == StatusCode.REQUEST_GITHUB_SIGNOUT) {

            mAuth.signOut();

            setResult(StatusCode.SUCCESS);
            finish();
        }
    }

    /**
     * An unguessable random string.
     * It is used to protect against cross-site request forgery attacks.
     */
    private String getRandomString() {

        return new BigInteger(130, new Random()).toString(32);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == StatusCode.REQUEST_GITHUB_REDIRECT) {

            Log.d("onActivityResult", "resultcode=" + resultCode);

            setResult(resultCode);
            finish();
        }
    }
}
