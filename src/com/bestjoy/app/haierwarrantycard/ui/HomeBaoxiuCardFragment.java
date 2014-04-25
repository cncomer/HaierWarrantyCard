package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.shwy.bestjoy.utils.AsyncTaskUtils;

public class HomeBaoxiuCardFragment extends SherlockFragment implements OnItemClickListener{
	
	private HomeObject mHomeObject;
	private ListView mListView;
	private CardsAdapter mCardsAdapter;
	private OnBaoxiuCardItemClickListener mOnItemClickListener;
	
	public static interface OnBaoxiuCardItemClickListener {
		void onItemClicked(BaoxiuCardObject card) ;
	}
	public void setHomeBaoxiuCard(HomeObject homeObject) {
		mHomeObject = homeObject;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_baoxiucard_fragment, container, false);
		mListView = (ListView) view.findViewById(R.id.listview);
		mCardsAdapter = new CardsAdapter(getActivity(), null, true);
		mListView.setAdapter(mCardsAdapter);
		mListView.setOnItemClickListener(this);
		return view;
	}
	
	public void setOnItemClickListener(OnBaoxiuCardItemClickListener listener) {
		mOnItemClickListener = listener;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadCardsAsync();
	}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHomeObject = null;
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		AsyncTaskUtils.cancelTask(mLoadCardsTask);
		mCardsAdapter.changeCursor(null);
		mCardsAdapter = null;
	}


	private LoadCardsTask mLoadCardsTask;
	private void loadCardsAsync() {
		AsyncTaskUtils.cancelTask(mLoadCardsTask);
		if (mHomeObject != null) {
			mLoadCardsTask = new LoadCardsTask();
			mLoadCardsTask.execute();
		}
		
	}

	private class LoadCardsTask extends AsyncTask<Void, Void, Cursor> {

		@Override
		protected Cursor doInBackground(Void... params) {
			return BaoxiuCardObject.getAllBaoxiuCardsCursor(getActivity().getContentResolver(), mHomeObject.mHomeUid, mHomeObject.mHomeAid);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			super.onPostExecute(result);
			mCardsAdapter.changeCursor(result);
		}
		
		
	}
	
	private class CardsAdapter extends CursorAdapter {

		public CardsAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.home_baoxiucard_list_item, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			BaoxiuCardObject card = BaoxiuCardObject.getFromBaoxiuCardsCursor(cursor);
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				holder = new ViewHolder();
				holder._tag = (TextView) view.findViewById(R.id.tag);
				holder._pinpai = (TextView) view.findViewById(R.id.pinpai);
				holder._xinghao = (TextView) view.findViewById(R.id.xinghao);
				holder._title2 = (TextView) view.findViewById(R.id.title2);
				holder._title3 = (TextView) view.findViewById(R.id.title3);
				view.setTag(holder);
			}
			//设置view
			holder._tag.setText(card.mCardName);
			holder._pinpai.setText(card.mPinPai);
			holder._xinghao.setText(card.mXingHao);
			holder._card = card;
			
			//整机保修
			int validity = card.getBaoxiuValidity();
			if (validity > 0) {
				if (validity > 999) {
					holder._title3.setText(getString(R.string.baoxiucard_validity_toomuch));
				} else {
					holder._title3.setText(getString(R.string.baoxiucard_validity, validity));
				}
				
			} else {
				holder._title3.setText(getString(R.string.baoxiucard_outdate));
			}
			
			//主要部件保修
			validity = card.getComponentBaoxiuValidity();
			if (validity > 0) {
				holder._title2.setText(getString(R.string.baoxiucard_validity, validity));
			} else {
				holder._title2.setText(getString(R.string.baoxiucard_outdate));
			}
			
		}
		
	}
	
	private static final class ViewHolder {
		//分别是保修卡名字，品牌， 型号， 部件保修剩余时间， 整机保修剩余时间
		private TextView _tag, _pinpai, _xinghao, _title2, _title3;
		//分别是部件保修和整机保修布局(整个)
		private View _component, _zhengji;
		private BaoxiuCardObject _card;
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mOnItemClickListener != null) {
			//回调主Activity，如果有的话
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			mOnItemClickListener.onItemClicked(viewHolder._card);
		}
		
	}
	
	
	
}
