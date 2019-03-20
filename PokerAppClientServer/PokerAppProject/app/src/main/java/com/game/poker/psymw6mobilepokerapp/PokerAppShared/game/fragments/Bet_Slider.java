package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.game.poker.psymw6mobilepokerapp.R;

public class Bet_Slider extends Fragment {

    private BetSliderViewModel mViewModel;
    private SeekBar betSlider;

    public static Bet_Slider newInstance() {
        return new Bet_Slider();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bet_slider_fragment, container, false);

        betSlider = view.findViewById(R.id.betSlider);

        betSlider.setContentDescription(getString(R.string.betSliderDescription));

        betSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    public SeekBar getBetSlider()
    {
        return betSlider;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BetSliderViewModel.class);
        // TODO: Use the ViewModel
    }

}
