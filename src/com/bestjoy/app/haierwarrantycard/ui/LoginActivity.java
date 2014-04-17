package com.bestjoy.app.haierwarrantycard.ui;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends CommonButtonTitleActivity {
	private static final String TAG = "NewCardActivity";

	private Button mRegisterButton;

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		setContentView(R.layout.activity_login_20140415);
		this.initViews();
	}
	
	
	private void initViews() {
		mRegisterButton = (Button) findViewById(R.id.button_register);
		mRegisterButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.button_register:
				RegisterConfirmActivity.startIntent(this);
				break;
			default:
				super.onClick(v);
		}
		
	}
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}
	
}
