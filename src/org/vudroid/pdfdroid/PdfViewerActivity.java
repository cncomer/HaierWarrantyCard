package org.vudroid.pdfdroid;

import android.content.Intent;

import com.poqop.document.BaseViewerActivity;
import com.poqop.document.DecodeService;
import com.poqop.document.DecodeServiceBase;
import org.vudroid.pdfdroid.codec.PdfContext;

public class PdfViewerActivity extends BaseViewerActivity
{
    @Override
    protected DecodeService createDecodeService()
    {
        return new DecodeServiceBase(new PdfContext());
    }

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
}
