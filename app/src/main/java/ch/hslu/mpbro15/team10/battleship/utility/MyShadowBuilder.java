package ch.hslu.mpbro15.team10.battleship.utility;

import android.graphics.Point;
import android.util.Log;
import android.view.View;

/**
 * Created by dave on 10.05.2015.
 */
public class MyShadowBuilder extends View.DragShadowBuilder {
    private Point _offset;

    public MyShadowBuilder(View view, Point offset) {

        // Stores the View parameter passed to myDragShadowBuilder.
        super(view);

        // Save the offset :
        _offset = offset;
    }

    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        Log.d(this.getClass().getSimpleName(), "Shadow Point:" + touch);
//
//        // Set the shadow size :
//        size.set(getView().getWidth(), getView().getHeight());
//
//        // Sets the touch point's position to be in the middle of the drag shadow
//        touch.set(_offset.x, _offset.y);
    }
}
