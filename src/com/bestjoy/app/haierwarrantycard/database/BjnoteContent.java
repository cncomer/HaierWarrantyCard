package com.bestjoy.app.haierwarrantycard.database;

import android.content.ContentResolver;
import android.net.Uri;

public class BjnoteContent {

	public static final String AUTHORITY = "com.bestjoy.app.haierwarrantycard.provider.BjnoteProvider";
    // The notifier authority is used to send notifications regarding changes to messages (insert,
    // delete, or update) and is intended as an optimization for use by clients of message list
    // cursors (initially, the email AppWidget).
    public static final String NOTIFIER_AUTHORITY = "com.bestjoy.app.haierwarrantycard.notify.BjnoteProvider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    
    public static final String DEVICE_AUTHORITY = "com.bestjoy.app.haierwarrantycard.provider.DeviceProvider";
    public static final String DEVICE_NOTIFIER_AUTHORITY = "com.bestjoy.app.haierwarrantycard.notify.DeviceProvider";
    public static final Uri DEVICE_CONTENT_URI = Uri.parse("content://" + DEVICE_AUTHORITY);
    
    // All classes share this
    public static final String RECORD_ID = "_id";

    public static final String[] COUNT_COLUMNS = new String[]{"count(*)"};

    /**
     * This projection can be used with any of the EmailContent classes, when all you need
     * is a list of id's.  Use ID_PROJECTION_COLUMN to access the row data.
     */
    public static final String[] ID_PROJECTION = new String[] {
        RECORD_ID
    };
    public static final int ID_PROJECTION_COLUMN = 0;

    public static final String ID_SELECTION = RECORD_ID + " =?";
    
    
    public static class Accounts extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "accounts");
    }
    
    public static class Homes extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "homes");
    }
    /**我的保修卡设备*/
    public static class BaoxiuCard extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "baoxiucard");
    	public static final Uri BILL_CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "baoxiucard/preview/bill");
    }
    
    public static class DaLei extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "dalei");
    }
    
    public static class XiaoLei extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "xiaolei");
    }
    
    public static class PinPai extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "pinpai");
    }
    
    public static class XingHao extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "xinghao");
    }
    
    public static class Province extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "province");
    }
    
    public static class City extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "city");
    }
    
    public static class District extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "district");
    }
    
    public static class ScanHistory extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "scan_history");
    }
    
    public static class HaierRegion extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "haierregion");
    }
    
    public static class YMESSAGE extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "ymessage");
    	
    	public static String[] PROJECTION = new String[]{
    		HaierDBHelper.ID,
    		HaierDBHelper.YOUMENG_MESSAGE_ID,
    		HaierDBHelper.YOUMENG_TITLE,
    		HaierDBHelper.YOUMENG_TEXT,
    		HaierDBHelper.YOUMENG_MESSAGE_ACTIVITY,
    		HaierDBHelper.YOUMENG_MESSAGE_URL,
    		HaierDBHelper.YOUMENG_MESSAGE_CUSTOM,
    		HaierDBHelper.YOUMENG_MESSAGE_RAW, 
    		HaierDBHelper.DATE,
    	};
    	
    	public static final int INDEX_ID = 0;
    	public static final int INDEX_MESSAGE_ID = 1;
    	public static final int INDEX_TITLE = 2;
    	public static final int INDEX_TEXT = 3;
    	public static final int INDEX_MESSAGE_ACTIVITY = 4;
    	public static final int INDEX_MESSAGE_URL = 5;
    	public static final int INDEX_MESSAGE_CUSTOM = 6;
    	public static final int INDEX_MESSAGE_RAW = 7;
    	public static final int INDEX_DATE = 8;
    	
    	public static final String WHERE_YMESSAGE_ID = HaierDBHelper.YOUMENG_MESSAGE_ID + "=?";
    }
    
    /**调用该类的CONTENT_URI来关闭设备数据库*/
    public static class CloseDeviceDatabase extends BjnoteContent{
    	private static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "closedevice");
    	/**调用该方法来关闭设备数据库*/
    	public static void closeDeviceDatabase(ContentResolver cr) {
    		cr.query(CONTENT_URI, null, null, null, null);
    	}
    }
    
    public static class IM extends BjnoteContent{
    	public static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "im/qun");
    	public static final Uri CONTENT_URI_QUN = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "im/qun");
    	public static final Uri CONTENT_URI_FRIEND = Uri.withAppendedPath(BjnoteContent.CONTENT_URI, "im/friend");
    }
}
