package com.google.zxing.client.result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.shwy.bestjoy.utils.DebugUtils;

public class HaierParser extends ResultParser{
	private static final String TAG = "HaierParser";
	public static final Pattern FIND_PATTERN = Pattern.compile("http://oid.haier.com/oid\\?ewm=(.+)");

	@Override
	public ParsedResult parse(Result theResult) {
		String rawText = theResult.getText();
		if (TextUtils.isEmpty(rawText)) {
			return null;
		}
		if (theResult.getBarcodeFormat() == BarcodeFormat.CODE_128) {
			if (rawText.length() == 20) {
				DebugUtils.logD(TAG, "find Haier CODE_128 " + rawText);
				return new HaierParsedResult(rawText, rawText, theResult.getBarcodeFormat());
			}
		}
		Matcher matcher = FIND_PATTERN.matcher(rawText);
		String param = null;
		if (matcher.find()) {
			param = matcher.group(1);
			DebugUtils.logD(TAG, "find Haier barcode " + param + ", and length is "+ param.length());
			return new HaierParsedResult(rawText, param);
		} else {
			return null;
		}
	}

}
