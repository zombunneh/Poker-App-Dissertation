package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments;

import android.arch.lifecycle.ViewModelProviders;
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

    private CheckButtonViewModel mViewModel;

    public static Check_Button newInstance() {
        return new Check_Button();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_button_fragment, container, false);

        view.findViewById(R.id.foldButtonCh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameView)getActivity()).getActions().pressedFold();
            }
        });

        view.findViewById(R.id.checkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameView)getActivity()).getActions().pressedCheck();
            }
        });

        view.findViewById(R.id.betButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bet = ((GameView)getActivity()).bet_slider_frag.getBetSlider().getProgress();
                ((GameView)getActivity()).getActions().pressedRaise(bet);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CheckButtonViewModel.class);
        // TODO: Use the ViewModel
    }

}
