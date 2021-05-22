package com.fyp.shoping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.fyp.shoping.R;

@Deprecated
public class PaymentPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        init();

    }

    private void init() {
        Button btnConfirm = findViewById(R.id.btnConfirm);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        ProgressCheck progressCheck = new ProgressCheck(progressBar);

        btnConfirm.setOnClickListener(view -> progressCheck.execute());
    }


    private void intent(Class T) {
        Intent intent = new Intent(this, T);
        startActivity(intent);
    }

    public class ProgressCheck extends AsyncTask<String, String, String> {

        ProgressBar mProgressBar;


        public ProgressCheck(ProgressBar progressbar) {
            this.mProgressBar = progressbar;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBar.setVisibility(View.GONE);
            intent(PaymentResultActivity.class);
        }

        @Override
        protected String doInBackground(String... strings) {

            int min = 1000;
            int max = 10000;
            int rand = (int)Math.floor(Math.random()*(max-min+1)+min);
            try {
                Thread.sleep(rand);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

}