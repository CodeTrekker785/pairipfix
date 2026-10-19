package io.github.ahmedmani.pairfix;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Opens Android's built-in package installer for a user-selected APK.
 * Android displays the confirmation UI; this app does not silently install packages.
 */
public class MainActivity extends Activity {
    private static final int PICK_APK = 4102;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setTitle("Package Installer");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("Package Installer");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView info = new TextView(this);
        info.setText("Select an APK file to install it using Android's system package installer. Review the app and confirm installation on the next screen. You may need to allow this app to install unknown apps in Settings.");
        info.setTextSize(16);
        info.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoParams.topMargin = pad;
        root.addView(info, infoParams);

        Button choose = new Button(this);
        choose.setText("Select APK");
        choose.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pick.addCategory(Intent.CATEGORY_OPENABLE);
            pick.setType("application/vnd.android.package-archive");
            pick.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivityForResult(pick, PICK_APK);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open file picker: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.topMargin = pad;
        root.addView(choose, buttonParams);
        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_APK || resultCode != RESULT_OK || data == null) return;
        Uri apkUri = data.getData();
        if (apkUri == null) {
            Toast.makeText(this, "No APK was selected.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(install);
        } catch (Exception e) {
            Toast.makeText(this, "Android could not open the installer: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
