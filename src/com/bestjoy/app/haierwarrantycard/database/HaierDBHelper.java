package com.bestjoy.app.haierwarrantycard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shwy.bestjoy.utils.DebugUtils;

/**
 * @author Sean Owen
 * @author chenkai
 */
public final class HaierDBHelper extends SQLiteOpenHelper {
private static final String TAG = "HaierDBHelper";
  private static final int DB_VERSION = 4;
  private static final String DB_NAME = "haier.db";
  public static final String ID = "_id";
 
  public static final String TABLE_NAME_MY_CARD = "mycard";
  public static final String FLAG_DELETED = "deleted";
  public static final String CARD_ID = "_id";
  public static final String CONTACT_NAME="name";
  public static final String CONTACT_TEL="tel";
  public static final String CONTACT_BID="bid";
  public static final String CONTACT_DATE="date";
  public static final String CONTACT_NOTE="note";
  public static final String CONTACT_ORG="org";
  public static final String CONTACT_EMAIL="email";
  public static final String CONTACT_ADDRESS = "address";
  public static final String CONTACT_FILTER="filter";
  public static final String CONTACT_TITLE="title";
  public static final String CONTACT_PASSWORD="password";
  public static final String CONTACT_TYPE="type";
  public static final String CONTACT_HAS_PHOTO = "has_photo";
    /**�����ҵ���Ƭ��ݱ?accounts._id���*/
  public static final String CARD_ACCOUNT_MD = "account_pmd";
  public static final String CARD_ACCOUNT_PWD = "account_pwd";
  
  //account table
  public static final String TABLE_NAME_ACCOUNTS = "accounts";
  /**用户唯一识别码*/
  public static final String ACCOUNT_MD = "uid";
  public static final String ACCOUNT_DEFAULT = "isDefault";
  public static final String ACCOUNT_TEL = CONTACT_TEL;
  public static final String ACCOUNT_NAME = CONTACT_NAME;
  public static final String ACCOUNT_PWD = CONTACT_PASSWORD;
  public static final String ACCOUNT_CARD_COUNT = "card_count";
  public static final String ACCOUNT_HOME_COUNT = "home_count";

  public static final String ACCOUNT_PHONES = "phones";

  public static final String ACCOUNT_HAS_PHOTO = "hasPhoto";
  
  //home table
  public static final String TABLE_NAME_HOMES = "homes";
  public static final String REF_ACCOUNT_ID = "uid";
  /**地址id,每个地址的id,这个目前没用,要是更改地址的话可能会用到*/
  public static final String HOME_ADDRESS_ID = "aid";
  public static final String HOME_NAME = "name";
  public static final String HOME_WHERE = "where";
  public static final String HOME_DEFAULT = "isDefault";
  public static final String MODIFIED = "modified";
  /**我的家TAB位置,用户可以调整顺序*/
  public static final String POSITION = "position";
  
  //devices table
  public static final String TABLE_NAME_DEVICES = "devices";
  /**所属家*/
  public static final String REF_HOME_ID = "home_id";
  /**设别名称*/
  public static final String DEVICE_NAME = "name";
  /**设备类别，比如大类是电视剧*/
  public static final String DEVICE_TYPE = "type";
  /**品牌*/
  public static final String DEVICE_BRAND = "brand";
  /**商品编号*/
  public static final String DEVICE_SERIAL = "serial";
  /**型号*/
  public static final String DEVICE_MODEL = "model";
  /**售后电话*/
  public static final String DEVICE_SOLD_TEL = "tel";
  /**购买价格*/
  public static final String DEVICE_COST = "cost";
  /**购买日期*/
  public static final String DEVICE_BUY_DATE = "buyDate";
  /**购买途径*/
  public static final String DEVICE_BUY_WHERE = "buyWhere";
  /**延保时间*/
  public static final String DEVICE_BUY_EXTENDED = "buyExtended";
  /**整机保修*/
  public static final String DEVICE_WARRANTY_PERIOD = "warranty_period";
  /**配件保修*/
  public static final String DEVICE_COMPONENT_WARRANTY_PERIOD = "component_warranty_period";
  /**延保单位*/
  public static final String DEVICE_BUY_EXTENDED_COMPANY = "extendedCommany";
  /**延保单位电话*/
  public static final String DEVICE_BUY_EXTENDED_COPMANY_TEL = "extendedCommanyTel";
  
