package com.game.poker.psymw6mobilepokerapp.PokerAppShared;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.game.poker.psymw6mobilepokerapp.PokerAppAccessibleLayout.ui.mainmenu.MainMenuAccessibleFragment;
import com.game.poker.psymw6mobilepokerapp.R;

public class MainMenu extends AppCompatActivity{
    public static final String TAG = "g53ids-mainmenu";

    /**
     * Loads the main menu fragment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onWindowFocusChanged(true);

        setContentView(R.layout.main_menu_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainMenuContainer, MainMenuAccessibleFragment.newInstance())
                    .commitNow();
            //TODO REMEMBER TO CHANGE DEPENDING ON SHAREDPREFS WHEN IMPLEMENTED XXXXXXXXXXXX
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}