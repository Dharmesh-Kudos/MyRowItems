package smart.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tasol on 26/5/15.
 */
public class SmartInternetConnectionStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SmartUtils.setNetworkStateAvailability(context);
        Intent broadcastIntent = new Intent("NetworkState");
        context.sendBroadcast(broadcastIntent);


    }


}
