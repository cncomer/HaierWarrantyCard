package com.bestjoy.app.haierwarrantycard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.AccountObject;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

public class LoginActivity extends BaseActionbarActivity implements View.OnClickListener{
	private static final String TAG = "NewCardActivity";

	private TextView mRegisterButton;
	private static final int REQUEST_LOGIN = 1;
	private Button mLoginBtn;
	private EditText mTelInput, mPasswordInput;
	public static AccountObject mAccountObject;
	/**进入界面请求,如新建我的保修卡进来的*/
	private int mModelId;

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
		mModelId = ModleSettings.getModelIdFromBundle(getIntent().getExtras());
		setContentView(R.layout.activity_login_20140415);
		initViews();
	}
	
	public void onResume() {
		super.onResume();
		//每次进来我们都要先清空一下mAccountObject，这个值作为静态变量在各个Activity中传递
		mAccountObject = null;
	}
	
	
	private void initViews() {
		mRegisterButton = (TextView) findViewById(R.id.button_register);
		mRegisterButton.setOnClickListener(this);
		
		mLoginBtn = (Button) findViewById(R.id.button_login);
		mLoginBtn.setOnClickListener(this);
		
		mTelInput = (EditText) findViewById(R.id.tel);
		//显示上一次输入的用户号码
		mTelInput.setText(HaierAccountManager.getInstance().getLastUsrTel());
		
		mPasswordInput = (EditText) findViewById(R.id.pwd);
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
	 }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.button_register:
				RegisterActivity.startIntent(this);
				break;
			case R.id.button_login:
				String tel = mTelInput.getText().toString().trim();
				String pwd = mPasswordInput.getText().toString().trim();
				if (!TextUtils.isEmpty(tel) && !TextUtils.isEmpty(pwd)) {
					HaierAccountManager.getInstance().saveLastUsrTel(tel);
					startActivityForResult(LoginOrUpdateAccountDialog.createLoginOrUpdate(this, true, tel, pwd), REQUEST_LOGIN);
				} else {
					MyApplication.getInstance().showMessage(R.string.msg_input_usrtel_password);
				}
				break;
		}
		
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LOGIN) {
			if (resultCode == Activity.RESULT_OK) {
				// login successfully
				MyApplication.getInstance().showMessage(R.string.msg_login_confirm_success);
				switch(mModelId) {
				case R.id.model_my_card:
				case R.id.model_install:
				case R.id.model_repair:
					MyChooseDevicesActivity.startIntent(mContext, ModleSettings.createMyCardDefaultBundle(mContext));
					finish();
					break;
					default : //其他情况我们回到主界面，海尔要求
						MainActivity.startActivityForTop(mContext);
						finish();
						break;
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public static void startIntent(Context context, Bundle modelBundle) {
		Intent intent = new Intent(context, LoginActivity.class);
		if (modelBundle == null) {
			modelBundle = new Bundle();
		}
		intent.putExtras(modelBundle);
		context.startActivity(intent);
	}
	
}
