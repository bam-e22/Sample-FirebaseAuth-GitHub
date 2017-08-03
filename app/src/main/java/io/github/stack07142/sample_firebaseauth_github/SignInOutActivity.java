package io.github.stack07142.sample_firebaseauth_github;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignInOutActivity extends AppCompatActivity {

    private final String TAG = SignInOutActivity.class.getSimpleName();

    private Button signInButton;
    private Button signOutButton;

    private TextView nameTV;
    private TextView emailTV;
    private ImageView profileIV;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // TextView
        nameTV = (TextView) findViewById(R.id.tv_name);
        emailTV = (TextView) findViewById(R.id.tv_email);

        // Image View
        profileIV = (ImageView) findViewById(R.id.iv_profile);

        // Sign In Button
        signInButton = (Button) findViewById(R.id.btn_signin);
        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                signIn();
            }
        });

        // Sign In Button
        signOutButton = (Button) findViewById(R.id.btn_signout);
        signOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                signOut();
            }
        });

        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Firebase AuthStateListener
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                // User is signed in
                if (user != null) {

                    Log.d("SignInOutActivity", "User is signed in");

                    signInButton.setVisibility(View.GONE);
                    signOutButton.setVisibility(View.VISIBLE);

                    nameTV.setText(user.getDisplayName());
                    emailTV.setText(user.getEmail());

                    Glide.with(getBaseContext())
                            .load(user.getPhotoUrl()).into(profileIV);

                }
                // User is signed out
                else {

                    Log.d("SignInOutActivity", "User is signed out");

                    signInButton.setVisibility(View.VISIBLE);
                    signOutButton.setVisibility(View.GONE);

                    nameTV.setText("");
                    emailTV.setText("");
                    profileIV.setImageResource(0);
                }
            }
        };
    } // ~onCreate

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
            } else {

                showResult(StatusCode.SUCCESS);
            }
        }
    }

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

    private void signOut() {

        mAuth.signOut();

        showResult(StatusCode.SUCCESS);
    }

    /**
     * An unguessable random string.
     * It is used to protect against cross-site request forgery attacks.
     */
    private String getRandomString() {

        return new BigInteger(130, new Random()).toString(32);
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

                showResult(StatusCode.FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // e.g. Response form : access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
                String responseBody = response.body().string();
                String[] splittedBody = responseBody.split("=|&");

                if (splittedBody[0].equalsIgnoreCase("access_token")) {

                    signInWithToken(splittedBody[1]);
                } else {

                    showResult(StatusCode.FAIL);
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

                            Log.d(TAG, "signInWithToken() - task is successful");

                            showResult(StatusCode.SUCCESS);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("GitHubSignInOutActivity", "signInWithToken() - signInWithCredential - onFailure");
                        Log.d("GitHubSignInOutActivity", e.getMessage());

                        showResult(StatusCode.FAIL);
                    }
                });
    }

    private void showResult(@StatusCode.Result int resultCode) {

        progressBar.setVisibility(View.GONE);

        if (resultCode == StatusCode.SUCCESS) {

            Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }
}
