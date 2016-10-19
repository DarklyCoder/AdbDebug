package com.darklycoder.adbdebug;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mBtnStatus;
    TextView mTextStatus, mTextTip;

    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStatus = (Button) findViewById(R.id.btn_status);
        mTextStatus = (TextView) findViewById(R.id.tv_status);
        mTextTip = (TextView) findViewById(R.id.tv_tip);

        boolean isRoot = Utils.isRoot();
        if (!isRoot) {
            Toast.makeText(this, R.string.no_root_msg, Toast.LENGTH_LONG);
            finish();
        }

        mBtnStatus.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status = Utils.isOpened(this);
        if (status) {
            setConnectSuccess();
        } else {
            setConnectFailed();
        }
    }

    private void setConnectSuccess() {
        status = true;
        mTextTip.setVisibility(View.VISIBLE);
        mTextTip.setText(getResources().getString(R.string.run_cmd, Utils.getIP(MainActivity.this) + ":5555"));
        mBtnStatus.setText(getResources().getString(R.string.close_adb));

        String str = "Status：开";
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(str);
        spanBuilder.setSpan(new TextAppearanceSpan(null, Typeface.BOLD, 0, null, null), 7, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#70CB18")), 7, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanBuilder.setSpan(new AbsoluteSizeSpan(20, true), 7, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mTextStatus.setText(spanBuilder);
    }

    private void setConnectFailed() {
        mBtnStatus.setText(getResources().getString(R.string.open_adb));
        status = false;
        mTextTip.setVisibility(View.GONE);

        String str = "Status：关";
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(str);
        spanBuilder.setSpan(new TextAppearanceSpan(null, Typeface.BOLD, 0, null, null), 7, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#F24140")), 7, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanBuilder.setSpan(new AbsoluteSizeSpan(20, true), 7, str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mTextStatus.setText(spanBuilder);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnStatus) {
            if (status == false) {
                int status_value = Utils.startAdbWifiDebug(MainActivity.this, status);
                if (1 == status_value) {
                    setConnectSuccess();

                } else if (-1 == status_value) {
                    Toast.makeText(MainActivity.this, R.string.no_wifi_msg, Toast.LENGTH_LONG);

                } else if (-2 == status_value) {
                    Toast.makeText(MainActivity.this, R.string.open_error_msg, Toast.LENGTH_LONG);
                }

            } else {
                Utils.stopAdbWifiDebug(status);
                setConnectFailed();
            }
        }
    }

}
