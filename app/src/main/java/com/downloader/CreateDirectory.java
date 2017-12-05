package com.downloader;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rahul Abrol on 12/5/17.
 * <p>
 * Class @{@link CreateDirectory} used to create
 * folder into the external of internal SD Card
 * on the mobile device.
 */
public class CreateDirectory {

    private String path = "";

    /**
     * Method used to create the directory into the
     * mobile device with given folder name and category
     * that defines the type of data that is going to be stored into the folder
     * like images,videos,documents etc.
     *
     * @param folderName name of the folder.
     * @param category   type of data.
     * @param mainFolder main folder name that starts with app name.
     * @return list of objects.
     */
    public ArrayList<Object> createDirectory(final String folderName,
                                             final ArrayList<String> category, final String mainFolder) {
        ArrayList<Object> data = new ArrayList<>();
        boolean bol = true;
        try {

            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + mainFolder);
            if (!folder.exists()) {

                bol = folder.mkdir();
                path = folder.getAbsolutePath();


            }
            if (bol) {
                File folder1 = new File(Environment.getExternalStorageDirectory() + File.separator + mainFolder + File.separator + folderName);

                if (folder1.exists()) {
                    Log.d("inside if1", "if1");
                    path = folder1.getAbsolutePath();
                    if (category.size() > 0) {
                        bol = createCategory(folderName, category, mainFolder);
                    }
                } else {
                    Log.d("inside else", "if1");
                    bol = folder1.mkdir();
                    if (bol) {
                        path = folder1.getAbsolutePath();
                        if (category.size() > 0) {
                            bol = createCategory(folderName, category, mainFolder);
                        }
                    } else {
                        bol = false;
                    }
                }
            }
            if (!bol) {
                bol = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        data.add(bol);
        data.add(path);
        return data;
    }

    /**
     * Method used internally in this @{@link CreateDirectory} file to create the
     * folder into the mobile device.
     *
     * @param rootFolder root folder(main folder that is with aap name) that contains the data.
     * @param arrayList  list of data.
     * @param mainFolder name of the main folder.
     * @return true is created else false.
     */
    private boolean createCategory(String rootFolder, ArrayList arrayList, String mainFolder) {
        boolean isCreated = true;
        String allFolder = "";
        for (int x = 0; x < arrayList.size(); x++) {
            allFolder = allFolder + File.separator + arrayList.get(x) + File.separator;
        }
        int lastIndex = allFolder.lastIndexOf("/");
        String finalPath = allFolder.substring(0, lastIndex);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator
                + mainFolder + File.separator + rootFolder + File.separator + finalPath);

        if (folder.exists()) {
            path = folder.getAbsolutePath();
        } else {
            isCreated = true;
            isCreated = folder.mkdirs();
            path = folder.getAbsolutePath();
        }
        if (!isCreated) {
            isCreated = false;
        }

        return isCreated;
    }
}
