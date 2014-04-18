package com.bestjoy.app.haierwarrantycard.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.shwy.bestjoy.utils.NetworkUtils;

public class InstallFileUtils {
	private static final String TAG = "InstallFileUtils";

	public static boolean installDatabaseFiles(Context context, String fileName) {
		 File file = context.getDatabasePath(fileName);
		 boolean success = true;
		 if (!file.exists() || file.isDirectory()) {
			 file.delete();
			  DebugUtils.logD(TAG, "installDatabaseFiles " + fileName);
			  file.getParentFile().mkdirs();
			  InputStream is = null;
			  FileOutputStream fos = null;
			  try {
				  is = context.getResources().getAssets().open(fileName);
				  fos = new FileOutputStream(file);
				  byte[] buffer = new byte[8192];
				  int count = 0;
				  while ((count = is.read(buffer)) > 0) {
					  fos.write(buffer, 0, count);
				  }
			  } catch (IOException e) {
					e.printStackTrace();
					success = false;
			  } finally {
				  NetworkUtils.closeInputStream(is);
				  if (fos != null) {
					  try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				  }
			  }
		 }
		 return success;
	  }
}
