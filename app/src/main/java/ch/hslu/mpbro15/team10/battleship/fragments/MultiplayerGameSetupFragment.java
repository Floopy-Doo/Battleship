package ch.hslu.mpbro15.team10.battleship.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import ch.hslu.mpbro15.team10.battleship.R;
import ch.hslu.mpbro15.team10.battleship.activities.MessageListener;
import ch.hslu.mpbro15.team10.battleship.activities.MultiplayerActivity;
import ch.hslu.mpbro15.team10.battleship.model.BattleshipGameObject;
import ch.hslu.mpbro15.team10.battleship.model.BattleshipInvalidPlacementException;
import ch.hslu.mpbro15.team10.battleship.utility.ByteTransferObjectCoder;
import ch.hslu.mpbro15.team10.battleship.utility.MyShadowBuilder;
import ch.hslu.mpbro15.team10.battleship.utility.TransferObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultiplayerGameSetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiplayerGameSetupFragment extends Fragment implements MessageListener {
    private OnFragmentInteractionListener mListener;
    private MultiplayerActivity mActivity = (MultiplayerActivity) getActivity();
    private boolean mReady;
    private boolean mEnemyReady;
    private View mView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameSetupFragment.
     */
    public static MultiplayerGameSetupFragment newInstance() {
        MultiplayerGameSetupFragment fragment = new MultiplayerGameSetupFragment();
        return fragment;
    }

    public MultiplayerGameSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MultiplayerActivity) getActivity();
        mActivity.playMsgManager.addListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_setup, container, false);
        mView = view;
        Button ready = (Button) view.findViewById(R.id.btnReady);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Games.RealTimeMultiplayer.sendReliableMessage(mActivity.playConManager.client,null, ByteTransferObjectCoder.encodeTransferObject(new TransferObject("Ready","TRUE")),mActivity.getCurrentRoomId(),mActivity.getEnemy().getParticipantId());
                mReady = true;
                ((TextView)(mView.findViewById(R.id.textView2))).setText(getString(R.string.waitingForOponent));
                if(mEnemyReady&&mReady)
                {
                    mActivity.showGameFragment();
                }
            }
        });
        setupGame(view);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mActivity.playMsgManager.removeListener(this);
    }

    @Override
    public void onMessageRecieved(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();

        TransferObject transferObject = ByteTransferObjectCoder.decodeTransferObject(buf);

        if(transferObject.getType().equals("Ready"))
        {
            TextView oponentStatus = (TextView)getView().findViewById(R.id.txvOpponentGridStatus);
            oponentStatus.setText(getString(R.string.opponentStatusReady));
            mEnemyReady = true;
            if(mEnemyReady&&mReady)
            {
                mActivity.showGameFragment();
            }
        }

    }

    private void setupGame(View view)
    {
        try {
        mActivity.mMyGrid.placeShip(0,0,0,1);
        mActivity.mMyGrid.placeShip(1,0,1,1);
        mActivity.mMyGrid.placeShip(2,0,2,1);
        mActivity.mMyGrid.placeShip(3,0,3,1);
        mActivity.mMyGrid.placeShip(4,0,4,1);
        } catch (BattleshipInvalidPlacementException e) {
            e.printStackTrace();
        }

        view.findViewById(R.id.grid00).setTag(mActivity.mMyGrid.getGrid()[0][0]);
        view.findViewById(R.id.grid01).setTag(mActivity.mMyGrid.getGrid()[0][1]);
        view.findViewById(R.id.grid02).setTag(mActivity.mMyGrid.getGrid()[0][2]);
        view.findViewById(R.id.grid03).setTag(mActivity.mMyGrid.getGrid()[0][3]);
        view.findViewById(R.id.grid04).setTag(mActivity.mMyGrid.getGrid()[0][4]);
        view.findViewById(R.id.grid05).setTag(mActivity.mMyGrid.getGrid()[0][5]);
        view.findViewById(R.id.grid06).setTag(mActivity.mMyGrid.getGrid()[0][6]);
        view.findViewById(R.id.grid07).setTag(mActivity.mMyGrid.getGrid()[0][7]);
        view.findViewById(R.id.grid08).setTag(mActivity.mMyGrid.getGrid()[0][8]);
        view.findViewById(R.id.grid09).setTag(mActivity.mMyGrid.getGrid()[0][9]);
        view.findViewById(R.id.grid10).setTag(mActivity.mMyGrid.getGrid()[1][0]);
        view.findViewById(R.id.grid11).setTag(mActivity.mMyGrid.getGrid()[1][1]);
        view.findViewById(R.id.grid12).setTag(mActivity.mMyGrid.getGrid()[1][2]);
        view.findViewById(R.id.grid13).setTag(mActivity.mMyGrid.getGrid()[1][3]);
        view.findViewById(R.id.grid14).setTag(mActivity.mMyGrid.getGrid()[1][4]);
        view.findViewById(R.id.grid15).setTag(mActivity.mMyGrid.getGrid()[1][5]);
        view.findViewById(R.id.grid16).setTag(mActivity.mMyGrid.getGrid()[1][6]);
        view.findViewById(R.id.grid17).setTag(mActivity.mMyGrid.getGrid()[1][7]);
        view.findViewById(R.id.grid18).setTag(mActivity.mMyGrid.getGrid()[1][8]);
        view.findViewById(R.id.grid19).setTag(mActivity.mMyGrid.getGrid()[1][9]);
        view.findViewById(R.id.grid20).setTag(mActivity.mMyGrid.getGrid()[2][0]);
        view.findViewById(R.id.grid21).setTag(mActivity.mMyGrid.getGrid()[2][1]);
        view.findViewById(R.id.grid22).setTag(mActivity.mMyGrid.getGrid()[2][2]);
        view.findViewById(R.id.grid23).setTag(mActivity.mMyGrid.getGrid()[2][3]);
        view.findViewById(R.id.grid24).setTag(mActivity.mMyGrid.getGrid()[2][4]);
        view.findViewById(R.id.grid25).setTag(mActivity.mMyGrid.getGrid()[2][5]);
        view.findViewById(R.id.grid26).setTag(mActivity.mMyGrid.getGrid()[2][6]);
        view.findViewById(R.id.grid27).setTag(mActivity.mMyGrid.getGrid()[2][7]);
        view.findViewById(R.id.grid28).setTag(mActivity.mMyGrid.getGrid()[2][8]);
        view.findViewById(R.id.grid29).setTag(mActivity.mMyGrid.getGrid()[2][9]);
        view.findViewById(R.id.grid30).setTag(mActivity.mMyGrid.getGrid()[3][0]);
        view.findViewById(R.id.grid31).setTag(mActivity.mMyGrid.getGrid()[3][1]);
        view.findViewById(R.id.grid32).setTag(mActivity.mMyGrid.getGrid()[3][2]);
        view.findViewById(R.id.grid33).setTag(mActivity.mMyGrid.getGrid()[3][3]);
        view.findViewById(R.id.grid34).setTag(mActivity.mMyGrid.getGrid()[3][4]);
        view.findViewById(R.id.grid35).setTag(mActivity.mMyGrid.getGrid()[3][5]);
        view.findViewById(R.id.grid36).setTag(mActivity.mMyGrid.getGrid()[3][6]);
        view.findViewById(R.id.grid37).setTag(mActivity.mMyGrid.getGrid()[3][7]);
        view.findViewById(R.id.grid38).setTag(mActivity.mMyGrid.getGrid()[3][8]);
        view.findViewById(R.id.grid39).setTag(mActivity.mMyGrid.getGrid()[3][9]);
        view.findViewById(R.id.grid40).setTag(mActivity.mMyGrid.getGrid()[4][0]);
        view.findViewById(R.id.grid41).setTag(mActivity.mMyGrid.getGrid()[4][1]);
        view.findViewById(R.id.grid42).setTag(mActivity.mMyGrid.getGrid()[4][2]);
        view.findViewById(R.id.grid43).setTag(mActivity.mMyGrid.getGrid()[4][3]);
        view.findViewById(R.id.grid44).setTag(mActivity.mMyGrid.getGrid()[4][4]);
        view.findViewById(R.id.grid45).setTag(mActivity.mMyGrid.getGrid()[4][5]);
        view.findViewById(R.id.grid46).setTag(mActivity.mMyGrid.getGrid()[4][6]);
        view.findViewById(R.id.grid47).setTag(mActivity.mMyGrid.getGrid()[4][7]);
        view.findViewById(R.id.grid48).setTag(mActivity.mMyGrid.getGrid()[4][8]);
        view.findViewById(R.id.grid49).setTag(mActivity.mMyGrid.getGrid()[4][9]);
        view.findViewById(R.id.grid50).setTag(mActivity.mMyGrid.getGrid()[5][0]);
        view.findViewById(R.id.grid51).setTag(mActivity.mMyGrid.getGrid()[5][1]);
        view.findViewById(R.id.grid52).setTag(mActivity.mMyGrid.getGrid()[5][2]);
        view.findViewById(R.id.grid53).setTag(mActivity.mMyGrid.getGrid()[5][3]);
        view.findViewById(R.id.grid54).setTag(mActivity.mMyGrid.getGrid()[5][4]);
        view.findViewById(R.id.grid55).setTag(mActivity.mMyGrid.getGrid()[5][5]);
        view.findViewById(R.id.grid56).setTag(mActivity.mMyGrid.getGrid()[5][6]);
        view.findViewById(R.id.grid57).setTag(mActivity.mMyGrid.getGrid()[5][7]);
        view.findViewById(R.id.grid58).setTag(mActivity.mMyGrid.getGrid()[5][8]);
        view.findViewById(R.id.grid59).setTag(mActivity.mMyGrid.getGrid()[5][9]);
        view.findViewById(R.id.grid60).setTag(mActivity.mMyGrid.getGrid()[6][0]);
        view.findViewById(R.id.grid61).setTag(mActivity.mMyGrid.getGrid()[6][1]);
        view.findViewById(R.id.grid62).setTag(mActivity.mMyGrid.getGrid()[6][2]);
        view.findViewById(R.id.grid63).setTag(mActivity.mMyGrid.getGrid()[6][3]);
        view.findViewById(R.id.grid64).setTag(mActivity.mMyGrid.getGrid()[6][4]);
        view.findViewById(R.id.grid65).setTag(mActivity.mMyGrid.getGrid()[6][5]);
        view.findViewById(R.id.grid66).setTag(mActivity.mMyGrid.getGrid()[6][6]);
        view.findViewById(R.id.grid67).setTag(mActivity.mMyGrid.getGrid()[6][7]);
        view.findViewById(R.id.grid68).setTag(mActivity.mMyGrid.getGrid()[6][8]);
        view.findViewById(R.id.grid69).setTag(mActivity.mMyGrid.getGrid()[6][9]);
        view.findViewById(R.id.grid70).setTag(mActivity.mMyGrid.getGrid()[7][0]);
        view.findViewById(R.id.grid71).setTag(mActivity.mMyGrid.getGrid()[7][1]);
        view.findViewById(R.id.grid72).setTag(mActivity.mMyGrid.getGrid()[7][2]);
        view.findViewById(R.id.grid73).setTag(mActivity.mMyGrid.getGrid()[7][3]);
        view.findViewById(R.id.grid74).setTag(mActivity.mMyGrid.getGrid()[7][4]);
        view.findViewById(R.id.grid75).setTag(mActivity.mMyGrid.getGrid()[7][5]);
        view.findViewById(R.id.grid76).setTag(mActivity.mMyGrid.getGrid()[7][6]);
        view.findViewById(R.id.grid77).setTag(mActivity.mMyGrid.getGrid()[7][7]);
        view.findViewById(R.id.grid78).setTag(mActivity.mMyGrid.getGrid()[7][8]);
        view.findViewById(R.id.grid79).setTag(mActivity.mMyGrid.getGrid()[7][9]);
        view.findViewById(R.id.grid80).setTag(mActivity.mMyGrid.getGrid()[8][0]);
        view.findViewById(R.id.grid81).setTag(mActivity.mMyGrid.getGrid()[8][1]);
        view.findViewById(R.id.grid82).setTag(mActivity.mMyGrid.getGrid()[8][2]);
        view.findViewById(R.id.grid83).setTag(mActivity.mMyGrid.getGrid()[8][3]);
        view.findViewById(R.id.grid84).setTag(mActivity.mMyGrid.getGrid()[8][4]);
        view.findViewById(R.id.grid85).setTag(mActivity.mMyGrid.getGrid()[8][5]);
        view.findViewById(R.id.grid86).setTag(mActivity.mMyGrid.getGrid()[8][6]);
        view.findViewById(R.id.grid87).setTag(mActivity.mMyGrid.getGrid()[8][7]);
        view.findViewById(R.id.grid88).setTag(mActivity.mMyGrid.getGrid()[8][8]);
        view.findViewById(R.id.grid89).setTag(mActivity.mMyGrid.getGrid()[8][9]);
        view.findViewById(R.id.grid90).setTag(mActivity.mMyGrid.getGrid()[9][0]);
        view.findViewById(R.id.grid91).setTag(mActivity.mMyGrid.getGrid()[9][1]);
        view.findViewById(R.id.grid92).setTag(mActivity.mMyGrid.getGrid()[9][2]);
        view.findViewById(R.id.grid93).setTag(mActivity.mMyGrid.getGrid()[9][3]);
        view.findViewById(R.id.grid94).setTag(mActivity.mMyGrid.getGrid()[9][4]);
        view.findViewById(R.id.grid95).setTag(mActivity.mMyGrid.getGrid()[9][5]);
        view.findViewById(R.id.grid96).setTag(mActivity.mMyGrid.getGrid()[9][6]);
        view.findViewById(R.id.grid97).setTag(mActivity.mMyGrid.getGrid()[9][7]);
        view.findViewById(R.id.grid98).setTag(mActivity.mMyGrid.getGrid()[9][8]);
        view.findViewById(R.id.grid99).setTag(mActivity.mMyGrid.getGrid()[9][9]);



        view.findViewById(R.id.grid00).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid00).getTag()).getBackground(view));
        view.findViewById(R.id.grid01).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid01).getTag()).getBackground(view));
        view.findViewById(R.id.grid02).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid02).getTag()).getBackground(view));
        view.findViewById(R.id.grid03).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid03).getTag()).getBackground(view));
        view.findViewById(R.id.grid04).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid04).getTag()).getBackground(view));
        view.findViewById(R.id.grid05).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid05).getTag()).getBackground(view));
        view.findViewById(R.id.grid06).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid06).getTag()).getBackground(view));
        view.findViewById(R.id.grid07).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid07).getTag()).getBackground(view));
        view.findViewById(R.id.grid08).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid08).getTag()).getBackground(view));
        view.findViewById(R.id.grid09).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid09).getTag()).getBackground(view));
        view.findViewById(R.id.grid10).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid10).getTag()).getBackground(view));
        view.findViewById(R.id.grid11).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid11).getTag()).getBackground(view));
        view.findViewById(R.id.grid12).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid12).getTag()).getBackground(view));
        view.findViewById(R.id.grid13).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid13).getTag()).getBackground(view));
        view.findViewById(R.id.grid14).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid14).getTag()).getBackground(view));
        view.findViewById(R.id.grid15).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid15).getTag()).getBackground(view));
        view.findViewById(R.id.grid16).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid16).getTag()).getBackground(view));
        view.findViewById(R.id.grid17).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid17).getTag()).getBackground(view));
        view.findViewById(R.id.grid18).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid18).getTag()).getBackground(view));
        view.findViewById(R.id.grid19).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid19).getTag()).getBackground(view));
        view.findViewById(R.id.grid20).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid20).getTag()).getBackground(view));
        view.findViewById(R.id.grid21).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid21).getTag()).getBackground(view));
        view.findViewById(R.id.grid22).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid22).getTag()).getBackground(view));
        view.findViewById(R.id.grid23).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid23).getTag()).getBackground(view));
        view.findViewById(R.id.grid24).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid24).getTag()).getBackground(view));
        view.findViewById(R.id.grid25).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid25).getTag()).getBackground(view));
        view.findViewById(R.id.grid26).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid26).getTag()).getBackground(view));
        view.findViewById(R.id.grid27).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid27).getTag()).getBackground(view));
        view.findViewById(R.id.grid28).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid28).getTag()).getBackground(view));
        view.findViewById(R.id.grid29).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid29).getTag()).getBackground(view));
        view.findViewById(R.id.grid30).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid30).getTag()).getBackground(view));
        view.findViewById(R.id.grid31).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid31).getTag()).getBackground(view));
        view.findViewById(R.id.grid32).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid32).getTag()).getBackground(view));
        view.findViewById(R.id.grid33).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid33).getTag()).getBackground(view));
        view.findViewById(R.id.grid34).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid34).getTag()).getBackground(view));
        view.findViewById(R.id.grid35).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid35).getTag()).getBackground(view));
        view.findViewById(R.id.grid36).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid36).getTag()).getBackground(view));
        view.findViewById(R.id.grid37).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid37).getTag()).getBackground(view));
        view.findViewById(R.id.grid38).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid38).getTag()).getBackground(view));
        view.findViewById(R.id.grid39).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid39).getTag()).getBackground(view));
        view.findViewById(R.id.grid40).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid40).getTag()).getBackground(view));
        view.findViewById(R.id.grid41).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid41).getTag()).getBackground(view));
        view.findViewById(R.id.grid42).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid42).getTag()).getBackground(view));
        view.findViewById(R.id.grid43).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid43).getTag()).getBackground(view));
        view.findViewById(R.id.grid44).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid44).getTag()).getBackground(view));
        view.findViewById(R.id.grid45).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid45).getTag()).getBackground(view));
        view.findViewById(R.id.grid46).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid46).getTag()).getBackground(view));
        view.findViewById(R.id.grid47).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid47).getTag()).getBackground(view));
        view.findViewById(R.id.grid48).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid48).getTag()).getBackground(view));
        view.findViewById(R.id.grid49).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid49).getTag()).getBackground(view));
        view.findViewById(R.id.grid50).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid50).getTag()).getBackground(view));
        view.findViewById(R.id.grid51).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid51).getTag()).getBackground(view));
        view.findViewById(R.id.grid52).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid52).getTag()).getBackground(view));
        view.findViewById(R.id.grid53).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid53).getTag()).getBackground(view));
        view.findViewById(R.id.grid54).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid54).getTag()).getBackground(view));
        view.findViewById(R.id.grid55).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid55).getTag()).getBackground(view));
        view.findViewById(R.id.grid56).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid56).getTag()).getBackground(view));
        view.findViewById(R.id.grid57).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid57).getTag()).getBackground(view));
        view.findViewById(R.id.grid58).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid58).getTag()).getBackground(view));
        view.findViewById(R.id.grid59).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid59).getTag()).getBackground(view));
        view.findViewById(R.id.grid60).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid60).getTag()).getBackground(view));
        view.findViewById(R.id.grid61).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid61).getTag()).getBackground(view));
        view.findViewById(R.id.grid62).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid62).getTag()).getBackground(view));
        view.findViewById(R.id.grid63).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid63).getTag()).getBackground(view));
        view.findViewById(R.id.grid64).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid64).getTag()).getBackground(view));
        view.findViewById(R.id.grid65).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid65).getTag()).getBackground(view));
        view.findViewById(R.id.grid66).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid66).getTag()).getBackground(view));
        view.findViewById(R.id.grid67).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid67).getTag()).getBackground(view));
        view.findViewById(R.id.grid68).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid68).getTag()).getBackground(view));
        view.findViewById(R.id.grid69).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid69).getTag()).getBackground(view));
        view.findViewById(R.id.grid70).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid70).getTag()).getBackground(view));
        view.findViewById(R.id.grid71).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid71).getTag()).getBackground(view));
        view.findViewById(R.id.grid72).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid72).getTag()).getBackground(view));
        view.findViewById(R.id.grid73).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid73).getTag()).getBackground(view));
        view.findViewById(R.id.grid74).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid74).getTag()).getBackground(view));
        view.findViewById(R.id.grid75).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid75).getTag()).getBackground(view));
        view.findViewById(R.id.grid76).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid76).getTag()).getBackground(view));
        view.findViewById(R.id.grid77).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid77).getTag()).getBackground(view));
        view.findViewById(R.id.grid78).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid78).getTag()).getBackground(view));
        view.findViewById(R.id.grid79).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid79).getTag()).getBackground(view));
        view.findViewById(R.id.grid80).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid80).getTag()).getBackground(view));
        view.findViewById(R.id.grid81).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid81).getTag()).getBackground(view));
        view.findViewById(R.id.grid82).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid82).getTag()).getBackground(view));
        view.findViewById(R.id.grid83).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid83).getTag()).getBackground(view));
        view.findViewById(R.id.grid84).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid84).getTag()).getBackground(view));
        view.findViewById(R.id.grid85).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid85).getTag()).getBackground(view));
        view.findViewById(R.id.grid86).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid86).getTag()).getBackground(view));
        view.findViewById(R.id.grid87).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid87).getTag()).getBackground(view));
        view.findViewById(R.id.grid88).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid88).getTag()).getBackground(view));
        view.findViewById(R.id.grid89).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid89).getTag()).getBackground(view));
        view.findViewById(R.id.grid90).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid90).getTag()).getBackground(view));
        view.findViewById(R.id.grid91).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid91).getTag()).getBackground(view));
        view.findViewById(R.id.grid92).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid92).getTag()).getBackground(view));
        view.findViewById(R.id.grid93).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid93).getTag()).getBackground(view));
        view.findViewById(R.id.grid94).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid94).getTag()).getBackground(view));
        view.findViewById(R.id.grid95).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid95).getTag()).getBackground(view));
        view.findViewById(R.id.grid96).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid96).getTag()).getBackground(view));
        view.findViewById(R.id.grid97).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid97).getTag()).getBackground(view));
        view.findViewById(R.id.grid98).setBackground(((BattleshipGameObject)view.findViewById(R.id.grid98).getTag()).getBackground(view));
        view.findViewById(R.id.grid99).setBackground(((BattleshipGameObject) view.findViewById(R.id.grid99).getTag()).getBackground(view));
    }


    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new MyShadowBuilder(view, new Point(view.getHeight()/2,view.getHeight()/2));
                view.startDrag(data, shadowBuilder, view,0);
                //view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:

                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();

                    ((ViewGroup)view).getChildCount();

                    //ViewGroup owner = (ViewGroup) view.getParent();
                    //owner.removeView(view);

                    //LinearLayout container = (LinearLayout) v;
                    //container.addView(view);
                    //view.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

}
