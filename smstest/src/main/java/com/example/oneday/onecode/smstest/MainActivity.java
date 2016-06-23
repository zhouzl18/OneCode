package com.example.oneday.onecode.smstest;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_RECEIVE_SMS = 100;
    public static final int REQUEST_SEND_SMS = 101;

    public static final String SEND_SMS_ACTION = "SEND_SMS_ACTION";

    private MySMSReciver smsReciver;

    private SendStatusReceiver sendStatusReceiver;

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

        IntentFilter statusFilter = new IntentFilter();
        statusFilter.addAction(SEND_SMS_ACTION);
        sendStatusReceiver = new SendStatusReceiver();
        registerReceiver(sendStatusReceiver, statusFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestSmsPermission(Manifest.permission.RECEIVE_SMS, REQUEST_RECEIVE_SMS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReciver);
        unregisterReceiver(sendStatusReceiver);
        smsReciver = null;
    }

    /**
     * Result of request permissions.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: permissions=" + permissions.toString());
        for(Integer grant : grantResults){
            Log.i(TAG, "onRequestPermissionsResult: grant=" + grant);
        }
        if(requestCode == REQUEST_RECEIVE_SMS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //User granted this permission.
            }else{
                Toast.makeText(MainActivity.this, "Until you grant permission, we can't display contacts.", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_SEND_SMS){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setTextMessage();
            }else{
                Toast.makeText(MainActivity.this, "Until you grant permission, we can't send message.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * request permissions.
     */
    private boolean requestSmsPermission(String permissions, int requestCode) {
        boolean isGrant = true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(permissions) != PackageManager.PERMISSION_GRANTED){
            isGrant = false;
            requestPermissions(new String[]{permissions}, requestCode);
        }
        return isGrant;
    }

    /**
     * Receiver of SMS broadcast.
     */
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

    class SendStatusReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(SEND_SMS_ACTION)){
                //Send message is succesful.
                Toast.makeText(MainActivity.this, "Send succeeded", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onReceive: SEND_SMS_ACTION=true");
            }else{
                Toast.makeText(MainActivity.this, "Send failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onReceive: SEND_SMS_ACTION=false");
            }
        }
    }

    /**
     * sendMessage
     * @param view of button onClick:sendMessage
     */
    public void sendMessage(View view){
        if(requestSmsPermission(Manifest.permission.SEND_SMS, REQUEST_SEND_SMS)){
            setTextMessage();
        }
    }

    /**
     * Set object and content which is send.
     */
    private void setTextMessage(){
        EditText etTo = (EditText) findViewById(R.id.et_to);
        EditText etContent = (EditText) findViewById(R.id.et_content);
        String toText = etTo.getText().toString();
        String contentText = etContent.getText().toString();
        if(!TextUtils.isEmpty(toText) && !TextUtils.isEmpty(contentText)){
            Intent i = new Intent(SEND_SMS_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toText, null, contentText, pendingIntent, null);
        }
    }

}
