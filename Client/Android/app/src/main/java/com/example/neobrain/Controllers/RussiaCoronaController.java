package com.example.neobrain.Controllers;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.example.neobrain.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RussiaCoronaController extends Controller {

    private final String url = "https://corona.lmao.ninja/v2/countries/russia?yesterday=false&strict=true&query";
    private HttpURLConnection con;
    private StringBuilder sb;

    @BindView(R.id.new_admitted)
    public TextView new_admitted;
    @BindView(R.id.all)
    public TextView all;
    @BindView(R.id.new_deaths)
    public TextView new_deaths;
    @BindView(R.id.all_deaths)
    public TextView all_deaths;
    @BindView(R.id.tests)
    public TextView tests;
    @BindView(R.id.all_recovered)
    public TextView all_recovered;
    @BindView(R.id.casesPerOneMillion)
    public TextView casesPerOneMillion;
    @BindView(R.id.deathsPerOneMillion)
    public TextView deathsPerOneMillion;
    @BindView(R.id.activePerOneMillion)
    public TextView activePerOneMillion;
    @BindView(R.id.recoveredPerOneMillion)
    public TextView recoveredPerOneMillion;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.russia_corona, container, false);
        ButterKnife.bind(this, view);

        new AsyncRequest().execute();

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    class AsyncRequest extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... arg) {
            try {
                con = (HttpURLConnection) new URL(url).openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(10000);
                con.setReadTimeout(10000);
                con.setRequestProperty("Content-Type", "application/json");
                con.connect();

                if (HttpURLConnection.HTTP_OK == con.getResponseCode()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        sb.append(line.trim());
                    }
                    return new JSONObject(sb.toString());
                } else {
                    Log.e("Error", "fail " + con.getResponseCode() + ", " + con.getResponseMessage());
                }
            } catch (Exception e) {
                Log.e("Error", "exception " + e.getClass().getName() + " cause: " + e.getCause());
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    String new_add = s.get("todayCases").toString();
                    new_admitted.setText("+" + new_add);
                    String cases = s.get("cases").toString();
                    all.setText(cases);
                    String deaths = s.get("deaths").toString();
                    all_deaths.setText(deaths);
                    String todayDeaths = s.get("todayDeaths").toString();
                    new_deaths.setText("+" + todayDeaths);
                    String recovered = s.get("recovered").toString();
                    all_recovered.setText(recovered);
                    String test = s.get("tests").toString();
                    tests.setText(test);
                    String cas_per_mil = s.get("casesPerOneMillion").toString();
                    casesPerOneMillion.setText(cas_per_mil);
                    String det_per_mil = s.get("deathsPerOneMillion").toString();
                    deathsPerOneMillion.setText(det_per_mil);
                    String recPerOneMillion = s.get("recoveredPerOneMillion").toString();
                    recoveredPerOneMillion.setText(recPerOneMillion);
                    String actPerOneMillion = s.get("activePerOneMillion").toString();
                    activePerOneMillion.setText(actPerOneMillion);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
