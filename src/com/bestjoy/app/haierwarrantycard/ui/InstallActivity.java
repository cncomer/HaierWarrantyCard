package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;

public class InstallActivity extends BaseActionbarActivity {
	private static final String TAG = "InstallActivity";
	private EditText mAskInput;
	// private Handler mHandler;
	private Button mSpeakButton;
	// Tip
	private Toast mToast;
	// 识别窗口
	private RecognizerDialog iatDialog;
	// 识别对象
	private SpeechRecognizer iatRecognizer;
	// 用户词表下载结果
	private String mDownloadResult = "";

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugUtils.logD(TAG, "onCreate()");
		if (isFinishing()) {
			return;
		}
		// mHandler = new Handler();
		setContentView(R.layout.activity_install_20140418);

	}

	public static void startIntnet(Context context) {
		Intent intent = new Intent(context, InstallActivity.class);
		context.startActivity(intent);
	}

	public static Intent createIntnet(Context context) {
		return new Intent(context, InstallActivity.class);
	}

	public static void startIntent(Context context, Bundle bundle) {
		Intent intent = new Intent(context, InstallActivity.class);
		if (bundle != null) intent.putExtras(bundle);
		context.startActivity(intent);
	}

}
