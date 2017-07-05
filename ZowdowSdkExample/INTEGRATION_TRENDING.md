# Zowdow Trending Suggestions

### Overview

Trending category Fragment represents the suggestion rows (or switchable single row) with 
the cards related to one of several trending topics like: Food Nearby, Top Sites,
Trending Android-Apps, Music, Videos & Products.

### Basic Trending implementation

`ZowdowTrendingFragment` comes out-of-the-box, so you have no need to perform extra steps e.g. to
retrieve data from Zowdow or provide additional business-logic for your Trending Widget as it is
already completely ready for the instantiate.

We highly recommend to use one of two ways to instantiate our Trending Widget.

The first one is to use `ZowdowTrendingFragment`'s static `newInstance` method and
pass `TrendingConfiguration` instance (which may be complemented with extra properties described 
below) as a parameter:

```java
    ZowdowTrendingFragment.newInstance(new TrendingWidgetConfiguration());
```

Basically the line above is equivalent to the second way of instantiation which looks like this:

```java
    ZowdowTrendingFragment.newInstance();
```

or like this:

```java
    new ZowdowTrendingFragment();
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
                    .add(R.id.container, ZowdowTrendingFragment.newInstance(), "example_tag")
                    .commit();
```

or the another way you like.

Before attaching ZowdowTrendingFragment it is essential to initialize Zowdow if it hasn't been
performed in the current context as early as possible:

```java
    Zowdow.initialize(context);
```

Also, don't forget to make your activity or fragment which contains the Widget implement 
`OnCardClickListener` interface in order to handle the widget's cards click events!

### ZowdowTrendingFragment customization

As it was already mentioned, you have an ability to initialize `TrendingWidgetConfiguration` 
object with additional properties and pass it as a parameter to `ZowdowTrendingFragment.newInstance()`
method, e.g. like this:

```java
    TrendingWidgetConfiguration configuration = new TrendingWidgetConfiguration()
                    .cardLimit(8);
    ZowdowTrendingFragment fragment = ZowdowTrendingFragment.newInstance(configuration);
```

or like this by additionally using `categories` method which accepts variable count of available 
Trending categories (see `TrendingCategory` enum) if you also prefer to customize the 
categories displayed inside the Widget:

```java
    TrendingWidgetConfiguration configuration = new TrendingWidgetConfiguration()
                    .cardLimit(10)
                    .showCategoryTitle(false)
                    .categories(TrendingCategory.APPS, TrendingCategory.MUSIC, TrendingCategory.NEWS, TrendingCategory.PLACES, TrendingCategory.FOOD);
    ZowdowTrendingFragment fragment = ZowdowTrendingFragment.newInstance(configuration);
```

In order to handle Trending suggestions titles visibility consider using the following method:

```java
    TrendingConfiguration showCategoryTitle(boolean visible)
```

In this release we provide the only one navigation type which is called `CLASSIC` & represents 
multiple suggestion rows for each of the recipes for current conditions.
UI is almost the same as it is for the Autocomplete Suggestions for a given keyword.
Each suggestion contains inline cards.

By not specifying desired categories, the most trending ones for current time of a day will 
be displayed inside the widget by default.

You cannot define the priority order of the categories, but you can be sure that each of the ones
you requested will be shown in your Trending Widget if the data for them is available.

Here are all `TrendingConfiguration` methods which provide Trending widget customization opportunity:

```java
    TrendingConfiguration cardLimit(int cardLimit)
    TrendingConfiguration showCategoryTitle(boolean visible)
    TrendingConfiguration categories(TrendingCategory...categories)
```