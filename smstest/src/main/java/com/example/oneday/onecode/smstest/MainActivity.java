package com.example.oneday.onecode.smstest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_RECEIVE_SMS = 100;

    private MySMSReciver smsReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        intentFilter.setPriority(1000);
        smsReciver = new MySMSReciver();
        registerReceiver(smsReciver, intentFilter);


    }

    private void requestSmsPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "requestSmsPermission: " + true);
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_RECEIVE_SMS);
        }

    }

    class MySMSReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: " + TAG);
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for(int i=0; i<messages.length; i++){
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            }
            //获取发送号码
            String address = messages[0].getOriginatingAddress();
            String fullMessage = "";
            for(SmsMessage message : messages){
                fullMessage += message.getMessageBody();
            }

            //显示短信
            TextView tvSender = (TextView) findViewById(R.id.sender);
            TextView tvContent = (TextView) findViewById(R.id.content);

            tvSender.setText(address);
            tvContent.setText(fullMessage);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestSmsPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReciver);
        smsReciver = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MainActivity.REQUEST_RECEIVE_SMS){
            Log.d(TAG, "onRequestPermissionsResult: permissions=" + permissions.toString() + "/ grantResults=" + grantResults.toString());
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //User granted this permission.
            }else{
                Toast.makeText(MainActivity.this, "Until you grant permission, we can't display contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
