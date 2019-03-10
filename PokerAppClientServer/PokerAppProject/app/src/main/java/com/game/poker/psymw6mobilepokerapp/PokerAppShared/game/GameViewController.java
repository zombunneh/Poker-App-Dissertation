package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game;

import android.content.Context;
import android.util.Log;

import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.Card;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerMove;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUser;
import com.game.poker.psymw6mobilepokerapp.PokerAppMessage.PlayerUserMove;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameViewController implements Observer {
    private GameViewModel model;
    private Context view;
    private GameView gameView;

    public static final String TAG = "controller :3";

    public GameViewController(GameViewModel model, Context viewContext)
    {
        this.model = model;
        this.view = viewContext;
        gameView = (GameView) view;
    }

    public void updateView(GameViewModel model, final Object arg)
    {
        Log.d(TAG, "update view");
        if(arg instanceof Card[])
        {
            gameView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameView.removeCommunityCards();
                    gameView.setCommunityImageViews();
                }
            });
        }
        else if(arg instanceof Card)
        {
            Log.d(TAG, "turn/river update");
            gameView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameView.setCommunityImageView();
                }
            });
        }
        else if(arg instanceof List)
        {
            //update view + need one more to update individual players join/leave
            Log.d(TAG, "update playerlist");
            gameView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameView.updatePlayers();
                }
            });

        }
        else if(arg instanceof PlayerMove)
        {
            final PlayerMove tempMove = ((PlayerMove)arg);
            PlayerUser tempPlayer = model.getPlayer(tempMove.id);
            switch(tempMove.move)
            {
                case AWAY:
                {
                    /*gameView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameView.broadcastMove(tempMove);
                            gameView.setAway(tempMove.id);
                        }
                    });*/
                }
                    break;
                case EXIT:
                {
                    gameView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameView.broadcastMove(tempMove);
                            gameView.removePlayer(tempMove.id);
                        }
                    });
                }
                    break;
                case CALL:
                {
                    gameView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameView.broadcastMove(tempMove);
                        }
                    });
                }
                    break;
                case CHECK:
                {
                    gameView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameView.broadcastMove(tempMove);
                        }
                    });
                }
                    break;
                case FOLD:
                {
                    gameView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameView.broadcastMove(tempMove);
                        }
                    });
                }
                    break;
                case RAISE:
                {
                    gameView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameView.broadcastMove(tempMove);
                        }
                    });
                }
                    break;
                case BLIND:
                {
                    /*gameView.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameView.broadcastMove(tempMove);
                        }
                    });*/
                }
                    break;
            }
        }
        else if(arg instanceof GameViewModel.State)
        {
            if(arg == GameViewModel.State.CALL)
            {
                Log.d(TAG, "state = call");
                gameView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.addCallFrag();
                        gameView.addSliderFrag();
                    }
                });
            }
            if(arg == GameViewModel.State.CHECK)
            {
                Log.d(TAG, "state = check");
                gameView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.addCheckFrag();
                        gameView.addSliderFrag();
                    }
                });
            }
            if(arg == GameViewModel.State.READY)
            {
                Log.d(TAG, "state = ready");
                gameView.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameView.hideCheckFrag();
                        gameView.hideSliderFrag();
                        gameView.hideCallFrag();
                    }
                });
            }
        }
    }

    public void updateView(GameViewModel.MyPlayer player, final Object arg)
    {
        if(arg instanceof Card[])
        {
            gameView.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameView.setCardImageViews();
                }
            });
        }
    }

    public void updateView(GameViewModel.Bet bet, final Object arg)
    {

    }

    public void winnerList(final List<PlayerUser> winners, final int pot)
    {
        gameView.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameView.displayWinners(winners, pot);
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "change observed");
        if(o instanceof GameViewModel)
        {
            this.updateView((GameViewModel) o, arg);
        }
        else if(o instanceof GameViewModel.Bet)
        {
            this.updateView((GameViewModel.Bet) o, arg);
        }
        else if(o instanceof GameViewModel.MyPlayer)
        {
            this.updateView((GameViewModel.MyPlayer) o, arg);
        }
    }
}
