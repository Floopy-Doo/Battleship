package ch.hslu.mpbro15.team10.battleship.model;

import android.graphics.drawable.Drawable;
import android.view.View;

import ch.hslu.mpbro15.team10.battleship.R;

/**
 * Created by dave on 10.05.2015.
*/
public class GOMinecruiser extends BattleshipGameObject {

    private int lenght = 2;

    @Override
    public boolean isSunk() {
        return BattleshipGameObject.hitCountMinecruiser >= this.getLength();
    }

    @Override
    public int getLength() {
        return lenght;
    }

    @Override
    public void shoot() {
        super.hit();
        super.shot();
        BattleshipGameObject.hitCountMinecruiser++;
    }

    @Override
    public Drawable getBackground(View view)
    {
        if(super.isShot())
            return view.getResources().getDrawable(R.drawable.miss);
        if(super.isHit())
            return view.getResources().getDrawable(R.drawable.hit);
        return view.getResources().getDrawable(R.drawable.ship);
    }
}
