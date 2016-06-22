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
    String begin_date=null;
    String end_date=null;
    String sort = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        article = new ArrayList<Article>();
        adapter = new ArticleArrayAdapter(this,article);
        gvResults.setAdapter(adapter);

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
                Toast.makeText(getApplicationContext(),"Successfully Searching for " + query,Toast.LENGTH_SHORT).show();
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
            startActivity(intent);
            //1.start activity

            //2.Receive information and store in appropriate param strings

            //3.Recall client call and update list
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(String query) {

        Toast.makeText(this,"Searching for " +  query,Toast.LENGTH_SHORT).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String uri = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "8ad1eeff7c3942299c9b526dcbf7bddb");
        params.put("page", 1);
        params.put("q", query);


        client.get(uri, params, new JsonHttpResponseHandler(){
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
                Log.d("Debug", "failure"); ;
            }
        });
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ArticleActivity.class);
                Article passArticle = article.get(position);
                intent.putExtra("url", passArticle.getWebURL());
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"this", Toast.LENGTH_LONG).show();
            }
        });
    }


}
