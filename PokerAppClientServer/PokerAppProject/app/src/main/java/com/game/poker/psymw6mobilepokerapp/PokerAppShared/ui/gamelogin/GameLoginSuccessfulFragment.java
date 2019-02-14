package com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gamelogin;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.MainMenu;
import com.game.poker.psymw6mobilepokerapp.R;

public class GameLoginSuccessfulFragment extends Fragment {

    private GameLoginSuccessfulViewModel mViewModel;

    public static GameLoginSuccessfulFragment newInstance() {
        return new GameLoginSuccessfulFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.game_login_successful_fragment, container, false);

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
