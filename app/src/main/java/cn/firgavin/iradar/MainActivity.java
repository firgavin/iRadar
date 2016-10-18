package cn.firgavin.iradar;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
/*

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
*/
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import static android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE;
import static android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE;
import static android.telephony.SmsManager.RESULT_ERROR_NULL_PDU;
import static android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF;

public class MainActivity extends AppCompatActivity {

    private Button btnRefresh = null;
    private Button btnMark = null;

    private StaticStorage publicStorage;//静态全局

    private MapView mMapView = null;
    private Button buttonFriend = null;
    private Button buttonEnemy = null;

    private BaiduMap mBaiduMap;

    //定位模块
    private LocationManager locationManager;
    private String provider;
    private Location tempLoc;

    //发送短信模块
    private  String SMS_SEND_ACTION = "SMS_SEND";
    private  String SMS_DELIVERED_ACTION ="SMS_DELIVERED";
    private SmsStatusReceiver mSmsStatusReceiver;
    private SmsDeliveryStatusReceiver mSmsDeliveryStatusReceiver;
    private IntentFilter receiveFilter;
    private MessageReceiver messageReceiver;

  /*  public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    public void onCreate() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext,注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        /*mLocationClient.start();*/
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        initMarkMyself();

        btnMark = (Button) findViewById(R.id.btnMark);
        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //标记friend
                for(int i= 0;i<publicStorage.friendList.size();i++){
                    Contacts temp = publicStorage.friendList.get(i);
                    if(temp.getFlag()){
                        LatLng point = new LatLng(temp.getLatitude(), temp.getLonggitude());
//构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.pin_map_friend);
//构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(bitmap);
//在地图上添加Marker，并显示
                        mBaiduMap.addOverlay(option);

                    }
                }
                //标记enemy
                for(int i= 0;i<publicStorage.enemyList.size();i++){
                    Contacts temp = publicStorage.enemyList.get(i);
                    if(temp.getFlag()){
                        //LatLng point = new LatLng(39.963175, 116.400244);
                        LatLng point = new LatLng(temp.getLatitude(), temp.getLonggitude());
//构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory
                                .fromResource(R.drawable.pin_map_enemy);
//构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(bitmap);
//在地图上添加Marker，并显示
                        mBaiduMap.addOverlay(option);

                    }
                }

                LatLng cenpt = new LatLng(22.256479, 113.540707);
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(cenpt)
                        .zoom(18)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                mBaiduMap.setMapStatus(mMapStatusUpdate);
            }
        });

        //mLocationClient.start();
        //点击刷新重新发送短信对列表联系人进行发短信定位
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PendingIntent sentIntent = PendingIntent.getBroadcast(MainActivity.this, 1, new Intent(SMS_SEND_ACTION), 0);
                //这个意图包装了对短信接受状态回调的处理逻辑
                PendingIntent deliveryIntent = PendingIntent.getBroadcast(MainActivity.this, 2, new Intent(SMS_DELIVERED_ACTION), 0);
                SmsManager manager = SmsManager.getDefault();
                for(int i= 0;i<publicStorage.friendList.size();i++){
                    Contacts temp = publicStorage.friendList.get(i);
                    if(temp.getFlag()){
                        manager.sendTextMessage(temp.getPhoneNum(), null, "Where", sentIntent, deliveryIntent);
                    }
                }
                for(int i= 0;i<publicStorage.enemyList.size();i++){
                    Contacts temp = publicStorage.enemyList.get(i);
                    if(temp.getFlag()){
                        manager.sendTextMessage(temp.getPhoneNum(), null, "Where", sentIntent, deliveryIntent);
                    }
                }
                //manager.sendTextMessage(toWho.getText().toString(), null, sMessage.getText().toString(), sentIntent, deliveryIntent);

                //for(int wait=0;wait<100000;wait++){}
            }
        });
        //短信监听广播注册
        receiveFilter = new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver,receiveFilter);

        buttonEnemy = (Button) findViewById(R.id.btnEnemy);
        buttonEnemy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEnemy = new Intent();
                intentEnemy.setClass(MainActivity.this,EnemyList.class);
                startActivity(intentEnemy);
            }
        });

        buttonFriend = (Button) findViewById(R.id.btnFriend);
        buttonFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFriend = new Intent();
                intentFriend.setClass(MainActivity.this,FriendList.class );
                startActivity(intentFriend);
            }
        });
    }

   /* private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }*/

    //public int getLocType ();


    private void initMarkMyself(){
        LatLng point = new LatLng(22.256479, 113.540707);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.this_is_myself);
