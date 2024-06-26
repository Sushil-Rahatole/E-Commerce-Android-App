package com.example.splashactivity;

import static com.example.splashactivity.ProductDetailsActivity.ratenowcontainer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {
private int position;
private TextView title,price,quantity;
private ImageView productImage,orderedIndicator,packedIndicator,shippedIndicator,deliveredIndicator;
private ProgressBar O_P_progress,P_S_progress,S_D_progress;
private  TextView orderedTitle,packedTitle,shippedTitle,deliveredTitle;
    private  TextView orderedDate,packedDate,shippedDate,deliveredDate;
    private  TextView orderedBody,packedBody,shippedBody,deliveredBody;
    private LinearLayout rateNowContainer;
    private  int rating;
    private TextView fullName,address,pinCode;
    private TextView totalItemsPrice,deliveryPrice,totalAmount,totalItems;
    private TextView savedAmount;
    private  Dialog loadingDialog,cancelDialog;
    private SimpleDateFormat simpleDateFormat;// it was upto this
    private Button cancelOrderBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //loading dialog
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setCancelable(false);
//        loadingDialog.getWindow().setBackgroundDrawable(DeliveryActivity.this.getDrawable(R.drawable.slider_background));// it was alag
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));

        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loading dialog

        //cancel dialog
        cancelDialog = new Dialog(OrderDetailsActivity.this);
        cancelDialog.setCancelable(true);
        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        //cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //cancel dialog

        position=getIntent().getIntExtra("Position",-1);
        MyOrderItemModel model=DBqueries.myOrderItemModelList.get(position);

        title=findViewById(R.id.productTitle);
        price=findViewById(R.id.productPrice);
        quantity=findViewById(R.id.productQuantity);
        productImage=findViewById(R.id.productImage1);

        cancelOrderBtn=findViewById(R.id.cancel_Btn);

        orderedIndicator=findViewById(R.id.orderIndicator);
        packedIndicator=findViewById(R.id.packed_indicator);
        shippedIndicator=findViewById(R.id.shipping_indicator);
        deliveredIndicator=findViewById(R.id.deliverd_indicator);

        O_P_progress=findViewById(R.id.ordered_packed_progressbar);
        P_S_progress=findViewById(R.id.packed_shipping_progrees);
        S_D_progress=findViewById(R.id.shipping_delivered_progress);

        orderedTitle=findViewById(R.id.ordered_title);
        packedTitle=findViewById(R.id.packed_title);
        shippedTitle=findViewById(R.id.shipping_title);
        deliveredTitle=findViewById(R.id.delivered_title);

        orderedDate=findViewById(R.id.ordered_date);
        packedDate=findViewById(R.id.packed_date);
        shippedDate=findViewById(R.id.shipping_date);
        deliveredDate=findViewById(R.id.deliverde_date);

        orderedBody=findViewById(R.id.ordered_body);
        packedBody=findViewById(R.id.packed_body);
        shippedBody=findViewById(R.id.shipping_body);
        deliveredBody=findViewById(R.id.delivered_body);

        rateNowContainer=findViewById(R.id.ratenow);

        fullName=findViewById(R.id.fullname);
        address=findViewById(R.id.address);
        pinCode=findViewById(R.id.pincode);

        totalAmount=findViewById(R.id.total_price);
        totalItemsPrice=findViewById(R.id.total_items_price);
        deliveryPrice=findViewById(R.id.delivery_price);

        totalItems=findViewById(R.id.total_items);
        savedAmount=findViewById(R.id.saved_amount);


        title.setText(model.getProductTitle());
        if(!model.getDiscountedPrice().equals("")){
            price.setText("Rs. "+model.getDiscountedPrice()+"/-");
        }else{
            price.setText("Rs. "+model.getProductPrice()+"/-");
        }
        quantity.setText("Qty :"+String.valueOf(model.getProductQuantity()));


        Glide.with(this).load(model.getProductImage()).into(productImage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            simpleDateFormat =new SimpleDateFormat("EEE,dd MMM YYYY hh:mm aa");
        }

        switch (model.getOrderStatus()){
            case  "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setTextColor(Color.BLACK);
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate())));



                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case  "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate())));


                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate().toDate())));

                P_S_progress.setVisibility(View.GONE);
                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setProgress(100);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);



                break;
            case "Shipped":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate())));  //ordered


                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate().toDate()))); //packedDate

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate()))); //shipped date

                S_D_progress.setVisibility(View.GONE);
                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case  "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate()))); //ordered date


                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));//packed date
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate().toDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));//shipeddate
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen))); //deliverddate
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate())));


                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Your order is out for delivery");

                break;
            case  "Delivered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate()))); //ordered date


                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));//packed date
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate().toDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));//shipeddate
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen))); //deliverddate
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate().toDate())));


                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                break;
            case  "Cancelled":
                if (model.getPackedDate().toDate().after(model.getOrderedDate().toDate())){
                    if (model.getShippedDate().toDate().after(model.getPackedDate().toDate())){

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate()))); //ordered


                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));//packedDate

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate()))); //shipped

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.btnRed)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));//cancelled
                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been Cancelled.");

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setProgress(100);
                    }else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate()))); //ordered date


                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate()))); //packed date

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.btnRed)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate()))); //cancelled date
                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been Cancelled.");

                        S_D_progress.setVisibility(View.GONE);
                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                    }
                }else{
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));//ordered date


                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.btnRed)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate()))); // cancelled date

                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been Cancelled.");

                    P_S_progress.setVisibility(View.GONE);
                    S_D_progress.setVisibility(View.GONE);
                    O_P_progress.setProgress(100);

                    shippedIndicator.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                }
                break;
        }
