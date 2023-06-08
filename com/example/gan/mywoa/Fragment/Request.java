package com.example.gan.mywoa.Fragment;

/**
 * Created by GUSWIK on 7/16/2017.
 */

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gan.mywoa.LoginActivity;
import com.example.gan.mywoa.PesananObject;
import com.example.gan.mywoa.R;
import com.example.gan.mywoa.Utils.Command;
import com.example.gan.mywoa.Utils.PaketJenis;
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
import java.util.Vector;

public class Request extends Fragment {

    public Request(){}
    View rootView;
    ArrayAdapter adapter;
    private CustomListAdapter customListAdapter;
    SharedPrefManager sharedPrefManager;
    String[] daftar ;
    String paketId ="";
    String paketName ="";
    String paketHarga = "";
    String paketNominalHarga = "";
    String paketStatus = "";
    String paketTipe = "";
    String paketDetail="";
    String paketImg="";

    String paketMaxHours="";

    Spinner dropdown;
    String makananidarray="";
    String detail="";
    String totalharga="";
    Vector makananDipesanV=new Vector();
    int paketJenis=0;
    Fragment fragment = null;
    TextView textdetail = null;
    TextView textTitle = null;


    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private EditText mtglmulai;
    private EditText note;
    private final String serverUrl = "control_book.php";


