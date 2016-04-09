package com.mahutai.intraffic.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;

import com.mahutai.intraffic.GamePlayActivity;

/**
 * Created by mahutai on 4/9/2016.
 */
public class Player {
    public ImageView view;
    public Drawable image;
    public boolean hit = false;

    public Player(GamePlayActivity act, Drawable image){
        view = new ImageView(act);
        view.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        view.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        this.image = image;
        view.setImageDrawable(this.image);
    }

    public float getX(){
        return view.getX();
    }

    public void setX(float x){
        view.setX(x);
    }

    public float getY(){
        return view.getY();
    }

    public void setY(float Y){
        view.setY(Y);
    }

    public void setImage(Drawable image){
        view.setImageDrawable(image);
    }

    public void setVisible(boolean visible){
        if(visible == true){
            view.setVisibility(View.VISIBLE);
        }else {
            view.setVisibility(View.INVISIBLE);
        }
    }
}
