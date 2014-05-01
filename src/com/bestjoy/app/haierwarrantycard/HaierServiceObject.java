package com.bestjoy.app.haierwarrantycard;


public class HaierServiceObject {

	public static final String SERVICE_URL = "http://115.29.231.29/Haier/";
	
	public static final String PRODUCT_AVATOR_URL= "http://115.29.231.29/proimg/";
	public static final String FAPIAO_PREFIX = "http://115.29.231.29/Fapiao/";
	/***
	   * 产品图片网址  http://115.29.231.29/proimg/507/5070A000A.jpg  说明5070A000A：为Key，507：key 为前三位
	   * @return
	   */
	public static String getProdcutAvatorUrl(String ky) {
		String ky3 = ky.substring(0,3);
		  StringBuilder sb = new StringBuilder(PRODUCT_AVATOR_URL);
		  sb.append(ky3).append("/").append(ky).append(".jpg");
		  return sb.toString();
	}
	
	/**
	 * http://115.29.231.29/Fapiao/20140421/01324df60b0734de0f973c7907af55fc.jpg
	 * @param ky
	 * @return
	 */
	public static String getProdcutFaPiaoUrl(String fapiao) {
		return fapiao;
	}
}
