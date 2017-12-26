package com.dkzy.areaparty.phone.bluetoothxie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dkzy.areaparty.phone.R;

/**
 * Created by a c e r on 2017/4/10.
 */

public class PlaceholderFragment02 extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initView();
    }
    private void initView(){

    }
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */

    public PlaceholderFragment02() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.bluetooth_otherfragment, container, false);

        return rootView;
    }
}
