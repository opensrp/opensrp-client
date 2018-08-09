package util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/*
    puzz ~/projects/10000sentences (master *=) $ adb pull /sdcard/debug_info.puzz.a10000sentences.sqlite
    [100%] /sdcard/debug_info.puzz.a10000sentences.sqlite
    puzz ~/projects/10000sentences (master *=) $ sqlite3 debug_info.puzz.a10000sentences.sqlite
    SQLite version 3.14.0 2016-07-26 15:17:14
    Enter ".help" for usage hints.
    sqlite>
*/

public final class DebugUtils {

    private DebugUtils() throws Exception {
        throw new Exception();
    }

    public static void backupDatabase(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            String packageName = context.getApplicationInfo().packageName;

            if (sd.canWrite()) {
                String currentDBPath = String.format("//data//%s//databases//%s",
                        packageName, databaseName);
                String backupDBPath = String.format("drishti.db", packageName);
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static void importDatabase(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            String packageName = context.getApplicationInfo().packageName;

            if (sd.canWrite()) {
                String currentDBPath = String.format("//data//%s//databases//%s",
                        packageName, databaseName);
                String backupDBPath = String.format("drishti.db", packageName);
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            Utils.writePreference(context, "LAST_SYNC_TIMESTAMP", System.currentTimeMillis() + "");

        } catch (Exception e) {
            throw new Error(e);
        }
    }
}