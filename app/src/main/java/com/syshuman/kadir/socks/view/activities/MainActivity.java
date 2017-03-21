package com.syshuman.kadir.socks.view.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.syshuman.kadir.socks.R;
import com.syshuman.kadir.socks.model.BluetoothLeUart;

public class MainActivity extends AppCompatActivity implements BluetoothLeUart.Callback {

    String  readStr="";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private BluetoothLeUart uart;
    private Context context;
    private FloatingActionButton btnBLE;
    private Button btnStart;
    private TextView msgBox, txtStep;
    private MediaPlayer playSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_open, R.string.navigation_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_camera) {
                    // Handle the camera action
                } else if (id == R.id.nav_gallery) {

                } else if (id == R.id.nav_slideshow) {

                } else if (id == R.id.nav_manage) {

                } else if (id == R.id.nav_share) {

                } else if (id == R.id.nav_send) {

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;

            }
        });


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not supported", Toast.LENGTH_SHORT).show();
            finish();
        }


        btnBLE = (FloatingActionButton) findViewById(R.id.btnBLE);

        playSound = MediaPlayer.create(context, R.raw.beep07);


        btnBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        txtStep = (TextView) findViewById(R.id.txtStep);
        btnStart = (Button) findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // firstSound.start();
                uart.send("1"); // Tell Arduino to read
            }
        });



        msgBox = (TextView) findViewById(R.id.msgBox);

        //messages.setMovementMethod(new ScrollingMovementMethod());
        //firstSound = MediaPlayer.create(getApplicationContext(), R.raw.beep07);
        //lastSound = MediaPlayer.create(getApplicationContext(), R.raw.beep04);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uart = new BluetoothLeUart(context);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void disableBLE() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_passive);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorGrey)));
                writeLine("BLE Disabled\n");
            }
        });
    }

    public void enableBLE() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_active);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue)));
                writeLine("BLE Enabled\n");
            }
        });

    }

    public void deviceFound() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnBLE.setImageResource(R.drawable.bt_passive);
                btnBLE.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellow)));
                writeLine("Device Found\n");
            }
        });

    }

    private void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                msgBox.setText(text);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uart.registerCallback(this);
        uart.connectFirstAvailable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
        disableBLE();
    }

    @Override
    public void onConnected(BluetoothLeUart uart) {
        Log.i("BLE", "onConnected" + uart.toString());
        enableBLE();
        writeLine("BLE Connected\n");
    }

    @Override
    public void onConnectFailed(BluetoothLeUart uart) {
        Log.i("BLE", "onConnectFailed" + uart.toString());
        disableBLE();
        writeLine("BLE Connection Failed\n");
    }

    @Override
    public void onDisconnected(BluetoothLeUart uart) {
        Log.i("BLE", "onDisconnected" + uart.toString());
       disableBLE();
        writeLine("BLE Disconnected\n");
    }

    @Override
    public void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx) {
        String msg = "" + rx.getStringValue(0);
        if (msg.indexOf('|') > 0) {
            readStr = readStr + msg;
            decode(readStr);
            readStr = "";
        } else {
            readStr = readStr + msg;
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        Log.i("BLE", "device found" + device.toString());
        deviceFound();
        writeLine("BLE Device Found\n");
    }

    @Override
    public void onDeviceInfoAvailable() {
        Log.i("BLE", "info found" + uart.toString());
        writeLine("BLE Info enabled\n");

    }

    public boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    public void decode(final String str) {
        Log.d("Data", str);
        writeLine(str);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int s = 0;
                String step = str.substring(str.indexOf('s') + 1, str.indexOf('|'));
                try {
                    s = Integer.valueOf(step);
                }catch (Exception e) {
                    s = 0;
                } // nRF51822
                txtStep.setText("Step is : " + s++);
                playSound.start();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