  //这里是设备表的扩展，如遥控器
  
  //设备数据库
  public static final String DB_DEVICE_NAME = "device.db";
  public static final String TABLE_NAME_DEVICE_DALEI = "DaLei";
  public static final String DEVICE_DALEI_NAME = "Name";
  public static final String DEVICE_DALEI_ID = "ID";
  
  public static final String TABLE_NAME_DEVICE_XIAOLEI = "XiaoLei";
  public static final String DEVICE_XIALEI_DID = "DID";
  public static final String DEVICE_XIALEI_XID = "XID";
  public static final String DEVICE_XIALEI_NAME = "XName";
  
  public static final String TABLE_NAME_DEVICE_PINPAI = "PinPai";
  public static final String DEVICE_PINPAI_XID = "XID";
  public static final String DEVICE_PINPAI_PID = "PID";
  public static final String DEVICE_PINPAI_NAME = "PName";
  public static final String DEVICE_PINPAI_PINYIN = "PinYin";
  public static final String DEVICE_PINPAI_CODE = "Code";
  

  public static final String TABLE_NAME_DEVICE_CITY_ = "T_City";
  public static final String DEVICE_CITY_ID = "CityID";
  public static final String DEVICE_CITY_NAME = "CityName";
  public static final String DEVICE_CITY_PID = "ProID";
  public static final String DEVICE_CITY_SORT = "CitySort";
  
  public static final String TABLE_NAME_DEVICE_DISTRICT_ = "T_District";
  public static final String DEVICE_DIS_ID = "Id";
  public static final String DEVICE_DIS_NAME = "DisName";
  public static final String DEVICE_DIS_CID = "CityID";
  public static final String DEVICE_DIS_DISSORT = "DisSort";
  
  public static final String TABLE_NAME_DEVICE_PROVINCE = "T_Province";
  public static final String DEVICE_PRO_ID = "ProID";
  public static final String DEVICE_PRO_NAME = "ProName";
  public static final String DEVICE_PRO_SORT = "ProSort";
  public static final String DEVICE_PRO_REMARK = "ProRemark";
  
  // Qrcode scan part begin
  public static final String TABLE_SCAN_NAME = "history";
  public static final String ID_COL = "id";
  public static final String TEXT_COL = "text";
  public static final String FORMAT_COL = "format";
  public static final String DISPLAY_COL = "display";
  public static final String TIMESTAMP_COL = "timestamp";
  public static final String DETAILS_COL = "details";
  // Qrcode scan part end
  
  public HaierDBHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }
  
  private SQLiteDatabase mWritableDatabase;
  private SQLiteDatabase mReadableDatabase;
  
  public synchronized SQLiteDatabase openWritableDatabase() {
	  if (mWritableDatabase == null) {
		  mWritableDatabase = getWritableDatabase();
	  }
	  return mWritableDatabase;
  }
  
  public synchronized SQLiteDatabase openReadableDatabase() {
	  if (mReadableDatabase == null) {
		  mReadableDatabase = getReadableDatabase();
	  }
	  return mReadableDatabase;
  }
  
  public synchronized void closeReadableDatabase() {
	  if (mReadableDatabase != null && mReadableDatabase.isOpen()) {
		  mReadableDatabase.close();
		  mReadableDatabase = null;
	  }
  }
  
  public synchronized void closeWritableDatabase() {
	  if (mWritableDatabase != null && mWritableDatabase.isOpen()) {
		  mWritableDatabase.close();
		  mWritableDatabase = null;
	  }
  }
  
  public synchronized void closeDatabase() {
	  closeReadableDatabase();
	  closeWritableDatabase();
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
      DebugUtils.logD(TAG, "onCreate");
   
       // Create Account table
  	   createAccountTable(sqLiteDatabase);
  		//Create Homes table
  		createHomesTable(sqLiteDatabase);
  		// Create devices table
  		createDevicesTable(sqLiteDatabase);
//  		createTriggerForMyCardTable(sqLiteDatabase);
  		// Create scan history
  		createScanHistory(sqLiteDatabase);

  		
  }
  
