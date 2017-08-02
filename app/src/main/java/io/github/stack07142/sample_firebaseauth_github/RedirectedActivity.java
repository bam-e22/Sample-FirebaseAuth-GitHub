package io.github.stack07142.sample_firebaseauth_github;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GithubAuthProvider;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RedirectedActivity extends Activity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        // 2. Users are redirected back to your site by GitHub
        Uri uri = getIntent().getData();

        // Called after the GitHub server redirect us to GITHUB_REDIRECT_URL
        if (uri != null && uri.toString().startsWith(getString(R.string.github_redirect_url))) {

            String code = uri.getQueryParameter("code");
            String state = uri.getQueryParameter("state");

            if (code != null && state != null) {

                Log.d("GitHubSignInOutActivity", "code != null && state != null");

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
                setResult(StatusCode.FAIL);
                finish();
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
    private void signInWithToken(String token) {

        // credential object from the token
        AuthCredential credential = GithubAuthProvider.getCredential(token);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Log.d("GitHubSignInOutActivity", "signInWithToken() - task is successful");

                            setResult(StatusCode.SUCCESS);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("GitHubSignInOutActivity", "signInWithToken() - signInWithCredential - onFailure");
                        Log.d("GitHubSignInOutActivity", e.getMessage());

                        // onFailure
                        RedirectedActivity.this.setResult(StatusCode.FAIL);
                        finish();
                    }
                });
    }

}
