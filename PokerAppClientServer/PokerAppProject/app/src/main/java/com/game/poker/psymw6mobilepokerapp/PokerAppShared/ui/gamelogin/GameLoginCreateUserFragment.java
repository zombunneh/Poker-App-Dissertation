package com.game.poker.psymw6mobilepokerapp.PokerAppShared.ui.gamelogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.game.poker.psymw6mobilepokerapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameLoginCreateUserFragment extends Fragment {

    public static final String CREATE_USER_INTENT = "create_user";

    public GameLoginCreateUserFragment() {
        // Required empty public constructor
    }

    public static GameLoginCreateUserFragment newInstance() {
        return new GameLoginCreateUserFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.game_login_create_user_fragment, container, false);

        final TextView tv = view.findViewById(R.id.username_entry);
        view.findViewById(R.id.create_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv.getText().toString().equals("") )
                {
                    Toast.makeText(getContext(), "Invalid username", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Log.d("g53ids-createuserfrag", tv.getText().toString());
                    Intent intent = new Intent(CREATE_USER_INTENT);
                    intent.putExtra("message", tv.getText().toString());
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                }
            }
        });

        return view;
    }


}
