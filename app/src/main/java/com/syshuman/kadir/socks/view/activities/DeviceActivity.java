package com.syshuman.kadir.socks.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.syshuman.kadir.socks.R;
import com.syshuman.kadir.socks.model.BluetoothLeUart;

public class DeviceActivity extends Activity implements BluetoothLeUart.Callback {

    public TextView messages;
    public TextView txtStep;
    private TextView tMessage;
    private String readStr = "";
    private Button btnStart;
    private ImageView imgConnect;

    private BluetoothLeUart uart;
    private MediaPlayer firstSound, lastSound;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private void writeLine(final CharSequence text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages.append(text);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

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



        //tMessage = (TextView) findViewById(R.id.meessages);
        txtStep = (TextView) findViewById(R.id.txtStep);

        messages = (TextView) findViewById(R.id.msgBox);


        messages.setText("Started..!!!!!!!.\n");
        messages.setMovementMethod(new ScrollingMovementMethod());
        firstSound = MediaPlayer.create(getApplicationContext(), R.raw.beep07);
        lastSound = MediaPlayer.create(getApplicationContext(), R.raw.beep04);
        //imgConnect = (ImageView) findViewById(R.id.imgBT);

        btnStart = (Button) findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                firstSound.start();
                uart.send("1"); // Tell Arduino to read
            }
        });



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uart = new BluetoothLeUart(getApplicationContext());
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        writeLine("\nScanning for device... ");
        uart.registerCallback(this);
        uart.connectFirstAvailable();
        imgConnect.setBackgroundResource(R.drawable.bt_passive);
        tMessage.setText("Scanning....");
    }

    // OnStop, called right before the activity loses foreground fopublic void onConneccus.  Close the BTLE connection.
    @Override
    protected void onStop() {
        super.onStop();
        uart.unregisterCallback(this);
        uart.disconnect();
        writeLine("\nStopped..");
        imgConnect.setBackgroundResource(R.drawable.bt_passive);
        tMessage.setText("Stopped....");
    }


    @Override
    public void onConnected(BluetoothLeUart uart) {
        writeLine("\nConnected : ");
        btnStart.setClickable(true);
        btnStart.setEnabled(false);
        imgConnect.setBackgroundResource(R.drawable.bt_active);
        tMessage.setText("Connected...");
    }

    @Override
    public void onConnectFailed(BluetoothLeUart uart) {
        writeLine("\nError connecting to device ! ");
        btnStart.setClickable(false);
        btnStart.setEnabled(false);
        imgConnect.setBackgroundResource(R.drawable.bt_passive);
        tMessage.setText("Error connecting the device,..");

    }

    @Override
    public void onDisconnected(BluetoothLeUart uart) {
        writeLine("\nDisconnected!");
        btnStart.setClickable(false);
        btnStart.setEnabled(false);
        imgConnect.setBackgroundResource(R.drawable.bt_passive);
        tMessage.setText("Disconnecting....");

    }

    @Override
    public void onReceive(BluetoothLeUart uart, BluetoothGattCharacteristic rx) {
        String msg = "" + rx.getStringValue(0);
        Log.d("TAG", msg);
        tMessage.setText(msg);

        if (msg.indexOf('|') > 0) {
            readStr = readStr + msg;
            writeLine("\nReceived : " + readStr + '\r');
            decode(readStr);
            readStr = "";
        } else {
            readStr = readStr + msg;
        }
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        // Called when a UART device is discovered (after calling startScan).
        Log.d("Test", device.toString());
        writeLine("\nFound device : " + device.toString());
        writeLine("\nWaiting for a connection....");
        imgConnect.setBackgroundResource(R.drawable.bt_passive);
        tMessage.setText("Device Found : "+device.toString());
    }

    @Override
    public void onDeviceInfoAvailable() {

        writeLine(uart.getDeviceInfo());
    }

    public void decode(final String str) {
        Log.d("Data", str);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String step = str.substring(str.indexOf('l') + 1, str.indexOf('|'));
                lastSound.start();
                //String res =  getResult(Integer.parseInt(Red), Integer.parseInt(Gre), Integer.parseInt(Blu), Integer.parseInt(Cle), Integer.parseInt(Tem), Integer.parseInt(Lum) );
                txtStep.setText(step.toString());
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
