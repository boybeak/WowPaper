package com.nulldreams.base;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gaoyunfei on 16/8/28.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static final String ACTION_CRASH_CAUGHT = "com.nulldreams.action.ACTION_CRASH_CAUGHT";

    private static CrashHandler sHandler = null;
    public static synchronized CrashHandler getInstance (Context context) {
        if (sHandler == null) {
            sHandler = new CrashHandler(context.getApplicationContext());
        }
        return sHandler;
    }

    private Context mContext;

    private CrashHandler(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        throwable.printStackTrace();

        File crashLogFile = new File(mContext.getExternalCacheDir(),
                "crash" + File.separator + DATE_FORMAT.format(new Date()) + ".crash");
        if (!crashLogFile.getParentFile().exists()) {
            crashLogFile.getParentFile().mkdirs();
        }
        try {
            FileWriter fileWriter = new FileWriter(crashLogFile);
            PrintWriter writer = new PrintWriter(fileWriter);

            throwable.printStackTrace(writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.exit(1);
        }

        /*Intent it = new Intent();
        it.setAction(ACTION_CRASH_CAUGHT);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(it);*/
    }
}
