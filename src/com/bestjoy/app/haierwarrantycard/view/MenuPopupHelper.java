package com.bestjoy.app.haierwarrantycard.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actionbarsherlock.internal.view.View_HasStateListenerSupport;
import com.actionbarsherlock.internal.view.View_OnAttachStateChangeListener;
import com.actionbarsherlock.internal.view.menu.MenuBuilder;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.internal.view.menu.MenuPresenter;
import com.actionbarsherlock.internal.view.menu.MenuView;
import com.actionbarsherlock.internal.view.menu.SubMenuBuilder;
import com.actionbarsherlock.internal.widget.IcsListPopupWindow;
import com.actionbarsherlock.view.MenuItem;
import com.bestjoy.app.haierwarrantycard.R;

public class MenuPopupHelper implements AdapterView.OnItemClickListener, 
View.OnKeyListener, ViewTreeObserver.OnGlobalLayoutListener, PopupWindow.OnDismissListener, View_OnAttachStateChangeListener, MenuPresenter{
	static final int ITEM_LAYOUT = R.layout.list_menu_item_view;
	private Context mContext;
    private LayoutInflater mInflater;
    private IcsListPopupWindow mPopup;
    private MenuBuilder mMenu;
    private int mPopupMaxWidth;
    private View mAnchorView;
    private boolean mOverflowOnly;
    private MenuAdapter mAdapter;
    private ViewTreeObserver mTreeObserver;

    private Callback mPresenterCallback;

    boolean mForceShowIcon;

    private ViewGroup mMeasureParent;
    
    public MenuPopupHelper(Context context, MenuBuilder menu) {
        this(context, menu, null, false);
    }

    public MenuPopupHelper(Context context, MenuBuilder menu, View anchorView) {
        this(context, menu, anchorView, false);
    }
    public MenuPopupHelper(Context context, MenuBuilder menu, View anchorView, boolean overflowOnly) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMenu = menu;
        mOverflowOnly = overflowOnly;

        final Resources res = context.getResources();
        mPopupMaxWidth = Math.max(res.getDisplayMetrics().widthPixels / 2,
                res.getDimensionPixelSize(R.dimen.abs__config_prefDialogWidth));

        mAnchorView = anchorView;
        menu.addMenuPresenter(this);
    }
    
    public void setAnchorView(View anchor) {
        mAnchorView = anchor;
    }

    public void setForceShowIcon(boolean forceShow) {
        mForceShowIcon = forceShow;
    }

    public void show() {
        if (!tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }
    public boolean tryShow() {
        mPopup = new IcsListPopupWindow(mContext, null, R.attr.popupMenuStyle, com.actionbarsherlock.R.style.Widget_Sherlock_PopupMenu);
        mPopup.setOnDismissListener(this);
        mPopup.setOnItemClickListener(this);

        mAdapter = new MenuAdapter(mMenu);
        mPopup.setAdapter(mAdapter);
        mPopup.setModal(true);

        View anchor = mAnchorView;
        if (anchor != null) {
            // Don't attach to the VTO unless the anchor itself is attached to avoid VTO-related leaks.
            if (anchor.getWindowToken() != null) {
                ViewTreeObserver vto = anchor.getViewTreeObserver();
                if (vto != mTreeObserver) {
                    if (mTreeObserver != null && mTreeObserver.isAlive()) {
                        mTreeObserver.removeGlobalOnLayoutListener(this);
                    }
                    if ((mTreeObserver = vto) != null) {
                        vto.addOnGlobalLayoutListener(this);
                    }
                }
            } else if (anchor instanceof View_HasStateListenerSupport) {
                ((View_HasStateListenerSupport) anchor).addOnAttachStateChangeListener(this);
            }
            mPopup.setAnchorView(anchor);
        } else {
            return false;
        }

        mPopup.setContentWidth(Math.min(measureContentWidth(mAdapter), mPopupMaxWidth));
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.show();
        mPopup.getListView().setOnKeyListener(this);
        return true;
    }

    public void dismiss() {
        if (isShowing()) {
            mPopup.dismiss();
        }
    }

    public void onDismiss() {
        mPopup = null;
        mMenu.close();
        if (mTreeObserver != null) {
            if (mTreeObserver.isAlive()) mTreeObserver.removeGlobalOnLayoutListener(this);
            mTreeObserver = null;
        } else if (mAnchorView instanceof View_HasStateListenerSupport) {
            ((View_HasStateListenerSupport) mAnchorView).removeOnAttachStateChangeListener(this);
        }
    }

    public boolean isShowing() {
        return mPopup != null && mPopup.isShowing();
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuAdapter adapter = mAdapter;
        adapter.mAdapterMenu.performItemAction(adapter.getItem(position), 0);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU) {
            dismiss();
            return true;
        }
        return false;
    }
    
    private int measureContentWidth(ListAdapter adapter) {
        // Menus don't tend to be long, so this is more sane than it looks.
        int width = 0;
        View itemView = null;
        int itemType = 0;
        final int widthMeasureSpec =
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec =
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }
            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(mContext);
            }
            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);
            width = Math.max(width, itemView.getMeasuredWidth());
        }
        return width;
    }

    @Override
    public void onGlobalLayout() {
        if (isShowing()) {
            final View anchor = mAnchorView;
            if (anchor == null || !anchor.isShown()) {
                dismiss();
            } else if (isShowing()) {
                // Recompute window size and position
                mPopup.show();
            }
        }
    }
    
    @Override
    public void onViewAttachedToWindow(View v) {
        ((View_HasStateListenerSupport) v).removeOnAttachStateChangeListener(this);

        // The anchor wasn't attached in tryShow(), attach to the ViewRoot VTO now.
        if (mPopup != null && mTreeObserver == null) {
            (mTreeObserver = v.getViewTreeObserver()).addOnGlobalLayoutListener(this);
        }
    }

	@Override
	public void onViewDetachedFromWindow(View v) {
		
	}
	
    public void updateMenuView(boolean cleared) {
        if (mAdapter != null) mAdapter.notifyDataSetChanged();
    }
    
    private class MenuAdapter extends BaseAdapter {
        private MenuBuilder mAdapterMenu;

        public MenuAdapter(MenuBuilder menu) {
            mAdapterMenu = menu;
        }

        public int getCount() {
            return mAdapterMenu != null ? mAdapterMenu.size() : 0;
        }

        public MenuItem getItem(int position) {
            return mAdapterMenu.getItem(position);
        }

        public long getItemId(int position) {
            // Since a menu item's ID is optional, we'll use the position as an
            // ID for the item in the AdapterView
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(ITEM_LAYOUT, parent, false);
                viewHolder = new ViewHolder();
                viewHolder._icon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder._title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(viewHolder);
            } else {
            	viewHolder = (ViewHolder) convertView.getTag();
            }
            MenuItem menuItem = getItem(position);
            if (menuItem.getIcon() != null) {
            	viewHolder._icon.setVisibility(View.VISIBLE);
            	viewHolder._icon.setImageDrawable(menuItem.getIcon());
            } else {
            	viewHolder._icon.setVisibility(View.GONE);
            	viewHolder._icon.setImageDrawable(null);
            }
            viewHolder._title.setText(menuItem.getTitle());
            return convertView;
        }

    }
    
    private class ViewHolder {
    	private ImageView _icon;
    	private TextView _title;
    }

	@Override
	public void initForMenu(Context context, MenuBuilder menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MenuView getMenuView(ViewGroup root) {
		throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
	}

	@Override
	public void setCallback(Callback cb) {
		 mPresenterCallback = cb;
		
	}

	@Override
	public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
		if (subMenu.hasVisibleItems()) {
            MenuPopupHelper subPopup = new MenuPopupHelper(mContext, subMenu, mAnchorView, false);
            subPopup.setCallback(mPresenterCallback);

            boolean preserveIconSpacing = false;
            final int count = subMenu.size();
            for (int i = 0; i < count; i++) {
                MenuItem childItem = subMenu.getItem(i);
                if (childItem.isVisible() && childItem.getIcon() != null) {
                    preserveIconSpacing = true;
                    break;
                }
            }
            subPopup.setForceShowIcon(preserveIconSpacing);

            if (subPopup.tryShow()) {
                if (mPresenterCallback != null) {
                    mPresenterCallback.onOpenSubMenu(subMenu);
                }
                return true;
            }
        }
        return false;
	}

	@Override
	public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
		if (menu != mMenu) return;

        dismiss();
        if (mPresenterCallback != null) {
            mPresenterCallback.onCloseMenu(menu, allMenusAreClosing);
        }
		
	}

	@Override
	public boolean flagActionItems() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean collapseItemActionView(MenuBuilder menu, MenuItemImpl item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public Parcelable onSaveInstanceState() {
		return null;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		
	}

}
