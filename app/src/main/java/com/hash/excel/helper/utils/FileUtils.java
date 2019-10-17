package com.hash.excel.helper.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by HashWaney on 2019/10/16.
 */

public class FileUtils {

    public static final class SingletonHolder {
        private static final FileUtils INSTANCE = new FileUtils();

        public SingletonHolder() {
        }
    }


    public static FileUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void deleteFiles() {
        String path = Environment.getExternalStorageDirectory() + "/ubox";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }


        DeleteFilter deleteFilter = new DeleteFilter(".xls");
        File[] files = file.listFiles(deleteFilter);
        Log.e(FileUtils.getInstance().getClass().getSimpleName(), "files:" + files.length);
        if (files != null && files.length > 0) {
            for (File listFile : files) {
                if (listFile.isFile() && listFile.exists()) {
                    boolean delete = listFile.delete();
                    Log.e(FileUtils.getInstance().getClass().getSimpleName(), "删除成功:" + delete);
                }


            }
        }
    }

    class DeleteFilter implements FilenameFilter {

        private String prefix;

        public DeleteFilter(String prefix) {
            this.prefix = prefix;

        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(prefix);
        }
    }
}
