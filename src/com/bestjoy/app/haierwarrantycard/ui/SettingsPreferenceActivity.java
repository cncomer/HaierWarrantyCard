/*
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.HaierAccountManager;
import com.bestjoy.app.haierwarrantycard.utils.DebugUtils;
import com.bestjoy.app.haierwarrantycard.utils.MenuHandlerUtils;

public class SettingsPreferenceActivity extends SherlockPreferenceActivity {

	private static final String TAG = "SettingsPreferenceActivity";
	private static final String KEY_ACCOUNT_NAME = "preference_key_account_name";
	private static final String KEY_ACCOUNT_PASSWORD = "preference_key_account_password";
	private EditTextPreference mAccountName;
	private Preference mAccountPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!HaierAccountManager.getInstance().hasLoginned()) {
        	DebugUtils.logD(TAG, "finish Actvitiy due to hasLoginned() return false, you must login in firstlly.");
        	finish();
        	return;
        }
        addPreferencesFromResource(R.xml.settings_preferences);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		mAccountName = (EditTextPreference) getPreferenceScreen().findPreference(KEY_ACCOUNT_NAME);
		mAccountPassword = (Preference) getPreferenceScreen().findPreference(KEY_ACCOUNT_PASSWORD);
		
		mAccountName.setText(HaierAccountManager.getInstance().getAccountObject().mAccountName);
		mAccountName.setSummary(HaierAccountManager.getInstance().getAccountObject().mAccountName);
		
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
     	   Intent upIntent = NavUtils.getParentActivityIntent(this);
     	   if (upIntent == null) {
     		   // If we has configurated parent Activity in AndroidManifest.xml, we just finish current Activity.
     		   finish();
     		   return true;
     	   }
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    
//    @Override
//	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
//    	if (preference == mAccountPassword) {
//    		PasswordModifyFragment fragment = new PasswordModifyFragment();
//    		Bundle bundle = new Bundle();
//    		bundle.putString(KEY_ACCOUNT_PASSWORD, HaierAccountManager.getInstance().getAccountObject().mAccountPwd);
//    		fragment.setArguments(bundle);
//    		fragment.show(getFragmentManager(), "PasswordModifyFragment");
//    		return true;
//    	}
//		return super.onPreferenceTreeClick(preferenceScreen, preference);
//	}


    public static class PasswordModifyFragment extends SherlockDialogFragment implements View.OnClickListener{

    	private EditText _oldInput, _newInput, _newReInput;
    	private Button _saveBtn;
    	private String _oldPassword;
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			super.onCreateOptionsMenu(menu, inflater);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			return super.onOptionsItemSelected(item);
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			_oldPassword = getArguments().getString(KEY_ACCOUNT_PASSWORD);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.setting_preference_password, container, false);
			_oldInput = (EditText) view.findViewById(R.id.title);
			_newInput = (EditText) view.findViewById(R.id.title);
			_newReInput = (EditText) view.findViewById(R.id.title);
			_saveBtn = (Button) view.findViewById(R.id.button_save);
			
			_saveBtn.setOnClickListener(this);
			return view;
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.button_save:
				String oldInput = _oldInput.getText().toString().trim();
				String newInput = _newInput.getText().toString().trim();
				String newReInput = _newReInput.getText().toString().trim();
				
				if (!oldInput.equals(_oldPassword)) {
					MyApplication.getInstance().showMessage(R.string.msg_input_old_password_error);
					return;
				}
				
				if (TextUtils.isEmpty(newInput)) {
					MyApplication.getInstance().showMessage(R.string.hint_input_new_password);
					return;
				}
				
				if (TextUtils.isEmpty(newReInput)) {
					MyApplication.getInstance().showMessage(R.string.hint_reinput_new_password);
					return;
				}
				
				if (!newInput.equals(newReInput)) {
					MyApplication.getInstance().showMessage(R.string.msg_input_new_password_error);
					return;
				}
				
				//开始更新密码
				
				break;
			}
			
		}
		
    	
    }


	public static void startActivity(Context context) {
    	Intent intent = new Intent(context, SettingsPreferenceActivity.class);
    	context.startActivity(intent);
    }
	
}
