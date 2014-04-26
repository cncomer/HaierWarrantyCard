package com.bestjoy.app.haierwarrantycard.ui;

import com.shwy.bestjoy.utils.InfoInterface;

import android.os.Bundle;

public class ModleBaseFragment extends BaseFragment{

	/**请求扫描条码*/
	public static final int REQUEST_SCAN = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	/**
	 * 可以用来从其他地方更新当前Fragment的数据
	 * @param infoInterface
	 */
	public void updateInfoInterface(InfoInterface infoInterface) {
		
	}
}
