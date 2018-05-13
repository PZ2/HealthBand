package com.example.het3crab.healthband;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class TimeService extends Service implements BLEMiBand2Helper.BLEAction {
    private final String APP = "com.example.het3crab.healthband";
    private final String PULSE_FREQ_KEY = "com.example.het3crab.healthband.pulsefreq";
    int pulseFreq;
    int average;
    boolean connect=false;
    private SmsManager smsManager = SmsManager.getDefault();


    public RealmResults<RealmPulseReading> pulses2;
    public List<RealmPulseReading> pulses2ToAdd = new ArrayList<>();

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    Handler handler = new Handler(Looper.getMainLooper());
    BLEMiBand2Helper helper = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        connectToMiBand();

        Intent notificationIntent = new Intent(this, Main.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);

        pulseFreqUpdate();

        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, pulseFreq * 1000);
    }

    public void pulseFreqUpdate(){
        SharedPreferences prefs = this.getSharedPreferences(APP , Context.MODE_PRIVATE);
        pulseFreq = prefs.getInt(PULSE_FREQ_KEY, 60);
    }

    public void ifFreqChange(){
        SharedPreferences prefs = this.getSharedPreferences(APP , Context.MODE_PRIVATE);
        if (prefs.getInt(PULSE_FREQ_KEY, 60) != pulseFreq) {
            mTimer.cancel();
            mTimer = new Timer();
            pulseFreq = prefs.getInt(PULSE_FREQ_KEY, 60);
            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, pulseFreq * 1000);
        }

    }

    @Override
    public void onDisconnect() {
        connect = false;

    }

    @Override
    public void onConnect() {
        connect = true;
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
        lifeCheck();
    }

    public void sendSms()
    {
        smsManager.sendTextMessage("694759594", null, "elo", null, null);
    }

    void lifeCheck() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm realm = Realm.getInstance(realmConfiguration);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (RealmPulseReading pulse : pulses2ToAdd) {
                    realm.insertOrUpdate(pulse);
                }
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                pulses2 = realm.where(RealmPulseReading.class).findAll();
                if (pulses2.size() > 2) {
                    average = (pulses2.get(pulses2.size() - 1).getValue() + pulses2.get(pulses2.size() - 2).getValue() + pulses2.get(pulses2.size() - 3).getValue()) / 3;
                    Log.d("siema", String.valueOf(average));
                    if (average > 150 || average < 60) {
                        sendSms();
                    }

                }
            }
        });
    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    ifFreqChange();
                    if (connect)
                    {

                        helper.getNotificationsWithDescriptor(Consts.UUID_SERVICE_HEARTBEAT, Consts.UUID_NOTIFICATION_HEARTRATE, Consts.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        odczytPulsu();
                        Toast.makeText(getApplicationContext(), getDateTime(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        requestHehe();
                        connectToMiBand();
                    }
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            return sdf.format(new Date());
        }

    }

    void connectToMiBand() {
        if (helper == null) {
            helper = new BLEMiBand2Helper(TimeService.this, handler);
            helper.addListener(this);
        }


        // Setup Bluetooth:
        helper.connect();

    }

    public void odczytPulsu(){
        helper.writeData(Consts.UUID_SERVICE_HEARTBEAT, Consts.UUID_START_HEARTRATE_CONTROL_POINT, new byte[]{21, 2, 1} );
    }

    void requestHehe(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
        }
    }
}
