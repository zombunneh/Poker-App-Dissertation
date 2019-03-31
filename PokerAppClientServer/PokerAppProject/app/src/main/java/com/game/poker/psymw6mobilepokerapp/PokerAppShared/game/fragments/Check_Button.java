package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameView;
import com.game.poker.psymw6mobilepokerapp.R;

public class Check_Button extends Fragment {

    public static Check_Button newInstance() {
        return new Check_Button();
    }

    /**
     * Sets up buttons for the fragment to access the game model to notify button presses
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_button_fragment, container, false);

        view.findViewById(R.id.foldButtonCh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameView)getActivity()).getActions().pressedFold();
                ((GameView)getActivity()).setNotTurn();
            }
        });

        view.findViewById(R.id.checkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameView)getActivity()).getActions().pressedCheck();
                ((GameView)getActivity()).setNotTurn();
            }
        });

        view.findViewById(R.id.betButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bet = ((GameView)getActivity()).bet_slider_frag.getBetSlider().getProgress() + ((GameView)getActivity()).getMinValue();
                ((GameView)getActivity()).getActions().pressedRaise(bet);
                ((GameView)getActivity()).setNotTurn();
            }
        });

        return view;
    }
}
