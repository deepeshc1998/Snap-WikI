package com.example.snapwiki2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
   ImageView scanner_icon;
   private Button button;

    int REQUEST_CODE = 1;
    private ImageView captureIV;
    private Button snapBtn, getsearchResulatsBtn;
    private RecyclerView resultsRv;
    private SearchRVAdapter searchRVAdapter;
    private ArrayList<SearchRVModal> searchRVModalArrayList;
    private ProgressBar loadingPB;
    private Bitmap imageBitmap;
    String title,link,displayedLink,snippet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureIV =findViewById(R.id.image);
        snapBtn = findViewById(R.id.idBtnSnap);
        getsearchResulatsBtn = findViewById(R.id.idBtnResults);
        resultsRv = findViewById(R.id.idRVSearchResults);
        loadingPB = findViewById(R.id.idPBLoading);
        searchRVModalArrayList= new ArrayList<>();
        searchRVAdapter= new SearchRVAdapter(this, searchRVModalArrayList);
        resultsRv.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
        resultsRv.setAdapter(searchRVAdapter);
        scanner_icon=findViewById(R.id.image);
        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent);
            }
        });

        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRVModalArrayList.clear();
                searchRVAdapter.notifyDataSetChanged();
                takePictureIntent();
            }
        });

        getsearchResulatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRVModalArrayList.clear();
                searchRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.VISIBLE);
                getResults();
            }
        });
        scanner_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent);
            }
        });


    }
    private void getResults(){
        FirebaseVisionImage image =FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionImageLabeler labeler =FirebaseVision.getInstance().getOnDeviceImageLabeler();

        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>(){
        @Override
        public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
            String searchQuery = firebaseVisionImageLabels.get(0).getText();
            getSearchResults(searchQuery);
        }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            Toast.makeText(MainActivity.this,"Fail to detect Image...",Toast.LENGTH_SHORT).show();
        }
       });
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            captureIV.setImageBitmap(imageBitmap);

        }
    }

    private void getSearchResults(String searchQuery) {
    String url="https://serpapi.com/search.json?q="+searchQuery+"&location=Delhi,india&hi=en&google_domain=google.com&api_key=07d60fca70177a852dcb12431aaa45183d3e93c33987a9aed8304782f44c8fae";
    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            loadingPB.setVisibility(View.GONE);
            try {
                JSONArray organicArray = response.getJSONArray("organic_results");
                for(int i=0; i<organicArray.length(); i++) {
                    JSONObject organicObj = organicArray.getJSONObject(i);
                    if (organicObj.has("title")){
                        title = organicObj.getString("title");
                    }
                    if (organicObj.has("link")) {
                        link = organicObj.getString("link");
                    }
                    if (organicObj.has("displayed_link")) {
                        displayedLink = organicObj.getString("displayed_link");
                    }
                    if (organicObj.has("snippet")) {
                        snippet = organicObj.getString("snippet");
                    }
                    searchRVModalArrayList.add(new SearchRVModal(title,link,displayedLink,snippet));
                }
                searchRVAdapter.notifyDataSetChanged();
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }, new Response.ErrorListener() {
        @Override
            public void onErrorResponse(VolleyError error) {
            Toast.makeText(MainActivity.this, "No results found...",Toast.LENGTH_SHORT).show();
        }
        });
        queue.add(jsonObjectRequest);
    }


    private void takePictureIntent() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i,REQUEST_CODE );
        }
    }
}