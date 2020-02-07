package com.app.xit.home.signature;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.xit.R;
import java.io.File;


public class SignatureDialog extends AppCompatActivity {
    Activity mActivity;
    Path mPath;
    String pic_name = "csign";
    String StoredPath;
    String downloadDirectory;

    //    String StoredPath = AppConstants.downloadDirectory + "/" + pic_name + ".png";
    Signature mSignature;
    private String imageBase64;
    private BeanManager beanManager;

    public final static String IMAGE_BASE_64 = "IMAGE_BASE_64";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signature);
        mActivity = SignatureDialog.this;

        downloadDirectory = Environment.getExternalStorageDirectory().getPath() ;
        StoredPath = Environment.getExternalStorageDirectory().getPath() + "/xit" + pic_name + ".png";

//        getSupportActionBar().setTitle("Customer Signature");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

 /*       supportActionBar?.title = "Sample Collection"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/


        beanManager = BeanManager.getInstance();
        Bundle bundle = getIntent().getExtras();
        String isHiv = "0";
        if (bundle != null) {
            isHiv = bundle.getString("isHiv");
        }
        Log.d("Xit", "isHiv : " + isHiv);
        Button mClear = (Button) findViewById(R.id.clear);
        final Button getSign = (Button) findViewById(R.id.getsign);

        Button mCancel = (Button) findViewById(R.id.cancel);
        LinearLayout mContent = (LinearLayout) findViewById(R.id.linearLayout);

        if (savedInstanceState == null) {
            mSignature = new Signature(mActivity, null, getSign);
        } else {
            Log.d("Xit", "getSign.isEnabled() :" + getSign.isEnabled());
            mSignature = new Signature(mActivity, null, getSign, (Path) beanManager.getImagePath());

        }
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        try {
            File file = new File(downloadDirectory);

            if (file.mkdirs()) {
                Log.d("signatureDialog", "Directory %s has been created. " + file.getAbsolutePath());
            } else if (file.isDirectory()) {
                Log.d("signatureDialog", "Directory %s has already been created. " + file.getAbsolutePath());
            } else {
                Log.d("signatureDialog", "Directory %s could not be created. " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.d("signatureDialog", "Directory e exception : " + e.getMessage());
        }


        final LinearLayout view = mContent;

        mClear.setOnClickListener(v -> {
            Log.v("log_tag", "Panel Cleared");
            mSignature.clear();
            getSign.setEnabled(false);
        });

        getSign.setOnClickListener(v -> {
            view.setDrawingCacheEnabled(true);
            mSignature.save(view, StoredPath);
            imageBase64 = mSignature.getImageString();
            Log.d("Xit", "imageBase64 : " + imageBase64);
            Intent intent = new Intent();
            intent.putExtra(IMAGE_BASE_64, imageBase64);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
            Toast.makeText(mActivity, "Successfully Saved", Toast.LENGTH_SHORT).show();
        });

        /*getSign.setOnClickListener(new SafeClickListener(v -> {
            view.setDrawingCacheEnabled(true);
            mSignature.save(view, StoredPath);
            imageBase64 = mSignature.getImageString();
            Log.d("Xit", "imageBase64 : " + imageBase64);
            Intent intent = new Intent();
            intent.putExtra(AppConstants.IMAGE_BASE_64, imageBase64);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
            Toast.makeText(mActivity, "Successfully Saved", Toast.LENGTH_SHORT).show();

        }));
*/
        mCancel.setOnClickListener(v -> {
            Log.v("log_tag", "Panel Canceled");
            mActivity.finish();

        });
     /*   mCancel.setOnClickListener(new SafeClickListener(v -> {
            Log.v("log_tag", "Panel Canceled");
            mActivity.finish();
        }));*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSignature != null) {
            mPath = mSignature.getPath();
            beanManager.setImagePath(mPath);
            Log.v("Xit", "onSaveInstanceState mPath : " + mPath);
        }
    }
}

