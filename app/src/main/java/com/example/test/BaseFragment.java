package com.example.test;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class BaseFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    private Snackbar snackBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(new ConnectivityReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityReceiver.connectivityReceiverListener = this;
    }

    private void showNetworkMessage(boolean isConnected) {
        if (!isConnected) {
            snackBar = Snackbar.make(getView(), "You are offline", Snackbar.LENGTH_LONG);
            snackBar.setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE);
            snackBar.show();
        } else {
            if(snackBar != null)
                snackBar.dismiss();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showNetworkMessage(isConnected);
    }
}
