package com.hamsoftug.birduganda.models;

/**
 * Created by Kall on 8/5/2016.
 */
public class TourPackage {
    public int id;
    public String name;
    public int imageUrl;
    public int hero;
    public int list;
    public String price;

    public TourPackage(int id, String name, int imageUrl,int hero,int list,String price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.hero = hero;
        this.list = list;
    }

    @Override
    public String toString() {
        return name;
    }

}
