package com.hash.excel.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.hash.excel.helper.bean.ExcelImpl;
import com.hash.excel.helper.bean.Test;
import com.hash.excel.helper.utils.FileUtils;
import com.smile.excelhelper.ExcelUtil;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button exportButton;
    private Button openButton;
    private TextView textView;

    private AlertDialog alertDialog;
    private AlertDialog mDialog;


    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private int REQUEST_PERMISSION_CODE = 1000;


    public final String filePath = Constant.AUTO_TEST;

    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    permissions[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //授予权限
                Log.i("requestPermission:", "用户之前已经授予了权限！");
            } else {
                //未获得权限
                Log.i("requestPermission:", "未获得权限，现在申请！");
                requestPermissions(permissions
                        , REQUEST_PERMISSION_CODE);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


        exportButton = findViewById(R.id.export_button);
        exportButton.setOnClickListener(this);

        openButton = findViewById(R.id.open_button);
        openButton.setOnClickListener(this);

        textView = findViewById(R.id.textView);

        File dirs = new File(filePath);
        if (!dirs.exists()) {
            Log.e("MainAct:", "文件目录已创建");
            dirs.mkdirs();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("onPermissionsResult:", "权限" + permissions[0] + "申请成功");
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Log.i("onPermissionsResult:", "用户拒绝了权限申请");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("permission")
                        .setMessage("点击允许才可以使用我们的app哦")
                        .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        });
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    private void showDialogTipUserRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.export_button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        exportExcel();

                    }
                }).start();

                break;
            case R.id.open_button:
                FileUtils.getInstance().deleteFiles();
            default:
                break;
        }
    }

    private void openDir() {

        File file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "没有正确打开文件管理器", Toast.LENGTH_SHORT).show();
        }
    }


    private void exportExcel() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String format = simpleDateFormat.format(new Date());
        String excelFileName = format + ".xls";
        String[] title = {"姓名", "年龄", "男孩"};
        ArrayList<ExcelImpl> excels = new ArrayList<>();
        ExcelImpl excel = new ExcelImpl("cpu", "true");
        ExcelImpl excel2 = new ExcelImpl("gpu", "false");
        excel.setExtend("测试结果1");
        excel.setInfo("cpu 正常");
        excel2.setInfo("gpu 异常");
        excel2.setExtend("测试结果2");


        excels.add(excel);
        excels.add(excel2);

        //创建一个demo.xls 名为first的sheet
        final String path = filePath + File.separator + excelFileName;

        File file = new File(filePath, excelFileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            ExcelUtil.getInstance()
                    .createExcel(filePath, excelFileName)
                    .createSheet("first")
                    .close();

            //向demo.xls中插入second的sheet
            ExcelUtil.getInstance()
                    .openExcel(file)
                    .createSheet("second")
                    .close();

            //初始化first的标题
            ExcelUtil.getInstance()
                    .openExcel(file)
                    .openSheet(0, "first")
                    .format()
                    .initSheetTitle(filePath, title)
                    .close();

            //向first sheet表中写入数据
            ExcelUtil.getInstance()
                    .openExcel(file)
                    .format()
                    .openSheet(0, "first")
                    .injectData(excels)
                    .close();


            //初始化second的标题
            ExcelUtil.getInstance()
                    .openExcel(file)
                    .openSheet(0, "second")
                    .format()
                    .initSheetTitle(path, title)
                    .close();

            //向second sheet表中写入数据
            ExcelUtil.getInstance()
                    .openExcel(file)
                    .format()
                    .openSheet(0, "second")
                    .injectData(excels)
                    .close();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }

//        ExcelUtil.getInstance().writeObjListToExcel(demoBeanList, filePath, context);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.clearComposingText();
                Toast.makeText(MainActivity.this, "请前往" + path + "下查看生成的测试报告", Toast.LENGTH_SHORT).show();
                textView.setText("excel已导出至：" + path);

            }
        });

    }
}
