package com.hurontg.tiger.staff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.hurontg.tiger.R;

public class BaseMenuActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  protected void startAdminActivity() {
    Intent intent = new Intent(this, AdminActivity.class);
    startActivity(intent);
  }

  protected void startMapsActivity() {
    Intent intent = new Intent(this, MapsActivity.class);
    startActivity(intent);
  }

  protected void startListViewActivity() {
    Intent intent = new Intent(this, ListViewActivity.class);
    startActivity(intent);
  }

}
