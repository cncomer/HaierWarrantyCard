package com.bestjoy.app.haierwarrantycard.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bestjoy.app.haierwarrantycard.R;

public class AddressCompleteEditText extends EditText implements Filter.FilterListener {


	static final boolean DEBUG = false;
    static final String TAG = "AutoCompleteTextView";

    static final int EXPAND_MAX = 3;

    private CharSequence mHintText;
    private TextView mHintView;
    private int mHintResource;

    private ListAdapter mAdapter;
    private Filter mFilter;
    private int mThreshold;

    private PopupWindow mPopup;
    private int mDropDownAnchorId;

    private AdapterView.OnItemClickListener mItemClickListener;
    private AdapterView.OnItemSelectedListener mItemSelectedListener;

    private boolean mDropDownDismissedOnCompletion = true;

    private int mLastKeyCode = KeyEvent.KEYCODE_UNKNOWN;
    private boolean mOpenBefore;

    private Validator mValidator = null;

    // Set to true when text is set directly and no filtering shall be performed
    private boolean mBlockCompletion;

    // When set, an update in the underlying adapter will update the result list popup.
    // Set to false when the list is hidden to prevent asynchronous updates to popup the list again.
    private boolean mPopupCanBeUpdated = true;

    private PassThroughClickListener mPassThroughClickListener;
    private PopupDataSetObserver mObserver;
    
    private Context mContext;
	private GridView gridView;
//	private int screenWidth;
//	private int screenHeight;
	
	private boolean mIsCanFilter = true;
	
	private View popWindowView;

    public AddressCompleteEditText(Context context) {
        this(context, null);
    }

    public AddressCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

//        final Display display = ((Activity) mContext).getWindow().getWindowManager().getDefaultDisplay();
//		if (display != null) {
//			// 获取屏幕大小
//			screenWidth = display.getWidth();
//			screenHeight = display.getHeight();
//		}
		
		mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.AddressCompleteEditText); 
        int popViewId = a.getResourceId(R.styleable.AddressCompleteEditText_popView, R.layout.layout_popupwindow); 
        mThreshold = a.getInt(R.styleable.AddressCompleteEditText_completionThreshold, 0);
        a.recycle();
        
        popWindowView = LayoutInflater.from(mContext).inflate(popViewId, null);
		gridView = (GridView) popWindowView.findViewById(R.id.gridview);

