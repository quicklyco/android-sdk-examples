# Zowdow Auto-Suggest SDK for Android

© 2015-2017 Zowdow, Inc.

The Zowdow Auto-Suggest SDK is intended to provide Android application developers with a convenient access to Zowdow services via native APIs.

## Version

Current version as of October 26, 2016 is 2.0

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

    dependencies {
        compile 'co.zowdow:zowdow-sdk:+@aar'
        compile 'com.android.support:appcompat-v7:+'
        compile 'com.android.support:recyclerview-v7:+'
        compile 'com.android.support:cardview-v7:+'

        compile 'com.squareup.okhttp:okhttp:2.3.0'
        compile 'com.squareup.retrofit:retrofit:2.0.0-beta2'
        compile 'com.squareup.retrofit:converter-gson:2.0.0-beta2'
        compile ('com.squareup.retrofit:converter-simplexml:2.0.0-beta2') {
                exclude group: 'xpp3', module: 'xpp3'
                exclude group: 'stax', module: 'stax-api'
                exclude group: 'stax', module: 'stax'
        }

        // or
        compile 'com.squareup.okhttp3:okhttp:3.6.0'
        compile 'com.squareup.retrofit2:retrofit:2.1.0'
        compile 'com.squareup.retrofit2:converter-gson:2.1.0'
        compile ('com.squareup.retrofit2:converter-simplexml:2.1.0') {
                exclude group: 'xpp3', module: 'xpp3'
                exclude group: 'stax', module: 'stax-api'
                exclude group: 'stax', module: 'stax'
        }
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

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String suggestion);
    }

    public interface OnCardClickListener {
        void onCardClick(String suggestion, String cardUrl);
    }
```

Sometimes `cardUrl` may contain a deeplink. Just send Intent with ACTION_VIEW to open it with installed app, that can handle it.

### Configure ZowdowAdapter layout

To change carousel type use
```java
    zowdowAdapter.setCarouselType(int carouselType)
```
where `carouselType` is Zowdow.CAROUSEL_LINEAR_HALF, Zowdow.CAROUSEL_LINEAR_FULL, Zowdow.CAROUSEL_STACK or Zowdow.CAROUSEL_ROTARY

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
    void setMaxCardCount(int maxCardCount)
    void setHighlightUserFragment(boolean highlightUserFragment)
    void setHighlightColor(int color)
    void setNormalColor(int color)
    void setCarouselType(@CarouselType int carouselType) // carouselType can be Zowdow.CAROUSEL_LINEAR_A or Zowdow.CAROUSEL_LINEAR_B
    void setIconId(@DrawableRes int drawableId) // if drawableId is 0 ImageView won't be displayed, it's width will be 0, but left and right margins will have an impact on the view
    void setIconImageViewWidth(int widthInPx) // change width of icon ImageView
    void setMargins(int leftMarginInPx, int rightMarginInPx) // change margins of icon ImageView
    void setSeparatorLineColor(int color) // change color of the bottom separator line
    void setSeparatorLineThicknessPx(int thicknessPx) // change thickness of the bottom separator line

    // We use Mode.MULTIPLY, so if you pass -1 (#FFFFFF) there, image won't change. The same for null
    void setNightModeFilterColor(@ColorInt Integer color)
```

Behaviour of these methods is the same as of the ZowdowAdapter's methods


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
