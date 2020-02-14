package com.example.finallabassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    DatabaseHelper mDataBase;
    List<FavouritePlaces> places;

    SwipeMenuListView listView;

    PlacesAdapter placeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
       // listView = findViewById(R.id.LVPlaces);
        listView = findViewById(R.id.listView);
        places = new ArrayList<>();

        // mDataBase = openOrCreateDatabase(MainActivity.DATABASE_NAME,MODE_PRIVATE,null);
        mDataBase = new DatabaseHelper(this);
        loadPlaces();


        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                editItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                editItem.setWidth((250));

                // set item title fontsize
                editItem.setTitleSize(18);
                // set item title font color
                editItem.setTitleColor(Color.WHITE);
                // add to menu

                editItem.setIcon(R.drawable.ic_edit);
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth((250));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);

            }
        };
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        FavouritePlaces place = places.get(position);
                        double latitude = Double.parseDouble(place.getLatitude());
                        double longitude = Double.parseDouble(place.getLongitude());

                        Intent intent = new Intent(FavouriteActivity.this,MainActivity.class);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        intent.putExtra("id", place.getId());
                        intent.putExtra("address", place.getAddress());
                        intent.putExtra("date", place.getDate());
                        setResult(MainActivity.RESULT_OK, intent);
                        finish();
                        Toast.makeText(FavouriteActivity.this, "cell clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        // delete

                        Toast.makeText(FavouriteActivity.this, "Delete", Toast.LENGTH_SHORT).show();
                        FavouritePlaces place1 = places.get(position);
                        int id = place1.getId();
                        if(mDataBase.deletePlaces(id))
                            places.remove(position);
                        placeAdapter.notifyDataSetChanged();
                        //loadPlaces();
                        break;

                }
                // false : close the menu; true : not close the menu
                return true;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FavouritePlaces place = places.get(position);
                double latitude = Double.parseDouble(place.getLatitude());
                double longitude = Double.parseDouble(place.getLongitude());

                Intent intent = new Intent(FavouriteActivity.this,MainActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                setResult(MainActivity.RESULT_OK, intent);
                finish();
                Toast.makeText(FavouriteActivity.this, "cell clicked", Toast.LENGTH_SHORT).show();

            }
        });




    }


    private void loadPlaces() {
        /*
        String sql = "SELECT * FROM employees";


        Cursor cursor = mDataBase.rawQuery(sql, null);

         */
        Cursor cursor = mDataBase.getAllPlaces();
        if(cursor.moveToFirst()){
            do {
                places.add(new FavouritePlaces(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                ));


            }while (cursor.moveToNext());
            cursor.close();
            //show item in a listView
            //we use a custom adapter to show employees

            placeAdapter = new PlacesAdapter(this, R.layout.list_layout_places, places, mDataBase);
            listView.setAdapter(placeAdapter);

        }
    }


}