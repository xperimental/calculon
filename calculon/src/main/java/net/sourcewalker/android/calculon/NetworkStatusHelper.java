package net.sourcewalker.android.calculon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusHelper extends BroadcastReceiver {

    public interface NetworkListener {

        void onNetworkChange(boolean connected);

    }

    private static final IntentFilter NETWORK_FILTER = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    private final Context context;
    private final ConnectivityManager networkManager;
    private boolean connected;
    private NetworkListener listener;

    public boolean isConnected() {
        return connected;
    }

    public NetworkListener getListener() {
        return listener;
    }

    public void setListener(NetworkListener listener) {
        this.listener = listener;
    }

    public NetworkStatusHelper(Context context) {
        this.context = context;
        this.networkManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        updateConnected();
    }

    public void onResume() {
        context.registerReceiver(this, NETWORK_FILTER);
    }

    public void onPause() {
        context.unregisterReceiver(this);
    }

    private void updateConnected() {
        NetworkInfo networkInfo = networkManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            connected = false;
        } else {
            connected = networkInfo.getState() == NetworkInfo.State.CONNECTED;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        updateConnected();
        if (listener != null) {
            listener.onNetworkChange(connected);
        }
    }

}
