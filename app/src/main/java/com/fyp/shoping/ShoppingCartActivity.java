package com.fyp.shoping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyp.shoping.R;
import com.fyp.shoping.http.Endpoint;
import com.fyp.shoping.http.PostApi;
import com.fyp.shoping.model.CheckoutRequest;
import com.fyp.shopping.model.OrderProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShoppingCartActivity extends AppCompatActivity {

    String orderCode, orderAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoping_cart);

        init();
    }

    private void init() {

        Intent intent = getIntent();
        orderCode = intent.getStringExtra("orderCode");

        PostApi postApi = new PostApi();
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setOrderCode(orderCode);
        Map<String, Object> resp = postApi.fire(Endpoint.ORDER_CHECKOUT, checkoutRequest);

        orderAmt = (String) resp.get("orderAmount");
        List rawList = (List) resp.get("orderProductList");

        List<OrderProduct> orderProductList = new ArrayList<>();

        ObjectMapper m = new ObjectMapper();

        for (Object o : rawList) {
            Map<String, Object> mappedObject = m.convertValue(o, new TypeReference<Map<String, String>>() {
            });
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProductName((String) mappedObject.get("productName"));
            orderProduct.setQuantity((String) mappedObject.get("quantity"));
            orderProduct.setUnitPrice((String) mappedObject.get("unitPrice"));
            orderProduct.setTotalPrice((String) mappedObject.get("totalPrice"));

            orderProductList.add(orderProduct);
        }

        int count = 1;
        TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText("No. ");
        tv0.setTextColor(Color.BLACK);
        tv0.setPadding(8,8,8,8);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Product ");
        tv1.setTextColor(Color.BLACK);
        tv1.setPadding(8,8,8,8);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Unit Price ");
        tv2.setTextColor(Color.BLACK);
        tv2.setPadding(8,8,8,8);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Quantity ");
        tv3.setTextColor(Color.BLACK);
        tv3.setPadding(8,8,8,8);
        tbrow0.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Total Price ");
        tv4.setTextColor(Color.BLACK);
        tv4.setPadding(8,8,8,8);
        tbrow0.addView(tv4);
        stk.addView(tbrow0);
        for (OrderProduct op : orderProductList) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText("" + count++);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.CENTER);
            t1v.setPadding(8,8,8,8);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(op.getProductName());
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            t2v.setPadding(8,8,8,8);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(op.getUnitPrice());
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            t3v.setPadding(8,8,8,8);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText("x"+op.getQuantity());
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.CENTER);
            t4v.setPadding(8,8,8,8);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText(op.getTotalPrice());
            t5v.setTextColor(Color.BLACK);
            t5v.setGravity(Gravity.CENTER);
            t5v.setPadding(8,8,8,8);
            tbrow.addView(t5v);
            stk.addView(tbrow);
        }

        Button btnPay = findViewById(R.id.btnPay);
        btnPay.setOnClickListener(view -> intent(CheckoutActivity.class));

    }

    private void intent(Class T) {
        Intent intent = new Intent(this, T);
        intent.putExtra("orderCode", orderCode);
        intent.putExtra("orderAmount",orderAmt);
        startActivity(intent);
    }
}
