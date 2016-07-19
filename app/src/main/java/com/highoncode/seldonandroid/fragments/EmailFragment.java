package com.highoncode.seldonandroid.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.highoncode.seldonandroid.R;
import com.highoncode.seldonandroid.activities.SeldonActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EmailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmailFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "paramEmail";

    private String mEmail;

    private Animation mFadeIn;



    private OnFragmentInteractionListener mListener;


    private TextView mTxtEmail;
    private Button mNextButton;


    public EmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment EmailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmailFragment newInstance(String email) {
        EmailFragment fragment = new EmailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_PARAM1);
        }
    }

    private void setUpSubViews(View view) {
        mTxtEmail = (EditText) view.findViewById(R.id.fragment_email_txt_email);
        mNextButton = (Button) view.findViewById(R.id.fragment_email_btn_next);
        mNextButton.setOnClickListener(this);
        setUpEmailEnteredListener();

        mFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        hideNextButton(mEmail == null);
    }

    private void hideNextButton(boolean hidden) {

            if (!hidden) {
                mNextButton.setVisibility(View.VISIBLE);
                mNextButton.startAnimation(mFadeIn);
            } else {
                mNextButton.setVisibility(View.GONE);
            }

    }


    private void setUpEmailEnteredListener() {
        mTxtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (isEmailValid(v.getText())) {
                        System.out.println("This is a valid email - saved");
                        mEmail = v.getText().toString();
                        ((SeldonActivity)getActivity()).setmRecipientEmail(mEmail);
                        hideNextButton(false);
                    } else {
                        System.out.println("This is NOT a valid email");
                        hideNextButton(true);

                    }
                }
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragmentView =  inflater.inflate(R.layout.fragment_email, container, false);
        setUpSubViews(thisFragmentView);
        return thisFragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_email_btn_next:
                ((SeldonActivity)getActivity()).setmRecipientEmail(mEmail);
                ((SeldonActivity)getActivity()).goToNextPage();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
