package com.fyp.shoping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.fyp.shoping.R;

public class PaymentResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);
        init();
    }

    private void init() {
        Button btnHome = findViewById(R.id.btnHome);
        TextView paymentResult = findViewById(R.id.paymentResult);

        Intent intent = getIntent();
        boolean success = intent.getBooleanExtra("OK", true);

        if (!success) {
            paymentResult.setText("Payment Failed. Please make order again.");
        }

        btnHome.setOnClickListener(view -> intent(MainActivity.class));
    }

    private void intent(Class T) {
        Intent intent = new Intent(this, T);
        startActivity(intent);
    }
}