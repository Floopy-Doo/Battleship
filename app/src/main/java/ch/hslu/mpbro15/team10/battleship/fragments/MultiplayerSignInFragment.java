package ch.hslu.mpbro15.team10.battleship.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.SignInButton;

import ch.hslu.mpbro15.team10.battleship.R;
import ch.hslu.mpbro15.team10.battleship.activities.MultiplayerActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultiplayerSignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiplayerSignInFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MultiplayerFragment.
     */
    public static MultiplayerSignInFragment newInstance() {
        MultiplayerSignInFragment fragment = new MultiplayerSignInFragment();
        return fragment;
    }

    public MultiplayerSignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        SignInButton btnSignIn = (SignInButton) view.findViewById(R.id.button_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    MultiplayerActivity activity = (MultiplayerActivity) getActivity();
                    activity.playConManager.signIn();
                }
            }
        });


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
    }
}
