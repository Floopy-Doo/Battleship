package ch.hslu.mpbro15.team10.battleship.model;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.Serializable;

/**
 * Created by dave on 10.05.2015.
 */
public abstract class BattleshipGameObject implements Serializable {
    public static int hitCountCarrier = 0;
    public static int hitCountBattleship = 0;
    public static int hitCountDestroyer = 0;
    public static int hitCountSubmarine = 0;
    public static int hitCountMinecruiser = 0;

    private boolean hit;
    private boolean shot;

    public abstract int getLength();
    public abstract boolean isSunk();
    public abstract void shoot() ;
    public abstract Drawable getBackground(View view);

    public boolean isHit() {
        return hit;
    }

    public boolean isShot() {
        return shot;
    }

    public void hit() {
        hit = true;
    }

    public void shot() {
        shot = true;
    }
}
