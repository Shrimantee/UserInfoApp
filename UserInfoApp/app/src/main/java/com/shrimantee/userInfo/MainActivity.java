package com.shrimantee.userInfo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;

import androidx.appcompat.app.AppCompatActivity;

/**
 Activity displayed after {@link SplashActivity}

 Here's a demonstration to prompt the user to choose from the phone numbers/emails stored on the device and
 thereby avoid having to manually type a phone number

 This is a sample sign up form where hints will be provided for email and phone number fields.
 On retrieving the hint, associated fields with it like first name, last name, etc can be used to fill up the form

 Here the first name and last name is automatically populated based on the respective values
 linked with the retrieved email hint.
 The phone number retrieved from hint is used to receive an OTP (One-Time Password) on that number.
 */

public class MainActivity extends AppCompatActivity  {


    // Request codes to identify the purpose of API call
    private static final int EMAIL_PICKER_REQUEST = 9909;
    private static final int PHONE_NUMBER_PICKER_REQUEST = 9908;
    public EditText etEmail, etFirstName, etLastName, etPhone;
    Button btnRetrieveOTP;
    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etEmail = findViewById(R.id.email);
        etFirstName = findViewById(R.id.firstName);
        etLastName = findViewById(R.id.lastName);
        etPhone =  findViewById(R.id.phone);
        btnRetrieveOTP = findViewById(R.id.button);
        btnRetrieveOTP.setEnabled(false);
    }
    /**
     * @param view
     */
    // Construct a request for emails and show the picker
    public void requestEmailHints(View view) throws IntentSender.SendIntentException {
        CredentialPickerConfig credentialPickerConfig = new CredentialPickerConfig.Builder()
                .setShowCancelButton(true)
                .build();
        HintRequest hintRequest = new HintRequest.Builder()
                .setIdTokenRequested(true)
                .setHintPickerConfig(credentialPickerConfig)
                .setEmailAddressIdentifierSupported(true) // configuration to display only email addresses associated with the device
                .build();
        PendingIntent intent = Credentials.getClient(MainActivity.this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                EMAIL_PICKER_REQUEST, null, 0, 0, 0);
    }
    /**
     * @param view
     */
    public void requestPhoneNoHints(View view) throws IntentSender.SendIntentException{

        CredentialPickerConfig credentialPickerConfig = new CredentialPickerConfig.Builder()
                .setShowCancelButton(true)
                .build();
        HintRequest hintRequest = new HintRequest.Builder()
                .setIdTokenRequested(true)
                .setHintPickerConfig(credentialPickerConfig)
                .setPhoneNumberIdentifierSupported(true) // configuration to display only email addresses associated with the device
                .build();
        PendingIntent intent = Credentials.getClient(MainActivity.this).getHintPickerIntent(hintRequest);
        startIntentSenderForResult(intent.getIntentSender(),
                PHONE_NUMBER_PICKER_REQUEST, null, 0, 0, 0);

    }
    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EMAIL_PICKER_REQUEST:
                // Obtain the email from the result
                if (resultCode == RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    assert credential != null;
                    etEmail.setText(credential.getId());
                    etFirstName.setText(credential.getName()); // the first name associated with the retrieved hint
                    etLastName.setText(credential.getFamilyName()); // the last name associated with the retrieved hint
                }
                break;
            case PHONE_NUMBER_PICKER_REQUEST:
                // Obtain the phone no. from the result
                if(resultCode == RESULT_OK){
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    assert credential != null;
                    etPhone.setText(credential.getId());
                    btnRetrieveOTP.setEnabled(true);
                }
                break;
        }
    }
    /**
     * @param view
     */
    public void getOTP(View view){
        // To retrive an OTP automatically, the control is passed to another activity with the retrieved phone number as data
        Intent intent = new Intent(MainActivity.this,OTPActivity.class);
        intent.putExtra("phoneNo",etPhone.getText().toString());
        intent.setAction("SEND_SMS");
        startActivity(intent);
    }



}