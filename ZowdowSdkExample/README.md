# Zowdow Auto-Suggest SDK for Android

© 2015-2017 Zowdow, Inc.

The Zowdow Auto-Suggest SDK is intended to provide Android application developers with a convenient access to Zowdow services via native APIs.

## Version

Current version as of May 16, 2017 is 2.1

## Terminology

Zowdow is an autocomplete suggestions service. Therefore we operate with the following concepts:

*   **Fragment** - several characters (usually without whitespace), a phrase entered into a search box
*   **Suggestion** - a phrase returned by the service, which contains or is associated in some way with a given fragment

## Features implemented to date

There are 2 integration scenarios:

*   **Basic** - when we provide the data/adapter used to fill the list of suggestions - and Zowdow suggestions are on top of the list of your own suggestions
*   **Advanced** - when you have your own data/adapter and place Zowdow suggestions at arbitrary positions

Available APIs:

*   `void loadSuggestions(String query, LoaderConfiguration loaderConfig)` - loads a list of suggestions with specified parameters

*   `LoaderConfiguration` - to set parameters for suggestions request

*   `void setCallback(ZowdowCallback cb)` - ZowdowCallback setter

*   `ZowdowAdapter createAdapter(int maxZowdowItemCount, Adapter adapter)` - returns ZowdowAdapter instance, which you can set as an adapter for your ListView. _maxZowdowItemCount_ sets the maximum count of elements which will be displayed in ListView by Zowdow. The rest of elements will be displayed by your _adapter_.

*   `void bindView(int position, View convertView, @NonNull ViewGroup parent, @NonNull Suggestion suggestion, @NonNull Zowdow.Params params)` - create a view to insert Zowdow suggestion in any position within the ListView

*   `tracking APIs` - to be called for the other suggestions you show in the list

# Basic API Integration

The API addresses a use case when you allow a user to input a phrase into a text field, and use that phrase as a fragment to receive autocomplete suggestions from the Zowdow service.

For example: User types in _**st**_ and the SDK sends _**'s'**_ and then _**'st'**_ to the Zowdow Auto-Suggest service and receives back suggestions that are then displayed to the user.

## Integration steps

### General integration steps

