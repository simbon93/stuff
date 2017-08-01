package simone.bonvicini.travalert.travalert.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;

import simone.bonvicini.travalert.travalert.R;

/**
 * Created by simone on 22/12/16.
 */

public class DialogHelper {

    public static void showSingleChoiceDialog(Context context, String title, String[] items, int checkedItem, DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        builder.setTitle(title);

        builder.setSingleChoiceItems(items, checkedItem, listener);

        Dialog dialog = builder.create();
        dialog.show();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setLayout(((int) (0.9 * width)), ActionBar.LayoutParams.WRAP_CONTENT);
    }

    public static void showSingleChoiceDialogWithButton(Context context, String title, String positiveAction, String neutralAction
            , String[] items, int checkedItem, DialogInterface.OnClickListener buttonListener
            , DialogInterface.OnClickListener itemClickListener ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        builder.setTitle(title);
        builder.setPositiveButton(positiveAction, buttonListener);
        builder.setNeutralButton(neutralAction, buttonListener);
        builder.setSingleChoiceItems(items, checkedItem, itemClickListener);

        Dialog dialog = builder.create();
        dialog.show();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setLayout(((int) (0.9 * width)), ActionBar.LayoutParams.WRAP_CONTENT);
    }

    public static void showSingleChoiceDialog(Context context, String title, String[] items, int checkedItem, DialogInterface.OnClickListener listener, DialogInterface.OnDismissListener dismissListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialog);

        builder.setTitle(title);

        builder.setSingleChoiceItems(items, checkedItem, listener);

        Dialog dialog = builder.create();
        dialog.setOnDismissListener(dismissListener);
        dialog.show();

        int width = context.getResources().getDisplayMetrics().widthPixels;
        dialog.getWindow().setLayout(((int) (0.9 * width)), ActionBar.LayoutParams.WRAP_CONTENT);
    }

    public static void showDialog(Activity activity, String message, DialogInterface.OnClickListener listener) {

        showDialog(activity, activity.getString(R.string.app_name), message, activity.getString(R.string.ok), (String) null, listener);
    }

    public static void showDialog(Activity activity, String message, String positiveText, String negativeText, DialogInterface.OnClickListener listener) {

        showDialog(activity, activity.getString(R.string.app_name), message, positiveText, negativeText, listener);
    }

    public static void showDialog(Activity activity, String title, String content, String positiveText, String negativeText, DialogInterface.OnClickListener listener) {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity, R.style.CustomDialog);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveText, listener);
        if (negativeText != null) {
            builder.setNegativeButton(negativeText, listener);
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                builder.create().show();
            }
        });
    }
}
