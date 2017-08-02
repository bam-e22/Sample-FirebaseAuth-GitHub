package io.github.stack07142.sample_firebaseauth_github;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class StatusCode {

    public static final String REQUEST_CODE = "requestCode";

    // TypeDef - IntDef
    // Constants
    public static final int SUCCESS = 100;
    public static final int FAIL = 101;
    public static final int NONE = 2000;
    public static final int REQUEST_GITHUB_SIGNIN = 1000;
    public static final int REQUEST_GITHUB_SIGNOUT = 1001;
    public static final int REQUEST_GITHUB_REDIRECT = 1002;

    // Declare the @IntDef for these contant
    @IntDef({SUCCESS, FAIL, NONE, REQUEST_GITHUB_SIGNIN, REQUEST_GITHUB_SIGNOUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }

}
