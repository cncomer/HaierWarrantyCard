package com.google.zxing.client.result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.zxing.Result;
import com.shwy.bestjoy.utils.DebugUtils;

public class HaierParser extends ResultParser{
	private static final String TAG = "HaierParser";
	public static final Pattern FIND_PATTERN = Pattern.compile("http://oid.haier.com/oid\\?ewm=(.+)");

	@Override
	public ParsedResult parse(Result theResult) {
		String rawText = theResult.getText(); 
		Matcher matcher = FIND_PATTERN.matcher(rawText);
		String param = null;
		if (matcher.find()) {
			param = matcher.group(1);
			DebugUtils.logD(TAG, "find Haier barcode " + param);
		}
		return new HaierParsedResult(rawText, param);
	}

}
