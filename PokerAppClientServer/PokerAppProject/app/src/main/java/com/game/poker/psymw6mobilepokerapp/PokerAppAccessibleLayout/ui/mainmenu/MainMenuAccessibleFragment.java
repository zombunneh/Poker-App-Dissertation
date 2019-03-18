package com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.mainmenu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameHelp;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameOptions;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.GameProfile;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameView;
import com.game.poker.psymw6mobilepokerapp.R;

public class MainMenuAccessibleFragment extends Fragment {

    private MainMenuAccessibleViewModel mViewModel;
    public static final String TAG = "g53ids-mainmenu-frag";
    public static final String SERVICE_INTENT = "service_intent";

    public static MainMenuAccessibleFragment newInstance() {
        return new MainMenuAccessibleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.main_menu_accessible_fragment, container, false);

        view.findViewById(R.id.playButton).setOnClickListener(onClick);
        view.findViewById(R.id.helpButton).setOnClickListener(onClick);
        view.findViewById(R.id.profileButton).setOnClickListener(onClick);
        view.findViewById(R.id.friendsButton).setOnClickListener(onClick);
        view.findViewById(R.id.optionsButton).setOnClickListener(onClick);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver, new IntentFilter(SERVICE_INTENT));
        return view;
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
                switch(v.getId())
                {
                    case R.id.playButton:
                        Log.d(TAG, "play");
                        Intent broadcastIntent = new Intent(SERVICE_INTENT);
                        broadcastIntent.putExtra("message", "join_queue");
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(broadcastIntent); // add fragment or something to show we joined queue
                        //intent = new Intent(getContext(),);
                        break;
                    case R.id.helpButton:
                        Log.d(TAG, "help");
                        intent = new Intent(getContext(), GameHelp.class);
                        break;
                    case R.id.profileButton:
                        Log.d(TAG, "profile");
                        intent = new Intent(getContext(), GameProfile.class);
                        startActivity(intent);
                        break;
                    case R.id.friendsButton:
                        Log.d(TAG, "friends");
                        break;
                    case R.id.optionsButton:
                        Log.d(TAG, "options");
                        intent = new Intent(getContext(), GameOptions.class);
                        startActivity(intent);
                        break;
                }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainMenuAccessibleViewModel.class);
        // TODO: Use the ViewModel
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            switch(message)
            {
                case "queue_joined":
                    Log.d(TAG, "queue joined");
                    break;
                case "unable_to_join_queue":
                    Log.d(TAG, "unable to join queue");
                    break;
                default:
                    break;
            }
        }
    };
}
