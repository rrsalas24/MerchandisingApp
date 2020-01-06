package com.lafilgroup.merchandisinginventory.offline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lafilgroup.merchandisinginventory.R;
import com.lafilgroup.merchandisinginventory.config.MessageDialog;
import com.lafilgroup.merchandisinginventory.sqlconfig.OfflineInventoryHeaderSQL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RR SALAS on 3/1/2019.
 */

public class OfflineSave extends Fragment
{
    ListView lvwOffline;
    OfflineInventoryHeaderSQL offlineInventoryHeaderSQL;
    OfflineSaveAdapter offlineSaveAdapter;
    public static boolean submitted;
    MessageDialog messageDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_offline_save,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        lvwOffline=view.findViewById(R.id.lvwOffline);
        submitted=false;
        getOffline();
    }

    public void getOffline()
    {
        offlineInventoryHeaderSQL=new OfflineInventoryHeaderSQL(getActivity());
        ArrayList<OfflineInventoryHeaderSQL.OfflineInventoryHeaderDTO> dto = offlineInventoryHeaderSQL.offlineList(offlineInventoryHeaderSQL);
        offlineSaveAdapter=new OfflineSaveAdapter(getActivity(),dto);
        lvwOffline.setAdapter(offlineSaveAdapter);
    }

    @Override
    public void onResume()
    {
        if (submitted==true)
        {
            messageDialog=new MessageDialog(getActivity(),"Success","Inventory was successfully posted.");
            messageDialog.messageSuccess();
            submitted=false;
            getOffline();
        }
        super.onResume();
    }
}
