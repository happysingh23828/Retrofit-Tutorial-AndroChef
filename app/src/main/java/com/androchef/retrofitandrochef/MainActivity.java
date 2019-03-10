package com.androchef.retrofitandrochef;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androchef.retrofitandrochef.Adapter.CustomRecyclerAdapter;
import com.androchef.retrofitandrochef.model.Worldpopulation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    Button btnLoadList;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    CustomRecyclerAdapter adapter;
    List<Worldpopulation> listOfWorldPopulations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setRecyclerView();
        onClicks();
    }

    private void init() {
        btnLoadList = findViewById(R.id.btn_load_list);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.flag_list_recyclerView);
        adapter = new CustomRecyclerAdapter(listOfWorldPopulations, this);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    private void onClicks() {
        btnLoadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWorldPopulationListFromRetrofit();
            }
        });
    }

    private void loadWorldPopulationListFromRetrofit() {

        //Showing Progress while fetching data from server
        progressBar.setVisibility(View.VISIBLE);

        String baseUrl = "http://androchef.com";

        // Creating a retrofit object and adding GsonConverterFactory which uses GSon for JSON
        Retrofit retrofit = new Retrofit.Builder().
                baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<List<Worldpopulation>> apiCall = apiInterface.loadWordPopulationList();

        //Calling the API to fetch Data
        apiCall.enqueue(new Callback<List<Worldpopulation>>() {
            @Override
            public void onResponse(Call<List<Worldpopulation>> call, Response<List<Worldpopulation>> response) {

                //updating or notifying adapter to show latest fetched list
                listOfWorldPopulations.clear();
                listOfWorldPopulations.addAll(response.body());
                adapter.notifyDataSetChanged();

                //hide progress bar
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Worldpopulation>> call, Throwable t) {
                //hide progress bar
                progressBar.setVisibility(View.GONE);

                //when we got some network error while fetching data
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    //Retrofit turns your HTTP API into a Java interface.
    //Annotations on the interface methods and its parameters indicate how a request will be handled.
    public interface ApiInterface {

        // Request method and URL Given in the notation
        @GET("/wp-content/uploads/2019/03/world-population-androchef.txt")
        Call <List<Worldpopulation>> loadWordPopulationList();

    }
}
