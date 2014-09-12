package com.bestjoy.app.haierwarrantycard.im;

import java.util.Date;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.service.IMService;
import com.bestjoy.app.haierwarrantycard.ui.BaseActionbarActivity;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.NotifyRegistrant;

public class ConversationListActivity extends BaseActionbarActivity implements View.OnClickListener{

	private static final String TAG = "IMConversationActivity";
	private static final int WHAT_REQUEST_REFRESH_LIST = 11000;
	private int mCoversationTargetType = IMHelper.TARGET_TYPE_QUN;
	private String mCoversationTarget = "";
	private ListView mListView;
	private EditText mInputEdit;
	private Button mButtonCommit;
	/**如果当前在列表底部了，当有新消息到来的时候我们需要自动滚定到最新的消息处，否则提示下面有新的消息*/
	private boolean mIsAtListBottom = false;
	private Handler mUiHandler;
	
	private IMService mImService;
	private ServiceConnection mServiceConnection;
	
	private ConversationAdapter mConversationAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.isFinishing()) {
			return;
		}
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mImService = ((IMService.MyBinder)service).getService();
				mButtonCommit.setEnabled(mImService.isConnected());
				NotifyRegistrant.getInstance().register(mUiHandler);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				NotifyRegistrant.getInstance().unRegister(mUiHandler);
			}
		};
		Intent intent = new Intent(mContext, IMService.class);
		
		mContext.bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
		setContentView(R.layout.activity_im_conversation);
		
		mListView = (ListView) findViewById(R.id.listview);
		mConversationAdapter = new ConversationAdapter(this, null, true);
		mListView.setAdapter(mConversationAdapter);
		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (totalItemCount > 0) {
					if (firstVisibleItem == 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
						mIsAtListBottom = true;
					} else if (firstVisibleItem > 0 && firstVisibleItem + visibleItemCount < totalItemCount) {
						mIsAtListBottom = false;
					} else {
						mIsAtListBottom = true;
					}
					
				}
				
			}
			
		});
		mInputEdit = (EditText) findViewById(R.id.input);
		mButtonCommit = (Button) findViewById(R.id.button_commit);
		mButtonCommit.setOnClickListener(this);
		//当连接上IM服务器的时候设置为true
		mButtonCommit.setEnabled(false);
		mUiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
				case IMService.WHAT_SEND_MESSAGE_LOGIN:
					mButtonCommit.setEnabled(mImService.isConnected());
					break;
				case IMService.WHAT_SEND_MESSAGE_EXIT:
					mButtonCommit.setEnabled(mImService.isConnected());
					break;
				case WHAT_REQUEST_REFRESH_LIST:
					mConversationAdapter.callSuperOnContentChanged();
					if (mIsAtListBottom) {
						mListView.setSelection(mConversationAdapter.getCount());
					} else {
						MyApplication.getInstance().showMessage(R.string.new_msg_comming);
					}
					break;
				}
			}
		};
		
		loadLocalMessageAsync();
		IMService.connectIMService(this);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		AsyncTaskUtils.cancelTask(mLocalMessageAsyncTask);
		mContext.unbindService(mServiceConnection);
		NotifyRegistrant.getInstance().unRegister(mUiHandler);
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button_commit:
			if (!ComConnectivityManager.getInstance().isConnected()) {
				MyApplication.getInstance().showMessage(MyApplication.getInstance().getGernalNetworkError());
				return;
			}
			String text = mInputEdit.getText().toString().trim();
			if (!TextUtils.isEmpty(text)) {
				mInputEdit.getText().clear();
				//不允许只输入空白字符，这样的内容是无意义的
				ConversationItemObject message = new ConversationItemObject();
				message.mUid = MyAccountManager.getInstance().getCurrentAccountUid();
				message.mPwd = MyAccountManager.getInstance().getAccountObject().mAccountPwd;
				message.mUName = MyAccountManager.getInstance().getAccountObject().mAccountName;
				message.mTargetType = mCoversationTargetType;
				message.mTarget = mCoversationTarget;
				message.mMessage = text;
				message.mMessageStatus = 0;
				mImService.sendMessageAsync(message);
			}
			break;
		}
	}
	
	@Override
	protected boolean checkIntent(Intent intent) {
		mCoversationTarget = intent.getStringExtra(IMHelper.EXTRA_TARGET);
		mCoversationTargetType = intent.getIntExtra(IMHelper.EXTRA_TYPE, -1);
		if (TextUtils.isEmpty(mCoversationTarget)) {
			DebugUtils.logE(TAG, "checkIntent failed, you must supply IMHelper.EXTRA_TARGET");
			return false;
		}
		if (mCoversationTargetType == -1) {
			DebugUtils.logE(TAG, "checkIntent failed, you must supply IMHelper.EXTRA_TYPE");
			return false;
		}
		return true;
	}
	
	public static void startActivity(Context context, int targetType, String target) {
		Intent intent = new Intent(context, ConversationListActivity.class);
		intent.putExtra(IMHelper.EXTRA_TARGET, target);
		intent.putExtra(IMHelper.EXTRA_TYPE, targetType);
		context.startActivity(intent);
	}
	
	private class ConversationAdapter extends CursorAdapter{
		private static final int TYPE_TOP = 0;
		private static final int TYPE_LEFT = 1;
		private static final int TYPE_RIGHT = 2;
		
		private static final int TYPE_COUNT = 3;

		public ConversationAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}
		
		@Override
		protected void onContentChanged() {
			//一秒内延迟刷新，提高性能
			mUiHandler.removeMessages(WHAT_REQUEST_REFRESH_LIST);
			mUiHandler.sendEmptyMessageDelayed(WHAT_REQUEST_REFRESH_LIST, 1000);
		}
		
		private void callSuperOnContentChanged() {
			super.onContentChanged();
		}


		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = null;
			ViewHolder viewHolder = new ViewHolder();
			int viewType = getItemViewType(cursor.getPosition());
			if (TYPE_LEFT == viewType) {
				view = LayoutInflater.from(context).inflate(R.layout.conversation_item_left, parent, false);
			} else if (TYPE_RIGHT == viewType) {
				view = LayoutInflater.from(context).inflate(R.layout.conversation_item_right, parent, false);
			}
			viewHolder._avator = (ImageView) view.findViewById(R.id.avator);
			viewHolder._name = (TextView) view.findViewById(R.id.name);
			viewHolder._content = (TextView) view.findViewById(R.id.content);
			viewHolder._time = (TextView) view.findViewById(R.id.date);
			view.setTag(viewHolder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			viewHolder._name.setText(cursor.getString(IMHelper.INDEX_UNAME));
			viewHolder._content.setText(cursor.getString(IMHelper.INDEX_TEXT));
			if (cursor.getInt(IMHelper.INDEX_STATUS) == 0) {
				viewHolder._time.setText(R.string.msg_sending);
			} else {
				viewHolder._time.setText(IMHelper.LOCAL_DATE_TIME_FORMAT.format(new Date(Long.valueOf(cursor.getString(IMHelper.INDEX_SERVICE_TIME)))));
			}
			
			
		}
		

		@Override
		public int getItemViewType(int position) {
			Cursor c = (Cursor) getItem(position);
			String sender = c.getString(IMHelper.INDEX_UID);
			if (sender.equals(MyAccountManager.getInstance().getCurrentAccountUid())) {
				//用户自己的消息显示在右边
				return TYPE_RIGHT;
			} else {
				return TYPE_LEFT;
			}
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_COUNT;
		}
	}
	
	private static class ViewHolder {
		private ImageView _avator;
		private TextView _name, _content, _time;
	}
	
	private LocalMessageAsyncTask mLocalMessageAsyncTask;
	private void loadLocalMessageAsync() {
		AsyncTaskUtils.cancelTask(mLocalMessageAsyncTask);
		mLocalMessageAsyncTask = new LocalMessageAsyncTask();
		mLocalMessageAsyncTask.execute();
	}
	
	private class LocalMessageAsyncTask extends AsyncTask<Void, Void, Cursor> {

		@Override
		protected Cursor doInBackground(Void... params) {
			return IMHelper.getAllLocalMessage(mContext.getContentResolver(), MyAccountManager.getInstance().getCurrentAccountUid(), mCoversationTargetType, mCoversationTarget);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			mConversationAdapter.changeCursor(result);
			if (mConversationAdapter.getCount() > 0) {
				mListView.setSelection(mConversationAdapter.getCount());
			}
			
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		
	}

}
