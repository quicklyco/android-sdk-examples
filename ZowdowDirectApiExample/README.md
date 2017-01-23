# Demo-app with direct Zowdow API integration

© 2015-2017 Zowdow, Inc.

This app is intended to represent the opportunities for Android-developers to interact
with Zowdow API directly, without SDK.

## Version

Current version as of January, 23 2017 is 1.0.1.

## Overview

This application demonstrates pretty basic interaction with Zowdow Auto-Suggest API.
There are two Zowdow APIs consumed by this client: Initialization & Unified.
More detailed info about their usage is described below, right after Architecture paragraph.

## Architecture

The architectural pattern of this app is a pretty classical implementation of Model-View-Presenter architecture.
Without Domain/Repository layers for now, as suggestions caching is not implemented yet.

The project consists of 5 key packages on the surface:

*   **injection** consists of Dagger modules and components. For now, there is a single module
called `NetworkModule` and it provides the access to Retrofit-service classes that represent request calls
to Zowdow API. We use Dagger 2 in order to make this project unit-testable in a closest perspective & also to keep
its' architecture clean. `NetworkComponent` is initialized inside `ZowdowDirectApplication` class.

*   **network** consists of Retrofit-service classes, which purpose was described above & also entity-classes, which
mostly represent suggestions, cards & ad listings.

*   **presenters** package consists of presenters that implement business-logic for the activities that need it & networking operations, results of which
may perform UI-changes.

*   **ui** is for Activity classes, adapters, custom views & interfaces with callback-methods. Each of these
 are categorized by sections to which they belong.

*   **utils** contains constants-interfaces, utility-classes for runtime permissions checks, connectivity state observations, requests parameters collection & formatting and other useful stuff.

## Interaction with Initialization API

A simple call to get the app defaults for an app identifier string is enough to start consuming Zowdow API.

**URL Structures**

```
http://i1.quick1y.com/*/init?app_id=com.kika.test
```
Any version will respond (v1, v4, v5 whatever).

**The API Arguments**

app_id string is required.

**Response**

JSON is the response type. It comes with an envelope wrapper, so responses will look like this:

```
curl -XGET 'http://i1.quick1y.com/v1/init?app_id=com.kika.test' | python -m json.tool

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

No many actual errors -- Most everything else returns 200 with an empty set if there is an error.

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

**Consuming Init API in this demo-app**

The request calls to Init API is provided by `Observable<InitResponse> init(@QueryMap Map<String, Object> queryMap)`
method inside `InitApiService` interface. FYI: in the following example app RxJava wrapper is used for
Retrofit-calls.

The basic map of `queryParams` is formed in `RequestUtils` class by `createQueryMap` method.
Basically, this map includes key-value pairs, declared in mentioned utils class, but it may be extended by another ones
for Unified API needs, which you may find in `Map<String, Object> HomeDemoPresenter`'s `getDefaultUnifiedQueryMap` method.

It's quite important to notice that in this app we use hardcoded values for the next keys:

*   **app_id:** we are using the another demo app package name as a value to ensure that the results will be returned to this client in a proper way.
For now it's `com.searchmaster.searchapp`.
*   **app_ver:** we use the another demo app version as a value.
Same for **app_code** parameter value.
*   **app_ver:** we decided to enable AdMarketPlace cards for this app by setting the value of this parameter to 1.

`InitApiService` usage can be reviewed in `HomeDemoPresenter` class. This code snippet demonstrates it clearly:

```
public void initializeZowdowApi() {
    Map<String, Object> initQueryMap = RequestUtils.createQueryMap(context);
    initQueryMap.put(QueryKeys.DEVICE_ID, RequestUtils.getDeviceId(context));
    if (apiInitialized) {
        onApiInitialized();
    } else {
        initApiSubscription = initApiService.init(initQueryMap)
        .subscribeOn(Schedulers.io())
        .map(InitResponse::getRecords)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(records -> {
            Log.d(TAG, "Initialization was performed successfully!");
        }, throwable -> {
            Log.e(TAG, "Something went wrong during initialization: " + throwable.getMessage());
        }, this::onApiInitialized);
    }
}
```

## Interaction with Unified API ##

Unified API is the key Zowdow API to interact with in order to retrieve and process auto-suggest data.
In this app's case, it is about search suggestions retrieval by multiple parameters and keyword, defined by user.

We implemented `UnifiedApiService` which works with Unified API & some tracking events, and
`AdMarketPlaceService` which loads ad listings for special AdMarketPlace cards and performs their parsing from XML format.

**Base URL for this API**

```
https://u1.quick1y.com/v1/
```

By the way, all API endpoints constants are available in `network/ApiBaseUrls` interface.

**Consuming Unified API**

The example of network call to Unified API may be found in `HomeDemoPresenter` class.

This method retrieves suggestions response and converts its' contents into
the list full of cards with the parameters we need to render cards in the suggestions' carousels (lists).
If the server response is successful we are switching to the UI thread and passing retrieved and processed suggestions
into suggestions list view's adapter.

```
private void requestSuggestionsFromServer(Map<String, Object> queryMap) {
        unifiedApiSubscription = unifiedApiService.loadSuggestions(queryMap)
                .subscribeOn(Schedulers.io())
                .cache()
                .subscribe(this::processSuggestionsResponse, throwable -> {
                    view.onSuggestionsLoadingFailed();
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
                .subscribe(suggestions -> {
                    view.onSuggestionsLoaded(suggestions);
                }, throwable -> {
                    Log.e(TAG, "Could not load suggestions: " + throwable.getMessage());
                });
    }
```

**UI-representation of suggestions**

In the scope of this app we consider `stream` as carousel type. It is a simple list of items
 with horizontal scroll. More carousel types like `mid_stream`, `zoom` or `rotary` are available in SDK.

`SuggestionsAdapter` performs the suggestions list rendering. Each suggestion row is bound by
view holder class called `SuggestionViewHolder`, which contains a RecyclerView for cards. `CardsAdapter` instance should be
attached to each of these RecyclerViews in view holder instances.

**Card formats**

You may also dynamically change the cards format by replacing the `card_format` value
for suggestions' retrieval query map.

All card formats are declared in the interface `utils/constants/CardFormats`.

**Tracking**

We use `clickUrl` and `impressionUrl` field values for cards interaction tracking.
The first one for click events, and the another one is for card appearance events.
Impression events are processed directly in `ZowdowImageView` class.

## Contact

For technical support please email support@zowdow.com

## Thank You
