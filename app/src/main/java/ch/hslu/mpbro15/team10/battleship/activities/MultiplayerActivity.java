package ch.hslu.mpbro15.team10.battleship.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import ch.hslu.mpbro15.team10.battleship.R;
import ch.hslu.mpbro15.team10.battleship.basegame.BaseMultiplayerAcitvity;
import ch.hslu.mpbro15.team10.battleship.fragments.MultiplayerGameFragment;
import ch.hslu.mpbro15.team10.battleship.fragments.MultiplayerGameSetupFragment;
import ch.hslu.mpbro15.team10.battleship.fragments.MultiplayerSignInFragment;
import ch.hslu.mpbro15.team10.battleship.fragments.MultiplayerSignedInFragment;
import ch.hslu.mpbro15.team10.battleship.fragments.OnFragmentInteractionListener;
import ch.hslu.mpbro15.team10.battleship.fragments.WaitingFragment;
import ch.hslu.mpbro15.team10.battleship.googleplaybasegame.BaseGameUtils;
import ch.hslu.mpbro15.team10.battleship.model.BattleshipGrid;
import ch.hslu.mpbro15.team10.battleship.utility.ByteTransferObjectCoder;
import ch.hslu.mpbro15.team10.battleship.utility.TransferObject;

public class MultiplayerActivity extends BaseMultiplayerAcitvity implements OnFragmentInteractionListener, MessageListener {

    /**
     * ConnectionHandler for the Google Play Api Connector
     */
    public final GooglePlayConnectionManager playConManager = new GooglePlayConnectionManager();
    public final GooglePlayInvitationManager playInvManager = new GooglePlayInvitationManager();
    public final GooglePlayMessageManager playMsgManager = new GooglePlayMessageManager();
    public final GooglePlayRoomManager playRoomManager = new GooglePlayRoomManager(playConManager, playMsgManager);
    public boolean myTurn;

    public String getCurrentRoomId()
    {
        return playRoomManager.currentRoom.getRoomId();
    }

    public BattleshipGrid mMyGrid;
    public BattleshipGrid mEnemyGrid;

    public Participant getEnemy()
    {
        Participant enemy = null;
        for(Participant p:playRoomManager.roomParticipiants)
        {
            if(!p.getParticipantId().equals(playRoomManager.currentPlayerID))
            enemy = p;
        }
        return enemy;
    }

