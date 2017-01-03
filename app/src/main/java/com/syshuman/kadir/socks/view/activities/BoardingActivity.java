package com.syshuman.kadir.socks.view.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.syshuman.kadir.socks.R;
import com.syshuman.kadir.socks.model.RestServer;
import com.syshuman.kadir.socks.model.SocksUser;
import com.syshuman.kadir.socks.view.fragments.LoginFragment;
import com.syshuman.kadir.socks.view.fragments.RegisterFragment;

public class BoardingActivity extends AppCompatActivity {

    RestServer restServer;
    Context context;
    String devId;

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding);
        this.context = getBaseContext();

        this.devId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        SharedPreferences prefs = this.getSharedPreferences("com.syshuman.kadir.socks", this.MODE_PRIVATE);
        Integer um_no = prefs.getInt("um_no", 0);
        if(um_no!=0) {
            SocksUser hcuser = new SocksUser();
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        } else {
            LoginFragment loginFragment = new LoginFragment();
            replaceFragment(loginFragment, "LoginFragment");
        }
    }

    public void replaceFragment(Fragment fragment, String TAG) {

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, TAG);
        fragmentTransaction.commit();
    }

    public void login( String uName, String uPass) {
        restServer = new RestServer(context);
        restServer.login( uName, uPass);
    }


}
