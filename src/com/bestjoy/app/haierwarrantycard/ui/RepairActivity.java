package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

public class RepairActivity extends CommonButtonTitleActivity {
	private static final String TAG = "RepairActivity";
	private EditText mAskInput;
	private Handler mHandler;

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
		mHandler = new Handler();
		setContentView(R.layout.activity_repair);
		TextView productSelected = (TextView) findViewById(R.id.product);
		productSelected.setText(getIntent().getStringExtra(Intents.EXTRA_NAME));
		
		mAskInput = (EditText) findViewById(R.id.product_ask_online_input);
		
		mHandler.postDelayed(new Runnable(){

			@Override
			public void run() {
				((ScrollView) findViewById(R.id.scrollview)).scrollTo(0, 0);
			}
			
		}, 100);
		
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			default:
				super.onClick(v);
		}
		
	}
	
	public static void startIntnet(Context context) {
		Intent intent = new Intent(context, RepairActivity.class);
		context.startActivity(intent);
	}
	
	public static Intent createIntnet(Context context) {
		return new Intent(context, RepairActivity.class);
	}
	
}
