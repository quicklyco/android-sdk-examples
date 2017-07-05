# Zowdow Advanced integration

## Advanced API reference

### Zowdow initialization approaches

Before using Zowdow object instance within the application Zowdow (`com.zowdow.sdk.android.Zowdow`) should be performed:
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

### Suggestions loading events & ZowdowCallback

`ZowdowCallback` is a callback-interface which contains 2 methods that are invoked
every time the new suggestions are loaded or the error occurs. It can be implemented by your 
Activity/Fragment or you can create a separate instance to handle the callback:

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

Upon successful suggestions loading completion your listener will be notified by
```java
    void onSuggestionsDidLoad(List<Suggestion> suggestions, boolean isTakenFromCache);
```

of the object that is acting as `ZowdowCallback`. Notice, that `suggestions` list is UnmodifiableList.

In case of error
```java
       void onSuggestionsDidFailToLoad(Throwable e);
```

is called on the same object.

### Suggestion and Card clicks handling

You can process clicks either on cards or suggestions by implementing `OnSuggestionClickListener` and
`OnCardClickListener` and setting them to your `ZowdowAdapter` instance.

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

Sometimes `cardUrl` may contain a deeplink. 
Just send Intent with ACTION_VIEW to open it with installed app, that can handle it like it's performed 
in the code snippet below:

```java
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cardUrl));
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
```

### Configure the layout of suggestions list with ZowdowAdapter

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

### Customize Suggestions and Cards layouts

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
    void setCarouselType(@CarouselType int carouselType) // carouselType can be, Zowdow.CAROUSEL_STACK, Zowdow.CAROUSEL_ROTARY, Zowdow.CAROUSEL_MID_STREAM or Zowdow.CAROUSEL_STREAM
    void setIconId(@DrawableRes int drawableId) // if drawableId is 0 ImageView won't be displayed, it's width will be 0, but left and right margins will have an impact on the view
    void setIconImageViewWidth(int widthInPx) // change width of icon ImageView
    void setMargins(int leftMarginInPx, int rightMarginInPx) // change margins of icon ImageView
    void setSeparatorLineColor(int color) // change color of the bottom separator line
    void setSeparatorLineThicknessPx(int thicknessPx) // change thickness of the bottom separator line

    // We use Mode.MULTIPLY, so if you pass -1 (#FFFFFF) there, image won't change. The same for null
    void setNightModeFilterColor(@ColorInt Integer color)
```

Behaviour of these methods is the same as of the ZowdowAdapter's methods.

### Deduplication logic example

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

`SuggestionsData` is just a:

```java
    public interface SuggestionData {
        String getSuggestion();
    }
```

## Trending Suggestions

We also provide a Discovery Widget which comes as a standalone configurable UI 
component (Fragment) and represents the suggestion row (or switchable rows) with 
the cards related to one of several trending topics like: Food Nearby, Top Sites,
Trending Android-Apps, Music, Videos & Products.

You can find more info about its integration in [Trending Integration Guide](INTEGRATION_TRENDING.md)
