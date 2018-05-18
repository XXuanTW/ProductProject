package com.example.user.productproject;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    TextView textBarCode;
    TextView textProduct;
    TextView textCheckTime;
    NumberPicker StockeditText;
    Toolbar toolbar;
    NavigationView liftmenu;
    DrawerLayout mainlayout;
    CameraSource cameraSource;
    SurfaceView CamreaView;
    BarcodeDetector barcodeDetector;
    Button CheckButton;
    Spinner textWareHouse;
    final int CameraID = 1;
    String url;
    String imgurl;
    String PID;
    String dataSQL;
    String userID;
    ImageView ProductImg;
    String[] sh;
    Integer[] shid;
    SQLdata DH = null;
    String [] arrayNFC = new String [4];
    String NFCdata;
    private final String[][] techList = new String[][] {
            new String[] {
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textWareHouse = (Spinner)findViewById(R.id.textWarehouse);
        textProduct = (TextView)findViewById(R.id.textProduct);
        textBarCode = (TextView)findViewById(R.id.textBarCode);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        liftmenu = (NavigationView)findViewById(R.id.liftmenu);
        mainlayout = (DrawerLayout)findViewById(R.id.main_layout);
        CamreaView = (SurfaceView) findViewById(R.id.CamareView);
        CheckButton = (Button)findViewById(R.id.CheckButton);
        ProductImg = (ImageView)findViewById(R.id.ProductImgView);
        StockeditText = (NumberPicker)findViewById(R.id.StockeditText);
        SetToolBar();
        SetLifeMenu();
        opendialog();
        DH = new SQLdata(this);
        addurl();
        //最大最小
        StockeditText.setMaxValue(1000);
        StockeditText.setMinValue(0);
        //不可編輯
        StockeditText.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        new SpinnerList().execute("http://"+dataSQL+"/StoreHouse.php");
        //不循環顯示
        StockeditText.setWrapSelectorWheel(false);
        //QRcode掃描配置
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        //照相機配置
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();
        //SurfaceView配置
        CamreaView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, CameraID);
                    return;
                }
                try {
                    cameraSource.start(CamreaView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0)
                    textBarCode.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(500);
                            textProduct.setText("");
                            textBarCode.setText("");
                            StockeditText.setValue(0);
                            ProductImg.setImageDrawable(null);
                            textBarCode.setText(qrcodes.valueAt(0).displayValue);
                            runCode();

                        }

                        private void runCode() {
                            int shidvalue = shid[textWareHouse.getSelectedItemPosition()];
                            url = "http://"+dataSQL+"/TestP.php?shid=";
                            url = url + shidvalue;
                            url = url + "&barcode=";
                            url = url+textBarCode.getText().toString();
                            new TransTask().execute(url);
                        }
                    });
            }
        });
        CheckButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                int shidvalue = shid[textWareHouse.getSelectedItemPosition()];
                if (!"".equals(StockeditText.getValue())) {
                    int Stocke = Integer.valueOf(StockeditText.getValue());
                    url = "http://"+dataSQL+"/UPDATE.php?shid=";
                    url = url + shidvalue + "&pid=" + PID ;
                    url = url + "&inventory=" + Stocke;
                    url = url + "&uid=" + userID;

                    new TransTask().execute(url);
                }
            }
        });
    }

    private void opendialog() {
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText userEditText = (EditText)subView.findViewById(R.id.userEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("員工編號");
        builder.setMessage("請輸入員工編號");
        builder.setView(subView);
        userEditText.setText(NFCdata);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               userID = userEditText.getText().toString();
                Toast.makeText(MainActivity.this, "員工編號："+userID, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("使用NFC自動輸入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "請將NFC靠近來讀取", Toast.LENGTH_LONG).show();
            }
        });

        builder.show();
    }

    //資料庫讀取
    private void addurl() {

        SQLiteDatabase db = DH.getWritableDatabase();
        Cursor cursor = db.query("data", new String[]{"_id", "_url"}, null, null, null, null, null);
        while (cursor.moveToNext()){
            dataSQL= cursor.getString(1);
        }


    }

    //NFC Read
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            String NFC = ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
            int x = 0;
            for (int i=0 ; i<NFC.length() ; i+=2)
            {
                arrayNFC[x]= NFC.substring(i,i+2);
                x=x+1;
            }
            String stringNFC ="";
            for (int j=3 ; j>=0;j--){
                stringNFC+=arrayNFC[j];
            }
