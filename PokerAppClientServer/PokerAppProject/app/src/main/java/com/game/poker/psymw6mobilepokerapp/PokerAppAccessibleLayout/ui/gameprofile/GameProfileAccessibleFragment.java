package com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.gameprofile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.game.poker.psymw6mobilepokerapp.PokerAppService.ServerConnectionService;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameLogin;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameProfile;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.MainMenu;
import com.game.poker.psymw6mobilepokerapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GameProfileAccessibleFragment extends Fragment {

    private SharedPreferences sharedPrefs;
    private GoogleSignInClient mGoogleSignInClient;
    public View view;
    private static int RC_SIGN_IN;
    private Button googleButton;

    public static final String RETRIEVE_INTENT = "retrieve_intent";
    public static final String LINKER_INTENT = "linker_intent";

    public static GameProfileAccessibleFragment newInstance() {
        return new GameProfileAccessibleFragment();
    }

    /**
     * Populates the profile views, sets up the backbutton to return to the main menu
     * Activates and shows the sign out button if the user is signed in with a google account
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.game_profile_accessible_fragment, container, false);

        populateViews(view);

        view.findViewById(R.id.profileBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), MainMenu.class));
            }
        });

        googleButton = view.findViewById(R.id.googleButton);

        int accType = sharedPrefs.getInt(getString(R.string.accountType), 0);

        boolean linkedAccount = sharedPrefs.getBoolean(getString(R.string.linkedAccount), false);

        if(accType == 0)
        {
            googleButton.setVisibility(View.VISIBLE);
            googleButton.setText(getString(R.string.profileSignOutButton));
            googleButton.setContentDescription(getString(R.string.profileSignOutDescription));
            googleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        }
        else if(accType == 1 && !linkedAccount)
        {
            googleButton.setVisibility(View.VISIBLE);
            googleButton.setText(getString(R.string.profileLinkButton));
            googleButton.setContentDescription(getString(R.string.profileLinkDescription));
            googleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectAccount();
                }
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver, new IntentFilter(RETRIEVE_INTENT));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver, new IntentFilter(LINKER_INTENT));

        return view;
    }

    /**
     * Signs the user out of their google account and returns them to the app's login activity
     */
    private void signOut()
    {
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(getContext(), GameLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    /**
     * Links a user who is logged in with a guest account to their google account for persistency across devices and/or instances of the app
     */
    private void connectAccount()
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
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = sharedPrefs.getString(getString(R.string.currentUUID), "");
            ((GameProfile)getActivity()).serviceInstance.linkGoogleAccount(account, id);
            //talk to service, link accs
        } catch (ApiException e) {

        }
    }

    /**
     * Populates the profile fragment views with the profile data stored in the shared prefs
     *
     * @param v The fragment view
     */
    private void populateViews(View v)
    {
        sharedPrefs = getContext().getSharedPreferences(getString(R.string.dataPreferences), Context.MODE_PRIVATE);

        TextView username = v.findViewById(R.id.usernameProfile);
        TextView lastlogin = v.findViewById(R.id.lastLoginProfile);
        TextView loginStreak = v.findViewById(R.id.loginStreakProfile);
        TextView currency = v.findViewById(R.id.currencyProfile);
        TextView handsplayed = v.findViewById(R.id.handsPlayedProfile);
        TextView handswon = v.findViewById(R.id.handsWonProfile);
        TextView winrate = v.findViewById(R.id.winRateProfile);
        TextView maxWinnings = v.findViewById(R.id.maxWinningsProfile);
        TextView maxChips = v.findViewById(R.id.maxChipsProfile);

        String profileNameString = String.format(getString(R.string.profileNameDisplay), sharedPrefs.getString(getString(R.string.username), ""));
        String profileLastLoginString = String.format(getString(R.string.profileLastLogin), sharedPrefs.getString(getString(R.string.last_login), ""));
        String profileLoginStreakString = String.format(getString(R.string.profileLoginStreak), sharedPrefs.getInt(getString(R.string.login_streak), 0));
        String profileCurrencyString = String.format(getString(R.string.profileCurrency), sharedPrefs.getInt(getString(R.string.currency), 0));
        String profileHandsPlayedString = String.format(getString(R.string.profileHandsPlayed), sharedPrefs.getInt(getString(R.string.hands_played), 0));
        String profileHandsWonString = String.format(getString(R.string.profileHandsWon), sharedPrefs.getInt(getString(R.string.hands_won), 0));
        String profileWinRateString = String.format(getString(R.string.profileWinRate), sharedPrefs.getInt(getString(R.string.win_rate), 0));
        String profileMaxWinningsString = String.format(getString(R.string.profileMaxWinnings), sharedPrefs.getInt(getString(R.string.max_winnings), 0));
        String profileMaxChipsString = String.format(getString(R.string.profileMaxChips), sharedPrefs.getInt(getString(R.string.max_chips), 0));

        username.setText(profileNameString);
        lastlogin.setText(profileLastLoginString);
        loginStreak.setText(profileLoginStreakString);
        currency.setText(profileCurrencyString);
        handsplayed.setText(profileHandsPlayedString);
        handswon.setText(profileHandsWonString);
        winrate.setText(profileWinRateString);
        maxWinnings.setText(profileMaxWinningsString);
        maxChips.setText(profileMaxChipsString);
    }

    /**
     * Removes link account button if linked and shows a pop up to confirm linking successful, only shows pop up if link was unsuccessful
     *
     * @param linkSuccess True if account linked, false if not
     */
    public void finishLinkAccount(boolean linkSuccess)
    {
        if(linkSuccess)
        {
            SharedPreferences.Editor edit = sharedPrefs.edit();
            edit.putBoolean(getString(R.string.linkedAccount), true);
            edit.apply();
            Toast.makeText(getContext(), getString(R.string.linkSuccessful), Toast.LENGTH_LONG).show();
            googleButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            SharedPreferences.Editor edit = sharedPrefs.edit();
            edit.putBoolean(getString(R.string.linkedAccount), false);
            edit.apply();
            Toast.makeText(getContext(), getString(R.string.linkUnsuccessful), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Receiver to update the profile when the new version is retrieved
     */
    private final BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");

            switch(message)
            {
                case "profile_retrieved":
                {
                    populateViews(view);
                    break;
                }
                case "account_linked":
                {
                    finishLinkAccount(true);
                    break;
                }
                case "account_link_fail":
                {
                    finishLinkAccount(false);
                    break;
                }
            }
        }
    };
}