///rating layout



        rating=model.getRating();
        setRating(rating);
        for(int x=0;x<rateNowContainer.getChildCount();x++){
            final int starposition=x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadingDialog.show();// ith nahi aal static ch tikade aal
                    setRating(starposition);
                    DocumentReference documentReference= FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductId());
                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot documentSnapshot=transaction.get(documentReference);
                            if (rating!=0){
                                Long increase=documentSnapshot.getLong(starposition+1+"_star")+1;
                                Long decrease=documentSnapshot.getLong(rating+1+"_star")-1;
                                transaction.update(documentReference,starposition+1+"_star",increase);
                                transaction.update(documentReference,rating+1+"_star",decrease);
                            }else{
                                Long increase=documentSnapshot.getLong(starposition+1+"_star")+1;
                                transaction.update(documentReference,starposition+1+"_star",increase);
                            }
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            Map<String, Object> myRating = new HashMap<>();

                            if (DBqueries.myRatedIds.contains(model.getProductId())) {

                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(model.getProductId()), (long) starposition + 1);


                            } else {
                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), model.getProductId());
                                myRating.put("rating_" + DBqueries.myRatedIds.size(), (long) starposition + 1);

                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS").update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        DBqueries.myOrderItemModelList.get(position).setRating(starposition);
                                        if (DBqueries.myRatedIds.contains(model.getProductId())){
                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(model.getProductId()),Long.parseLong(String.valueOf(starposition+1)));
                                        }else{
                                            DBqueries.myRatedIds.add(model.getProductId());
                                            DBqueries.myRating.add(Long.parseLong(String.valueOf(starposition+1)));
                                        }


                                    }else {
                                        String error=task.getException().getMessage();
                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }// extra
                                    loadingDialog.dismiss();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                        }
                    });
                }


            });

        }
        //rating layout

        if (model.isCancellationRequested()){
            cancelOrderBtn.setVisibility(View.VISIBLE);
            cancelOrderBtn.setEnabled(false);
            cancelOrderBtn.setText("Cancellation in process.");
            cancelOrderBtn.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
            cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        }else{
            if (model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")){
                cancelOrderBtn.setVisibility(View.VISIBLE);
                cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        cancelDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelDialog.dismiss();
                            }
                        });
                        cancelDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelDialog.dismiss();
                                loadingDialog.show();
                                Map<String,Object> map = new HashMap<>();
                                map.put("Order Id",model.getOrderID());
                                map.put("product Id",model.getProductId());
                                map.put("Order Cancelled",false);
                                FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderID()).collection("OrderItems").document(model.getProductId()).update("Cancellation requested",true)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        model.setCancellationRequested(true);
                                                                        cancelOrderBtn.setEnabled(false);
                                                                        cancelOrderBtn.setText("Cancellation in process.");
                                                                        cancelOrderBtn.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_primary));
                                                                        cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                                    }else{
                                                                        String error =task.getException().getMessage();
                                                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });

                                                }else{
                                                    loadingDialog.dismiss();
                                                    String error =task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                        cancelDialog.show();
                    }
                });
            }
        }

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pinCode.setText(model.getPincode());
        totalItems.setText("Price("+model.getProductQuantity()+" items)");
        Long totalItemsPriceValue;
        if (model.getDiscountedPrice().equals("")){
            totalItemsPriceValue=model.getProductQuantity()*Long.valueOf(model.getProductPrice());
            totalItemsPrice.setText("Rs."+totalItemsPriceValue+"/-");

        }else{
            totalItemsPriceValue=model.getProductQuantity()*Long.valueOf(model.getDiscountedPrice());
            totalItemsPrice.setText("Rs."+totalItemsPriceValue+"/-");
        }
        if (!model.getDeliveryPrice().equals("FREE")){
            deliveryPrice.setText("Rs."+model.getDeliveryPrice()+"/-");
            totalAmount.setText("Rs."+(totalItemsPriceValue+Long.valueOf(model.getDeliveryPrice()))+"/-");
        }else{
            deliveryPrice.setText("Rs."+model.getDeliveryPrice()+"/-");
        }
        if (!model.getCuttedPrice().equals("")){
            if (!model.getDiscountedPrice().equals("")){
                savedAmount.setText("You saved Rs. "+model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice())-Long.valueOf(model.getDiscountedPrice())) +"/- on this order");
            }else{
                savedAmount.setText("You saved Rs. "+model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice())-Long.valueOf(model.getProductPrice())) +"/- on this order");

            }
        }else{
            if (!model.getDiscountedPrice().equals("")){
                savedAmount.setText("You saved Rs. "+model.getProductQuantity()*(Long.valueOf(model.getProductPrice())-Long.valueOf(model.getDiscountedPrice())) +" on this order");

            }else{
                 savedAmount.setText("You saved Rs.0/- on this order");
            }
        }



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setRating(int starposition) {
        for(int x=0;x<rateNowContainer.getChildCount();x++){
            ImageView starBtn=(ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if(x<=starposition){
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }
}