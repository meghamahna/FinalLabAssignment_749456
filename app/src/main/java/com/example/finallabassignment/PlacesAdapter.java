package com.example.finallabassignment;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlacesAdapter extends ArrayAdapter {

    Context mContext;
    int layoutRes;
    List<FavouritePlaces> places;

    //SQLiteDatabase mDatabase;
    DatabaseHelper mDatabase;

    public PlacesAdapter( Context mContext, int layoutRes, List<FavouritePlaces> places, DatabaseHelper mDatabase) {
        super(mContext, layoutRes,places);
        this.mContext = mContext;
        this.layoutRes = layoutRes;
        this.places = places;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(layoutRes, null);
        TextView tvLatitude = v.findViewById(R.id.latitude);
        TextView tvLongitude = v.findViewById(R.id.longitude);
        TextView tvAddress = v.findViewById(R.id.address);

        final FavouritePlaces favouritePlaces = places.get(position);
        tvLatitude.setText(favouritePlaces.getLatitude());
        tvLongitude.setText(favouritePlaces.getLongitude());
        tvAddress.setText(favouritePlaces.getAddress());

        //loadPlaces();
        return v;

    }

//    private void loadPlaces() {
//
///*
//        String sql = "SELECT * FROM employees";
//        Cursor cursor = mDatabase.rawQuery(sql, null);
//
// */
//        Cursor cursor = mDatabase.getAllPlaces();
//
//        if(cursor.moveToFirst()){
//            places.clear();
//            do{
//                places.add(new FavouritePlaces(cursor.getInt(0),
//                        cursor.getString(1),
//                        cursor.getDouble(2),
//                        cursor.getDouble(3),
//                        cursor.getString(4)
//                ));
//            }while (cursor.moveToNext());
//
//            cursor.close();
//        }
//        notifyDataSetChanged();
//
//
//
//    }
}
