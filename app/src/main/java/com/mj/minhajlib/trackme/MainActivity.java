package com.mj.minhajlib.trackme;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mj.minhajlib.trackme.backgound.DownloadImage;
import com.mj.minhajlib.trackme.service.MyService;
import com.mj.minhajlib.trackme.utils.Utils;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private final int RC_PERMISSIONS = 100;
    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private String authProvider;
    private boolean flag;
    private Utils mUtils;

    private ImageButton mIbSwitch;
    private Toolbar mToolbar;
    private TextView mTvEmail;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        updateButton();
    }

    private void updateButton() {
        flag = mUtils.getPref();
        Log.d("tracker","button "+flag);
        if (flag){
            mIbSwitch.setBackgroundResource(R.drawable.ic_switch);
        }else {
            mIbSwitch.setBackgroundResource(R.drawable.ic_switchoff);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();

        mIbSwitch = (ImageButton) findViewById(R.id.ib_switch);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
        setSupportActionBar(mToolbar);
        mIbSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });
        //startTrackingMe();
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser == null) {
                    startSignInFlow();
                } else {
                    Toast.makeText(MainActivity.this, "you are signed in", Toast.LENGTH_SHORT).show();
                    updateUi();
                }
            }
        };
    }

    private void onButtonClick() {
        if (flag) {
            mIbSwitch.setBackgroundResource(R.drawable.ic_switchoff);
            flag = false;
            mUtils.setPref(flag);
            stopTrackingMe();
        } else {
            mIbSwitch.setBackgroundResource(R.drawable.ic_switch);
            flag = true;
            mUtils.setPref(flag);
            startTrackingMe();
        }
    }

    private void stopTrackingMe() {
        stopService(new Intent(this,MyService.class));
    }

    private void startTrackingMe() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,PERMISSIONS,RC_PERMISSIONS);
            return;
        }
        Intent service = new Intent(this, MyService.class);
        startService(service);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startTrackingMe();
            }
        }
    }

    private void initializeVariables() {
        flag = false;
        mUtils = new Utils(this);
    }

    private void updateUi() {
        if (mUser.getPhotoUrl() != null) {
            downloadProfilePhoto(mUser.getPhotoUrl().toString());
        }else {
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }
        mTvEmail.setText("Sign in as:"+mUser.getEmail());
    }

    private void downloadProfilePhoto(String photoUrl) {
        DownloadImage downloadImage = new DownloadImage(this, getSupportActionBar());
        downloadImage.execute(photoUrl);
    }

    private void startSignInFlow() {
        startActivityForResult(
                // Get an instance of AuthUI based on the default app
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                authenticateWithFirebase(response);
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showToast("no network", Toast.LENGTH_SHORT);
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void authenticateWithFirebase(IdpResponse response) {
        String token = response.getIdpToken();
        authProvider = response.getProviderType();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(token, null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showToast("logged in", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void showToast(String s, int length) {
        Toast.makeText(this, s, length).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.logout) {
            logoutUser();
        }
        return true;
    }

    private void logoutUser() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "your are logout", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}