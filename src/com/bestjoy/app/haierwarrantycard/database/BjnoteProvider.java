package com.shwy.bestjoy.bjnote.varrantcard.provider;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import com.google.zxing.client.android.history.ContactsDBHelper;
import com.shwy.bestjoy.bjnote.myhome.goods.GoodsListActivity;
import com.shwy.bestjoy.bjnote.varrantcard.BJfileApp;
import com.shwy.bestjoy.utils.DebugUtils;

public class BjnoteProvider extends ContentProvider{
	private static final String TAG = "BjnoteProvider";
	private SQLiteDatabase mContactDatabase;
	private String[] mTables = new String[]{
			HaierDBHelper.TABLE_NAME_MY_CARD,
			HaierDBHelper.TABLE_NAME_ACCOUNTS,
			HaierDBHelper.TABLE_NAME_MYHOME_GOODS,
			HaierDBHelper.TABLE_NAME_MYLIFE,
			HaierDBHelper.TABLE_NAME_GOODS_FEEDBACK,
//			ContactsDBHelper.TABLE_NAME_MYLIFE_CONSUME,
	};
	private static final int BASE = 8;
	private static final int MY_CARD = 0x0000; //>>BASE
	private static final int MY_CARD_ID = 0x0001;
	
	private static final int ACCOUNT = 0x0100;
	private static final int ACCOUNT_ID = 0x0101;
	
	
	private static final int HOME_GOODS = 0x0200;
	private static final int HOME_GOODS_ID = 0x0201;
	/**����Ԥ����Ʊ*/
	private static final int HOME_GOODS_BILL_AVATOR = 0x0202;
	/**����Ԥ����ƷͼƬ*/
	private static final int HOME_GOODS_PRODUCT_AVATOR = 0x0203;
	/**����Ԥ����Ʊ,�����õ�*/
	private static final int HOME_GOODS_TEMP_BILL_AVATOR = 0x0204;
	/**����Ԥ����ƷͼƬ�������õ�*/
	private static final int HOME_GOODS_TEMP_PRODUCT_AVATOR = 0x0205;
	
	private static final int MYLIFE = 0x0300;
	private static final int MYLIFE_ID = 0x0301;
	
//	private static final int MYLIFE_CONSUME = 0x0400;
//	private static final int MYLIFE_CONSUME_ID = 0x0401;
	
	/**��Ʒ����*/
	private static final int HOME_GOODS_FEEDBACK = 0x0400;
	private static final int HOME_GOODS_FEEDBACK_ID = 0x0401;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	 static {
	        // URI matching table
	        UriMatcher matcher = sURIMatcher;
	        matcher.addURI(BjnoteContent.AUTHORITY, "mycard", MY_CARD);
	        matcher.addURI(BjnoteContent.AUTHORITY, "mycard/#", MY_CARD_ID);
	        
	        matcher.addURI(BjnoteContent.AUTHORITY, "accounts", ACCOUNT);
	        matcher.addURI(BjnoteContent.AUTHORITY, "accounts/#", ACCOUNT_ID);
	        
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods", HOME_GOODS);
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods/#", HOME_GOODS_ID);
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods/preview/bill", HOME_GOODS_BILL_AVATOR);
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods/preview/avator", HOME_GOODS_PRODUCT_AVATOR);
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods/preview/billtemp", HOME_GOODS_TEMP_BILL_AVATOR);
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods/preview/avatortemp", HOME_GOODS_TEMP_PRODUCT_AVATOR);
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods/feedback", HOME_GOODS_FEEDBACK);
	        matcher.addURI(BjnoteContent.AUTHORITY, "homegoods/feedback/#", HOME_GOODS_FEEDBACK_ID);
	        
	        matcher.addURI(BjnoteContent.AUTHORITY, "mylife", MYLIFE);
	        matcher.addURI(BjnoteContent.AUTHORITY, "mylife/#", MYLIFE_ID);
	        
//	        matcher.addURI(BjnoteContent.AUTHORITY, "mylife/consume", MYLIFE_CONSUME);
//	        matcher.addURI(BjnoteContent.AUTHORITY, "mylife/consume/#", MYLIFE_CONSUME);
	        
	        //TODO ���Ӹ��
	 }
	
	synchronized SQLiteDatabase getDatabase(Context context) {
        // Always return the cached database, if we've got one
        if (mContactDatabase != null) {
            return mContactDatabase;
        }


        HaierDBHelper helper = new HaierDBHelper(context);
        mContactDatabase = helper.getWritableDatabase();
        mContactDatabase.setLockingEnabled(true);
        return mContactDatabase;
	}
	
	@Override
	public boolean onCreate() {
		return false;
	}
	
