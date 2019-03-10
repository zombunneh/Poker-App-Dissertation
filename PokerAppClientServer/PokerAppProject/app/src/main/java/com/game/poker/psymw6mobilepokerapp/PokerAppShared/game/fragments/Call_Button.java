package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameView;
import com.game.poker.psymw6mobilepokerapp.R;

public class Call_Button extends Fragment {

    private CallButtonViewModel mViewModel;

    public static Call_Button newInstance() {
        return new Call_Button();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.call_button_fragment, container, false);

        view.findViewById(R.id.foldButtonCa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameView)getActivity()).getActions().pressedFold();
            }
        });

        view.findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameView)getActivity()).getActions().pressedCall();
            }
        });

        view.findViewById(R.id.raiseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bet = ((GameView)getActivity()).bet_slider_frag.getBetSlider().getProgress() + ((GameView)getActivity()).getMinValue();
                ((GameView)getActivity()).getActions().pressedRaise(bet);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CallButtonViewModel.class);
        // TODO: Use the ViewModel
    }

}
