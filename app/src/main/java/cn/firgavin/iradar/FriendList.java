package cn.firgavin.iradar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.A;

import java.util.ArrayList;
import java.util.List;

import static android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE;
import static android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE;
import static android.telephony.SmsManager.RESULT_ERROR_NULL_PDU;
import static android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF;

public class FriendList extends AppCompatActivity {

    private StaticStorage publicStorage;
    private static boolean initFlagOfFriend = false; //标识第一次初始化

    private  String SMS_SEND_ACTION = "SMS_SEND";
    private  String SMS_DELIVERED_ACTION ="SMS_DELIVERED";

 /*   private SmsStatusReceiver mSmsStatusReceiver;
    private SmsDeliveryStatusReceiver mSmsDeliveryStatusReceiver;

    private IntentFilter receiveFilter;
    private MessageReceiver messageReceiver;*/

    private Button btnReturn = null;
    private Button btnGetLocation = null;
    private Button btnEdit = null;
    private Button btnAdd = null;

    private List<Contacts> friendList = new ArrayList<Contacts>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        //initFriend();
        if(!initFlagOfFriend) {
            initFriend();
            publicStorage.friendList = friendList;
            initFlagOfFriend = true;
        }
        FriendAdapter adapter = new FriendAdapter(FriendList.this,R.layout.friend_item,publicStorage.friendList);
        ListView listview = (ListView) findViewById(R.id.listViewOfFriend);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //此处编辑联系人信息
                //final Contacts ob = friendList.get(position);

                LayoutInflater factory = LayoutInflater.from(FriendList.this);
                final View textEntryView = factory.inflate(R.layout.logindialog,null);
                AlertDialog dlg = new AlertDialog.Builder(FriendList.this)
                        .setTitle("编辑联系人")
                        .setView(textEntryView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String inputUserName = "";
                                String inputPhoneNum = "";

                                EditText userName = (EditText) textEntryView.findViewById(R.id.edit_username);
                                if(userName!= null) {
                                    inputUserName = userName.getText().toString();
                                }
                                EditText phoneNum = (EditText) textEntryView.findViewById(R.id.edit_password);
                                if (phoneNum!= null) {
                                    inputPhoneNum = phoneNum.getText().toString();
                                }

                                publicStorage.friendList.get(position).setFlag(true);
                                publicStorage.friendList.get(position).setName(inputUserName);
                                publicStorage.friendList.get(position).setPhoneNum(inputPhoneNum);
                                //friendList.get(position) = ob;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dlg.show();
            }
        });

   /*     btnGetLocation = (Button) findViewById(R.id.locate_friend);
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处发送短信获取联系人位置
                //这个意图包装了对短信发送状态回调的处理逻辑
               *//* PendingIntent sentIntent = PendingIntent.getBroadcast(FriendList.this, 1, new Intent(SMS_SEND_ACTION), 0);
                //这个意图包装了对短信接受状态回调的处理逻辑
                PendingIntent deliveryIntent = PendingIntent.getBroadcast(FriendList.this, 2, new Intent(SMS_DELIVERED_ACTION), 0);
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(toWho.getText().toString(), null, sMessage.getText().toString(), sentIntent, deliveryIntent);*//*
            }
        });*/

        btnReturn = (Button) findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFriend(){
        Contacts Allen = new Contacts("Allen","10086",22.257008,113.539689,true);
        friendList.add(Allen);
        Contacts Amy = new Contacts("Amy","10086", 22.255302,113.542833,true);
        friendList.add(Amy);
        for(int i=0;i<20;++i){
            Contacts test = new Contacts("FRIEND NAME","666666",6,6,false);
            friendList.add(test);
        }
    }

  /*  @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mSmsStatusReceiver);
        unregisterReceiver(mSmsDeliveryStatusReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
        *//*unregisterReceiver(receiver);*//*
    }

    //短信发送后的发送状态广播接收器
    public class SmsStatusReceiver extends BroadcastReceiver {

        private static final String TAG = "SmsStatusReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"SmsStatusReceiver onReceive.");
            switch(getResultCode()) {
                case Activity.RESULT_OK:
                    Log.d(TAG, "Activity.RESULT_OK");
                    break;
                case RESULT_ERROR_GENERIC_FAILURE:
                    Log.d(TAG, "RESULT_ERROR_GENERIC_FAILURE");
                    break;
                case RESULT_ERROR_NO_SERVICE:
                    Log.d(TAG, "RESULT_ERROR_NO_SERVICE");
                    break;
                case RESULT_ERROR_NULL_PDU:
                    Log.d(TAG, "RESULT_ERROR_NULL_PDU");
                    break;
                case RESULT_ERROR_RADIO_OFF:
                    Log.d(TAG, "RESULT_ERROR_RADIO_OFF");
                    break;
            }
        }
    }

    //短信发送到对方后，对对方返回的接受状态的处理逻辑
    public class SmsDeliveryStatusReceiver extends BroadcastReceiver {

        private static final String TAG = "SmsDeliveryStatusReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"SmsDeliveryStatusReceiver onReceive.");
            switch(getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context,"Send Succeeded",Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "RESULT_OK");
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context,"Send Failed",Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "RESULT_CANCELED");
                    break;
            }
        }
    }

    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String sms = "";
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String address = smsMessage.getDisplayOriginatingAddress();
                String fullMessage = smsMessage.getMessageBody();


                //sender.setText(address);
                //content.setText(fullMessage);
                sms += fullMessage;
               *//* if ("10086".equals(address)) {//测试截断短信
                    abortBroadcast();
                }*//*
            }
            //content.setText(sms);
        }
    }*/
}
