package simone.bonvicini.travalert.travalert.services;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by simone on 03/06/2017.
 */

public class AndroidStore {

    private static final String TAG = AndroidStore.class.getSimpleName();

    private final Context context;

    public AndroidStore(Context context) {

        this.context = context;
    }

    public InputStream loadStream(String key, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        Log.d(TAG, "loadObject file: " + file.getAbsolutePath());
        if (!file.exists()) {
            return null;
        } else {
            FileInputStream is = null;

            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException var6) {
                var6.printStackTrace();
            }

            return is;
        }
    }

    public Object loadObject(String key, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        Log.d(TAG, "loadObject file: " + file.getAbsolutePath());
        if (!file.exists()) {
            return null;
        } else {
            Object bytes = null;
            Object obj = null;

            try {
                long e = file.length();
                byte[] bytes1 = new byte[(int) e];
                FileInputStream is = new FileInputStream(file);
                is.read(bytes1);
                is.close();
                obj = this.getObject(bytes1);
            } catch (FileNotFoundException var9) {
                var9.printStackTrace();
            } catch (IOException var10) {
                var10.printStackTrace();
            }

            return obj;
        }
    }

    public void storeObject(String key, Object value, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        file.delete();

        try {
            file.createNewFile();
            FileOutputStream e = new FileOutputStream(file);
            e.write(this.getBytes(value));
            e.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void removeObject(String key, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        file.delete();
    }

    private byte[] getBytes(Object obj) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        byte[] data = bos.toByteArray();
        return data;
    }

    private Object getObject(byte[] bytes) throws IOException {

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(bis);
        Object obj = null;

        try {
            obj = in.readObject();
        } catch (ClassNotFoundException var6) {
            var6.printStackTrace();
        }

        bis.close();
        in.close();
        return obj;
    }

    public void assureParentDirs(String path, AndroidStore.StoreType storeType) {

        (new File(this.getDirFromType(storeType), path)).mkdirs();
    }

    public String getFolderPath(String path, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), path);
        return file.getAbsolutePath();
    }

    public String getFilePath(String key, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        return !file.exists() ? null : file.getAbsolutePath();
    }

    public byte[] loadFile(String key, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        if (!file.exists()) {
            return null;
        } else {
            byte[] bytes = null;

            try {
                long e = file.length();
                bytes = new byte[(int) e];
                FileInputStream is = new FileInputStream(file);
                is.read(bytes);
                is.close();
            } catch (FileNotFoundException var8) {
                var8.printStackTrace();
            } catch (IOException var9) {
                var9.printStackTrace();
            }

            return bytes;
        }
    }

    public void storeFile(String key, byte[] value, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        file.delete();

        try {
            file.createNewFile();
            FileOutputStream e = new FileOutputStream(file);
            e.write(value);
            e.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void appendToFile(String key, byte[] value, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream e = new FileOutputStream(file, true);
            e.write(value);
            e.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public void removeFile(String key, AndroidStore.StoreType storeType) {

        File file = new File(this.getDirFromType(storeType), key);
        file.delete();
    }

    public File getDirFromType(AndroidStore.StoreType storeType) {

        File dir = null;
        switch (storeType) {
            case Persistent:
                dir = this.context.getFilesDir();
                break;
            case PersistentExternal:
                dir = this.context.getExternalFilesDir((String) null);
                if (dir == null) {
                    dir = this.context.getFilesDir();
                }
                break;
            case Cache:
                dir = this.context.getCacheDir();
        }

        return dir;
    }

    public void removeAll(AndroidStore.StoreType storeType) {

        File dir = this.getDirFromType(storeType);
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; ++i) {
                files[i].delete();
            }
        }

    }

    public static enum StoreType {
        Persistent,
        PersistentExternal,
        Cache;

        private StoreType() {

        }
    }

}
