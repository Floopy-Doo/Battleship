package ch.hslu.mpbro15.team10.battleship.activities;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;

/**
 * Created by Floopy-Doo on 09.05.2015.
 */
public interface MessageListener {
    void onMessageRecieved(RealTimeMessage realTimeMessage);
}
