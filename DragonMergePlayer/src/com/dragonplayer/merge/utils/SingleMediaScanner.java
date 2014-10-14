package com.dragonplayer.merge.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import java.io.File;

public class SingleMediaScanner
    implements android.media.MediaScannerConnection.MediaScannerConnectionClient {

    private File mFile;
    private MediaScannerConnection mMs;

    public SingleMediaScanner(Context context, File file) {
        mFile = file;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    public void onScanCompleted(String s, Uri uri) {
        mMs.disconnect();
    }
}
