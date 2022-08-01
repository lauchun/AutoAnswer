package com.lauchun.autoanswer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lauchun.autoanswer.service.CallService;
import com.lauchun.autoanswer.utils.PermissionUtils;
import com.lauchun.autoanswer.utils.PhoneUtils;
import com.lauchun.autoanswer.utils.ServiceUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;

    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ANSWER_PHONE_CALLS
    };

    private TextView tv_phone;
    private TextView tv_status;
    private TextView tv_times;
    private EditText tv_num;
    private Button bt_call;
    private Button bt_acceptcall;
    private Button bt_none;
    private Button bt_update;
    private Spinner sp_times;
    private Spinner sp_acceptTime;
    private Spinner sp_callTime;

    private SubscriptionManager mSubscriptionManager;


    private TelephonyManager tManager;
    private StringBuilder info;
    private String[] phoneType = {"NONE", "GSM", "CDMA", "SIP"};
    private String[] simState = {"UNKNOWN", "ABSENT", "PIN_REQUIRED", "PUK_REQUIRED",
            "NETWORK_LOCKED", "READY", "NOT_READY", "PERM_DISABLED", "CARD_IO_ERROR",
            "CARD_RESTRICTED"};
    private String[] dataType = {"UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO_0", "EVDO_A",
            "1xRTT", "HSDPA", "HSUPA", "HSPA", "IDEN", "EVDO_B", "LTE", "EHRPD", "HSPAP", "GSM",
            "TD_SCDMA", "IWLAN", "LTE_CA", "5G"};


    private PermissionUtils mPermissionUtils;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_num = findViewById(R.id.tv_num);
        tv_status = findViewById(R.id.tv_status);
        bt_call = findViewById(R.id.bt_call);
        bt_acceptcall = findViewById(R.id.bt_acceptcall);
        bt_none = findViewById(R.id.bt_none);
        sp_times = findViewById(R.id.sp_times);
        sp_acceptTime = findViewById(R.id.sp_acceptTime);
        sp_callTime = findViewById(R.id.sp_callTime);
        tv_times = findViewById(R.id.tv_times);
        bt_update = findViewById(R.id.bt_update);
        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtils.callPhone(MainActivity.this, tv_num.getText().toString());
                CallActivity.sCall_op = CallActivity.CALL_OP.AUTO_CALL;
            }
        });
        bt_acceptcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
                CallActivity.sCall_op = CallActivity.CALL_OP.CALL_ACCEPT;
                tv_status.setText("自动接听状态：已开启");
            }
        });

        bt_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyService();
                CallActivity.sCall_op = CallActivity.CALL_OP.NONE;
                tv_status.setText("自动接听状态：未开启");
            }
        });

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_times.setText("当前设置拨打次数：" + PhoneUtils.getCallTimes());
            }
        });

        sp_times.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();
                PhoneUtils.setCallTimes(Integer.parseInt(result));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PhoneUtils.setCallTimes(0);
            }
        });

        sp_acceptTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();
                PhoneUtils.setAcceptTime(Integer.parseInt(result));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PhoneUtils.setAcceptTime(3);
            }
        });

        sp_callTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();
                PhoneUtils.setCallTime(Integer.parseInt(result));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PhoneUtils.setCallTime(3);
            }
        });

        if (ServiceUtils.isServiceRunning(this, "com.lauchun.autoanswer.service.CallService")) {
            tv_status.setText("自动接听状态：已开启");
        }


        tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager =
                (SubscriptionManager) getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        mPermissionUtils = new PermissionUtils(this);
        int phoneId = 1;
        int[] subIds = mSubscriptionManager.getSubscriptionIds(phoneId);
        for (int subId : subIds) {
            Log.i("lzzz"," subId=" + subId);
        }
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPermissionUtils.checkPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

    @SuppressLint("MissingPermission")
    private void bindViews() {
        tv_phone = findViewById(R.id.tv_phone);
        info = new StringBuilder();


        try {
            info.append("手机类型：" + this.phoneType[tManager.getPhoneType()]);
            info.append("\n运营商代号：" + tManager.getNetworkOperator());
            info.append("\n运营商名称：" + tManager.getNetworkOperatorName());
            info.append("\n网络类型：" + this.dataType[tManager.getNetworkType()]);
            info.append("\n手机号码：" + tManager.getLine1Number());
            info.append("\nSIM卡的国别：" + tManager.getSimCountryIso().toUpperCase());
            List<SubscriptionInfo> infoList =
                    mSubscriptionManager.getActiveSubscriptionInfoList();
            info.append("\nSIM卡状态：" + this.simState[tManager.getSimState()]);
            if (infoList != null) {
                for (SubscriptionInfo mInfo : infoList) {
                    System.out.println(mInfo);
                }
            }
            boolean b = tManager.setLine1NumberForDisplay("中国联通", "1113");
            System.out.println("setLine1NumberForDisplay: " + b);

        } catch (Exception e) {
            System.out.println("———————————————————————开始Log———————————————————————");
            e.printStackTrace();
            System.out.println("———————————————————————结束Log———————————————————————");
        }
        tv_phone.setText(info);
    }

    private void startService() {
        Intent intent = new Intent(MainActivity.this, CallService.class);
        intent.putExtra("phoneNum", tv_num.getText().toString());
        startForegroundService(intent);
        Log.i("Service", "CallService启动成功");
    }

    private void destroyService() {
        Intent intent = new Intent(MainActivity.this, CallService.class);
        stopService(intent);
        Log.i("Service", "CallService关闭成功");
    }
}