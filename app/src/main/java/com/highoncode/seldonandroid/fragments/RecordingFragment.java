package com.highoncode.seldonandroid.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import com.highoncode.seldonandroid.R;
import com.highoncode.seldonandroid.activities.SeldonActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordingFragment extends Fragment implements View.OnClickListener {



    private static final String RECORDING_OUTPUT_FILE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecordtest.3gp";
    private static final String LOG_TAG = "AudioRecording";


    private OnFragmentInteractionListener mListener;

    private boolean isRecording;
    private Button mRecordButton;
    private TextView mCountDownLabel;
    private TextView mBodyTextView;

    private CountDownTimer mPreRecordingCountDownTimer;
    private CountDownTimer mRecordingCountDownTimer;

    private int preRecordingCountDownValue = 3;
    private int recordingCountDownValue = 60;


    private MediaRecorder mRecorder;
    public RecordingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecordingFragment.
     */
    public static RecordingFragment newInstance() {
        RecordingFragment fragment = new RecordingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragmentView =  inflater.inflate(R.layout.fragment_recording, container, false);
        setUpSubviews(thisFragmentView);
        return thisFragmentView;
    }



    private void setUpSubviews(View view) {
        mRecordButton = (Button) view.findViewById(R.id.fragment_recording_btn_record);
        mRecordButton.setOnClickListener(this);

        mBodyTextView = (TextView) view.findViewById(R.id.fragment_recording_txt_body);
        mCountDownLabel = (TextView) view.findViewById(R.id.fragment_recording_txt_countdown);
        setUpPreRecordingCountdown();
        setUpRecordingCountdown();
    }


    private void updateCountdownLabel(int timeRemaining, boolean counting, boolean recording) {
        if (!recording) {
            if (counting) {
                mCountDownLabel.setTextColor(Color.BLACK);
                mCountDownLabel.setText(String.format("%s.", String.valueOf(timeRemaining)));
            } else {
                mRecordButton.setVisibility(View.VISIBLE);
                // TODO: add to strings.xml
                mCountDownLabel.setText("60 seconds left.");
                mRecordButton.setText("Stop recording.");
                mBodyTextView.setText(R.string.fragment_recording_txt_body_is_recording);
            }
        } else {
            if (counting) {
                // TODO: add to strings.xml
                if (timeRemaining <= 10) {
                    mCountDownLabel.setTextColor(Color.RED);
                }

                // TODO: add to strings.xml
                mCountDownLabel.setText(String.format("%d seconds left.", timeRemaining));
            }
        }
    }

    private void setUpPreRecordingCountdown() {
        // sets up a 3 second countdown timer.
        mPreRecordingCountDownTimer = new CountDownTimer(3000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (Math.round((float)millisUntilFinished / 1000.0f) != preRecordingCountDownValue)  {
                    preRecordingCountDownValue = Math.round((float)millisUntilFinished / 1000.0f);
                    updateCountdownLabel(preRecordingCountDownValue + 1, true, false);
                    Log.d( getString(R.string.app_name) ,"Show " + preRecordingCountDownValue + " seconds remaining");
                }

            }

            @Override
            public void onFinish() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d(getString(R.string.app_name) ,"Timer finished, starting recording.");
                updateCountdownLabel(0, false, false);
                startRecording();
               // resets the timer
                preRecordingCountDownValue = 3;

            }
        };
    }


    private void setUpRecordingCountdown() {
        // sets up a 3 second countdown timer.
        mRecordingCountDownTimer = new CountDownTimer(60000, 250) {
            @Override
            public void onTick(long millisUntilFinished) {

                if (Math.round((float)millisUntilFinished / 1000.0f) != recordingCountDownValue)  {
                    recordingCountDownValue = Math.round((float)millisUntilFinished / 1000.0f);
                    updateCountdownLabel(recordingCountDownValue, true, true);
                    Log.d( getString(R.string.app_name) ,"Show " + recordingCountDownValue + " seconds remaining");
                }
            }

            @Override
            public void onFinish() {
                Log.d(getString(R.string.app_name) ,"Timer finished, recording end");
                updateCountdownLabel(0, false, true);
                stopRecording();
                goToPreview();
                // resets the timer
                recordingCountDownValue = 60;

            }
        };
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
            case R.id.fragment_recording_btn_record:
                recordButtonPressed();
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


    /** RECORDING SHIT */

    private void onRecord(boolean start) {
        if (start) {
            if (appCanRecord()) {
                isRecording = true;
                startRecordingCountdown();
            } else {
                isRecording = false;
                requestPermissions();
            }
        } else {
            stopRecording();
            goToPreview();
        }
    }

    public void startRecordingCountdown() {
        mRecordButton.setVisibility(View.GONE);
        mCountDownLabel.setText("3.");
        mCountDownLabel.setVisibility(View.VISIBLE);
        mPreRecordingCountDownTimer.start();
    }

    private boolean appCanRecord() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    private void startRecording() {

            isRecording = true;

            Log.e(LOG_TAG, "startRecording()");
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(RECORDING_OUTPUT_FILE_NAME);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
                mRecordingCountDownTimer.start();
                mRecorder.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Media player failed setup" + e.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
    }

    public void stopRecording() {

        mRecordingCountDownTimer.cancel();
        recordingCountDownValue = 60;
        preRecordingCountDownValue = 3;

        isRecording = false;

        Log.e(LOG_TAG, "stopRecording()");
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }

        mCountDownLabel.setVisibility(View.INVISIBLE);
        mRecordButton.setText("Start recording.");
        mRecordButton.setVisibility(View.VISIBLE);
        mBodyTextView.setText(R.string.fragment_recording_txt_body_not_recording);
    }

    private void goToPreview() {
        ((SeldonActivity)getActivity()).goToNextPage();
    }

    private void recordButtonPressed() {
        isRecording = !isRecording;
        onRecord(isRecording);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {

            if (mPreRecordingCountDownTimer != null) {
                mPreRecordingCountDownTimer.cancel();
            }
            if (mRecordingCountDownTimer != null) {
                   mRecordingCountDownTimer.cancel();
            }

            if (isRecording) {
                stopRecording();
            }

        }
    }



}
