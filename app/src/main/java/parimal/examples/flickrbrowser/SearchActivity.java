package parimal.examples.flickrbrowser;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;

public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivity";

    private SearchView msearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        activateToolbar(true);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        getMenuInflater().inflate(R.menu.menu_search,menu);

        SearchManager searchManager=(SearchManager)getSystemService(Context.SEARCH_SERVICE);
        msearchView= (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        final SearchableInfo searchableInfo=searchManager.getSearchableInfo(getComponentName());
        msearchView.setSearchableInfo(searchableInfo);
        //Log.d(TAG, "onCreateOptionsMenu: "+getComponentName().toString());
        //Log.d(TAG, "onCreateOptionsMenu: hint is "+msearchView.getQueryHint());
        //Log.d(TAG, "onCreateOptionsMenu: searchable info is "+searchableInfo.toString());
        
        msearchView.setIconified(false);

        msearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: called");
                SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(FLICKR_QUERY,query).apply();
                msearchView.clearFocus();
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        msearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });

        Log.d(TAG, "onCreateOptionsMenu: returned "+true);
        return true;
    }
}
