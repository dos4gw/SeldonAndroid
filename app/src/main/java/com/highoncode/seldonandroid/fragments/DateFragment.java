package com.highoncode.seldonandroid.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.highoncode.seldonandroid.R;
import com.highoncode.seldonandroid.activities.SeldonActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateFragment extends DialogFragment implements  View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "paramDate";

    private static final int TAG_DIALOG_DATE_PICKER = 1;

    // TODO: Rename and change types of parameters
    private Date mDate;

    private EditText mTxtPickDate;

    private Animation mFadeIn;

    private Button mButtonNext;

    private void hideNextButton(boolean hidden) {
            if (!hidden) {
                mButtonNext.setVisibility(View.VISIBLE);
                mButtonNext.startAnimation(mFadeIn);
            } else {
                mButtonNext.setVisibility(View.GONE);
            }
    }

    private OnFragmentInteractionListener mListener;

    public DateFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramDate Parameter 1.
     * @return A new instance of fragment DateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DateFragment newInstance(Date paramDate) {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, paramDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDate = (Date) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisFragmentView = inflater.inflate(R.layout.fragment_date, container, false);
        setUpSubViews(thisFragmentView);
        return thisFragmentView;
    }

    private void setUpSubViews(View view) {
        mTxtPickDate = (EditText) view.findViewById(R.id.fragment_date_txt_pickDate);
        mButtonNext = (Button) view.findViewById(R.id.fragment_date_btn_next);
        mButtonNext.setOnClickListener(this);
        mTxtPickDate.setOnClickListener(this);
        mFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        hideNextButton(mDate == null);


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


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            mDate = calendar.getTime();
            ((SeldonActivity)getActivity()).setmSendDate(mDate);

            DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
            String date = df.format(calendar.getTime());
            mTxtPickDate.setText(date);
            hideNextButton(false);
        }
    };

    protected Dialog onCreateDialog(int id) {
        // Use the current date as the default date in the picker
        final GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);


        if (mDate != null) {
            gc.setTime(mDate);
        }

        int year = gc.get(Calendar.YEAR);
        int month = gc.get(Calendar.MONTH);
        int day = gc.get(Calendar.DAY_OF_MONTH);


        switch (id) {
            case TAG_DIALOG_DATE_PICKER:
                return new DatePickerDialog(getActivity(),  R.style.DialogTheme, dateSetListener, year, month, day);
            default:
                return new DatePickerDialog(getActivity(),  R.style.DialogTheme, dateSetListener, year, month, day);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_date_txt_pickDate:
                DatePickerDialog datePickerDialog = (DatePickerDialog) onCreateDialog(TAG_DIALOG_DATE_PICKER);

                GregorianCalendar gc = new GregorianCalendar();
                gc.add(Calendar.DATE, 1);

                datePickerDialog.getDatePicker().setMinDate(gc.getTime().getTime());
                datePickerDialog.show();

                break;
            case R.id.fragment_date_btn_next:
                ((SeldonActivity)getActivity()).setmSendDate(mDate);
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
}