	/**
     * Wrap the UriMatcher call so we can throw a runtime exception if an unknown Uri is passed in
     * @param uri the Uri to match
     * @return the match value
     */
    private static int findMatch(Uri uri, String methodName) {
        int match = sURIMatcher.match(uri);
        if (match < 0) {
            throw new IllegalArgumentException("Unknown uri: " + uri);
        } 
        DebugUtils.logD(TAG, methodName + ": uri=" + uri + ", match is " + match);
        return match;
    }
    
    private void notifyChange(int match) {
    	Context context = getContext();
    	Uri notify = BjnoteContent.CONTENT_URI;
    	switch(match) {
    	case MY_CARD:
    	case MY_CARD_ID:
    		notify = BjnoteContent.MyCard.CONTENT_URI;
    		break;
		case ACCOUNT:
		case ACCOUNT_ID:
			notify = BjnoteContent.Accounts.CONTENT_URI;
			break;
		case HOME_GOODS:
		case HOME_GOODS_ID:
			notify = BjnoteContent.HomeGoods.CONTENT_URI;
			break;
		case HOME_GOODS_FEEDBACK:
		case HOME_GOODS_FEEDBACK_ID:
			notify = BjnoteContent.HomeGoods.FEEDBACK_CONTENT_URI;
			break;
		case MYLIFE:
		case MYLIFE_ID:
			notify = BjnoteContent.MyLife.CONTENT_URI;
			break;
    	}
    	ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(notify, null);
    }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = findMatch(uri, "delete");
        Context context = getContext();

        // See the comment at delete(), above
        SQLiteDatabase db = getDatabase(context);
        String table = mTables[match>>BASE];
        DebugUtils.logProvider(TAG, "delete data from table " + table);
        int count = 0;
        switch(match) {
        case MY_CARD:
        case MY_CARD_ID:
		case ACCOUNT:
		case ACCOUNT_ID:
		case HOME_GOODS:
		case HOME_GOODS_ID:
		case MYLIFE:
		case MYLIFE_ID:
		case HOME_GOODS_FEEDBACK:
		case HOME_GOODS_FEEDBACK_ID:
        	count = db.delete(table, buildSelection(match, uri, selection), selectionArgs);
        }
        if (count >0) notifyChange(match);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		 int match = findMatch(uri, "insert");
         Context context = getContext();

         // See the comment at delete(), above
         SQLiteDatabase db = getDatabase(context);
         String table = mTables[match>>BASE];
         DebugUtils.logProvider(TAG, "insert values into table " + table);
//         switch(match) {
//	         case MY_CARD:
//	         case MY_CARD_ID:
//	         case RECEIVED_CONTACT:
//	         case RECEIVED_CONTACT_ID:
//	         case EXCHANGE_TOPIC:
//	     	 case EXCHANGE_TOPIC_ID:
//	     	 case EXCHANGE_TOPIC_LIST:
//	     	 case EXCHANGE_TOPIC_LIST_ID:
//	     	 case CIRCLE_TOPIC:
//			 case CIRCLE_TOPIC_ID:
//			 case CIRCLE_TOPIC_LIST:
//			 case CIRCLE_TOPIC_LIST_ID:
//			 case CIRCLE_MEMBER_DETAIL:
//			 case CIRCLE_MEMBER_DETAIL_ID:
//			 case ACCOUNT:
//			 case ACCOUNT_ID:
//			 case FEEDBACK:
//			 case FEEDBACK_ID:
//			 case QUANPHOTO:
//			 case QUANPHOTO_ID:
//			 case ZHT:
//			 case ZHT_ID:
//	     		break;
//         }
         //������insert����_id�ֶΣ�������Զ������
         if (values.containsKey(HaierDBHelper.CONTACT_ID)) {
      		values.remove(HaierDBHelper.CONTACT_ID);
      	 }
     	 long id = db.insert(table, null, values);
     	 if (id > 0) {
     		notifyChange(match);
   		    return ContentUris.withAppendedId(uri, id);
     	 }
		 return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		 int match = findMatch(uri, "query");
         Context context = getContext();
         // See the comment at delete(), above
         SQLiteDatabase db = getDatabase(context);
         String table = mTables[match>>BASE];
         DebugUtils.logProvider(TAG, "query table " + table);
         Cursor result = null;
         switch(match) {
	         case MY_CARD:
	         case MY_CARD_ID:
			 case ACCOUNT:
			 case ACCOUNT_ID:
			 case HOME_GOODS:
			 case HOME_GOODS_ID:
			 case MYLIFE:
			case MYLIFE_ID:
			case HOME_GOODS_FEEDBACK:
			case HOME_GOODS_FEEDBACK_ID:
        	     result = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
         }
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = findMatch(uri, "update");
        Context context = getContext();
        
