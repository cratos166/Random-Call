package com.nbird.call_random.UNIVERSAL.DIALOG;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nbird.call_random.DATA.AppData;
import com.nbird.call_random.DATA.Constant;
import com.nbird.call_random.MAIN.MainActivity;
import com.nbird.call_random.R;

public class LowBalanceDialog {

    AppData appData;

    Context context;
    View v;
    RewardedAd rewardedAd;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    TextView balanceTextView;
    int k=5;


    public LowBalanceDialog(Context context, View v,TextView balanceTextView) {
        this.context = context;
        this.v = v;
        this.balanceTextView=balanceTextView;

    }

    public void start(){


        appData=new AppData(context);

        AlertDialog.Builder builderRemove=new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        View viewRemove1= LayoutInflater.from(context).inflate(R.layout.dialog_low_balance,(ConstraintLayout) v.findViewById(R.id.layoutDialogContainer),false);
        builderRemove.setView(viewRemove1);
        builderRemove.setCancelable(false);
        Button adButton=(Button) viewRemove1.findViewById(R.id.adButton);
        Button paymentButton=(Button) viewRemove1.findViewById(R.id.paymentButton);

        adButton.setEnabled(false);

        new CountDownTimer(1000*5,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                adButton.setText("WATCH ADS IN "+k);
                k--;
            }

            @Override
            public void onFinish() {
                adButton.setText("WATCH ADS");
                adButton.setEnabled(true);
            }
        }.start();


        balanceTextView = (TextView) ((Activity)context).findViewById(R.id.balanceTextView);



        TextView textDis=(TextView) viewRemove1.findViewById(R.id.textDis);
        textDis.setText("Balance : "+appData.getMyBalance());

        ImageView cancel=(ImageView) viewRemove1.findViewById(R.id.cancel);


        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, Constant.REWAD_ADS_ID,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.

                        Toast.makeText(context, loadAdError.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                       // Toast.makeText(context, "Ad was loaded", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Ad was loaded.");
                    }
                });






        MobileAds.initialize(context);
        AdLoader adLoader = new AdLoader.Builder(context, Constant.NATIVE_ID)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        ColorDrawable cd = new ColorDrawable(0x393F4E);

                        NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(cd).build();
                        TemplateView template = viewRemove1.findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                        template.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());


        final AlertDialog alertDialog=builderRemove.create();
        if(alertDialog.getWindow()!=null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        try{
            alertDialog.show();
        }catch (Exception e){

        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }
            }
        });


        try{
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.




                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Toast.makeText(context, "Ads failed to load", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    rewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.



                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        }catch (Exception e){

        }

        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    alertDialog.dismiss();
                }catch (Exception e){

                }


                if (rewardedAd != null) {
                    Activity activityContext = (Activity) context;
                    rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.

                            Toast.makeText(context, "You earned "+Constant.REWARD_AD_MONEY+" coins", Toast.LENGTH_SHORT).show();

                            appData.setMyBalance(appData.getMyBalance()+Constant.REWARD_AD_MONEY);

                            myRef.child("USER").child(appData.getMyUID()).child("balance").setValue(appData.getMyBalance()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                }
                            });


                            balanceTextView.setText(String.valueOf(appData.getMyBalance()));



                        }
                    });
                } else {
                    Toast.makeText(context, "Ad failed to show fullscreen content", Toast.LENGTH_SHORT).show();
                }





            }
        });

    }




}
