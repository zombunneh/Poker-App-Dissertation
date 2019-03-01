package com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.gameview;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.poker.psymw6mobilepokerapp.R;

public class GameViewAccessibleFragment extends Fragment {

    private GameViewAccessibleViewModel mViewModel;

    public static GameViewAccessibleFragment newInstance() {
        return new GameViewAccessibleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_view_accessible_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GameViewAccessibleViewModel.class);
        // TODO: Use the ViewModel
    }

}
