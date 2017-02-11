# Zowdow搜索推荐Android SDK
© 2015-2017 Zowdow, Inc.

Zowdow搜索推荐SDK(下文简称ZowdowSDK)提供给Android应用本地化API接口以方便快捷接入Zowdow的搜索推荐服务。

## 版本

截止2016/10/26，SDK版本为2.0。

## 术语
Zowdow是一项搜索推荐服务，因此我们带入以下几个概念：

*   **片段、字、词** - 一个或多个字符。一般为用户在搜索栏输入的字符串。
*   **搜索推荐** - 一组由API返回的文字和卡片。包含或者与搜索的片段、字、词相关联。

## 目前实现的特征

两种集成方式：

*   **基本** - 我们提供原始数据和Adapter。Zowdow的搜索推荐出现在您自己的搜索推荐或其他第三方搜索推荐上方。

*   **高级** - 我们提供原始数据，但是您用自定义的Adapter。这样您可以把Zowdow的搜索推荐放到任意位置。

可调用的API:

*   `void loadSuggestions(String query, LoaderConfiguration loaderConfig)` - 用指定的参数加载一组搜索推荐

*   `LoaderConfiguration` - 设置请求加载搜索推荐的参数

*   `void setCallback(ZowdowCallback cb)` - ZowdowCallback的setter

*   `ZowdowAdapter createAdapter(int maxZowdowItemCount, Adapter adapter)` - 返回ZowdowAdapter实例。您可以直接赋值为ListView的Adapter。_maxZowdowItemCount_设置了Zowdow搜索推荐显示在您的ListView上的最大数量。除Zowdow以外的第三方搜索推荐会显示在您自己的_adapter_上。

*   `void bindView(int position, View convertView, @NonNull ViewGroup parent, @NonNull Suggestion suggestion, @NonNull Zowdow.Params params)` - 创建一个View以便在您的ListView上的任意位置植入Zowdow搜索推荐。

*   `tracking APIs` - 当您的ListView显示除Zowdow外的第三方搜索推荐时调用。

# 基本API集成指南

API的用例：当用户在文字框输入时，用之当做片段、字、词来接收Zowdow的搜索推荐。

示例: 用户输入_**st**_，SDK会分别发送_**'s'**_和_**'st'**_给Zowdow的搜索推荐服务并收到返回的搜索推荐，然后展示给用户。 

## 集成步骤

### SDK集成

1.  编辑您的应用的**module**路径下的build.gradle文件，将以下代码添加到文件的根级别：

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

        compile 'com.squareup.retrofit:retrofit:2.0.0-beta2'
        compile 'com.squareup.retrofit:converter-gson:2.0.0-beta2'
        compile 'com.squareup.okhttp:okhttp:2.3.0'
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

    我们支持retrofit2-beta和retrofit2-stable。 添加下列打包选项（注意：`packagingOptions`在`android`块下）

    ```gradle
    android {
        packagingOptions {
            exclude 'META-INF/LICENSE.txt'
            exclude 'META-INF/NOTICE.txt'
        }
    }
    ```

2.  在AndroidManifest.xml中添加以下许可：

    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ```

3.  在使用Zowdow实例前，必须首先初始化Zowdow（`com.zowdow.sdk.android.Zowdow`）：
    ```java
    Zowdow.initialize(context);
    ```

4.  对每个使用Zowdow实例的Fragment或Activity，您必须在Fragment或Activity的**onStart()**和**onStop()**方法内调用**zowdowInstance.onStart()**和**zowdowInstance.onStop()**。

5.  如果您使用的版本为targetSdkVersion=23， 您需要申请用户的"ACCESS_COARSE_LOCATION"和"ACCESS_FIND_LOCATION"许可，以便ZowdowSDK跟踪当前的位置信息。

### 代码集成到Activity/Fragment

1.  必要的导入包。
    
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
    
2.  在您的Activity/Fragment中声明一个Zowdow实例。
    
    ```java
    Zowdow mZowdow;
    ```
    
3.  在您的Activity/Fragment中的onCreate()方法里创建一个Zowdow实例。

    ```java
    mZowdow = new Zowdow(this);
    ```

    或
    
    ```java
    mZowdow = new Zowdow(this, zowdowCallback);
    ```

    `zowdowCallback`可以在您的Activity/Fragment中实现，或者您可以创建一个单独的实例来处理这个回调：
    ```java
    interface ZowdowCallback {
        void onSuggestionsDidLoad(List<Suggestion> suggestions, boolean isTakenFromCache);
        void onSuggestionsDidFailToLoad(Throwable e);
    }
    ```

    您也可以随时用setter来为Zowdow设置ZowdowCallback实例：
    ```java
    mZowdow.setCallback(zowdowCallback);
    ```

    注意，这会重置先前的ZowdowCallback实例。

4.  在您的Activity/Fragment中实现`onStart()`和`onStop()`方法，并分别调用`mZowdow.onStart()`和`mZowdow.onStop()`：
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

## 公共API

### loadSuggestions

```java
    void loadSuggestions(String query, LoaderConfiguration loaderConfig);
