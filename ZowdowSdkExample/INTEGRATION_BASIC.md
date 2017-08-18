# Zowdow Basic integration

## Integration steps

1.  Edit build.gradle for the app's **module**, add the following blocks to the root level of the file:

    ```gradle
    repositories {
        maven {
            url "http://34.199.187.39:8081/artifactory/libs-release-local"
        }
    }

    def playServicesModule = "location"

    dependencies {
        compile 'co.zowdow:zowdow-sdk:2.1.154@aar'
        compile 'com.android.support:appcompat-v7:+'
        compile 'com.android.support:recyclerview-v7:+'
        compile 'com.android.support:cardview-v7:+'

        /* If you already use at least one of Google Play Services modules, it would be enough to include any
         different from 'base' module for correct Zowdow SDK integration.
         We need Google Play Services in order to retrieve an appropriate Android ID of the device
         on which application with Zowdow SDK integrated is installed.
         */

        compile 'com.google.android.gms:play-services-base:10.0.+'

        compile 'com.squareup.retrofit:retrofit:2.0.0-beta2'
        compile 'com.squareup.retrofit:converter-gson:2.0.0-beta2'
        compile 'com.squareup.okhttp:okhttp:2.3.0'
        // or
        compile 'com.squareup.okhttp3:okhttp:3.6.0'
        compile 'com.squareup.retrofit2:retrofit:2.1.0'
        compile 'com.squareup.retrofit2:converter-gson:2.1.0'
    }
    ```

    We support both retrofit2-beta and retrofit2-stable. Add the following packaging options (notice that `packagingOptions` block is nested within `android` block)

    ```gradle
    android {
        packagingOptions {
            exclude 'META-INF/LICENSE.txt'
            exclude 'META-INF/NOTICE.txt'
        }
    }
    ```


2.  Add the following permissions to AndroidManifest.xml:

    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ```

    If you use targetSdkVersion=23 or above, you need to ask user for `ACCESS_COARSE_LOCATION` and `ACCESS_FIND_LOCATION` permissions 
    in runtime to let ZowdowSDK track current location info.

3.  Before using Zowdow object instance within the application - initialize Zowdow (`com.zowdow.sdk.android.Zowdow`):
    ```java
    Zowdow.initialize(context);
    ```

    or pass the custom user-agent string previously retrieved by your application, as a second parameter of the overloaded
    version of Zowdow.initialize method:

    ```java
    String userAgent = /* your user agent */;

    Zowdow.initialize(context, userAgent);
    ```

    API reference guide will help you to choose the most suitable version of this method.

4.  In onCreate() method of your Activity/Fragment create an instance of Zowdow class

    Declare an instance of Zowdow object in your Activity/Fragment.
    ```java
    Zowdow mZowdow;
    ```

    and then initialize it this way:
    ```java
    mZowdow = new Zowdow(this);
    ```
    
    or additionally pass the second `ZowdowCallback` parameter in order to process received 
    suggestions and handle error cases. You can find more on this in [Advanced Integration Guide (Suggestions loading events & ZowdowCallback)](INTEGRATION_ADVANCED.md).
    
5.  For each Fragment or Activity which uses a Zowdow instance - you MUST call **zowdowInstance.onStart()** and **zowdowInstance.onStop()** within Fragment's or Activity's **onStart()** and **onStop()** methods:

    ```java
        @Override
        protected void onStart() {
            super.onStart();
            mZowdow.onStart();
        }

        @Override
        protected void onStop() {
            mZowdow.onStop();
            super.onStop();
        }
    ```
    
    It's very important not to forget to call both. E.g. without `onStart()` method call you will get only cached results. 
    
6.  Consider using
    ```java
        void loadSuggestions(String query, LoaderConfiguration loaderConfig);
    ```
    in order to retrieve suggestions.

    Use `LoaderConfiguration` to specify suggestions request parameters. Something like this:
    
    ```java
        loaderConfig = new LoaderConfiguration()
                        .cardFormats(Zowdow.CARD_FORMAT_INLINE)
                        .cardLimit(5)
                        .suggestionLimit(3);
    ```
    
    `cardFormats`  is one of `Zowdow.CARD_FORMAT_INLINE`, `Zowdow.CARD_FORMAT_STAMP`, `Zowdow.CARD_FORMAT_TICKET`, but also may accept multiple card formats
    separated with commas (you can pass them as variable arguments).

7.  Initialize Suggestions list adapter.

    You should perform it with the following method:
    ```java
        ZowdowAdapter createAdapter(Adapter adapter)
    ```
    
    This method returns ZowdowAdapter, which you can set as an adapter for your ListView. 

    You can call it and initialize `ZowdowAdapter` the following way (for basic implementation, if only Zowdow suggestions are required):

   ```java
    ZowdowAdapter adapter = mZowdow.createAdapter(null);
    listView.setAdapter(adapter);
    ```
    
    If you want to display your own items after Zowdow's, use `adapter` argument for this. For example:
    ```java
        ZowdowAdapter adapter = mZowdow.createAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList("Hello", "World")));
        listView.setAdapter(adapter);
    ```
    
    will show Zowdow `Suggestions` with `Cards` retrieved, the rest ("Hello", "World") will be displayed by ArrayAdapter.

You can find more on ZowdowAdapter customization, cards & suggestions clicks handling and many 
other useful features of Zowdow SDK in [Advanced Integration Guide](INTEGRATION_ADVANCED.md).