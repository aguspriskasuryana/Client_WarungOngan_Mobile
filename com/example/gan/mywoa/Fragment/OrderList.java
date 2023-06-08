package com.example.gan.mywoa.Fragment;

/**
 * Created by GUSWIK on 7/16/2017.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gan.mywoa.PesananObject;
import com.example.gan.mywoa.R;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class OrderList extends Fragment {

    public OrderList(){}
    View rootView;
    ArrayList<ListItem> listData = new ArrayList<ListItem>();
    ArrayAdapter adapter;
    private CustomListAdapter customListAdapter;
    ListView listView;
    String[] daftar ;

    String paketId ="";
    String paketName ="";
    String paketHarga = "";
    String paketStatus = "";
    String paketTipe = "";
    String paketDetail="";
    String paketImg="";


    Vector makananDipesanV=new Vector();
    //Hashtable makananDipesanHash=new Hashtable();
    //Hashtable JumlahmakananDipesanHash=new Hashtable();

    Button requestB ;

    SharedPrefManager sharedPrefManager;
    String makananDipesan="";
    String idmakananDipesan="";
    String idmember="";

    Fragment fragment = null;
    TextView noteTextView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.request_list, container, false);


        getActivity().setTitle("ORDER");
        sharedPrefManager = new SharedPrefManager(getActivity());
        idmember = sharedPrefManager.getSPUserId();
        attemptLogin(sharedPrefManager.getSPUserId());



        ArrayList<ListItem> listData = getListData();

        listView = (ListView) rootView.findViewById(R.id.custom_listReq);
        customListAdapter = new CustomListAdapter(getActivity(),listData);
        listView.setAdapter(customListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {

                final int it = arg2;
                final CharSequence[] dialogitem = {"0"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Jumlah");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:

                                break;

                        }
                    }
                });
                builder.create().show();
            }
        });


        return rootView;
        }

private ArrayList<ListItem>getListData(){
        ArrayList<ListItem>listMockData=new ArrayList<ListItem>();
        String[]images=getResources().getStringArray(R.array.images_array);
        String[]headlines=getResources().getStringArray(R.array.headline_array);

        for (int i = 0; i < images.length; i++) {
            ListItem newsData = new ListItem();
            newsData.setUrl(images[i]);
            newsData.setHeadline(headlines[i]);
            newsData.setTipe(headlines[i]);
            newsData.setStatus("Avalaible");
            listMockData.add(newsData);
        }

        return listMockData;
    }
    private void attemptLogin(String memberid) {
        AsyncDataClass asyncRequestObject = new AsyncDataClass();
        asyncRequestObject.execute(getResources().getString(R.string.url_valid)+"control_book.php","8",memberid,"");

    }


    private class AsyncDataClass extends AsyncTask<String, Void, String> {

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
                nameValuePairs.add(new BasicNameValuePair("MEMBER_ID", params[2]));
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

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            try{

                JSONObject jsonObject =new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                daftar = new String[jsonArray.length()];
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject json = jsonArray.getJSONObject(i);
                    String id = json.getString("PAKET_ID");
                    String urlimages = json.getString("PAKET_IMG");
                    String makanandetail = json.getString("TANGGAL_REQUEST");
                     String name=json.getString("PAKET_NAME");
                     String status ="";
                    if ((json.getString("RESERVASI_STATUS")).equals("1") ){
                        status="Sudah bayar";
                    } else {
                        status="Menunggu Konfirmasi";
                    }

                    String tipe=json.getString("PAKET_NAME");
                    double harga =json.getDouble("TOTAL_HARGA");

                    ListItem newsData = new ListItem();
                    newsData.setUrl(getResources().getString(R.string.url_Images) +"paket/"+urlimages);
                    newsData.setHeadline(name);
                    newsData.setTipe("" + kursIndonesia.format(harga));
                    newsData.setDetail(makanandetail);
                    newsData.setStatus(status);
                    newsData.setPaketId(id);
                    newsData.setNominalharga(harga);
                    newsData.setHarga("" + kursIndonesia.format(harga));
                    //makananDipesanHash.put(id, newsData);
                    //makananDipesanV.add(id);
                    //JumlahmakananDipesanHash.put(id,0);
                    daftar[i]= name;
                    listData2.add(newsData) ;
                }
            }catch   (JSONException e) {
                e.printStackTrace();
            }



            //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, daftar);
            //listView.setAdapter(arrayAdapter);

            //ListView = (ListView) rootView.findViewById(R.id.custom_listPaket);
            //ListView.setAdapter(new CustomListAdapter(getActivity(), listData));

            //CustomListAdapter customListAdapter2 = new CustomListAdapter(getActivity(),listData2);
            customListAdapter.updateArray(listData2);
            listView.setAdapter(customListAdapter);
            customListAdapter.notifyDataSetChanged();
            //

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

    public static long setpesananHash( ListItem newsData,long jumlah){
        long nilai = 0;

        PesananObject pesananObject = new PesananObject();
        pesananObject.setId(newsData.getPaketId());
        pesananObject.setNama_makanan(newsData.getHeadline());
        pesananObject.setJumlah(jumlah);
        pesananObject.setHarga(newsData.getNominalharga());


        return nilai;
    }
    private void callFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    }
