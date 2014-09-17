package com.bestjoy.app.haierwarrantycard.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.internal.view.menu.MenuPresenter;
import com.actionbarsherlock.internal.view.menu.SubMenuBuilder;
import com.actionbarsherlock.internal.widget.IcsLinearLayout;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.view.MenuPopupHelper;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.ImageHelper;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseNoActionBarActivity extends SherlockFragmentActivity implements MenuBuilder.Callback{
	private static final String TAG = "BaseActivity";

	private static final int CurrentPictureGalleryRequest = 11000;
	private static final int CurrentPictureCameraRequest = 11001;
	private int mCurrentPictureRequest;
	protected Activity mContext;
	
	private ImageView mHomeBtn;
	private TextView mTitleView;
	private LinearLayout mTitleBar;
	private RelativeLayout mTitleLayout;
	private FrameLayout mContent;
	private IcsLinearLayout mActionMenuLayout;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		DebugUtils.logD(TAG, "onCreate()");
		if (!checkIntent(getIntent())) {
			finish();
			DebugUtils.logD(TAG, "checkIntent() failed, finish this activiy");
			return;
		}
		mContext = this;
		initTitleBar();
		invalidateOptionsMenu();
		setShowHomeUp(false);
		
	}
	//add by chenkai, 兼容ActionBar和自定义的居中TitleBar on
	private void initTitleBar() {
		if (mTitleView == null) {
			super.setContentView(getTitlebarLayout());
			mTitleBar = (LinearLayout) super.findViewById(R.id.cncom_title_bar);
			mTitleLayout = (RelativeLayout) super.findViewById(R.id.cncom__title_layout);
			mActionMenuLayout = (IcsLinearLayout) super.findViewById(R.id.cncom__action_menu);
			mTitleView = (TextView) super.findViewById(R.id.cncom__title);
			mContent = (FrameLayout) super.findViewById(R.id.cncom_content);
			mHomeBtn = (ImageView) super.findViewById(R.id.cncom__up);
			mTitleView.setText(getTitle());
			mHomeBtn.setOnClickListener(mTitleActionOnClickListener);
		}
		
		
	}
	@Override
	public void setContentView(int layoutResId) {
		initTitleBar();
		LayoutInflater.from(mContext).inflate(layoutResId, mContent, true);
	}
	
	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}
	
    public void setTitle(CharSequence title) {
    	initTitleBar();
		mTitleView.setText(title);
		super.setTitle(title);
    }
    /**返回TitleBar*/
    public View getTitleBar() {
    	return mTitleBar;
    }
    /**返回TitleBar上的Title*/
    public View getTitleLayout() {
    	return mTitleLayout;
    }
    public void setTitleColor(int textColor) {
    	initTitleBar();
		mTitleView.setTextColor(textColor);
		super.setTitleColor(textColor);
    }
	//add by chenkai, 兼容ActionBar和自定义的居中TitleBar off
	
	//add by chenkai, 20140726 增加youmeng统计时长 begin
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	/**
	 * 提供自定义的TitleBar布局
	 * @return
	 */
	protected int getTitlebarLayout() {
		return R.layout.title_bar;
	}
	//add by chenkai, 20140726 增加youmeng统计时长 end
    protected abstract boolean checkIntent(Intent intent);
	
	public static final int DIALOG_PICTURE_CHOOSE_CONFIRM = 10002;
	//add by chenkai, 20131208, for updating check
	/**SD不可�?*/
	protected static final int DIALOG_MEDIA_UNMOUNTED = 10003;
	
	public static final int DIALOG_DATA_NOT_CONNECTED = 10006;//数据连接不可�?
	public static final int DIALOG_MOBILE_TYPE_CONFIRM = 10007;//
	
	
	public static final int DIALOG_PROGRESS = 10008;
	private ProgressDialog mProgressDialog;
	/**
	 * @param uri 选择的图库的图片的Uri
	 * @return
	 */
	protected void onPickFromGalleryFinish(Uri uri) {
	}
    protected void onPickFromCameraFinish() {
	}
    protected void onPickFromGalleryStart() {
	}
    protected void onPickFromCameraStart() {
	}
    protected void onMediaUnmountedConfirmClick() {
   	}
    protected void onDialgClick(int id, DialogInterface dialog, boolean ok, int witch) {
   	}
	/**
	 * pick avator from local gallery app.
	 * @return
	 */
    protected void pickFromGallery() {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
    	Intent intent = ImageHelper.createGalleryIntent();
    	startActivityForResult(intent, CurrentPictureGalleryRequest);
	}
	/**
	 * pick avator by camera
	 * @param savedFile
	 */
    protected void pickFromCamera(File savedFile) {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
		Intent intent = ImageHelper.createCaptureIntent(Uri.fromFile(savedFile));
		startActivityForResult(intent, CurrentPictureCameraRequest);
	}
    
    /**
	 * pick avator from local gallery app.
	 * @return
	 */
    protected void pickFromGallery(int questCode) {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
    	Intent intent = ImageHelper.createGalleryIntent();
    	startActivityForResult(intent, questCode);
	}
	/**
	 * pick avator by camera
	 * @param savedFile
	 */
    protected void pickFromCamera(File savedFile, int questCode) {
    	if (!MyApplication.getInstance().hasExternalStorage()) {
			MyApplication.getInstance().showMessage(R.string.msg_no_sdcard);
			return;
		}
		Intent intent = ImageHelper.createCaptureIntent(Uri.fromFile(savedFile));
		startActivityForResult(intent, questCode);
	}
    
    public int getCurrentPictureRequest() {
    	return mCurrentPictureRequest;
    }
    
    @Override
   	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (resultCode == Activity.RESULT_OK) {
   			if (CurrentPictureGalleryRequest == requestCode) {
   				onPickFromGalleryFinish(data.getData());
   			} else if (CurrentPictureCameraRequest == requestCode) {
   				onPickFromCameraFinish();
   				
   			}
   		}
   	}
       
       @Override
   	public Dialog onCreateDialog(int id) {
   		switch(id) {
   		case DIALOG_PICTURE_CHOOSE_CONFIRM:
   			return new AlertDialog.Builder(this)
   			.setItems(this.getResources().getStringArray(R.array.picture_op_items), new DialogInterface.OnClickListener() {
   				
   				@Override
   				public void onClick(DialogInterface dialog, int which) {
   					switch(which) {
   					case 0: //Gallery
   						mCurrentPictureRequest = CurrentPictureGalleryRequest;
   						onPickFromGalleryStart();
   						break;
   					case 1: //Camera
   						mCurrentPictureRequest = CurrentPictureCameraRequest;
   						onPickFromCameraStart();
   						break;
   					}
   					
   				}
   			})
   			.setNegativeButton(android.R.string.cancel, null)
   			.create();
   		case DIALOG_MEDIA_UNMOUNTED:
   			return new AlertDialog.Builder(this)
   			.setMessage(R.string.dialog_msg_media_unmounted)
   			.setCancelable(false)
   			.setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {
   				
   				@Override
   				public void onClick(DialogInterface dialog, int which) {
   					onMediaUnmountedConfirmClick();
   					
   				}
   			})
   			.create();
   			 //add by chenkai, 20131201, add network check
   	      case DIALOG_DATA_NOT_CONNECTED:
   	    	  return ComConnectivityManager.getInstance().onCreateNoNetworkDialog(mContext);
   	      case DIALOG_PROGRESS:
   	    	  mProgressDialog = new ProgressDialog(this);
   	    	  mProgressDialog.setMessage(getString(R.string.msg_progressdialog_wait));
   	    	  mProgressDialog.setCancelable(false);
   	    	  return mProgressDialog;
   		}
   		return super.onCreateDialog(id);
   	}
       
       protected ProgressDialog getProgressDialog() {
    	   return mProgressDialog;
       }
       /**
        * 设置是否显示HomeUp
        * @param show
        */
     public void setShowHomeUp(boolean show) {
    	 initTitleBar();
    	 mHomeBtn.setVisibility(show?View.VISIBLE:View.INVISIBLE);
     }
     
     private View.OnClickListener mTitleActionOnClickListener = new View.OnClickListener() {
    	 @Override
    		public void onClick(View view) {
    			switch(view.getId()) {
    			case R.id.cncom__up:
    				Intent upIntent = NavUtils.getParentActivityIntent(mContext);
    	     	   if (upIntent == null) {
    	     		   // If we has configurated parent Activity in AndroidManifest.xml, we just finish current Activity.
    	     		   finish();
    	     		   return;
    	     	   }
    	            if (NavUtils.shouldUpRecreateTask(mContext, upIntent)) {
    	                // This activity is NOT part of this app's task, so create a new task
    	                // when navigating up, with a synthesized back stack.
    	                TaskStackBuilder.create(mContext)
    	                        // Add all of this activity's parents to the back stack
    	                        .addNextIntentWithParentStack(upIntent)
    	                        // Navigate up to the closest parent
    	                        .startActivities();
    	            } else {
    	                // This activity is part of this app's task, so simply
    	                // navigate up to the logical parent activity.
    	                NavUtils.navigateUpTo(mContext, upIntent);
    	            }
    				break;
    			default:
    				if (view.getTag() instanceof MenuItem) {
    					MenuItem item = (MenuItem) view.getTag();
    					if (item.hasSubMenu()) {
    						//显示子菜单
    						onSubMenuSelected(view, (SubMenuBuilder) item.getSubMenu());
    						
    					} else {
//    						((MenuWrapper)mMenu).performItemAction(item, 0);
    						onOptionsItemSelected(item);
    					}
    				}
    				
    			}
    	 }
     };

     public void invalidateOptionsMenu() {
    	 super.invalidateOptionsMenu();
    	 if (mMenu == null) {
    		 mMenu = new MenuBuilder(mContext);
    		 mMenu.setCallback(this);
    		 onCreateOptionsMenu(mMenu);
    	 }
    	 onPrepareOptionsMenu(mMenu);
    	 invalidateActionMenu();
     }

    protected MenuBuilder mMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	@Override
	 public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
		 return onOptionsItemSelected(item);
	 }
	@Override
	public void onMenuModeChange(MenuBuilder menu) {
		// TODO Auto-generated method stub
		
	}
	
	public Menu getActionMenu() {
		return mMenu;
	}
	
	private void invalidateActionMenu() {
		mActionMenuLayout.removeAllViews();
		int len = mMenu.size();
		MenuItem item = null;
		for(int index = 0; index < len; index++) {
			item = mMenu.getItem(index);
			if (item.isVisible()) {
				addItemView(item);
			}
			
		}
	}
	public void addItemView(MenuItem item) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.action_menu_item_layout, mActionMenuLayout, true);
		view.setTag(item);
		view.setOnClickListener(mTitleActionOnClickListener);
		if (item.getIcon() != null) {
			view.findViewById(R.id.imageButton).setVisibility(View.VISIBLE);
		} else {
			view.findViewById(R.id.imageButton).setVisibility(View.GONE);
		}
		if (TextUtils.isEmpty(item.getTitle())) {
			view.findViewById(R.id.textButton).setVisibility(View.GONE);
		} else {
			view.findViewById(R.id.textButton).setVisibility(View.VISIBLE);
		}
		view.setId(item.getItemId());
	}
	public boolean onSubMenuSelected(View view, SubMenuBuilder subMenu) {
        if (!subMenu.hasVisibleItems()) return false;
//        final int count = subMenu.size();
//        for (int i = 0; i < count; i++) {
//            MenuItem childItem = subMenu.getItem(i);
//            if (!childItem.isVisible()) {
//            	subMenu.removeItemAt(i);
//                break;
//            }
//        }

        mOpenSubMenuId = subMenu.getItem().getItemId();
        mActionButtonPopup = new ActionButtonSubmenu(mContext, subMenu);
        mActionButtonPopup.setAnchorView(view);
        mActionButtonPopup.show();
        return true;
    }

	private OverflowPopup mOverflowPopup;
    private ActionButtonSubmenu mActionButtonPopup;

    private OpenOverflowRunnable mPostedOpenRunnable;

    final PopupPresenterCallback mPopupPresenterCallback = new PopupPresenterCallback();
    int mOpenSubMenuId;
    private View mOverflowButton;
	
    private class OverflowPopup extends MenuPopupHelper {
        public OverflowPopup(Context context, MenuBuilder menu, View anchorView,
                boolean overflowOnly) {
            super(context, menu, anchorView, overflowOnly);
        }

        @Override
        public void onDismiss() {
            super.onDismiss();
            mMenu.close();
            mOverflowPopup = null;
        }
    }
	 private class OpenOverflowRunnable implements Runnable {
	        private OverflowPopup mPopup;

	        public OpenOverflowRunnable(OverflowPopup popup) {
	            mPopup = popup;
	        }

	        public void run() {
	            if (mActionMenuLayout != null && mActionMenuLayout.getWindowToken() != null && mPopup.tryShow()) {
	                mOverflowPopup = mPopup;
	            }
	            mPostedOpenRunnable = null;
	        }
	    }
	
	 private class ActionButtonSubmenu extends MenuPopupHelper {
	        //UNUSED private SubMenuBuilder mSubMenu;

	        public ActionButtonSubmenu(Context context, SubMenuBuilder subMenu) {
	            super(context, subMenu);
	            //UNUSED mSubMenu = subMenu;

	            MenuItemImpl item = (MenuItemImpl) subMenu.getItem();
	            if (!item.isActionButton()) {
	                // Give a reasonable anchor to nested submenus.
	                setAnchorView(mOverflowButton == null ? (View) mActionMenuLayout : mOverflowButton);
	            }
	            setCallback(mPopupPresenterCallback);
	        }

	        @Override
	        public void onDismiss() {
	            super.onDismiss();
	            mActionButtonPopup = null;
	            mOpenSubMenuId = 0;
	        }
	    }
     
	 private class PopupPresenterCallback implements MenuPresenter.Callback {

	        @Override
	        public boolean onOpenSubMenu(MenuBuilder subMenu) {
	            if (subMenu == null) return false;

	            mOpenSubMenuId = ((SubMenuBuilder) subMenu).getItem().getItemId();
	            return false;
	        }

	        @Override
	        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
	            if (menu instanceof SubMenuBuilder) {
	                ((SubMenuBuilder) menu).getRootMenu().close();
	            }
	        }
	    }
     
       
}