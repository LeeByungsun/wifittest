package net.yap.wifitest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {
  private String TAG = "wifitest";
  private WifiManager mWifimanager;
  private Button mStart_btn;
  private Button mStop_btn;
  private TextView mReslut_tv;
  private List<ScanResult> mScanResult;
  private int mScanount = 0;
  private int REQUEST_CODE_LOCATION = 101;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    checkpermission();
    mWifimanager = (WifiManager) getSystemService(WIFI_SERVICE);//초기화
    if (mWifimanager.isWifiEnabled() == false) {
      mWifimanager.setWifiEnabled(true);
    }
    mStart_btn = (Button) findViewById(R.id.search_btn);
    mStop_btn = (Button) findViewById(R.id.stop_btn);
    mReslut_tv = (TextView) findViewById(R.id.result);
    mStart_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startscan();
      }
    });

    mStop_btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mWifimanager != null) {
          unregisterReceiver(mWifiReceiver);
        }
      }
    });

  }

  private void startscan() {
    mScanount = 0;
    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    registerReceiver(mWifiReceiver, filter);
    mWifimanager.startScan();

  }

  private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
        getWifiScanResult();
        mWifimanager.startScan();
      } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
        sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
      }
    }
  };

  public void getWifiScanResult() {
    int size = 0;
    mScanount++;
    mScanResult = mWifimanager.getScanResults();
    size = mScanResult.size();
    Log.e(TAG, "result count" + mScanount);
    mReslut_tv.setText("Count = "+mScanount+"\n ");
    for (int i = 0; i < size; i++) {
      ScanResult result = mScanResult.get(i);
      Log.e(TAG, "SSID : " + result.SSID.toString() +" MAC : "+result.BSSID +" RSSI : " + result.level );
      mReslut_tv.append((i + 1) + " SSID : " + result.SSID.toString()+" MAC : " + result.BSSID + " RSSI : " +result.level +" dBm\n");
    }

  }

  private void checkpermission() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
        Toast.makeText(this, "get user location", Toast.LENGTH_SHORT).show();
      }
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);

    } else {

    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_CODE_LOCATION) {
      if (grantResults.length == 1
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

      }
    }
  }
}
