package org.donntu.itt.dehax.wificonnectionmanager;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WifiConnectionInfoActivity extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connection_info);

        mTextView = findViewById(R.id.textView);

        Intent intent = getIntent();

        WifiInfo info = intent.getParcelableExtra("INFO");

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        info = wifiManager.getConnectionInfo();

        String text = "Connected to " + info.getSSID() + " (" + info.getBSSID() + ")";
        mTextView.setText(text);
    }
}
