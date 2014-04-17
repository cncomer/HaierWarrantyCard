package com.bestjoy.app.haierwarrantycard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import com.bestjoy.app.haierwarrantycard.R;

public class SQLiteDatabaseHelper {
	private static SQLiteDatabase database;
	public static final String DATABASE_FILENAME = "bx.db"; // 这个是DB文件名字
  	public static final String PACKAGE_NAME = "com.bestjoy.app.haierwarrantycard"; // 这个是自己项目包路径
	public static final String DATABASE_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME; // 获取存储位置地址

	public static SQLiteDatabase openDatabase(Context context) {
		try {
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			File dir = new File(DATABASE_PATH);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (!(new File(databaseFilename)).exists()) {
				InputStream is = context.getResources().openRawResource(R.raw.bx);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			}
			database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
