//package com.downloader;
//
//import android.app.IntentService;
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import android.util.Log;
//
//import java.util.ArrayList;
//
//import static android.content.ContentValues.TAG;
//
///**
// * Created by Rahul abrol on 12/5/17.
// * <p>
// * Class @{@link DownloadService} is used to download
// * request form the user and handle request asynchronously.
// * After the completion,cancel or error in the request this
// * notify the user about the status of the request.
// */
//public class DownloadService extends IntentService {
//
//    private String path;
//
//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     */
//    public DownloadService() {
//        super("IntentService");
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//    }
//
//    @Override
//    protected void onHandleIntent(@Nullable final Intent intent) {
//        if (intent != null) {
//            Bundle bundle = intent.getBundleExtra("bundle");
//            if (bundle != null) {
//                String url = bundle.getString("url");
//                String subFolder = bundle.getString("sub_folder");
//                String mainFolder = bundle.getString("main_folder");
//                new Downloader(url, subFolder, mainFolder).start();
//            }
//        }
//    }
//}
