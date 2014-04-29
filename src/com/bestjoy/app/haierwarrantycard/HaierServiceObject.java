package com.bestjoy.app.haierwarrantycard;


public class HaierServiceObject {

	public static final String SERVICE_URL = "http://115.29.231.29/Haier/";
	
	public static final String PRODUCT_AVATOR_URL= "http://www.51cck.com/";
	/***
	   * 返回商品预览图网址www.51cck.com/KY前9位数字/KY.jpg
	   * @return
	   */
	public static String getProdcutAvatorUrl(String ky) {
		String ky9 = ky.substring(0,9);
		  StringBuilder sb = new StringBuilder(PRODUCT_AVATOR_URL);
		  sb.append(ky9).append("/").append(ky).append(".jpg");
		  return sb.toString();
	}
}
