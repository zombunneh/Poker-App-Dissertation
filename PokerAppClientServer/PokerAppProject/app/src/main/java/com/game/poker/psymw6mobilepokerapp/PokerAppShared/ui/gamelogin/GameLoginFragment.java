package com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gamelogin;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameLogin;
import com.game.poker.psymw6mobilepokerapp.R;
import com.google.android.gms.common.SignInButton;

public class GameLoginFragment extends Fragment {

    private GameLoginViewModel mViewModel;

    public static GameLoginFragment newInstance() {
        return new GameLoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_login_fragment, container, false);

        final TextView tv = view.findViewById(R.id.loginText);
        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GameLogin)getActivity()).signIn();
                tv.setText(R.string.loggingIn);
            }
        });

        view.findViewById(R.id.guestLoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getContext().getSharedPreferences(getString(R.string.loginPreferences), Context.MODE_PRIVATE);
                String user_id = prefs.getString(getString(R.string.currentUUID), "");
                ((GameLogin)getActivity()).signInFlow(null, user_id);
                tv.setText(R.string.loggingIn);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GameLoginViewModel.class);
        // TODO: Use the ViewModel
    }

}
