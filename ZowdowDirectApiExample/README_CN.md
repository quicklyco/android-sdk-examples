# Zowdow API集成示例

© 2015-2017 Zowdow, Inc.

此示例的目的是向Android开发者展示如何在无需SDK的情况下直接接入Zowdow API接口。

## 版本

截止2017/2/7，示例版本为1.0.1。

## 概括

此示例程序展示了与Zowdow搜索推荐API的基本交互。程序主要使用了**Initialization & Unified**两种API。更多API细节会在下文介绍。

## 架构

工程包含三个关键的包：

*   **injection** 由Dagger模块和组件构成。目前只有一个单一模块`NetworkModule`，它提供了Retrofit-service类（代表Zowdow API请求）的使用。为了方便工程的单元测试以及保持架构的整洁，我们使用了Dagger 2。`NetworkComponent`在`ZowdowDirectApplication`类里初始化。

*   **network** 由Retrofit-service类（如上面介绍）和Entity类（代表搜索推荐，卡片和广告）构成。

*   **ui** 被Activity类, adapters, 自定义视图和回调程序。程序工作流程中重要的活动是`HomeDemoActivity`。很多关键的事件比如Zowdow API的初始化和搜索推荐加载都在这个类里发生。`WebViewActivity`和`VideoActivity`代表卡片的内容信息类型，可以是网页，也可以是视频。

*   **utils** 包含常数，简单的地理位置信息获取，运行时间的权限检查，观察网络连接状态，调用参数集合等有用的信息。

## 与初始化（Initialization）API的交互

作为示例，一个简单的调用来获取默认的app数据就足够开始使用API了。

**URL结构**

```
http://i1.quick1y.com/*/init?app_id=com.example.test
```
所有版本都会响应 (v1, v4, v5)。

**API参数**

app_id是必须的。

**返回数据**

返回数据为JSON类型。例子如下：

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
**错误信息**

如果出错，会返回编码200和一个空集合。

JSON格式为：
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

**使用Init API**

Init API调用是由`InitApiService`内的`Observable<InitResponse> init(@QueryMap Map<String, Object> queryMap)`方法实现。注意：Retrofit-calls使用了RxJava封装。

`queryParams`的map是由`QueryUtils`类的`createQueryMap`方法来生成的。基本上说，这个map包含了由上面提到的utils类定义的键和键值串，但是为了Unified API的需要，它也可以被拓展，由`Map<String, Object> QueryUtils`的`createQueryMapForUnifiedApi(Context context, String searchQuery, String currentCardFormat) `方法实现。

注意：在此示例程序中，下面的参数是预定义好的固定值。

*   **app_id:** 您的应用id，由我们分配。示例使用的app_id为 `com.searchmaster.searchapp`
*   **app_ver:** 示例程序版本号

`InitApiService`的用法可在`HomeDemoActivity`类中查找。如下面的代码片段：

```
public void initializeZowdowApi() {
    LocationManager.get().start(this);
    Map<String, Object> initQueryMap = QueryUtils.createQueryMap(this);
    if (apiInitialized) {
        onApiInitialized();
        restoreSuggestions();
    } else {
        initApiSubscription = initApiService.init(initQueryMap)
            .subscribeOn(Schedulers.io())
            .map(InitResponse::getRecords)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(records -> {
                Log.d(TAG, "Initialization was performed successfully!");
                apiInitialized = true;
            }, throwable -> Log.e(TAG, "Something went wrong during initialization: " + throwable.getMessage()), this::onApiInitialized);
    }
}
```

##与Unified API交互##

Unified API负责获取和处理搜索推荐数据。在示例程序中，数据通过开发者设置多种参数和关键词来获取。

我们实现了`UnifiedApiService`来展示Unified API的用法和一些追踪记录事件。

**API基URL**

```
https://u1.quick1y.com/v1/
```

所有API端点常数都在`network/ApiBaseUrls`中。

**使用Unified API**

调用Unified API的示例可在`HomeDemoActivity`类中找到。

这个方法将API返回的搜索推荐原始数据转化成可供渲染的卡片以及相关数据。如果服务器返回成功，我们就将由UI线程来渲染这些处理好的数据。

```
    private void findSuggestions(String searchKeyWord) {
        Map<String, Object> queryMap = QueryUtils.createQueryMapForUnifiedApi(this, searchKeyWord, currentCardFormat);
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

**搜索推荐的可视化UI**

此示例程序默认使用`stream`作为carousel类型，即一串可以左右滑动的卡片。 更多carousel类型`mid_stream`, `stack`和`rotary`可通过SDK获取。

`SuggestionsAdapter`用来渲染搜索推荐。每一行搜索推荐由`SuggestionViewHolder`类封装，包含了卡片的RecyclerView。这些RecyclerView应该附带一个`CardsAdapter`实例。 

**卡片格式**

您还可以通过替换`card_format`值来动态地改变卡片格式。

所有卡片格式在`utils/constants/CardFormats`中声明。

**追踪记录**

我们用`clickUrl`和`impressionUrl`来进行卡片交互的追踪记录。只有在API调用里设置`tracking=1`才能看到这两项。

`clickUrl`用于卡片点击，`impressionUrl`用于卡片展示。当用户点击卡片/卡片展示给用户时，必须调用这两个URL，否则无法得到准确的变现信息。

卡片展示事件在`CardImageView`类中处理。

## 联系方式

如需技术支持请发送邮件至support@zowdow.com

## 感谢
