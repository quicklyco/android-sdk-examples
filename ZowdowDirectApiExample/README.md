# Demo App with direct Zowdow API integration

© 2015-2017 Zowdow, Inc.

This app is intended to represent the opportunities for Android developers to interact
with Zowdow API directly, without SDK.

## Version

Current version as of Feb, 7 2017 is 1.0.1.

## Overview

This application demonstrates basic interaction with Zowdow AutoSuggest API.
There are two Zowdow APIs consumed by this client: **Initialization & Unified**.
More detailed info about their usage is described below, after the Architecture section.

## Architecture

The project consists of 3 key packages:

*   **injection** consists of Dagger modules and components. For now, there is a single module
called `NetworkModule` and it provides the access to Retrofit-service classes that represent request calls
to Zowdow API. We use Dagger 2 in order to make this project unit-testable in a closest perspective & also to keep
its' architecture clean. `NetworkComponent` is initialized inside `ZowdowDirectApplication` class.

*   **network** consists of Retrofit-service classes, which purpose was described above & also entity-classes, which
mostly represent suggestions, cards & ad listings.

*   **ui** is for Activity classes, adapters, custom views & interfaces with callback-methods.
There only activity that plays such an important role in application's workflow is `HomeDemoActivity`.
The key events like Zowdow API initialization & suggestions loading are happening inside this class.
`WebViewActivity` and `VideoActivity` just represent the cards' content in master-detail flow: it may be either web-
and video-content.

*   **utils** contains constants-interfaces, simple utility-classes for geolocation, runtime permissions checks, connectivity state observations, requests parameters collection & formatting
and other useful stuff.

## Interaction with Initialization API

A simple call to get the app defaults for an app identifier string is enough to start consuming Zowdow API.

**URL Structures**

```
http://i1.quick1y.com/*/init?app_id=com.example.test
```
Any version will respond (v1, v4, v5 whatever).

**The API Arguments**

app_id string is required.

**Response**

JSON is the response type. It comes with an envelope wrapper, so responses will look like this:

```
curl -XGET 'http://i1.quick1y.com/v1/init?app_id=com.example.test' | python -m json.tool

{
    "_meta": {
        "count": 3,
        "rid": "c29bbb88-b6ca-4f64-cf1b-cf19dfa31665",
        "status": "SUCCESS",
        "ttl": 3600
    },
    "records": {
        "app_id": "11",
        "default_card_format": "inline",
        "use_cache": true
    }
}

```
**Errors**

Not many actual errors -- Almost every request else returns 200 with an empty set if there is an error.

JSON format like:
```
{
    "_meta": {
        "count": 0,
        "rid": "d7e53c5b-ab21-4f78-cf87-07a4e1a06f1b",
        "status": "SUCCESS",
        "ttl": 3600
    },
    "records": []
}
```

**Consuming Init API in this demo app**

The request calls to Init API is provided by `Observable<InitResponse> init(@QueryMap Map<String, Object> queryMap)`
method inside `InitApiService` interface. FYI: in the following example app RxJava wrapper is used for
Retrofit-calls.

The basic map of `queryParams` is formed in `QueryUtils` class by `getQueryMapObservable` method.
Basically, the map (which is emitted by `queryMapObservable` returned by the mentioned method) includes key-value pairs, declared in mentioned utils class, but it may be extended by another ones
for Unified API needs, which you may find in `Map<String, Object> QueryUtils`'s `createQueryMapForUnifiedApi(Context context, String searchQuery, String currentCardFormat) ` method.

It's quite important to notice that in this app we use hardcoded values for the next keys:

*   **app_id:** we are using the another demo app package name as a value to ensure that the results will be returned to this client in a proper way.
For now it's `com.zowdow.android.example`.
*   **app_ver:** Demo app version as a value.

`InitApiService` usage can be reviewed in `HomeDemoActivity` class. This code snippet demonstrates it clearly:

```
public void initializeZowdowApi() {
    LocationManager.get().start(this);
            if (apiInitialized) {
                onApiInitialized();
                restoreSuggestions();
            } else {
                initApiSubscription = QueryUtils.getQueryMapObservable(this)
                        .subscribeOn(Schedulers.newThread())
                        .switchMap(new Func1<Map<String, Object>, Observable<InitResponse>>() {
                            @Override
                            public Observable<InitResponse> call(Map<String, Object> queryMap) {
                                return initApiService.init(queryMap);
                            }
                        })
                        .map(InitResponse::getRecords)
                        .cache()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(records -> {
                            Log.d(TAG, "Initialization was performed successfully!");
                            apiInitialized = true;
                        }, throwable -> Log.e(TAG, "Something went wrong during initialization: " + throwable.getMessage()), this::onApiInitialized);
            }
    }
}
```

## Interaction with Unified API ##

Unified API is the key Zowdow API to interact with in order to retrieve and process autosuggest data.
In this app's case, it is about search suggestions retrieval by multiple parameters and keywords, defined by developer.

We implemented `UnifiedApiService` which works with Unified API & some tracking events.

**Base URL for this API**

```
https://u1.quick1y.com/v1/
```

All API endpoints constants are available in `network/ApiBaseUrls` interface.

**Consuming Unified API**

The example of network call to Unified API may be found in `HomeDemoActivity` class.

This method retrieves suggestions response and converts its' contents into
the list full of cards with the parameters we need to render cards in the suggestion carousels (lists).
If the server response is successful we are switching to the UI thread and passing retrieved and processed suggestions
into suggestions list view's adapter.

```
    private void findSuggestions(String searchKeyWord) {
        Map<String, Object> queryMap = QueryUtils.createQueryMapForUnifiedApi(searchKeyWord, currentCardFormat);
        unifiedApiSubscription = unifiedApiService.loadSuggestions(queryMap)
                .subscribeOn(Schedulers.io())
                .cache()
                .subscribe(this::processSuggestionsResponse, throwable -> {
                    Toast.makeText(HomeDemoActivity.this, "Could not load suggestions", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Could not load suggestions: " + throwable.getMessage());
                });
    }

    private void processSuggestionsResponse(BaseResponse<UnifiedDTO> suggestionsResponse) {
        final String rId = suggestionsResponse.getMeta().getRid();
        suggestionsSubscription = Observable.just(suggestionsResponse)
                .subscribeOn(Schedulers.io())
                .flatMapIterable(BaseResponse::getRecords) // converts response wrapper into an iterable list of suggestions
                .map(suggestionItem -> // performs suggestion deserialization
                        suggestionItem
                                .getSuggestion()
                                .toSuggestion(rId, DEFAULT_CAROUSEL_TYPE, currentCardFormat)
                )
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuggestionsLoaded, throwable -> {
                    Log.e(TAG, "Could not load suggestions: " + throwable.getMessage());
                });
    }
```

**UI-representation of suggestions**

In the scope of this app we consider `stream` as carousel type. It is a simple list of items
 with horizontal scroll. More carousel types like `mid_stream`, `stack` or `rotary` are available in SDK.

`SuggestionsAdapter` performs the suggestions list rendering. Each suggestion row is bound by
view holder class called `SuggestionViewHolder`, which contains a RecyclerView for cards. `CardsAdapter` instance should be
attached to each of these RecyclerViews in view holder instances.

**Card formats**

You may also dynamically change the cards format by replacing the `card_format` value
for suggestions' retrieval query map.

All card formats are declared in the interface `utils/constants/CardFormats`.

**Tracking**

We use `clickUrl` and `impressionUrl` field values for cards interaction tracking. These fields are returned when you pass `tracking=1` in the query parameter call to the API.

The first one for click events, and the another one is for card appearance events. These URLs must be called during a click or an impression event in order for accurate tracking for monetization.

Impression events are processed directly in `CardImageView` class.

## Contact

For technical support please email support@zowdow.com

## Thank You