        SQLiteDatabase db = getDatabase(context);
        String table = mTables[match>>BASE];
        DebugUtils.logProvider(TAG, "update data for table " + table);
        int count = 0;
        switch(match) {
	        case MY_CARD:
	        case MY_CARD_ID:
			case ACCOUNT:
			case ACCOUNT_ID:
			case HOME_GOODS:
			case HOME_GOODS_ID:
			case MYLIFE:
			case MYLIFE_ID:
			case HOME_GOODS_FEEDBACK:
			case HOME_GOODS_FEEDBACK_ID:
        	    count = db.update(table, values, buildSelection(match, uri, selection), selectionArgs);
        }
        if (count >0) notifyChange(match);
		return count;
	}
	
	private String buildSelection(int match, Uri uri, String selection) {
		long id = -1;
		switch(match) {
		case MY_CARD_ID:
		case ACCOUNT_ID:
		case HOME_GOODS_ID:
		case MYLIFE_ID:
		case HOME_GOODS_FEEDBACK_ID:
			try {
				id = ContentUris.parseId(uri);
			} catch(java.lang.NumberFormatException e) {
				e.printStackTrace();
			}
			break;
		}
		
		if (id == -1) {
			return selection;
		}
		DebugUtils.logProvider(TAG, "find id from Uri#" + id);
		StringBuilder sb = new StringBuilder();
		sb.append(HaierDBHelper.CONTACT_ID);
		sb.append("=").append(id);
		if (!TextUtils.isEmpty(selection)) {
			sb.append(" and ");
			sb.append(selection);
		}
		DebugUtils.logProvider(TAG, "rebuild selection#" + sb.toString());
		return sb.toString();
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		int match = findMatch(uri, "openFile");
		switch(match) {
		case HOME_GOODS_ID: //���Ӧ�ò���ʹ��
			if (GoodsListActivity.mCurrentGoodsObject != null) {
	        	File file = BJfileApp.getInstance().getHomeGoodsBillPhoto(GoodsListActivity.mCurrentGoodsObject.getPhotoId());
	        	if (file.exists()) {
			        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
			    } else if (GoodsListActivity.mCurrentGoodsObject.mBillTempFile.exists()) {
			    	return ParcelFileDescriptor.open(GoodsListActivity.mCurrentGoodsObject.mBillTempFile, ParcelFileDescriptor.MODE_READ_WRITE);
			    } else if (GoodsListActivity.mCurrentGoodsObject.mProductAvatorTempFile.exists()) {
			    	return ParcelFileDescriptor.open(GoodsListActivity.mCurrentGoodsObject.mProductAvatorTempFile, ParcelFileDescriptor.MODE_READ_WRITE);
			    } else {
			    	DebugUtils.logE(TAG, "openFile can't find file " + file.getAbsolutePath());
			    }
	        }
			break;
		case HOME_GOODS_BILL_AVATOR:
			if (GoodsListActivity.mCurrentGoodsObject != null) {
	        	File file = BJfileApp.getInstance().getHomeGoodsBillPhoto(GoodsListActivity.mCurrentGoodsObject.getPhotoId());
	        	if (file.exists()) {
			        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
			    } else if (GoodsListActivity.mCurrentGoodsObject.mBillTempFile.exists()) {
			    	return ParcelFileDescriptor.open(GoodsListActivity.mCurrentGoodsObject.mBillTempFile, ParcelFileDescriptor.MODE_READ_WRITE);
			    } else {
			    	DebugUtils.logE(TAG, "openFile can't find file " + file.getAbsolutePath());
			    }
	        }
			break;
		case HOME_GOODS_PRODUCT_AVATOR:
			if (GoodsListActivity.mCurrentGoodsObject != null) {
	        	File file = BJfileApp.getInstance().getHomeGoodsPhoto(GoodsListActivity.mCurrentGoodsObject.getPhotoId());
	        	if (file.exists()) {
			        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
			    } else if (GoodsListActivity.mCurrentGoodsObject.mProductAvatorTempFile.exists()) {
			    	return ParcelFileDescriptor.open(GoodsListActivity.mCurrentGoodsObject.mProductAvatorTempFile, ParcelFileDescriptor.MODE_READ_WRITE);
			    } else {
			    	DebugUtils.logE(TAG, "openFile can't find file " + file.getAbsolutePath());
			    }
	        }
			break;
		}
		throw new FileNotFoundException("no Image found for uri " + uri);
	}

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode)
			throws FileNotFoundException {
		return super.openAssetFile(uri, mode);
	}

	
	
}
