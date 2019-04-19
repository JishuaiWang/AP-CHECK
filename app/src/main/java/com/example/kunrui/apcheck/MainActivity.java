package com.example.kunrui.apcheck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.kunrui.apcheck.MethodsClass.FileOpr;
import com.example.kunrui.apcheck.MethodsClass.SqlOpr;
import com.example.kunrui.apcheck.MethodsClass.WifiAdmin;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Bundle bundle;
    private WifiAdmin mwifiAdmin;
    private Handler mHandler;
    private FileOpr fileOpr = new FileOpr();
    private SqlOpr sqlOpr = new SqlOpr(this, "wifi.db", null, 1);
    private LocationManager locManager;
    private ArrayList<String> list_goal = new ArrayList<>();
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private String SSID, PASS, ENCRYPT;
    private Fragment mCurrentFragment;

    private int isCur = 0, addNet = 1;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private boolean checkPermissionGranted(String permission) {
        return PermissionChecker.checkPermission(this, permission, android.os.Process.myPid(), android.os.Process.myUid(), getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //去掉标题
        locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        fileOpr.write_status("TEP_SSID", "default");//用于配置页面选择临时SSID连接用

        fileOpr.init_config_file();
        initFragmentList();
        changeTab(0);

        mwifiAdmin = new WifiAdmin(this);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if(mwifiAdmin.checkState() != 3){
                            mwifiAdmin.openWifi();//打开wifi
                            System.out.println("mwifiAdmin.checkState() != 3");
                            break;
                        }
                        if(mwifiAdmin.checkState() == 3 && addNet == 1) {
                            System.out.println("addNetWork");
                            addNet = 0;
                            //这个得先添加一次， 如果是隐藏的wifi则扫描是搜寻不到的
                            mwifiAdmin.addNetWork(mwifiAdmin.CreateWifiInfo(SSID, PASS, ENCRYPT, true));
                        }
                        list_goal = getNeedConnectSSID();
                        if(list_goal.size() > 0) {
                            isCur = 1;
                        }
                        break;
                }
            }
        };

        if(checkPermission() == -1) {
            isCur = 9;
        } else {
            checkWifi.start();
        }

        Button homeWeb = findViewById(R.id.homeWeb);
        Button language = findViewById(R.id.language);
        Button wifi_config = findViewById(R.id.wifi_config);
        Button reset = findViewById(R.id.reset);
        homeWeb.setOnClickListener(new myListen());
        language.setOnClickListener(new myListen());
        wifi_config.setOnClickListener(new myListen());
        reset.setOnClickListener(new myListen());
    }


    //检查wifi是否连接， 保存MAC和RSSI
    public Thread checkWifi = new Thread() {
        public void run() {
            int i = 0;
            isCur = 0;addNet = 1;
            configInit();
            SSID = fileOpr.read_status("SSID");
            PASS = fileOpr.read_status("PASSWORD");
            ENCRYPT = fileOpr.read_status("ENCRYPT");
            System.out.println("read config: " + SSID + " " + PASS + " " + ENCRYPT);
            //这里用或不用与的原因是需要针对同名wifi进行区分
            while (isCur == 0 || !mwifiAdmin.getSSID().equals(SSID)) {
                System.out.println("getSSID:" + mwifiAdmin.getSSID());
                mHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                if(i == 5) {
                    System.out.println("加载超时");
                    isCur = 9;  //9表示可以开启下一个线程
                    break;
                }
            }

            //预防由于手机性能造成的连接缓慢
            do {
                System.out.println("load...");
                i++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (isCur != 9 && i != 10 && mwifiAdmin.getBSSID().equals("00:00:00:00:00:00"));

            //比较信号强度再次验证连接准确性
            if(isCur != 9) {
                if (!mwifiAdmin.getBSSID().equals(getNeedConnectSSID().get(0).split(", ", 5)[1])) {
                    System.out.println("重新连接, mwifiAdmin.getBSSID():" + mwifiAdmin.getBSSID());
                    mwifiAdmin.resconnect();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            isCur = 9;
            System.out.println("BSSID&RSSI: " + mwifiAdmin.getBSSID() + "  " + mwifiAdmin.getRSSI());
            fileOpr.write_status("BSSID", mwifiAdmin.getBSSID());
            fileOpr.write_status("RSSI", mwifiAdmin.getRSSI());
            bundle.putString("Address", fileOpr.read_status("Address"));
            changeTab(1);
        }
    };

    private class myListen implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.homeWeb:
                    fileOpr.write_status("AppPath", "homeWeb");
                    if(isCur == 9) {
                        changeTab(0);
                        new Thread(checkWifi).start();
                    }
                    break;
                case R.id.wifi_config:
                    fileOpr.write_status("AppPath", "wifi_config");
                    bundle.putString("layout", "wifi_config");
                    changeTab(2);
                    break;
                case R.id.language:
                    break;
                case R.id.reset:
                    SQLiteDatabase db;
                    db = sqlOpr.getWritableDatabase();
                    sqlOpr.resetSQL(db);
                    db.close();

                    fileOpr.write_status("LANGUAGE", "en");

                    //重启APP
                    Intent intent = getPackageManager().getLaunchIntentForPackage( getPackageName() );
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    onBackPressed();
                    startActivity(intent);
                    break;
            }
        }
    }

    public int checkPermission() {
        int result = 0;
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.gps), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 9);
            result = -1;
        }
        //判断是否获得定位权限
        boolean isLocation1 = checkPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean isLocation2 = checkPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(isLocation1 && isLocation2) {
            //获取成功的操作
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 7);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 8);
            Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.pos_cue), Toast.LENGTH_SHORT).show();
            result = -1;
        }
        return result;
    }

    public Bundle getCurSSID() {
        Bundle bundle = new Bundle();
        SQLiteDatabase db;
        db = sqlOpr.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from wifiMsg where isFirst = 1;", null);
        if (cursor.moveToFirst()){
            bundle.putString("SSID", cursor.getString(cursor.getColumnIndex("SSID")));
            bundle.putString("PASSWORD", cursor.getString(cursor.getColumnIndex("PASSWORD")));
            bundle.putString("Encrypt", cursor.getString(cursor.getColumnIndex("Encrypt")));
            bundle.putString("Address", cursor.getString(cursor.getColumnIndex("Address")));
        }
        System.out.println("getCurSSID(), bundle: " + bundle);
        cursor.close();
        db.close();
        return bundle;
    }

    public void configInit() {
        System.out.println("configInit");
        bundle = getCurSSID();
        if(fileOpr.read_status("TEP_SSID").equals("temporary")) return;
        System.out.println("初始化");
        fileOpr.write_status("SSID", bundle.getString("SSID"));
        fileOpr.write_status("PASSWORD", bundle.getString("PASSWORD"));
        fileOpr.write_status("ENCRYPT", bundle.getString("Encrypt"));
        fileOpr.write_status("Address", bundle.getString("Address"));
    }

    public ArrayList<String> getNeedConnectSSID() {
        ArrayList<String> list_all =getAllNetWorkList();
        ArrayList<String> list_goal = new ArrayList<>();
        List<Integer> id_list = new ArrayList<>();
        int dbm_max = 0, max_rssi = -200, new_rssi;
        if(list_all != null) {
            System.out.println("list != null");
            for (int i = 0; i < list_all.size(); i++) {
                if(list_all.get(i).split(", ", 5)[0].equals(SSID)) {
                    id_list.add(i);
                    new_rssi = Integer.parseInt(list_all.get(i).split(", ", 5)[2]);
                    if(new_rssi > max_rssi) {
                        dbm_max = i;
                        max_rssi = new_rssi;
                    }
                }
            }
        }
        //排序， 将信号强度最高的放在首位
        if(id_list.size() > 0) {
            list_goal.add(list_all.get(dbm_max));
            for (int i = 0; i < id_list.size(); i++) {
                if(id_list.get(i) == dbm_max) continue;
                list_goal.add(list_all.get(id_list.get(i)));
            }
        }
        System.out.println("list_goal: " + list_goal);

        return list_goal;
    }

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public ArrayList<String> getAllNetWorkList() {
        String strbuf;
        ArrayList<String> SSID_LIST = new ArrayList<>();

        //开始扫描网络
        mwifiAdmin.startScan();
        List<ScanResult> list = mwifiAdmin.getWifiList();
        System.out.println(list);
        if(list != null) {
            for(int i = 0; i< list.size(); i++) {
                ScanResult mScanResult = list.get(i);
                strbuf = mScanResult.SSID + ", " + mScanResult.BSSID + ", " + mScanResult.level + ", " + mScanResult.frequency + ", " + mScanResult.capabilities;
//                System.out.println("扫描到的wifi网络: " + strbuf);
                SSID_LIST.add(String.valueOf(strbuf));
            }
        }
        return SSID_LIST;
    }

    public void initFragmentList() {
        FragmentWait frag_wait = new FragmentWait();
        FragmentWeb fragment_web = new FragmentWeb();
        FragmentRight fragment_config = new FragmentRight();

        fragmentArrayList.add(frag_wait);
        fragmentArrayList.add(fragment_web);
        fragmentArrayList.add(fragment_config);
    }

    public void changeTab(int index) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (null != mCurrentFragment) {
            ft.hide(mCurrentFragment);
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentArrayList.get(index).getClass().getName());
        if (null == fragment) {
            fragment = fragmentArrayList.get(index);
        }
        fragment.setArguments(bundle);
        mCurrentFragment = fragment;

        if (!fragment.isAdded()) {
            ft.add(R.id.right_fragment, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //按下返回键并且webview界面可以返回
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && openWrt.canGoBack()) {
//            openWrt.goBack(); // goBack()表示返回WebView的上一页面
//            return true;
//        }
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                if (openWrt.canGoBack()) {
//                    openWrt.goBack(); // goBack()表示返回WebView的上一页面
//                }
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                if (openWrt.canGoForward()) {
//                    openWrt.goForward(); // goBack()表示返回WebView的上一页面
//                }
//                return true;
//            default:
//                break;
//        }
//        return super.onKeyDown(keyCode,event);
//    }
}
