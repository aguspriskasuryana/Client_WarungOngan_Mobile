package com.example.gan.mywoa;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gan.mywoa.Fragment.MakananList;
import com.example.gan.mywoa.Fragment.MakananListFirst;
import com.example.gan.mywoa.Fragment.MinumanListFirst;
import com.example.gan.mywoa.Fragment.NewPaket;
import com.example.gan.mywoa.Fragment.OrderList;
import com.example.gan.mywoa.Fragment.Profil;
import com.example.gan.mywoa.Fragment.RequestList;
import com.example.gan.mywoa.Utils.Command;
import com.example.gan.mywoa.Utils.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    SharedPrefManager sharedPrefManager;


    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    Fragment fragment = null;

    private final String serverUrl = "control_token.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new NewPaket();
                Bundle args = new Bundle();
                args.putInt("PAKET_CATEGORY_ID", 1);
                fragment.setArguments(args);
                callFragment(fragment);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView textEmail = (TextView) header.findViewById(R.id.textEmail);
        TextView textFullname = (TextView) header.findViewById(R.id.textviewFullname);
        sharedPrefManager = new SharedPrefManager(this);
        textEmail.setText(sharedPrefManager.getSPEmail());
        textFullname.setText(sharedPrefManager.getSPNama());
        fragmentManager = getFragmentManager();

        try{
            fragment = new NewPaket();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 0);
            fragment.setArguments(args);
            callFragment(fragment);
        } catch (Exception e){
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fragment = new RequestList();
                Bundle args = new Bundle();
                args.putInt("PAKET_CATEGORY_ID", 0);
                fragment.setArguments(args);
                callFragment(fragment);
            }
        });

        String token= FirebaseInstanceId.getInstance().getToken();
        String tokenx= token;
        savetoken(token,sharedPrefManager.getSPUserId());

    }

    private void savetoken(String tokenx,String iduser) {
        String token = tokenx;
        String idUser = iduser;
        String command = Command.CMD_SAVE;

        AsyncDataClass asyncRequestObject = new AsyncDataClass();
        asyncRequestObject.execute(getString(R.string.url_valid) + serverUrl, command,token,idUser);

    }

    private class AsyncDataClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);

            String jsonResult = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("COMMAND", params[1]));
                nameValuePairs.add(new BasicNameValuePair("TOKEN_ID", params[2]));
                nameValuePairs.add(new BasicNameValuePair("USER_ID", params[3]));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResult;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("Resulted Value: " + result);
            if(result.equals("") || result == null){
                Toast.makeText(MainActivity.this, "Server connection failed", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject resultObject = null;
            int jsonResult = 0;
            try {
                resultObject = new JSONObject(result);
                jsonResult = resultObject.getInt("success");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                while ((rLine = br.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return answer;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//noinspection SimplifiableIfStatement

        if (id == R.id.action_logout) {
            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_SUDAH_LOGIN, false);
            startActivity(new Intent(MainActivity.this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            //finish();

        }
        if (id == R.id.action_profil) {
            fragment = new Profil();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 2);
            fragment.setArguments(args);
            callFragment(fragment);

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_meeting) {
            fragment = new NewPaket();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 1);
            fragment.setArguments(args);
            callFragment(fragment);
        } else if (id == R.id.nav_wedding) {
            fragment = new NewPaket();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 2);
            fragment.setArguments(args);
            callFragment(fragment);
        } else if (id == R.id.nav_chart) {
            fragment = new RequestList();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 2);
            fragment.setArguments(args);
            callFragment(fragment);
        } else if (id == R.id.nav_pemesanan) {
            fragment = new OrderList();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 2);
            fragment.setArguments(args);
            callFragment(fragment);
        }  else if (id == R.id.nav_makanan) {
            fragment = new MakananListFirst();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 2);
            fragment.setArguments(args);
            callFragment(fragment);
        }   else if (id == R.id.nav_minuman) {
            fragment = new MinumanListFirst();
            Bundle args = new Bundle();
            args.putInt("PAKET_CATEGORY_ID", 2);
            fragment.setArguments(args);
            callFragment(fragment);
        }   else if (id == R.id.nav_map) {

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_all) {
            fragment = new NewPaket();
            callFragment(fragment);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // untuk mengganti isi kontainer menu yang dipiih
    private void callFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }

    public static void hideKeyboard(boolean val, Activity activity) {
        View view;
        view = activity.getWindow().getCurrentFocus();
        if (val == true) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
