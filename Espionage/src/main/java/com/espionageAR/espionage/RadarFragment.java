package com.espionageAR.espionage;

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

    private Radar mRadar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        mRadar = (Radar) view.findViewById(R.id.radar_fragment);
        return view;
    }


    //These two methods expose the Radar class to higher functions.

    //Points are passed in as [ID, X-pos, Y-pos]
    public void setPointArray(long[] rawPoints){
        mRadar.setPointArray(rawPoints);
    }

    //Arcs are passed as [Angle Start, Angle Duration]
    public void setArcArray(long[] rawArc){
        mRadar.setArcArray(rawArc);
    }

}
