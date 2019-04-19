package com.example.kunrui.apcheck;

import android.annotation.SuppressLint;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kunrui.apcheck.MethodsClass.FileOpr;

public class FragmentWait extends Fragment {
    private ImageView load;
    private Handler mHandler;
    private String explain;
    private FileOpr fileOpr = new FileOpr();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.side_wait, container, false);
        return view;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        load = getActivity().findViewById(R.id.load);
        rotate(load, 360);
        explain = getActivity().getString(R.string.explain);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Toast.makeText(getActivity(), explain, Toast.LENGTH_SHORT).show();
                        load.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        };
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(fileOpr.read_status("BSSID").equals("none") && fileOpr.read_status("AppPath").equals("homeWeb")) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    public void rotate(ImageView view, int Degrees) {
        //旋转GIF设置
        Animation rotateAnimation = new RotateAnimation(0, Degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(1000);  //旋转时间
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        view.startAnimation(rotateAnimation);
    }
}