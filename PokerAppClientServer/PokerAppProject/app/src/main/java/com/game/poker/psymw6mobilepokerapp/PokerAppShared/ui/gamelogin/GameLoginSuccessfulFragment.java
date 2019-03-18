package com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gamelogin;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.MainMenu;
import com.game.poker.psymw6mobilepokerapp.R;

public class GameLoginSuccessfulFragment extends Fragment {

    private GameLoginSuccessfulViewModel mViewModel;
    private SharedPreferences sharedPrefs;

    public static GameLoginSuccessfulFragment newInstance() {
        return new GameLoginSuccessfulFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.game_login_successful_fragment, container, false);

       sharedPrefs = getContext().getSharedPreferences(getString(R.string.dataPreferences), Context.MODE_PRIVATE);
       int streak = sharedPrefs.getInt(getString(R.string.login_streak), 0);
       boolean streakChanged = sharedPrefs.getBoolean(getString(R.string.login_streak_changed), false);

       if(streak != 0 && streakChanged)
       {
           TextView loginStreak = view.findViewById(R.id.loginStreakText);
           int BASE_BONUS = 100000;
           double multiplier = (1.0) + ((streak / 10.0) - 0.1);
           String loginStreakString = String.format(getString(R.string.loginStreakDisplay), streak, (BASE_BONUS * multiplier));
           loginStreak.setText(loginStreakString);
       }

        view.findViewById(R.id.view_to_listen_for_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainMenu.class); // intent to proceed to main game menu
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GameLoginSuccessfulViewModel.class);
        // TODO: Use the ViewModel
    }

}
