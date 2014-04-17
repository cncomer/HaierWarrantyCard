package com.bestjoy.app.haierwarrantycard.database;

import com.shwy.bestjoy.utils.DebugUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Sean Owen
 * @author chenkai
 */
public final class HaierDBHelper extends SQLiteOpenHelper {
private static final String TAG = "HaierDBHelper";
  private static final int DB_VERSION = 1;
  private static final String DB_NAME = "haier.db";
 
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
  public static final String ACCOUNT_MD = "pmd";
  public static final String ACCOUNT_DEFAULT = "isDefault";
  public static final String ACCOUNT_TEL = CONTACT_TEL;
  public static final String ACCOUNT_NAME = CONTACT_NAME;
  public static final String ACCOUNT_PWD = CONTACT_PASSWORD;
  public static final String ACCOUNT_CARD_COUNT = "card_count";

  public static final String ACCOUNT_PHONES = "phones";

  public static final String ACCOUNT_HAS_PHOTO = "hasPhoto";
  
  //home table
  public static final String TABLE_NAME_HOMES = "homes";
  public static final String REF_ACCOUNT_ID = "account_id";
  public static final String HOME_NAME = "name";
  public static final String HOME_WHERE = "where";
  public static final String HOME_DEFAULT = "isDefault";
  
  
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
  		//Create Home table
  		createHomeTable(sqLiteDatabase);
  	    // Create repair card table.
  		createCardTable(sqLiteDatabase);
//  		createTriggerForMyCardTable(sqLiteDatabase);

  		
  }
  
  private void createCardTable(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_NAME_MY_CARD + " (" +
	            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            CARD_ACCOUNT_MD + " INTEGER  NOT NULL DEFAULT 0 CONSTRAINT fk_accounts_id REFERENCES accounts(account_pmd), " +
	            CARD_ACCOUNT_PWD + " TEXT, " +
	            CONTACT_NAME + " TEXT, " +
	            CONTACT_TEL + " TEXT, " +
	            CONTACT_BID + " TEXT, " +
	            CONTACT_EMAIL + " TEXT, " +
	            CONTACT_ADDRESS + " TEXT, " +
	            CONTACT_ORG + " TEXT, " +
	            CONTACT_DATE + " TEXT, " +
	            CONTACT_PASSWORD + " TEXT, " +
	            CONTACT_NOTE + " TEXT, " +
	            CONTACT_TYPE + " TEXT, " +
	            CONTACT_HAS_PHOTO + " INTEGER NOT NULL DEFAULT 0, " +
	            CONTACT_TITLE + " TEXT" +
	            ");");
  }
  
  
  
  private void createTriggerForMyCardTable(SQLiteDatabase sqLiteDatabase) {
	  String sql = "CREATE TRIGGER insert_contact_mycard" + " AFTER INSERT " + " ON " + TABLE_NAME_MY_CARD + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET card_count = card_count+1 WHERE _id = new.account_pmd; END;";
	  sqLiteDatabase.execSQL(sql);
	  
	  sql = "CREATE TRIGGER delete_contact_mycard" + " AFTER DELETE " + " ON " + TABLE_NAME_MY_CARD + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET card_count = card_count-1 WHERE _id = old.account_pmd; END;";
	  sqLiteDatabase.execSQL(sql);
  }
  
  private void createTriggerForAccountTable(SQLiteDatabase sqLiteDatabase) {
	  String sql = "CREATE TRIGGER insert_account" + " BEFORE INSERT " + " ON " + TABLE_NAME_ACCOUNTS + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET isDefault = 0 WHERE pmd != new.pmd and isDefault = 1; END;";
	  sqLiteDatabase.execSQL(sql);
	  
	  sql = "CREATE TRIGGER update_default_account" + " BEFORE UPDATE OF isDefault " + " ON " + TABLE_NAME_ACCOUNTS + 
			  " BEGIN UPDATE " + TABLE_NAME_ACCOUNTS + " SET isDefault = 0 WHERE pmd != old.pmd and isDefault = 1; END;";
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
	            ACCOUNT_NAME + " TEXT, " +
	            ACCOUNT_PHONES  + " TEXT, " +
	            ACCOUNT_HAS_PHOTO + " INTEGER NOT NULL DEFAULT 0, " +
	            CONTACT_DATE + " TEXT" +
	            ");");
	  createTriggerForAccountTable(sqLiteDatabase);
  }
  
  private void createHomeTable(SQLiteDatabase sqLiteDatabase) {
	  sqLiteDatabase.execSQL(
	            "CREATE TABLE " + TABLE_NAME_HOMES + " (" +
	            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	            REF_ACCOUNT_ID + " TEXT, " +
	            HOME_WHERE + " TEXT, " +
	            HOME_NAME + " TEXT, " +
	            HOME_DEFAULT + " INTEGER NOT NULL DEFAULT 1, " +
	            CONTACT_DATE + " TEXT" +
	            ");");
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
