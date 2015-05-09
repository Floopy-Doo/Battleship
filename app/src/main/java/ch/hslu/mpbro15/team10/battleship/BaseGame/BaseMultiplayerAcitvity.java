package ch.hslu.mpbro15.team10.battleship.basegame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

/**
 * Base class for all activities dedicated to multiplayer
 * Created by Floopy-Doo on 09.05.2015.
 */
public abstract class BaseMultiplayerAcitvity extends FragmentActivity {

    /**
     * Request codes for single pages
     */
    public final static int RC_SIGN_IN = 9000001;
    public final static int RC_SEE_INVITATIONS = 9000002;
    public final static int RC_PLAYER_INVITATION = 9000003;
    public final static int RC_WAITING_ROOM = 9000004;

    private Dialog currentDisplayedWaitDialog;

    /**
     * Displays a AlertDialog without controls just for the waiting display.
     *
     * @param message the displayed message
     */
    protected void displayWaitingDialog(String message) {
        currentDisplayedWaitDialog =
                (new AlertDialog.Builder(this))
                        .setMessage(message)
                        .create();
    }

    /**
     * Dismisses the currently displayed waiting dialog if one exists.
     */
    protected void dismissWaitingDialog() {
        if (currentDisplayedWaitDialog != null) {
            currentDisplayedWaitDialog.dismiss();
            currentDisplayedWaitDialog = null;
        }
    }

    /**
     * Sets flag for keeping screen always on
     */
    protected void enableKeepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Resets flag for keeping screen always on
     */
    protected void disableKeepScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
