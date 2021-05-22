package com.fyp.shoping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fyp.shoping.R;
import com.fyp.shoping.http.Endpoint;
import com.fyp.shoping.http.PostApi;
import com.fyp.shopping.model.PaymentRequest;

import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    EditText etCardNumber, etExpireMonth, etExpireYear, etCvv;
    Button btnCheckout;
    boolean t1, t2, t3, t4;
    Dialog dialog;
    boolean result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        init();
    }

    private void init() {
        btnCheckout = findViewById(R.id.btnChk);
        btnCheckout.setEnabled(false);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpireMonth = findViewById(R.id.etExpireMonth);
        etExpireYear = findViewById(R.id.etExpireYear);
        etCvv = findViewById(R.id.etExpireCvv);

        Intent intent = getIntent();

        textListener();
        dialog = new Dialog(this);

        String orderAmount = intent.getStringExtra("orderAmount");

        btnCheckout.setText("PAY NOW RM " + orderAmount);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.startLoadingDialog();
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        PaymentRequest paymentRequest = new PaymentRequest();
                        String orderCode = intent.getStringExtra("orderCode");
                        paymentRequest.setOrderCode(orderCode);

                        PostApi postApi = new PostApi();
                        Map<String, Object> resp = postApi.fire(Endpoint.ORDER_PAYMENT, paymentRequest);
                        result = resp.containsKey("OK");
                        dialog.dismiss();
                        intent(PaymentResultActivity.class);
                    }
                });
            }
        });
    }

    private void textListener() {

        etCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etCardNumber.length() != 16) {
                    etCardNumber.setError("Please insert 16 digit card number!");
                    t1 = false;
                } else {
                    etCardNumber.setError(null);
                    t1 = true;
                }

                btnCheckoutClick();
            }
        });

        etExpireMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etExpireMonth.length() != 2) {
                    etExpireMonth.setError("Please insert 2 digit expire month!");
                    t2 = false;
                } else {
                    etExpireMonth.setError(null);
                    t2 = true;
                }

                btnCheckoutClick();
            }
        });

        etExpireYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etExpireYear.length() != 2) {
                    etExpireYear.setError("Please insert 2 digit expire year!");
                    t3 = false;
                } else {
                    etExpireYear.setError(null);
                    t3 = true;
                }

                btnCheckoutClick();
            }
        });

        etCvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etCvv.length() != 3) {
                    etCvv.setError("Please insert 3 digit expire year!");
                    t4 = false;
                } else {
                    etCvv.setError(null);
                    t4 = true;
                }

                btnCheckoutClick();
            }
        });
    }

    private void btnCheckoutClick() {
        if (pass()) {
            btnCheckout.setEnabled(true);
        } else {
            btnCheckout.setEnabled(false);
        }
    }

    private boolean pass() {
        return t1 && t2 && t3 && t4;
    }

    private void intent(Class T) {
        Intent intent = new Intent(this, T);
        intent.putExtra("OK", result);
        startActivity(intent);
    }

}
