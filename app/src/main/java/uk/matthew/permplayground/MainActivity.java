package uk.matthew.permplayground;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PermissionLab. An educational playground for Android's runtime permission
 * system. Every button asks the system for one permission so the behaviour of
 * dialogs, grants and denials can be studied. The app only ever ASKS: it never
 * uses anything it is granted, collects nothing, has no INTERNET permission
 * and is not affiliated with any vendor.
 */
public class MainActivity extends AppCompatActivity {

    private static class Spec {
        final String label;
        final String permission;
        final int minApi;
        Spec(String label, String permission, int minApi) {
            this.label = label;
            this.permission = permission;
            this.minApi = minApi;
        }
    }

    private final List<Spec> specs = new ArrayList<>();
    private TextView statusText;
    private View root;
    private int requestSeq = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        statusText = findViewById(R.id.status);
        root = findViewById(R.id.root);
        GridLayout grid = findViewById(R.id.grid);

        addSpecs();

        for (final Spec spec : specs) {
            final com.google.android.material.button.MaterialButton b =
                    new com.google.android.material.button.MaterialButton(this);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.width = 0;
            int m = dp(4);
            lp.setMargins(m, m, m, m);
            b.setLayoutParams(lp);
            b.setAllCaps(false);
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
            b.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { requestOne(spec); }
            });
            b.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) { openAppInfo(); return true; }
            });
            grid.addView(b);
            labelToButton.put(spec.permission, b);
        }

        addSpecialButtons(grid);
        refresh();
    }

    private final Map<String, com.google.android.material.button.MaterialButton> labelToButton = new HashMap<>();

    private void addSpecs() {
        specs.add(new Spec("Camera", Manifest.permission.CAMERA, 23));
        specs.add(new Spec("Microphone", Manifest.permission.RECORD_AUDIO, 23));
        specs.add(new Spec("Location (fine)", Manifest.permission.ACCESS_FINE_LOCATION, 23));
        specs.add(new Spec("Location (coarse)", Manifest.permission.ACCESS_COARSE_LOCATION, 23));
        specs.add(new Spec("Location (background)", Manifest.permission.ACCESS_BACKGROUND_LOCATION, 29));
        specs.add(new Spec("Contacts (read)", Manifest.permission.READ_CONTACTS, 23));
        specs.add(new Spec("Contacts (write)", Manifest.permission.WRITE_CONTACTS, 23));
        specs.add(new Spec("Calendar (read)", Manifest.permission.READ_CALENDAR, 23));
        specs.add(new Spec("Calendar (write)", Manifest.permission.WRITE_CALENDAR, 23));
        specs.add(new Spec("Accounts", Manifest.permission.GET_ACCOUNTS, 23));
        specs.add(new Spec("Phone state", Manifest.permission.READ_PHONE_STATE, 23));
        specs.add(new Spec("Phone number", Manifest.permission.READ_PHONE_NUMBERS, 30));
        specs.add(new Spec("Call phone", Manifest.permission.CALL_PHONE, 23));
        specs.add(new Spec("Answer calls", Manifest.permission.ANSWER_PHONE_CALLS, 26));
        specs.add(new Spec("Call log (read)", Manifest.permission.READ_CALL_LOG, 23));
        specs.add(new Spec("Call log (write)", Manifest.permission.WRITE_CALL_LOG, 23));
        specs.add(new Spec("Add voicemail", Manifest.permission.ADD_VOICEMAIL, 23));
        specs.add(new Spec("Use SIP", Manifest.permission.USE_SIP, 23));
        specs.add(new Spec("Outgoing calls (legacy)", Manifest.permission.PROCESS_OUTGOING_CALLS, 24));
        specs.add(new Spec("Body sensors", Manifest.permission.BODY_SENSORS, 23));
        specs.add(new Spec("Activity recognition", Manifest.permission.ACTIVITY_RECOGNITION, 29));
        specs.add(new Spec("Accept handover", Manifest.permission.ACCEPT_HANDOVER, 26));
        specs.add(new Spec("SMS (send)", Manifest.permission.SEND_SMS, 23));
        specs.add(new Spec("SMS (receive)", Manifest.permission.RECEIVE_SMS, 23));
        specs.add(new Spec("SMS (read)", Manifest.permission.READ_SMS, 23));
        specs.add(new Spec("MMS (receive)", Manifest.permission.RECEIVE_MMS, 23));
        specs.add(new Spec("WAP push", Manifest.permission.RECEIVE_WAP_PUSH, 23));
        specs.add(new Spec("Storage (read, legacy)", Manifest.permission.READ_EXTERNAL_STORAGE, 23));
        specs.add(new Spec("Storage (write, legacy)", Manifest.permission.WRITE_EXTERNAL_STORAGE, 23));
        specs.add(new Spec("Media location", Manifest.permission.ACCESS_MEDIA_LOCATION, 29));
        specs.add(new Spec("Media: photos", Manifest.permission.READ_MEDIA_IMAGES, 33));
        specs.add(new Spec("Media: videos", Manifest.permission.READ_MEDIA_VIDEO, 33));
        specs.add(new Spec("Media: audio", Manifest.permission.READ_MEDIA_AUDIO, 33));
        specs.add(new Spec("Media: selected photos", Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, 34));
        specs.add(new Spec("Bluetooth (scan)", Manifest.permission.BLUETOOTH_SCAN, 31));
        specs.add(new Spec("Bluetooth (connect)", Manifest.permission.BLUETOOTH_CONNECT, 31));
        specs.add(new Spec("Bluetooth (advertise)", Manifest.permission.BLUETOOTH_ADVERTISE, 31));
        specs.add(new Spec("Nearby WiFi devices", Manifest.permission.NEARBY_WIFI_DEVICES, 33));
        specs.add(new Spec("UWB ranging", Manifest.permission.UWB_RANGING, 31));
        specs.add(new Spec("Notifications", Manifest.permission.POST_NOTIFICATIONS, 33));
    }

    /** Two permissions Android never grants through a dialog: they get Settings pages. */
    private void addSpecialButtons(GridLayout grid) {
        addSpecial(grid, "All files access (special)", 30, new Runnable() {
            @Override public void run() {
                try {
                    startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.fromParts("package", getPackageName(), null)));
                } catch (Exception e) {
                    try {
                        startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
                    } catch (Exception ignored) { toast("Not available on this device"); }
                }
            }
        });
        addSpecial(grid, "Exact alarms (special)", 31, new Runnable() {
            @Override public void run() {
                try {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.fromParts("package", getPackageName(), null)));
                } catch (Exception e) { toast("Not available on this device"); }
            }
        });
    }

    private void addSpecial(GridLayout grid, String label, int minApi, final Runnable action) {
        com.google.android.material.button.MaterialButton b =
                new com.google.android.material.button.MaterialButton(this);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.width = 0;
        int m = dp(4);
        lp.setMargins(m, m, m, m);
        b.setLayoutParams(lp);
        b.setAllCaps(false);
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f);
        b.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (Build.VERSION.SDK_INT < minApi) { toast("Needs a newer Android version"); return; }
                action.run();
            }
        });
        grid.addView(b);
        labelToButton.put(label, b);
    }

    private void requestOne(Spec spec) {
        if (Build.VERSION.SDK_INT < spec.minApi) {
            toast(spec.label + " needs Android API " + spec.minApi + "+");
            return;
        }
        if (ContextCompat.checkSelfPermission(this, spec.permission)
                == PackageManager.PERMISSION_GRANTED) {
            refresh();
            toast(spec.label + " is already granted");
            return;
        }
        if (Manifest.permission.ACCESS_BACKGROUND_LOCATION.equals(spec.permission)
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            toast("Grant fine location first, then background");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestSeq++);
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{spec.permission}, requestSeq++);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int grantedNow = 0;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) grantedNow++;
        }
        refresh();
        if (grantResults.length > 0 && grantResults[grantResults.length - 1]
                == PackageManager.PERMISSION_GRANTED) {
            toast("Granted " + grantedNow + " of " + permissions.length);
        } else {
            toast("Denied. Deny twice and the system stops asking; long-press to reset in App info");
        }
    }

    private void refresh() {
        int granted = 0;
        for (Spec spec : specs) {
            com.google.android.material.button.MaterialButton b = labelToButton.get(spec.permission);
            if (b == null) continue;
            if (Build.VERSION.SDK_INT < spec.minApi) {
                b.setEnabled(false);
                b.setText(spec.label + " · API " + spec.minApi + "+");
                b.setBackgroundTintList(ColorStateList.valueOf(0xFF4A4A4A));
                b.setTextColor(ColorStateList.valueOf(0xFFB0B0B0));
                continue;
            }
            boolean g = ContextCompat.checkSelfPermission(this, spec.permission)
                    == PackageManager.PERMISSION_GRANTED;
            if (g) {
                granted++;
                b.setEnabled(true);
                b.setText(spec.label + " ✓");
                b.setBackgroundTintList(ColorStateList.valueOf(0xFF2E7D32));
                b.setTextColor(ColorStateList.valueOf(0xFFFFFFFF));
            } else {
                b.setEnabled(true);
                b.setText(spec.label);
                b.setBackgroundTintList(null);
                b.setTextColor(themedColor(com.google.android.material.R.attr.colorOnSurface));
            }
        }
        statusText.setText("Granted " + granted + " of " + specs.size()
                + " · tap to ask · long-press for App info");
    }

    private void openAppInfo() {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null)));
        } catch (Exception e) { toast("Could not open App info"); }
    }

    private int themedColor(int attr) {
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(attr, tv, true);
        return tv.data;
    }

    private int dp(int v) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
                getResources().getDisplayMetrics());
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.about_title)
                    .setMessage(R.string.about_body)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else if (id == R.id.action_how) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.how_title)
                    .setMessage(R.string.how_body)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else if (id == R.id.action_refresh) {
            refresh();
            return true;
        } else if (id == R.id.action_appinfo) {
            openAppInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
