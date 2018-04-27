package com.example.user.productproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainProduct extends AppCompatActivity {
    Toolbar toolbar;
    TextView textProduct;
    TextView textWarehouse;
    TextView textProductID;
    TextView textBarCode;
    TextView textCheckTime;
    TextView textStock;
    NavigationView liftmenu;
    DrawerLayout mainlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_product);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        liftmenu = (NavigationView)findViewById(R.id.liftmenu);
        mainlayout = (DrawerLayout)findViewById(R.id.Product_layout);
        textProduct = (TextView)findViewById(R.id.textProduct);
        textWarehouse = (TextView)findViewById(R.id.textWarehouse);
        textProductID = (TextView)findViewById(R.id.textProductID);
        textBarCode = (TextView)findViewById(R.id.textBarCode);
        textCheckTime = (TextView)findViewById(R.id.textCheckTime);
        textStock = (TextView)findViewById(R.id.textStock);
        Bundle bundle = this.getIntent().getExtras();
        String BarcodeRead = bundle.getString("Barcode");
        SetToolBar();
        SetLifeMenu();
        String url = "http://www.itioi.com/TestP.php?shid=1&barcode="+BarcodeRead;
        new TransTask().execute(url);
    }
    //Json 解析
    class TransTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    Log.d("HTTP", line);
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("JSON", s);
            parseJSON(s);
        }

        private void parseJSON(String s) {
            ArrayList<Json> trans = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(s);
                for (int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    String shname = obj.getString("sh_name");
                    String sh_id = obj.getString("sh_id");
                    String p_name = obj.getString("p_name");
                    String p_no = obj.getString("p_no");
                    String p_barcode = obj.getString("p_barcode");
                    String p_inventory_date = obj.getString("p_inventory_date");
                    String p_photo = obj.getString("p_photo");
                    String p_count = obj.getString("p_count");
                    Json t = new Json(shname,sh_id,p_name,p_no, p_barcode,p_inventory_date, p_photo,p_count);

                    textProduct.setText(p_name);
                    textWarehouse.setText(shname);
                    textProductID.setText(p_no);
                    textBarCode.setText(p_barcode);
                    textCheckTime.setText(p_inventory_date);
                    textStock.setText(p_count);
                    trans.add(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    //ToolBar
    private void SetToolBar() {

        toolbar.setTitle("詳細資料");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);//toolbar 上面


        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mainlayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
    }

    //左側表單
    private void SetLifeMenu() {
        liftmenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 點選時收起選單
                mainlayout.closeDrawer(GravityCompat.START);
                // 取得選項id
                int id = item.getItemId();
                // 依照id判斷點了哪個項目並做相應事件
                if (id == R.id.action_ProductView) {
                    Toast.makeText(MainProduct.this, "目前顯示頁面：詳細資料", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.action_inventory){
                    Intent intent = new Intent();
                    intent.setClass(MainProduct.this, MainActivity.class);
                    MainProduct.this.finish();
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

}
