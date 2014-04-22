package com.bestjoy.app.haierwarrantycard.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bestjoy.app.haierwarrantycard.utils.JsonParser;
import com.bestjoy.app.haierwarrantycard.utils.SpeechRecognizerEngine;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class RepairActivity extends BaseActionbarActivity implements OnClickListener {
	private static final String TAG = "RepairActivity";
	private EditText mAskInput;
	//private Handler mHandler;
	private Button mSpeakButton;
	private SpeechRecognizerEngine mSpeechRecognizerEngine;

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
		//mHandler = new Handler();
		setContentView(R.layout.activity_repair_20140418);
		//TextView productSelected = (TextView) findViewById(R.id.product);
		//productSelected.setText(getIntent().getStringExtra(Intents.EXTRA_NAME));
		
		mAskInput = (EditText) findViewById(R.id.product_ask_online_input);
		
		/*mHandler.postDelayed(new Runnable(){

			@Override
			public void run() {
				((ScrollView) findViewById(R.id.scrollview)).scrollTo(0, 0);
			}
			
		}, 100);*/
		initViews();
		mSpeechRecognizerEngine = SpeechRecognizerEngine.getInstance(this, mAskInput);
		
	}
	
	private void initViews() {
		mSpeakButton =  (Button) findViewById(R.id.button_speak);
		mSpeakButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_speak:
			//显示语音听写Dialog.
			mSpeechRecognizerEngine.showIatDialog(RepairActivity.this);
			break;

		default:
			break;
		}
	}
	

	public static void startIntnet(Context context) {
		Intent intent = new Intent(context, RepairActivity.class);
		context.startActivity(intent);
	}
	
	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, RepairActivity.class);
		if (bundle != null) intent.putExtras(bundle);
		context.startActivity(intent);
	}
	public static Intent createIntnet(Context context) {
		return new Intent(context, RepairActivity.class);
	}
}
