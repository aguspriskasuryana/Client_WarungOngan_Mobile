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
import android.widget.Toast;

import com.example.gan.mywoa.Utils.PaketJenis;

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
import com.example.gan.mywoa.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class NewPaket extends Fragment {

    public NewPaket(){}
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.paket, container, false);

        if (getArguments() != null) {
            paketJenis = getArguments().getInt("PAKET_CATEGORY_ID");
        }

        getActivity().setTitle("" + PaketJenis.jenis[paketJenis]);
        attemptLogin();

        listView = (ListView) rootView.findViewById(R.id.custom_listPaket);


        ArrayList<ListItem> listData = getListData();

        chkout = (Button) rootView.findViewById(R.id.buttonOrder);
        chkout.setVisibility(View.INVISIBLE);
        listView = (ListView) rootView.findViewById(R.id.custom_listPaket);
        customListAdapter = new CustomListAdapter(getActivity(),listData);
        listView.setAdapter(customListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {

                                final int it = arg2;

                                ListItem newsData = (ListItem) listView.getItemAtPosition(it);
                                Bundle bundle = new Bundle();
                                bundle.putString("paketId", newsData.getPaketId());
                                bundle.putString("paketName", newsData.getHeadline());
                                bundle.putString("paketHarga", newsData.getHarga());
                                bundle.putString("paketStatus", newsData.getStatus());
                                bundle.putString("paketTipe", newsData.getTipe());
                                bundle.putString("paketDetail", newsData.getDetail());
                                bundle.putString("paketNominalHarga", ""+newsData.getNominalharga());
                                bundle.putString("paketImg", newsData.getUrl());
                                bundle.putString("paketMaxHours", newsData.getPaketMaxHours());    // Put anything what you want

                                MakananList makanan = new MakananList();
                                makanan.setArguments(bundle);

                                if(newsData.getIntstatus().equals("0")){
                                    Toast.makeText(getActivity(), "Not Available", Toast.LENGTH_LONG).show();
                                } else {
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame_container, makanan)
                                            .commit();
                                }

            }
        });


        return rootView;
    }

    private ArrayList<ListItem> getListData() {
        ArrayList<ListItem> listMockData = new ArrayList<ListItem>();
        String[] images = getResources().getStringArray(R.array.images_array);
        String[] headlines = getResources().getStringArray(R.array.headline_array);

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
    private void attemptLogin() {
        AsyncDataClass asyncRequestObject = new AsyncDataClass();
        asyncRequestObject.execute(getResources().getString(R.string.url_valid)+"control_paket.php","8",""+paketJenis);

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
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("COMMAND", params[1]));
                nameValuePairs.add(new BasicNameValuePair("PAKET_CATEGORY_ID", params[2]));
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
                    String paketdetail = json.getString("PAKET_DETAIL");
                    String name=json.getString("PAKET_NAME");
                    String paketId=json.getString("PAKET_ID");
                    String paketMaxHours=json.getString("MAX_HOURS");
                    String status ="";
                    if ((json.getString("PAKET_STATUS")).equals("1") ){
                        status=(json.getString("MAX_KURSI"))+" kursi - Avalaible";
                    } else {
                        status=(json.getString("MAX_KURSI"))+" kursi - Not Available";
                    }

                    String tipe=json.getString("PAKET_CATEGORY_NAME");
                    double harga =json.getDouble("PAKET_HARGA");
                    ListItem newsData = new ListItem();
                    newsData.setUrl(getResources().getString(R.string.url_Images) + "paket/" + urlimages);
                    newsData.setHeadline(name);
                    newsData.setPaketId(paketId);
                    newsData.setTipe(tipe);
                    newsData.setDetail(paketdetail);
                    newsData.setStatus(status);
                    newsData.setPaketMaxHours(paketMaxHours);
                    newsData.setIntstatus((json.getString("PAKET_STATUS")));
                    newsData.setHarga(""+kursIndonesia.format(harga));
                    newsData.setNominalharga(harga);
                    daftar[i]= name;
                    listData2.add(newsData);
                }
            }catch  (JSONException e) {
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
    private void callFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    }
