package com.game.poker.psymw6mobilepokerapp.PokerAppShared;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.gameprofile.GameProfileAccessibleFragment;
import com.game.poker.psymw6mobilepokerapp.R;

public class GameProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_profile_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.gameProfileContainer, GameProfileAccessibleFragment.newInstance())
                    .commitNow();
            //TODO REMEMBER TO CHANGE DEPENDING ON SHAREDPREFS WHEN IMPLEMENTED XXXXXXXXXXXX
        }
    }
}