```

用`LoaderConfiguration`来指定Zowdow搜索推荐请求的参数. 例如：

```java
    loaderConfig = new LoaderConfiguration()
                    .cardFormats(Zowdow.CARD_FORMAT_INLINE)
                    .cardLimit(5)
                    .suggestionLimit(3);
```

或者像以下这样，请求多种卡片格式：

```java
    loaderConfig = new LoaderConfiguration()
                    .cardFormats(Zowdow.CARD_FORMAT_INLINE, Zowdow.CARD_FORMAT_STAMP)
                    .cardLimit(5)
                    .suggestionLimit(3);
```

`cardFormats`为以下三者之一：`Zowdow.CARD_FORMAT_INLINE`, `Zowdow.CARD_FORMAT_STAMP`, `Zowdow.CARD_FORMAT_TICKET`，或者多个组合（用逗号来区分不同格式）。

不要忘记调用`onStart()`，否则您只会得到缓存的结果。一旦请求成功，您的监听器会收到以下通知：
```java
    void onSuggestionsDidLoad(List<Suggestion> suggestions, boolean isTakenFromCache);
```

通过用作`ZowdowCallback`的对象。注意，`suggestions`为只读UnmodifiableList。

如果请求失败，
```java
       void onSuggestionsDidFailToLoad(Throwable e);
```
会被同一个`ZowdowCallback`对象回调。

如果您只是想跟踪"fragment"事件而不是加载搜索推荐，把`loadSuggestions()`方法的最后一个参数`boolean trackOnly`设为`true`。

### createAdapter
```java
    ZowdowAdapter createAdapter(Adapter adapter)
```

返回ZowdowAdapter实例。您可以直接赋值为ListView的Adapter。如果您想显示除Zowdow以外的第三方搜索推荐，使用`adapter`参数。例如：
```java
    ZowdowAdapter adapter = mZowdow.createAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Arrays.asList("Hello", "World")));
    listView.setAdapter(adapter);
```

会显示发现的Zowdow`Suggestions`和相关联的`Cards`,剩下的("Hello", "World")会被ArrayAdapter显示。

### 为Suggestion和Card点击添加监听器
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

有时候`cardUrl`会包含一个deeplink。 将ACTION_VIEW发送给Intent来用已安装的app打开，这些app会处理这个deeplink。

### 配置ZowdowAdapter布局

改变Carousel样式，用：
```java
    zowdowAdapter.setCarouselType(int carouselType)
```
`carouselType`为以下四者之一：Zowdow.CAROUSEL_MID_STREAM, Zowdow.CAROUSEL_STREAM, Zowdow.CAROUSEL_STACK, Zowdow.CAROUSEL_ROTARY

改变搜索图标，用：
```java
    zowdowAdapter.setIconId(@DrawableRes int drawableId)
```

改变搜索图标的ImageView的宽度，用：
```java
    zowdowAdapter.setIconImageViewWidth(int widthInPx)
```

如果需要的话不要忘记转换`dp`为`px`。

要改变搜索图标的ImageView到左右两边的距离，用：
```java
    zowdowAdapter.setMargins(int leftMarginInPx, int rightMarginInPx)
```

改变换行符的样式，用：
```java
    zowdowAdapter.setSeparatorLineColor(int color);
    zowdowAdapter.setSeparatorLineThicknessPx(int thicknessPx);
