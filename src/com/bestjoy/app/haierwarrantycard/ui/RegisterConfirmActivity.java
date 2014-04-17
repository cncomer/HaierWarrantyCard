package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

public class RegisterConfirmActivity extends CommonButtonTitleActivity{
	private static final String TAG = "RegisterConfirmActivity";
	
	private Button mNextButton;

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return ;
		}
		setContentView(R.layout.activity_register_confirm);
		this.initViews();
	}

	private void initViews() {
		mNextButton = (Button) findViewById(R.id.button_next);
		mNextButton.setOnClickListener(this);
	}

	public static void startIntent(Context context) {
		Intent intent = new Intent(context, RegisterConfirmActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_next:
				RegisterActivity.startIntent(this);
				break;
		}
	}
}
