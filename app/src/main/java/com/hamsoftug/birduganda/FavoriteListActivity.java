package com.hamsoftug.birduganda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hamsoftug.birduganda.dummy.DummyPackages;
import com.hamsoftug.birduganda.models.TourPackage;

import java.util.List;

/**
 * An activity representing a list of Favorites. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link FavoriteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class FavoriteListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    private SQLDatabaseHelper sqlDatabaseHelper = null;

    List<TourPackage> tp = DummyPackages.ITEMS;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        sqlDatabaseHelper = new SQLDatabaseHelper(FavoriteListActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sqlDatabaseHelper.getProfile().size()<1) {
                    Snackbar.make(view, "User profile needed to chat, ", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Login or Register ?", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(FavoriteListActivity.this,LoginActivity.class));
                                }
                            }).show();
                } else {

                    startActivity(new Intent(FavoriteListActivity.this,Chat.class));

                }

            }
        });

        View recyclerView = findViewById(R.id.favorite_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.favorite_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(tp));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<TourPackage> tourPackages = null;

        public SimpleItemRecyclerViewAdapter(List<TourPackage> tourPackages) {
            this.tourPackages = tourPackages;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.favorite_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final TourPackage tourPackage = tourPackages.get(position);
            holder.title.setText(tourPackage.name);

            holder.thumbnail.setImageResource(tourPackage.list);
            holder.price.setText(tourPackage.price);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(FavoriteDetailFragment.ARG_ITEM_ID, String.valueOf(tourPackage.id));
                        FavoriteDetailFragment fragment = new FavoriteDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.favorite_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, FavoriteDetailActivity.class);
                        intent.putExtra(FavoriteDetailFragment.ARG_ITEM_ID, String.valueOf(tourPackage.id));

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return tourPackages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            public TextView title, price;
            public ImageView thumbnail;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                title = (TextView) view.findViewById(R.id.title);
                price = (TextView) view.findViewById(R.id.price);
                thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + title.getText() + "'";
            }
        }
    }
}
