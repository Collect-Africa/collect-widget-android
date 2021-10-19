
## Collect-Africa Android Widget

Collect Android Widget enables you to embed Collect's checkout into your Android Mobile applications, allowing your users to choose their preferred payment method and be redirected to its authorization process without needing to go into Collect's hosted checkout.

## Before getting started


- Retrieve your Client Public API Keys.
- Create a sandbox customer, so you can get connecting immediately.


## Requirements
- Android Studio 3.5.X and above

## Installation

### Android Studio (using Gradle)
Add the following lines to your project level `build.gradle`:
```gradle
allprojects {
  repositories {
   ...
   maven { url 'https://jitpack.io' }
  }
}
```
Add the following lines to your app level `build.gradle`:
```
dependencies {
    implementation 'co.paystack.android:paystack:3.1.3'
}
```

## Usage

### 1.0) Add Required permission

Head over to  your project  AndroidManifest.xml and add the `uses-permission` line below

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 1.1) Initialize SDK

Initialize the SDK:

```java
  new CollectWidget()
    .CollectCheckout(
      "john.doe@examlple.com", //customer email | required
      "John", //customer first name | required
      "Doe", //customer last email | required
      generateRef(), // your payment reference | required
      10000, // Amount in Kobo | required
      "NGN", // Currency 
      "", // itemImage | optional
      Enviroment.SANDBOX, // [ Enviroment.SANDBOX,Enviroment.LIVE ] | required
      "your_collect_africa_public_key" // your collect Africa publickey
    )
    .build(Activity, new OnClose() {
      @Override
      public void OnClose() {
        //Modal Closed
      }
    }, new OnFailed() {
      @Override
      public void OnFailed(String msg) {
        //payment failed
       Toast.makeText(activity, msg, Toast.LENGTH_LONG ).show();

      }
    }, new OnSuccess() {
      @Override
      public void OnSuccess(String reference, int amount) {
      // payment successful!
        Toast.makeText(activity, msg, Toast.LENGTH_LONG ).show();
      }
    });
```
## Other Information
For enquires and questions, contact
[@Astrocodr](https://github.com/alome007/)
