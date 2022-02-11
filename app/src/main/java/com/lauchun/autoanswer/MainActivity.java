package com.lauchun.autoanswer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lauchun.autoanswer.listener.CallReceiver;
import com.lauchun.autoanswer.service.CallService;
import com.lauchun.autoanswer.utils.PermissionUtil;
import com.lauchun.autoanswer.utils.PhoneUtil;

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
    private EditText tv_num;
    private Button bt_call;
    private Button bt_acceptcall;
    private Button bt_none;
    private TelephonyManager tManager;
    private StringBuilder info;
    private String[] phoneType = {"NONE", "GSM", "CDMA", "SIP"};
    private String[] simState = {"UNKNOWN", "ABSENT", "PIN_REQUIRED", "PUK_REQUIRED",
            "NETWORK_LOCKED", "READY", "NOT_READY", "PERM_DISABLED", "CARD_IO_ERROR",
            "CARD_RESTRICTED"};
    private String[] dataType = {"UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO_0", "EVDO_A",
            "1xRTT", "HSDPA", "HSUPA", "HSPA", "IDEN", "EVDO_B", "LTE", "EHRPD", "HSPAP", "GSM",
            "TD_SCDMA", "IWLAN", "LTE_CA" ,"5G"};


    private PermissionUtil mPermissionUtil;
    private CallReceiver callReceiver = null;
    private CallService callService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_num = (EditText) findViewById(R.id.tv_num);
        tv_status = findViewById(R.id.tv_status);
        bt_call = (Button) findViewById(R.id.bt_call);
        bt_acceptcall = (Button) findViewById(R.id.bt_acceptcall);
        bt_none = (Button) findViewById(R.id.bt_none);
        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtil.callPhone(MainActivity.this,tv_num.getText().toString());
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
        tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPermissionUtil = new PermissionUtil(this);
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPermissionUtil.checkPermissions(PERMISSIONS)) {
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
            info.append("\nSIM卡状态：" + this.simState[tManager.getSimState()]);
            System.out.println("------------网络类型：" + this.dataType[tManager.getNetworkType()]);
        } catch (Exception e) {
            System.out.println("———————————————————————开始Log———————————————————————");
            e.printStackTrace();
            System.out.println("———————————————————————结束Log———————————————————————");
        }
        tv_phone.setText(info);
    }





    private void startService() {
        Intent intent = new Intent(MainActivity.this,CallService.class);
        intent.putExtra("phoneNum", tv_num.getText().toString());
        startForegroundService(intent);
        Log.i("Service", "CallService启动成功");
    }

    private void destroyService() {
        Intent intent = new Intent(MainActivity.this,CallService.class);
        stopService(intent);
        Log.i("Service", "CallService关闭成功");
    }
}