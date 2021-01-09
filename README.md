# UserInfo App

UserInfo App is an Android Project demonstrating two key features:

1.Prompts the user to choose from the phone numbers/emails stored on the device and thereby avoid having to manually type a phone number.

2.Retrieve One-Time Password (OTP) automatically from SMS which might be used for phone-number verification

# Workflow
Touch on the field "E-Mail ID" to pick an E-mail ID from the populated list of e-mails stored in the device.

Other fields like first name and last name will be populated automatically based on the values linked with the selected email id respectively.

Touch on the field "Phone No." to pick a mobile number from the populated list of phone numbers stored in the device.

After the phone number is populated in the field, SMS Retriever API is initiated at the same time and listens for an SMS on successful initiation.

On receiving an SMS message that includes a verification code and a hash to identify the app, SMS Retriever API reads the SMS.

The app extracts the verification code ( OTP) from the SMS and fills the OTP field automatically.

# Prerequisites
The SMS Retriever API is available only on Android devices with Play services version 10.2 and newer.

# Permissions
For demo purpose, the SMS is being sent from this app itself and verification is not implemented.

So SMS permission have to be enabled before app launch from Settings.

# Important

The standard SMS format is given below.

<#> Your ExampleApp code is: 123ABC78 FA+9qCX9VSu 

SMS always starts with <#> sign and has a hash key FA+9qCX9VSu to identify the app.
It is generated with your app's package id. Ideally, this hash key needs to be shared with the verification server.
The server then sends an SMS message that includes a verification code and the hash to identify the app.
After OTP retrieval, the code should be sent back to the server and validates the phone number.

# How to run a sample
Clone or download the project open it with Android Studio compile and run it will work.

# Credits
https://developers.google.com/identity/sms-retriever/overview 

https://developers.google.com/identity/smartlock-passwords/android/retrieve-hints

