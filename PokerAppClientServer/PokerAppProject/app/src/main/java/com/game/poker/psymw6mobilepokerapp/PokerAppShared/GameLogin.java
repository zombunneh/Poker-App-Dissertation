package com.game.poker.psymw6mobilepokerapp.PokerAppShared;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.game.poker.psymw6mobilepokerapp.PokerAppService.ServerConnectionService;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gamelogin.GameLoginCreateUserFragment;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gamelogin.GameLoginFragment;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gamelogin.GameLoginSuccessfulFragment;
import com.game.poker.psymw6mobilepokerapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.UUID;

/*
    Created by psymw6
    30/01/2019
    GameLogin.java
*/

/*
    Login class
    Functionality:
    present sign in options for new user, retrieve user details if account created, server creates new if not
    if not already user profile set up
    if already signed in show retrieve details and display tap to continue screen
    TODO sign in token send to server for auth
    TODO make silent sign in resulting fragment show instantly without delay 
*/

/*
shared preferences:
    settings preferences
    login preferences
    data/stats preferences
 */
public class GameLogin extends AppCompatActivity {
    private static int RC_SIGN_IN;
    private GoogleSignInClient mGoogleSignInClient;
    public static final String TAG = "g53ids";
    public static final String BROADCAST_INTENT = "login_intent";
    private boolean isLoggedIn = false;

    private ServerConnectionService.ServerBinder serviceBinder;
    private ServerConnectionService serviceInstance;
    private Handler handler = new Handler();
    // sign in status variables
    private boolean loginDetailsUpdated = false;
    private boolean loginCompleted = false;
    private boolean accountNotFound = false;

    private GameLoginFragment signInFragment;

    private SharedPreferences loginPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_login_activity);
        signInFragment = GameLoginFragment.newInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.signInLayout, signInFragment)
                    .commitNow();
        }

        loginPrefs = getSharedPreferences(getString(R.string.loginPreferences), MODE_PRIVATE);
        isLoggedIn = loginPrefs.getBoolean(getString(R.string.isLoggedIn), false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        if(!loginPrefs.getBoolean(getString(R.string.existingUUID), false));
        {
            UUID id = UUID.randomUUID();
            Log.d(TAG, id.toString());
            SharedPreferences.Editor edit = loginPrefs.edit();
            edit.putBoolean(getString(R.string.existingUUID), true);
            edit.putString("", id.toString());
            edit.apply();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, new IntentFilter(BROADCAST_INTENT));
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //explicitly start service so it wont be destroyed
        startService(new Intent(this, ServerConnectionService.class));
        Intent intent = new Intent(this, ServerConnectionService.class);
        this.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //change function if this is not null

        if(isLoggedIn)
        {
            mGoogleSignInClient.silentSignIn()
                    .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            handleSignInResult(task);
                        }
                    });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(connection!= null)
        {
            unbindService(connection);
            connection = null;
        }
        if(serviceInstance != null)
        {
            serviceInstance = null;
        }
        Log.d(TAG, "activity stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "activity destroyed");
    }

    public void signIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.d(TAG, "onActivityResult");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "attempting to sign in");
            //TODO send idtoken to server to retrieve data :3
            signInFlow(account, null);
            SharedPreferences.Editor editLoginPrefs = loginPrefs.edit();
            editLoginPrefs.putBoolean(getString(R.string.isLoggedIn), true);
            editLoginPrefs.apply();
            // Signed in successfully, show authenticated UI.
            //authenticated ui fragment
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            String error = GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode());
            Log.d(TAG, "detailed error:" + error);
            TextView tv = findViewById(R.id.loginText);
            tv.setText(R.string.loginFailed);
            //sign in failed fragment
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "service connected");
            serviceBinder = (ServerConnectionService.ServerBinder) service;
            serviceInstance = serviceBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "service disconnected");
            serviceBinder = null;
        }
    };

    private final BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d(TAG, "message received: " + message);
            switch(message)
            {
                case "loginDetailsUpdated":
                    loginDetailsUpdated = true;
                    break;
                case "accountNotFound":
                    accountNotFound = true;
                    break;
                default:
                    break;
            }
        }
    };

    public void signInFlow(GoogleSignInAccount account, String user_id)
    {
        class signInRunnable implements Runnable {
            private GoogleSignInAccount account;
            private String user_id;
            private int accountType;

            public signInRunnable(GoogleSignInAccount account)
            {
                this.account = account;
                accountType = 0;
            }

            public signInRunnable(String uid)
            {
                this.user_id = uid;
                accountType = 1;
            }

            @Override
            public void run() {
                //logic flow for logging in
                Log.d(TAG, "sign in flow started");
                boolean creatingAccount = false;
                while(serviceInstance == null)
                {
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch(InterruptedException e)
                    {
                        Log.d(TAG, e.toString());
                    }
                    //wait for service instance to be initialised
                }
                if(serviceInstance.isServerConnected())
                {
                    //login with google acc
                    if(accountType == 0)
                        serviceInstance.retrieveUserDetailsOnLogin(account);
                    else
                        serviceInstance.retrieveUserDetailsOnLogin(user_id);
                    while(!loginCompleted)
                    {

                        if(!accountNotFound)
                        {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    TextView tv = findViewById(R.id.loginText);
                                    tv.setText(R.string.attemptUserRetrieval);
                                }
                            });
                        }


                        if(loginDetailsUpdated)
                        {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.signInLayout, GameLoginSuccessfulFragment.newInstance())
                                    .commitNow();
                                }
                            });
                            loginCompleted = true;
                        }
                        else if(accountNotFound)
                        {
                            //fragment for account creation
                            if(!creatingAccount)
                            {
                                Log.d(TAG, "account not found");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.createUserBox, GameLoginCreateUserFragment.newInstance())
                                                .commitNow();
                                        getSupportFragmentManager().beginTransaction()
                                                .hide(signInFragment)
                                                .commitNow();
                                    }
                                });
                                creatingAccount = true;
                            }


                            //take input of username
                            //close fragment
                        }
                        try
                        {
                            Thread.sleep(500);
                        }
                        catch(InterruptedException e)
                        {
                            Log.d(TAG, e.toString());
                        }
                    }
                }
                else
                {
                    //update ui with login failed if server cannot be connected to
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv = findViewById(R.id.loginText);
                            tv.setText(R.string.loginFailed);
                        }
                    });
                }
            }

        }

        if(account != null)
        {
            Log.d(TAG, "sign  in flow thread created");
            Thread t = new Thread(new signInRunnable(account));
            t.start();
        }
        else if(user_id != null)
        {
            Thread t = new Thread(new signInRunnable(user_id));
            t.start();
        }

    }

}