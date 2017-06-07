# Zowdow Trending Suggestions

### Overview

Trending category Fragment represents the suggestion rows (or switchable single row) with 
the cards related to one of several trending topics like: Food Nearby, Top Sites,
Trending Android-Apps, Music, Videos & Products.

### Basic Trending implementation

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
