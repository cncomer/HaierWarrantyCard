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

import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class RepairActivity extends BaseActionbarActivity implements OnClickListener {
	private static final String TAG = "RepairActivity";
	private EditText mAskInput;
	//private Handler mHandler;
	private Button mSpeakButton;
	// Tip
	private Toast mToast;
	//识别窗口
	private RecognizerDialog iatDialog;
	//识别对象
	private SpeechRecognizer iatRecognizer;
	//用户词表下载结果
	private String mDownloadResult = "";	

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
		//创建听写对象,如果只使用无UI听写功能,不需要创建RecognizerDialog
		iatRecognizer=SpeechRecognizer.createRecognizer(this);
		//初始化听写Dialog,如果只使用有UI听写功能,无需创建SpeechRecognizer
		iatDialog =new RecognizerDialog(this);
		mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		
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
			showIatDialog();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 显示听写对话框.
	 * @param
	 */
	public void showIatDialog()
	{
		if(null == iatDialog) {
		//初始化听写Dialog	
		iatDialog =new RecognizerDialog(this);
		}

				
		//清空Grammar_ID，防止识别后进行听写时Grammar_ID的干扰
		iatDialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);;
		//设置听写Dialog的引擎
		iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
		iatDialog.setParameter(SpeechConstant.SAMPLE_RATE, "16000");

		//清空结果显示框.
			mAskInput.setText(null);
		//显示听写对话框
		iatDialog.setListener(recognizerDialogListener);
		iatDialog.show();
		showTip("开始说话");
	}
RecognizerListener recognizerListener=new RecognizerListener()
{

	@Override
	public void onBeginOfSpeech() {	
		showTip("开始说话");
	}


	@Override
	public void onError(SpeechError err) {
		showTip(err.getPlainDescription(true));
	}

	@Override
	public void onEndOfSpeech() {
		showTip("结束说话");
	}

	@Override
	public void onEvent(int eventType, int arg1, int arg2, String msg) {

	}

	@Override
	public void onResult(RecognizerResult results, boolean isLast) {		
		String text = JsonParser.parseIatResult(results.getResultString());
		mAskInput.append(text);
		mAskInput.setSelection(mAskInput.length());
	}

	@Override
	public void onVolumeChanged(int volume) {
		showTip("当前正在说话，音量大小：" + volume);
	}
	
};
/**
 * 识别回调监听器
 */
RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener()
{
	@Override
	public void onResult(RecognizerResult results, boolean isLast) {
		String text = JsonParser.parseIatResult(results.getResultString());
		mAskInput.append(text);
		mAskInput.setSelection(mAskInput.length());
	}

	/**
	 * 识别回调错误.
	 */
	public void onError(SpeechError error) {
		
	}
	
};
private void showTip(String str)
{
	if(!TextUtils.isEmpty(str))
	{
		mToast.setText(str);
		mToast.show();
	}
}
/**
 * 获取字节流对应的字符串,文件默认编码为UTF-8
 * @param inputStream
 * @return
 * @throws UnsupportedEncodingException
 * @throws IOException
 */
private String readStringFromInputStream(InputStream inputStream)
		throws UnsupportedEncodingException, IOException {
	BufferedReader reader = new BufferedReader(new InputStreamReader(
			inputStream, "UTF-8"));
	StringBuilder builder = new StringBuilder();
	String line;
	while ((line = reader.readLine()) != null) {
		builder.append(line);
	}
	return builder.toString();
}

	public static void startIntnet(Context context) {
		Intent intent = new Intent(context, RepairActivity.class);
		context.startActivity(intent);
	}
	
	public static Intent createIntnet(Context context) {
		return new Intent(context, RepairActivity.class);
	}
	
}
