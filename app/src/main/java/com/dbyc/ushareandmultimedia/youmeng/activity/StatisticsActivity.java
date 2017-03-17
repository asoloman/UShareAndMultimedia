package com.dbyc.ushareandmultimedia.youmeng.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dbyc.ushareandmultimedia.BaseActivity;
import com.dbyc.ushareandmultimedia.R;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class StatisticsActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_input;
    private Button btn_charge;
    private Button btn_chargeok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        initView();
    }

    private void initView() {
        et_input = (EditText) findViewById(R.id.et_input);
        btn_charge = (Button) findViewById(R.id.btn_charge);
        btn_chargeok = (Button) findViewById(R.id.btn_chargeOk);
        btn_charge.setOnClickListener(this);
        btn_chargeok.setOnClickListener(this);

        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    btn_chargeok.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_charge:
                goCharge();
                break;
            case R.id.btn_chargeOk:
                goChargeOk();
                break;
        }
    }

    private void goCharge() {
        et_input.setVisibility(View.VISIBLE);
        MobclickAgent.onEvent(this, "event_charge");
    }

    private void goChargeOk() {
        String money = et_input.getText().toString();
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "请先输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap map = new HashMap();
        map.put("money", money);
        MobclickAgent.onEvent(this, "event_chargeOk", map);
        Toast.makeText(this, "成功充值" + money, Toast.LENGTH_SHORT).show();
        et_input.setText("");
        et_input.setVisibility(View.INVISIBLE);
        btn_chargeok.setVisibility(View.INVISIBLE);
    }
}
