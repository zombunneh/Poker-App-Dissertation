package com.game.poker.psymw6mobilepokerapp.PokerAppShared;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.mainmenu.MainMenuAccessibleFragment;
import com.game.poker.psymw6mobilepokerapp.R;

public class MainMenu extends AppCompatActivity{
    public static final String TAG = "g53ids-mainmenu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainMenuContainer, MainMenuAccessibleFragment.newInstance())
                    .commitNow();
            //TODO REMEMBER TO CHANGE DEPENDING ON SHAREDPREFS WHEN IMPLEMENTED XXXXXXXXXXXX
        }

    }

}
/*
goals:
be able to login  with google play/guest account
create new account with default values
login with existing account
view profile
join queue for a game
 */