    ImageView image = null;
    FragmentManager fragmentManager;
    Button chkout ;
    FragmentTransaction fragmentTransaction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.request, container, false);

        if (getArguments() != null) {
            paketName = getArguments().getString("paketName");
            paketId = getArguments().getString("paketId");
            paketHarga =  getArguments().getString("paketHarga");
            paketNominalHarga =  getArguments().getString("paketNominalHarga");

            dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            paketStatus =  getArguments().getString("paketStatus");
            paketTipe =  getArguments().getString("paketTipe");
            paketDetail= getArguments().getString("paketDetail");
            paketImg= getArguments().getString("paketImg");
            paketMaxHours= getArguments().getString("paketMaxHours");

            try{
                for (int v = 0; v < 10; v++) {

                    String[] myList = getArguments().getStringArray("" + v);
                    PesananObject pesananObject = new PesananObject();
                    pesananObject.setId(myList[0]);
                    pesananObject.setNama_makanan(myList[1]);
                    pesananObject.setJumlah(Long.parseLong(myList[3]));
                    pesananObject.setHarga(Double.parseDouble(myList[2]));
                    makananDipesanV.add(pesananObject);
                    makananidarray +=myList[0]+",";
                }

            }catch (Exception e){}

        }

        TableLayout tableLayout = new TableLayout(getActivity());
        tableLayout = (TableLayout) rootView.findViewById(R.id.table);
        tableLayout.removeAllViews();

        dropdown = (Spinner)rootView.findViewById(R.id.spinnerjam);
        String[] items;

        items = new String[0];

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        ArrayAdapter myAdap = (ArrayAdapter) dropdown.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition = 0;
        dropdown.setSelection(spinnerPosition);

        textdetail = (TextView) rootView.findViewById(R.id.textViewdetailPaket);
        textTitle = (TextView) rootView.findViewById(R.id.textTitle);

        note = (EditText) rootView.findViewById(R.id.editTextketerangan);


        mtglmulai = (EditText) rootView.findViewById(R.id.tglmulai);

        mtglmulai.setInputType(InputType.TYPE_NULL);
        mtglmulai.requestFocus();


        mtglmulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDateDialog();
            }
        });

        image = (ImageView) rootView.findViewById(R.id.imageView3);

        textdetail.setText(paketDetail);
        textTitle.setText(paketName);
        try{
            new ImageDownloaderTask(image).execute(paketImg);
        }catch (Exception e){
            System.out.print(e);
        }
        getActivity().setTitle("Detail");

        TableLayout.LayoutParams parameterTableLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);

        TableRow TRHeader = new TableRow(getActivity());
        TRHeader.setBackgroundColor(Color.DKGRAY);
        TRHeader.setLayoutParams(parameterTableLayout);

        TableRow.LayoutParams parameterTableRowHeader = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

        parameterTableRowHeader.setMargins(4, 4, 4, 4);

        TextView headerNoTransaksi = new TextView(getActivity());
        headerNoTransaksi.setText(" Nama ");
        headerNoTransaksi.setTextColor(Color.BLACK);
        headerNoTransaksi.setGravity(Gravity.CENTER);
        headerNoTransaksi.setBackgroundColor(Color.DKGRAY);
        TRHeader.addView(headerNoTransaksi, parameterTableRowHeader);


        TextView headerJumlahTransaksi = new TextView(getActivity());
        headerJumlahTransaksi.setText(" Jumlah ");
        headerJumlahTransaksi.setTextColor(Color.BLACK);
        headerJumlahTransaksi.setGravity(Gravity.CENTER);
        headerJumlahTransaksi.setBackgroundColor(Color.DKGRAY);
        TRHeader.addView(headerJumlahTransaksi, parameterTableRowHeader);


        TextView headerDateTransaksi = new TextView(getActivity());
        headerDateTransaksi.setText(" Harga ");
        headerDateTransaksi.setTextColor(Color.BLACK);
        headerDateTransaksi.setGravity(Gravity.CENTER);
        headerDateTransaksi.setBackgroundColor(Color.DKGRAY);
        TRHeader.addView(headerDateTransaksi, parameterTableRowHeader);

        tableLayout.addView(TRHeader);
        double totalharga = 0;
        try {

            TableRow TR = new TableRow(getActivity());
            TR.setBackgroundColor(Color.GRAY);
            TR.setLayoutParams(parameterTableLayout);

            TableRow.LayoutParams parameterTableRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

            parameterTableRow.setMargins(4, 4, 4, 4);



            TextView txtNoTransaksi = new TextView(getActivity());
            txtNoTransaksi.setText("" + paketName);
            txtNoTransaksi.setTextColor(Color.DKGRAY);
            //TV.setPadding(2, 25, 2, 25);
            txtNoTransaksi.setGravity(Gravity.CENTER);
            txtNoTransaksi.setBackgroundColor(Color.GRAY);
            TR.addView(txtNoTransaksi, parameterTableRow);

            TextView txtJumlahTransaksi = new TextView(getActivity());
            txtJumlahTransaksi.setText("");
            txtJumlahTransaksi.setTextColor(Color.DKGRAY);
            //TV.setPadding(2, 25, 2, 25);
            txtJumlahTransaksi.setGravity(Gravity.CENTER);
            txtJumlahTransaksi.setBackgroundColor(Color.GRAY);
            TR.addView(txtJumlahTransaksi, parameterTableRow);

            TextView txtDateTransaksi = new TextView(getActivity());
            txtDateTransaksi.setText(""+paketNominalHarga);
            totalharga = totalharga+(Double.parseDouble(paketNominalHarga));
            txtDateTransaksi.setTextColor(Color.DKGRAY);
            //TV.setPadding(2, 25, 2, 25);
            txtDateTransaksi.setGravity(Gravity.CENTER);
            txtDateTransaksi.setBackgroundColor(Color.GRAY);
            TR.addView(txtDateTransaksi, parameterTableRow);

            tableLayout.addView(TR);

        } catch (Exception e) {
        }



            try {
                if (makananDipesanV != null && makananDipesanV.size() > 0) {
                    for (int v = 0; v < makananDipesanV.size(); v++) {
                        try {
                            PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);

                            TableRow TR = new TableRow(getActivity());
                            TR.setBackgroundColor(Color.GRAY);
                            TR.setLayoutParams(parameterTableLayout);

                            TableRow.LayoutParams parameterTableRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                            parameterTableRow.setMargins(4, 4, 4, 4);



                            TextView txtNoTransaksi = new TextView(getActivity());
                            txtNoTransaksi.setText(pesananObjectAdd.getNama_makanan());
                            txtNoTransaksi.setTextColor(Color.DKGRAY);
                            //TV.setPadding(2, 25, 2, 25);
                            txtNoTransaksi.setGravity(Gravity.CENTER);
                            txtNoTransaksi.setBackgroundColor(Color.GRAY);
                            TR.addView(txtNoTransaksi, parameterTableRow);

                            TextView txtJumlahTransaksi = new TextView(getActivity());
                            txtJumlahTransaksi.setText("" + pesananObjectAdd.getJumlah());
                            txtJumlahTransaksi.setTextColor(Color.DKGRAY);
                            //TV.setPadding(2, 25, 2, 25);
                            txtJumlahTransaksi.setGravity(Gravity.CENTER);
                            txtJumlahTransaksi.setBackgroundColor(Color.GRAY);
                            TR.addView(txtJumlahTransaksi, parameterTableRow);

                            TextView txtDateTransaksi = new TextView(getActivity());
                            txtDateTransaksi.setText(""+(pesananObjectAdd.getJumlah()*pesananObjectAdd.getHarga()));
                            totalharga = totalharga+(pesananObjectAdd.getJumlah()*pesananObjectAdd.getHarga());

                            detail += pesananObjectAdd.getNama_makanan()+"("+pesananObjectAdd.getHarga()+")="+pesananObjectAdd.getJumlah()+", ";
                            txtDateTransaksi.setTextColor(Color.DKGRAY);
                            //TV.setPadding(2, 25, 2, 25);
                            txtDateTransaksi.setGravity(Gravity.CENTER);
                            txtDateTransaksi.setBackgroundColor(Color.GRAY);
                            TR.addView(txtDateTransaksi, parameterTableRow);

                            tableLayout.addView(TR);

                        } catch (Exception e) {
                        }
                    }

                    try {

                        TableRow TR = new TableRow(getActivity());
                        TR.setBackgroundColor(Color.GRAY);
                        TR.setLayoutParams(parameterTableLayout);

                        TableRow.LayoutParams parameterTableRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                        parameterTableRow.setMargins(4, 4, 4, 4);



                        TextView txtNoTransaksi = new TextView(getActivity());
                        txtNoTransaksi.setText("Total");
                        txtNoTransaksi.setTextColor(Color.BLUE);
                        //TV.setPadding(2, 25, 2, 25);
                        txtNoTransaksi.setGravity(Gravity.CENTER);
                        txtNoTransaksi.setBackgroundColor(Color.GRAY);
                        TR.addView(txtNoTransaksi, parameterTableRow);

                        TextView txtJumlahTransaksi = new TextView(getActivity());
                        txtJumlahTransaksi.setText("");
                        txtJumlahTransaksi.setTextColor(Color.BLUE);
                        //TV.setPadding(2, 25, 2, 25);
                        txtJumlahTransaksi.setGravity(Gravity.CENTER);
                        txtJumlahTransaksi.setBackgroundColor(Color.GRAY);
                        TR.addView(txtJumlahTransaksi, parameterTableRow);

                        TextView txtDateTransaksi = new TextView(getActivity());
                        txtDateTransaksi.setText(""+totalharga);
                        txtDateTransaksi.setTextColor(Color.BLUE);
                        //TV.setPadding(2, 25, 2, 25);
                        txtDateTransaksi.setGravity(Gravity.CENTER);
                        txtDateTransaksi.setBackgroundColor(Color.GRAY);
                        TR.addView(txtDateTransaksi, parameterTableRow);

                        tableLayout.addView(TR);

                    } catch (Exception e) {
                    }

                }


            } catch (Exception e){}




        chkout = (Button) rootView.findViewById(R.id.requestbutton);

        chkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String tanggalDari = mtglmulai.getText().toString()+" "+dropdown.getSelectedItem().toString();
                    if (tanggalDari.length() < 15){

                        Toast.makeText(getActivity(), "Mohon lengkapi data", Toast.LENGTH_LONG).show();
                    } else {

                        attemptLogin();
                    }
                }catch(Exception e){

                }
