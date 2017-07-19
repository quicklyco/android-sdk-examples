# Custom keyboard with Zowdow SDK integration.

© 2015-2017 Zowdow, Inc.

We provide an ability to integrate Zowdow SDK not only with the apps, but also with
custom Android keyboards.

## Integration steps

### General integration steps

1.  In order to integrate Zowdow SDK to your custom keyboard in a proper way, make sure
    you have included all necessary dependencies in your **module-level build.gradle** file.
    Below is the basic minimum which would be enough for successful integration which includes
    Zowdow SDK dependency itself (please, note that the **repositories** block where maven source is
    defined is essential), Android Support modules (you can also use some older versions of them),
    OkHttp & Retrofit dependencies.

    ```gradle
    repositories {
        maven {
            url "http://34.199.187.39:8081/artifactory/libs-release-local"
        }
    }

    dependencies {
        compile "co.zowdow:zowdow-sdk:2.1.154@aar"

        compile 'com.android.support:appcompat-v7:25.3.0'
        compile 'com.android.support:recyclerview-v7:25.3.0'
        compile 'com.android.support:cardview-v7:25.3.0'

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

2.  Don't forget to add the following permissions to AndroidManifest.xml:

    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ```

    Also, in your input method you need to implement the user experience for asking and granting
    location permissions: ```ACCESS_COARSE_LOCATION``` and ```ACCESS_FINE_LOCATION``` in runtime
    on devices with Android 6 (Marshmallow) and above..

3.  Before using Zowdow instance within the keyboard - initialize Zowdow (`com.zowdow.sdk.android.Zowdow`).

    In order to achieve this, create the class which extends Android *Application* class
    (if you don't already have one) inside your Input method project. Make sure,
    you have declared this class in your Manifest inside the application tag as
    a value for 'name' field:

    ```<application android:name='.KeyboardApp'>```

    Initialize Zowdow in ```onCreate``` method with the line below:

    ```java
    Zowdow.initialize(context);
    ```

### Code integration to customized InputMethodService

1.  Basically, you will start developing your custom input method by
    creating the class which extends ```InputMethodService``` and represents
    the entry point component to it.

    It is important to make your custom InputMethodService implement
    ```ZowdowKeyboardCandidatesView.OnZowdowCandidateClickListener```
    in order to handle clicks on the cards provided by Zowdow SDK which
    would be shown after complete integration of your input type with the SDK.
    You can define click behavior inside this listener's ```onCardClick(String suggestion, String cardWebUrl)```
    method. The example approaches to this implementation are described below.

    As a result your class will be declared in similar to a following way:

    ```java
    public class CustomInputMethodService extends InputMethodService implements KeyboardActionListener,
            ZowdowKeyboardCandidatesView.OnZowdowCandidateClickListener,
            PermissionsManager.PermissionsResultCallback {
            ...
    }
    ```

    It's totally fine if your class implements more non-conflicting with
    the mentioned ones interfaces than this example.

2.  The next step would be the new Zowdow instance initialization. ```onCreate``` method is
    a good place for SDK injection. Make sure, you have declared the references to ```Zowdow```
    and ```ZowdowKeyboardCandidatesView``` as your ```InputMethodService``` members.

    ```java
        private Zowdow mZowdow;
        private ZowdowKeyboardCandidatesView mSuggestionStripView;
    ```

    ```ZowdowKeyboardCandidatesView``` is the UI-component from our SDK which represents
    the strip which will be displayed within your keyboard and contain list view full of cards, which content depends on the query
    fragment / keyword typed by the user).

    You can perform the initialization of Zowdow-related components in a separate method or even inside a separate wrapper-class like ```InputLogic``` if you feel
    more convenient to see interaction with the SDK functionality abstracted.

    For simplicity we will interact with Zowdow SDK inside our InputMethodService class.

    ```java
        public void initZowdow() {
            mZowdow = new Zowdow(mLatinIME, new Zowdow.ZowdowCallback() {
                @Override
                public void onSuggestionsDidLoad(List<Suggestion> list, boolean isFromCache) {
                    if (!list.isEmpty()) {
                        updateSuggestion(list.get(0));
                    } else {
                        updateSuggestion(null);
                    }
                }

                @Override
                public void onSuggestionsDidFailToLoad(Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        }

        public void updateSuggestion(Suggestion suggestion) {
            if (suggestion != null) {
                updateSuggestionsVisibility(true);
                mSuggestionStripView.setSuggestion(suggestion);
            } else {
                updateSuggestionsVisibility(false);
            }
        }

        private void updateSuggestionsVisibility(boolean shouldBeVisible) {
            mSuggestionStripView.setVisibility(shouldBeVisible ? View.VISIBLE : View.GONE);
        }
    ```

    ```onSuggestionsDidLoad``` of ```Zowdow.ZowdowCallback``` gets invoked as soon as the
    cards content for a given keyword is ready to be displayed inside the keyboard.

    As soon as it happens, if the suggestions list from Zowdow SDK is not empty
    you should pass a retrieved suggestion **(only the first one)** to your ```mSuggestionStripView```,
    so the cards could be rendered on a screen.

    As for ```ZowdowKeyboardCandidatesView``` initialization, it should be done in a following way:

    ```java
        @Override
        public void setInputView(final View view) {
            super.setInputView(view);
            if (((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildCount() == 1) {
                mSuggestionStripView = new ZowdowKeyboardCandidatesView(this, this);
                ((ViewGroup) ((ViewGroup) view).getChildAt(0)).addView(mSuggestionStripView, 0);
            } else {
                mSuggestionStripView = (ZowdowKeyboardCandidatesView) ((ViewGroup) ((ViewGroup) view).getChildAt(0)).getChildAt(0);
            }
            ((InputView) mInputView).onZowdowCandidatesInflated();
        }
    ```

    InputView is your custom defined view which would be displayed within your
    keyboard and represent the row of Zowdow cards.

    Here's the example of its implementation:

    ```java
        public final class InputView extends FrameLayout {
            private final Rect mInputViewRect = new Rect();
            private MainKeyboardView            mMainKeyboardView;
            private KeyboardTopPaddingForwarder mKeyboardTopPaddingForwarder;
            private MoreSuggestionsViewCanceler mMoreSuggestionsViewCanceler;
            private MotionEventForwarder<?, ?>  mActiveForwarder;

            public InputView(final Context context, final AttributeSet attrs) {
                super(context, attrs, 0);
            }

            public void onZowdowCandidatesInflated() {
                final ZowdowKeyboardCandidatesView suggestionStripView =
                        (ZowdowKeyboardCandidatesView) ((ViewGroup) getChildAt(0)).getChildAt(0);
                mMainKeyboardView = (MainKeyboardView) findViewById(R.id.keyboard_view);
                mKeyboardTopPaddingForwarder = new KeyboardTopPaddingForwarder(
                        mMainKeyboardView, suggestionStripView);
                mMoreSuggestionsViewCanceler = new MoreSuggestionsViewCanceler(
                        mMainKeyboardView, suggestionStripView);
            }

        /**
         * This class forwards {@link MotionEvent}s happened in the top padding of
         * {@link MainKeyboardView} to {@link SuggestionStripView}.
         */
        private static class KeyboardTopPaddingForwarder
                extends MotionEventForwarder<MainKeyboardView, ZowdowKeyboardCandidatesView> {
            private int mKeyboardTopPadding;

            public KeyboardTopPaddingForwarder(final MainKeyboardView mainKeyboardView,
                                               final ZowdowKeyboardCandidatesView suggestionStripView) {
                super(mainKeyboardView, suggestionStripView);
            }

            ...
        }

        /**
         * This class forwards {@link MotionEvent}s happened in the {@link MainKeyboardView} to
         * {@link SuggestionStripView} when the {@link MoreSuggestionsView} is showing.
         * {@link SuggestionStripView} dismisses {@link MoreSuggestionsView} when it receives any event
         * outside of it.
         */
        private static class MoreSuggestionsViewCanceler
                extends MotionEventForwarder<MainKeyboardView, ZowdowKeyboardCandidatesView> {
            public MoreSuggestionsViewCanceler(final MainKeyboardView mainKeyboardView,
                                               final ZowdowKeyboardCandidatesView suggestionStripView) {
                super(mainKeyboardView, suggestionStripView);
            }

            ...
        }
    ```

