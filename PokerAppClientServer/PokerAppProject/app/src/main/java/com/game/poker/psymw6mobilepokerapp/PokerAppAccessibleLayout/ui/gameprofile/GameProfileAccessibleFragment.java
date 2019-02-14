package com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.gameprofile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.game.poker.psymw6mobilepokerapp.R;

public class GameProfileAccessibleFragment extends Fragment {

    private GameProfileAccessibleViewModel mViewModel;
    private SharedPreferences sharedPrefs;

    public static GameProfileAccessibleFragment newInstance() {
        return new GameProfileAccessibleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_profile_accessible_fragment, container, false);

        populateViews(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GameProfileAccessibleViewModel.class);
        // TODO: Use the ViewModel
    }

    private void populateViews(View v)
    {
        sharedPrefs = getContext().getSharedPreferences(getString(R.string.dataPreferences), Context.MODE_PRIVATE);

        TextView username = v.findViewById(R.id.usernameProfile);
        TextView currency = v.findViewById(R.id.currencyProfile);
        TextView handsplayed = v.findViewById(R.id.handsPlayedProfile);
        TextView handswon = v.findViewById(R.id.handsWonProfile);
        TextView winrate = v.findViewById(R.id.winRateProfile);
        TextView maxWinnings = v.findViewById(R.id.maxWinningsProfile);
        TextView maxChips = v.findViewById(R.id.maxChipsProfile);

        username.setText(sharedPrefs.getString(getString(R.string.username), ""));
        int banana = sharedPrefs.getInt(getString(R.string.currency), 0);
        Log.d("g53ids", "int: " + banana);
        currency.setText(Integer.toString(
                sharedPrefs.getInt(
                getString(R.string.currency),
                0)));
        handsplayed.setText(Integer.toString(
                sharedPrefs.getInt(
                getString(R.string.hands_played),
                0)));
        handswon.setText(Integer.toString(
                sharedPrefs.getInt(
                getString(R.string.hands_won),
                0)));
        winrate.setText(Integer.toString(
                sharedPrefs.getInt(
                        getString(R.string.win_rate),
                        0)));
        maxWinnings.setText(Integer.toString(
                sharedPrefs.getInt(
                        getString(R.string.max_winnings),
                        0)));
        maxChips.setText(Integer.toString(
                sharedPrefs.getInt(
                        getString(R.string.max_chips),
                        0)));

    }

}
