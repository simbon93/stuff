package simone.bonvicini.travalert.travalert.helper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by simone on 22/07/2017.
 */

public class MediaHelper {

    private final String TAG = getClass().getSimpleName();

    private static MediaHelper instance = null;

    private Context mContext;

    private ArrayList<HashMap<String, String>> fileList = new ArrayList<>();

    public static MediaHelper get(Context context) {

        if (instance == null) {
            instance = new MediaHelper(context);
        }

        return instance;
    }

    public MediaHelper(Context context) {

        mContext = context;
    }

    public void fetchRingtones() {

        // getPlayList("/storage/sdcard1/");
        getPlayList(Environment.getExternalStorageDirectory().getAbsolutePath());
        //getAllAudioFromDevice(mContext);
    }

    public ArrayList<HashMap<String, String>> getFileList() {

        return fileList;
    }

    private ArrayList<HashMap<String, String>> getPlayList(String rootPath) {

        Log.d(TAG, "Media fetch started from rootpath: " + rootPath);

        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {

                if (file.isDirectory()) {
                    Log.d(TAG, file.getAbsolutePath());
                }
            }

            //here you will get NPE if directory doesn't contains  any file,handle it like this.
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    if (getPlayList(file.getAbsolutePath()) != null) {
//                        fileList.addAll(getPlayList(file.getAbsolutePath()));
//                    } else {
//                        break;
//                    }
//                } else if (file.getName().endsWith(".mp3")) {
//                    HashMap<String, String> song = new HashMap<>();
//                    song.put("file_path", file.getAbsolutePath());
//                    song.put("file_name", file.getName());
//                    fileList.add(song);
//                    Log.d(TAG,"Media fetched: "+song);
//                }

            Log.d(TAG, "Media from roothpath " + rootPath + " loaded succesfully");
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }
}
