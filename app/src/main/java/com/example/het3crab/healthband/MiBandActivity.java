package com.example.het3crab.healthband;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.NotifyListener;
import com.zhaoxiaodan.miband.model.VibrationMode;

import java.util.Arrays;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

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

    public void laczenie(View view) {
        connectToMiBand();

    }

    public void nasluchuj(View view) {
        //helper.sendSms("sd");
      // helper.writeData(Consts.UUID_SERVICE_HEARTBEAT, Consts.UUID_START_HEARTRATE_CONTROL_POINT, new byte[]{21, 1, 1} );
        //helper.writeData(Consts.UUID_SERVICE_HEARTBEAT, Consts.UUID_SENSOR_CHARACTERISTIC, new byte[]{1, 3, 25} );
        //helper.readData(Consts.UUID_SERVICE_HEARTBEAT, Consts.UUID_START_HEARTRATE_CONTROL_POINT);
        helper.getNotificationsWithDescriptor(Consts.UUID_SERVICE_HEARTBEAT, Consts.UUID_NOTIFICATION_HEARTRATE, Consts.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);
        //helper.readData(Consts.UUID_SERVICE_MIBAND_SERVICE, Consts.UUID_CHARACTERISTIC_7_REALTIME_STEPS);
        //helper.startScanHeartRate();
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

    public void odczytPulsu(View view){
        helper.writeData(Consts.UUID_SERVICE_HEARTBEAT, Consts.UUID_START_HEARTRATE_CONTROL_POINT, new byte[]{21, 2, 1} );
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
        UUID alertUUID = characteristic.getUuid();
        Log.d("odczyt", " - odczyt: " + Arrays.toString(characteristic.getValue()));

        final RealmPulseReading pulse = new RealmPulseReading();
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        long x = now.getTime();
        pulse.setDate(x);
        pulse.setValue((int)(characteristic.getValue()[1]));

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm realm = Realm.getInstance(realmConfiguration);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                    realm.insertOrUpdate(pulse);
            }
        });
    }
}
