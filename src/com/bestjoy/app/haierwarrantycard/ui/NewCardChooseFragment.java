package com.bestjoy.app.haierwarrantycard.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.XinghaoObject;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.DeviceDBHelper;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.NetworkUtils;

public class NewCardChooseFragment extends SherlockFragment implements View.OnClickListener{
	private static final String TAG = "NewCardChooseFragment";
	private ListView mDaleiListViews, mXiaoleiListViews, mPinpaiListViews, mXinghaoListViews;
	
	private TextView mDalei, mXiaolei, mPinpai, mXinghao;
	
	private long mDaleiId = -1, mPinpaiId = -1, mXinghaoId = -1;
	/**当前选中的品牌的code码*/
	private String mPinPaiCode = null;
	/**选择的小类id,是字符型的，注意*/
	private String mXiaoleiId = null;
	
	private BaoxiuCardObject mBaoxiuCardObject;
	
	private static final String[] DALEI_PROJECTION = new String[]{
		HaierDBHelper.ID,
		DeviceDBHelper.DEVICE_DALEI_NAME,                //1
		DeviceDBHelper.DEVICE_DALEI_ID,
	};
	
	private static final String[] XIAOLEI_PROJECTION = new String[]{
		HaierDBHelper.ID,
		DeviceDBHelper.DEVICE_XIALEI_NAME,              //1
		DeviceDBHelper.DEVICE_XIALEI_DID,
		DeviceDBHelper.DEVICE_XIALEI_XID,
		
		
	};
	
	private static final String[] PINPAI_PROJECTION = new String[]{
		HaierDBHelper.ID,
		DeviceDBHelper.DEVICE_PINPAI_NAME,            //1
		DeviceDBHelper.DEVICE_PINPAI_XID,
		DeviceDBHelper.DEVICE_PINPAI_PID,
		DeviceDBHelper.DEVICE_PINPAI_PINYIN,
		DeviceDBHelper.DEVICE_PINPAI_CODE,
		DeviceDBHelper.DEVICE_PINPAI_BXPHONE,       //6
	};
	
	private static final String XIAOLEI_SELECTION = DeviceDBHelper.DEVICE_XIALEI_DID + "=?";
	private static final String PINPAI_SELECTION = DeviceDBHelper.DEVICE_PINPAI_XID + "=?";

	private View mProgressBarLayout;
	
	/**品牌海尔名称，这个用来在用户选择品牌时候做判断，如果是，那么保修卡的电话是400699999*/
	private String mPinpaiHaierDes = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBaoxiuCardObject = new BaoxiuCardObject();
		mPinpaiHaierDes = getActivity().getString(R.string.pinpai_haier);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.new_card_choose_fragment, container, false);
		mProgressBarLayout = view.findViewById(R.id.progressbarLayout);
