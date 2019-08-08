package com.huriyo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.huriyo.Adapter.SearchAdapter;
import com.huriyo.Model.Suggestions;
import com.huriyo.R;
import com.huriyo.Ui.HomeActivity;
import com.huriyo.Ui.SearchResultActivity;
import com.huriyo.Utility.Constants;
import com.huriyo.Utility.DividerItemDecoration;
import com.huriyo.Utility.FragmentBase;
import com.huriyo.Utility.Globals;
import com.huriyo.Utility.RecyclerTouchListener;
import com.huriyo.Utility.Screen;
import com.huriyo.Utility.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jay on 07/11/17.
 */

public class SearchFragment extends FragmentBase<HomeActivity> implements View.OnClickListener {

    EditText edSearch;
    TextView tvCancel, tvRecent;
    View view;
    RecyclerView recyclerView;
    SearchAdapter adapter;
    ArrayList<String> data, search;
    Type listType;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public Screen getCode() {
        return Screen.SEARCH;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        search = new ArrayList<>();

        edSearch = (EditText) view.findViewById(R.id.edSearch);
        edSearch.setTypeface(Utils.getTypefaceNormal(getActivity()));
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);
        tvCancel.setTypeface(Utils.getTypefaceNormal(getActivity()));
        tvRecent = (TextView) view.findViewById(R.id.tvRecent);
        tvRecent.setTypeface(Utils.getTypefaceBold(getActivity()));

        listType = new TypeToken<ArrayList<String>>() {
        }.getType();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.rcl_divider));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new SearchAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                ArrayList<String> data = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_recent), listType);
                if (data == null) {
                    data = new ArrayList<>();
                    data.add(adapter.getMdata().get(position));
                    Utils.setPreference(getActivity(), Constants.MM_recent, new Gson().toJson(data));
                } else if (!data.contains(adapter.getMdata().get(position))) {
                    data.add(adapter.getMdata().get(position));
                    Utils.setPreference(getActivity(), Constants.MM_recent, new Gson().toJson(data));
                }
                Utils.hideSoftKeyboard(getActivity());
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra(Constants.MM_search, adapter.getMdata().get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));


        edSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    data = new Gson().fromJson(Utils.getPreference(getActivity(), Constants.MM_recent), listType);

                    if (data != null && data.size() > 0) {
                        adapter.doRefresh(data);
                    }
                    edSearch.addTextChangedListener(watcher);
                }
            }
        });

        return view;
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if (s.length() <= 0) {
                adapter.doRefresh(data);
            } else {
                getSuggestions(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCancel:
                Utils.hideSoftKeyboard(getActivity());
                edSearch.setText("");
                break;
        }
    }

    private static List<String> filter(List<String> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<String> filteredModelList = new ArrayList<>();
        for (String model : models) {
            final String text = model.toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void getSuggestions(String keyword) {
        JsonObject object = new JsonObject();
        try {
            object.addProperty(Constants.MM_keyword, keyword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Globals.initRetrofit(getActivity()).getSuggestions(object).enqueue(new Callback<Suggestions>() {
            @Override
            public void onResponse(Call<Suggestions> call, Response<Suggestions> response) {
                if (response.body().response_code == 200) {
                    if (response.body().getSuggestions() != null && response.body().getSuggestions().size() > 0) {
                        adapter.doRefresh(response.body().getSuggestions());
                    } else {
                        adapter.doRefresh(new ArrayList<String>());
                    }
                }
            }

            @Override
            public void onFailure(Call<Suggestions> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
