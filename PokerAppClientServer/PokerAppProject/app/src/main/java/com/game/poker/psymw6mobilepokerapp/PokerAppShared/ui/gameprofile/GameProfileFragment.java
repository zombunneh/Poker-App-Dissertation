package com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gameprofile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.game.poker.psymw6mobilepokerapp.R;

public class GameProfileFragment extends Fragment {
    public static GameProfileFragment newInstance() {
        return new GameProfileFragment();
    }

    /**
     * Fragment containing the profile elements
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_profile_fragment, container, false);
    }
}
