package com.example.het3crab.healthband;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.Toast;

import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.NotifyListener;
import com.zhaoxiaodan.miband.model.VibrationMode;

public class MiBandActivity extends AppCompatActivity implements BLEMiBand2Helper.BLEAction {

    private MiBand miband;
    private BluetoothDevice device;
    private ScanCallback scanCallback;
    private Notifications mNotifications;

    Handler handler = new Handler(Looper.getMainLooper());
    BLEMiBand2Helper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_band);
        
        mNotifications = new Notifications(this);
        
        if (ActivityCompat.checkSelfPermission(MiBandActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MiBandActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MiBandActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            // Write you code here if permission already given.
        }
        requestHehe();

//        final BluetoothDevice device = intent.getParcelableExtra("device");
       /* scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                device = result.getDevice();
                String name = device.getName();
                int x = 5;
                if(name!=null){
                    if(name.contains("MI1S"))
                    {
                        MiBand.stopScan(scanCallback);
                        connectToMiBand();
                    }
                }

            }
        };*/



    }
    void connectToMiBand() {

        helper = new BLEMiBand2Helper(MiBandActivity.this, handler);
        helper.addListener(this);


        // Setup Bluetooth:
        helper.connect();

    }



    void requestHehe(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
        if (!mBluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);

        }
    }

    public void onClick(View view) {
        connectToMiBand();

    }

    public void onClick2(View view) {
        helper.sendSms("sd");

//        miband.getBatteryInfo(new ActionCallback() {
//
//            @Override
//            public void onSuccess(Object data) {
//                BatteryInfo info = (BatteryInfo) data;
//                Log.d("Siema", info.toString());
//            }
//
//            @Override
//            public void onFail(int errorCode, String msg) {
//                Log.d("Elo", "getBatteryInfo fail");
//            }
//        });
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {

    }

    @Override
    public void onNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }
}
