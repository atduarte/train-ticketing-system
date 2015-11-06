package com.cmov.railwaysportugal;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ListResultsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_results);
        //set title text
        addResult((ScrollView)this.findViewById(R.id.listofresults));
    }


    protected void addResult(ScrollView viewById)
    {

        LinearLayout l1 = new LinearLayout(this);
        l1.setOrientation(LinearLayout.VERTICAL);

        //viewById.addView();
        /*
        <LinearLayout
            android:layout_width="fill_parent"
            android:layout_height="fill_parent"
            android:orientation="vertical">

            <TextView
                android:gravity="center_horizontal"
                android:id="@+id/title"
                android:layout_width="fill_parent"
                android:layout_height="wrap_content"
                android:textColor="#000000"
                android:textSize="35sp"
                android:text="Porto  Aveiro\n2015-11-6"
                >
            </TextView>

            <LinearLayout
                android:layout_width="fill_parent"
                android:layout_height="fill_parent"
                android:orientation="vertical">

                <TextView
                    android:gravity="center_horizontal"
                    android:id="@+id/lblUserRep"
                    android:layout_width="fill_parent"
                    android:layout_height="wrap_content"
                    android:lines="2"

                    android:text="HH:MM Porto 35min Aveito HH:MM"
                    >
                </TextView>


                <Button
                    style="?android:attr/buttonStyleSmall"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Buy"
                    android:id="@+id/button1"
                    android:layout_gravity="center" />
            </LinearLayout>




         */

    }

}
