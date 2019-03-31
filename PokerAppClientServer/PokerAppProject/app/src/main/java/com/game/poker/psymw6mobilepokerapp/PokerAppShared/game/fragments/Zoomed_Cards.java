package com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.game.poker.psymw6mobilepokerapp.PokerAppObjects.ClientCard;
import com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameView;
import com.game.poker.psymw6mobilepokerapp.R;

import static com.game.poker.psymw6mobilepokerapp.PokerAppShared.game.GameView.decodeSampledBitmapFromResource;

public class Zoomed_Cards extends Fragment {

    private ImageView[] zoomedCards;

    private int type;

    /**
     * Creates a new instance of the fragment for a card setup
     *
     * @param type 0 for zoomed hand cards, 1 for zoomed community cards
     * @return A new instance of the fragment
     */
    public static Zoomed_Cards newInstance(int type) {
        Zoomed_Cards zoom = new Zoomed_Cards();
        Bundle args = new Bundle();
        args.putInt("type", type);
        zoom.setArguments(args);
        return zoom;
    }

    /**
     * Sets up the fragment view to have larger versions of either hand or community cards depending on the type arg
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zoomed_cards_fragment, container, false);

        Bundle args = getArguments();
        int type = args.getInt("type", 0);

        view.getBackground().setAlpha(85);

        zoomedCards = new ImageView[5];

        zoomedCards[0] = view.findViewById(R.id.zoomCard1);
        zoomedCards[1] = view.findViewById(R.id.zoomCard2);
        zoomedCards[2] = view.findViewById(R.id.zoomCard3);
        zoomedCards[3] = view.findViewById(R.id.zoomCard4);
        zoomedCards[4] = view.findViewById(R.id.zoomCard5);

        for(ImageView imageView : zoomedCards)
        {
            imageView.setOnClickListener(listener);
        }

        view.findViewById(R.id.zoomedCardsLayout).setOnClickListener(listener);

        if(type == 0)
        {
            int[][] cards = new int[2][2];

            for(int i = 0; i < 2; i++)
            {
                cards[i][0] = ((GameView)getActivity()).model.myPlayer.getMyHand()[i].getCardSuit().ordinal();
                cards[i][1] = ((GameView)getActivity()).model.myPlayer.getMyHand()[i].getCardRank().ordinal();
            }

            Bitmap handCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards_large, 390, 500);
            ClientCard handCardBitmap = new ClientCard(handCardBitmap1, 0, 0, cards[0][0], cards[0][1]);

            Bitmap resized = getResizedBitmap(handCardBitmap.getBitmap(), 400, 700);
            zoomedCards[0].setImageBitmap(resized);

            String card1 = String.format(getString(R.string.cardDescription),
                    ((GameView)getActivity()).model.myPlayer.getMyHand()[0].getCardRank().toString(),
                    ((GameView)getActivity()).model.myPlayer.getMyHand()[0].getCardSuit().toString());
            zoomedCards[0].setContentDescription(card1);

            handCardBitmap.update(cards[1][0], cards[1][1]);
            resized = getResizedBitmap(handCardBitmap.getBitmap(), 400, 700);
            zoomedCards[1].setImageBitmap(resized);

            String card2 = String.format(getString(R.string.cardDescription),
                    ((GameView)getActivity()).model.myPlayer.getMyHand()[1].getCardRank().toString(),
                    ((GameView)getActivity()).model.myPlayer.getMyHand()[1].getCardSuit().toString());
            zoomedCards[1].setContentDescription(card2);
        }
        else if(type == 1)
        {
            int[][] cards = new int[5][2];

            for(int i = 0; i < 5; i++) {
                if(((GameView)getActivity()).model.getCommunityCards()[i] != null)
                {
                    cards[i][0] = ((GameView)getActivity()).model.getCommunityCards()[i].getCardSuit().ordinal();
                    cards[i][1] = ((GameView)getActivity()).model.getCommunityCards()[i].getCardRank().ordinal();
                }
            }

            Bitmap communityCardBitmap1 = decodeSampledBitmapFromResource(getResources(), R.drawable.playing_cards_large, 390, 500);
            ClientCard communityCardBitmap = new ClientCard(communityCardBitmap1, 0, 0, cards[0][0], cards[0][1]);

            for(int i = 0; i < 5; i++)
            {
                if(((GameView)getActivity()).model.getCommunityCards()[i] != null)
                {
                    Bitmap resized = getResizedBitmap(communityCardBitmap.getBitmap(), 400, 700);
                    zoomedCards[i].setImageBitmap(resized);
                    if(i!=4)
                    {
                        communityCardBitmap.update(cards[i+1][0], cards[i+1][1]);
                    }
                    String card = String.format(getString(R.string.cardDescription),
                            ((GameView)getActivity()).model.getCommunityCards()[i].getCardRank().toString(),
                            ((GameView)getActivity()).model.getCommunityCards()[i].getCardSuit().toString());
                    zoomedCards[i].setContentDescription(card);
                }
            }
        }

        return view;
    }

    /**
     * Resizes bitmaps using a matrix to provide a scaled version of supplied image
     *
     * @param bm The bitmap to resize
     * @param newWidth The new width of the bitmap
     * @param newHeight The new height of the bitmap
     * @return The resized bitmap
     */
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(Zoomed_Cards.this).commitNow();
        }
    };
}
