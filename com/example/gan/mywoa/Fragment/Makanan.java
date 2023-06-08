package com.example.gan.mywoa.Fragment;

/**
 * Created by GUSWIK on 7/16/2017.
 */

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gan.mywoa.R;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class Makanan extends Fragment {

    public Makanan(){}
    View rootView;
    ArrayList<ListItemmakanan> listData = new ArrayList<ListItemmakanan>();
    ArrayAdapter adapter;
    private CustomListAdapterMakanan customListAdapter;
    ListView listView;
    String[] daftar ;
    int paketId=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.makanan, container, false);

        if (getArguments() != null) {
            paketId = getArguments().getInt("PAKET_ID");
        }

        getActivity().setTitle("Paket "+ paketId);
        attemptLogin();

        listView = (ListView) rootView.findViewById(R.id.custom_listMakanan);


        ArrayList<ListItemmakanan> listData = getListData();

        listView = (ListView) rootView.findViewById(R.id.custom_listMakanan);
        customListAdapter = new CustomListAdapterMakanan(getActivity(),listData);
        listView.setAdapter(customListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                ListItemmakanan newsData = (ListItemmakanan) listView.getItemAtPosition(position);
                Toast.makeText(getActivity(), "Selected :" + " " + newsData, Toast.LENGTH_LONG).show();
            }
        });


        return rootView;
    }

    private ArrayList<ListItemmakanan> getListData() {
        ArrayList<ListItemmakanan> listMockData = new ArrayList<ListItemmakanan>();
        String[] images = getResources().getStringArray(R.array.images_array);
        String[] headlines = getResources().getStringArray(R.array.headline_array);

        for (int i = 0; i < images.length; i++) {
            ListItemmakanan newsData = new ListItemmakanan();
            newsData.setUrl(images[i]);
            newsData.setHeadline(headlines[i]);
            newsData.setStatus("Oktober 26, 2017, 13:35");
            listMockData.add(newsData);
        }

        return listMockData;
    }
    private void attemptLogin() {
        AsyncDataClass asyncRequestObject = new AsyncDataClass();
        asyncRequestObject.execute(getResources().getString(R.string.url_valid)+"control_makanan.php","8",""+paketId);

    }


    private class AsyncDataClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);

            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPost httpPost = new HttpPost(params[0]);

            String jsonResult = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("COMMAND", params[1]));
                nameValuePairs.add(new BasicNameValuePair("PAKET_ID", params[2]));
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
            ArrayList<ListItemmakanan> listData2 = new ArrayList<ListItemmakanan>();

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
                    String id = json.getString("MAKANAN_ID");
                    String urlimages = json.getString("MAKANAN_IMG");
                    String paketdetail = json.getString("MAKANAN_DETAIL");
                    String name=json.getString("MAKANAN_NAME");
                    String status=json.getString("MAKANAN_STATUS");
                    double harga =json.getDouble("PAKET_HARGA");
                    ListItemmakanan newsData = new ListItemmakanan();
                    newsData.setUrl(getResources().getString(R.string.url_Images) + "makanan/" + urlimages);
                    newsData.setHeadline(name);
                    newsData.setDetail(paketdetail);
                    newsData.setStatus(status);
                    newsData.setHarga(""+kursIndonesia.format(harga));
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

    }
