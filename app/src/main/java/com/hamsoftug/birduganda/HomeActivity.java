package com.hamsoftug.birduganda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hamsoftug.birduganda.dummy.DummyPackages;
import com.hamsoftug.birduganda.models.TourPackage;

import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    List<TourPackage> tp = DummyPackages.ITEMS;
    private boolean mTwoPane;
    private boolean mPackages;

    private FloatingActionButton fab = null;
    private SQLDatabaseHelper sqlDatabaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sqlDatabaseHelper = new SQLDatabaseHelper(HomeActivity.this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sqlDatabaseHelper.getProfile().size()<1) {
                    Snackbar.make(view, "User profile needed to chat, ", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Login or Register ?", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                }
                            }).show();
                } else {

                    startActivity(new Intent(HomeActivity.this,Chat.class));

                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View recyclerView = findViewById(R.id.recycler_view);
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        setupRecyclerView((RecyclerView) recyclerView);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPackages = true;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new TourPackagesGridAdapter(this, tp, mTwoPane));
    }


    /*
    *Tour packages grid adapter
     */
    public class TourPackagesGridAdapter extends RecyclerView.Adapter<TourPackagesGridAdapter.MyViewHolder> {
        private Context mContext;
        private List<TourPackage> tourPackages = null;
        private boolean mTwoPane;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView title, price;
            public ImageView thumbnail;

            public MyViewHolder(View view) {
                super(view);
                mView = view;
                title = (TextView) view.findViewById(R.id.title);
                price = (TextView) view.findViewById(R.id.price);
                thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            }
        }


        public TourPackagesGridAdapter(Context c, List<TourPackage> tourPackages, boolean mTwoPane) {
            mContext = c;
            this.tourPackages = tourPackages;
            this.mTwoPane = mTwoPane;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.gride_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final TourPackage tourPackage = tourPackages.get(position);
            holder.title.setText(tourPackage.name);

            holder.thumbnail.setImageResource(tourPackage.imageUrl);
            holder.price.setText(tourPackage.price);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, String.valueOf(tourPackage.id));
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                        Log.e("THE ID ", String.valueOf(String.valueOf(tourPackage.id)));
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, String.valueOf(tourPackage.id));
                        Log.e("THE ID ", String.valueOf(String.valueOf(tourPackage.id)));

                        context.startActivity(intent);
                    }

                }
            });
        }

        /**
         * Showing popup menu when tapping on 3 dots
         */
    /*private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    */

        /**
         * Click listener for popup menu items
         *//*
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_details:
                    Toast.makeText(mContext, "TourPackage details", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }
*/
        @Override
        public int getItemCount() {
            return tourPackages.size();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_packages) {

        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(getApplicationContext()
                    ,FavoriteListActivity.class));

        } else if (id == R.id.nav_bookings) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
