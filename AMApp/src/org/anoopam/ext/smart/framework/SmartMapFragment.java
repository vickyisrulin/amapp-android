package org.anoopam.ext.smart.framework;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidmapsextensions.SupportMapFragment;

public class SmartMapFragment extends SupportMapFragment {


    private static OnMapReadyListener target;
    public SmartMapFragment() {
        super();
    }

    public static SmartMapFragment newInstance(OnMapReadyListener target) {
        SmartMapFragment.target=target;
        SmartMapFragment fragment = new SmartMapFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
        View v = super.onCreateView(arg0, arg1, arg2);

        target.onMapReady();
        return v;
    }



}
