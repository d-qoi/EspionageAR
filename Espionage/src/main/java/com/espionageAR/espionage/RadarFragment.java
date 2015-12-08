package com.espionageAR.espionage;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Louis on 12/7/15.
 */
public class RadarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new RadarSurfaceView(getActivity());
    }
}
