package com.npcf34.android.cannongame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.npcf34.android.cannongame.Classes.Cannonball;
import com.npcf34.android.cannongame.Classes.Line;
import com.npcf34.android.cannongame.Util.DatabaseConnector;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class CannonView extends SurfaceView implements SurfaceHolder.Callback {
    // constants for game play
    public static final int TARGET_PIECES = 7; // sections in the target
    public static final int MISS_PENALTY = 2; // seconds deducted on a miss
    public static final int HIT_REWARD = 3; // seconds added on a hit
    public static int MAX_BALLS = 3;
    public static boolean STARTED = false;
    public static boolean ROTATED = false;
    public static int LEVEL = 1;
    public static int MAX_LEVEL = 3;
    private static final String TAG = "CannonView"; // for logging errors
    // constants and variables for managing sounds
    private static final int TARGET_SOUND_ID = 0;
    private static final int CANNON_SOUND_ID = 1;
    private static final int BLOCKER_SOUND_ID = 2;
    private static final int POWERUP_SOUND_ID = 3;
    protected double totalElapsedTime; // elapsed seconds
    private CannonThread cannonThread; // controls the game loop
    private Activity activity; // to display Game Over dialog in GUI thread
    private boolean dialogIsDisplayed = false;
    // variables for the game loop and tracking statistics
    private boolean gameOver; // is the game over?
    private double timeLeft; // time remaining in seconds
    private int shotsFired; // shots the user has fired
    private double score;
    // variables for the blocker and target
    private Line blocker; // start and end points of the blocker
    private int blockerDistance; // blocker distance from left
    private int blockerBeginning; // blocker top-edge distance from top
    private int blockerEnd; // blocker bottom-edge distance from top
    private int initialBlockerVelocity; // initial blocker speed multiplier
    private float blockerVelocity; // blocker speed multiplier during game
    private Line target; // start and end points of the target
    private int targetDistance; // target distance from left
    private int targetBeginning; // target distance from top
    private double pieceLength; // length of a target piece
    private int targetEnd; // target bottom's distance from top
    private int initialTargetVelocity; // initial target speed multiplier
    private float targetVelocity; // target speed multiplier
    private int lineWidth; // width of the target and blocker
    private boolean[] hitStates; // is each target piece hit?
    private int targetPiecesHit; // number of target pieces hit (out of 7)
    // variables for the cannon and cannonball
    private ArrayList<Cannonball> cannonballs;
    private int cannonballRadius;
    private int cannonballSpeed;
    private int cannonBaseRadius; // cannon base's radius
    private int cannonLength; // cannon barrel's length
    private Point barrelEnd; // the endpoint of the cannon's barrel
    private int screenWidth;
    private int screenHeight;
    private SoundPool soundPool; // plays sound effects
    private SparseIntArray soundMap; // maps IDs to SoundPool

    // Paint variables used when drawing each item on the screen
    private Paint textPaint; // Paint used to draw text
    private Paint cannonPaint; // Paint used to draw the cannon
    private Paint blockerPaint; // Paint used to draw the blocker
    private Paint targetPaint; // Paint used to draw the target
    private Paint backgroundPaint; // Paint used to clear the drawing area

    // public constructor
    public CannonView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        activity = (Activity) context; // store reference to MainActivity

        // register SurfaceHolder.Callback listener
        getHolder().addCallback(this);

        // initialize Lines and Point representing game items
        blocker = new Line(); // create the blocker as a Line
        target = new Line(); // create the target as a Line
        cannonballs = new ArrayList<Cannonball>();

        // initialize hitStates as a boolean array
        hitStates = new boolean[TARGET_PIECES];

        // initialize SoundPool to play the app's three sound effects
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        // create Map of sounds and pre-load sounds
        soundMap = new SparseIntArray(4); // create new HashMap
        soundMap.put(TARGET_SOUND_ID,
                soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID,
                soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID,
                soundPool.load(context, R.raw.blocker_hit, 1));
        soundMap.put(POWERUP_SOUND_ID,
                soundPool.load(context, R.raw.power_up, 1));

        // construct Paints for drawing text, cannonball, cannon,
        // blocker and target; these are configured in method onSizeChanged
        textPaint = new Paint();
        cannonPaint = new Paint();
        blockerPaint = new Paint();
        targetPaint = new Paint();
        backgroundPaint = new Paint();
    } // end CannonView constructor

    // called by surfaceChanged when the size of the SurfaceView changes,
    // such as when it's first added to the View hierarchy
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w; // store CannonView's width
        screenHeight = h; // store CannonView's height
        cannonBaseRadius = h / 18; // cannon base radius 1/18 screen height
        cannonLength = w / 8; // cannon length 1/8 screen width

        cannonballRadius = w / 36; // cannonball radius 1/36 screen width
        cannonballSpeed = w * 3 / 2; // cannonball speed multiplier

        lineWidth = w / 24; // target and blocker 1/24 screen width

        // configure instance variables related to the blocker
        blockerDistance = w * 5 / 8; // blocker 5/8 screen width from left
        blockerBeginning = h / 8; // distance from top 1/8 screen height
        blockerEnd = h * 3 / 8; // distance from top 3/8 screen height
        initialBlockerVelocity = h / 2; // initial blocker speed multiplier
        blocker.start = new Point(blockerDistance, blockerBeginning);
        blocker.end = new Point(blockerDistance, blockerEnd);

        // configure instance variables related to the target
        targetDistance = w * 7 / 8; // target 7/8 screen width from left
        targetBeginning = h / 8; // distance from top 1/8 screen height
        targetEnd = h * 7 / 8; // distance from top 7/8 screen height
        pieceLength = (targetEnd - targetBeginning) / TARGET_PIECES;
        initialTargetVelocity = -h / 4; // initial target speed multiplier
        target.start = new Point(targetDistance, targetBeginning);
        target.end = new Point(targetDistance, targetEnd);

        // endpoint of the cannon's barrel initially points horizontally
        barrelEnd = new Point(cannonLength, h / 2);

        // configure Paint objects for drawing game elements
        textPaint.setTextSize(w / 20); // text size 1/20 of screen width
        textPaint.setAntiAlias(true); // smoothes the text
        cannonPaint.setStrokeWidth(lineWidth * 1.5f); // set line thickness
        blockerPaint.setStrokeWidth(lineWidth); // set line thickness
        targetPaint.setStrokeWidth(lineWidth); // set line thickness
        backgroundPaint.setColor(Color.WHITE); // set background color

        if(!STARTED) {
            newGame(); // set up and start a new game
        }else {
            ROTATED = true;
            //keep game the same but rotate text
        }
    } // end method onSizeChanged

    // reset all the screen elements and start a new game
    public void newGame() {
        // set every element of hitStates to false--restores target pieces
        for (int i = 0; i < TARGET_PIECES; i++)
            hitStates[i] = false;

        MAX_BALLS = 3;
        targetPiecesHit = 0; // no target pieces have been hit
        blockerVelocity = initialBlockerVelocity; // set initial velocity
        targetVelocity = initialTargetVelocity; // set initial velocity
        timeLeft = 10; // start the countdown at 10 seconds
        shotsFired = 0; // set the initial number of shots fired
        totalElapsedTime = 0.0; // set the time elapsed to zero

        // set the start and end Points of the blocker and target
        blocker.start.set(blockerDistance, blockerBeginning);
        blocker.end.set(blockerDistance, blockerEnd);
        target.start.set(targetDistance, targetBeginning);
        target.end.set(targetDistance, targetEnd);

        if (gameOver) // starting a new game after the last game ended
        {
            gameOver = false;
            cannonThread = new CannonThread(getHolder(), this); // create thread
            cannonThread.start(); // start the game loop thread
        }

        STARTED = true;
    } // end method newGame

    // called repeatedly by the CannonThread to update game elements
    protected void updatePositions(double elapsedTimeMS) throws ConcurrentModificationException {
        double interval = elapsedTimeMS / 1000.0; // convert to seconds

        for(Cannonball cannonball: cannonballs) {
            if (cannonball.cannonballOnScreen) // if there is currently a shot fired
            {

                // update cannonball position
                cannonball.x += interval * cannonball.cannonballVelocityX;
                cannonball.y += interval * cannonball.cannonballVelocityY;

                // check for collision with blocker
                if (cannonball.x + cannonball.cannonballRadius > blockerDistance &&
                        cannonball.x - cannonball.cannonballRadius < blockerDistance &&
                        cannonball.y + cannonball.cannonballRadius > blocker.start.y &&
                        cannonball.y - cannonball.cannonballRadius < blocker.end.y) {
                    cannonball.cannonballVelocityX *= -1; // reverse cannonball's direction
                    timeLeft -= MISS_PENALTY; // penalize the user

                    // play blocker sound
                    soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
                }
                // check for collisions with left and right walls
                else if (cannonball.x + cannonball.cannonballRadius > screenWidth ||
                        cannonball.x - cannonball.cannonballRadius < 0) {
                    cannonball.cannonballOnScreen = false; // remove cannonball from screen
                }
                // check for collisions with top and bottom walls
                else if (cannonball.y + cannonball.cannonballRadius > screenHeight ||
                        cannonball.y - cannonball.cannonballRadius < 0) {
                    cannonball.cannonballOnScreen = false; // remove cannonball from screen
                }
                // check for cannonball collision with target
                else if (cannonball.x + cannonball.cannonballRadius > targetDistance &&
                        cannonball.x - cannonball.cannonballRadius < targetDistance &&
                        cannonball.y + cannonball.cannonballRadius > target.start.y &&
                        cannonball.y - cannonball.cannonballRadius < target.end.y) {
                    // determine target section number (0 is the top)
                    int section =
                            (int) ((cannonball.y - target.start.y) / pieceLength);

                    // check if the piece hasn't been hit yet
                    if ((section >= 0 && section < TARGET_PIECES) &&
                            !hitStates[section]) {
                        hitStates[section] = true; // section was hit
                        cannonball.cannonballOnScreen = false; // remove cannonball
                        timeLeft += HIT_REWARD; // add reward to remaining time
                        MAX_BALLS++; //POWERUP: If we hit a target get one more max ball on the field

                        // play target hit sound
                        soundPool.play(soundMap.get(TARGET_SOUND_ID), 1,
                                1, 1, 0, 1f);
                        // play power up sound
                        soundPool.play(soundMap.get(POWERUP_SOUND_ID), 1, 1, 1, 0, 1f);

                        // if all pieces have been hit
                        if (++targetPiecesHit == TARGET_PIECES) {
                            cannonThread.setRunning(false); // terminate thread
                            showGameOverDialog(R.string.win); // show winning dialog
                            gameOver = true;
                            if(LEVEL < 3) {
                                LEVEL++;
                            } else {
                                LEVEL = 1;
                            }
                        }
                    }
                }
            }
        }

        // update the blocker's position
        double blockerUpdate = interval * blockerVelocity;
        blocker.start.y += blockerUpdate;
        blocker.end.y += blockerUpdate;

        // update the target's position
        double targetUpdate = interval * targetVelocity;
        target.start.y += targetUpdate;
        target.end.y += targetUpdate;

        // if the blocker hit the top or bottom, reverse direction
        if (blocker.start.y < 0 || blocker.end.y > screenHeight)
            blockerVelocity *= -1;

        // if the target hit the top or bottom, reverse direction
        if (target.start.y < 0 || target.end.y > screenHeight)
            targetVelocity *= -1;

        timeLeft -= interval; // subtract from time left

        // if the timer reached zero
        if (timeLeft <= 0.0) {
            timeLeft = 0.0;
            gameOver = true; // the game is over
            cannonThread.setRunning(false); // terminate thread
            showGameOverDialog(R.string.lose); // show the losing dialog
        }

        //after we finish updating positions, remove all cannonballs no longer on screen from
        //the array list
        for(int i = 0; i < cannonballs.size(); i++) {
            if(!cannonballs.get(i).cannonballOnScreen) {
                cannonballs.remove(i);
            }
        }

    } // end method updatePositions

    // fires a cannonball
    public void fireCannonball(MotionEvent event) {

        if(cannonballs.size() < MAX_BALLS) {

            Cannonball cannonball = new Cannonball();

            cannonball.cannonballRadius = cannonballRadius * LEVEL;
            cannonball.cannonballSpeed = cannonballSpeed * LEVEL;

            double angle = alignCannon(event); // get the cannon barrel's angle

            // move the cannonball to be inside the cannon
            cannonball.x = cannonballRadius; // align x-coordinate with cannon
            cannonball.y = screenHeight / 2; // centers ball vertically

            // get the x component of the total velocity
            cannonball.cannonballVelocityX = (int) (cannonballSpeed * Math.sin(angle)) * LEVEL;

            // get the y component of the total velocity
            cannonball.cannonballVelocityY = (int) (-cannonballSpeed * Math.cos(angle)) * LEVEL;
            cannonball.cannonballOnScreen = true; // the cannonball is on the screen
            ++shotsFired; // increment shotsFired

            // play cannon fired sound
            soundPool.play(soundMap.get(CANNON_SOUND_ID), 1, 1, 1, 0, 1f);

            cannonballs.add(cannonball);
        }
    } // end method fireCannonball

    // aligns the cannon in response to a user touch
    public double alignCannon(MotionEvent event) {
        // get the location of the touch in this view
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());

        // compute the touch's distance from center of the screen
        // on the y-axis
        double centerMinusY = (screenHeight / 2 - touchPoint.y);

        double angle = 0; // initialize angle to 0

        // calculate the angle the barrel makes with the horizontal
        if (centerMinusY != 0) // prevent division by 0
            angle = Math.atan((double) touchPoint.x / centerMinusY);

        // if the touch is on the lower half of the screen
        if (touchPoint.y > screenHeight / 2)
            angle += Math.PI; // adjust the angle

        // calculate the endpoint of the cannon barrel
        barrelEnd.x = (int) (cannonLength * Math.sin(angle));
        barrelEnd.y =
                (int) (-cannonLength * Math.cos(angle) + screenHeight / 2);

        return angle; // return the computed angle
    } // end method alignCannon

    // draws the game to the given Canvas
    public void drawGameElements(Canvas canvas) {
        // clear the background
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
                backgroundPaint);

        // display time remaining
        canvas.drawText(getResources().getString(
                R.string.time_remaining_format, timeLeft), 30, 50, textPaint);

        canvas.drawText(getResources().getString(
                R.string.max_balls, MAX_BALLS), 30, 130, textPaint);

        canvas.drawText(getResources().getString(R.string.curr_level, LEVEL, LEVEL), 30, 180, textPaint);

        // if a cannonball is currently on the screen, draw it

        for(Cannonball cannonball: cannonballs) {
            if (cannonball.cannonballOnScreen)
                canvas.drawCircle(cannonball.x, cannonball.y, cannonballRadius,
                        cannonball.cannonballPaint);
        }

        // draw the cannon barrel
        canvas.drawLine(0, screenHeight / 2, barrelEnd.x, barrelEnd.y,
                cannonPaint);

        // draw the cannon base
        canvas.drawCircle(0, (int) screenHeight / 2,
                (int) cannonBaseRadius, cannonPaint);

        // draw the blocker
        canvas.drawLine(blocker.start.x, blocker.start.y, blocker.end.x,
                blocker.end.y, blockerPaint);

        Point currentPoint = new Point(); // start of current target section

        // initialize currentPoint to the starting point of the target
        currentPoint.x = target.start.x;
        currentPoint.y = target.start.y;

        // draw the target
        for (int i = 0; i < TARGET_PIECES; i++) {
            // if this target piece is not hit, draw it
            if (!hitStates[i]) {
                // alternate coloring the pieces
                if (i % 2 != 0)
                    targetPaint.setColor(Color.BLUE);
                else
                    targetPaint.setColor(Color.YELLOW);

                canvas.drawLine(currentPoint.x, currentPoint.y, target.end.x,
                        (int) (currentPoint.y + pieceLength), targetPaint);
            }

            // move currentPoint to the start of the next piece
            currentPoint.y += pieceLength;
        }


    } // end method drawGameElements

    // display an AlertDialog when the game ends
    private void showGameOverDialog(final int messageId) {
        // DialogFragment to display quiz stats and start new quiz
        final DialogFragment gameResult =
                new DialogFragment() {
                    // create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        // create dialog displaying String resource for messageId
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(messageId));

                        // display number of shots fired and total time elapsed
                        builder.setMessage(getResources().getString(
                                R.string.results_format, shotsFired, totalElapsedTime));
                        builder.setPositiveButton(R.string.reset_game,
                                new DialogInterface.OnClickListener() {
                                    // called when "Reset Game" Button is pressed
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogIsDisplayed = false;
                                        newGame(); // set up and start a new game
                                    }
                                } // end anonymous inner class
                        ); // end call to setPositiveButton

                        return builder.create(); // return the AlertDialog
                    } // end method onCreateDialog
                }; // end DialogFragment anonymous inner class

        // in GUI thread, use FragmentManager to display the DialogFragment
        activity.runOnUiThread(
                new Runnable() {
                    public void run() {
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false); // modal dialog
                        gameResult.show(activity.getFragmentManager(), "results");
                    }
                } // end Runnable
        ); // end call to runOnUiThread
    } // end method showGameOverDialog

    // stops the game; called by CannonGameFragment's onPause method
    public void stopGame() {
        if(shotsFired != 0) {
            score = (double)(targetPiecesHit/shotsFired);
        }
        saveScore();
        if (cannonThread != null)
            cannonThread.setRunning(false); // tell thread to terminate
    }

    // releases resources; called by CannonGameFragment's onDestroy method
    public void releaseResources() {
        soundPool.release(); // release all resources used by the SoundPool
        soundPool = null;
    }

    // called when surface changes size
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {


    }

    // called when surface is first created
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!dialogIsDisplayed) {
            cannonThread = new CannonThread(holder, this); // create thread
            cannonThread.setRunning(true); // start game running
            cannonThread.start(); // start the game loop thread
        }
    }

    // called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // ensure that thread terminates properly
        boolean retry = true;
        cannonThread.setRunning(false); // terminate cannonThread

        while (retry) {
            try {
                cannonThread.join(); // wait for cannonThread to finish
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    } // end method surfaceDestroyed

    // called when the user touches the screen in this Activity
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // get int representing the type of action which caused this event
        int action = e.getAction();

        // the user user touched the screen or dragged along the screen
        if (action == MotionEvent.ACTION_UP) {
            fireCannonball(e); // fire the cannonball toward the touch point
        }

        return true;
    } // end method onTouchEvent

    public long saveScore() {

        long rowID = 9999;
        DatabaseConnector databaseConnector = new DatabaseConnector(getContext());
        databaseConnector.open();
        Cursor scoreCursor = databaseConnector.getScoresByLevel(LEVEL);

        if(scoreCursor.getCount() < 3) {
            //add score to database
            rowID = databaseConnector.insertScore(score, LEVEL);
        } else {
            for (int i = 0; i < scoreCursor.getCount(); i++) {
                if (score > scoreCursor.getDouble(i) && LEVEL == scoreCursor.getInt(i)) {
                    databaseConnector.updateScore(i, score, LEVEL);
                    break;
                }
            }
        }

        databaseConnector.close();
        return rowID;

    }

} // end class CannonView
