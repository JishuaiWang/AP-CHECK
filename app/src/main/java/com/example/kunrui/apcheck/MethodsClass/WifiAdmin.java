package com.example.kunrui.apcheck.MethodsClass;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.Editable;

public class WifiAdmin {
    //定义一个WifiManager对象
    private WifiManager mWifiManager;
    //定义一个WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    private WifiLock mWifiLock;

    @SuppressLint("WifiManagerPotentialLeak")
    public WifiAdmin(Context context) {
//取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//取得WifiInfo对象
        assert mWifiManager != null;
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //打开wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    //关闭wifi
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前wifi状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    //锁定wifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    //解锁wifiLock
    public void releaseWifiLock() {
//判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    //创建一个wifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    //得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }

    //指定配置好的网络进行连接
    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
//连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    public void startScan() {
        mWifiManager.startScan();
//得到扫描结果
        mWifiList = mWifiManager.getScanResults();
//得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    //得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    //查看扫描结果
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_").append(Integer.toString(i + 1)).append(":");
// 将ScanResult信息转换成一个字符串包
// 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    @SuppressLint("HardwareIds")
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public String getRSSI() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : String.valueOf(mWifiInfo.getRssi());
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    //得到连接的ID
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    //得到wifiInfo的所有信息
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public void resconnect() {
        mWifiManager.disconnect();
        mWifiManager.reconnect();
    }

    /**
     * 获取当前连接WIFI的SSID(注:得到的SSID有双引号)
     */
    public String getSSID() {
        if (mWifiManager != null) {
            WifiInfo winfo = mWifiManager.getConnectionInfo();
            if (winfo != null) {
                String s = winfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "";
    }

    //添加一个网络并连接
    public void addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        System.out.println("wcgId: " + wcgId);
        boolean bol = mWifiManager.enableNetwork(wcgId, true);
        System.out.println("bol: " + bol);
    }

    //断开指定ID的网络
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                            String Type, boolean isHidden) {
        System.out.println("CreateWifiInfo SSID:" + SSID + " Password:" + Password + "Type: " + Type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
//        config.BSSID = "9E:4E:36:B4:FD:C8";  无效的

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if (tempConfig != null) {
            System.out.println("tempConfig.networkId: " + tempConfig.networkId);
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (Type.equals("ESS")) // WIFICIPHER_NOPASS
        {
            System.out.println("ESS");
            config.hiddenSSID = isHidden;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type.equals("WEP")) // WIFICIPHER_WEP
        {
            System.out.println("WEP");
            config.hiddenSSID = isHidden;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type.equals("WPA")) // WIFICIPHER_WPA
        {
            System.out.println("WPA");
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = isHidden;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();

        WifiConfiguration wifiConfiguration = null;

        for (WifiConfiguration existingConfig : existingConfigs) {
            if(existingConfig.SSID == null) {
                System.out.println("existingConfig.SSID 为 null ，暂时不知道扫描原因");
                continue;
            }
            System.out.println();
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                System.out.println("existingConfig! networkId:" + existingConfig.networkId);
                wifiConfiguration = existingConfig;
            }
        }
        return wifiConfiguration;
    }
}