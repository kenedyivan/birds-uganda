 package com.hamsoftug.birduganda.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hamsoftug.birduganda.R;
import com.hamsoftug.birduganda.models.TourPackage;

import java.util.List;


 public class TourPackagesGridAdapter extends RecyclerView.Adapter<TourPackagesGridAdapter.MyViewHolder>{
    private Context mContext;
    private List<TourPackage> tourPackages = null;
     private boolean mTwoPane;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView title, price;
        public ImageView thumbnail, overflow;

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

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, String.valueOf(tourPackage.id));
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    .getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, String.valueOf(tourPackage.id));

                    context.startActivity(intent);
                }

            }
        });*/
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

    *//**
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