3.  As soon as the input logic for input in an editor is initialized and the keyboard is ready to be shown
    and interacted with (basically it is followed by ```onStartInputView``` lifecycle method of the ```InputMethodService```
    invocation, but your choice for this event may vary) you should start the Zowdow
    session by calling ```onStart``` method of your ```Zowdow``` instance.

    You can see the example of this call below:

    ```java
        @Override
        public void onStartInputView(final EditorInfo editorInfo, final boolean restarting) {
            if (mZowdow != null) {
                mZowdow.onStart();
            }
            ...
        }
    ```

4.  Moreover, your Zowdow instance should call ```onStop``` every time the configuration
    (screen orientation) changes or after the input is finished.

    Here's the example of Zowdow SDK and your input method lifecycles synchronization (in a very simple
    customization case):

    ```java
        public void finishInput() {
            if (mZowdow != null) {
                mZowdow.onStop();
            }
            ...
        }

        @Override
        public void onCurrentInputMethodSubtypeChanged(final InputMethodSubtype subtype) {
            finishInput();
            ...
        }

        @Override
        public void onConfigurationChanged(final Configuration conf) {
            SettingsValues settingsValues = mSettings.getCurrent();
            ...
            if (settingsValues.mHasHardwareKeyboard != Settings.readHasHardwareKeyboard(conf)) {
                if (isImeSuppressedByHardwareKeyboard()) {
                    finishInput();
                }
            }
            super.onConfigurationChanged(conf);
        }

        @Override
        public void onFinishInputView(final boolean finishingInput) {
            ...
            finishInput();
        }
    ```

5.  Let's get back to the cards click events handling.
    Here's the example of ```onCardClick(String suggestion, String cardWebUrl)``` implementation:

    ```java
            @Override
            public void onCardClick(String suggestion, String cardWebUrl) {
                onZowdowSuggestionPicked(mSettings.getCurrent(), suggestion + " " + cardWebUrl + " ",
                            mKeyboardSwitcher.getKeyboardShiftMode(),
                            mKeyboardSwitcher.getCurrentKeyboardScriptId(),
                            mHandler );
            }


    ```
    This callback-method provides the suggestion title and the url which you can both
    use in any way you would prefer.

    Our example ```onZowdowSuggestionPicked``` is intended to define the optional (but classical) behavior of the suggestion's
    title to the text field addition:

    ```java
        public void onZowdowSuggestionPicked(final SettingsValues settingsValues,
                                                 final String suggestion, final int keyboardShiftState,
                                                 final int currentKeyboardScriptId, final UIHandler handler) {
            mConnection.beginBatchEdit();
            commitChosenWord(settingsValues, suggestion, LastComposedWord.COMMIT_TYPE_MANUAL_PICK,
                        LastComposedWord.NOT_A_SEPARATOR);
            mConnection.endBatchEdit();
            // Don't allow cancellation of manual pick
            mLastComposedWord.deactivate();
        }
    ```