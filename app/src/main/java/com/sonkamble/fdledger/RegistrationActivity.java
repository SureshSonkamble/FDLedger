package com.sonkamble.fdledger;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button btn_stud_registrtion;
    EditText edt_agent_code,edt_name,edt_addr,edt_password,edt_mobile,edt_occupation,edt_acc_no,edt_adhar_no,edt_pan_no,edt_nom_addr,edt_nom_adhar_no,edt_nom_pan_no;
    Spinner spinner_gender,spinner_emi;
    String str_gender,str_emi,str_term,str_name,str_agent_code,str_dob,str_addr,str_password,str_mobile,str_occupation,str_acc_no,str_adhar_no,str_pan_no,str_nom_addr,str_nom_adhar_no,str_nom_pan_no,str_start_date,str_mature_date;
    ProgressDialog progressDoalog;
    TextView txt_date,btn_take_pic,txt_rd_mature_date,txt_rd_start_date,terms;
    ImageView img_date,img_rd_start_date,img_rd_mature_date;
    int mYear, mMonth, mDay;
    private ImageView imageview;
    CheckBox chk_term;
    private static final String IMAGE_DIRECTORY = "/TEST_CAM_PIC";
    private int GALLERY = 1, CAMERA = 2;
    Bitmap bitmap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ByteArrayOutputStream bytes;
   // String URL="http://althauniversal.com/UMANG/rd_register.php";
  //  String registration_url="http://192.168.29.252/WS/STUD/insert_test.php?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("User Registrtion");

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        btn_take_pic = (TextView) findViewById(R.id.btn_take_pic);

        chk_term=findViewById(R.id.chk_term);
        imageview=findViewById(R.id.iv);
        edt_agent_code=(EditText)findViewById(R.id.edt_agent_code);
        edt_name=(EditText)findViewById(R.id.edt_name);
        edt_addr=(EditText)findViewById(R.id.edt_addr);
        edt_password=(EditText)findViewById(R.id.edt_password);
        edt_mobile=(EditText)findViewById(R.id.edt_mobile);
        edt_occupation=(EditText)findViewById(R.id.edt_occupation);
        edt_acc_no=(EditText)findViewById(R.id.edt_acc_no);
        edt_adhar_no=(EditText)findViewById(R.id.edt_adhar_no);
        edt_pan_no=(EditText)findViewById(R.id.edt_pan_no);
        edt_nom_addr=(EditText)findViewById(R.id.edt_nom_addr);
        edt_nom_adhar_no=(EditText)findViewById(R.id.edt_nom_adhar_no);
        edt_nom_pan_no=(EditText)findViewById(R.id.edt_nom_pan_no);


        terms=(TextView) findViewById(R.id.terms);
        terms.setPaintFlags(terms.getPaintFlags() |  Paint.UNDERLINE_TEXT_FLAG);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertWebView();

            }
        });
        txt_date=(TextView) findViewById(R.id.txt_date);
        txt_rd_start_date=(TextView) findViewById(R.id.txt_rd_start_date);
        txt_rd_mature_date=(TextView) findViewById(R.id.txt_rd_mature_date);
        img_date=(ImageView) findViewById(R.id.img_date);

        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        //----------Gender--------------------------------------------------------
        spinner_gender=(Spinner)findViewById(R.id.spinner_gender);
        // Spinner Drop down elements
        List<String> list = new ArrayList<String>();

        list.add("Select Gender");
        list.add("Male");
        list.add("Female");
        list.add("other");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_gender.setAdapter(dataAdapter);

        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                str_gender = adapterView.getItemAtPosition(position).toString();
                // Toast.makeText(getApplicationContext(), "Selected: " + chk_cat_type, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
         //----------Gender--------------------------------------------------------
        spinner_emi=(Spinner)findViewById(R.id.spinner_emi);
        // Spinner Drop down elements
        List<String> lists = new ArrayList<String>();

        lists.add("Select Monthly RD Amount");
        lists.add("500");
        lists.add("1000");
        lists.add("2000");
        lists.add("3000");
        lists.add("4000");
        lists.add("5000");
        lists.add("7500");
        lists.add("10000");
        lists.add("12500");
        lists.add("15000");
        lists.add("20000");
        lists.add("25000");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapters = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, lists);
        // Drop down layout style - list view with radio button
        dataAdapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner_emi.setAdapter(dataAdapters);

        spinner_emi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                str_emi = adapterView.getItemAtPosition(position).toString();
                // Toast.makeText(getApplicationContext(), "Selected: " + chk_cat_type, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        img_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                //txt_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);
                                txt_date.setText(""+year + "/" +  (monthOfYear + 1) + "/" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        img_rd_start_date=findViewById(R.id.img_rd_start_date);
        img_rd_start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                              //  txt_rd_start_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);
                                txt_rd_start_date.setText(""+year + "/" +  (monthOfYear + 1) + "/" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        txt_rd_start_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

                //create instance of the Calendar class and set the date to the given date
                Calendar cal = Calendar.getInstance();
                try{
                    cal.setTime(sdf.parse(txt_rd_start_date.getText().toString()));
                }catch(ParseException e){
                    e.printStackTrace();
                }

                // use add() method to add the days to the given date
                cal.add(Calendar.YEAR, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                String dateAfter = sdf.format(cal.getTime());
                txt_rd_mature_date.setText(dateAfter);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        img_rd_mature_date=findViewById(R.id.img_rd_mature_date);
/*
        img_rd_mature_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                               // txt_rd_mature_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);
                                txt_rd_mature_date.setText(""+year + "/" +  (monthOfYear + 1) + "/" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
*/
        //=======================================================================================

        btn_stud_registrtion=(Button)findViewById(R.id.btn_stud_registrtion);
        btn_stud_registrtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edt_agent_code.getText().toString().equals("")||edt_name.getText().toString().equals("")||edt_addr.getText().toString().equals("")||edt_password.getText().toString().equals("")
                ||edt_mobile.getText().toString().equals("")||edt_occupation.getText().toString().equals("")||edt_acc_no.getText().toString().equals("")
                ||edt_adhar_no.getText().toString().equals("")||edt_pan_no.getText().toString().equals("")||edt_nom_addr.getText().toString().equals("")
                ||edt_nom_adhar_no.getText().toString().equals("")||edt_nom_pan_no.getText().toString().equals(""))
                {
                    Toast.makeText(RegistrationActivity.this, "Value Can Not Be Null", Toast.LENGTH_SHORT).show();
                }
                else if(str_emi.equals("Select Monthly RD Amount"))
                {
                    Toast.makeText(RegistrationActivity.this, "Plese Select RD Amount", Toast.LENGTH_SHORT).show();
                }
                else  if(chk_term.isChecked())
                  {
                      str_term="YES";
                      UploadImage();
                  }else
                  {
                      Toast.makeText(RegistrationActivity.this, "Please Accept Terms And Condition.", Toast.LENGTH_SHORT).show();
                  }
                  
            }
        });
        TextView signIn_text = findViewById(R.id.signIn_text);
        signIn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, Login.class));
                finish();
            }
        });

    }
        private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                check_permission();
                                //takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    public void check_permission() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                takePhotoFromCamera();

            } else {
                requestPermission(); // Code for permission
            }
        } else {
            takePhotoFromCamera();
            Toast.makeText(RegistrationActivity.this, "Below 23 API Oriented Device No Permission ....", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.CAMERA);
        int result1 = ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(RegistrationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED&& result1 == PackageManager.PERMISSION_GRANTED&& result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.CAMERA)&& ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)&& ActivityCompat.shouldShowRequestPermissionRationale(RegistrationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(RegistrationActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RegistrationActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(RegistrationActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }
    public String saveImage(Bitmap myBitmap) {
        bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

   /* public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }*/

    public String encode_img(Bitmap bm){

        byte[] imagebyte = bytes.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }
    private void UploadImage(){
        progressDoalog = new ProgressDialog(RegistrationActivity.this);
        progressDoalog.setMessage("Uploading please wait....");
        progressDoalog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDoalog.dismiss();
                String s = response.trim();
               // Toast.makeText(RegistrationActivity.this, ""+s, Toast.LENGTH_SHORT).show();
                if(s.equalsIgnoreCase("Loi")){
                    Toast.makeText(RegistrationActivity.this, "Loi", Toast.LENGTH_SHORT).show();
                }else{
                    Send_SMS();
                    Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this, error+"", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String title= edt_name.getText().toString();
                title=title.replaceAll("\\s+","");
                Log.d("title",title);
                // String date= txt_date.getText().toString();
               // String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String image = encode_img(bitmap);
                //  String image = ""+bitmap;

                str_agent_code= edt_agent_code.getText().toString();
                str_addr= edt_addr.getText().toString();
                str_password= edt_password.getText().toString();
                str_mobile= edt_mobile.getText().toString();
                str_occupation= edt_occupation.getText().toString();
                str_acc_no= edt_acc_no.getText().toString();
                str_adhar_no= edt_adhar_no.getText().toString();
                str_pan_no= edt_pan_no.getText().toString();
                str_nom_addr= edt_nom_addr.getText().toString();
                str_nom_adhar_no= edt_nom_adhar_no.getText().toString();
                str_nom_pan_no= edt_nom_pan_no.getText().toString();
                str_mature_date=txt_rd_mature_date.getText().toString();
                str_start_date=txt_rd_start_date.getText().toString();
                str_dob=txt_date.getText().toString();
                Map<String ,String> params = new HashMap<String,String>();
                params.put("img_url",image);
                params.put("name",title);
                params.put("agent_code",str_agent_code);
                params.put("address",str_addr);
                params.put("password",str_password);
                params.put("mobile",str_mobile);
                params.put("gender",str_gender);
                params.put("dob",str_dob);
                params.put("occupation",str_occupation);
                params.put("acc_no",str_acc_no);
                params.put("adhar_no",str_adhar_no);
                params.put("pan_no",str_pan_no);
                params.put("nom_addr",str_nom_addr);
                params.put("nom_adhar",str_nom_adhar_no);
                params.put("nom_pan",str_nom_pan_no);
                params.put("rd_matur_date",str_mature_date);
                params.put("rd_start_date",str_start_date);
                params.put("emi",str_emi);
                params.put("$term",str_term);
              /*  Log.d("ssss",image);
                Log.d("ssss",title);
                Log.d("ssss",str_addr);
                Log.d("ssss",str_password);
                Log.d("ssss",str_mobile);
                Log.d("ssss",str_gender);
                Log.d("ssss",str_dob);
                Log.d("ssss",str_occupation);
                Log.d("ssss",str_acc_no);
                Log.d("ssss",str_adhar_no);
                Log.d("ssss",str_pan_no);
                Log.d("ssss",str_nom_addr);
                Log.d("ssss",str_nom_adhar_no);
                Log.d("ssss",str_nom_pan_no);
                Log.d("ssss",str_mature_date);
                Log.d("ssss",str_start_date);
                Log.d("ssss",str_emi);*/
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void refresh()
    {
        Intent refresh = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(refresh);
        finish();
    }
    void Send_SMS()
    {
        JsonObjectRequest objectRequest=new JsonObjectRequest(Request.Method.GET, API.sms+str_mobile, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                refresh();
                Toast.makeText(RegistrationActivity.this, ""+response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestque(objectRequest);

    }



    public void alertWebView() {
        // WebView is created programatically here.
        WebView myWebView = new WebView(RegistrationActivity.this);
        myWebView.loadUrl("https://althauniversal.com/UMANG/term.html");
        /*
         * This part is needed so it won't ask the user to open another browser.
         */
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        new AlertDialog.Builder(RegistrationActivity.this).setView(myWebView)
                .setTitle("Terms And Conditions")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                }).show();
    }


}