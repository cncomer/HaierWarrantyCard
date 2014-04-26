package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.AsyncTaskUtils;

public class NewCardChooseFragment extends SherlockFragment implements View.OnClickListener{
	
	private ListView mDaleiListViews, mXiaoleiListViews, mPinpaiListViews, mXinghaoListViews;
	
	private TextView mDalei, mXiaolei, mPinpai, mXinghao;
	
	private long mDaleiId = -1, mXiaoleiId = -1, mPinpaiId = -1, mXinghaoId = -1;
	
	private BaoxiuCardObject mBaoxiuCardObject;
	
	private static final String[] DALEI_PROJECTION = new String[]{
		HaierDBHelper.ID,
		HaierDBHelper.DEVICE_DALEI_NAME,                //1
		HaierDBHelper.DEVICE_DALEI_ID,
	};
	
	private static final String[] XIAOLEI_PROJECTION = new String[]{
		HaierDBHelper.ID,
		HaierDBHelper.DEVICE_XIALEI_NAME,              //1
		HaierDBHelper.DEVICE_XIALEI_DID,
		HaierDBHelper.DEVICE_XIALEI_XID,
		
		
	};
	
	private static final String[] PINPAI_PROJECTION = new String[]{
		HaierDBHelper.ID,
		HaierDBHelper.DEVICE_PINPAI_NAME,            //1
		HaierDBHelper.DEVICE_PINPAI_XID,
		HaierDBHelper.DEVICE_PINPAI_PID,
		HaierDBHelper.DEVICE_PINPAI_PINYIN,
		HaierDBHelper.DEVICE_PINPAI_CODE,
	};
	
	private static final String XIAOLEI_SELECTION = HaierDBHelper.DEVICE_XIALEI_DID + "=?";
	private static final String PINPAI_SELECTION = HaierDBHelper.DEVICE_PINPAI_XID + "=?";

	private View mProgressBarLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBaoxiuCardObject = new BaoxiuCardObject();
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
		
		mXiaolei = (TextView) view.findViewById(R.id.title_xiaolei);
		mXiaolei.setOnClickListener(this);
		
		mPinpai = (TextView) view.findViewById(R.id.title_pinpai);
		mPinpai.setOnClickListener(this);
		
		mXinghao = (TextView) view.findViewById(R.id.title_xinghao);
		mXinghao.setOnClickListener(this);
		
		
		mDaleiListViews = (ListView) view.findViewById(R.id.dalei);
		mXiaoleiListViews = (ListView) view.findViewById(R.id.xiaolei);
		mPinpaiListViews = (ListView) view.findViewById(R.id.pinpai);
		mXinghaoListViews = (ListView) view.findViewById(R.id.xinghao);
		
		
		mDaleiListViews.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mDaleiListViews.setOnItemSelectedListener(null);
		
		mXiaoleiListViews.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mXiaoleiListViews.setOnItemSelectedListener(null);
		
		mPinpaiListViews.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mPinpaiListViews.setOnItemSelectedListener(null);
		
		mXinghaoListViews.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mXinghaoListViews.setOnItemSelectedListener(null);
		
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		AsyncTaskUtils.cancelTask(mLoadDataAsyncTask);
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
				return getActivity().getContentResolver().query(BjnoteContent.PinPai.CONTENT_URI, PINPAI_PROJECTION, PINPAI_SELECTION, new String[]{String.valueOf(mXiaoleiId)}, null);
			case R.id.xinghao:
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
			((CursorAdapter) _listView.getAdapter()).changeCursor(result);
			_listView.setTag(new Object());
			mProgressBarLayout.setVisibility(View.GONE);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.title_dalei:
			setListViewVisibility(View.VISIBLE, View.GONE, View.GONE, View.GONE);
			if (mDaleiListViews.getTag() == null) {
				//TAG对象表示的是ListView是否已经加载过数据了，否则我们还需要异步加载
				loadDataAsync(mDaleiListViews);
			}
			break;
		case R.id.title_xiaolei:
			if (mDaleiId != -1 && mXiaoleiListViews.getTag() == null) {
				setListViewVisibility(View.GONE, View.VISIBLE, View.GONE, View.GONE);
				loadDataAsync(mXiaoleiListViews);
			}
			break;
		case R.id.title_pinpai:
			if (mXiaoleiId != -1 && mPinpaiListViews.getTag() == null) {
				setListViewVisibility(View.GONE, View.GONE, View.VISIBLE, View.GONE);
				loadDataAsync(mPinpaiListViews);
			}
			break;
		case R.id.xinghao:
//			setListViewVisibility(View.GONE, View.GONE, View.GONE, View.VISIBLE);
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
				viewHoldr._xId = cursor.getLong(3);
				break;
			case R.id.pinpai:
				viewHoldr._id = cursor.getLong(0);
				viewHoldr._xId = cursor.getLong(2);
				viewHoldr._pId = cursor.getLong(3);
				break;
			case R.id.xinghao:
				break;
			}
			viewHoldr._title.setText(cursor.getString(1));
			view.setTag(viewHoldr);
			
		}
		
	}
	
	private class ViewHolder {
		private TextView _title;
		private long _id, _dId, _xId, _pId, _xinghaoId;
		private int _position;
		
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
					
					mXiaoleiId = -1;
					mXiaolei.setText(R.string.title_xiaolei);
					
					mPinpaiId = -1;
					mPinpai.setText(R.string.title_pinpai);
					
					mXinghaoId = -1;
					mXinghao.setText(R.string.title_xinghao);
					
					mXiaoleiListViews.setTag(null);
					mPinpaiListViews.setTag(null);
					mXinghaoListViews.setTag(null);
				}
				break;
			case R.id.xiaolei:
				if (viewHolder._xId != mXiaoleiId) {
					mXiaoleiId = viewHolder._xId;
					mBaoxiuCardObject.mLeiXin = viewHolder._title.getText().toString();
					mXiaolei.setText(getGroupTitle(R.string.title_xiaolei, mBaoxiuCardObject.mLeiXin));
					
					mPinpaiId = -1;
					mPinpai.setText(R.string.title_pinpai);
					
					mXinghaoId = -1;
					mXinghao.setText(R.string.title_xinghao);
					
					mPinpaiListViews.setTag(null);
					mXinghaoListViews.setTag(null);
				}
				break;
			case R.id.pinpai:
				if (viewHolder._pId != mPinpaiId) {
					mPinpaiId = viewHolder._pId;
					mBaoxiuCardObject.mPinPai = viewHolder._title.getText().toString();
					mPinpai.setText(getGroupTitle(R.string.title_pinpai, mBaoxiuCardObject.mPinPai));
					
					mXinghaoId = -1;
					mXinghao.setText(R.string.title_xinghao);
					
					mXinghaoListViews.setTag(null);
				}
				break;
			case R.id.xinghao:
//				if (viewHolder._xinghaoId != mXinghaoId) {
//					mXinghaoId = viewHolder._xinghaoId;
//					mPinpai.setText(getGroupTitle(R.string.title_pinpai, viewHolder._title.getText().toString()));
//					
//					mXinghaoId = -1;
//					mXinghao.setText(R.string.title_xinghao);
//				}
				break;
			}
			
		}
		
	}
}
