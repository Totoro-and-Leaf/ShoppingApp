package com.fyp.shoping;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.shoping.R;
import com.fyp.shoping.http.Endpoint;
import com.fyp.shoping.http.PostApi;
import com.fyp.shoping.model.AddCartRequest;
import com.fyp.shoping.model.RemoveCartRequest;
import com.fyp.shoping.model.ScanRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private TextView txtProductDetail;

    private String intentData = "";
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    boolean doubleBackToExitPressedOnce = false;
    String productCode = "";
    private String orderCode = "";
    String[][] twoDarray = new String[30][2];
    BarcodeDetector barcodeDetector;
    Button btnCheckout, btnAdd,btnRemove, btnStart;
    FrameLayout frameLayout;
    private int dialogCount = 0;
    private int count = 0;
    int addCount = 0;
    int checkedRow = 0;
    boolean itemExist= false;
    int row = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grantPermission();
        initViews();
    }

    /**
     * Grant thread and camera permission
     */
    private void grantPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    /**
     * init view
     */
    private void initViews() {
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        surfaceView = findViewById(R.id.scanner);
        txtProductDetail = findViewById(R.id.productDetail);
        btnAdd = findViewById(R.id.btnAdd);
        btnRemove = findViewById(R.id.btnRemove);
        btnCheckout = findViewById(R.id.btnCheckOut);
        btnStart = findViewById(R.id.btnStart);

        btnAdd.setEnabled(false);
        btnCheckout.setEnabled(false);
        btnRemove.setEnabled(false);
        btnStart.setEnabled(false);



        btnCheckout.setOnClickListener(view -> intent(ShoppingCartActivity.class));

        btnAdd.setOnClickListener(view -> {
            addCount++;
            // if order is new, generate new order code
            if (orderCode.length() == 0) {
                orderCode = UUID.randomUUID().toString();
            }

            // sent request to backend update shopping cart
            AddCartRequest addCartRequest = new AddCartRequest(productCode, orderCode);

            //Array is used for error prevention, first part is to force create the 1st item into the array

                //2nd part is to check if the item exists in the array
                for (int row = 0; row<twoDarray.length; row++) {
                                      try {
                                          if(twoDarray[row][0].equals(productCode))
                                                {
                                          int tempInt = Integer.parseInt(twoDarray[row][1]);
                                          tempInt++;
                                          twoDarray[row][1] = Integer.toString(tempInt);
                                          break;
                                                }
                                  }
                                    catch(Exception e)
                                    {
                                        twoDarray[row-checkedRow][0]=productCode;
                                        twoDarray[row-checkedRow][1]="1";
                                        itemExist=true;
                                        break;
                                    }
               }

            checkFirst();
            PostApi postApi = new PostApi();
            Map<String, Object> resp = postApi.fire(Endpoint.ORDER_ADD, addCartRequest);

            // assign order code
            orderCode = (String) resp.get("orderCode");

            // add item count to checkout
            count++;
            btnCheckout.setText("Checkout (" + count + ")");
            btnCheckout.setEnabled(true);
        });

        btnRemove.setOnClickListener(view -> {
            RemoveCartRequest removeCartRequest = new RemoveCartRequest(productCode, orderCode);
              for(row=0; row<= twoDarray.length; row++) {

                  try {
                      if (twoDarray[row][0].equals(productCode)) {

                          int tempInt = Integer.parseInt(twoDarray[row][1]);
                          tempInt--;
                          System.out.println(productCode);
                          if (tempInt == 0) {
                              twoDarray[row][0] = null;
                              twoDarray[row][1] = null;
                              itemExist=false;
                              removeLast();
                              break;
                          } else {

                              twoDarray[row][1] = Integer.toString(tempInt);
                              itemExist=true;
                              break;
                          }
                      }
                  }
                  catch(Exception e)
                  {

                  }
              }

            PostApi postApi = new PostApi();
            Map<String, Object> resp = postApi.fire(Endpoint.ORDER_REMOVE, removeCartRequest);
            orderCode = (String) resp.get("orderCode");

            // reduce item count to checkout
                count --;
            if(count>0)
            {
                btnCheckout.setText("Checkout (" + count + ")");
                btnCheckout.setEnabled(true);
            }


        });
    }
    // need new class to calculate checkout items on the button
    @Override
    public void onClick(View v) {

  }

    /**
     * scan function
     */
    private void initialiseDetectorsAndSources() {
        btnStart.setVisibility(View.INVISIBLE);
        btnStart.setEnabled(false);
        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();
        Context context;
        SurfaceView surfaceView = new SurfaceView (this);
        frameLayout.addView(surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    txtProductDetail.post(() -> {
                        intentData = barcodes.valueAt(0).displayValue;

                        ScanRequest scanRequest = new ScanRequest();
                        scanRequest.setBarCode(intentData);

                        PostApi postApi = new PostApi();
                        Map<String, Object> resp = postApi.fire(Endpoint.SCAN_BARCODE, scanRequest);
                        if (resp.containsKey("ERROR")) {
                            txtProductDetail.setText("Fail to Scan Product");
                            frameLayout.removeAllViews();
                            btnAdd.setEnabled(false);
                            btnRemove.setEnabled(false);
                            btnStart.setVisibility(VISIBLE);
                            btnStart.setEnabled(true);
                            btnStart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogCount=0;
                                    initialiseDetectorsAndSources();
                                }
                            });
                            noItem();
                        } else {
                            txtProductDetail.setText(buildProductTxt(resp));
                            productCode = intentData;
                            btnAdd.setEnabled(true);
                            checkFirst();
                            frameLayout.removeAllViews();
                            btnStart.setVisibility(VISIBLE);
                            btnStart.setEnabled(true);
                            for (int row = 0; row<twoDarray.length; row++) {
                                try {
                                    if(twoDarray[row][0].equals(productCode))
                                    {
                                        int tempInt = Integer.parseInt(twoDarray[row][1]);
                                        tempInt++;
                                        twoDarray[row][1] = Integer.toString(tempInt);
                                        break;
                                    }
                                }
                                catch(Exception e)
                                {
                                    btnRemove.setEnabled(false);
                                }
                            }
                            btnStart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogCount=0;
                                    initialiseDetectorsAndSources();
                                }
                            });

                        }
                    });
                }
            }
        });

    }
    public  void checkFirst()
    {
        if(twoDarray[0][0]!=null)
        {
            btnRemove.setEnabled(true);
        }
        else
        {
            btnRemove.setEnabled(false);
        }
    }
    public void removeLast()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Warning");
        builder.setMessage("Remove Product From Cart");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                btnAdd.performClick();
                System.out.println(count);
                System.out.println(itemExist);
            }
        });
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(itemExist==false)
                {
                    btnRemove.setEnabled(false);
                }
                if(count==0)
                {
                    btnCheckout.setEnabled(false);
                    btnRemove.setEnabled(false);
                    btnCheckout.setText("Checkout");
                }
            }
        });

        //show alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        dialogCount=1;
    }
    public void noItem()
    {
        if(dialogCount==0) {
            //Dialog box for exit confirmation//
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Error");
            builder.setMessage("Product is not in database");


            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }
            });

            //show alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            dialogCount=1;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
//        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    /**
     * @param T intent class
     */
    private void intent(Class T) {
        Intent intent = new Intent(this, T);
        intent.putExtra("orderCode", orderCode);
        startActivity(intent);
    }

    private String buildProductTxt(Map<String, Object> map) {

        String productCode = (String) map.get("productCode");
        String productName = (String) map.get("productName");
        String productDesc = (String) map.get("productDesc");
        String productPrice = (String) map.get("productPrice");

        String resp = "";
        resp = "Product Code: " + productCode + "\n";
        resp += "Product Name: " + productName + "\n";
        resp += "Product Description: " + productDesc + "\n";
        resp += "Product Price: " + productPrice + "\n";

        return resp;
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
