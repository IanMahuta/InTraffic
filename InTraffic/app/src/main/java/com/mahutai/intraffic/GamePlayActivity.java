package com.mahutai.intraffic;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

import com.mahutai.intraffic.util.Car;
import com.mahutai.intraffic.util.Player;

import java.util.ArrayList;


public class GamePlayActivity extends Activity implements SimpleGestureFilter.SimpleGestureListener{

    private SimpleGestureFilter detector;
    RelativeLayout mLinearLayout;

    public Player danny;
    Drawable dannyImage;
    static int moveDistance = 75;

    ImageView road;

    ArrayList<Car> cars;
    Drawable carImage;
    int startingCars = 3;
    private Thread carThread;
    private Runnable carRunnable;

    boolean shouldContinue = true;
    private Handler handler;
    int step = 10000000;

    boolean backPressed = false;

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
        dannyImage = res.getDrawable(R.drawable.danny);
        carImage = res.getDrawable(R.drawable.car);

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

        danny = new Player(GamePlayActivity.this, dannyImage);
        mLinearLayout.addView(danny.view);

        Drawable d = getResources().getDrawable(R.drawable.danny);
        System.out.println("DANNY HEIGHT:                     " + d.getIntrinsicHeight());
        System.out.println("frame height:                " + height);
        danny.setY(height - (d.getIntrinsicHeight() * 2));
        danny.setX(width/2);

        // Add the ImageView to the layout and set the layout as the content view

        setContentView(mLinearLayout);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(!backPressed) {
                    if (msg.what == 0) {
                        Toast.makeText(GamePlayActivity.this, "You are dead!  Don't play in traffic.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GamePlayActivity.this, "I didn't spawn any more cars.", Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(GamePlayActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                super.handleMessage(msg);
            }

        };

        System.out.println("new thread");
        carRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("move cars");
                System.out.println("in");
                float previousTime = System.nanoTime();

                // spawn starting cars
                cars = new ArrayList<Car>();
                for(int i = 0; i < startingCars; i++) {
                    Car car = new Car(GamePlayActivity.this, carImage);
                    mLinearLayout.addView(car.view);
                    car.setX(car.getX() + 300 * i);
                    car.setY(-500);
                    car.gone = true;
                    car.waitTime = Math.random() * 10000000000.0;
                    cars.add(car);
                }

                try {
                    Thread.currentThread().sleep(step/1000000);
                } catch(Exception e){
                    System.err.println("Error sleeping thread.");
                    e.printStackTrace();
                }
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                float dannyX = danny.getX();
                float dannyY = danny.getY();
                while(shouldContinue && !danny.hit) {
                    if (System.nanoTime() - previousTime > step) {
                        dannyX = danny.getX();
                        dannyY = danny.getY();
                        for (Car car : cars) {
                            if(car.waitTime > 0){
                                car.waitTime -= step;
                            }else if (car.gone){
                                car.setY(-500);
                                car.gone = false;
                            }else{
                                car.setY(car.getY() + 5);
                                if ((Math.abs(car.getY() - dannyY) < 350) && (Math.abs(car.getX() - dannyX) < 125)) {
                                    danny.hit = true;
                                }
                                try {
                                    // System.out.println("move sleep");
                                    Thread.currentThread().sleep(step / 1000000);
                                } catch (Exception e) {
                                    System.err.println("Error sleeping thread.");
                                    e.printStackTrace();
                                }
                                if (car.getY() > height) {
                                    car.gone = true;
                                    car.waitTime = Math.random() * 10000000000.0;
                                }
                            }
                        }
                    }
                }
                cars.clear();
                try {
                    System.out.println("Thread return");
                    Message msg = Message.obtain();
                    if(danny.hit) {
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }else{
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    return;
                }catch(Exception e){
                    System.err.println("Error interrupting thread.");
                    e.printStackTrace();
                }
            }
        };
        carThread = new Thread(carRunnable);
        shouldContinue = true;
        carThread.start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT :
                danny.setX(danny.getX() + moveDistance);
                break;
            case SimpleGestureFilter.SWIPE_LEFT :
                danny.setX(danny.getX() - moveDistance);
                break;
            case SimpleGestureFilter.SWIPE_DOWN :
                danny.setY(danny.getY() + moveDistance);
                break;
            case SimpleGestureFilter.SWIPE_UP :
                danny.setY(danny.getY() - moveDistance);
                break;

        }
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            shouldContinue = false;
            backPressed = true;
            try {
                Thread.currentThread().sleep(2*step / 1000000);
                carThread.interrupt();
                handler.removeCallbacks(carRunnable);
                System.out.println("Dissapear");
                for(Car car : cars){
                    car.setVisible(false);
                }
                cars.clear();
            } catch (Exception e) {
                System.err.println("Error closing thread.");
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
