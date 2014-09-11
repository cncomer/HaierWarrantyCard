package com.bestjoy.app.haierwarrantycard.im;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
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

import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.ui.BaseActionbarActivity;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DebugUtils;

public class IMConversationActivity extends BaseActionbarActivity implements View.OnClickListener{

	private static final String TAG = "IMConversationActivity";
	private int mCoversationTargetType = IMHelper.TARGET_TYPE_QUN;
	private String mCoversationTarget = "";
	private ListView mListView;
	private EditText mInputEdit;
	private Button mButtonCommit;
	//为了简单起见，所有的异常都直接往外抛  
    private static final String HOST = "192.168.1.149";//"115.29.231.29";  //要连接的服务端IP地址  
    private static final int PORT = 1029;   //要连接的服务端对应的监听端口 
    private static final int BUFFER_LENGTH = 4 * 1024; //4k
    private CoversationReceiveServerThread mCoversationReceiveServerThread;
	private DatagramSocket mSocket;
	BufferedOutputStream mOutputStream;
	BufferedInputStream mIntputStream;
	
	private HandlerThread mWorkThread;
	private Handler mWorkHandler;
	private Handler mUiHandler;
	/**发送登录消息*/
	private static final int WHAT_SEND_MESSAGE_LOGIN = 1000;
	/**获得消息*/
	private static final int WHAT_SEND_MESSAGE = 1001;
	/**发送退出消息*/
	private static final int WHAT_SEND_MESSAGE_EXIT = 1002;
	/**请求刷新界面*/
	private static final int WHAT_REQUEST_REFRESH_LIST = 1003;
	private static final int HEART_BEAT_DELAY_TIME = 30 * 1000;
	/**在会话结束前，我们需要等待，比如退出当前界面*/
	private boolean mIsLogined = false;
	/**如果当前在列表底部了，当有新消息到来的时候我们需要自动滚定到最新的消息处，否则提示下面有新的消息*/
	private boolean mIsAtListBottom = false;
	
