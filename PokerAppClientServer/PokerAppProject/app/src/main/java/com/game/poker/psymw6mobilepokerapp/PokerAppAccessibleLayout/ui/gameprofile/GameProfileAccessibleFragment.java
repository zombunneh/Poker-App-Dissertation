package com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.gameprofile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameLogin;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.MainMenu;
import com.game.poker.psymw6mobilepokerapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GameProfileAccessibleFragment extends Fragment {

    private GameProfileAccessibleViewModel mViewModel;
    private SharedPreferences sharedPrefs;
    private GoogleSignInClient mGoogleSignInClient;

    public static GameProfileAccessibleFragment newInstance() {
        return new GameProfileAccessibleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_profile_accessible_fragment, container, false);

        populateViews(view);

        view.findViewById(R.id.profileBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), MainMenu.class));
            }
        });

        Button signOutButton = view.findViewById(R.id.googleSignOutButton);

        int accType = sharedPrefs.getInt(getString(R.string.accountType), 0);

        if(accType == 0)
        {
            signOutButton.setVisibility(View.VISIBLE);
            signOutButton.setOnClickListener(new View.OnClickListener() {
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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GameProfileAccessibleViewModel.class);
        // TODO: Use the ViewModel
    }

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
}
