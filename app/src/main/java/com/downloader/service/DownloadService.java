package com.downloader;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Rahul abrol on 12/5/17.
 * <p>
 * Class @{@link DownloadService} is used to download
 * request form the user and handle request asynchronously.
 * After the completion,cancel or error in the request this
 * notify the user about the status of the request.
 */

public class DownloadService extends IntentService {

    private String path;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DownloadService() {
        super("IntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("bundle");
            if (bundle != null) {
                String url = bundle.getString("url");
                String subFolder = bundle.getString("sub_folder");
                String mainFolder = bundle.getString("main_folder");
//                new Downloader(url, subFolder, mainFolder).start();

                try {
                    ArrayList<Object> data = new CreateDirectory().createDirectory(subFolder, getFile(url), mainFolder);
                    if (data != null && data.size() > 1) {
                        path = data.get(1) + "";
                        Log.d("path==", path);
                        if ((boolean) data.get(0)) {
                            Log.d("thread name", Thread.currentThread().getName());
//                            start();
                            //startProgress();
                        } else {
                            Log.e(TAG, "Downloader: ----> Some Technical issue");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Method used to get the file from the url and identify the
     * type of file that is being returned.
     *
     * @param url url of the file.
     * @return Array list of string with folder names.Example
     * is when the type of the file is IMAGE then the list is
     * added with Folder name-Media/Images if VIDEO then name is
     * Media/Videos, if the FILE name is doc Format then folder
     * name is Files else Others.
     */
    private ArrayList<String> getFile(final String url) throws Exception {
        ArrayList<String> categoryList = new ArrayList<>();
        int ext1 = url.lastIndexOf(".");
        String ext = url.substring(ext1 + 1, url.length());
        switch (FileType.fromPropertyName(ext)) {
            case JPEG:
            case JPG:
            case PNG:
                categoryList.add("Media");
                categoryList.add("Images");
                break;
            case DOC:
            case DOCX:
            case HTML:
            case PDF:
            case SQL:
            case TXT:
                categoryList.add("Files");
                break;
            case MP4:
            case a3GP:
                categoryList.add("Media");
                categoryList.add("Videos");
                break;
            default:
                categoryList.add("Others");
                break;
        }
        return categoryList;
    }
}