//                Toast.makeText(getActivity(), "Mohon lengkapi data", Toast.LENGTH_LONG).show();



            }
        });
//        chkout.setVisibility(View.INVISIBLE);

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

                long timeNowx = 0;


                long timeselectlong =0;

                try{
                    DateFormat formatterx = new SimpleDateFormat("yyyy-MM-dd");

                    Date nowdate = new Date();
                    String nowdates = formatterx.format(nowdate);

                    Date timenow = formatterx.parse(nowdates);
                    timeNowx = timenow.getTime();

                    Date timeselect = formatterx.parse(""+dateFormatter.format(newDate.getTime()));
                    timeselectlong = timeselect.getTime();
                }catch (Exception e){
                }

                if ( timeNowx <= timeselectlong){
                    mtglmulai.setText("" + dateFormatter.format(newDate.getTime()));
                    getjam(dateFormatter.format(newDate.getTime()));
                } else {
                    Toast.makeText(getActivity(), "Date is Expired", Toast.LENGTH_LONG).show();
                }





            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }


    private void attemptLogin() {


        // Store values at the time of the login attempt.
        //String paketId = "1";
        sharedPrefManager = new SharedPrefManager(getActivity());
        String memberId = ""+sharedPrefManager.getSPUserId();
        String tanggalDari = mtglmulai.getText().toString()+" "+dropdown.getSelectedItem().toString();
        String tanggalBerhenti ="";
        try{
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date tanggalberhentidate = formatter.parse(tanggalDari);
            int maxhours = Integer.parseInt(paketMaxHours);
            long berhentitime = (tanggalberhentidate.getTime())+(maxhours*3600000);
            Date newtanggalberhenti = new Date(berhentitime);

            tanggalBerhenti =formatter.format(newtanggalberhenti);

        }catch (Exception e){

        }
        String bookingstatus = "0";;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String tanggalRequest = dateFormat.format(date);
        String makananId = makananidarray;

        String detailx = detail;
        double totalx = 0;

        try {

            totalharga = totalharga+(Double.parseDouble(paketNominalHarga));
            if (makananDipesanV != null && makananDipesanV.size() > 0) {
                for (int v = 0; v < makananDipesanV.size(); v++) {
                    try {
                        PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                        totalx = totalx+(pesananObjectAdd.getJumlah()*pesananObjectAdd.getHarga());

                    } catch (Exception e) {
                    }
                }

            }


        } catch (Exception e){}

        String notex = note.getText().toString();

        boolean cancel =false;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //focusView.requestFocus();
        } else {
            //showProgress(true);

            //command = Command.CMD_SAVE;
            //showProgress(true);
            String token= FirebaseInstanceId.getInstance().getToken();
            String tokenx= token;
            AsyncDataClass asyncRequestObject = new AsyncDataClass();
            asyncRequestObject.execute(getString(R.string.url_valid) + serverUrl, Command.CMD_SAVE, memberId,paketId,tanggalDari,tanggalBerhenti, bookingstatus, tanggalRequest,makananId,""+totalx,detailx,notex,tokenx);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);
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
                nameValuePairs.add(new BasicNameValuePair("MEMBER_ID", params[2]));
                nameValuePairs.add(new BasicNameValuePair("PAKET_ID", params[3]));
                nameValuePairs.add(new BasicNameValuePair("TANGGAL_DARI", params[4]));
                nameValuePairs.add(new BasicNameValuePair("TANGGAL_BERHENTI", params[5]));
                nameValuePairs.add(new BasicNameValuePair("BOOKING_STATUS", params[6]));
                nameValuePairs.add(new BasicNameValuePair("TANGGAL_REQUEST", params[7]));
                nameValuePairs.add(new BasicNameValuePair("MAKANAN_ID", params[8]));
                nameValuePairs.add(new BasicNameValuePair("TOTAL_HARGA", params[9]));
                nameValuePairs.add(new BasicNameValuePair("DETAIL", params[10]));
                nameValuePairs.add(new BasicNameValuePair("NOTE", params[11]));
                nameValuePairs.add(new BasicNameValuePair("token_id", params[12]));
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
            String queryReady = "";
            try {
                resultObject = new JSONObject(result);
                jsonResult = resultObject.getInt("success");;
                queryReady = resultObject.getString("query");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonResult == 1) {
                //showProgress(false);
                RequestList RequestList = new RequestList();
                //makanan.setArguments(bundle);

                        getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, RequestList)
                        .commit();
            } else {
                //showProgress(false);
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



    private void getjam(String tanggaldari) {
        AsyncDataClassJam asyncRequestObject = new AsyncDataClassJam();
        asyncRequestObject.execute(getResources().getString(R.string.url_valid)+"control_book.php","6",tanggaldari,paketId);

    }


    private class AsyncDataClassJam extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 6000);
            HttpConnectionParams.setSoTimeout(httpParameters, 6000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);

            String jsonResult = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("COMMAND", params[1]));
                nameValuePairs.add(new BasicNameValuePair("TANGGAL_DARI", params[2]));
                nameValuePairs.add(new BasicNameValuePair("PAKET_ID", params[3]));
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
            if (result.equals("") || result == null) {
                Toast.makeText(getActivity(), "Server connection failed", Toast.LENGTH_LONG).show();
                return;
            }

            ArrayList<ListItem> listData2 = new ArrayList<ListItem>();

            try{
                String sjam = ",08:00:00,09:00:00,10:00:00,11:00:00,12:00:00,13:00:00,14:00:00,15:00:00,16:00:00,17:00:00,18:00:00,19:00:00,20:00:00,21:00:00,22:00:00";

                Date newx= new Date();
                int hours = newx.getHours();
                String tglmu = mtglmulai.getText().toString();
                String nowdates ="";
                long tglp=0;
                long tgln=0;
                try{

                    Date nowdate = new Date();

                    Date selectdatex = new Date();
                    DateFormat formatterx = new SimpleDateFormat("yyyy-MM-dd");

                    Date tanggalpilih = formatterx.parse(tglmu);
                    selectdatex.setYear(tanggalpilih.getYear());
                    selectdatex.setMonth(tanggalpilih.getMonth());
                    selectdatex.setDate(tanggalpilih.getDate());

                    nowdates = formatterx.format(nowdate);
                    tglp=selectdatex.getTime();
                    //nowdate.setHours(0);
                    //nowdate.setMinutes(0);
                    //nowdate.setSeconds(0);
                    tgln=nowdate.getTime();
                }catch (Exception e){
                }

                if((tglmu.equals(nowdates))){
                    try{
                        for(int x=8;x < (hours+2);x++){
                            if (x<10){
                                String jam = "0"+x+":00:00";
                                sjam = sjam.replace(","+jam,"");
                            } else {
                                String jam = x+":00:00";
                                sjam = sjam.replace(","+jam,"");
                            }

                        }
                    }catch (Exception e){
                    }

                }


                JSONObject jsonObject =new JSONObject(result);
                try{

                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    //String[] items;
                    //items = new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject json = jsonArray.getJSONObject(i);
                        String jam = json.getString("JAM");
                        sjam = sjam.replace(","+jam,"");
                        // items[i]=jam;
                    }

                }catch (Exception e){

                }

                String[] parts = sjam.split(",");
                if ((sjam.length()==0) || (tglp<tgln)){

                    Toast.makeText(getActivity(), "Tanggal penuh/expired", Toast.LENGTH_LONG).show();
                    showDateDialog();
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, parts);
                dropdown.setAdapter(adapter);
            }catch   (JSONException e) {
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


    private void callFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    }
