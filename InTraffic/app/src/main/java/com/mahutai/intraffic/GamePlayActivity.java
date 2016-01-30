package com.mahutai.intraffic;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;


public class GamePlayActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener{

    private SimpleGestureFilter detector;
    RelativeLayout mLinearLayout;
    ImageView danny;
    ImageView road;
    ArrayList<ImageView> cars = new ArrayList<ImageView>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        // Detect touched area
        detector = new SimpleGestureFilter(this,this);
        detector.setSwipeMaxDistance(1000);
        detector.setSwipeMinDistance(100);
        detector.setSwipeMinVelocity(20);

        mLinearLayout = new RelativeLayout(this);
        Resources res = this.getApplicationContext().getResources();
        Drawable roadImage = res.getDrawable(R.drawable.road);
        Drawable dannyImage = res.getDrawable(R.drawable.danny);
        Drawable carImage = res.getDrawable(R.drawable.car);

        // Instantiate an ImageView and define its properties
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        road = new ImageView(this);
        road.setImageDrawable(roadImage);
        road.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        road.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mLinearLayout.addView(road);

        danny = new ImageView(this);
        danny.setImageDrawable(dannyImage);
        danny.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        danny.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mLinearLayout.addView(danny);
        danny.setX(0);
        Drawable d = getResources().getDrawable(R.drawable.danny);

        System.out.println("DANNY HEIGHT:                     " + d.getIntrinsicHeight());
        System.out.println("frame height:                " + height);
        danny.setY(height - d.getIntrinsicHeight());
        for(int i = 0; i < 3; i++) {
            ImageView car = new ImageView(this);
            car.setImageDrawable(carImage);
            car.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
            car.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            cars.add(car);
            mLinearLayout.addView(car);
            car.setX(car.getX() + 300*i);
        }

        // Add the ImageView to the layout and set the layout as the content view

        setContentView(mLinearLayout);
/*
        Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        myImage.draw(c);*/
        System.out.println("new thread");
        new Thread(new Runnable() {
            public void run() {
                System.out.println("move cars");
                moveCars(cars);
            }
        }).start();
    }

    public void moveCars(ArrayList<ImageView> cars){
        boolean hasCars = true;
        System.out.println("in");
        float previousTime = System.nanoTime();
        int step = 10000000;
        try {
            System.out.println("sleep");
            Thread.currentThread().sleep(step/1000000);
        } catch(Exception e){
            System.err.println("Error sleeping thread.");
            e.printStackTrace();
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        while(hasCars) {
            if (System.nanoTime() - previousTime > step) {
                for (ImageView car : cars) {
                    car.setY(car.getY() + 5);
                    try {
                        System.out.println("move sleep");
                        Thread.currentThread().sleep(step / 1000000);
                    } catch (Exception e) {
                        System.err.println("Error sleeping thread.");
                        e.printStackTrace();
                    }
                    if (car.getY() > height) {
                      //  cars.remove(car);
                     //   hasCars = !cars.isEmpty();
                    }
                }
            }
        }
        try {
            System.out.println("Thread return");
            Thread.currentThread().interrupt();
            return;
        }catch(Exception e){
            System.err.println("Error interrupting thread.");
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        String str = "";
        System.out.println("SWIPE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT :
                danny.setX(danny.getX() + 50);
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                danny.setX(danny.getX() - 50);
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                danny.setY(danny.getY() + 50);
                break;
            case SimpleGestureFilter.SWIPE_UP :
                danny.setY(danny.getY() - 50);
                break;

        }
   //     Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

}
