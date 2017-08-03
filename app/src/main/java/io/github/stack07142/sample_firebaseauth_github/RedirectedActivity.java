package io.github.stack07142.sample_firebaseauth_github;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class RedirectedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Users are redirected back to your site by GitHub
        Uri uri = getIntent().getData();

        // Called after the GitHub server redirect us to GITHUB_REDIRECT_URL
        if (uri != null && uri.toString().startsWith(getString(R.string.github_redirect_url))) {

            Intent intent = new Intent(this, SignInOutActivity.class);

            intent.putExtra("code", uri.getQueryParameter("code"));
            intent.putExtra("state", uri.getQueryParameter("state"));

            startActivity(intent);

            finish();
        }
    }
}
