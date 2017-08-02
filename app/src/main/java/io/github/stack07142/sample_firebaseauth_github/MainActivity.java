package io.github.stack07142.sample_firebaseauth_github;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

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

                Intent signInIntent = new Intent(getBaseContext(), GitHubSignInOutActivity.class);
                signInIntent.putExtra(StatusCode.REQUEST_CODE, StatusCode.REQUEST_GITHUB_SIGNIN);

                startActivityForResult(signInIntent, StatusCode.REQUEST_GITHUB_SIGNIN);
            }
        });

        // Sign In Button
        signOutButton = (Button) findViewById(R.id.btn_signout);

        signOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                Intent signOutIntent = new Intent(getBaseContext(), GitHubSignInOutActivity.class);
                signOutIntent.putExtra(StatusCode.REQUEST_CODE, StatusCode.REQUEST_GITHUB_SIGNOUT);

                startActivityForResult(signOutIntent, StatusCode.REQUEST_GITHUB_SIGNOUT);
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

                    Log.d("MainActivity", "User is signed in");

                    signInButton.setVisibility(View.GONE);
                    signOutButton.setVisibility(View.VISIBLE);

                    nameTV.setText(user.getDisplayName());
                    emailTV.setText(user.getEmail());

                    Glide.with(getBaseContext())
                            .load(user.getPhotoUrl()).into(profileIV);

                }
                // User is signed out
                else {

                    Log.d("MainActivity", "User is signed out");

                    signInButton.setVisibility(View.VISIBLE);
                    signOutButton.setVisibility(View.GONE);

                    nameTV.setText("");
                    emailTV.setText("");
                    profileIV.setImageResource(0);
                }
            }
        };
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        progressBar.setVisibility(View.GONE);

        if (requestCode == StatusCode.REQUEST_GITHUB_SIGNIN) {

            if (resultCode == StatusCode.SUCCESS) {

                Toast.makeText(this, "Sign In : Success", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Sign In : FAIL", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == StatusCode.REQUEST_GITHUB_SIGNOUT) {

            if (resultCode == StatusCode.SUCCESS) {

                Toast.makeText(this, "Sign Out : Success", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Sign Out : Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
