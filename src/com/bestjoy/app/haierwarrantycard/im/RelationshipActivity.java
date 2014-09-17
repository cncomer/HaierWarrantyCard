package com.bestjoy.app.haierwarrantycard.im;

import java.io.InputStream;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.MyAccountManager;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerUtilsV2;
import com.bestjoy.app.haierwarrantycard.service.PhotoManagerUtilsV2.TaskType;
import com.bestjoy.app.haierwarrantycard.ui.PullToRefreshListPageActivity;
import com.bestjoy.app.haierwarrantycard.utils.Query;
import com.bestjoy.app.haierwarrantycard.view.AvatorImageView;
import com.shwy.bestjoy.utils.AdapterWrapper;
import com.shwy.bestjoy.utils.InfoInterface;
import com.shwy.bestjoy.utils.PageInfo;

public class RelationshipActivity extends PullToRefreshListPageActivity{
	private static final String TAG = "RelationshipActivity";
	private Handler mHandler;
	private static final int WHAT_REFRESH_LIST = 1000;
	private RelationshipAdapter mRelationshipAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFinishing()) {
			return;
		}
		setShowHomeUp(true);
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
				case WHAT_REFRESH_LIST:
					mRelationshipAdapter.refreshList();
					break;
				}
			}
			
		};
		PhotoManagerUtilsV2.getInstance().requestToken(TAG);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		PhotoManagerUtilsV2.getInstance().releaseToken(TAG);
	}
	
	@Override
	protected AdapterWrapper<? extends BaseAdapter> getAdapterWrapper() {
		mRelationshipAdapter = new RelationshipAdapter(mContext, null, true);
		return new AdapterWrapper<CursorAdapter>(mRelationshipAdapter);
	}

	@Override
	protected Cursor loadLocal(ContentResolver cr) {
		return BjnoteContent.RELATIONSHIP.getAllRelationships(cr, MyAccountManager.getInstance().getCurrentAccountUid());
	}

	@Override
	protected int savedIntoDatabase(ContentResolver cr, List<? extends InfoInterface> infoObjects) {
		int insertOrUpdateCount = 0;
		if (infoObjects != null) {
			for(InfoInterface object:infoObjects) {
				if (object.saveInDatebase(cr, null)) {
					insertOrUpdateCount++;
				}
			}
		}
		return insertOrUpdateCount;
	}

	@Override
	protected List<? extends InfoInterface> getServiceInfoList(InputStream is, PageInfo pageInfo) {
		return RelationshipObject.parseList(is, pageInfo);
	}

	@Override
	protected Query getQuery() {
		Query query =  new Query();
		query.qServiceUrl = HaierServiceObject.getRelationshipUrl(MyAccountManager.getInstance().getCurrentAccountUid(), MyAccountManager.getInstance().getAccountObject().mAccountPwd);
		
		return query;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View itemView, int position, long id) {
		super.onItemClick(parent, itemView, position, id);
		ViewHolder viewHolder = (ViewHolder) itemView.getTag();
		ConversationListActivity.startActivity(mContext, viewHolder._relationshipObject);
	}
	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
	public static void startActivity(Context context) {
		Intent intent = new Intent(context, RelationshipActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		
	}
	@Override
	protected int getContentLayout() {
		return R.layout.pull_to_refresh_page_activity;
	}
	
	private class RelationshipAdapter extends CursorAdapter {

		public RelationshipAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		protected void onContentChanged() {
			mHandler.removeMessages(WHAT_REFRESH_LIST);
			mHandler.sendEmptyMessageDelayed(WHAT_REFRESH_LIST, 500);
		}


		private void refreshList() {
			super.onContentChanged();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = LayoutInflater.from(context).inflate(R.layout.relationship_item, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			
			viewHolder._storeName = (TextView) view.findViewById(R.id.storename);
			viewHolder._name = (TextView) view.findViewById(R.id.name);
			viewHolder._title = (TextView) view.findViewById(R.id.title);
			viewHolder._workExperience = (TextView) view.findViewById(R.id.workexperience);
			viewHolder._workplace = (TextView) view.findViewById(R.id.workplace);
			viewHolder._leixing = (TextView) view.findViewById(R.id.typename);
			viewHolder._avator = (AvatorImageView) view.findViewById(R.id.avator);
			view.setTag(viewHolder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			viewHolder._relationshipObject = RelationshipObject.getFromCursor(cursor);
			viewHolder._name.setText(cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_UNAME));
			viewHolder._storeName.setText(cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_ORG));
			viewHolder._workplace.setText(cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_WORKPLACE));
			String workExperience = cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_BRIEF);
			if (!TextUtils.isEmpty(workExperience)) {
				viewHolder._workExperience.setVisibility(View.VISIBLE);
				viewHolder._workExperience.setText(workExperience);
			} else {
				viewHolder._workExperience.setVisibility(View.INVISIBLE);
			}
			
			viewHolder._leixing.setText(cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_LEIXING));
			viewHolder._title.setText(cursor.getString(BjnoteContent.RELATIONSHIP.INDEX_RELASTIONSHIP_TITLE));
			
			if (viewHolder._relationshipObject.hasAvator()) {
				PhotoManagerUtilsV2.getInstance().loadPhotoAsync(TAG, viewHolder._avator, viewHolder._relationshipObject.mTargetAvator, null, TaskType.IM_AVATOR_PREVIEW);
			}
		}
		
	}
	
	private class ViewHolder {
		private TextView _name, _leixing, _xinghao, _title, _workplace, _workExperience, _storeName;
		private AvatorImageView _avator;
		private RelationshipObject _relationshipObject;
	}
	@Override
	protected void onRefreshStart() {
		BjnoteContent.RELATIONSHIP.delete(getContentResolver(), BjnoteContent.RELATIONSHIP.CONTENT_URI, BjnoteContent.RELATIONSHIP.UID_SELECTION, new String[]{MyAccountManager.getInstance().getCurrentAccountUid()});
	}
	@Override
	protected void onRefreshEnd() {
		
	}


}