//  private void createCardTable(SQLiteDatabase sqLiteDatabase) {
//	  sqLiteDatabase.execSQL(
//	            "CREATE TABLE " + TABLE_NAME_MY_CARD + " (" +
//	            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//	            CARD_ACCOUNT_MD + " INTEGER  NOT NULL DEFAULT 0 CONSTRAINT fk_accounts_id REFERENCES accounts(account_pmd), " +
//	            CARD_ACCOUNT_PWD + " TEXT, " +
//	            CONTACT_NAME + " TEXT, " +
//	            CONTACT_TEL + " TEXT, " +
//	            CONTACT_BID + " TEXT, " +
//	            CONTACT_EMAIL + " TEXT, " +
//	            CONTACT_ADDRESS + " TEXT, " +
//	            CONTACT_ORG + " TEXT, " +
//	            CONTACT_DATE + " TEXT, " +
//	            CONTACT_PASSWORD + " TEXT, " +
//	            CONTACT_NOTE + " TEXT, " +
//	            CONTACT_TYPE + " TEXT, " +
//	            CONTACT_HAS_PHOTO + " INTEGER NOT NULL DEFAULT 0, " +
//	            CONTACT_TITLE + " TEXT" +
//	            ");");
//  }
  
  private void createTriggerForAccountTable(SQLiteDatabase sqLiteDatabase) {
	  String sql = "CREATE TRIGGER insert_account" + " BEFORE INSERT " + " ON " + TABLE_NAME_ACCOUNTS + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET isDefault = 0 WHERE pmd != new.pmd and isDefault = 1; END;";
	  sqLiteDatabase.execSQL(sql);
	  
	  sql = "CREATE TRIGGER update_default_account" + " BEFORE UPDATE OF isDefault " + " ON " + TABLE_NAME_ACCOUNTS + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET isDefault = 0 WHERE pmd != old.pmd and isDefault = 1; END;";
	  sqLiteDatabase.execSQL(sql);
	  
  }
  
  private void createTriggerForHomeTable(SQLiteDatabase sqLiteDatabase) {
	  String sql = "CREATE TRIGGER insert_home_update_account" + " AFTER INSERT " + " ON " + TABLE_NAME_HOMES + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET home_count = home_count+1 WHERE _id = new.account_id; END;";
	  sqLiteDatabase.execSQL(sql);
	  
	  sql = "CREATE TRIGGER delete_home_update_account" + " AFTER DELETE " + " ON " + TABLE_NAME_HOMES + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET home_count = home_count-1 WHERE _id = old.account_id; END;";
	  sqLiteDatabase.execSQL(sql);
	
  }
  
  private void createTriggerForDeviceTable(SQLiteDatabase sqLiteDatabase) {
	  String sql = "CREATE TRIGGER insert_device_update_account" + " AFTER INSERT " + " ON " + TABLE_NAME_DEVICES + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET card_count = card_count+1 WHERE _id = new.account_id; END;";
	  sqLiteDatabase.execSQL(sql);
	  
	  sql = "CREATE TRIGGER delete_device_update_account" + " AFTER DELETE " + " ON " + TABLE_NAME_DEVICES + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET card_count = card_count-1 WHERE _id = old.account_id; END;";
	  sqLiteDatabase.execSQL(sql);
	  
	  sql = "CREATE TRIGGER insert_device_update_home" + " AFTER INSERT " + " ON " + TABLE_NAME_DEVICES + 
	  " BEGIN UPDATE " + TABLE_NAME_HOMES + " SET card_count = card_count+1 WHERE _id = new.home_id; END;";
	  sqLiteDatabase.execSQL(sql);
		
	  sql = "CREATE TRIGGER delete_device_update_home" + " AFTER DELETE " + " ON " + TABLE_NAME_DEVICES + 
			  " BEGIN UPDATE " + TABLE_NAME_HOMES + " SET card_count = card_count-1 WHERE _id = old.home_id; END;";
	  sqLiteDatabase.execSQL(sql);
  }
  
  private void createAccountTable(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_NAME_ACCOUNTS + " (" +
	            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            ACCOUNT_MD + " TEXT, " +
	            ACCOUNT_TEL + " TEXT, " +
	            ACCOUNT_PWD + " TEXT, " +
	            ACCOUNT_DEFAULT + " INTEGER NOT NULL DEFAULT 1, " +
	            ACCOUNT_CARD_COUNT + " INTEGER NOT NULL DEFAULT 0, " +
	            ACCOUNT_HOME_COUNT + " INTEGER NOT NULL DEFAULT 0, " +
	            ACCOUNT_NAME + " TEXT, " +
	            ACCOUNT_PHONES  + " TEXT, " +
	            ACCOUNT_HAS_PHOTO + " INTEGER NOT NULL DEFAULT 0, " +
	            CONTACT_DATE + " TEXT" +
	            ");");
	  createTriggerForAccountTable(sqLiteDatabase);
  }
  
  private void createHomesTable(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_NAME_HOMES + " (" +
	            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            REF_ACCOUNT_ID + " TEXT, " +
	            HOME_ADDRESS_ID + " TEXT, " +
	            HOME_WHERE + " TEXT, " +
	            HOME_NAME + " TEXT, " +
	            HOME_DEFAULT + " INTEGER NOT NULL DEFAULT 1, " +
	            POSITION + " INTEGER NOT NULL DEFAULT 1, " +
	            MODIFIED + " TEXT, " +
	            CONTACT_DATE + " TEXT" +
	            ");");
	  createTriggerForHomeTable(sqLiteDatabase);
  }
  
  private void createDevicesTable(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_NAME_DEVICES + " (" +
	            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            REF_ACCOUNT_ID + " TEXT, " +  //账户id
	            REF_HOME_ID + " TEXT, " +     //家id
	            DEVICE_TYPE + " TEXT, " +
	            DEVICE_NAME + " TEXT, " +
	            DEVICE_BRAND + " TEXT, " +
	            DEVICE_MODEL + " TEXT, " +
	            DEVICE_SERIAL + " TEXT, " +
	            DEVICE_SOLD_TEL + " TEXT, " +
	            DEVICE_BUY_DATE + " TEXT, " +
	            DEVICE_COST + " TEXT, " +
	            DEVICE_BUY_WHERE + " TEXT, " +
	            DEVICE_BUY_EXTENDED + " TEXT, " +
	            DEVICE_BUY_EXTENDED_COMPANY + " TEXT, " +
	            DEVICE_BUY_EXTENDED_COPMANY_TEL + " TEXT, " +
	            DEVICE_WARRANTY_PERIOD + " TEXT, " +
	            DEVICE_COMPONENT_WARRANTY_PERIOD + " TEXT, " +
	            MODIFIED + " TEXT" +
	            ");");
	  createTriggerForDeviceTable(sqLiteDatabase);
  }
  
  private void createScanHistory(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_SCAN_NAME + " (" +
	            ID_COL + " INTEGER PRIMARY KEY, " +
	            TEXT_COL + " TEXT, " +
	            FORMAT_COL + " TEXT, " +
	            DISPLAY_COL + " TEXT, " +
	            TIMESTAMP_COL + " INTEGER, " +
	            DETAILS_COL + " TEXT);");
  }
  
  private void addTextColumn(SQLiteDatabase sqLiteDatabase, String table, String column) {
	    String alterForTitleSql = "ALTER TABLE " + table +" ADD " + column + " TEXT";
		sqLiteDatabase.execSQL(alterForTitleSql);
  }
  private void addIntColumn(SQLiteDatabase sqLiteDatabase, String table, String column, int defaultValue) {
	    String alterForTitleSql = "ALTER TABLE " + table +" ADD " + column + " INTEGER NOT NULL DEFAULT " + defaultValue;
		sqLiteDatabase.execSQL(alterForTitleSql);
}

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
	  DebugUtils.logD(TAG, "onUpgrade oldVersion " + oldVersion + " newVersion " + newVersion);
  }
}
