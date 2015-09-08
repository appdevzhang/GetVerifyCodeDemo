package com.example.appdevzhang.getverifycodedemo;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.appdevzhang.getverifycodedemo.R.id;
import static com.example.appdevzhang.getverifycodedemo.R.layout;

public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private EditText verifyCode_et;
    private SmsObserver smsObserver;
    private Button sign_bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        verifyCode_et = (EditText)findViewById(id.verifycode_et);
        sign_bt = (Button) findViewById(id.sign_button);
        smsObserver = new SmsObserver(this,smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX,true,smsObserver);
        getSmsFromPhone();
        sign_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getContentResolver().unregisterContentObserver(smsObserver);
                Log.d(TAG+"unregist","unregisted");
            }
        });
    }
    private Uri SMS_INBOX = Uri.parse("content://sms/inbox");
    public Handler smsHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            System.out.println("smsHandler 执行了.....");
        };
    };
    public void getSmsFromPhone(){
        String [] projection = new String[]{"body","address"};
        String where = "date > "+(System.currentTimeMillis()-10*60*1000);
        Cursor cur = this.getContentResolver().query(SMS_INBOX,projection,where,null,"date desc");
        if(null == cur){
            return;
        }
        while(cur.moveToNext()){
            Log.d(TAG+":GETSMS","START");
            String number = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndex("body"));

            if(number.contains("15011663043")){
                Pattern pattern = Pattern.compile("[a-zA-Z0-9]{2}");
                Matcher matcher = pattern.matcher(body);

                if(matcher.find()){
                    String res = matcher.group().substring(0,2);
                    verifyCode_et.setText(res);
                }
//                verifyCode_et.setText(body);
                Log.v(TAG + ":number", number);
                Log.v(TAG+"body",body);
            }

        }
        cur.close();


    }
    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // 每当有新短信到来时，使用我们获取短消息的方法
            getSmsFromPhone();
        }
    }

}
