package com.google.zxing.client.result;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bestjoy.app.haierwarrantycard.HaierServiceObject;
import com.bestjoy.app.haierwarrantycard.MyApplication;
import com.bestjoy.app.haierwarrantycard.R;
import com.bestjoy.app.haierwarrantycard.account.BaoxiuCardObject;
import com.bestjoy.app.haierwarrantycard.ui.NewCardActivity;
import com.bestjoy.app.haierwarrantycard.ui.model.ModleSettings;
import com.google.zxing.client.result.HaierParsedResult.HaierBaoxiuCardParser;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.NetworkUtils;

public final class HaierResultHandler extends ResultHandler {

	public static final String QUERY_SERVICE = HaierServiceObject.SERVICE_URL + "TransCode.ashx?oid=";
  private static final int[] buttons = {
	  R.string.button_ignore,
      R.string.menu_new_card,
  };
  
  /**是否是其他Activity调用来识别条码任务*/
	private boolean mParseTask = false;
	private static final int[] parse_task_buttons = { R.string.button_rescan,
		R.string.button_scan_finish_return_result,};

  public HaierResultHandler(Activity activity, ParsedResult result) {
    super(activity, result);
  }

  @Override
	public int getButtonCount() {
		if (mParseTask) {
			return 2; //如果是解析任务，我们只显示两个按钮，返回扫描和确定商品信息
		}
		return buttons.length;
	}

  @Override
	public int getButtonText(int index) {
		if (mParseTask) {
			return parse_task_buttons[index];
		} else {
			return buttons[index];
		}
		
	}
	
	public void setParseOperation(boolean parseTask) {
		mParseTask = parseTask;
	}

  @Override
  public void handleButtonPress(int index) {
    switch (index) {
      case 0:
    	gobackAndScan();
        break;
      case 1:
    	  if (ComConnectivityManager.getInstance().isConnected()) {
    		  queryDeviceInfoFromService(((HaierParsedResult) getResult()).getParam());
    	  } else {
    		  //no network
    		  MyApplication.getInstance().showMessage(R.string.msg_scan_finish_return_result_no_network);
    	  }
        break;
    }
  }
  
  private QueryDeviceInfoFromService mQueryDeviceInfoFromService;
  private ProgressDialog mProgressDialog;
  public void queryDeviceInfoFromService(String param) {
	  if (mQueryDeviceInfoFromService!= null) mQueryDeviceInfoFromService.cancelTask(true);
	  if (mProgressDialog == null) {
		  mProgressDialog = new ProgressDialog(activity);
		  mProgressDialog.setMessage(activity.getResources().getString(R.string.msg_progressdialog_wait));
		  mProgressDialog.setCancelable(false);
		  mProgressDialog.setButton(activity.getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mQueryDeviceInfoFromService!= null) mQueryDeviceInfoFromService.cancelTask(true);
			}
		});
	  }
	  mProgressDialog.show();
	  mQueryDeviceInfoFromService = new QueryDeviceInfoFromService(param);
	  mQueryDeviceInfoFromService.execute();
  }
  
  private class QueryDeviceInfoFromService extends AsyncTask<Void, Void, Boolean> {

	  private String _param;
	  private String _error;
	  private BaoxiuCardObject _baoxiuCardObject;
	  
	  public QueryDeviceInfoFromService(String param) {
		  _param = param;
	  }
	@Override
	protected Boolean doInBackground(Void... params) {
		InputStream is = null;
		try {
			is = NetworkUtils.openContectionLocked(QUERY_SERVICE, _param, MyApplication.getInstance().getSecurityKeyValuesObject());
			if (is == null) {
				_error = activity.getResources().getString(R.string.msg_can_not_access_network);
				return false;
			} else {
				_baoxiuCardObject = new BaoxiuCardObject();
				_error = HaierBaoxiuCardParser.parse(is, _baoxiuCardObject);
				return _error == null;
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		if (isCancelled()) {
			return;
		}
		if (result) {
			if (mParseTask) { //确定条码信息
	        	  Intent intent = new Intent();
	        	  activity.setResult(Activity.RESULT_OK, intent);
	        	  BaoxiuCardObject.setBaoxiuCardObject(_baoxiuCardObject);
	        	  activity.finish();
	    	  } else {//新建我的保修卡
	    		  Bundle bundle = ModleSettings.createMyCardDefaultBundle(activity);
	    		  BaoxiuCardObject.setBaoxiuCardObject(_baoxiuCardObject);
	    		  //扫描结果
	    		  NewCardActivity.startIntent(activity, bundle);
	    	  }
		} else {
			MyApplication.getInstance().showMessage(_error);
		}
	}
	@Override
	protected void onCancelled() {
		super.onCancelled();
		mProgressDialog.dismiss();
	}
	
	public void cancelTask(boolean mayInterruptIfRunning) {
		super.cancel(mayInterruptIfRunning);
	}
	
	  
  }

  @Override
  public int getDisplayTitle() {
    return R.string.result_haier_barcode;
  }
}