	WifiManager.MulticastLock mMulticastLock;
	private ConversationAdapter mConversationAdapter;
	private ContentResolver mContentResolver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.isFinishing()) {
			return;
		}
		mContentResolver = mContext.getContentResolver();
		WifiManager manager = (WifiManager) this .getSystemService(Context.WIFI_SERVICE);
		mMulticastLock = manager.createMulticastLock("IMConversationActivity wifi");
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
				case WHAT_SEND_MESSAGE_LOGIN:
					mButtonCommit.setEnabled(mIsLogined);
					break;
				case WHAT_SEND_MESSAGE_EXIT:
					dismissDialog(DIALOG_PROGRESS);
					finish();
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
		mWorkThread = new HandlerThread("IMConversationThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mWorkThread.start();
		mWorkHandler = new Handler(mWorkThread.getLooper()) {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what) {
				case WHAT_SEND_MESSAGE_LOGIN:
					//发送登录消息
					try {
						sendMessageLocked(IMHelper.createOrJoinConversation(
								MyAccountManager.getInstance().getCurrentAccountUid(), 
								MyAccountManager.getInstance().getAccountObject().mAccountPwd,
								MyAccountManager.getInstance().getAccountObject().mAccountName).toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				case WHAT_SEND_MESSAGE_EXIT:
					try {
						sendMessageLocked(IMHelper.exitConversation(
								MyAccountManager.getInstance().getCurrentAccountUid(), 
								MyAccountManager.getInstance().getAccountObject().mAccountPwd,
								MyAccountManager.getInstance().getAccountObject().mAccountName).toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				case WHAT_SEND_MESSAGE:
					try {
						//在发送消息的时候，我们先在本地新增一条发送中的数据
						ConversationItemObject message = new ConversationItemObject();
						message.mUid = MyAccountManager.getInstance().getCurrentAccountUid();
						message.mPwd = MyAccountManager.getInstance().getAccountObject().mAccountPwd;
						message.mUName = MyAccountManager.getInstance().getAccountObject().mAccountName;
						message.mTargetType = mCoversationTargetType;
						message.mTarget = mCoversationTarget;
						message.mMessage = (String)msg.obj;
						message.mMessageStatus = 0;
						message.saveInDatebaseWithoutCheckExisted(mContentResolver, null);
						sendMessageLocked(IMHelper.createMessageConversation(message).toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					break;
				
				}
			}
			
		};
		mCoversationReceiveServerThread = new CoversationReceiveServerThread();
		mCoversationReceiveServerThread.start();
		
		loadLocalMessageAsync();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mWorkHandler.removeMessages(WHAT_SEND_MESSAGE_LOGIN);
		mWorkThread.quit();
		mCoversationReceiveServerThread.cancel();
		mMulticastLock.release();
		AsyncTaskUtils.cancelTask(mLocalMessageAsyncTask);
		mContentResolver = null;
	}
	
	@Override
	public void onBackPressed() {
		if (mIsLogined) {
    		//显示退出登录对话框
    		showDialog(DIALOG_PROGRESS);
    		mWorkHandler.sendEmptyMessage(WHAT_SEND_MESSAGE_EXIT);
    		return;
    	}
		super.onBackPressed();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
        	if (mIsLogined) {
        		//显示退出登录对话框
        		showDialog(DIALOG_PROGRESS);
        		mWorkHandler.sendEmptyMessage(WHAT_SEND_MESSAGE_EXIT);
        		return true;
        	}
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
        return true;

    }
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button_commit:
			String text = mInputEdit.getText().toString().trim();
			if (!TextUtils.isEmpty(text)) {
				//不允许只输入空白字符，这样的内容是无意义的
				mInputEdit.getText().clear();
				Message msg = Message.obtain();
				msg.what = WHAT_SEND_MESSAGE;
				msg.obj = text;
				mWorkHandler.sendMessage(msg);
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
		Intent intent = new Intent(context, IMConversationActivity.class);
		intent.putExtra(IMHelper.EXTRA_TARGET, target);
		intent.putExtra(IMHelper.EXTRA_TYPE, targetType);
		context.startActivity(intent);
	}
	
	private void sendMessageLocked(final byte[] data) {
		if(data.length!=0){
			try{
				DatagramPacket dp=new DatagramPacket(data, data.length, InetAddress.getByName(HOST), PORT);
				mSocket.send(dp);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	private void receiveMessageLocked(String message) {
		DebugUtils.logD(TAG, "receiveMessageLocked receive: " + message);
		if (!TextUtils.isEmpty(message)) {
			IMHelper.ImServiceResultObject serviceResult = IMHelper.ImServiceResultObject.parse(message);
			if (serviceResult.isOpSuccessfully()) {
				int type = Integer.valueOf(serviceResult.mType);
				switch(type){
				case IMHelper.TYPE_LOGIN: //登录成功
					mIsLogined = true;
					mUiHandler.sendEmptyMessage(WHAT_SEND_MESSAGE_LOGIN);
					break;
				case IMHelper.TYPE_EXIT: //退出登录成功
					mIsLogined = false;
					mUiHandler.sendEmptyMessage(WHAT_SEND_MESSAGE_EXIT);
					break;
				case IMHelper.TYPE_MESSAGE: //我发送的消息得到了返回
				case IMHelper.TYPE_MESSAGE_FORWARD: //收到别人发送的消息
					ConversationItemObject conversationItemObject = IMHelper.getConversationItemObject(serviceResult.mJsonData);
					if (conversationItemObject != null) {
						conversationItemObject.mUid = serviceResult.mUid;
						conversationItemObject.mUName = serviceResult.mUName;
						conversationItemObject.mPwd = serviceResult.mPwd;
						if (IMHelper.TYPE_MESSAGE == type) {
							if (conversationItemObject.hasId()) {
								//服务器返回了我们之前发送的信息，表明该条消息发送成功，我们更新本地信息的发送状态
								conversationItemObject.mMessageStatus = 1;
								conversationItemObject.saveInDatebase(mContentResolver, null);
							}
						} else if (IMHelper.TYPE_MESSAGE_FORWARD == type) {
							//收到其他用户发来的消息，我们只要保存就好了
							conversationItemObject.saveInDatebase(mContentResolver, null);
						}
					}
					break;
				}
			} else {
				MyApplication.getInstance().showMessageAsync(MyApplication.getInstance().getGernalNetworkError());
			}
		}
	}
	
	
	
	private class CoversationReceiveServerThread extends Thread {

		private boolean _cancel = false;
		
		private void cancel() {
			_cancel = true;
		}
		@Override
		public void run() {
			super.run();
		      try {
		    	  DebugUtils.logD(TAG, "start Conversation.");
		    	  if(mSocket == null){
		    		  mSocket = new DatagramSocket(null);
		    		  mSocket.setReuseAddress(true);
		    		  mSocket.bind(new InetSocketAddress(8904));
		    	 }
		    	 DebugUtils.logD(TAG, "准备接受UDP");
				 mMulticastLock.acquire();
		    	 mWorkHandler.sendEmptyMessage(WHAT_SEND_MESSAGE_LOGIN);//立即登录一次
		 		//心跳检测
//		 		mWorkHandler.sendEmptyMessageDelayed(WHAT_SEND_MESSAGE_LOGIN, HEART_BEAT_DELAY_TIME);
				byte[] buffer = new byte[BUFFER_LENGTH];
				DatagramPacket dp=new DatagramPacket(buffer,BUFFER_LENGTH);
				while(!_cancel){
					mSocket.receive(dp);
//					lst.append("对方（来自"+dp.getAddress().getHostAddress()+"，接口:"+dp.getPort()+"） "+"当前时间："+"\n"+new String(buf,0,dp.getLength())+"\n");
					String message = new String(buffer, 0, dp.getLength(), "utf-8");
					receiveMessageLocked(message);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(mSocket != null) mSocket.close();
				DebugUtils.logD(TAG, "close Conversation.");
			}
		      //建立连接后就可以往服务端写数据了  
		}
    	
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
//			viewHolder._time.setText(IMHelper.LOCAL_DATE_TIME_FORMAT.format(Date(Long.valueOf(cursor.getString(IMHelper.INDEX_LOCAL_TIME)))));
			
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
			return IMHelper.getAllLocalMessage(mContentResolver, MyAccountManager.getInstance().getCurrentAccountUid(), mCoversationTargetType, mCoversationTarget);
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
