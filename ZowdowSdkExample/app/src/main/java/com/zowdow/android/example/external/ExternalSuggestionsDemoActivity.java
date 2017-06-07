package com.zowdow.android.example.external;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.squareup.okhttp.OkHttpClient;
import com.zowdow.android.example.R;
import com.zowdow.android.example.advanced.network.BingNetworkService;
import com.zowdow.android.example.advanced.network.BingResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ExternalSuggestionsDemoActivity extends AppCompatActivity {
    private BingNetworkService mBingService;
    private Call<BingResponse> mBingCall;
    private ArrayAdapter<String> mSuggestionsAdapter;

    private EditText mSearchEditText;
    private ListView mSuggestionsListView;

    {
        OkHttpClient client = new OkHttpClient();
        mBingService = new Retrofit.Builder()
                .baseUrl(BingNetworkService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build().create(BingNetworkService.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integration_demo);

        mSearchEditText = (EditText) findViewById(R.id.editText);
        mSuggestionsListView = (ListView) findViewById(R.id.listView);

        setupSearchField();
        setupSuggestionsList();
    }

    private void setupSearchField() {
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    mBingCall = mBingService.bing(URLEncoder.encode(s.toString(), "UTF-8"));
                    mBingCall.enqueue(createBingResponseCallback());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSuggestionsList() {
        mSuggestionsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>());
        mSuggestionsListView.setAdapter(mSuggestionsAdapter);
    }

    private retrofit.Callback<BingResponse> createBingResponseCallback() {
        return new retrofit.Callback<BingResponse>() {
            @Override
            public void onResponse(Response<BingResponse> response, Retrofit retrofit) {
                mSuggestionsAdapter.clear();
                mSuggestionsAdapter.addAll(response.body().getSuggestions());
                mSuggestionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        };
    }
}
