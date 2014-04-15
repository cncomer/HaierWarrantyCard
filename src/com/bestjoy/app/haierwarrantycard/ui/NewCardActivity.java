package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

public class NewCardActivity extends CommonButtonTitleActivity {
	private static final String TAG = "NewCardActivity";

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
		setContentView(R.layout.activity_new_card);
		findViewById(R.id.button_save).setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button_save:
			LoginActivity.startIntent(this);
			break;
			default:
				super.onClick(v);
		}
		
	}
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, NewCardActivity.class);
		context.startActivity(intent);
	}
	
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, NewCardActivity.class);
		if (bundle != null) intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
}
