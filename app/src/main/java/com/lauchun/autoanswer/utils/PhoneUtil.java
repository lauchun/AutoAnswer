package com.lauchun.autoanswer.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telecom.TelecomManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author ：lauchun
 * @date ：Created in 2/8/22
 * @description ：
 * @version: 1.0
 */
public class PhoneUtil {

    static String phoneNum = null;
    static int callTimes = 0;

    public static void acceptCall(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        TelecomManager telecomManager =
                (TelecomManager) context.getSystemService(context.TELECOM_SERVICE);
        try {
            Method method = Class.forName("android.telecom.TelecomManager").getMethod(
                    "acceptRingingCall");
            method.invoke(telecomManager);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void endCall(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        TelecomManager telecomManager =
                (TelecomManager) context.getSystemService(context.TELECOM_SERVICE);
        try {
            Method method = Class.forName("android.telecom.TelecomManager").getMethod(
                    "endCall");
            method.invoke(telecomManager);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void callPhone(Context context, String lineNumber) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (lineNumber.isEmpty()) {
            AlertDialog(context);
        } else {
            phoneNum = lineNumber;
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + lineNumber);
            intent.setData(data);
            context.startActivity(intent);
        }
    }

    private static void AlertDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("注意")
                .setMessage("手机号码不能为空！")
                .setPositiveButton("知道了", null)
                .show();
    }

    public static String getPhoneNum() {
        return phoneNum;
    }

    public static int getCallTimes() {
        return callTimes;
    }

    public static int setCallTimes(int i) {
        callTimes = i;
        return callTimes;
    }
}