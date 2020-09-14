package com.downloader;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import timber.log.Timber;

import static android.content.ContentValues.TAG;

/**
 * Created by Rahul Abrol on 12/5/17.
 */
public class Downloader extends Thread {

    public static final int MESSAGE_DOWNLOAD_STARTED = 1000;
    public static final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
    public static final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
    public static final int MESSAGE_DOWNLOAD_CANCELED = 1003;
    public static final int MESSAGE_CONNECTING_STARTED = 1004;
    public static final int MESSAGE_ENCOUNTERED_ERROR = 1005;
    public static final int DOWNLOAD_BUFFER_SIZE = 4096;

    private String downloadUrl;
    private String path;
    private Handler handler;

    /**
     * Constructor of this @{@link Downloader} class to get the url and Folder name
     * for category/type of file.
     *
     * @param url        url  of the file that is going to
     *                   be downloaded.
     * @param subFolder  folder name that is inside
     *                   the main App Folder Name.
     * @param mainFolder main folder names starts with app name.
     */
    public Downloader(@NonNull final String url, final String subFolder, final String mainFolder, Handler handler) {
        downloadUrl = url;
        this.handler = handler;
        try {
            ArrayList<Object> data = new CreateDirectory().createDirectory(subFolder, getFile(url), mainFolder);
            if (data != null && data.size() > 1) {
                path = data.get(1) + "";
                Timber.tag("path==").d(path);
                if ((boolean) data.get(0)) {
                    Timber.tag("thread name").d(Thread.currentThread().getName());
                    start();
                } else {
                    Timber.tag(TAG).e("Downloader: ----> Some Technical issue");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        String ext = url.substring(ext1 + 1);
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


    @Override
    public void run() {
        Timber.tag("thread name 2").d(Thread.currentThread().getName());
        URL url;
        URLConnection conn;
        int fileSize, lastSlash;
        String fileName;
        BufferedInputStream inStream;
        BufferedOutputStream outStream;
        File outFile;
        FileOutputStream fileStream;
        Message msg = Message.obtain(handler, MESSAGE_CONNECTING_STARTED, 0, 0, downloadUrl);
        if (handler != null)
            handler.sendMessage(msg);
        try {
            url = new URL(downloadUrl);
            conn = url.openConnection();
            conn.setUseCaches(false);
            fileSize = conn.getContentLength();
            lastSlash = url.toString().lastIndexOf('/');
            fileName = "file.bin";
            if (lastSlash >= 0) {
                fileName = url.toString().substring(lastSlash + 1);
            }
            if (fileName.equals("")) {
                fileName = "file.bin";
            }

            int fileSizeInKB = fileSize / 1024;
            msg = Message.obtain(handler, MESSAGE_DOWNLOAD_STARTED, fileSizeInKB, 0, fileName);
            handler.sendMessage(msg);

            // start download
            inStream = new BufferedInputStream(conn.getInputStream());
            outFile = new File(path + "/" + fileName);
            fileStream = new FileOutputStream(outFile);
            outStream = new BufferedOutputStream(fileStream, DOWNLOAD_BUFFER_SIZE);
            byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
            int bytesRead = 0, totalRead = 0;
            while (!isInterrupted() && (bytesRead = inStream.read(data, 0, data.length)) >= 0) {
                outStream.write(data, 0, bytesRead);

                // update progress bar
                totalRead += bytesRead;
                int totalReadInKB = totalRead / 1024;
                msg = Message.obtain(handler,
                        MESSAGE_UPDATE_PROGRESS_BAR,
                        totalReadInKB, 0);
                handler.sendMessage(msg);
            }
            outStream.close();
            fileStream.close();
            inStream.close();

            if (isInterrupted()) {
                outFile.delete();
            } else {
                msg = Message.obtain(handler,
                        MESSAGE_DOWNLOAD_COMPLETE);
                handler.sendMessage(msg);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String errMsg = "Bad url...";
            msg = Message.obtain(handler,
                    MESSAGE_ENCOUNTERED_ERROR,
                    0, 0, errMsg);
            handler.sendMessage(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String errMsg = "File not Found...";
            msg = Message.obtain(handler,
                    MESSAGE_ENCOUNTERED_ERROR,
                    0, 0, errMsg);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            //String errMsg = getString(R.string.error_message_general);
            msg = Message.obtain(handler, MESSAGE_ENCOUNTERED_ERROR, 0, 0, "Error");
            handler.sendMessage(msg);
        }
    }
}