1.  Edit build.gradle for the app's **module**, add the following blocks to the root level of the file:

    ```gradle
    repositories {
        maven {
            url "http://34.199.187.39:8081/artifactory/libs-release-local"
        }
    }

    def playServicesModule = "location"

    dependencies {
        compile 'co.zowdow:zowdow-sdk:2.1.16@aar'
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

3.  Before using Zowdow object instance within the application - initialize Zowdow (`com.zowdow.sdk.android.Zowdow`):
    ```java
    Zowdow.initialize(context);
    ```

    Since version 2.0 of the SDK we send the user-agent string as a parameter required by Zowdow services.
    You can either rely on the user-agent retrieval functionality implemented in our SDK which looks like this:

    ```java
    public static String getUserAgent(Context context) {
        if (context != null && (sUserAgent == null || sUserAgent.isEmpty())) {
            sUserAgent = new WebView(context).getSettings().getUserAgentString();
        }
        return sUserAgent;
    }
    ```

    or pass the custom user-agent string previously retrieved by your application, as a second parameter of the overloaded
    version of Zowdow.initialize method:

    ```java
    String userAgent = /* your user agent */;

    Zowdow.initialize(context, userAgent);
    ```

    As during Zowdow initialization within `Zowdow.initialize(Context)` method we are using the
    WebView instance in our SDK in order to retrieve the user agent for tracking and monetization purposes
    it is highly recommended to call the first option of this method (with a context passed only) inside the
    class derived from `Activity` (or `Fragment`), but not the `Application` class.
    
    If initialization of our SDK is either critically essential for you only inside your `Application` class
    or you application is i.e. the browser-app which uses its own `WebView` instance, in order to prevent
    some unexpected issues during the app initialization consider using the second variant of `initialize` method by
    passing your own `userAgent` as a second parameter within the context.

4.  For each Fragment or Activity which uses a Zowdow instance - you MUST call **zowdowInstance.onStart()** and **zowdowInstance.onStop()** within Fragment's or Activity's **onStart()** and **onStop()** methods.

5.  If you use targetSdkVersion=23, you need to ask user for "ACCESS_COARSE_LOCATION" and "ACCESS_FIND_LOCATION" permissions to let ZowdowSDK track current location info.

### Code integration into Activity/Fragment

1.  Necessary imports
    ```java
    import co.zowdow.sdk.android.Suggestion;
    import co.zowdow.sdk.android.Card;
    import co.zowdow.sdk.android.SuggestionsData;
    import co.zowdow.sdk.android.OnSuggestionClickListener;
    import co.zowdow.sdk.android.OnCardClickListener;
    import co.zowdow.sdk.android.OnVideoCardClickListener;
    import co.zowdow.sdk.android.Zowdow;
    import co.zowdow.sdk.android.Zowdow.ZowdowCallback;
    import co.zowdow.sdk.android.Zowdow.Params;
    import co.zowdow.sdk.android.ZowdowAdapter;
    ```

2.  Declare an instance of Zowdow object in your Activity/Fragment.
    ```java
    Zowdow mZowdow;
    ```

3.  In onCreate() method of your Activity/Fragment create an instance of Zowdow class

    ```java
    mZowdow = new Zowdow(this);
    ```

    or

    ```java
    mZowdow = new Zowdow(this, zowdowCallback);
    ```

    `zowdowCallback` can be implemented by your Activity/Fragment or you can create a separate instance to handle the callback:
    ```java
    interface ZowdowCallback {
        void onSuggestionsDidLoad(List<Suggestion> suggestions, boolean isTakenFromCache);
        void onSuggestionsDidFailToLoad(Throwable e);
    }
    ```

    Also, you can set new ZowdowCallback instance for Zowdow at anytime using a setter:
    ```java
    mZowdow.setCallback(zowdowCallback);
    ```

    Notice, that this will reset previous ZowdowCallback instance.

4.  Implement `onStart()` and `onStop()` methods on your Activity/Fragment, and call `mZowdow.onStart()` and `mZowdow.onStop()` correspondingly:
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

## Public APIs

### loadSuggestions
```java
    void loadSuggestions(String query, LoaderConfiguration loaderConfig);
```

Use `LoaderConfiguration` to specify suggestions request parameters. Something like this:

```java
    loaderConfig = new LoaderConfiguration()
                    .cardFormats(Zowdow.CARD_FORMAT_INLINE)
                    .cardLimit(5)
                    .suggestionLimit(3);
```

Or even like this if you intend to deal with multiple card formats:

```java
    loaderConfig = new LoaderConfiguration()
                    .cardFormats(Zowdow.CARD_FORMAT_INLINE, Zowdow.CARD_FORMAT_STAMP)
                    .cardLimit(5)
                    .suggestionLimit(3);
```

`cardFormats` is one of `Zowdow.CARD_FORMAT_INLINE`, `Zowdow.CARD_FORMAT_STAMP`, `Zowdow.CARD_FORMAT_TICKET`, but also may accept multiple card formats
separated with commas (you can pass them as variable arguments).

Don't forget to call `onStart()`, otherwise you will get only cached results. Upon successful completion your listener will be notified by
```java
    void onSuggestionsDidLoad(List<Suggestion> suggestions, boolean isTakenFromCache);
```

of the object that is acting as `ZowdowCallback`. Notice, that `suggestions` list is UnmodifiableList.

In case of error
```java
       void onSuggestionsDidFailToLoad(Throwable e);
```

is called on the same object.

If you want just to track "fragment" event and not to load suggestion, set the last `boolean trackOnly` parameter of `loadSuggestions()` method to `true`

### createAdapter
```java
    ZowdowAdapter createAdapter(Adapter adapter)
```

This method returns ZowdowAdapter, which you can set as an adapter for your ListView. If you want to display your own items after Zowdow's, use `adapter` argument for this. For example:
```java
    ZowdowAdapter adapter = mZowdow.createAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList("Hello", "World")));
    listView.setAdapter(adapter);
