package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;

public class MyCardActivity extends CommonButtonTitleActivity {
	private static final String TAG = "MyCardActivity";
	private ListView mProductList;

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
		setContentView(R.layout.activity_my_card);
		mProductList = (ListView) findViewById(R.id.listview);
		//TODO set debug adapter
		mProductList.setAdapter(new DebugProductsAdapter(this));
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			default:
				super.onClick(v);
		}
		
	}
	
	public static void startIntent(Context context) {
		Intent intent = new Intent(context, MyCardActivity.class);
		context.startActivity(intent);
	}
	
}
