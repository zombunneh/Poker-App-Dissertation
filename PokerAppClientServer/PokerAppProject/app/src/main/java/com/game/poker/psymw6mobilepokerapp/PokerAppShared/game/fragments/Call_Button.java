package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.poker.psymw6mobilepokerapp.R;

public class Call_Button extends Fragment {

    private CallButtonViewModel mViewModel;

    public static Call_Button newInstance() {
        return new Call_Button();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.call_button_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CallButtonViewModel.class);
        // TODO: Use the ViewModel
    }

}
