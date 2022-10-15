package com.sonkamble.fdledger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sonkamble.fdledger.Class.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    SharedPreferences sp;
    String user, name, id,img_url,mobile,adhar_no,rd_start_date,rd_matur_date,emi;
    Toolbar toolbar;
    TextView txt_mobile,txt_nm,txt_adhar_no,txt_start_date,txt_matur_date,txt_emi,txt_mature_amt;
    ImageView img_pic;
    //String stud_url="https://althauniversal.com/UMANG/rd_user_details_list.php?name=";
    //String stud_url="https://althauniversal.com/UMANG/rd_passbook_list.php?id=";
    ProgressDialog progressDoalog;
    vehical_recyclerAdapter demo_recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager_demo;
    private RecyclerView recyclerView_demo;
    String SubCodeStr,ontime,offtime,offday,mob,adr,str_cat;
    SearchView searchView;
    TextView txt_ttl_amt,txt_ttl_cnt;
    ProgressBar progressBar;
    ArrayList<HashMap<String, String>> post_arryList;
    String TAG ="main";
    final int UPI_PAYMENT = 0;
    int mature_amt=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);
        //------------------------User Session------------------------------------------
        Bundle b = getIntent().getExtras();
        try {
            user = b.getString("email");
            name = b.getString("name");
            id = b.getString("id");
            img_url = b.getString("img_url");
            mobile = b.getString("mobile");
            adhar_no = b.getString("adhar_no");
            rd_start_date = b.getString("rd_start_date");
            rd_matur_date = b.getString("rd_matur_date");
            emi = b.getString("emi");

            //Toast.makeText(getApplicationContext(), "Welcome-" + "\n" + user + "\n" + "id :" + id + "\n" + "Name :" + name, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }

        if (user == null) {
            // Toast.makeText(getApplicationContext(),"User Id Null...",Toast.LENGTH_LONG).show();
        } else {
            sp = this.getSharedPreferences("PI", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("email", user);
            editor.putString("name", name);
            editor.putString("id", id);
            editor.commit();
        }
        SharedPreferences sp = getSharedPreferences("STUD_DATA", MODE_PRIVATE);
        name = sp.getString("sname", "");
        id = sp.getString("sid", "");
        img_url = sp.getString("img_url", "");
        mobile = sp.getString("mobile", "");
        adhar_no = sp.getString("adhar_no", "");
        rd_start_date = sp.getString("rd_start_date", "");
        rd_matur_date = sp.getString("rd_matur_date", "");
        emi = sp.getString("emi", "");
        //  Toast.makeText(getApplicationContext(), "sfid :" + id + "\n" + "SfName :" + name, Toast.LENGTH_LONG).show();
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        ImageView pay = (ImageView) toolbar.findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payUsingUpi();
            }
        });
        ImageView toolbar_img = (ImageView) toolbar.findViewById(R.id.img_logout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("RD USER DASHBOARD");

        toolbar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Warning");
                builder.setIcon(R.drawable.exit);
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.logoutUser();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

            }
        });

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        txt_ttl_cnt=findViewById(R.id.txt_ttl_cnt);
        txt_ttl_amt=findViewById(R.id.txt_ttl_amt);
        txt_adhar_no=findViewById(R.id.txt_adhar_no);
        txt_start_date=findViewById(R.id.txt_start_date);
        txt_matur_date=findViewById(R.id.txt_matur_date);
        txt_mature_amt=findViewById(R.id.txt_mature_amt);
        txt_emi=findViewById(R.id.txt_emi);
        txt_nm=findViewById(R.id.txt_nm);
        txt_mobile=findViewById(R.id.txt_mobile);
        img_pic=(ImageView) findViewById(R.id.img_pic);
        txt_adhar_no.setText(adhar_no);
        txt_start_date.setText(rd_matur_date);
        int ttl_emi=Integer.parseInt(emi)*12;
        mature_amt=(ttl_emi*18)/100+ttl_emi;
        txt_mature_amt.setText("₹ "+mature_amt+"/-");
        txt_matur_date.setText(rd_start_date);
        txt_emi.setText(emi);
        txt_nm.setText(name);
        txt_mobile.setText(mobile);
        Picasso.get()
                .load(img_url)
                .resize(50, 50)
                .centerCrop()
                .into(img_pic);

        //---------------------------------------------------------------
        //API.passbook=stud_url+id;
        /*  //--------------Search Items----------------
        searchView = (SearchView) findViewById(R.id.grid_searchView);
        //------------------------------------------------------------------------------------------
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() >= 0) {
                    SubCodeStr = newText;
                    SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    Log.d("ssss", SubCodeStr);
                    load_data(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {
                    load_data("");
                } else {
                    load_data("");
                }
                return false;
            }
        });*/

        progressBar=(ProgressBar)findViewById(R.id.pg);
        post_arryList = new ArrayList<HashMap<String, String>>();

        recyclerView_demo=(RecyclerView)findViewById(R.id.recycler_vehical);
        //--------for linear layout--------------
        layoutManager_demo = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        recyclerView_demo.setLayoutManager(layoutManager_demo);
        //---------for grid layout--------------
        // recyclerView_demo.setLayoutManager(new GridLayoutManager(View_Complaint.this,2));

        //------------------------------------------
        demo_recyclerAdapter=new vehical_recyclerAdapter(MainActivity.this,post_arryList);
        recyclerView_demo.setAdapter(demo_recyclerAdapter);

        load_data();
        load_cnt();
        //------------------------------------------------------------------------------------------
    }
    public void load_data()
    {
        {   progressDoalog = new ProgressDialog(MainActivity.this);
            progressDoalog.setMessage("Loading....");
            progressDoalog.show();

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.passbook+id, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressDoalog.dismiss();
                    //Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                    try
                    {
                        if(response != null){
                            progressBar.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject postobject = jsonObject.getJSONObject("posts");
                            String status = postobject.getString("status");
                            if (status.equals("200")) {
                                post_arryList.clear();
                                // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                                JSONArray jsonArray=postobject.getJSONArray("post");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.optJSONObject(i);
                                    if (c != null) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        String  ID = c.getString("ID");
                                        String  RD_AMT = c.getString("RD_AMT");
                                      //  String  RD_PAY_DATE = c.getString("RD_PAY_DATE");
                                        String RD_PAID_DATE=c.getString("RD_PAID_DATE");
                                        String LATE_DAYS=c.getString("LATE_DAYS");
                                        String FINe_AMT=c.getString("FINe_AMT");
                                        String TOT_AMT=c.getString("TOT_AMT");


                                        map.put("ID", ID);
                                        map.put("RD_AMT", RD_AMT);
                                      //  map.put("RD_PAY_DATE", RD_PAY_DATE);
                                        map.put("RD_PAID_DATE", RD_PAID_DATE);
                                        map.put("LATE_DAYS", LATE_DAYS);
                                        map.put("FINe_AMT", FINe_AMT);
                                        map.put("TOT_AMT", TOT_AMT);

                                        post_arryList.add(map);
                                        //json_responce.setText(""+post_arryList);

                                    }
                                }
                            }
                        }
                    }catch (Exception e){}
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);
        }
        if (demo_recyclerAdapter != null) {
            demo_recyclerAdapter.notifyDataSetChanged();

            System.out.println("Adapter " + demo_recyclerAdapter.toString());
        }
    }

    public class vehical_recyclerAdapter extends RecyclerView.Adapter<vehical_recyclerAdapter.DemoViewHolder>
    {
        Context context;
        ArrayList<HashMap<String, String>> img_list;

        public vehical_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> quans_list) {
            this.img_list = quans_list;
            this.context = context;
        }

        @Override
        public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stud_list, parent, false);
            DemoViewHolder ViewHolder = new DemoViewHolder(view);
            return ViewHolder;
        }

        @Override
        public void onBindViewHolder(DemoViewHolder merchantViewHolder, final int position)
        {

            merchantViewHolder.txt_d1.setText(img_list.get(position).get("RD_AMT"));
           // merchantViewHolder.txt_d2.setText(img_list.get(position).get("RD_PAY_DATE"));
            merchantViewHolder.txt_d3.setText(img_list.get(position).get("RD_PAID_DATE"));
            merchantViewHolder.txt_d4.setText(img_list.get(position).get("LATE_DAYS"));
            merchantViewHolder.txt_d5.setText(img_list.get(position).get("FINe_AMT"));
            merchantViewHolder.txt_d6.setText(img_list.get(position).get("TOT_AMT"));

            merchantViewHolder.img_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  /*  AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Do You Want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    id=img_list.get(position).get("ID");
                                   // delete();
                                }
                            });
                    alertDialog.show();*/
                    //  mob=img_list.get(position).get("MOBILE");
                    // check_permission();
                }
            });
        }

        @Override
        public int getItemCount() {
            return img_list.size();
        }

        public class DemoViewHolder extends RecyclerView.ViewHolder
        {    LinearLayout lin;
            ImageView img_call;
            TextView txt_d1,txt_d2,txt_d3,txt_d4,txt_d5,txt_d6;
            public DemoViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.txt_d1 = (TextView) itemView.findViewById(R.id.txt_d1);
               // this.txt_d2 = (TextView) itemView.findViewById(R.id.txt_d2);
                this.txt_d3 = (TextView) itemView.findViewById(R.id.txt_d3);
                this.txt_d4 = (TextView) itemView.findViewById(R.id.txt_d4);
                this.txt_d5 = (TextView) itemView.findViewById(R.id.txt_d5);
                this.txt_d6 = (TextView) itemView.findViewById(R.id.txt_d6);
                this.img_call = (ImageView) itemView.findViewById(R.id.img_call);

            }
        }
    }
    public void load_cnt()
    {
        {

            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.cnt+id, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    //Toast.makeText(getApplicationContext(),"Responce"+response,Toast.LENGTH_LONG).show();
                    try
                    {
                        if(response != null){

                            JSONObject jsonObject = new JSONObject(response.toString());
                            JSONObject postobject = jsonObject.getJSONObject("posts");
                            String status = postobject.getString("status");
                            if (status.equals("200")) {
                                post_arryList.clear();
                                // Toast.makeText(getApplicationContext(),"Success:"+status,Toast.LENGTH_LONG).show();
                                JSONArray jsonArray=postobject.getJSONArray("post");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.optJSONObject(i);
                                    if (c != null) {
                                        HashMap<String, String> map = new HashMap<String, String>();
                                        String  ID = c.getString("ID");
                                        String  RD_CNT = c.getString("RD_CNT");
                                        String  RD_TOTAL_AMT = c.getString("RD_TOTAL_AMT");
                                        String RD_TOTAL_FINE=c.getString("RD_TOTAL_FINE");

                                        txt_ttl_amt.setText(RD_TOTAL_AMT);
                                        txt_ttl_cnt.setText(RD_CNT);

                                    }
                                }
                            }
                        }
                    }catch (Exception e){}
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            MySingleton.getInstance(MainActivity.this).addToRequestque(jsonObjectRequest);
        }

    }
  //  void payUsingUpi(  String name,String upiId, String note, String amount) {
    void payUsingUpi() {
      //  Log.e("main ", "name "+name +"--up--"+upiId+"--"+ note+"--"+amount);
        // main: name pavan n--up--pavan.n.sap@okaxis--Test UPI Payment--5.00
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "umang75885@barodampay")
                .appendQueryParameter("pn", "UMANG FINE INVESMENT SOLUTIONS")
                //.appendQueryParameter("mc", "")
                //.appendQueryParameter("tid", "02125412")
                //.appendQueryParameter("tr", "25584584")
                .appendQueryParameter("tn", "Monthly EMI")
                .appendQueryParameter("am", emi)
                .appendQueryParameter("cu", "INR")
                //.appendQueryParameter("refUrl", "blueapp")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if(null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(MainActivity.this,"No UPI app found, please install one to continue",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );
        /*
       E/main: response -1
       E/UPI: onActivityResult: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPIPAY: upiPaymentDataOperation: txnId=AXI4a3428ee58654a938811812c72c0df45&responseCode=00&Status=SUCCESS&txnRef=922118921612
       E/UPI: payment successfull: 922118921612
         */
        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.e("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.e("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    Log.e("UPI", "onActivityResult: " + "Return data is null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(MainActivity.this)) {
            String str = data.get(0);
            Log.e("UPIPAY", "upiPaymentDataOperation: "+str);
            String paymentCancel = "";
            if(str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if(equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    }
                    else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
                else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(MainActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "payment successfull: "+approvalRefNo);
            }
            else if("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(MainActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "Cancelled by user: "+approvalRefNo);

            }
            else {
                Toast.makeText(MainActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                Log.e("UPI", "failed payment: "+approvalRefNo);

            }
        } else {
            Log.e("UPI", "Internet issue: ");

            Toast.makeText(MainActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

}