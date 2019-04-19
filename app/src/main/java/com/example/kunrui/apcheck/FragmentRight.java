package com.example.kunrui.apcheck;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.kunrui.apcheck.MethodsClass.FileOpr;
import com.example.kunrui.apcheck.MethodsClass.LanguageChoose;
import com.example.kunrui.apcheck.MethodsClass.SqlOpr;
import com.example.kunrui.apcheck.MethodsClass.WifiAdmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FragmentRight extends Fragment {
    private Spinner spinner_list;
    private Spinner spinner_scan;
    private EditText editText;
    private SqlOpr sqlOpr;
    private LanguageChoose languageChoose = new LanguageChoose();
    private FileOpr fileOpr = new FileOpr();
    private WifiAdmin mwifiAdmin;
    private Map<String, String> SSID_ENCRYPT;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        languageChoose.selectLanguage(fileOpr.read_status("LANGUAGE"), getResources());
        View view = inflater.inflate(R.layout.blank_page, container, false);

        if(getArguments() != null)
            switch (Objects.requireNonNull(getArguments().getString("layout"))) {
                case "wifi_config":
                    view = inflater.inflate(R.layout.side_config, container, false);
                    break;
                case "language_set":
                    view = inflater.inflate(R.layout.language_set, container, false);
                    break;
                default:
                    break;
            }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("FragmentRight Created");
        sqlOpr = new SqlOpr(getActivity(), "wifi.db", null, 1);
        if(getArguments() != null) {
            switch (Objects.requireNonNull(getArguments().getString("layout"))) {
                case "wifi_config":
                    spinner_list = Objects.requireNonNull(getActivity()).findViewById(R.id.list_SSID);
                    spinner_scan = Objects.requireNonNull(getActivity()).findViewById(R.id.scan_list);
                    ArrayAdapter<String> arrayAdapterList = new ArrayAdapter<>(getActivity(), R.layout.spinner_item,  getSSIDList());
                    spinner_list.setAdapter(arrayAdapterList);
                    Button save = getActivity().findViewById(R.id.save);
                    Button scan = getActivity().findViewById(R.id.scan);
                    Button set = getActivity().findViewById(R.id.set);
                    set.setOnClickListener(new myListen());
                    save.setOnClickListener(new myListen());
                    scan.setOnClickListener(new myListen());

                    mwifiAdmin = new WifiAdmin(getContext());

                    if(mwifiAdmin.checkState() != 3){
                        mwifiAdmin.openWifi();//打开wifi
                        System.out.println("mwifiAdmin.checkState() != 3");
                    }
                    ArrayAdapter<String> arrayAdapterTemp = new ArrayAdapter<>(getActivity(), R.layout.spinner_item,  getAllNetWorkList());
                    spinner_scan.setAdapter(arrayAdapterTemp);
                    break;
//                case "language_set":
//                    Button china = getActivity().findViewById(R.id.zh);
//                    Button french = getActivity().findViewById(R.id.fr);
//                    china.setOnClickListener(new myListen());
//                    french.setOnClickListener(new myListen());
//                    break;
            }
        }
    }

    private class myListen implements View.OnClickListener {

        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save:
                    String SSID = (String) spinner_list.getSelectedItem();
                    System.out.println("默认wifi修改为: " + SSID);
                    SQLiteDatabase db;
                    db = sqlOpr.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("isFirst", 0);
                    db.update("wifiMsg", contentValues, null, null);
                    contentValues = new ContentValues();
                    contentValues.put("isFirst", 1);
                    db.update("wifiMsg", contentValues, "SSID=?", new String[]{SSID});
                    db.close();

                    fileOpr.write_status("TEP_SSID", "default");
                    Toast.makeText(getActivity(),getContext().getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.scan:
                    if(mwifiAdmin.checkState() != 3){
                        mwifiAdmin.openWifi();//打开wifi
                        System.out.println("mwifiAdmin.checkState() != 3");
                    }
                    Toast.makeText(getActivity(),getContext().getString(R.string.scan_ing), Toast.LENGTH_SHORT).show();
                    ArrayAdapter<String> arrayAdapterTemp = new ArrayAdapter<>(getActivity(), R.layout.spinner_item,  getAllNetWorkList());
//                    Toast.makeText(getActivity(),getContext().getString(R.string.scan_end), Toast.LENGTH_SHORT).show();
                    spinner_scan.setAdapter(arrayAdapterTemp);
                    break;
                case R.id.set:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    LayoutInflater factory = LayoutInflater.from(getContext());
                    View textEntryView = factory.inflate(R.layout.temp_set_ssid, null);
                    textEntryView.setBackgroundColor(Color.argb(20, 255, 255, 240));
                    final EditText pass = textEntryView.findViewById(R.id.password);
                    final EditText address = textEntryView.findViewById(R.id.address);
                    if(SSID_ENCRYPT.get(spinner_scan.getSelectedItem()).substring(1, 4).equals("ESS")){
                        pass.setText(getContext().getString(R.string.NoPass));
                        pass.setEnabled(false);
                    } else {
                        pass.setText("65HP7FW4");
                    }
                    address.setText("http://192.168.1.1");

                    builder.setTitle(getContext().getString(R.string.wifi_temporar_set));
                    builder.setView(textEntryView);
                    builder.setPositiveButton(getContext().getString(R.string.set), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(spinner_scan.getSelectedItem() == null) {
                                System.out.println("未选中");
                                return;
                            }

                            System.out.println((String) spinner_scan.getSelectedItem());
                            System.out.println(String.valueOf(pass.getText()));
                            System.out.println(SSID_ENCRYPT.get(spinner_scan.getSelectedItem()).substring(1, 4));
                            System.out.println(String.valueOf(address.getText()));

                            fileOpr.write_status("TEP_SSID", "temporary");
                            fileOpr.write_status("SSID", (String) spinner_scan.getSelectedItem());
                            fileOpr.write_status("PASSWORD", String.valueOf(pass.getText()));
                            fileOpr.write_status("ENCRYPT", SSID_ENCRYPT.get(spinner_scan.getSelectedItem()).substring(1, 4));
                            fileOpr.write_status("Address", String.valueOf(address.getText()));
                            Toast.makeText(getActivity(),getContext().getString(R.string.set_success), Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton(getContext().getString(R.string.cancel), null);
                    builder.show();
                    break;
            }
        }
    }

    @SuppressLint({"WrongConstant", "SetTextI18n"})
    public ArrayList<String> getAllNetWorkList() {
        ArrayList<String> SSID_LIST = new ArrayList<>();
        SSID_ENCRYPT = new HashMap<>();

        //开始扫描网络
        mwifiAdmin.startScan();
        List<ScanResult> list = mwifiAdmin.getWifiList();
        System.out.println(list);
        if(list != null) {
            for(int i = 0; i< list.size(); i++) {
                ScanResult mScanResult = list.get(i);
                if(mScanResult.SSID.equals("")) continue;
                SSID_LIST.add(mScanResult.SSID);
                SSID_ENCRYPT.put(mScanResult.SSID, mScanResult.capabilities);
            }
        }
        return SSID_LIST;
    }

    public ArrayList<String> getSSIDList() {
        System.out.println("getSSIDList(*)");
        int id = 0;
        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase db;
        db = sqlOpr.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from wifiMsg where isFirst = 1;", null);
        if (cursor.moveToFirst()){
            System.out.println(cursor.getString(cursor.getColumnIndex("SSID")));
            arrayList.add(cursor.getString(cursor.getColumnIndex("SSID")));
        }
        cursor.close();

        cursor = db.rawQuery("select * from wifiMsg;", null);
        if (cursor.moveToFirst()){
            LinearLayout linearLayout = getActivity().findViewById(R.id.SSID_List);
            linearLayout.removeAllViews();
            TableRow tableRow;
            do{
                id++;
                System.out.println("ALL LIST: " + cursor.getString(cursor.getColumnIndex("SSID")));
                arrayList.add(cursor.getString(cursor.getColumnIndex("SSID"))); //添加SSID集合

                //表格内容动态添加
                tableRow = new TableRow(getContext());
                tableRow.setBackgroundColor(Color.parseColor("#6495ED"));
                //这里一定要用android.widget.TableRow.LayoutParams，不然TableRow不显示
                tableRow.setLayoutParams(new LinearLayout.LayoutParams(android.widget.TableRow.LayoutParams.MATCH_PARENT, android.widget.TableRow.LayoutParams.MATCH_PARENT));
                setEditAdd(0, String.valueOf(id), id);//ID
                tableRow.addView(editText);
                setEditAdd(0, cursor.getString(cursor.getColumnIndex("SSID")), id);//ID
                tableRow.addView(editText);
                setEditAdd(0, cursor.getString(cursor.getColumnIndex("PASSWORD")), id);//ID
                tableRow.addView(editText);
                setEditAdd(0, cursor.getString(cursor.getColumnIndex("Address")), id);//ID
                tableRow.addView(editText);
                linearLayout.addView(tableRow);
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        //去重、排序
        Set<String> hashSet = new LinkedHashSet<String>(arrayList);
        List<String> sortHashList = new ArrayList<String>(hashSet);
        arrayList = (ArrayList<String>) sortHashList;
        return arrayList;
    }

    //dp单位转换成px单位
    public static int dp2px(Context context, float dpValue){
        return (int)(dpValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
    }


    //动态添加函数
    @TargetApi(Build.VERSION_CODES.N)
    public void setEditAdd(int width_t, String value, final int id)
    {
        int height=dp2px(Objects.requireNonNull(getContext()), 40);
        int margin=dp2px(Objects.requireNonNull(getContext()), 0.5f);
        int padding=dp2px(Objects.requireNonNull(getContext()), 2);
        int width = dp2px(Objects.requireNonNull(getContext()), width_t);

        editText=new EditText(getContext());
        editText.setSingleLine();
        editText.setBackgroundColor(Color.parseColor("#0d122e"));
        android.widget.TableRow.LayoutParams params=new android.widget.TableRow.LayoutParams(width, height);
        params.weight=1;
        editText.setGravity(Gravity.CENTER);
        params.setMargins(margin,margin,margin,margin);
        editText.setLayoutParams(params);
        editText.setPadding(padding,padding,padding,padding);
        editText.setTextSize(14);

        //设置不可编辑但有点击
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setTextColor(Color.parseColor("#FFFFFFFF"));
        editText.setText(value);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}