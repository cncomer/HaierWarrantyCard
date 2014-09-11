package com.poqop.document;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.view.Window;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.ui.BaseActionbarActivity;
import com.poqop.document.events.CurrentPageListener;
import com.poqop.document.events.DecodingProgressListener;
import com.poqop.document.models.CurrentPageModel;
import com.poqop.document.models.DecodingProgressModel;
import com.poqop.document.models.ZoomModel;
import com.poqop.document.views.PageViewZoomControls;

public abstract class BaseViewerActivity extends BaseActionbarActivity implements DecodingProgressListener, CurrentPageListener
{
    private static final int MENU_EXIT = 0;
    private static final int MENU_GOTO = 1;
    private static final int MENU_FULL_SCREEN = 2;
    private static final int DIALOG_GOTO = 0;
    private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";
    private DecodeService decodeService;
    private DocumentView documentView;
    private ViewerPreferences viewerPreferences;
    private Toast pageNumberToast;
    private CurrentPageModel currentPageModel;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        viewerPreferences = new ViewerPreferences(this);
        final SharedPreferences sharedPreferences = getSharedPreferences(DOCUMENT_VIEW_STATE_PREFERENCES, 0);
        super.onCreate(savedInstanceState);
        initDecodeService();
        final ZoomModel zoomModel = new ZoomModel();
        final DecodingProgressModel progressModel = new DecodingProgressModel();
        progressModel.addEventListener(this);
        currentPageModel = new CurrentPageModel();
        currentPageModel.addEventListener(this);
        documentView = new DocumentView(this, zoomModel, progressModel, currentPageModel);
        zoomModel.addEventListener(documentView);
        documentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        decodeService.setContentResolver(getContentResolver());
        decodeService.setContainerView(documentView);
        documentView.setDecodeService(decodeService);
        decodeService.open(getIntent().getData());

        final FrameLayout frameLayout = createMainContainer();
        frameLayout.addView(documentView);
        frameLayout.addView(createZoomControls(zoomModel));
       
        setContentView(frameLayout);
        documentView.goToPage(sharedPreferences.getInt(getIntent().getData().toString(), 0));
        documentView.showDocument();

        viewerPreferences.addRecent(getIntent().getData());
        
        setFullScreen();
    }

    public void decodingProgressChanged(final int currentlyDecoding)
    {
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                try {
                	setSupportProgress(currentlyDecoding == 0 ? 10000 : currentlyDecoding);
//                	getSherlock().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS, currentlyDecoding == 0 ? 10000 : currentlyDecoding);
                } catch (final Throwable e) {
                }
            }
        });
    }

    public void currentPageChanged(int pageIndex)
    {
        final String pageText = (pageIndex + 1) + "/" + decodeService.getPageCount();
        if (pageNumberToast != null)
        {
            pageNumberToast.setText(pageText);
        }
        else
        {
            pageNumberToast = Toast.makeText(this, pageText, 300);
        }
        pageNumberToast.setGravity(Gravity.TOP | Gravity.LEFT,0,0);
        pageNumberToast.show();
        saveCurrentPage();
    }

    private void setWindowTitle()
    {
        final String name = getIntent().getData().getLastPathSegment();
        getWindow().setTitle(name);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        setWindowTitle();
    }

    private void setFullScreen()
    {
        if (viewerPreferences.isFullScreen()) {
        	getSupportActionBar().hide();
        }  else {
        	getSupportActionBar().show();
        }
    }

    private PageViewZoomControls createZoomControls(ZoomModel zoomModel)
    {
        final PageViewZoomControls controls = new PageViewZoomControls(this, zoomModel);
        controls.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        zoomModel.addEventListener(controls);
        return controls;
    }

    private FrameLayout createMainContainer()
    {
        return new FrameLayout(this);
    }

    private void initDecodeService()
    {
        if (decodeService == null)
        {
            decodeService = createDecodeService();
        }
    }

    protected abstract DecodeService createDecodeService();

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        decodeService.recycle();
        decodeService = null;
        super.onDestroy();
    }

    private void saveCurrentPage()
    {
        final SharedPreferences sharedPreferences = getSharedPreferences(DOCUMENT_VIEW_STATE_PREFERENCES, 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getIntent().getData().toString(), documentView.getCurrentPage());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
    	SubMenu subMenu1 = menu.addSubMenu(1000, R.string.menu_more, 1000, R.string.menu_more);
    	getSupportMenuInflater().inflate(R.menu.pdfviewer_activity_actionbarmenu, subMenu1);
    	MenuItem subMenu1Item = subMenu1.getItem();
    	 subMenu1Item.setIcon(R.drawable.abs__ic_menu_moreoverflow_normal_holo_dark);
         subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	getSupportMenuInflater().inflate(R.menu.pdfviewer_activity_menu, menu);
        return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	MenuItem menuItem = menu.findItem(R.string.menu_fullscreen);
    	MenuItem menuItem2 = menu.findItem(R.string.menu_fullscreen2);
    	if (menuItem != null) {
    		menuItem.setChecked(viewerPreferences.isFullScreen());
    	}
    	if (menuItem2 != null) {
    		menuItem2.setChecked(viewerPreferences.isFullScreen());
    	}
    	return true;
    }

    private void setFullScreenMenuItemText(MenuItem menuItem)
    {
        menuItem.setTitle("Full screen " + (menuItem.isChecked() ? "on" : "off"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.string.menu_exit:
                System.exit(0);
                return true;
            case R.string.menu_goto:
                showDialog(DIALOG_GOTO);
                return true;
            case R.string.menu_fullscreen:
            case R.string.menu_fullscreen2:
                item.setChecked(!item.isChecked());
                viewerPreferences.setFullScreen(item.isChecked());

                setFullScreen();
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case DIALOG_GOTO:
                return new GoToPageDialog(this, documentView, decodeService);
        }
        return null;
    }
}
