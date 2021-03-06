package com.bestjoy.app.haierwarrantycard.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;
import com.shwy.bestjoy.utils.DebugUtils;

public class ModuleView extends RelativeLayout{
	private static final String TAG = "ModuleView";
	
	private TextView mTitleView, mTitleEnView;
	private ImageView mIconImage;

	public ModuleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTitleView = (TextView) this.findViewById(R.id.title);
		mTitleEnView = (TextView) this.findViewById(R.id.en_title);
		mIconImage = (ImageView) this.findViewById(R.id.icon);
	}
	
	
	public void setUnreadNum(int num) {
	}
	
	public void setTitle(CharSequence text) {
		if (mTitleView != null) mTitleView.setText(text);
	}
	public void setTitle(int resId) {
		if (mTitleView != null) mTitleView.setText(resId);
	}
	public void setEntitle(CharSequence text) {
		if (mTitleEnView != null) mTitleEnView.setText(text);
	}
	public void setEntitle(int resId) {
		if (mTitleEnView != null) mTitleEnView.setText(resId);
	}
	
	public void setIcon(int resId) {
		if (mIconImage != null) {
			mIconImage.setImageResource(resId);
		}
	}
	
	public static ModuleView findViewById(int id, Activity activity) {
		View view = activity.findViewById(id);
		if (view != null && view instanceof ModuleView) {
			return (ModuleView) view;
		}
		DebugUtils.logD(TAG, "findViewById return null for viewId " + id);
		return null;
	}
	
	
	public static class ScanModuleView extends ModuleView {

		private Button mScanBtn;
		public ScanModuleView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		@Override
		protected void onFinishInflate() {
			super.onFinishInflate();
			mScanBtn = (Button) this.findViewById(R.id.button_scan);
		}
		
	}
	
}