//构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
//在地图上添加Marker，并显示
        //mMapView.addOverlay(option);
        mBaiduMap.addOverlay(option);
        LatLng cenpt = new LatLng(22.256479, 113.540707);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cenpt)
                .zoom(18)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化


        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
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
            String phoneNum = "";
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String address = smsMessage.getDisplayOriginatingAddress();
                String fullMessage = smsMessage.getMessageBody();


                phoneNum = address;
                //这里获取短讯的发件人和内容
                //sender.setText(address);
                //content.setText(fullMessage);
                sms += fullMessage;
               /* if ("10086".equals(address)) {//测试截断短信
                    abortBroadcast();
                }*/
            }
            if (sms.equals("Where")){
                //启动定位，定位当前位置
                List<String> providerList = locationManager.getProviders(true);
                if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                    provider = LocationManager.GPS_PROVIDER;
                } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                    provider = LocationManager.NETWORK_PROVIDER;
                } else {
                    Toast.makeText(MainActivity.this, "No location provider to use", Toast.LENGTH_SHORT).show();
                    //return;
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    showLocation(location);
                    //System.out.println("get!!!!!");
                }
                locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);

                PendingIntent sentIntent = PendingIntent.getBroadcast(MainActivity.this, 1, new Intent(SMS_SEND_ACTION), 0);
                //这个意图包装了对短信接受状态回调的处理逻辑
                PendingIntent deliveryIntent = PendingIntent.getBroadcast(MainActivity.this, 2, new Intent(SMS_DELIVERED_ACTION), 0);
                SmsManager manager = SmsManager.getDefault();
                String curPosition =location.getLatitude() + "-" + location.getLongitude();
                manager.sendTextMessage(phoneNum, null, curPosition, sentIntent, deliveryIntent);
            }else{
                //读入经纬度，更新储存
                //分离经纬度
                String tempLatitude = "";
                String tempLongitude = "";
                boolean mark = false;
                for(int i = 0;i<sms.length();i++){
                    if(sms.charAt(i) == '-'){
                        mark = true;
                    }else {
                        if (!mark) {
                            tempLatitude += sms.charAt(i);
                        } else {
                            tempLongitude += sms.charAt(i);
                        }
                    }
                }
                //分别刷新
                for(int i= 0;i<publicStorage.friendList.size();i++){
                    Contacts temp = publicStorage.friendList.get(i);
                    if(temp.getFlag()){
                        //manager.sendTextMessage(temp.getPhoneNum(), null, "Where", sentIntent, deliveryIntent);
                        if(temp.getPhoneNum().equals(phoneNum)){
                            temp.setLatitude(Double.valueOf(tempLatitude.toString()));
                            temp.setLonggitude(Double.valueOf(tempLongitude.toString()));
                        }
                    }
                }
                for(int i= 0;i<publicStorage.enemyList.size();i++){
                    Contacts temp = publicStorage.enemyList.get(i);
                    if(temp.getFlag()){
                        //manager.sendTextMessage(temp.getPhoneNum(), null, "Where", sentIntent, deliveryIntent);
                        if(temp.getPhoneNum().equals(phoneNum)){
                            temp.setLatitude(Double.valueOf(tempLatitude.toString()));
                            temp.setLonggitude(Double.valueOf(tempLongitude.toString()));
                        }
                    }
                }

                //联系人列表刷新完毕
            }

            //content.setText(sms);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    private  void  showLocation(Location location){
        String currentPosition ="latitude is "+location.getLatitude() + "\n" + "longgitude is " + location.getLongitude();
        //positionTextView.setText(currentPosition);
        tempLoc = location;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();

        //短信发送模块注销短信接收模块
        unregisterReceiver(messageReceiver);

        //注销位置信息监听(此处有bug)
        if(locationListener!=null) {
            //locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

        //短信发送模块注册广播
        mSmsStatusReceiver = new SmsStatusReceiver();
        registerReceiver(mSmsStatusReceiver,new IntentFilter(SMS_SEND_ACTION));
        mSmsDeliveryStatusReceiver = new SmsDeliveryStatusReceiver();
        registerReceiver(mSmsDeliveryStatusReceiver,new IntentFilter(SMS_DELIVERED_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();

        //短信发送模块注销监听广播
        unregisterReceiver(mSmsStatusReceiver);
        unregisterReceiver(mSmsDeliveryStatusReceiver);
    }
}
