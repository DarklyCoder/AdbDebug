package com.darklycoder.adbdebug;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * @author zhangqy
 * @Description:
 * @date 2016/10/19 11:09
 */
public final class Utils {

    /**
     * 判断是否获取Root权限
     *
     * @return
     */
    public static boolean isRoot() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            int i = process.waitFor();
            if (0 == i) {
                Runtime.getRuntime().exec("su");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int startAdbWifiDebug(Context context, boolean status) {
        if (status == false) {
            if (TextUtils.isEmpty(getIP(context))) {
                return -1;
            }
        }
        return runSuCommand("setprop service.adb.tcp.port 5555;stop adbd;start adbd") ? 1 : -2;
    }

    public static boolean stopAdbWifiDebug(boolean status) {
        return status == false || runSuCommand("setprop service.adb.tcp.port -1;stop adbd");
    }

    /**
     * 判断是否已打开
     *
     * @return
     */
    public static boolean isOpened(Context context) {
        try {
            Process process = Runtime.getRuntime().exec("getprop service.adb.tcp.port");
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            String port = null;
            while ((line = reader.readLine()) != null) {
                port = line;
            }
            process.waitFor();
            is.close();
            reader.close();
            process.destroy();

            if ("5555".equals(port) && !TextUtils.isEmpty(getIP(context))) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean runSuCommand(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            OutputStream os = p.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(cmd + "\n");
            bw.write("exit \n");
            bw.close();
            osw.close();
            os.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getIP(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (!wm.isWifiEnabled()) {
                wm.setWifiEnabled(true);
            }
            WifiInfo wi = wm.getConnectionInfo();
            int ip = wi.getIpAddress();
            if (ip == 0) {
                return null;
            }
            return intToIp(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

}
