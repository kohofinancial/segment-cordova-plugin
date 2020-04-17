# segment-cordova-plugin
> Cordova plugin for [Segment mobile SDK](https://segment.com/docs/sources/#mobile)

This version of the plugin uses versions `3.7.1` (iOS) and `4.3.1` (Android) of the Segment mobile SDK.
You can view Android and iOS SDK sources on Github.

* https://github.com/segmentio/analytics-android
* https://github.com/segmentio/analytics-ios

Prerequisites:
* Segment API keys 

## Installing

You can install the latest version of the plugin directly from git through the Cordova CLI:
```bash
cordova plugin add https://github.com/kohofinancial/segment-cordova-plugin.git
```

## Usage
In your 'deviceready' handler, start Segment Analytics :
```javascript
window.Segment.startWithConfiguration(IOS_OR_ANDROID_KEY);
```                

To track a Screen :
```javascript
window.Segment.screen({
    name: 'Home',
    properties: {
        'path': '/home'
    }
});
``` 
To track an Event:
```javascript
window.Segment.track({
    event: 'Order Completed',
    properties: {
        'revenue': 10
    }
});
```
To track an Identity:
```javascript
window.Segment.identity({
    userId: 'segment_sdk_user',
    traits: {
        birthday: '2000-01-01'
    }
});
```

## Configuration options for `.startWithConfiguration()` 
You can configure number of options to setup SDK.

#### trackApplicationLifecycleEvents (Android and iOS)
Record certain application lifecycle events like `Application Opened`, `Application Installed`, `Application Updated`. (Default: false)

#### recordScreenViews (Android and iOS)
Record screen views automatically. It's not useful for the Cordova PhoneGap app. (Default: false)

#### defaultOptions (Android and iOS)
Specify which integrations should be enabled or not for all calls. (Default: All)

#### trackAttributionInformation (Android and iOS)
Record attribution data from enabled providers using the mobile service. (Default: false)

#### flushQueueSize (Android and iOS)
The queue size at which to flush events. (Default: 20, Max: 250 for Android and 100 for iOS) 

#### enableBrazeIntegration (Android and iOS)
Enable device-mode connection to the Braze destination 

#### collectDeviceId (Android)
Record the device id. (Default: true)

#### flushInterval (Android)
The interval at which the client should flush events (Default: 30 seconds)

#### tag (Android)
Key for caching. It's used to share different caches across the instances (Default: "analytics_write_key")

#### logLevel (Android)
Controls the level of logging (Default: INFO)

#### shouldUseLocationServices (iOS)
Use location services. (Default: false)

#### enableAdvertisingTracking (iOS)
Record advertising info. (Default: true)

#### shouldUseBluetooth (iOS)
Record bluetooth information. (Default: false)

#### trackInAppPurchases (iOS)
Record in-app purchases from the App Store. (Default: false)

#### trackPushNotifications (iOS)
Record push notifications.  (Default: false)

**Example Usage:**
```javascript
window.Segment.startWithConfiguration(IOS_OR_ANDROID_KEY, {
    trackApplicationLifecycleEvents: true,
    trackAttributionInformation: true,
    flushInterval: 60,
    trackInAppPurchases: true,
    enableBrazeIntegration: true
});
```      