```

will show founded Zowdow `Suggestions` with `Cards`, the rest ("Hello", "World") will be displayed by ArrayAdapter.

### Add listeners for Suggestion and Card clicks
```java
    zowdowAdapter.setSuggestionClickListener(OnSuggestionClickListener listener);
    zowdowAdapter.setCardClickListener(OnCardClickListener listener);
    zowdowAdapter.setOnVideoCardClickListener(OnVideoCardClickListener listener);

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    public interface OnCardClickListener {
        void onCardClick(String suggestion, String cardUrl);
    }
    
    public interface OnVideoCardClickListener {
        void onVideoCardClick();
    }
```

Sometimes `cardUrl` may contain a deeplink. Just send Intent with ACTION_VIEW to open it with installed app, that can handle it.

Consider listening to a separate `OnVideoCardClickListener`'s `onVideoCardClick()` event when the
card with Youtube-video content is clicked if you wish to complement the standard, already existing,
video card click behavior implemented on the SDK side.

### Configure ZowdowAdapter layout

To change carousel type use
```java
    zowdowAdapter.setCarouselType(int carouselType)
```
where `carouselType` is Zowdow.CAROUSEL_MID_STREAM, Zowdow.CAROUSEL_STREAM, Zowdow.CAROUSEL_STACK or Zowdow.CAROUSEL_ROTARY

To change icon use
```java
    zowdowAdapter.setIconId(@DrawableRes int drawableId)
```

To change icon ImageView width use
```java
    zowdowAdapter.setIconImageViewWidth(int widthInPx)
```

Don't forget to convert `dp` into `px` if needed.

To apply margins to left and to right of icon ImageView use
```java
    zowdowAdapter.setMargins(int leftMarginInPx, int rightMarginInPx)
```

To change line separator at the bottom of the view use these two methods
```java
    zowdowAdapter.setSeparatorLineColor(int color);
    zowdowAdapter.setSeparatorLineThicknessPx(int thicknessPx);
```

To apply color filter to the cards, use
```java
    // We use Mode.MULTIPLY, so if you pass -1 (#FFFFFF) there, image won't change. The same for null
    zowdowAdapter.setNightModeColorFilter(@ColorInt Integer nightModeColorFilter)
```


# Advanced API integration

If you want to use your own Adapter, but with Zowdow suggestion views at arbitrary positions within the list, just call

```java
    convertView = Zowdow.bindView(position, convertView, parent, Suggestion, Zowdow.Params);
```

and you'll get a view, which is ready to be returned as a result of `getView()` method

Zowdow.Params has the following methods:

```java
    void setOnSuggestionClickListener(OnSuggestionClickListener onSuggestionClickListener)
    void setOnCardClickListener(OnCardClickListener onCardClickListener)
    void setOnVideoCardClickListener(OnVideoCardClickListener onVideoCardClickListener)
    void setMaxCardCount(int maxCardCount)
    void setHighlightUserFragment(boolean highlightUserFragment)
    void setHighlightColor(int color)
    void setNormalColor(int color)
    void setCarouselType(@CarouselType int carouselType) // carouselType can be, Zowdow.CAROUSEL_STACK, Zowdow.CAROUSEL_ROTARY, Zowdow.CAROUSEL_MID_STREAM or Zowdow.CAROUSEL_STREAM
    void setIconId(@DrawableRes int drawableId) // if drawableId is 0 ImageView won't be displayed, it's width will be 0, but left and right margins will have an impact on the view
    void setIconImageViewWidth(int widthInPx) // change width of icon ImageView
    void setMargins(int leftMarginInPx, int rightMarginInPx) // change margins of icon ImageView
    void setSeparatorLineColor(int color) // change color of the bottom separator line
    void setSeparatorLineThicknessPx(int thicknessPx) // change thickness of the bottom separator line

    // We use Mode.MULTIPLY, so if you pass -1 (#FFFFFF) there, image won't change. The same for null
    void setNightModeFilterColor(@ColorInt Integer color)
