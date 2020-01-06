package com.lafilgroup.merchandisinginventory.about;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lafilgroup.merchandisinginventory.BuildConfig;
import com.lafilgroup.merchandisinginventory.R;

/**
 * Created by Ronald Remond Salas on 3/9/2018.
 */

public class About extends Fragment
{
    TextView lblVersion;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lblVersion=view.findViewById(R.id.lblVersion);
        lblVersion.setText("MobileDiser Version: " + BuildConfig.VERSION_NAME);
    }
}
