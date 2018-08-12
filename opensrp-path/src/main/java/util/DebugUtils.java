package util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.path.application.VaccinatorApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            String packageName = context.getApplicationInfo().packageName;

            if (sd.canWrite()) {
//                String currentDBPath = String.format("//data//%s//databases//%s",
//                        packageName, databaseName);
                String backupDBPath = String.format("plaintext.db.onlydata.sql", packageName);
//                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

//                if (currentDB.exists()) {
//                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    importFromDatabase(VaccinatorApplication.getInstance().getRepository().getWritableDatabase(),new FileInputStream(backupDB));
//                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
//                    dst.transferFrom(src, 0, src.size());
//                    src.close();
//                    dst.close();
//                }
            }

            Utils.writePreference(context, "LAST_SYNC_TIMESTAMP", System.currentTimeMillis() + "");

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static void importFromDatabase(SQLiteDatabase db,InputStream is){
        StringBuilder createStatement = new StringBuilder();

//        db.beginTransaction();
        try {

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            /* Cache the file line by line, when the line ends with a
             * semi-colon followed by a line break (end of a SQL command),
             * execute it against the database and move on. */
            while ((line = br.readLine()) != null) {
                String lineTrimmed = line.trim();
                if (lineTrimmed.length() == 0)
                    continue;
                createStatement.append(line).append("\r\n");
                if (lineTrimmed.endsWith(";")) {
//                    Log.d(TAG, "Executing SQL: \r\n" + createStatement.toString());
                    db.execSQL(createStatement.toString());
                    createStatement.setLength(0);
                }
            }
            br.close();
        } catch (Exception e) {

            e.printStackTrace();
//            Log.e(TAG, "IOException thrown while attempting to "
//                    + "create database from " + createDbFile + ".");
            return;
        }
//        db.setTransactionSuccessful();
//        db.endTransaction();
    }
}