//		mExpandableListView = (ExpandableListView) view.findViewById(R.id.listview);
//		mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//			@Override
//			public void onGroupExpand(int groupPosition) {
//				//每次只能显示一组group和child
//				for (int i = 0; i < mExpandableListViewAdapter.getGroupCount(); i++) {
//					if (groupPosition != i) {
//						mExpandableListView.collapseGroup(i);
//					}
//				}
//			}
//
//		});
//		mExpandableListViewAdapter = new ExpandableListViewAdapter();
//		mExpandableListView.setAdapter(mExpandableListViewAdapter);
//		mExpandableListView.setGroupIndicator(null);
//		mExpandableListView.setOnChildClickListener(this);
		
		mDalei = (TextView) view.findViewById(R.id.title_dalei);
		mDalei.setOnClickListener(this);
		TextPaint tp = mDalei.getPaint();
		tp.setFakeBoldText(true);
		
		
		mXiaolei = (TextView) view.findViewById(R.id.title_xiaolei);
		mXiaolei.setOnClickListener(this);
		tp = mXiaolei.getPaint();
		tp.setFakeBoldText(true);
		
		mPinpai = (TextView) view.findViewById(R.id.title_pinpai);
		mPinpai.setOnClickListener(this);
		tp = mPinpai.getPaint();
		tp.setFakeBoldText(true);
		
		mXinghao = (TextView) view.findViewById(R.id.title_xinghao);
		mXinghao.setOnClickListener(this);
		tp = mXinghao.getPaint();
		tp.setFakeBoldText(true);
		
		
		mDaleiListViews = (ListView) view.findViewById(R.id.dalei);
		mXiaoleiListViews = (ListView) view.findViewById(R.id.xiaolei);
		mPinpaiListViews = (ListView) view.findViewById(R.id.pinpai);
		mXinghaoListViews = (ListView) view.findViewById(R.id.xinghao);
		
		initListView(mDaleiListViews);
		initListView(mXiaoleiListViews);
		initListView(mPinpaiListViews);
		initListView(mXinghaoListViews);
		
		setListViewVisibility(View.GONE, View.GONE, View.GONE, View.GONE);
		
		return view;
	}
	
	private void initListView(ListView listView) {
		listView.setAdapter(new Adapter(getActivity(), listView.getId(), false));
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(new ListViewItemSelectedListener(listView.getId()));
	}
	
	private void releaseAdapter(CursorAdapter adapter) {
		adapter.changeCursor(null);
		adapter = null;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		AsyncTaskUtils.cancelTask(mLoadDataAsyncTask);
		releaseAdapter((CursorAdapter)mDaleiListViews.getAdapter());
		releaseAdapter((CursorAdapter)mXiaoleiListViews.getAdapter());
		releaseAdapter((CursorAdapter)mPinpaiListViews.getAdapter());
		releaseAdapter((CursorAdapter)mXinghaoListViews.getAdapter());
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public BaoxiuCardObject getBaoxiuCardObject() {
		return mBaoxiuCardObject;
	}
	
	private LoadDataAsyncTask mLoadDataAsyncTask;
	public void loadDataAsync(ListView listview) {
		AsyncTaskUtils.cancelTask(mLoadDataAsyncTask);
		mProgressBarLayout.setVisibility(View.VISIBLE);
		AsyncTaskUtils.cancelTask(mLoadDataAsyncTask);
		mLoadDataAsyncTask = new LoadDataAsyncTask(listview);
		mLoadDataAsyncTask.execute();
	}
	
	private class LoadDataAsyncTask extends AsyncTask<Void, Void, Cursor> {
		private ListView _listView;
		public LoadDataAsyncTask(ListView listView) {
			_listView = listView;
		}

		@Override
		protected Cursor doInBackground(Void... arg0) {
			switch(_listView.getId()) {
			case R.id.dalei:
				return getActivity().getContentResolver().query(BjnoteContent.DaLei.CONTENT_URI, DALEI_PROJECTION, null, null, null);
			case R.id.xiaolei:
				return getActivity().getContentResolver().query(BjnoteContent.XiaoLei.CONTENT_URI, XIAOLEI_PROJECTION, XIAOLEI_SELECTION, new String[]{String.valueOf(mDaleiId)}, null);
			case R.id.pinpai:
				return getActivity().getContentResolver().query(BjnoteContent.PinPai.CONTENT_URI, PINPAI_PROJECTION, PINPAI_SELECTION, new String[]{mXiaoleiId}, null);
			case R.id.xinghao:
				//对于型号来说，由于要从服务器上获取，所以，这里的而处理与前三者不同，我们先要判断是否本地已经缓存了，有则直接使用，没有则先获取数据保存导本地再查询出来。
				Cursor c = getActivity().getContentResolver().query(BjnoteContent.XingHao.CONTENT_URI, XinghaoObject.XINGHAO_PROJECTION, XinghaoObject.XINGHAO_CODE_SELECTION, new String[]{mPinPaiCode}, null);
				//TODO 这里可能需要判断即使已经有数据了，也要重新更新型号列表，如新增，目前咱不住处理
				if (c != null) {
					if (c.getCount() > 0) {
						return c;
					} else {
						c.close();
						//下载型号列表
						InputStream is = null;
						try {
							if (!ComConnectivityManager.getInstance().isConnected()) {
								//没有网络连接，提示用户
								MyApplication.getInstance().showMessageAsync(R.string.msg_can_not_access_network);
								return null;
							}
							is = NetworkUtils.openContectionLocked(XinghaoObject.getUpdateUrl(mPinPaiCode), MyApplication.getInstance().getSecurityKeyValuesObject());
							if (is == null) {
								DebugUtils.logD(TAG, "can't open connection " + XinghaoObject.getUpdateUrl(mPinPaiCode));
							} else {
								MyApplication.getInstance().showMessageAsync(R.string.msg_download_xinghao_wait);
								List<XinghaoObject> list = XinghaoObject.parse(is, mPinPaiCode);
								if (list.size() > 0) {
									DebugUtils.logD(TAG, "find " + list.size() + " records for pinpaiCode " + mPinPaiCode);
									ContentResolver cr = getActivity().getContentResolver();
									for(XinghaoObject object:list) {
										object.saveInDatebase(cr, null);
									}
								}
								return getActivity().getContentResolver().query(BjnoteContent.XingHao.CONTENT_URI, XinghaoObject.XINGHAO_PROJECTION, XinghaoObject.XINGHAO_CODE_SELECTION, new String[]{mPinPaiCode}, null);
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
				break;
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mProgressBarLayout.setVisibility(View.GONE);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			if (result == null || result != null && result.getCount() == 0) {
				switch(_listView.getId()) {
				case R.id.dalei:
				case R.id.xiaolei:
				case R.id.pinpai:
					break;
				case R.id.xinghao:
					MyApplication.getInstance().showMessageAsync(R.string.msg_download_no_xinghao_wait);
					break;
				}
				
			}
			((CursorAdapter) _listView.getAdapter()).changeCursor(result);
			_listView.setTag(new Object());
			mProgressBarLayout.setVisibility(View.GONE);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_dalei:
			setListViewVisibility(mDaleiListViews.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE, View.GONE, View.GONE, View.GONE);
			if (mDaleiListViews.getTag() == null) {
				//TAG对象表示的是ListView是否已经加载过数据了，否则我们还需要异步加载
				loadDataAsync(mDaleiListViews);
			}
			break;
		case R.id.title_xiaolei:
			if (mDaleiId != -1) {
				setListViewVisibility(View.GONE, mXiaoleiListViews.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE, View.GONE, View.GONE);
				if (mXiaoleiListViews.getTag() == null) {
					loadDataAsync(mXiaoleiListViews);
				}
				
			}
			break;
		case R.id.title_pinpai:
			if (mXiaoleiId != null) {
				setListViewVisibility(View.GONE, View.GONE, mPinpaiListViews.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE, View.GONE);
				if (mPinpaiListViews.getTag() == null) {
					loadDataAsync(mPinpaiListViews);
				}
			}
			break;
		case R.id.title_xinghao:
			if (mPinPaiCode != null) {
				setListViewVisibility(View.GONE, View.GONE, View.GONE, mXinghaoListViews.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE);
				if (mXinghaoListViews.getTag() == null) {
					loadDataAsync(mXinghaoListViews);
				}
			}
			break;
		}
		
	}
	
	private void setListViewVisibility(int showDalei, int showXiaolei, int showPinpai, int showXinghao) {
		mDaleiListViews.setVisibility(showDalei);
		mXiaoleiListViews.setVisibility(showXiaolei);
		mPinpaiListViews.setVisibility(showPinpai);
		mXinghaoListViews.setVisibility(showXinghao);
	}

	
	private class Adapter extends CursorAdapter {
		private int _listViewId;

		public Adapter(Context context, int listViewId, boolean autoRequery) {
			super(context, null, autoRequery);
			_listViewId = listViewId;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(getActivity()).inflate(R.layout.child_textview, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder viewHoldr = (ViewHolder) view.getTag();
			if (view.getTag() == null) {
				viewHoldr = new ViewHolder();
			}
			viewHoldr._title = (TextView) view;
			switch(_listViewId){
			case R.id.dalei:
				viewHoldr._dId = cursor.getLong(2);
				viewHoldr._id = cursor.getLong(0);
				break;
			case R.id.xiaolei:
				viewHoldr._id = cursor.getLong(0);
				viewHoldr._dId = cursor.getLong(2);
				viewHoldr._xId = cursor.getString(3);
				break;
			case R.id.pinpai:
				viewHoldr._id = cursor.getLong(0);
				viewHoldr._xId = cursor.getString(2);
				viewHoldr._pId = cursor.getLong(3);
				viewHoldr._pinpaiCode = cursor.getString(5);
				viewHoldr.mBXphone = cursor.getString(6);
				break;
			case R.id.xinghao:
				viewHoldr._id = cursor.getLong(0);
				viewHoldr._pinpaiCode = cursor.getString(3);
				viewHoldr._mn = cursor.getString(1);
				viewHoldr._ky = cursor.getString(2);
				break;
			}
			viewHoldr._title.setText(cursor.getString(1));
			view.setTag(viewHoldr);
			
		}
		
	}
	
	private class ViewHolder {
		private TextView _title;
		private long _id, _dId, _pId, _xinghaoId;
		private int _position;
		private String _xId, _pinpaiCode, _mn, _ky, mBXphone;
		
	}
	
	
	private String getGroupTitle(int titleId, String subTitle) {
		StringBuilder sb = new StringBuilder();
		sb.append(getString(titleId)).append(getString(R.string.title_choose_type_divider)).append(subTitle);
		return sb.toString();
	}
	
	private class ListViewItemSelectedListener implements AdapterView.OnItemClickListener{

		private int _listViewId;
		public ListViewItemSelectedListener(int listViewId) {
			_listViewId = listViewId;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			switch(_listViewId) {
			case R.id.dalei:
				if (viewHolder._dId != mDaleiId) {
					mDaleiId = viewHolder._dId;
					mDalei.setText(getGroupTitle(R.string.title_dalei, viewHolder._title.getText().toString()));
					
					mXiaoleiId = null;
					mXiaolei.setText(R.string.title_xiaolei);
					
					mPinpaiId = -1;
					mPinPaiCode = null;
					mPinpai.setText(R.string.title_pinpai);
					
					mXinghaoId = -1;
					mXinghao.setText(R.string.title_xinghao);
					
					mXiaoleiListViews.setTag(null);
					mPinpaiListViews.setTag(null);
					mXinghaoListViews.setTag(null);
					parent.setVisibility(View.GONE);
					mXiaolei.performClick();
				}
				break;
			case R.id.xiaolei:
				if (viewHolder._xId != mXiaoleiId) {
					mXiaoleiId = viewHolder._xId;
					mBaoxiuCardObject.mLeiXin = viewHolder._title.getText().toString();
					mXiaolei.setText(getGroupTitle(R.string.title_xiaolei, mBaoxiuCardObject.mLeiXin));
					
					mPinpaiId = -1;
					mPinPaiCode = null;
					mPinpai.setText(R.string.title_pinpai);
					
					mXinghaoId = -1;
					mXinghao.setText(R.string.title_xinghao);
					
					mPinpaiListViews.setTag(null);
					mXinghaoListViews.setTag(null);
					parent.setVisibility(View.GONE);
					mPinpai.performClick();
				}
				break;
			case R.id.pinpai:
				if (viewHolder._pId != mPinpaiId) {
					mPinpaiId = viewHolder._pId;
					mPinPaiCode = viewHolder._pinpaiCode;
					mBaoxiuCardObject.mPinPai = viewHolder._title.getText().toString();
					mBaoxiuCardObject.mBXPhone = viewHolder.mBXphone;
					
					mPinpai.setText(getGroupTitle(R.string.title_pinpai, mBaoxiuCardObject.mPinPai));
					
					mXinghaoId = -1;
					mXinghao.setText(R.string.title_xinghao);
					
					mXinghaoListViews.setTag(null);
					parent.setVisibility(View.GONE);
					mXinghao.performClick();
				}
				break;
			case R.id.xinghao:
				if (viewHolder._id != mXinghaoId) {
					mXinghaoId = viewHolder._id;
					mBaoxiuCardObject.mXingHao = viewHolder._mn;
					mBaoxiuCardObject.mKY = viewHolder._ky;
					mXinghao.setText(getGroupTitle(R.string.title_xinghao, mBaoxiuCardObject.mXingHao));
				}
				break;
			}
			
		}
		
	}
}
