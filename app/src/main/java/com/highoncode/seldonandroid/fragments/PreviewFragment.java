package com.highoncode.seldonandroid.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.highoncode.seldonandroid.R;
import com.highoncode.seldonandroid.activities.SeldonActivity;
import com.highoncode.seldonandroid.utility.IAPHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviewFragment extends DialogFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "paramRecipientEmail";
    private static final String ARG_PARAM2 = "paramSendDate";

    private static final int TAG_DIALOG_DATE_PICKER = 1;

    private static final String RECORDING_OUTPUT_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.3gp";
    private static final String LOG_TAG = "AudioRecording";




    // TODO: Rename and change types of parameters
    private String mRecipientEmail;
    private Date mSendDate;

    private EditText mTxtMessage;
    private EditText mTxtRecipientEmail;
    private EditText mTxtSendDate;
    private Button mButtonSend;

    private MediaPlayer mPlayer = null;
    private boolean mIsPlaying = false;

    private OnFragmentInteractionListener mListener;

    public PreviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramRecipientEmail Parameter 1.
     * @param paramSendDate Parameter 2.
     * @return A new instance of fragment PreviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PreviewFragment newInstance(String paramRecipientEmail, Date paramSendDate) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, paramRecipientEmail);
        args.putSerializable(ARG_PARAM2, paramSendDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRecipientEmail = getArguments().getString(ARG_PARAM1);
            mSendDate = (Date) getArguments().getSerializable(ARG_PARAM2);
        }

        System.out.println("Bundle Details: \n" +
                "Email: " + mRecipientEmail + "\n" +
                "Send Date: " + mSendDate + "\n");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragmentView = inflater.inflate(R.layout.fragment_preview, container, false);
        setUpSubviews(thisFragmentView);
        return thisFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
            case R.id.fragment_preview_txt_message:
                mIsPlaying = !mIsPlaying;
                onPlay(mIsPlaying);
                break;
            case R.id.fragment_preview_txt_date:
                DatePickerDialog datePickerDialog = (DatePickerDialog) onCreateDialog(TAG_DIALOG_DATE_PICKER);
                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.DATE, 1);
                datePickerDialog.getDatePicker().setMinDate(gc.getTime().getTime());
                datePickerDialog.show();
                break;
            case R.id.fragment_preview_btn_send:
                if (!IAPHandler.purchaseItem()) {
                    System.out.println("CANNOT START PURCHASE, REASONS: \n" +
                            "1. Product not available \n" +
                            "2. No internet connection");
                }
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



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            int mMessageDuration = getLengthOfAudioInSeconds() / 1000;
            mTxtMessage.setText(String.format("%d second message", mMessageDuration));
        }
    }

    private void setUpSubviews(View view) {
        mTxtMessage = (EditText) view.findViewById(R.id.fragment_preview_txt_message);
        mTxtMessage.setOnClickListener(this);

        mTxtRecipientEmail = (EditText) view.findViewById(R.id.fragment_preview_txt_email);
        mTxtRecipientEmail.setText(mRecipientEmail);
        setUpEmailEnteredListener();

        mTxtSendDate = (EditText) view.findViewById(R.id.fragment_preview_txt_date);
        mTxtSendDate.setOnClickListener(this);
        DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String date = df.format(mSendDate);
        mTxtSendDate.setText(date);

        mButtonSend = (Button) view.findViewById(R.id.fragment_preview_btn_send);
        mButtonSend.setOnClickListener(this);

    }


    //    AUDIO PLAYING


    private int getLengthOfAudioInSeconds() {
        Uri uri = Uri.parse(RECORDING_OUTPUT_FILE_NAME);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(getActivity(),uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr);
    }




    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {

        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Audio Started Playing", Toast.LENGTH_SHORT);
        toast.show();

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(RECORDING_OUTPUT_FILE_NAME);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mIsPlaying = false;

        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Audio Stopped Playing", Toast.LENGTH_SHORT);
        toast.show();

        mPlayer.release();
        mPlayer = null;
    }

    // DATE

    protected Dialog onCreateDialog(int id) {
        // Use the current date as the default date in the picker
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(mSendDate);
        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH);
        int day = gc.get(Calendar.DAY_OF_MONTH);

        switch (id) {
            case TAG_DIALOG_DATE_PICKER:
                return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
            default:
                return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            mSendDate = calendar.getTime();

            DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
            String date = df.format(calendar.getTime());
            mTxtSendDate.setText(date);
        }
    };


    // EMAIL

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setUpEmailEnteredListener() {
        mTxtRecipientEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (isEmailValid(v.getText())) {
                        System.out.println("This is a valid email - saved");
                        ((SeldonActivity)getActivity()).setmRecipientEmail(v.getText().toString());
                        hideSendButton(false);
                    } else {
                        System.out.println("This is NOT a valid email");
                        hideSendButton(true);
                    }
                }
                return false;
            }
        });
    }


    private void hideSendButton(boolean hidden) {
            if (!hidden) {
                mButtonSend.setVisibility(View.VISIBLE);
            } else {
                mButtonSend.setVisibility(View.GONE);
            }

            mButtonSend.invalidate();
    }
}
