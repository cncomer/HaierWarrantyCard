package com.bestjoy.app.haierwarrantycard.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.database.BjnoteContent;
import com.bestjoy.app.haierwarrantycard.database.HaierDBHelper;
import com.shwy.bestjoy.utils.AsyncTaskUtils;

public class ChooseDevicesPinpaiFragment extends SherlockFragment{
	private ExpandableListView mExpandableListView;
	private ExpandableListViewAdapter mExpandableListViewAdapter;
	
	private static final String[] GROUP_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_DALEI_ID,
		HaierDBHelper.DEVICE_DALEI_NAME,
	};
	
	private static final String[] CHILD_PROJECTION = new String[]{
		HaierDBHelper.DEVICE_XIALEI_DID,
		HaierDBHelper.DEVICE_XIALEI_XID,
		HaierDBHelper.DEVICE_XIALEI_NAME,
	};

	private View mProgressBarLayout;
	private boolean mIsLoaded = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_choose_devices, null);
		mProgressBarLayout = view.findViewById(R.id.progressbarLayout);
		mExpandableListView = (ExpandableListView) view.findViewById(R.id.listview);
		mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				//每次只能显示一组group和child
				for (int i = 0; i < mExpandableListViewAdapter.getGroupCount(); i++) {
					if (groupPosition != i) {
						mExpandableListView.collapseGroup(i);
					}
				}
			}

		});
		mExpandableListViewAdapter = new ExpandableListViewAdapter();
		mExpandableListView.setAdapter(mExpandableListViewAdapter);
		mExpandableListView.setGroupIndicator(null);
		return view;
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
		loadDataAsync();
	}



	private LoadDataAsyncTask mLoadDataAsyncTask;
	public void loadDataAsync() {
		AsyncTaskUtils.cancelTask(mLoadDataAsyncTask);
		mProgressBarLayout.setVisibility(View.VISIBLE);
		AsyncTaskUtils.cancelTask(mLoadDataAsyncTask);
		mLoadDataAsyncTask = new LoadDataAsyncTask();
		mLoadDataAsyncTask.execute();
	}
	
	private class LoadDataAsyncTask extends AsyncTask<Void, Void, Void> {

		private ArrayList<GroupObject> _groupsList = new ArrayList<GroupObject>();
		private HashMap<Long, ArrayList<ChildObject>> _childsList = new HashMap<Long, ArrayList<ChildObject>>();
		
		@Override
		protected Void doInBackground(Void... arg0) {
			mIsLoaded = false;
			_groupsList = GroupObject.initGroupData(getActivity());
			if (_groupsList.size() > 0) {
				ArrayList<ChildObject> childList = ChildObject.initChildData(getActivity());
				ArrayList<ChildObject> childListInGroup = null;
				for(GroupObject group : _groupsList) {
					childListInGroup = new ArrayList<ChildObject>();
					for(ChildObject child : childList) {
						if (group._id == child._dId) {
							childListInGroup.add(child);
							_childsList.put(group._id, childListInGroup);
						}
					}
				}
			} else {
				_childsList.clear();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mProgressBarLayout.setVisibility(View.GONE);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressBarLayout.setVisibility(View.GONE);
			mIsLoaded = true;
			mExpandableListViewAdapter.changeGroupsAndChilds(_groupsList, _childsList);
		}
		
	}
	
	
	private class ExpandableListViewAdapter extends BaseExpandableListAdapter {

		private List<GroupObject> _goupList = new ArrayList<GroupObject>();
		private HashMap<Long, ArrayList<ChildObject>> _childsList = new HashMap<Long, ArrayList<ChildObject>>();
		
		public void changeGroupsAndChilds(List<GroupObject> group, HashMap<Long, ArrayList<ChildObject>> map) {
			
			if (_goupList != group) {
				_goupList.clear();
				_goupList = group;
			}
			if (_childsList != map) {
				_childsList.clear();
				_childsList = map;
			}
			notifyDataSetChanged();
		}
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			long groupId = getGroupId(groupPosition);
			return _childsList.get(groupId).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			long groupId = getGroupId(groupPosition);
			return _childsList.get(groupId).get(childPosition)._xId;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder childViewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.child_textview, parent, false);
				childViewHolder = new ChildViewHolder();
				childViewHolder._name = (TextView) convertView;
				convertView.setTag(childViewHolder);
			} else {
				childViewHolder = (ChildViewHolder) convertView.getTag();
			}
			childViewHolder._childObject = (ChildObject) getChild(groupPosition, childPosition);
			childViewHolder._name.setText(childViewHolder._childObject._name);
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			long groupId = getGroupId(groupPosition);
			return _childsList.get(groupId).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return _goupList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return _goupList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return _goupList.get(groupPosition)._id ;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			GroupViewHolder groupViewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.group_textview, parent, false);
				groupViewHolder = new GroupViewHolder();
				groupViewHolder._name = (TextView) convertView;
				convertView.setTag(groupViewHolder);
			} else {
				groupViewHolder = (GroupViewHolder) convertView.getTag();
			}
			groupViewHolder._groupObject = (GroupObject) getGroup(groupPosition);
			groupViewHolder._name.setText(groupViewHolder._groupObject._name);
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
	
	private static class GroupViewHolder {
		private TextView _name;
		private GroupObject _groupObject;
	}
	private static class ChildViewHolder {
		private TextView _name;
		private ChildObject _childObject;
	}
	
	private static class GroupObject {
		private long _id;
		private String _name;
		
		private static ArrayList<GroupObject> initGroupData(Context context) {
			ArrayList<GroupObject> groups = new ArrayList<GroupObject>();
			Cursor c = context.getContentResolver().query(BjnoteContent.DaLei.CONTENT_URI, GROUP_PROJECTION, null, null, null);
			if (c != null) {
				groups = new ArrayList<GroupObject>(c.getCount());
				GroupObject group = null;
				while(c.moveToNext()) {
					group = new GroupObject();
					group._id = c.getLong(0);
					group._name = c.getString(1);
					groups.add(group);
				}
				c.close();
			}
			return groups;
		}
	}
	
	private static class ChildObject {
		private long _dId;
		private long _xId;
		private String _name;
		
		private static ArrayList<ChildObject> initChildData(Context context) {
			ArrayList<ChildObject> childs = new ArrayList<ChildObject>();
			Cursor c = context.getContentResolver().query(BjnoteContent.XiaoLei.CONTENT_URI, CHILD_PROJECTION, null, null, null);
			if (c != null) {
				childs = new ArrayList<ChildObject>(c.getCount());
				ChildObject child = null;
				while(c.moveToNext()) {
					child = new ChildObject();
					child._dId = c.getLong(0);
					child._xId = c.getLong(1);
					child._name = c.getString(2);
					childs.add(child);
				}
				c.close();
			}
			
			return childs;
		}
	}

}
