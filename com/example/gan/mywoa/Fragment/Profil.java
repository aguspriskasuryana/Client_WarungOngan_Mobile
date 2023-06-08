package com.example.gan.mywoa.Fragment;

/**
 * Created by GUSWIK on 7/16/2017.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gan.mywoa.LoginActivity;
import com.example.gan.mywoa.R;
import com.example.gan.mywoa.Utils.Command;
import com.example.gan.mywoa.Utils.PaketJenis;
import com.example.gan.mywoa.Utils.SharedPrefManager;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.READ_CONTACTS;

public class Profil extends Fragment {

    public Profil(){}
    View rootView;
    ArrayList<ListItem> listData = new ArrayList<ListItem>();
    ArrayAdapter adapter;
    private CustomListAdapter customListAdapter;
    ListView listView;
    String[] daftar ;
    int paketJenis=0;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    Button chkout ;
    FragmentTransaction fragmentTransaction;
    SharedPrefManager sharedPrefManager;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mFullname;
    private EditText mBirthdate;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private EditText mNoTelp;
    private View mProgressView;
    private View mLoginFormView;

    private String command;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private final String serverUrl = "control_user.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.profil, container, false);

        sharedPrefManager = new SharedPrefManager(getActivity());
        getActivity().setTitle("Profil");
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.regemail);

        mFullname = (EditText) rootView.findViewById(R.id.regfullname);
        mBirthdate = (EditText) rootView.findViewById(R.id.regtanggallahir);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        mBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDateDialog();
            }
        });
        mPasswordView = (EditText) rootView.findViewById(R.id.regpassword);
        mNoTelp = (EditText) rootView.findViewById(R.id.editTextNoTelp);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mRePasswordView = (EditText) rootView.findViewById(R.id.regrepassword);
        mRePasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mEmailView.setText(sharedPrefManager.getSPEmail());
        mFullname.setText(sharedPrefManager.getSPNama());
        mBirthdate.setText(sharedPrefManager.getSPBirthDate());
        mFullname.setText(sharedPrefManager.getSPFullName());
        mPasswordView.setText(sharedPrefManager.getSPPassword());
        mRePasswordView.setText(sharedPrefManager.getSPPassword());
        mNoTelp.setText(sharedPrefManager.getSPNoTelp());

        Button mRegisterButton = (Button) rootView.findViewById(R.id.Register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = rootView.findViewById(R.id.register_form);
        mProgressView = rootView.findViewById(R.id.register_progress);

        return rootView;
    }

    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                mBirthdate.setText("" + dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("COMMAND", params[1]));
                nameValuePairs.add(new BasicNameValuePair("EMAIL", params[2]));
                nameValuePairs.add(new BasicNameValuePair("USERNAME", params[3]));
                nameValuePairs.add(new BasicNameValuePair("FULL_NAME", params[4]));
                nameValuePairs.add(new BasicNameValuePair("BIRTH_DATE", params[5]));
                nameValuePairs.add(new BasicNameValuePair("PASSWORD", params[6]));
                nameValuePairs.add(new BasicNameValuePair("USER_ID", params[7]));
                nameValuePairs.add(new BasicNameValuePair("PHONE_NUMBER", params[8]));
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
                Toast.makeText(getActivity(), "Server connection failed", Toast.LENGTH_LONG).show();
                return;
            }
            JSONObject resultObject = null;
            int jsonResult = 0;
            int jsonEmailReady = 0;
            int jsonUsernameReady = 0;
            String queryReady = "";
            try {
                resultObject = new JSONObject(result);
                jsonResult = resultObject.getInt("success");
                jsonUsernameReady = resultObject.getInt("usernameReady");
                jsonEmailReady = resultObject.getInt("emailReady");
                queryReady = resultObject.getString("query");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonResult == 1) {
                showProgress(false);


                sharedPrefManager.saveSPString(SharedPrefManager.SP_EMAIL, mEmailView.getText().toString());
                sharedPrefManager.saveSPString(SharedPrefManager.SP_BIRTHDATE, mBirthdate.getText().toString());
                sharedPrefManager.saveSPString(SharedPrefManager.SP_FULL_NAME, mFullname.getText().toString());
                sharedPrefManager.saveSPString(SharedPrefManager.SP_PASSWORD, mFullname.getText().toString());
                sharedPrefManager.saveSPString(SharedPrefManager.SP_NOTELP, mNoTelp.getText().toString());

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("EMAIL", mEmailView.getText().toString());
                intent.putExtra("MESSAGE", "You have been successfully ");
                startActivity(intent);
            } else  if (jsonEmailReady == 1){

                showProgress(false);
                mEmailView.setError("Email is already");
                mEmailView.requestFocus();
            } else  if (jsonUsernameReady == 1){

                showProgress(false);
            } else {
                showProgress(false);
                Toast.makeText(getActivity(), "failed", Toast.LENGTH_LONG).show();
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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mFullname.setError(null);
        mBirthdate.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);
        mNoTelp.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String fullname = mFullname.getText().toString();
        String birthdate = mBirthdate.getText().toString();
        String password = mPasswordView.getText().toString();
        String repassword = mRePasswordView.getText().toString();
        String notelp = mNoTelp.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }// Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(repassword) && !isPasswordValid(repassword)&& !repassword.equals(password)) {
            mRePasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mRePasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            command = Command.CMD_GENERATE;
            showProgress(true);
            AsyncDataClass asyncRequestObject = new AsyncDataClass();
            asyncRequestObject.execute(getString(R.string.url_valid) + serverUrl, command, email,"",fullname,birthdate, password,sharedPrefManager.getSPUserId(),notelp);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
        }
    }




    private void callFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    }
