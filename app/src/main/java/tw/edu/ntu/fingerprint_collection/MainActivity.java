package tw.edu.ntu.fingerprint_collection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView wifi_tv;
    private EditText file_et;
    private EditText position_x_et;
    private EditText position_y_et;
    private Button record_bt;
    private Button check_bt;
    private WifiManager wifiManager;
    private BroadcastReceiver wifiScanReceiver;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };
    }

    private void findViews(){
        wifi_tv = findViewById(R.id.wifi_tv);
        wifi_tv.setMovementMethod(new ScrollingMovementMethod());
        file_et = findViewById(R.id.file_et);
        position_x_et = findViewById(R.id.position_x_et);
        position_y_et = findViewById(R.id.position_y_et);
        record_bt = findViewById(R.id.record_bt);
        check_bt = findViewById(R.id.check_bt);
        record_bt.setClickable(false);
    }


    public void checkFileName(View view){
        if(file_et.getText().toString().isEmpty()){
            Toast.makeText(this, "please enter the saving name", Toast.LENGTH_LONG).show();
        }else{
            fileName = file_et.getText().toString();
            record_bt.setClickable(true);

            Toast.makeText(this, "Now you can collect fingerprints", Toast.LENGTH_LONG).show();
        }
    }

    public void record(View view){
        String position_x = position_x_et.getText().toString();
        String position_y = position_y_et.getText().toString();
        if(position_x.isEmpty() || position_y.isEmpty()){
            Toast.makeText(this, "please enter the position", Toast.LENGTH_LONG).show();
        }else{
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            registerReceiver(wifiScanReceiver, intentFilter);

            boolean success = wifiManager.startScan();
            if (!success) {
                // scan failure handling
                scanFailure();
            }
        }
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        String position_x = position_x_et.getText().toString();
        String position_y = position_y_et.getText().toString();

        String show = "Position" + "," + position_x + "," + position_y + "\n";
        for(ScanResult result: results){
            show += result.BSSID + "," + result.level + "\n";
        }
        wifi_tv.setText(show);
        try {
            FileOutputStream fos = openFileOutput(fileName, getApplicationContext().MODE_APPEND);
            fos.write(show.getBytes());
            fos.close();
        }catch (IOException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void scanFailure() {
        List<ScanResult> results = wifiManager.getScanResults();
        String position_x = position_x_et.getText().toString();
        String position_y = position_y_et.getText().toString();

        String show = "Position" + "," + position_x + "," + position_y + "\n";
        for(ScanResult result: results){
            show += result.BSSID + "," + result.level + "\n";
        }
        wifi_tv.setText(show);
        try {
            FileOutputStream fos = openFileOutput(fileName, getApplicationContext().MODE_APPEND);
            fos.write(show.getBytes());
            fos.close();
        }catch (IOException e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

}