    public Participant getMe()
    {
        Participant me = null;
        for(Participant p:playRoomManager.roomParticipiants)
        {
            if(p.getParticipantId().equals(playRoomManager.currentPlayerID))
                me = p;
        }
        return me;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        if (savedInstanceState == null && playConManager.isConnected()) {
            showSignedInFragment();
        } else {
            showSignInFragment();
        }

        playConManager.client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(playConManager)
                .addOnConnectionFailedListener(playConManager)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)   //google plus login
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)      //google games
                .build();

        findViewById(R.id.button_accept_popup_invitation)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismissInvitation();
                        playRoomManager.acceptInvite(playInvManager.dispayedInvId);
                    }
                });
        mMyGrid = BattleshipGrid.prepareOwnGrid();
        mEnemyGrid = BattleshipGrid.prepareOpponentGrid();
        playMsgManager.addListener(this);
    }

    @Override
    public void onStart() {
        if (playConManager.isConnected()) {
            showSignedInFragment();
        } else if (playConManager.client != null) {
            showWaitingFragment();
            playConManager.client.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        playRoomManager.leaveRoom();
        disableKeepScreenOn();

        if (playConManager.isConnected()) {
            showSignedInFragment();
        } else {
            showWaitingFragment();
        }
        super.onStop();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.container);

        boolean isGameScreen = frag instanceof MultiplayerGameFragment;
        isGameScreen |= frag instanceof MultiplayerGameSetupFragment;

        if (keyCode == KeyEvent.KEYCODE_BACK && isGameScreen) {
            playRoomManager.leaveRoom();
            return true;
        }
        return super.onKeyDown(keyCode, e);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            playConManager.signIn();
        } else if (requestCode == RC_PLAYER_INVITATION
                && resultCode == RESULT_OK) {
            Log.d(this.getClass().getName(), "Select players UI succeeded.");
            playRoomManager.setUpGameRoom(
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS),
                    data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0),
                    data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0));
        } else if (requestCode == BaseMultiplayerAcitvity.RC_SEE_INVITATIONS
                && resultCode == RESULT_OK) {
            Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
            playRoomManager.acceptInvite(inv.getInvitationId());
        } else if (requestCode == RC_WAITING_ROOM && resultCode == RESULT_OK) {
            // ready to start playing
            Log.d(this.getClass().getName(), "Starting game (waiting room returned OK).");
            perpareForGameStart();
        } else if (requestCode == RC_WAITING_ROOM &&
                (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM || resultCode == RESULT_CANCELED)) {
            playRoomManager.leaveRoom();
        }
    }


    @Override
    protected void onDestroy() {
        playRoomManager.leaveRoom();
        playConManager.close();

        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    protected void showSignInFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, MultiplayerSignInFragment.newInstance())
                .commit();
    }

    protected void showSignedInFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, MultiplayerSignedInFragment.newInstance())
                .commit();
    }

    protected void showGameSetupFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, MultiplayerGameSetupFragment.newInstance())
                .commit();
    }

    public void showGameFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, MultiplayerGameFragment.newInstance())
                .commit();
    }

    protected void showWaitingFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, WaitingFragment.newInstance())
                .commit();
    }

    protected void showInvitation(String playerName) {
        ((TextView) findViewById(R.id.incoming_invitation_text))
                .setText(String.format("%s %s", playerName, getString(R.string.is_inviting_you)));
        findViewById(R.id.invitation_popup)
                .setVisibility(View.VISIBLE);
    }

    protected void dismissInvitation() {
        findViewById(R.id.invitation_popup)
                .setVisibility(View.GONE);
    }

    private void perpareForGameStart() {

        if(playRoomManager.roomParticipiants.get(0).getParticipantId().equals(playRoomManager.currentPlayerID)) {
            Random rn = new Random();
            int startingPlayersIndex = rn.nextInt(2);

            TransferObject transferObject = new TransferObject("StartingPlayer",playRoomManager.roomParticipiants.get(startingPlayersIndex).getParticipantId());

            byte[] mMsgBuf = ByteTransferObjectCoder.encodeTransferObject(transferObject);

            // Send to every other participant.
            for (Participant p : playRoomManager.roomParticipiants) {
                if (p.getParticipantId().equals(playRoomManager.currentPlayerID))
                    continue;
                if (p.getStatus() != Participant.STATUS_JOINED)
                    continue;

                // it's an interim score notification, so we can use unreliable
                Games.RealTimeMultiplayer.sendUnreliableMessage(playConManager.client, mMsgBuf, playRoomManager.currentRoom.getRoomId(),
                        p.getParticipantId());

            }
            if (playRoomManager.currentPlayerID.equals(transferObject.getMessage()))
                myTurn = true;
        }
        showGameSetupFragment();
    }

    @Override
    public void onMessageRecieved(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();
        TransferObject transferObject = ByteTransferObjectCoder.decodeTransferObject(buf);

        if(transferObject.getType().equals("StartingPlayer"))
        {
            if(transferObject.getMessage().equals(playRoomManager.currentPlayerID))
            {
                myTurn=true;
            }
        }
    }

    public class GooglePlayConnectionManager implements
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        public GoogleApiClient client;

        public boolean isConnected() {
            return client != null && client.isConnected();
        }

        public void signIn() {
            Log.d(this.getClass().getName(), "Signing out player");
            if (client != null) {
                client.connect();
            }
        }

        public void signOut() {
            Log.d(this.getClass().getName(), "Signing out player");
            if (client != null && client.isConnected()) {
                Games.signOut(client);
            }
            close();
            MultiplayerActivity.this.showSignInFragment();
        }

        public void close() {
            if (client != null) {
                client.disconnect();
            }
        }

        @Override
        public void onConnected(Bundle bundle) {
            Log.d(this.getClass().getName(), "Connection etablished. Login successfull.");

            // Register listener for invitation callbacks
            Games.Invitations
                    .registerInvitationListener(client, MultiplayerActivity.this.playInvManager);

            // check for room invites
            Invitation invite;
            if (bundle != null
                    && (invite = bundle.getParcelable(Multiplayer.EXTRA_INVITATION)) != null
                    && invite.getInvitationId() != null) {
                Log.d(this.getClass().getName(), "Received room invite");
                MultiplayerActivity.this.playRoomManager.acceptInvite(invite.getInvitationId());
            } else {
                MultiplayerActivity.this.showSignedInFragment();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(this.getClass().getName(),
                    String.format("Connection suspended. ClientPresent:%s", client != null));
            // reconnect if possible
            if (client != null) {
                client.connect();
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(this.getClass().getName(), "Connecting to GooglePlay failed");
            MultiplayerActivity.this.showWaitingFragment();

            BaseGameUtils.resolveConnectionFailure(
                    MultiplayerActivity.this,
                    client,
                    connectionResult,
                    RC_SIGN_IN,
                    getString(R.string.signin_other_error));
        }
    }

    public class GooglePlayInvitationManager implements
            OnInvitationReceivedListener {
        public String dispayedInvId;


        @Override
        public void onInvitationReceived(Invitation invitation) {
            dispayedInvId = invitation.getInvitationId();
            MultiplayerActivity.this.showInvitation(invitation.getInviter().getDisplayName());
        }

        @Override
        public void onInvitationRemoved(String inviteID) {
            if (inviteID == dispayedInvId) {
                MultiplayerActivity.this.dismissInvitation();
            }
        }
    }

    public class GooglePlayRoomManager implements
            RoomStatusUpdateListener,
            RoomUpdateListener {
        public static final int ROOM_MIN_PLAYERS = 1;
        public static final int ROOM_MAX_PLAYERS = 1;

        private final GooglePlayConnectionManager connectionManager;
        private final GooglePlayMessageManager messageManager;
        private Room currentRoom;
        private String currentPlayerID;
        private List<Participant> roomParticipiants = new ArrayList<>();

        public GooglePlayRoomManager(GooglePlayConnectionManager conMgr,
                                     GooglePlayMessageManager mgsMgr) {
            this.connectionManager = conMgr;
            this.messageManager = mgsMgr;
        }

        public void acceptInvite(String invitationID) {
            Log.d(this.getClass().getName(), "Accepting invitation: " + invitationID);

            enableKeepScreenOn();

            // Dipslay dialog
            MultiplayerActivity.this.displayWaitingDialog(
                    "Accepting invitation\n ... please wait ...");

            // Joining multiplayer game
            Games.RealTimeMultiplayer.join(connectionManager.client,
                    RoomConfig
                            .builder(this)
                            .setRoomStatusUpdateListener(this)
                            .setMessageReceivedListener(messageManager)
                            .setInvitationIdToAccept(invitationID)
                            .build()
            );
        }

        public void setUpGameRoom(final ArrayList<String> playerIds, int minPlayerCount, int maxPlayerCount) {
            Log.d(this.getClass().getName(), "Creating room for multiplayer match ...");

            Bundle autoMatchCriteria = null;
            if (minPlayerCount > 0 || maxPlayerCount > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minPlayerCount, maxPlayerCount, 0);
            }

            RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this)
                    .addPlayersToInvite(playerIds)
                    .setMessageReceivedListener(messageManager)
                    .setRoomStatusUpdateListener(this);
            if (autoMatchCriteria != null) {
                rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }

            enableKeepScreenOn();
            Games.RealTimeMultiplayer.create(
                    connectionManager.client,
                    rtmConfigBuilder.build());

            Log.d(this.getClass().getName(), "Creating room done, waiting for it to be ready...");
        }

        private void updateRoom(Room room) {
            if (currentRoom.getRoomId() == room.getRoomId()) {
                roomParticipiants = room.getParticipants();
            }
        }

        private void showWaitingRoom(Room room) {
            Intent intent = Games.RealTimeMultiplayer
                    .getWaitingRoomIntent(connectionManager.client, room, ROOM_MAX_PLAYERS);
            MultiplayerActivity.this.startActivityForResult(intent, RC_WAITING_ROOM);
        }

        public void leaveRoom() {
            if (currentRoom != null) {
                Games.RealTimeMultiplayer.leave(connectionManager.client, this, currentRoom.getRoomId());
            }
        }

        @Override
        public void onRoomConnecting(Room room) {
            updateRoom(room);
        }

        @Override
        public void onRoomAutoMatching(Room room) {
            updateRoom(room);
        }

        @Override
        public void onPeerInvitedToRoom(Room room, List<String> strings) {
            updateRoom(room);
        }

        @Override
        public void onPeerDeclined(Room room, List<String> strings) {
            updateRoom(room);
        }

        @Override
        public void onPeerJoined(Room room, List<String> strings) {
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(Room room, List<String> strings) {
            updateRoom(room);
        }

        @Override
        public void onConnectedToRoom(Room room) {
            currentRoom = room;
            currentPlayerID = room.getParticipantId(
                    Games.Players.getCurrentPlayerId(connectionManager.client));
            roomParticipiants = room.getParticipants();

            Log.d(this.getClass().getName(), String.format(
                    "Connected to Room. RoomID:%s, PlayerID:%s"
                    , room.getRoomId()
                    , currentPlayerID));
        }

        @Override
        public void onDisconnectedFromRoom(Room room) {
            currentRoom = null;
            Log.e(this.getClass().getName(), "Disconnected from room.");
            MultiplayerActivity.this.showGameError(getString(R.string.game_problem));
        }

        @Override
        public void onPeersConnected(Room room, List<String> strings) {
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(Room room, List<String> strings) {
            updateRoom(room);
        }

        @Override
        public void onP2PConnected(String s) {

        }

        @Override
        public void onP2PDisconnected(String s) {

        }

        @Override
        public void onRoomCreated(int status, Room room) {
            if (status == GamesStatusCodes.STATUS_OK) {
                showWaitingRoom(room);
            } else {
                Log.e(this.getClass().getName(), "Room creation failed. StatusID:" + status);
                MultiplayerActivity.this.showGameError(getString(R.string.game_problem));
            }
        }

        @Override
        public void onJoinedRoom(int status, Room room) {
            if (status == GamesStatusCodes.STATUS_OK) {
                showWaitingRoom(room);
            } else {
                Log.e(this.getClass().getName(), "Room joining failed. StatusID:" + status);
                MultiplayerActivity.this.showGameError(getString(R.string.game_problem));
            }
        }

        @Override
        public void onLeftRoom(int status, String roomID) {
            if (currentRoom.getRoomId() == roomID) {
                currentRoom = null;
                MultiplayerActivity.this.showSignedInFragment();
            }
        }

        @Override
        public void onRoomConnected(int status, Room room) {
            if (status == GamesStatusCodes.STATUS_OK) {
                updateRoom(room);
            } else {
                Log.e(this.getClass().getName(), "Room connected failed. StatusID:" + status);
                MultiplayerActivity.this.showGameError(getString(R.string.game_problem));
            }
        }
    }

    public class GooglePlayMessageManager implements
            RealTimeMessageReceivedListener {
        private List<MessageListener> listeners = new ArrayList<>();

        @Override
        public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
            for (MessageListener list : listeners) {
                list.onMessageRecieved(realTimeMessage);
            }
        }

        public void addListener(MessageListener listener) {
            listeners.add(listener);
        }

        public boolean removeListener(MessageListener listener) {
            return listeners.remove(listener);
        }
    }

    public void showGameError(String msg) {
        Log.d(this.getClass().getName(), msg);
        (new AlertDialog.Builder(this))
                .setMessage(msg)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showSignedInFragment();
                    }
                })
                .create();
    }


}