```

Behaviour of these methods is the same as of the ZowdowAdapter's methods

# Discovery Widget

We also provide a Discovery Widget which comes as a standalone configurable UI component (Fragment) 
and represents the suggestion row (or switchable rows) with the cards related to one of several trending
topics like: Food Nearby, Top Sites, Trending Android-Apps, Music, Videos & Products.

`ZowdowDiscoveryFragment` comes out-of-the-box, so you have no need to perform extra steps e.g. to
retrieve data from Zowdow or provide additional business-logic for your Discovery Widget as it is
already completely ready for the instantiate.

We highly recommend to use one of two ways to instantiate our Discovery Widget.

The first one is to use `ZowdowDiscoveryFragment`'s static `newInstance` method and
pass `DiscoveryWidgetConfiguration` instance (which may be complemented with extra properties described 
below) as a parameter:

```java
    ZowdowDiscoveryFragment.newInstance(new DiscoveryWidgetConfiguration());
```

Basically the line above is equivalent to the second way of instantiation which looks like this:

```java
    ZowdowDiscoveryFragment.newInstance();
```

or like this:

```java
    new ZowdowDiscoveryFragment();
```

Each of these ways represent passing the default configuration parameters for this widget.
Consider using the instantiation without extra arguments if you **don't** wish to customize
your widget with a specific list of categories (instead of the predefined one by our SDK) 
or cards amount inside the each row.

In default configuration presented above this widget will look like a simple list of several 
suggestion rows, just like standard Zowdow suggestions for a given keyword. Each of them contain at 
most 10 cards of the most trending common topic for the current time of a day, the title of which 
is displayed above the cards carousel.

You can attach this fragment to the desired context and handle its state as you usually perform 
this with your own fragments:

```java
    getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ZowdowDiscoveryFragment.newInstance(), "example_tag")
                    .commit();
```

or the another way you like.

Before attaching ZowdowDiscoveryFragment it is essential to initialize Zowdow if it hasn't been
performed in the current context as early as possible:

```java
    Zowdow.initialize(context);
```

Also, don't forget to make your activity or fragment which contains the Widget implement 
`OnCardClickListener` interface in order to handle the widget's cards click events!

### ZowdowDiscoveryWidget customization

As it was already mentioned, you have an ability to initialize `DiscoveryWidgetConfiguration` 
object with additional properties and pass it as a parameter to `ZowdowDiscoveryFragment.newInstance()`
method, e.g. like this:

```java
    DiscoveryWidgetConfiguration configuration = new DiscoveryWidgetConfiguration()
                    .cardLimit(8);
    ZowdowDiscoveryFragment fragment = ZowdowDiscoveryFragment.newInstance(configuration);
```

or like this by additionally using `categories` method which accepts variable count of available 
Discovery categories (see `DiscoveryNavigationType` enum) if you also prefer to customize the 
categories displayed inside the Widget:

```java
    DiscoveryWidgetConfiguration configuration = new DiscoveryWidgetConfiguration()
                    .cardLimit(10)
                    .categories(DiscoveryCategory.APPS, DiscoveryCategory.MUSIC, DiscoveryCategory.NEWS, DiscoveryCategory.PLACES, DiscoveryCategory.FOOD);
    ZowdowDiscoveryFragment fragment = ZowdowDiscoveryFragment.newInstance(configuration);
```

By not specifying desired categories, the most trending ones for current time of a day will 
be displayed inside the widget by default.

You cannot define the priority order of the categories, but you can be sure that each of the ones
you requested will be shown in your Discovery Widget if the data for them is available.

### Multiple Discovery Widgets inside ViewPager

If you would like to have multiple Discovery Widgets on a single screen, consider using the native
ViewPager tied with the adapter-class which extends `FragmentStatePagerAdapter` of the Android SDK, 
initialize `ZowdowDiscoveryFragment` instances inside your adapter and make sure you pass the 
same `DiscoveryWidgetConfiguration` instances even after device rotation (in order to keep you 
Discovery Widget's state and data).

`DiscoveryWidgetConfiguration` is `Parcelable`, so you can easily handle its persistence in any way
of implementation you find convenient.

Here's the example of correct multiple Discovery Widgets setup:

```java
    public class DiscoveryPagerAdapter extends FragmentStatePagerAdapter {
        private static final int SIMPLE_WIDGET = 0;
        private static final int CUSTOMIZED_WIDGET = 1;
        private static final int WIDGETS_COUNT = 2;
    
        public DiscoveryPagerAdapter(FragmentManager fm) {
            super(fm);
        }
    
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SIMPLE_WIDGET:
                    return ZowdowDiscoveryFragment.newInstance();
                case CUSTOMIZED_WIDGET:
                    return ZowdowDiscoveryFragment
                            .newInstance(new DiscoveryWidgetConfiguration()
                                    .cardLimit(8)
                                    .categories(DiscoveryCategory.MUSIC, DiscoveryCategory.APPS, 
                                                DiscoveryCategory.FOOD, DiscoveryCategory.VIDEOS)
                            );
            }
            return new Fragment();
        }
    
        @Override
        public int getCount() {
            return WIDGETS_COUNT;
        }
    }
