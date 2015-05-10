package ch.hslu.mpbro15.team10.battleship.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.games.Games;

import ch.hslu.mpbro15.team10.battleship.R;
import ch.hslu.mpbro15.team10.battleship.activities.MultiplayerActivity;
import ch.hslu.mpbro15.team10.battleship.basegame.BaseMultiplayerAcitvity;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ch.hslu.mpbro15.team10.battleship.fragments.MultiplayerSignedInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiplayerSignedInFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MultiplayerFragment.
     */
    public static MultiplayerSignedInFragment newInstance() {
        MultiplayerSignedInFragment fragment = new MultiplayerSignedInFragment();
        return fragment;
    }

    public MultiplayerSignedInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signedin, container, false);

        ((Button) view.findViewById(R.id.button_sign_out))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonSignOutClick();
                    }
                });
        ((Button) view.findViewById(R.id.button_invite_players))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonInvitePlayersClick();
                    }
                });
        ((Button) view.findViewById(R.id.button_see_invitations))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buttonSeeInvitionsClick();
                    }
                });

        return view;
    }

    private void buttonInvitePlayersClick() {
        if (getActivity() != null) {
            Log.d(this.getClass().getName(), "Calling intent for player invitation");
            MultiplayerActivity mAct = (MultiplayerActivity) getActivity();
            Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(
                    mAct.playConManager.client,
                    MultiplayerActivity.GooglePlayRoomManager.ROOM_MIN_PLAYERS,
                    MultiplayerActivity.GooglePlayRoomManager.ROOM_MAX_PLAYERS);
            mAct.startActivityForResult(intent, BaseMultiplayerAcitvity.RC_PLAYER_INVITATION);
        }
    }

    private void buttonSeeInvitionsClick() {
        if (getActivity() != null) {
            Log.d(this.getClass().getName(), "Calling intent for received invitations");
            MultiplayerActivity mAct = (MultiplayerActivity) getActivity();
            Intent intent = Games.Invitations.getInvitationInboxIntent(mAct.playConManager.client);
            mAct.startActivityForResult(intent, BaseMultiplayerAcitvity.RC_SEE_INVITATIONS);
        }
    }

    private void buttonSignOutClick() {
        if (getActivity() != null) {
            Log.d(this.getClass().getName(), "Signing player out from google play");
            MultiplayerActivity mAct = (MultiplayerActivity) getActivity();
            mAct.playConManager.signOut();
        }
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
    }
}