//            textNFC.setText(stringNFC);
            String []arrayHex = new String[stringNFC.length()];

            for (int i = 0 ; i < stringNFC.length() ; i++){
                arrayHex[i]=stringNFC.substring(i,i+1);
            }
            int y = 1;
            long dec = 0;
            for(int i = stringNFC.length()-1 ; i >= 0 ; i--){
                dec = Long.valueOf(Integer.valueOf(arrayHex[i],16))*y +dec;
                y=y*16;
            }
//            Toast.makeText(this, String.format("%010d",dec), Toast.LENGTH_SHORT).show();
            NFCdata = String.format("%010d",dec);
            opendialog();


        }
    }

    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out = out + hex[i];
            i = in & 0x0f;
            out = out + hex[i];
        }
//        for(i =0; i<inarray.length; i++){
//            out += Integer.toHexString(inarray[i] & 0xFF);
//        }

        return out;
    }
    //ImgViewurl
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    //StoreHouseSpinner
    class SpinnerList extends AsyncTask<String, Void, String> {
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
            ArrayList<SpinnerJson> trans = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(s);
                sh = new String[array.length()];
                shid = new Integer[array.length()];
                for (int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    String sh_name = obj.getString("sh_name");
                    String sh_id = obj.getString("sh_id");
                    SpinnerJson t = new SpinnerJson(sh_name,sh_id);
                    shid[i] = Integer.valueOf(sh_id);
                    sh[i] = sh_name;
                    trans.add(t);
                }

                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_spinner_item,sh);
                //設定下拉選單的樣式
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                textWareHouse.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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
//                    int id = obj.getInt("id");
                    String shname = obj.getString("sh_name");
                    String sh_id = obj.getString("sh_id");
                    String p_id = obj.getString("p_id");
                    String p_name = obj.getString("p_name");
                    String p_no = obj.getString("p_no");
                    String p_barcode = obj.getString("p_barcode");
                    String p_inventory_date = obj.getString("p_inventory_date");
                    String p_photo = obj.getString("p_photo");
                    String p_count = obj.getString("p_count");
                    String p_inventory = obj.getString("p_inventory");
                    String p_inventory_eid = obj.getString("p_inventory_eid");
                    Json t = new Json(shname,sh_id,p_id,p_name,p_no, p_barcode,p_inventory_date, p_photo,p_count,p_inventory,p_inventory_eid);
                    textProduct.setText(p_name);
                    textBarCode.setText(p_barcode);
                    StockeditText.setValue(Integer.valueOf(p_count));
                    PID = p_id;
                    imgurl = "http://p0520.com/admin/upload/product/"+p_photo;
                    trans.add(t);
                }
                new DownloadImageFromInternet(ProductImg).execute(imgurl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    //ToolBar
    private void SetToolBar() {

        toolbar.setTitle("盤點系統");

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
                if (id == R.id.action_inventory) {
                    Toast.makeText(MainActivity.this, "目前顯示頁面：盤點系統", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.action_ProductView){
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,MainProduct.class);
                    MainActivity.this.finish();
                    Bundle bundle = new Bundle();
                    bundle.putString("Barcode",textBarCode.getText().toString());
                    bundle.putString("shid",String.valueOf(shid[textWareHouse.getSelectedItemPosition()]));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return true;
                }else if(id == R.id.action_setting){
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MainSetting.class);
                    MainActivity.this.finish();
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    //權限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CameraID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(CamreaView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

}
