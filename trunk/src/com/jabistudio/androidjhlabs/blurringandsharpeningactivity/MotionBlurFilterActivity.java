package com.jabistudio.androidjhlabs.blurringandsharpeningactivity;

import com.jabistudio.androidjhlabs.SuperFilterActivity;
import com.jabistudio.androidjhlabs.filter.GaussianFilter;
import com.jabistudio.androidjhlabs.filter.MotionBlurFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MotionBlurFilterActivity extends SuperFilterActivity implements OnSeekBarChangeListener{
    private static final String TITLE = "MotionBlur";
    
    private static final String ANGLE_STRING = "ANGLE:";
    private static final String DISTANCE_STRING = "DISTANCE:";
    private static final String ROTATION_STRING = "ROTATION:";
    private static final String ZOOM_STRING = "ZOOM:";
    private static final int MAX_ANGLE_VALUE = 624;
    private static final int MAX_ROTATION_VALUE = 157;
    private static final int MAX_VALUE = 100;
    
    private static final int ANGLE_SEEKBAR_RESID = 21865;
    private static final int DISTANCE_SEEKBAR_RESID = 21866;
    private static final int ROTATION_SEEKBAR_RESID = 21867;
    private static final int ZOOM_SEEKBAR_RESID = 21868;
    
    private SeekBar mAngleSeekBar;
    private TextView mAngleTextView;
    private SeekBar mDistanceSeekBar;
    private TextView mDistanceTextView;
    private SeekBar mRotationSeekBar;
    private TextView mRotationTextView;
    private SeekBar mZoomSeekBar;
    private TextView mZoomTextView;
    
    private int mAngleValue;
    private int mDistanceValue;
    private int mRotationValue;
    private int mZoomValue;
    
    private ProgressDialog mProgressDialog;
    private int[] mColors;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TITLE);
        filterSeekBarSetup(mMainLayout);
    }

    /**
     * filterButtonSetting
     * @param mainLayout
     */
    private void filterSeekBarSetup(LinearLayout mainLayout){
        mAngleTextView = new TextView(this);
        mAngleTextView.setText(ANGLE_STRING+mAngleValue);
        mAngleTextView.setTextSize(TITLE_TEXT_SIZE);
        mAngleTextView.setTextColor(Color.BLACK);
        mAngleTextView.setGravity(Gravity.CENTER);
        
        mAngleSeekBar = new SeekBar(this);
        mAngleSeekBar.setOnSeekBarChangeListener(this);
        mAngleSeekBar.setId(ANGLE_SEEKBAR_RESID);
        mAngleSeekBar.setMax(MAX_ANGLE_VALUE);
        
        mDistanceTextView = new TextView(this);
        mDistanceTextView.setText(DISTANCE_STRING+mDistanceValue);
        mDistanceTextView.setTextSize(TITLE_TEXT_SIZE);
        mDistanceTextView.setTextColor(Color.BLACK);
        mDistanceTextView.setGravity(Gravity.CENTER);
        
        mDistanceSeekBar = new SeekBar(this);
        mDistanceSeekBar.setOnSeekBarChangeListener(this);
        mDistanceSeekBar.setId(DISTANCE_SEEKBAR_RESID);
        mDistanceSeekBar.setMax(MAX_VALUE);
        
        mRotationTextView = new TextView(this);
        mRotationTextView.setText(ROTATION_STRING+mRotationValue);
        mRotationTextView.setTextSize(TITLE_TEXT_SIZE);
        mRotationTextView.setTextColor(Color.BLACK);
        mRotationTextView.setGravity(Gravity.CENTER);
        
        mRotationSeekBar = new SeekBar(this);
        mRotationSeekBar.setOnSeekBarChangeListener(this);
        mRotationSeekBar.setId(ROTATION_SEEKBAR_RESID);
        mRotationSeekBar.setMax(MAX_ROTATION_VALUE);
        
        mZoomTextView = new TextView(this);
        mZoomTextView.setText(ZOOM_STRING+mZoomValue);
        mZoomTextView.setTextSize(TITLE_TEXT_SIZE);
        mZoomTextView.setTextColor(Color.BLACK);
        mZoomTextView.setGravity(Gravity.CENTER);
        
        mZoomSeekBar = new SeekBar(this);
        mZoomSeekBar.setOnSeekBarChangeListener(this);
        mZoomSeekBar.setId(ZOOM_SEEKBAR_RESID);
        mZoomSeekBar.setMax(MAX_VALUE);
        
        mainLayout.addView(mAngleTextView);
        mainLayout.addView(mAngleSeekBar);
        mainLayout.addView(mDistanceTextView);
        mainLayout.addView(mDistanceSeekBar);
        mainLayout.addView(mRotationTextView);
        mainLayout.addView(mRotationSeekBar);
        mainLayout.addView(mZoomTextView);
        mainLayout.addView(mZoomSeekBar);
    }
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch(seekBar.getId()){
        case ANGLE_SEEKBAR_RESID:
            mAngleValue = progress;
            mAngleTextView.setText(ANGLE_STRING+getAngle(mAngleValue));
            break;
        case DISTANCE_SEEKBAR_RESID:
            mDistanceValue = progress;
            mDistanceTextView.setText(DISTANCE_STRING+mDistanceValue);
            break;
        case ROTATION_SEEKBAR_RESID:
            mRotationValue = progress;
            mRotationTextView.setText(ROTATION_STRING+getRotation(mRotationValue));
            break;
        case ZOOM_SEEKBAR_RESID:
            mZoomValue = progress;
            mZoomTextView.setText(ZOOM_STRING+getDistance(mZoomValue));
            break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        final int width = mOriginalImageView.getDrawable().getIntrinsicWidth();
        final int height = mOriginalImageView.getDrawable().getIntrinsicHeight();
        
        mColors = AndroidUtils.drawableToIntArray(mOriginalImageView.getDrawable());
        mProgressDialog = ProgressDialog.show(this, "", "Wait......");
        
        Thread thread = new Thread(){
            public void run() {
                MotionBlurFilter filter = new MotionBlurFilter();
                filter.setAngle(getAngle(mAngleValue));
                filter.setDistance(mDistanceValue);
                filter.setRotation(getRotation(mRotationValue));
                filter.setZoom(getDistance(mZoomValue));

                mColors = filter.filter(mColors, width, height);
                MotionBlurFilterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setModifyView(mColors, width, height);
                    }
                });
                mProgressDialog.dismiss();
            }
        };
        thread.setDaemon(true);
        thread.start();        
    }
    
    private float getAngle(int value){
        float retValue = 0;
        retValue = (float)(value / 100f);
        return retValue;
    }
    
    private float getRotation(int value){
        float retValue = 0;
        retValue = (float)(value / 100f);
        return retValue;
    }
    
    private float getDistance(int value){
        float retValue = 0;
        retValue = (float)(value / 100f);
        return retValue;
    }
}
