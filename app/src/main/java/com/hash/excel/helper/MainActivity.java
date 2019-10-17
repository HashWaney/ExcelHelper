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
//                openDir();
                Test test = new Test();
                test.setAge(1);
                test.setName("hash");
                Log.e(MainActivity.class.getSimpleName(), "test:" + test.toString());

//                FileUtils.getInstance().deleteFiles();
                ArrayList<ExcelImpl> excels = new ArrayList<>();
                ExcelImpl excel = new ExcelImpl("cpu", "true");
                ExcelImpl excel2 = new ExcelImpl("gpu", "false");
                excel.setExtend("测试结果1");
                excel2.setExtend("测试结果2");

                excels.add(excel);
                excels.add(excel2);
                ArrayList<ArrayList<String>> list = new ArrayList<>();
                for (int i = 0; i < excels.size(); i++) {
                    ExcelImpl excel1 = excels.get(i);
                    Log.e(MainActivity.class.getSimpleName(), "excels:" + excel1.toString() + " [classname]:" + excel1.getClass().getSimpleName());
                    String txt = excel1.toString();
                    if (!TextUtils.isEmpty(txt)) {
                        String replaceTxt = txt.replace(excel1.getClass().getSimpleName(), "");
                        if (replaceTxt.contains("{") && replaceTxt.contains("}")) {
                            String split = replaceTxt.replace("{", "").replace("}", "");
                            String[] splitArr = split.split(",");
                            Log.e(MainActivity.class.getSimpleName(), "split:[" + split + "]" + " splitArr:[" + splitArr.toString() + "]" + " splitArr.length:[" + splitArr.length + "]");
                            ArrayList<String> arrayList = new ArrayList<>();
                            for (String str : splitArr) {
                                arrayList.add(str);

                            }
                            list.add(arrayList);
                        }
                    }

                }
                Log.e(MainActivity.class.getSimpleName(), "list:" + list + " [list.size]:" + list.size());


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
//        List<DemoBean> demoBeanList = new ArrayList<>();
//        DemoBean demoBean1 = new DemoBean("starting instant run server: is main process", 111, true);
//        DemoBean demoBean2 = new DemoBean("SSS12jlaflakflaklkwlqklfq", 122, false);
//        DemoBean demoBean3 = new DemoBean("piler allocated 5MB to compile void jxl.write.biff.SheetWriter.wri", 183, true);
//        DemoBean demoBean4 = new DemoBean("Do partial code cache collection, code=8KB, data=27KB", 134, false);
//        DemoBean demoBean5 = new DemoBean("ailed for pid 16058: Operation not permitted", 134, false);
//        DemoBean demoBean6 = new DemoBean("action=com.ubox.update.install totalCount=9 current=1result=true", 134, false);
//        DemoBean demoBean7 = new DemoBean("SSS12jlaflakflaklkwlqklfq", 134, false);
//        DemoBean demoBean8 = new DemoBean("132 16107-16123/com.ubox.install D/UboxInstall: syncInstall = true", 134, false);
//        DemoBean demoBean9 = new DemoBean(":\"\\/mnt\\/sdcard\\/Ubox\\/update\\/vbox_1.3.22.20190905_1820_aliface_release.apk\"}]", 134, false);
//        DemoBean demoBean10 = new DemoBean("SSS10", 134, false);
//        DemoBean demoBean11 = new DemoBean(".101 16107-16107/? D/UboxInstall: recevice com.ubox.insta", 134, false);
//        DemoBean demoBean12 = new DemoBean("SSS12", 134, false);
//        DemoBean demoBean13 = new DemoBean("\"install\":true,\"path\":\"\\/mnt\\/sdcard\\/Ubox\\/update\\/Alipay_IoTMaster_2.3.0.apk\"},{\"install\":true,\"path\":\"\\", 134, false);
//        DemoBean demoBean14 = new DemoBean("der referenced unknown path: /system/lib64/UboxInstall1.0.0", 134, false);
//        demoBeanList.add(demoBean1);
//        demoBeanList.add(demoBean2);
//        demoBeanList.add(demoBean3);
//        demoBeanList.add(demoBean4);
//        demoBeanList.add(demoBean5);
//        demoBeanList.add(demoBean6);
//        demoBeanList.add(demoBean7);
//        demoBeanList.add(demoBean8);
//        demoBeanList.add(demoBean9);
//        demoBeanList.add(demoBean10);
//        demoBeanList.add(demoBean11);
//        demoBeanList.add(demoBean12);
//        demoBeanList.add(demoBean13);
//        demoBeanList.add(demoBean14);

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
