package ch.hslu.mpbro15.team10.battleship.model;

import android.graphics.drawable.Drawable;
import android.view.View;

import ch.hslu.mpbro15.team10.battleship.R;

/**
 * Created by dave on 10.05.2015.
 */
public class GOWater extends BattleshipGameObject {

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public boolean isSunk() {
        return false;
    }

    @Override
    public void shoot() {
        super.shot();
    }

    @Override
    public Drawable getBackground(View view)
    {
        if(super.isShot())
            return view.getResources().getDrawable(R.drawable.miss);
        if(super.isHit())
            return view.getResources().getDrawable(R.drawable.hit);
        return view.getResources().getDrawable(R.drawable.water);
    }

    private String coordinates;
    @Override
    public void setCoordinates(String x, String y) {
        coordinates = x+y;
    }

    @Override
    public String getCoordinates() {
        return coordinates;
    }
}
