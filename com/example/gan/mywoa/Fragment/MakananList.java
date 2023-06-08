package com.example.gan.mywoa.Fragment;

/**
 * Created by GUSWIK on 7/16/2017.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.gan.mywoa.RegisterActivity;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class MakananList extends Fragment {

    public MakananList(){}
    View rootView;
    ArrayList<ListItem> listData = new ArrayList<ListItem>();
    ArrayAdapter adapter;
    private CustomListAdapter customListAdapter;
    ListView listView;
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


    Vector makananDipesanV=new Vector();
    //Hashtable makananDipesanHash=new Hashtable();
    //Hashtable JumlahmakananDipesanHash=new Hashtable();

    Button requestB ;

    String makananDipesan="";
    String idmakananDipesan="";
    Fragment fragment = null;
    TextView noteTextView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.paket, container, false);

        if (getArguments() != null) {
            paketName = getArguments().getString("paketName");
            paketId = getArguments().getString("paketId");
            paketHarga =  getArguments().getString("paketHarga");

            paketStatus =  getArguments().getString("paketStatus");
            paketTipe =  getArguments().getString("paketTipe");
            paketDetail= getArguments().getString("paketDetail");
            paketImg= getArguments().getString("paketImg");
            paketMaxHours= getArguments().getString("paketMaxHours");
            paketNominalHarga =  getArguments().getString("paketNominalHarga");

        }

        getActivity().setTitle("Request");
        attemptLogin();

        requestB = (Button) rootView.findViewById(R.id.buttonOrder);
        requestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("paketId", paketId);
                bundle.putString("paketName", paketName);
                bundle.putString("paketHarga", paketHarga);
                bundle.putString("paketNominalHarga", paketNominalHarga);
                bundle.putString("paketStatus", paketStatus);
                bundle.putString("paketTipe", paketTipe);
                bundle.putString("paketDetail", paketDetail);
                bundle.putString("paketImg", paketImg); // Put anything what you want

                bundle.putString("paketMaxHours", paketMaxHours);
                Request request = new Request();
                request.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, request)
                        .commit();
            }
        });
        // requestB.setVisibility(View.INVISIBLE);

        noteTextView = (TextView) rootView.findViewById(R.id.textViewnote);

        noteTextView.setText(""+paketName+", Silahkan memilih makanan :");


        ArrayList<ListItem> listData = getListData();

        listView = (ListView) rootView.findViewById(R.id.custom_listPaket);
        customListAdapter = new CustomListAdapter(getActivity(),listData);
        listView.setAdapter(customListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {

                final int it = arg2;
                final CharSequence[] dialogitem = {"0", "1", "2", "3", "4", "5", "6"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                ListItem newsDataX = (ListItem) listView.getItemAtPosition(it);
                if (newsDataX.getIntstatus().equals("1")){
                builder.setTitle("Jumlah");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:

                                ListItem newsData = (ListItem) listView.getItemAtPosition(it);
                                makananDipesan += newsData.getHarga() + ", ";
                                try {
                                    PesananObject pesananObject = new PesananObject();
                                    pesananObject.setId(newsData.getPaketId());
                                    pesananObject.setNama_makanan(newsData.getHeadline());
                                    pesananObject.setJumlah(0);
                                    pesananObject.setHarga(newsData.getNominalharga());

                                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                                        for (int v = 0; v < makananDipesanV.size(); v++) {
                                            try {
                                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                                if (pesananObjectAdd.getId() == newsData.getPaketId()) {
                                                    makananDipesanV.remove(v);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());

                                    } else {
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());
                                    }
                                } catch (Exception e) {

                                }

                                Toast.makeText(getActivity(), "Anda memilih :" + " " + newsData.getHeadline() + " Jumlah 0", Toast.LENGTH_LONG).show();
                                break;
                            case 1:

                                ListItem newsData1 = (ListItem) listView.getItemAtPosition(it);
                                makananDipesan += newsData1.getHarga() + ", ";

                                try {
                                    PesananObject pesananObject = new PesananObject();
                                    pesananObject.setId(newsData1.getPaketId());
                                    pesananObject.setNama_makanan(newsData1.getHeadline());
                                    pesananObject.setJumlah(1);
                                    pesananObject.setHarga(newsData1.getNominalharga());

                                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                                        for (int v = 0; v < makananDipesanV.size(); v++) {
                                            try {
                                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                                if (pesananObjectAdd.getId() == newsData1.getPaketId()) {
                                                    makananDipesanV.remove(v);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());

                                    } else {
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());
                                    }
                                } catch (Exception e) {

                                }

                                Toast.makeText(getActivity(), "Anda memilih :" + " " + newsData1.getHeadline() + " Jumlah 1", Toast.LENGTH_LONG).show();
                                break;
                            case 2:

                                ListItem newsData2 = (ListItem) listView.getItemAtPosition(it);
                                makananDipesan += newsData2.getHarga() + ", ";

                                try {
                                    PesananObject pesananObject = new PesananObject();
                                    pesananObject.setId(newsData2.getPaketId());
                                    pesananObject.setNama_makanan(newsData2.getHeadline());
                                    pesananObject.setJumlah(2);
                                    pesananObject.setHarga(newsData2.getNominalharga());

                                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                                        for (int v = 0; v < makananDipesanV.size(); v++) {
                                            try {
                                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                                if (pesananObjectAdd.getId() == newsData2.getPaketId()) {
                                                    makananDipesanV.remove(v);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());

                                    } else {
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());
                                    }
                                } catch (Exception e) {

                                }

                                Toast.makeText(getActivity(), "Anda memilih :" + " " + newsData2.getHeadline() + " Jumlah 2", Toast.LENGTH_LONG).show();
                                break;
                            case 3:

                                ListItem newsData3 = (ListItem) listView.getItemAtPosition(it);
                                makananDipesan += newsData3.getHarga() + ", ";
                                try {
                                    PesananObject pesananObject = new PesananObject();
                                    pesananObject.setId(newsData3.getPaketId());
                                    pesananObject.setNama_makanan(newsData3.getHeadline());
                                    pesananObject.setJumlah(3);
                                    pesananObject.setHarga(newsData3.getNominalharga());

                                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                                        for (int v = 0; v < makananDipesanV.size(); v++) {
                                            try {
                                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                                if (pesananObjectAdd.getId() == newsData3.getPaketId()) {
                                                    makananDipesanV.remove(v);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());

                                    } else {
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());
                                    }
                                } catch (Exception e) {

                                }
                                Toast.makeText(getActivity(), "Anda memilih :" + " " + newsData3.getHeadline() + " Jumlah 3", Toast.LENGTH_LONG).show();
                                break;

                            case 4:

                                ListItem newsData4 = (ListItem) listView.getItemAtPosition(it);
                                makananDipesan += newsData4.getHarga() + ", ";
                                try {
                                    PesananObject pesananObject = new PesananObject();
                                    pesananObject.setId(newsData4.getPaketId());
                                    pesananObject.setNama_makanan(newsData4.getHeadline());
                                    pesananObject.setJumlah(4);
                                    pesananObject.setHarga(newsData4.getNominalharga());

                                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                                        for (int v = 0; v < makananDipesanV.size(); v++) {
                                            try {
                                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                                if (pesananObjectAdd.getId() == newsData4.getPaketId()) {
                                                    makananDipesanV.remove(v);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());

                                    } else {
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());
                                    }
                                } catch (Exception e) {

                                }
                                Toast.makeText(getActivity(), "Anda memilih :" + " " + newsData4.getHeadline() + " Jumlah 4", Toast.LENGTH_LONG).show();
                                break;

                            case 5:

                                ListItem newsData5 = (ListItem) listView.getItemAtPosition(it);
                                makananDipesan += newsData5.getHarga() + ", ";
                                try {
                                    PesananObject pesananObject = new PesananObject();
                                    pesananObject.setId(newsData5.getPaketId());
                                    pesananObject.setNama_makanan(newsData5.getHeadline());
                                    pesananObject.setJumlah(5);
                                    pesananObject.setHarga(newsData5.getNominalharga());

                                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                                        for (int v = 0; v < makananDipesanV.size(); v++) {
                                            try {
                                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                                if (pesananObjectAdd.getId() == newsData5.getPaketId()) {
                                                    makananDipesanV.remove(v);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());

                                    } else {
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());
                                    }
                                } catch (Exception e) {

                                }
                                Toast.makeText(getActivity(), "Anda memilih :" + " " + newsData5.getHeadline() + " Jumlah 5", Toast.LENGTH_LONG).show();
                                break;

                            case 6:

                                ListItem newsData6 = (ListItem) listView.getItemAtPosition(it);
                                makananDipesan += newsData6.getHarga() + ", ";
                                try {
                                    PesananObject pesananObject = new PesananObject();
                                    pesananObject.setId(newsData6.getPaketId());
                                    pesananObject.setNama_makanan(newsData6.getHeadline());
                                    pesananObject.setJumlah(6);
                                    pesananObject.setHarga(newsData6.getNominalharga());

                                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                                        for (int v = 0; v < makananDipesanV.size(); v++) {
                                            try {
                                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                                if (pesananObjectAdd.getId() == newsData6.getPaketId()) {
                                                    makananDipesanV.remove(v);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());

                                    } else {
                                        makananDipesanV.add(pesananObject);
                                        //noteTextView.setText("Anda memilih :" + " " + pesananObject.getNama_makanan() + " = " + pesananObject.getHarga());
                                    }
                                } catch (Exception e) {

                                }
                                Toast.makeText(getActivity(), "Anda memilih :" + " " + newsData6.getHeadline() + " Jumlah 6", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            } else {
                    Toast.makeText(getActivity(), "Not Available", Toast.LENGTH_LONG).show();
                }
                builder.create().show();
            }
        });

        requestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("paketId", paketId);
                bundle.putString("paketName", paketName);
                bundle.putString("paketHarga", paketHarga);
                bundle.putString("paketNominalHarga", paketNominalHarga);
                bundle.putString("paketStatus", paketStatus);
                bundle.putString("paketTipe", paketTipe);
                bundle.putString("paketDetail", paketDetail);
                bundle.putString("paketImg", paketImg); // Put anything what you want
                bundle.putString("paketMaxHours", paketMaxHours);


                try {
                    if (makananDipesanV != null && makananDipesanV.size() > 0) {
                        for (int v = 0; v < makananDipesanV.size(); v++) {
                            try {
                                PesananObject pesananObjectAdd = (PesananObject) makananDipesanV.get(v);
                                if (pesananObjectAdd.getJumlah() > 0) {
                                    String[] myList = {pesananObjectAdd.getId(), pesananObjectAdd.getNama_makanan(), "" + pesananObjectAdd.getHarga(), "" + pesananObjectAdd.getJumlah()};
                                    bundle.putStringArray(""+v, myList);
                                }
                            } catch (Exception e) {
                            }
                        }

                    }
                } catch (Exception e){}

                Request req = new Request();
                req.setArguments(bundle);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, req)
                        .commit();
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
    private void attemptLogin() {
        AsyncDataClass asyncRequestObject = new AsyncDataClass();
        asyncRequestObject.execute(getResources().getString(R.string.url_valid)+"control_makanan.php","4","");

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
                    String id = json.getString("MAKANAN_ID");
                    String urlimages = json.getString("MAKANAN_IMG");
                    String makanandetail = json.getString("MAKANAN_DETAIL");
                     String name=json.getString("MAKANAN_NAME");
                     String status ="";
                    if ((json.getString("MAKANAN_STATUS")).equals("1") ){
                        status="Avalaible";
                    } else {
                        status="Not Available";
                    }

                    String tipe=json.getString("MAKANAN_NAME");
                    double harga =json.getDouble("MAKANAN_HARGA");

                    ListItem newsData = new ListItem();
                    newsData.setUrl(getResources().getString(R.string.url_Images) +"makanan/"+urlimages);
                    newsData.setHeadline(name);
                    newsData.setTipe("" + kursIndonesia.format(harga));
                    newsData.setDetail(makanandetail);
                    newsData.setStatus(status);
                    newsData.setIntstatus((json.getString("MAKANAN_STATUS")));
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
