package com.nulldreams.base.content;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by gaoyunfei on 2017/2/8.
 */

public class It extends Intent {

    public static It newInstance () {
        return new It();
    }

    @Override
    public It setAction(String action) {
        super.setAction(action);
        return this;
    }

    @Override
    public It setData(Uri data) {
        super.setData(data);
        return this;
    }

    @Override
    public It setDataAndNormalize(Uri data) {
        super.setDataAndNormalize(data);
        return this;
    }

    @Override
    public It setType(String type) {
        super.setType(type);
        return this;
    }

    @Override
    public It setTypeAndNormalize(String type) {
        super.setTypeAndNormalize(type);
        return this;
    }

    @Override
    public It setDataAndType(Uri data, String type) {
        super.setDataAndType(data, type);
        return this;
    }

    @Override
    public It setDataAndTypeAndNormalize(Uri data, String type) {
        super.setDataAndTypeAndNormalize(data, type);
        return this;
    }

    @Override
    public It addCategory(String category) {
        super.addCategory(category);
        return this;
    }

    @Override
    public It putExtra(String name, boolean value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, byte value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, char value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, short value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, int value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, long value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, float value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, double value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, String value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, CharSequence value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, Parcelable value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, Parcelable[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        super.putParcelableArrayListExtra(name, value);
        return this;
    }

    @Override
    public It putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        super.putIntegerArrayListExtra(name, value);
        return this;
    }

    @Override
    public It putStringArrayListExtra(String name, ArrayList<String> value) {
        super.putStringArrayListExtra(name, value);
        return this;
    }

    @Override
    public It putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        super.putCharSequenceArrayListExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, Serializable value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, boolean[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, byte[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, short[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, char[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, int[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, long[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, float[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, double[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, String[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, CharSequence[] value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtra(String name, Bundle value) {
        super.putExtra(name, value);
        return this;
    }

    @Override
    public It putExtras(Intent src) {
        super.putExtras(src);
        return this;
    }

    @Override
    public It putExtras(Bundle extras) {
        super.putExtras(extras);
        return this;
    }

    @Override
    public It replaceExtras(Intent src) {
        super.replaceExtras(src);
        return this;
    }

    @Override
    public It replaceExtras(Bundle extras) {
        super.replaceExtras(extras);
        return this;
    }

    @Override
    public It setFlags(int flags) {
        super.setFlags(flags);
        return this;
    }

    @Override
    public It addFlags(int flags) {
        super.addFlags(flags);
        return this;
    }

    @Override
    public It setPackage(String packageName) {
        super.setPackage(packageName);
        return this;
    }

    @Override
    public It setComponent(ComponentName component) {
        super.setComponent(component);
        return this;
    }

    @Override
    public It setClassName(Context packageContext, String className) {
        super.setClassName(packageContext, className);
        return this;
    }

    @Override
    public It setClassName(String packageName, String className) {
        super.setClassName(packageName, className);
        return this;
    }

    @Override
    public It setClass(Context packageContext, Class<?> cls) {
        super.setClass(packageContext, cls);
        return this;
    }

    public void startActivity (Context context) {
        context.startActivity(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startActivity (Context context, Bundle options) {
        context.startActivity(this, options);
    }

    public void startActivity (Context context, Class<? extends Activity> activityClass) {
        setClass(context, activityClass);
        context.startActivity(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startActivity (Context context, Class<? extends Activity> activityClass, Bundle options) {
        setClass(context, activityClass);
        context.startActivity(this, options);
    }

    public void startActivityForResult (Activity activity, int requestCode) {
        activity.startActivityForResult(this, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startActivityForResult (Activity activity, int requestCode, Bundle options) {
        activity.startActivityForResult(this, requestCode, options);
    }

    /*public void startActivityFromFragment(Activity activity, Fragment fragment, Intent intent, int requestCode) {
        activity.startActivityFromFragment(fragment, intent, requestCode);
    }

    public void startActivityFromFragment(Activity activity, Fragment fragment, int requestCode, @Nullable Bundle options) {
        activity.startActivityFromFragment(fragment, this, requestCode, options);
    }*/

    public boolean startActivityIfNeeded(Activity activity, int requestCode) {
        return activity.startActivityIfNeeded(this, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean startActivityIfNeeded(Activity activity, int requestCode, Bundle options) {
        return activity.startActivityIfNeeded(this, requestCode, options);
    }

    public boolean startNextMatchingActivity(Activity activity) {
        return activity.startNextMatchingActivity(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean startNextMatchingActivity(Activity activity, Bundle options) {
        return activity.startNextMatchingActivity(this, options);
    }

    public void startActivityFromChild(Activity activity, Activity child, int requestCode) {
        activity.startActivityFromChild(child, this, requestCode);
    }

    /*public void startActivityFromChild(Activity activity, Activity child, int requestCode, Bundle options) {
        activity.startActivityFromChild(child, requestCode, options);
    }

    public void startIntentSenderFromChild(Activity activity, Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        activity.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    public void startIntentSenderFromChild(Activity activity, Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        activity.startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }*/

    public void startService (Context context, Class<? extends Service> serviceClass) {
        this.setClass(context, serviceClass);
        context.startService(this);
    }

    public void bindService (Context context, Class<? extends Service> serviceClass, ServiceConnection connection, int flags) {
        this.setClass(context, serviceClass);
        context.bindService(this, connection, flags);
    }

    public void sendBroadcast (Context context) {
        context.sendBroadcast(this);
    }

    public void sendBroadcast (Context context, String receiverPermission) {
        context.sendBroadcast(this, receiverPermission);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendBroadcastAsUser (Context context, UserHandle userHandle) {
        context.sendBroadcastAsUser(this, userHandle);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendBroadcastAsUser (Context context, UserHandle userHandle, String receiverPermission) {
        context.sendBroadcastAsUser(this, userHandle, receiverPermission);
    }

    public void sendOrderedBroadcast (Context context, String receiverPermission) {
        context.sendOrderedBroadcast(this, receiverPermission);
    }

    public void sendOrderedBroadcast (Context context, @Nullable String receiverPermission, BroadcastReceiver resultReceiver,
                                      @Nullable Handler scheduler, int initialCode, @Nullable String initialData,
                                      @Nullable  Bundle initialExtras) {
        context.sendOrderedBroadcast(this, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendOrderedBroadcastAsUser (Context context, UserHandle user, @Nullable String receiverPermission, BroadcastReceiver resultReceiver,
                      @Nullable Handler scheduler, int initialCode, @Nullable String initialData,
                      @Nullable  Bundle initialExtras) {
        context.sendOrderedBroadcastAsUser(this, user, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendStickyOrderedBroadcastAsUser(Context context, UserHandle user, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        context.sendStickyOrderedBroadcastAsUser(this, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void sendStickyBroadcastAsUser(Context context, UserHandle user) {
        context.sendStickyBroadcastAsUser(this, user);
    }

    public void sendStickyOrderedBroadcast(Context context, BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        context.sendStickyOrderedBroadcast(this, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    public void sendStickyBroadcast(Context context) {
        context.sendStickyBroadcast(this);
    }
}
