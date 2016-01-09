package com.espionageAR.espionage;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is where the radar is set up as a fragment.
 *
 */

public class RadarFragment extends Fragment {

    private GLSurfaceView mSurfaceView;
    private GLSurfaceView mGLView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new Radar(getActivity());
    }



}
