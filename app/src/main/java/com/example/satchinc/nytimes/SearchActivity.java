package com.example.satchinc.nytimes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.gvResults) GridView gvResults;
    ArrayList<Article> article = new ArrayList<Article>();
    ArticleArrayAdapter adapter;
    int morePage = 0;
    String begin_date="20150606";
    String end_date=null;
    String sort = "newest";
    String fq = "-1" ;
    String lastQuery = "-1";
    RequestParams params;
    String ARTICLESEARCHURI = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    String TOPSTORIESURI = "https://api.nytimes.com/svc/topstories/v2/home.json";
    int REQUEST_CODE=30;
    public String API_KEY = "8ad1eeff7c3942299c9b526dcbf7bddb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Top Stories");


        //toolbar.setTitle("NYTimesnwefjkoen");
        article = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this,article);
        gvResults.setAdapter(adapter);
        getTopStories();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                onArticleSearch(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.filter_item){
            Intent intent = new Intent(SearchActivity.this, FilterActivity.class);
            intent.putExtra("query", lastQuery);
            startActivityForResult(intent, REQUEST_CODE);

        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode==REQUEST_CODE){
        String checkQuery = data.getExtras().getString("query");
        begin_date=data.getExtras().getString("begin_date");
        fq = data.getExtras().getString("fq");
        sort=data.getExtras().getString("sort");
        if(!(checkQuery.equals("-1"))){onArticleSearch(lastQuery);}
        }
    }
    public void onArticleSearch(String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        lastQuery=query;
        setParams(query);
        client.get(ARTICLESEARCHURI, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.clear();
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();

                    Log.d("debug", article.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"The client has failed to receive successful data", Toast.LENGTH_SHORT).show();
            }
        });
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ArticleActivity.class);
                Article passArticle = article.get(position);
                intent.putExtra("url", passArticle.getWebURL());
                startActivity(intent);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {

                customLoadMoreDataFromApi(page+1);

                return true;
            }
        });
    }
    public void setParams(String query) {
        params = new RequestParams();
        params.put("api-key", API_KEY);
        params.put("page", 0);
        params.put("begin_date", begin_date);
        if (!(fq.equals("-1"))){
            //news_desk:("Education"%20"Health")
            //Fix the news desk
            params.put("fq", fq);
        }
        params.put("q", query);
        params.put("sort", sort);
    }

    public void customLoadMoreDataFromApi(int offset) {
        AsyncHttpClient client = new AsyncHttpClient();
        setParams(lastQuery);
        params.remove("page");
        params.put("page", Integer.toString(offset));
        client.get(ARTICLESEARCHURI, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();

                    Log.d("debug", article.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"The client has failed to receive anymore successful data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getTopStories() {
        AsyncHttpClient topClient = new AsyncHttpClient();
        RequestParams tsParams = new RequestParams();
        tsParams.put("api-key", API_KEY);
        topClient.get(TOPSTORIESURI, tsParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    Log.d("debug", response.toString());
                    //SET UP A HANDLER
                    articleJsonResults = response.getJSONArray("results");
                    adapter.clear();
                    adapter.addAll(Article.tsfromJSONArray(articleJsonResults));
                    //articleJsonResults = response.getJSONObject("response").getJSONArray("docs");

                    //adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(),"The client has failed to receive successful data", Toast.LENGTH_SHORT).show();
            }
        });
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ArticleActivity.class);
                Article passArticle = article.get(position);
                intent.putExtra("url", passArticle.getWebURL());
                startActivity(intent);
            }
        });

    }




}
