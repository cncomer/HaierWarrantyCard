package com.bestjoy.app.haierwarrantycard.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.account.HomeObject;
import com.shwy.bestjoy.utils.AsyncTaskUtils;

public class HomeBaoxiuCardFragment extends SherlockFragment{
	
	private HomeObject mHomeObject;
	private ListView mListView;
	private CardsAdapter mCardsAdapter;
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
		return view;
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
			// TODO Auto-generated method stub
			return new TextView(context);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			BaoxiuCardObject card = BaoxiuCardObject.getFromBaoxiuCardsCursor(cursor);
			((TextView) view).setText(card.mSHBianHao);
			
		}
		
	}
	
	
	
}
