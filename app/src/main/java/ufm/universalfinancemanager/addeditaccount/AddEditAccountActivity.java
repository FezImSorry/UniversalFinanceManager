package ufm.universalfinancemanager.addeditaccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import ufm.universalfinancemanager.R;
import ufm.universalfinancemanager.di.ActivityScoped;
import ufm.universalfinancemanager.earningshistory.EarningsHistoryActivity;
import ufm.universalfinancemanager.home.HomeActivity;
import ufm.universalfinancemanager.networth.NetworthActivity;
import ufm.universalfinancemanager.transactionhistory.TransactionHistoryActivity;
import ufm.universalfinancemanager.util.ActivityUtils;

/**
 * Created by smh7 on 1/23/18.
 */

@ActivityScoped
public class AddEditAccountActivity extends DaggerAppCompatActivity {
    @Inject
    AddEditAccountPresenter mPresenter;
    @Inject
    AddEditAccountFragment mFragment;

    //@Inject
    //@Nullable
    String accountName;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addedit_account_activity);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        setupDrawerContent(navigationView);

        AddEditAccountFragment addEditAccountFragment =
                (AddEditAccountFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if(addEditAccountFragment == null) {
            addEditAccountFragment = mFragment;

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditAccountFragment , R.id.contentFrame);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
            }

        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        setToolbarTitle(accountName);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setToolbarTitle(@Nullable String Id) {
        if(Id == null) {
            setTitle(R.string.account_add_title);
        } else {
            setTitle(R.string.account_edit_title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                if(mDrawerToggle.onOptionsItemSelected(item))
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId()) {
                            case R.id.home_menu_item:
                                // switch to home activity
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                break;
                            case R.id.budget_menu_item:
                                //switch to budget overview activity
                                break;
                            case R.id.trans_history_menu_item:
                                //switch to transaction history activity
                                startActivity(new Intent(getApplicationContext(), TransactionHistoryActivity.class));
                                break;
                            case R.id.earnings_menu_item:
                                //switch to earnings menu
                                startActivity(new Intent(getApplicationContext(), EarningsHistoryActivity.class));
                                break;
                            case R.id.networth_menu_item:
                                //switch to networth activity
                                startActivity(new Intent(getApplicationContext(), NetworthActivity.class));
                                break;
                            case R.id.reminder_menu_item:
                                break;
                            case R.id.settings_menu_item:
                                break;
                        }

                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }
}
