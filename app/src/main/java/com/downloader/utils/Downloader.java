package com.downloader;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Rahul Abrol on 12/5/17.
 */
public class Downloader extends Thread {

    private final int MESSAGE_DOWNLOAD_STARTED = 1000;
    private final int MESSAGE_DOWNLOAD_COMPLETE = 1001;
    private final int MESSAGE_UPDATE_PROGRESS_BAR = 1002;
    private final int MESSAGE_DOWNLOAD_CANCELED = 1003;
    private final int MESSAGE_CONNECTING_STARTED = 1004;
    private final int MESSAGE_ENCOUNTERED_ERROR = 1005;
    private final int DOWNLOAD_BUFFER_SIZE = 4096;

    private String downloadUrl;
    private String path;
    private Handler activityHandler;
//    private ProgressDialog progressDialog;

    /**
     * Constructor of this @{@link Downloader} class to get the url and Folder name
     * for category/type of file.
     *
     * @param url        url  of the file that is going to
     *                   be downloaded.
     * @param subFolder  folder name that is inside
     *                   the main App Folder Name.
     * @param mainFolder main folder names satrts with app name.
     */
    public Downloader(final String url, final String subFolder, final String mainFolder) {
        if (url != null) {
            downloadUrl = url;
        }
        try {
            ArrayList<Object> data = new CreateDirectory().createDirectory(subFolder, getFile(url), mainFolder);
            if (data != null && data.size() > 1) {
                path = data.get(1) + "";
                Log.d("path==", path);
                if ((boolean) data.get(0)) {
                    Log.d("thread name", Thread.currentThread().getName());
                    start();
                    //startProgress();
                } else {
                    Log.e(TAG, "Downloader: ----> Some Technical issue");
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

//        if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg") || ext.equalsIgnoreCase("png")) {
//
//
//        } else if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docx") || ext.equalsIgnoreCase("pdf") || ext.equalsIgnoreCase("sql")
//                || ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("txt")) {
//
//        } else if (ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("3gp")) {
//
//
//        } else {
//
//        }
        return categoryList;
    }

//    private void startProgress() {
//        Log.d("MSGGGG===nnnnnnn", "==========");
//        // HAndler defined to received the messages from the thread and updatre the progress.
//        activityHandler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//
//                    case MESSAGE_UPDATE_PROGRESS_BAR:
//                        if (progressDialog != null) {
//                            int currentProgress = msg.arg1;
//                            progressDialog.setProgress(currentProgress);
//                        }
//                        break;
//
//
//                    case MESSAGE_CONNECTING_STARTED:
//                        if (msg.obj != null && msg.obj instanceof String) {
//                            String url = (String) msg.obj;
//
//                            if (url.length() > 16) {
//                                String tUrl = url.substring(0, 15);
//                                tUrl += "...";
//                                url = tUrl;
//                            }
//                            String pdTitle = "Connecting...";
//                            String pdMsg = "Connected.";
//                            pdMsg += " " + url;
//
//                            dismissCurrentProgressDialog();
//                            progressDialog = new ProgressDialog(parentActivity);
//                            progressDialog.setTitle(pdTitle);
//                            progressDialog.setMessage(pdMsg);
//                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                            progressDialog.setIndeterminate(true);
//                            Message newMsg = Message.obtain(this, MESSAGE_DOWNLOAD_CANCELED);
//                            progressDialog.setCancelMessage(newMsg);
//                            progressDialog.show();
//                        }
//                        break;
//
//
//                    case MESSAGE_DOWNLOAD_STARTED:
//
//                        if (msg.obj != null && msg.obj instanceof String) {
//                            int maxValue = msg.arg1;
//                            String fileName = (String) msg.obj;
//                            String pdTitle = "Downloading...";
//                            String pdMsg = "Download.";
//                            pdMsg += " " + fileName;
//
//                            dismissCurrentProgressDialog();
//                            progressDialog = new ProgressDialog(parentActivity);
//                            progressDialog.setTitle(pdTitle);
//                            progressDialog.setMessage(pdMsg);
//                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                            progressDialog.setProgress(0);
//                            progressDialog.setMax(maxValue);
//                            // set the message to be sent when this dialog is canceled
//                            Message newMsg = Message.obtain(this, MESSAGE_DOWNLOAD_CANCELED);
//                            progressDialog.setCancelMessage(newMsg);
//                            progressDialog.setCancelable(true);
//                            progressDialog.show();
//                        }
//                        break;
//
//
//                    case MESSAGE_DOWNLOAD_COMPLETE:
//                        dismissCurrentProgressDialog();
//                        displayMessage("Download Complete");
//                        break;
//
//
//                    case MESSAGE_DOWNLOAD_CANCELED:
//                        interrupt();
//                        dismissCurrentProgressDialog();
//                        displayMessage("Download Canceled");
//                        break;
//
//                    case MESSAGE_ENCOUNTERED_ERROR:
//
//                        if (msg.obj != null && msg.obj instanceof String) {
//                            String errorMessage = (String) msg.obj;
//                            dismissCurrentProgressDialog();
//                            //displayMessage(errorMessage);
//                            displayMessage("Error");
//                        }
//                        break;
//
//                    default:
//                        break;
//                }
//            }
//        };
//    }

//    private void dismissCurrentProgressDialog() {
//        if (progressDialog != null) {
//            progressDialog.hide();
//            progressDialog.dismiss();
//            progressDialog = null;
//        }
//    }

//    private void displayMessage(final String message) {
//        if (message != null) {
//            Log.e(TAG, "displayMessage: -----------> " + message);
//            //Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void run() {
        Log.d("thread name 2", Thread.currentThread().getName());
        URL url;
        URLConnection conn;
        int fileSize, lastSlash;
        String fileName;
        BufferedInputStream inStream;
        BufferedOutputStream outStream;
        File outFile;
        FileOutputStream fileStream;
        Message msg = Message.obtain(activityHandler, MESSAGE_CONNECTING_STARTED, 0, 0, downloadUrl);
        if (activityHandler != null)
            activityHandler.sendMessage(msg);

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
            msg = Message.obtain(activityHandler, MESSAGE_DOWNLOAD_STARTED, fileSizeInKB, 0, fileName);
            activityHandler.sendMessage(msg);

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
                msg = Message.obtain(activityHandler,
                        MESSAGE_UPDATE_PROGRESS_BAR,
                        totalReadInKB, 0);
                activityHandler.sendMessage(msg);
            }

            outStream.close();
            fileStream.close();
            inStream.close();

            if (isInterrupted()) {

                outFile.delete();
            } else {

                msg = Message.obtain(activityHandler,
                        MESSAGE_DOWNLOAD_COMPLETE);
                activityHandler.sendMessage(msg);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            String errMsg = "Bad url...";
            msg = Message.obtain(activityHandler,
                    MESSAGE_ENCOUNTERED_ERROR,
                    0, 0, errMsg);
            activityHandler.sendMessage(msg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            String errMsg = "File not Found...";
            msg = Message.obtain(activityHandler,
                    MESSAGE_ENCOUNTERED_ERROR,
                    0, 0, errMsg);
            activityHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            //String errMsg = getString(R.string.error_message_general);
            msg = Message.obtain(activityHandler, MESSAGE_ENCOUNTERED_ERROR, 0, 0, "Error");
            activityHandler.sendMessage(msg);
        }
    }

//    /**
//     * Enum used to define the type of image file used in
//     * the directory creation.
//     */
//    public enum FileType {
//        JPG("jpg"), JPEG("jpeg"), PNG("png"),
//        DOC("doc"), DOCX("docx"), PDF("pdf"),
//        SQL("sql"), HTML("html"), TXT("txt"),
//        MP4("mp4"), a3GP("3gp");
//
//        private String value;
//
//        /**
//         * Constructor used to assign the value.
//         *
//         * @param value valued defined in the enum.
//         */
//        private FileType(final String value) {
//            this.value = value;
//        }
//
//        /**
//         * MEthod used to get the property of enum.
//         *
//         * @param string that we want to match.
//         * @return FileType
//         * @throws Exception execption if any.
//         */
//        static FileType fromPropertyName(final String string) throws Exception {
//            for (FileType currentType : FileType.values()) {
//                if (string.equalsIgnoreCase(currentType.getValue())) {
//                    return currentType;
//                }
//            }
//            throw new Exception("Unmatched Type: " + string);
//        }
//
//        public String getValue() {
//            return value;
//        }
//    }
}