package com.npcf34.android.cannongame.Classes;

import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by Nick on 4/19/2015.
 */
public class Cannonball extends Point{

    public int cannonballVelocityX; // cannonball's x velocity
    public int cannonballVelocityY; // cannonball's y velocity
    public boolean cannonballOnScreen; // whether cannonball on the screen
    public int cannonballRadius; // cannonball's radius
    public int cannonballSpeed; // cannonball's speed

    public Paint cannonballPaint; // Paint used to draw the cannonball

    public Cannonball() {
        cannonballOnScreen = false;
        cannonballPaint = new Paint();
    }
}
