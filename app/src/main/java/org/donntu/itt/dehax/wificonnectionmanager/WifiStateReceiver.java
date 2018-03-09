package org.donntu.itt.dehax.wificonnectionmanager;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class WifiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        assert action != null;
        switch (action) {
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);

                switch (state) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        showNotification(context, "Wi-Fi state changed", "Wi-Fi enabled!");
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        showNotification(context, "Wi-Fi state changed", "Wi-Fi disabled!");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        showNotification(context, "Wi-Fi state changed", "Wi-Fi is currently being enabled");
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        showNotification(context, "Wi-Fi state changed", "Wi-Fi is currently being disabled");
                        break;
                    default:
                        Toast.makeText(context, "Error! Unsupported state!", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION:
                Toast.makeText(context, "SUPPLICANT_CONNECTION_CHANGE_ACTION", Toast.LENGTH_SHORT).show();
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
                    WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);

                    if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED && wifiInfo.getBSSID() != null) {
                        Intent activityIntent = new Intent(context, WifiConnectionInfoActivity.class);
                        activityIntent.putExtra("INFO", wifiInfo);
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.getApplicationContext().startActivity(activityIntent);
                    }
                }
                break;
            default:
                Toast.makeText(context, "Error! Unknown action!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void showNotification(Context context, String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "12")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(title)
                        .setContentText(text);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.notify(1, mBuilder.build());
    }
}
