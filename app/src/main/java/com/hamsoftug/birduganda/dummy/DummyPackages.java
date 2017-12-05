package com.hamsoftug.birduganda.dummy;
import com.hamsoftug.birduganda.R;
import com.hamsoftug.birduganda.models.TourPackage;

import java.util.ArrayList;
import java.util.List;


public class DummyPackages {
    /**
     * An array of sample (dummy) packages.
     */
    public static final List<TourPackage> ITEMS = new ArrayList<>();


    static {

            addItem(new TourPackage(1,"18 days birding", R.drawable.b1,R.drawable.bb1,R.drawable.bf1,"$150"));
            addItem(new TourPackage(2,"12 days gorilla trekking",R.drawable.g1,R.drawable.gb1,R.drawable.gf1,"$200"));
            addItem(new TourPackage(3,"2 days rwenzori hiking", R.drawable.b2,R.drawable.bb2,R.drawable.bf2,"$100"));
            addItem(new TourPackage(4,"20 days birding + trekking", R.drawable.b3,R.drawable.bb3,R.drawable.bf3,"$400"));
            addItem(new TourPackage(5,"12 days karuma visit", R.drawable.b4,R.drawable.bb4,R.drawable.bf4,"$350"));
            addItem(new TourPackage(6,"10 days bird + hiking", R.drawable.b5,R.drawable.bb5,R.drawable.bf5,"$260"));
            addItem(new TourPackage(7,"4 days parroting", R.drawable.b6,R.drawable.bb6,R.drawable.bf6,"$120"));
    }

    private static void addItem(TourPackage item) {
        ITEMS.add(item);
    }

}
