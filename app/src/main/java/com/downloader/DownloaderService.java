package com.downloader;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Rahul Abrol on 10/9/20.
 */
public class DownloaderService extends Service {
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    private NotificationManager mNM;
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, LocalServiceActivities.Controller.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_download)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
//                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public DownloaderService getService() {
            return DownloaderService.this;
        }
    }
}
/*

    private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";
    public static boolean serviceState = false;
    SharedPreferences preferences;
    String downloadUrl;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private NotificationManager mNM;

    @Override
    public void onCreate() {
        serviceState = true;
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 1);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("onStartCommand");

        Bundle extra = intent.getExtras();
        if (extra != null) {
            String downloadUrl = extra.getString("downloadUrl");
            Timber.tag("URL").d(downloadUrl);

            this.downloadUrl = downloadUrl;
        }

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("DESTROY");
        serviceState = false;
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    public void downloadFile() {
        downloadFile(this.downloadUrl, fileName);
    }

    void showNotification(String message, String title) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = message;

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_download, "vvs",
                System.currentTimeMillis());
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Intent intent = new Intent(this, DownloadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this.getBaseContext(), 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, title, text, contentIntent);
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.app_name, notification);
    }

    public void downloadFile(String fileURL, String fileName) {

        StatFs stat_fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double avail_sd_space = (double) stat_fs.getAvailableBlocks() * (double) stat_fs.getBlockSize();
        //double GB_Available = (avail_sd_space / 1073741824);
        double MB_Available = (avail_sd_space / 10485783);
        //System.out.println("Available MB : " + MB_Available);
        Timber.d("" + MB_Available);
        try {
            File root = new File(Environment.getExternalStorageDirectory() + "/vvveksperten");
            if (root.exists() && root.isDirectory()) {

            } else {
                root.mkdir();
            }
            Timber.d(root.getPath());
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            int fileSize = c.getContentLength() / 1048576;
            Timber.d("" + fileSize);
            if (MB_Available <= fileSize) {
                this.showNotification(getResources().getString(R.string.notification_no_memory),
                        getResources().getString(R.string.notification_error));
                c.disconnect();
                return;
            }

            FileOutputStream f = new FileOutputStream(new File(root.getPath(), fileName));

            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
            File file = new File(root.getAbsolutePath() + "/" + "some.pdf");
            if (file.exists()) {
                file.delete();
                Timber.d("YES");
            } else {
                Timber.d("NO");
            }
            File from = new File(root.getAbsolutePath() + "/" + fileName);
            File to = new File(root.getAbsolutePath() + "/" + "some.pdf");
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            downloadFile();
            showNotification(getResources().getString(R.string.notification_catalog_downloaded), "VVS");
            stopSelf(msg.arg1);
        }
    }
}*/
