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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

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
    private TextView tv_times;
    private EditText tv_num;
    private Button bt_call;
    private Button bt_acceptcall;
    private Button bt_none;
    private Button bt_update;
    private Spinner spinner;

    private TelephonyManager tManager;
    private StringBuilder info;
    private String[] phoneType = {"NONE", "GSM", "CDMA", "SIP"};
    private String[] simState = {"UNKNOWN", "ABSENT", "PIN_REQUIRED", "PUK_REQUIRED",
            "NETWORK_LOCKED", "READY", "NOT_READY", "PERM_DISABLED", "CARD_IO_ERROR",
            "CARD_RESTRICTED"};
    private String[] dataType = {"UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO_0", "EVDO_A",
            "1xRTT", "HSDPA", "HSUPA", "HSPA", "IDEN", "EVDO_B", "LTE", "EHRPD", "HSPAP", "GSM",
            "TD_SCDMA", "IWLAN", "LTE_CA", "5G"};


    private PermissionUtil mPermissionUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_num = findViewById(R.id.tv_num);
        tv_status = findViewById(R.id.tv_status);
        bt_call = findViewById(R.id.bt_call);
        bt_acceptcall = findViewById(R.id.bt_acceptcall);
        bt_none = findViewById(R.id.bt_none);
        spinner = findViewById(R.id.times);
        tv_times = findViewById(R.id.tv_times);
        bt_update = findViewById(R.id.bt_update);
        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneUtil.callPhone(MainActivity.this, tv_num.getText().toString());
                CallActivity.sCall_op = CallActivity.CALL_OP.AUTO_CALL;
            }
        });
        bt_acceptcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
                CallActivity.sCall_op = CallActivity.CALL_OP.CALL_ACCEPT;
                tv_status.setText("??????????????????????????????");
            }
        });

        bt_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyService();
                CallActivity.sCall_op = CallActivity.CALL_OP.NONE;
                tv_status.setText("??????????????????????????????");
            }
        });

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_times.setText("???????????????????????????" + PhoneUtil.getCallTimes());
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String result = parent.getItemAtPosition(position).toString();
                PhoneUtil.setCallTimes(Integer.parseInt(result));
                System.out.println(result);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                PhoneUtil.setCallTimes(0);
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
            info.append("???????????????" + this.phoneType[tManager.getPhoneType()]);
            info.append("\n??????????????????" + tManager.getNetworkOperator());
            info.append("\n??????????????????" + tManager.getNetworkOperatorName());
            info.append("\n???????????????" + this.dataType[tManager.getNetworkType()]);
            info.append("\n???????????????" + tManager.getLine1Number());
            info.append("\nSIM???????????????" + tManager.getSimCountryIso().toUpperCase());
            info.append("\nSIM????????????" + this.simState[tManager.getSimState()]);
            System.out.println("------------???????????????" + tManager.getNetworkType());
        } catch (Exception e) {
            System.out.println("???????????????????????????????????????????????????????????????????????????Log?????????????????????????????????????????????????????????????????????");
            e.printStackTrace();
            System.out.println("???????????????????????????????????????????????????????????????????????????Log?????????????????????????????????????????????????????????????????????");
        }
        tv_phone.setText(info);
    }

    private void startService() {
        Intent intent = new Intent(MainActivity.this, CallService.class);
        intent.putExtra("phoneNum", tv_num.getText().toString());
        startForegroundService(intent);
        Log.i("Service", "CallService????????????");
    }

    private void destroyService() {
        Intent intent = new Intent(MainActivity.this, CallService.class);
        stopService(intent);
        Log.i("Service", "CallService????????????");
    }
}