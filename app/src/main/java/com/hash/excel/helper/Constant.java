package com.hash.excel.helper;

import android.os.Environment;

/**
 * Created by HashWaney on 2019/10/16.
 */

public interface Constant {
    String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    String UBox_PATH = SD_PATH + "/ubox";
    String AUTO_TEST = UBox_PATH + "/AutoTest";
}