```

给卡片加滤镜，用：
```java
    // 我们使用Mode.MULTIPLY，所以如果您在这里赋值-1(#FFFFFF)或null，图像不会发生变化。
    zowdowAdapter.setNightModeColorFilter(@ColorInt Integer nightModeColorFilter)
```


# 高级API集成指南

如果您想使用自定义的Adapter，同时在任意位置植入Zowdow搜索推荐，只需要调用：

```java
    convertView = Zowdow.bindView(position, convertView, parent, Suggestion, Zowdow.Params);
```

您会得到一个View，同时可以作为`getView()`方法返回的结果。

Zowdow.Params包含以下方法：

```java
    void setOnSuggestionClickListener(OnSuggestionClickListener onSuggestionClickListener)
    void setOnCardClickListener(OnCardClickListener onCardClickListener)
    void setMaxCardCount(int maxCardCount)
    void setHighlightUserFragment(boolean highlightUserFragment)
    void setHighlightColor(int color)
    void setNormalColor(int color)
    void setCarouselType(@CarouselType int carouselType) // carouselType为以下之一：Zowdow.CAROUSEL_STACK, Zowdow.CAROUSEL_ROTARY, Zowdow.CAROUSEL_MID_STREAM, Zowdow.CAROUSEL_STREAM
    void setIconId(@DrawableRes int drawableId) // 如果drawableId为0，ImageView不会被显示（因为宽度为0），但是到左右两边的距离会存在
    void setIconImageViewWidth(int widthInPx) // 改变搜索图标ImageView的宽度
    void setMargins(int leftMarginInPx, int rightMarginInPx) // 改变搜索图标ImageView到左右两边的距离
    void setSeparatorLineColor(int color) // 改变换行符的颜色
    void setSeparatorLineThicknessPx(int thicknessPx) // 改变换行符的厚度

    // 我们使用Mode.MULTIPLY，所以如果您在这里赋值-1(#FFFFFF)或null，图像不会发生变化。
    void setNightModeFilterColor(@ColorInt Integer color)
```

这些方法的行为和ZowdowAdapter的方法是一样的。


### 跟踪记录API

下面的API应该用来跟踪记录发生在ZowdowSDK外部的事件：

```java
    // 当用户点击键盘上的"搜索"按键时调用，
    // 初始化一个搜索，并传递搜索关键词作为query,
    // 以及搜索引擎（google|bing|other）作为origin
    Zowdow.trackSearch(Context context, String origin, String query);

    // 当收到并准备显示除Zowdow以外的第三方搜索推荐时调用，
    // 传递origin参数，比如您在用Bing的搜索推荐服务，传递"bing"，
    // fragment参数即上面的query
    Zowdow.trackSuggestionsReceived(Context context, String origin, String fragment, List<? SuggestionData> suggestions);
    Zowdow.trackSuggestionsReceived(Context context, String origin, String fragment, List<? SuggestionData> suggestions);

    // 当用户点击非Zowdow搜索推荐时调用，
    // 传递搜索推荐来源作为origin参数，例如"bing"，和实际的搜索推荐作为suggestion参数
    Zowdow.trackDidChooseSuggestion(Context context, String origin, String suggestion);
```

`SuggestionsData`为:

```java
    public interface SuggestionData {
        String getSuggestion();
    }
```

## 示例工程

作为集成参考，请看此SDK包内包含的Sample工程。

## 去重示例

当集成Zowdow到已有的搜索推荐服务上时，一个常见的任务就是去重，即去掉Zowdow和其他搜索推荐重复的内容二者留其一。我们建议保留Zowdow的搜索推荐但是这完全取决于开发者。上面提到的示例工程包含了一段简单的去重代码，您可以在`AdvancedDemoActivity.java`中找到：

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
我们用了一种简单的方法来实现（不一定是最高效的，但是对于处理这种小数量的搜索推荐足够了）。一共两个循环，一内一外。在外循环我们遍历Zowdow搜索推荐然后与所有的Bing搜索推荐比较（以Bing作为第三方搜索服务的例子），如果搜索推荐重复，我们移除Bing的搜索推荐。循环结束后我们排序并显示搜索推荐List。 

## 联系方式

如需技术支持请发送邮件至support@zowdow.com

## 感谢
