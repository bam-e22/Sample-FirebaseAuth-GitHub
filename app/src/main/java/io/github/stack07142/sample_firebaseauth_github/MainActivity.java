package io.github.stack07142.sample_firebaseauth_github;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button signInButton;
    private Button signOutButton;

    private TextView nameTV;
    private TextView emailTV;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView
        nameTV = (TextView) findViewById(R.id.tv_name);
        emailTV = (TextView) findViewById(R.id.tv_email);

        // Sign In Button
        signInButton = (Button) findViewById(R.id.btn_signin);

        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });

        // Sign In Button
        signOutButton = (Button) findViewById(R.id.btn_signout);

        signOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
