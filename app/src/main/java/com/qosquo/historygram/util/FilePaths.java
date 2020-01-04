package com.qosquo.historygram.util;

import android.os.Environment;

public class FilePaths {

    /**
     * "storage/emulated/0"
     */
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public static final String DOWNLOAD = ROOT_DIR + "/Download";
    public static final String SCREENSHOTS = ROOT_DIR + "/Screenshots";

    public static final String DCIM = ROOT_DIR + "/DCIM";
    public static final String CAMERA = DCIM + "/Camera";
}
