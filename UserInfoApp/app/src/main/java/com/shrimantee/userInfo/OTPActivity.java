package com.shrimantee.userInfo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity automatically retrieves OTP ( One-Time Passowrd) from SMS sent to the phone number
 * provided in the sign-up form sample.
 * Hence, no user-interaction is required to populate the OTP field.
 */
public class OTPActivity extends AppCompatActivity {


    private EditText etOTP;
    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otpactivity);
        etOTP = findViewById(R.id.otp);

        getOTP();

        Button btnOK = findViewById(R.id.button);
        btnOK.setOnClickListener(new View.OnClickListener() {
            /**
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OTPActivity.this, DisplayActivity.class);
                startActivity(intent);
            }
        });



    }

    public synchronized String generateAppHashKey() {
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(OTPActivity.this);
        return appSignatureHashHelper.getAppSignatures().get(0);
    }

    public void getOTP(){
        try {

            SMSBroadcastReceiver smsBroadcastReceiver = new SMSBroadcastReceiver();

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            OTPActivity.this.registerReceiver(smsBroadcastReceiver, intentFilter);

            // Get an instance of SmsRetrieverClient, used to start listening for a matching
            // SMS message.

            SmsRetrieverClient client = SmsRetriever.getClient(OTPActivity.this);

            // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
            // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
            // action SmsRetriever#SMS_RETRIEVED_ACTION.

            Task<Void> task = client.startSmsRetriever();

            // Listen for success/failure of the start Task

            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Successfully started retriever, expect the broadcast intent
                    /**
                     * Ideally,the phone number is sent to verification server,
                     * while this client side application listens for an SMS message containing a one-time code for the app.
                     * After the SMS is received, the one-time code is sent back to the server to complete the verification process.
                     * However, for demo purpose, the SMS is being sent from this app itself and verification is not implemented.
                     */

                    if (getIntent().getAction()=="SEND_SMS") // to verify the intent action which invoked the OTPActivity from MainActivity
                    {
                        sendSMS();
                    }
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                /**
                 * @param exception
                 */
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Failed to start retriever, Logging the Exception for more details
                    Log.e("Failure_Listener:","Failed to start retriever, inspect Exception for more details\n"+exception.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("Failure_exception:",e.getMessage());

            e.printStackTrace();
        }

    }

    /**
     * @param otp
     */
    public void onOTPReceived(String otp) {
        etOTP.setText(otp);
    }

    public void onOTPTimeOut() {
        Button btnRetry = findViewById(R.id.button);
        btnRetry.setText("RESEND OTP");
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });
    }

    /**
     * @param error
     */
    public void onOTPReceivedError(String error) {
        Toast.makeText(this, error+"", Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(){
        final String phoneNo = getIntent().getStringExtra("phoneNo");
        final String appHashKey = generateAppHashKey();
        //generate a random number for One-Time Password
        final int OTP = new Random().nextInt(10000);

        // a potentially time consuming task

        new Thread(new Runnable() {
            public void run() {
                String msgOTP = "Your OTP is: " +OTP+"\n" +
                        "\n" +
                        "\n" +
                        appHashKey +
                        "\n";

                /*
                    <#> Your ExampleApp code is: 1238
                    FA+9qCX9VSu
                */
                Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                //Get the SmsManager instance and call the sendTextMessage method to send message

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNo, null, msgOTP, pi, null);
            }
        }).start();
    }

    /**
     * BroadcastReceiver to wait for SMS messages.
     * It filters Intents on SmsRetriever.SMS_RETRIEVED_ACTION.
     */

    public class SMSBroadcastReceiver extends BroadcastReceiver {

        public Pattern p = Pattern.compile("[0-9]{4,7}"); // expected pattern of OTP
        String otp;

        /**
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status = (Status) (extras != null ? extras.get(SmsRetriever.EXTRA_STATUS) : null);
                assert status != null;
                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:

                        //This is the full message
                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);


                        //Extract the OTP code

                        assert message != null;
                        Matcher m = p.matcher(message); // checking if retrieved OTP matches with the expected pattern
                        if(m.find()) {
                            otp = m.group(0);

                            Log.d("OTP Received: " , otp +" ");

                            onOTPReceived(otp);
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Waiting for SMS timed out (5 minutes)
                        onOTPTimeOut();

                        Log.d("ERROR_CODE",""+CommonStatusCodes.TIMEOUT+"::"+CommonStatusCodes.getStatusCodeString(status.getStatusCode()));
                        break;

                    case CommonStatusCodes.API_NOT_CONNECTED:

                        onOTPReceivedError("API NOT CONNECTED");

                        Log.d("ERROR_CODE",""+CommonStatusCodes.API_NOT_CONNECTED+"::"+CommonStatusCodes.getStatusCodeString(status.getStatusCode()));

                        break;

                    case CommonStatusCodes.NETWORK_ERROR:

                        onOTPReceivedError("NETWORK ERROR");

                        Log.d("ERROR_CODE",""+CommonStatusCodes.NETWORK_ERROR+"::"+CommonStatusCodes.getStatusCodeString(status.getStatusCode()));

                        break;

                    case CommonStatusCodes.ERROR:

                        onOTPReceivedError("SOME THING WENT WRONG");

                        Log.d("ERROR_CODE",""+CommonStatusCodes.ERROR+"::"+CommonStatusCodes.getStatusCodeString(status.getStatusCode()));

                        break;

                }
            }


        }

    }
}