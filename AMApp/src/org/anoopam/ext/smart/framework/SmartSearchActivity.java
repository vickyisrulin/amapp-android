package org.anoopam.ext.smart.framework;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;

/**
 * Created by tasol on 16/7/15.
 */

public class SmartSearchActivity extends AMAppMasterActivity {

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.search_activity;
    }

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public void setAnimations() {
        super.setAnimations();
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setExitTransition(new Slide(Gravity.LEFT));
    }

    @Override
    public void preOnCreate() {

    }


    @Override
    public void initComponents() {


    }

    @Override
    public void prepareViews() {

    }

    @Override
    public void setActionListeners() {

        setDrawerStateListener(new OnDrawerStateListener() {
            @Override
            public void onDrawerOpen(View drawerView) {

            }

            @Override
            public void onDrawerClose(View drawerView) {

            }
        });


    }

    @Override
    public void postOnCreate() {

    }

    @Override
    public void manageAppBar(ActionBar actionBar,Toolbar toolbar,ActionBarDrawerToggle actionBarDrawerToggle) {

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)searchView.findViewById(R.id.search_src_text);

        theTextArea.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.rectangle_shape));
        theTextArea.setTextColor(getResources().getColor(R.color.search_dropdown_text_color));

        /*int color = getResources().getColor(R.color.textSecondary);
        int alpha = 204; // 80% alpha
        MenuColorizer.colorMenu(this, menu, color, alpha);*/

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {


        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SmartSuggestionProvider.AUTHORITY, SmartSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            setResult(RESULT_OK,new Intent().putExtra("loren ipsum",query));
            finish();
        }
    }


    @Override
    public int getDrawerLayoutID() {

        return 0;
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }


}