```

Inside the context (activity / fragment) where this ViewPager with Widgets is used:

```java
    private void attachDiscoveryWidget() {
        DiscoveryPagerAdapter pagerAdapter = new DiscoveryPagerAdapter(getSupportFragmentManager());
        mDiscoveryPager.setOffscreenPageLimit(2);
        mDiscoveryPager.setAdapter(mPagerAdapter);
    }
```

It is not necessary but recommended that you have the value of offscreen page limit equal
to ZowdowDiscoveryFragments you would like to have displayed on a screen.

### Tracking APIs

The following APIs should be used to track events that happen outside of the ZowdowSDK:

```java
    // Call when a user hits "Search" button on the keyboard,
    // initiating a search, pass the search phrase in the search parameter
    // and search engine (google|bing|other) as origin
    Zowdow.trackSearch(Context context, String origin, String query);

    // Call whenever you receive and ready to render suggestions other than Zowdow's,
    // passing an arbitrary origin param. f.e. if you use Bing autosuggestion service - pass "bing".
    // fragment stands for query string
    Zowdow.trackSuggestionsReceived(Context context, String origin, String fragment, List<? SuggestionData> suggestions);

    // Call when a user chose some non-Zowdow suggestion from the list provided by different sources,
    // pass the suggestion origin as origin param (f.e. "bing") and actual suggestion text as suggestion param
    Zowdow.trackDidChooseSuggestion(Context context, String origin, String suggestion);
```

`SuggestionsData` is just a:

```java
    public interface SuggestionData {
        String getSuggestion();
    }
```

## Sample project

For an example of integration - please see a Sample project contained as part of this SDK package.

## Deduplication logic example

One of the common tasks when integrating Zowdow with an existing auto-suggest service is to dedupe the results returned by Zowdow and that other service.  It is preferred that Zowdow's suggestions are higher priority to be displayed, but this strategy is completely arbitrary and is up to the application developer.  The Sample app mentioned above contains a simple deduplication example code, which you can find in the `AdvancedIntegrationDemoActivity.java` in method:

```java
    private void merge() {
        for (int i = 0; i < mZowdowResponse.size(); i++) {
            Iterator<? extends SuggestionData> it = mBingResponse.iterator();
            while (it.hasNext()) {
                SuggestionData suggestion = it.next();
                if (mZowdowResponse.get(i).getSuggestion().toLowerCase()
                        .equals(suggestion.getSuggestion().toLowerCase())) {
                    it.remove();
                    break;
                }
            }
        }
        List<SuggestionData> data = new ArrayList<>();
        data.addAll(mZowdowResponse);
        data.addAll(mBingResponse);
        Collections.sort(data, new Comparator<SuggestionData>() {
            @Override
            public int compare(SuggestionData lhs, SuggestionData rhs) {
                return lhs.getSuggestion().toLowerCase().compareTo(rhs.getSuggestion().toLowerCase());
            }
        });
        mAdapter.setItems(data);
    }
```
We use a simple construction (not necessarily most efficient, but sufficient for a small number of suggestions we deal with) - so there are 2 loops - outer and inner. We walk Zowdow suggestions in the outer loop and compare the current Zowdow suggestion against all of the Bing's suggestions (Bing is used as an example of other service) - if suggestions match - we simply remove Bing's duplicate.  After that we sort and display suggestions list.

## Contact

For technical support please email support@zowdow.com

## Thank You