//		int size = screenWidth > screenHeight ? screenWidth : screenHeight;
		
		mPopup = new PopupWindow(popWindowView, LayoutParams.MATCH_PARENT, 300, false);
		mPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mPopup.setAnimationStyle(R.style.AnimationPreview);  
		mPopup.setTouchable(true);
		mPopup.setOutsideTouchable(true);
		mPopup.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
		gridView.setOnItemClickListener(new DropDownItemClickListener());

        // Always turn on the auto complete input type flag, since it
        // makes no sense to use this widget without it.
        int inputType = getInputType();
        if ((inputType&EditorInfo.TYPE_MASK_CLASS)
                == EditorInfo.TYPE_CLASS_TEXT) {
            inputType |= EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE;
            setRawInputType(inputType);
        }

        a.recycle();

        setFocusable(true);

        addTextChangedListener(new MyWatcher());
        
        mPassThroughClickListener = new PassThroughClickListener();
        super.setOnClickListener(mPassThroughClickListener);
    }
    
    public PopupWindow getPopupWindow() {
    	return mPopup;
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mPassThroughClickListener.mWrapped = listener;
    }

    /**
     * Private hook into the on click event, dispatched from {@link PassThroughClickListener}
     */
    private void onClickImpl() {
        // If the dropdown is showing, bring the keyboard to the front
        // when the user touches the text field.
        if (isPopupShowing()) {
            ensureImeVisible(true);
        }
    }

    
    /**
     * <p>Returns the current width for the auto-complete drop down list. This can
     * be a fixed width, or {@link ViewGroup.LayoutParams#MATCH_PARENT} to fill the screen, or
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT} to fit the width of its anchor view.</p>
     * 
     * @return the width for the drop down list
     * 
     * @attr ref android.R.styleable#AutoCompleteTextView_dropDownWidth
     */
    public int getDropDownWidth() {
        return mPopup.getWidth();
    }
    
    /**
     * <p>Sets the current width for the auto-complete drop down list. This can
     * be a fixed width, or {@link ViewGroup.LayoutParams#MATCH_PARENT} to fill the screen, or
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT} to fit the width of its anchor view.</p>
     * 
     * @param width the width to use
     * 
     * @attr ref android.R.styleable#AutoCompleteTextView_dropDownWidth
     */
    public void setDropDownWidth(int width) {
        mPopup.setWidth(width);
    }

    /**
     * <p>Returns the current height for the auto-complete drop down list. This can
     * be a fixed height, or {@link ViewGroup.LayoutParams#MATCH_PARENT} to fill
     * the screen, or {@link ViewGroup.LayoutParams#WRAP_CONTENT} to fit the height
     * of the drop down's content.</p>
     *
     * @return the height for the drop down list
     *
     * @attr ref android.R.styleable#AutoCompleteTextView_dropDownHeight
     */
    public int getDropDownHeight() {
        return mPopup.getHeight();
    }

    /**
     * <p>Sets the current height for the auto-complete drop down list. This can
     * be a fixed height, or {@link ViewGroup.LayoutParams#MATCH_PARENT} to fill
     * the screen, or {@link ViewGroup.LayoutParams#WRAP_CONTENT} to fit the height
     * of the drop down's content.</p>
     *
     * @param height the height to use
     *
     * @attr ref android.R.styleable#AutoCompleteTextView_dropDownHeight
     */
    public void setDropDownHeight(int height) {
        mPopup.setHeight(height);
    }
    
    /**
     * <p>Returns the id for the view that the auto-complete drop down list is anchored to.</p>
     *  
     * @return the view's id, or {@link View#NO_ID} if none specified
     * 
     * @attr ref android.R.styleable#AutoCompleteTextView_dropDownAnchor
     */
    public int getDropDownAnchor() {
        return mDropDownAnchorId;
    }
    
    /**
     * <p>Gets the background of the auto-complete drop-down list.</p>
     * 
     * @return the background drawable
     * 
     * @attr ref android.R.styleable#PopupWindow_popupBackground
     */
    public Drawable getDropDownBackground() {
        return mPopup.getBackground();
    }
    
    /**
     * <p>Sets the background of the auto-complete drop-down list.</p>
     * 
     * @param d the drawable to set as the background
     * 
     * @attr ref android.R.styleable#PopupWindow_popupBackground
     */
    public void setDropDownBackgroundDrawable(Drawable d) {
        mPopup.setBackgroundDrawable(d);
    }
    
    /**
     * <p>Sets the background of the auto-complete drop-down list.</p>
     * 
     * @param id the id of the drawable to set as the background
     * 
     * @attr ref android.R.styleable#PopupWindow_popupBackground
     */
    public void setDropDownBackgroundResource(int id) {
        mPopup.setBackgroundDrawable(getResources().getDrawable(id));
    }
    
    /**
     * <p>Sets the vertical offset used for the auto-complete drop-down list.</p>
     * 
     * @param offset the vertical offset
     */
    public void setDropDownVerticalOffset(int offset) {
    }
    
    
    /**
     * <p>Sets the horizontal offset used for the auto-complete drop-down list.</p>
     * 
     * @param offset the horizontal offset
     */
    public void setDropDownHorizontalOffset(int offset) {
    }

     /**
     * <p>Sets the animation style of the auto-complete drop-down list.</p>
     *
     * <p>If the drop-down is showing, calling this method will take effect only
     * the next time the drop-down is shown.</p>
     *
     * @param animationStyle animation style to use when the drop-down appears
     *      and disappears.  Set to -1 for the default animation, 0 for no
     *      animation, or a resource identifier for an explicit animation.
     *
     * @hide Pending API council approval
     */
    public void setDropDownAnimationStyle(int animationStyle) {
        mPopup.setAnimationStyle(animationStyle);
    }

    /**
     * <p>Returns the animation style that is used when the drop-down list appears and disappears
     * </p>
     *
     * @return the animation style that is used when the drop-down list appears and disappears
     *
     * @hide Pending API council approval
     */
    public int getDropDownAnimationStyle() {
        return mPopup.getAnimationStyle();
    }

   
    /**
     * Checks whether the drop-down is dismissed when a suggestion is clicked.
     * 
     * @hide Pending API council approval
     */
    public boolean isDropDownDismissedOnCompletion() {
        return mDropDownDismissedOnCompletion;
    }

    /**
     * Sets whether the drop-down is dismissed when a suggestion is clicked. This is 
     * true by default.
     * 
     * @param dropDownDismissedOnCompletion Whether to dismiss the drop-down.
     * 
     * @hide Pending API council approval
     */
    public void setDropDownDismissedOnCompletion(boolean dropDownDismissedOnCompletion) {
        mDropDownDismissedOnCompletion = dropDownDismissedOnCompletion;
    }
 
    /**
     * <p>Returns the number of characters the user must type before the drop
     * down list is shown.</p>
     *
     * @return the minimum number of characters to type to show the drop down
     *
     * @see #setThreshold(int)
     */
    public int getThreshold() {
        return mThreshold;
    }

    /**
     * <p>Specifies the minimum number of characters the user has to type in the
     * edit box before the drop down list is shown.</p>
     *
     * <p>When <code>threshold</code> is less than or equals 0, a threshold of
     * 1 is applied.</p>
     *
     * @param threshold the number of characters to type before the drop down
     *                  is shown
     *
     * @see #getThreshold()
     *
     * @attr ref android.R.styleable#AutoCompleteTextView_completionThreshold
     */
    public void setThreshold(int threshold) {
        if (threshold <= 0) {
            threshold = 1;
        }

        mThreshold = threshold;
    }

    /**
     * <p>Sets the listener that will be notified when the user clicks an item
     * in the drop down list.</p>
     *
     * @param l the item click listener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        mItemClickListener = l;
    }

    /**
     * <p>Sets the listener that will be notified when the user selects an item
     * in the drop down list.</p>
     *
     * @param l the item selected listener
     */
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener l) {
        mItemSelectedListener = l;
    }

    /**
     * <p>Returns the listener that is notified whenever the user clicks an item
     * in the drop down list.</p>
     *
     * @return the item click listener
     *
     * @deprecated Use {@link #getOnItemClickListener()} intead
     */
    @Deprecated
    public AdapterView.OnItemClickListener getItemClickListener() {
        return mItemClickListener;
    }

    /**
     * <p>Returns the listener that is notified whenever the user selects an
     * item in the drop down list.</p>
     *
     * @return the item selected listener
     *
     * @deprecated Use {@link #getOnItemSelectedListener()} intead
     */
    @Deprecated
    public AdapterView.OnItemSelectedListener getItemSelectedListener() {
        return mItemSelectedListener;
    }

    /**
     * <p>Returns the listener that is notified whenever the user clicks an item
     * in the drop down list.</p>
     *
     * @return the item click listener
     */
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return mItemClickListener;
    }

    /**
     * <p>Returns the listener that is notified whenever the user selects an
     * item in the drop down list.</p>
     *
     * @return the item selected listener
     */
    public AdapterView.OnItemSelectedListener getOnItemSelectedListener() {
        return mItemSelectedListener;
    }

    /**
     * <p>Returns a filterable list adapter used for auto completion.</p>
     *
     * @return a data adapter used for auto completion
     */
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * <p>Changes the list of data used for auto completion. The provided list
     * must be a filterable list adapter.</p>
     * 
     * <p>The caller is still responsible for managing any resources used by the adapter.
     * Notably, when the AutoCompleteTextView is closed or released, the adapter is not notified.
     * A common case is the use of {@link android.widget.CursorAdapter}, which
     * contains a {@link android.database.Cursor} that must be closed.  This can be done
     * automatically (see 
     * {@link android.app.Activity#startManagingCursor(android.database.Cursor) 
     * startManagingCursor()}),
     * or by manually closing the cursor when the AutoCompleteTextView is dismissed.</p>
     *
     * @param adapter the adapter holding the auto completion data
     *
     * @see #getAdapter()
     * @see android.widget.Filterable
     * @see android.widget.ListAdapter
     */
    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        if (mObserver == null) {
            mObserver = new PopupDataSetObserver();
        } else if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
        }
        mAdapter = adapter;
        if (mAdapter != null) {
            //noinspection unchecked
            mFilter = ((Filterable) mAdapter).getFilter();
            adapter.registerDataSetObserver(mObserver);
        } else {
            mFilter = null;
        }

        gridView.setAdapter(mAdapter);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            // special case for the back key, we do not even try to send it
            // to the drop down list but instead, consume it immediately
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.startTracking(event, this);
                }
                return true;
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    state.handleUpEvent(event);
                }
                if (event.isTracking() && !event.isCanceled()) {
                    dismissDropDown();
                    return true;
                }
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * Returns <code>true</code> if the amount of text in the field meets
     * or exceeds the {@link #getThreshold} requirement.  You can override
     * this to impose a different standard for when filtering will be
     * triggered.
     */
    public boolean enoughToFilter() {
        if (DEBUG) Log.v(TAG, "Enough to filter: len=" + getText().length()
                + " threshold=" + mThreshold);
        return getText().length() >= mThreshold;
    }

    /**
     * This is used to watch for edits to the text view.  Note that we call
     * to methods on the auto complete text view class so that we can access
     * private vars without going through thunks.
     */
    private class MyWatcher implements TextWatcher {
        public void afterTextChanged(Editable s) {
            if (mIsCanFilter) doAfterTextChanged();
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        	if (mIsCanFilter) doBeforeTextChanged();
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    void doBeforeTextChanged() {
        if (mBlockCompletion) return;

        // when text is changed, inserted or deleted, we attempt to show
        // the drop down
        mOpenBefore = isPopupShowing();
        if (DEBUG) Log.v(TAG, "before text changed: open=" + mOpenBefore);
    }

    void doAfterTextChanged() {
        if (mBlockCompletion) return;

        // if the list was open before the keystroke, but closed afterwards,
        // then something in the keystroke processing (an input filter perhaps)
        // called performCompletion() and we shouldn't do any more processing.
        if (DEBUG) Log.v(TAG, "after text changed: openBefore=" + mOpenBefore
                + " open=" + isPopupShowing());
        if (mOpenBefore && !isPopupShowing()) {
            return;
        }

        // the drop down is shown only when a minimum number of characters
        // was typed in the text view
        if (enoughToFilter()) {
            if (mFilter != null) {
                mPopupCanBeUpdated = true;
                performFiltering(getText(), mLastKeyCode);
            }
        } else {
            // drop down is automatically dismissed when enough characters
            // are deleted from the text view
        	dismissDropDown();
            if (mFilter != null) {
                mFilter.filter(null);
            }
        }
    }

    /**
     * <p>Indicates whether the popup menu is showing.</p>
     *
     * @return true if the popup menu is showing, false otherwise
     */
    public boolean isPopupShowing() {
        return mPopup.isShowing();
    }

    /**
     * <p>Converts the selected item from the drop down list into a sequence
     * of character that can be used in the edit box.</p>
     *
     * @param selectedItem the item selected by the user for completion
     *
     * @return a sequence of characters representing the selected suggestion
     */
    protected CharSequence convertSelectionToString(Object selectedItem) {
        return mFilter.convertResultToString(selectedItem);
    }
    
    /**
     * <p>Clear the list selection.  This may only be temporary, as user input will often bring 
     * it back.
     */
    public void clearListSelection() {
    	gridView.setSelection(-1);
    }
    
    /**
     * Set the position of the dropdown view selection.
     * 
     * @param position The position to move the selector to.
     */
    public void setListSelection(int position) {
    	gridView.setSelection(position);
    }

    /**
     * Get the position of the dropdown view selection, if there is one.  Returns 
     * {@link ListView#INVALID_POSITION ListView.INVALID_POSITION} if there is no dropdown or if
     * there is no selection.
     * 
     * @return the position of the current selection, if there is one, or 
     * {@link ListView#INVALID_POSITION ListView.INVALID_POSITION} if not.
     * 
     * @see ListView#getSelectedItemPosition()
     */
    public int getListSelection() {
        return gridView.getSelectedItemPosition();
    }

    /**
     * <p>Starts filtering the content of the drop down list. The filtering
     * pattern is the content of the edit box. Subclasses should override this
     * method to filter with a different pattern, for instance a substring of
     * <code>text</code>.</p>
     *
     * @param text the filtering pattern
     * @param keyCode the last character inserted in the edit box; beware that
     * this will be null when text is being added through a soft input method.
     */
    @SuppressWarnings({ "UnusedDeclaration" })
    protected void performFiltering(CharSequence text, int keyCode) {
        mFilter.filter(text, this);
    }

    
    /**
     * Identifies whether the view is currently performing a text completion, so subclasses
     * can decide whether to respond to text changed events.
     */
    public boolean isPerformingCompletion() {
        return mBlockCompletion;
    }

    /**
     * Like {@link #setText(CharSequence)}, except that it can disable filtering.
     *
     * @param filter If <code>false</code>, no filtering will be performed
     *        as a result of this call.
     * 
     * @hide Pending API council approval.
     */
    public void setText(CharSequence text, boolean filter) {
        if (filter) {
            setText(text);
        } else {
            mBlockCompletion = true;
            setText(text);
            mBlockCompletion = false;
        }
    }

    /**
     * <p>Performs the text completion by replacing the current text by the
     * selected item. Subclasses should override this method to avoid replacing
     * the whole content of the edit box.</p>
     *
     * @param text the selected suggestion in the drop down list
     */
    protected void replaceText(CharSequence text) {
        clearComposingText();

        setText(text);
        // make sure we keep the caret at the end of the text view
        Editable spannable = getText();
        Selection.setSelection(spannable, spannable.length());
    }


    private void updateDropDownForFilter(int count) {
        // Not attached to window, don't update drop-down
        if (getWindowVisibility() == View.GONE) return;

        /*
         * This checks enoughToFilter() again because filtering requests
         * are asynchronous, so the result may come back after enough text
         * has since been deleted to make it no longer appropriate
         * to filter.
         */

        final boolean enoughToFilter = enoughToFilter();
        if (count > 0 && enoughToFilter) {
            if (hasFocus() && hasWindowFocus() && mPopupCanBeUpdated) {
                showDropDown();
            }
        } else if (isPopupShowing()) {
            dismissDropDown();
            // When the filter text is changed, the first update from the adapter may show an empty
            // count (when the query is being performed on the network). Future updates when some
            // content has been retrieved should still be able to update the list.
            mPopupCanBeUpdated = true;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            dismissDropDown();
        }
    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        // Perform validation if the view is losing focus.
        if (!focused) {
            performValidation();
            dismissDropDown();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        dismissDropDown();
        super.onDetachedFromWindow();
    }

    /**
     * <p>Closes the drop down if present on screen.</p>
     */
    public void dismissDropDown() {
        mPopup.dismiss();
        mPopupCanBeUpdated = false;
    }

    @Override
    protected boolean setFrame(final int l, int t, final int r, int b) {
        boolean result = super.setFrame(l, t, r, b);

        if (isPopupShowing()) {
            showDropDown();
        }

        return result;
    }
    
    /**
     * Ensures that the drop down is not obscuring the IME.
     * @param visible whether the ime should be in front. If false, the ime is pushed to
     * the background.
     * @hide internal used only here and SearchDialog
     */
    public void ensureImeVisible(boolean visible) {
        mPopup.setInputMethodMode(visible
                ? ListPopupWindow.INPUT_METHOD_NEEDED : ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
        showDropDown();
    }

    /**
     * @hide internal used only here and SearchDialog
     */
    public boolean isInputMethodNotNeeded() {
        return mPopup.getInputMethodMode() == ListPopupWindow.INPUT_METHOD_NOT_NEEDED;
    }

    /**
     * <p>Displays the drop down on screen.</p>
     */
    public void showDropDown() {
        if (!isPopupShowing()) {
            // Make sure the list does not obscure the IME when shown for the first time.
            mPopup.setInputMethodMode(ListPopupWindow.INPUT_METHOD_NEEDED);
        }
        mPopup.showAsDropDown(this, 10, 10);
    }


    /**
     * Sets the validator used to perform text validation.
     *
     * @param validator The validator used to validate the text entered in this widget.
     *
     * @see #getValidator()
     * @see #performValidation()
     */
    public void setValidator(Validator validator) {
        mValidator = validator;
    }

    /**
     * Returns the Validator set with {@link #setValidator},
     * or <code>null</code> if it was not set.
     *
     * @see #setValidator(android.widget.AutoCompleteTextView.Validator)
     * @see #performValidation()
     */
    public Validator getValidator() {
        return mValidator;
    }

    /**
     * If a validator was set on this view and the current string is not valid,
     * ask the validator to fix it.
     *
     * @see #getValidator()
     * @see #setValidator(android.widget.AutoCompleteTextView.Validator)
     */
    public void performValidation() {
        if (mValidator == null) return;

        CharSequence text = getText();

        if (!TextUtils.isEmpty(text) && !mValidator.isValid(text)) {
            setText(mValidator.fixText(text));
        }
    }

    /**
     * Returns the Filter obtained from {@link Filterable#getFilter},
     * or <code>null</code> if {@link #setAdapter} was not called with
     * a Filterable.
     */
    protected Filter getFilter() {
        return mFilter;
    }

    private class DropDownItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
        	if (isPopupShowing()) {
                Object selectedItem = mAdapter.getItem(position);
                if (selectedItem == null) {
                    Log.w(TAG, "performCompletion: no selected item");
                    return;
                }
            	 mBlockCompletion = true;
                 replaceText(convertSelectionToString(selectedItem));
                 mBlockCompletion = false;
            	if (mItemClickListener != null) {
                    if (v == null || position < 0) {
                        v = parent.getSelectedView();
                        position = parent.getSelectedItemPosition();
                        id = parent.getSelectedItemId();
                    }
                    mItemClickListener.onItemClick(parent, v, position, id);
                }
            	
        	}
        	dismissDropDown();

        }
    }

    /**
     * This interface is used to make sure that the text entered in this TextView complies to
     * a certain format.  Since there is no foolproof way to prevent the user from leaving
     * this View with an incorrect value in it, all we can do is try to fix it ourselves
     * when this happens.
     */
    public interface Validator {
        /**
         * Validates the specified text.
         *
         * @return true If the text currently in the text editor is valid.
         *
         * @see #fixText(CharSequence)
         */
        boolean isValid(CharSequence text);

        /**
         * Corrects the specified text to make it valid.
         *
         * @param invalidText A string that doesn't pass validation: isValid(invalidText)
         *        returns false
         *
         * @return A string based on invalidText such as invoking isValid() on it returns true.
         *
         * @see #isValid(CharSequence)
         */
        CharSequence fixText(CharSequence invalidText);
    }
    
    /**
     * Allows us a private hook into the on click event without preventing users from setting
     * their own click listener.
     */
    private class PassThroughClickListener implements OnClickListener {

        private View.OnClickListener mWrapped;

        /** {@inheritDoc} */
        public void onClick(View v) {
            onClickImpl();

            if (mWrapped != null) mWrapped.onClick(v);
        }
    }

    private class PopupDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            if (mAdapter != null) {
                // If the popup is not showing already, showing it will cause
                // the list of data set observers attached to the adapter to
                // change. We can't do it from here, because we are in the middle
                // of iterating through the list of observers.
                post(new Runnable() {
                    public void run() {
                        final ListAdapter adapter = mAdapter;
                        if (adapter != null) {
                            // This will re-layout, thus resetting mDataChanged, so that the
                            // listView click listener stays responsive
                            updateDropDownForFilter(adapter.getCount());
                        }
                    }
                });
            }
        }
    }

    /** {@inheritDoc} */
    public void onFilterComplete(int count) {
        updateDropDownForFilter(count);
    }
    
    public void setCanFilte(boolean canFilter) {
    	mIsCanFilter = canFilter;
    }

}
