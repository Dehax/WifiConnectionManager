package org.donntu.itt.dehax.wificonnectionmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class WifiListActivity extends AppCompatActivity {

    private ListView wifiListView;
    private Button refreshButton;

    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanResultsReceiver;
    private BroadcastReceiver wifiStateReceiver;

    private List<ScanResult> scanResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);

        wifiListView = findViewById(R.id.wifiListView);
        refreshButton = findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!wifiManager.startScan()) {
                    Toast.makeText(WifiListActivity.this, "FAILED startScan()", Toast.LENGTH_SHORT).show();
                }
            }
        });
        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
                String networkSSID = "\"" + scanResultList.get((int) l).SSID + "\"";

                WifiConfiguration foundWifiConfiguration = null;
                for (int index = 0; index < configurations.size(); index++) {
                    WifiConfiguration wifiConfiguration = configurations.get(index);
                    if (wifiConfiguration.SSID.equals(networkSSID)) {
                        foundWifiConfiguration = wifiConfiguration;
                        break;
                    }
                }

                if (foundWifiConfiguration != null) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(foundWifiConfiguration.networkId, true);
                    wifiManager.reconnect();
                } else {
                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = networkSSID;
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                    int networkId = wifiManager.addNetwork(conf);

                    wifiManager.disconnect();
                    wifiManager.enableNetwork(networkId, true);
                    wifiManager.reconnect();
                }
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        wifiScanResultsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                assert action != null;

                switch (action) {
                    case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                        boolean updated = true;

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            updated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                        }

                        if (!updated) {
                            return;
                        }

                        WifiListActivity.this.onWifiScanResultsReceived();

                        break;
                }
            }
        };
        wifiStateReceiver = new WifiStateReceiver();

        IntentFilter filterWifiState = new IntentFilter();
        filterWifiState.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filterWifiState.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        filterWifiState.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, filterWifiState);

        registerReceiver(wifiScanResultsReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(wifiScanResultsReceiver);
        unregisterReceiver(wifiStateReceiver);
    }

    public void onWifiScanResultsReceived() {
        scanResultList = wifiManager.getScanResults();
        String[] bssidArray = new String[scanResultList.size()];
        for (int i = 0; i < bssidArray.length; i++) {
            ScanResult scanResult = scanResultList.get(i);
            bssidArray[i] = scanResult.SSID + " (" + scanResult.BSSID + ")";
        }
        wifiListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bssidArray));
    }
}
