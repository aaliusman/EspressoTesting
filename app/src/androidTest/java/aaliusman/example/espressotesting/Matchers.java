package aaliusman.example.espressotesting;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ScrollToAction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor;
import androidx.test.espresso.remote.annotation.RemoteMsgField;

import junit.framework.AssertionFailedError;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.security.InvalidParameterException;
import java.util.regex.Pattern;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyAbove;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static androidx.test.espresso.assertion.PositionAssertions.isCompletelyRightOf;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.util.Checks.checkNotNull;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.AnyOf.anyOf;

public class Matchers {

    public static final String TEXT_LABEL_PATTERN_TIME = "\\d{1,2}:\\d{2}[ap]";
    public static final String TEXT_LABEL_PATTERN_TIME_WITH_SECONDS = "\\d{1,2}:\\d{2}:\\d{2}[ap]";
    public static final String TEXT_LABEL_PATTERN_AT_TIME = "at " + TEXT_LABEL_PATTERN_TIME;
    public static final String TEXT_LABEL_PATTERN_AT_DAY_DATE = "((Mon)|(Tue)|(Wed)|(Thu)|(Fri)|(Sat)|(Sun)), (1[0-2]|[1-9])/(0[0-9]|1[0-9]|2[0-9]|3[01])";
    public static final String TEXT_LABEL_PATTERN_TROUBLE_COUNTDOWN = "(0[1-3]):([0-9][0-9]):(([0-9][0-9]))";
    public static final String TEXT_LABEL_PATTERN_RESET_TROUBLE_COUNTDOWN = "(03):(59):((5[6-9]))";
    public static final String TEXT_LABEL_PATTERN_DAY_AT_TIME = TEXT_LABEL_PATTERN_AT_DAY_DATE + " " + TEXT_LABEL_PATTERN_AT_TIME;

    /**
     * Creates a matcher of {@link ImageView}s that matches when an examined ImageView
     * has the same Bitmap as resource with given resourceId
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isBitmapTheSame(mActivity, R.drawable.ic_down)));
     * </pre>
     *
     * @param resourceId - the id of resource with expected bitmap
     * @return Matcher<View> for given resourceId
     * @author attobus@gmail.com
     * https://gist.github.com/attobus/6eb04f840029d34882c8
     */
    public static Matcher<View> isBitmapTheSame(final int resourceId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has the same Bitmap as the resource with id=");
                description.appendValue(resourceId);
                description.appendText(" has.");
            }

            @Override
            public boolean matchesSafely(ImageView view) {
                Drawable otherDrawable = view.getResources().getDrawable(resourceId);
                return sameBitmap(view.getDrawable(), otherDrawable);
            }
        };
    }

    /**
     * Creates a matcher of {@link ImageView}s that matches when an examined ImageView
     * has the same Bitmap in Drawable as
     * the specified <code>operand</code>
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isBitmapTheSame(theDrawableWithExpectedBitmap)));
     * </pre>
     *
     * @param drawable - Drawable with expected Bitmap
     * @return Matcher<View> for given Drawable
     * @author attobus@gmail.com
     * https://gist.github.com/attobus/6eb04f840029d34882c8
     */
    public static Matcher<View> isBitmapTheSame(final Drawable drawable) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has the same Bitmap as this drawable: ");
                description.appendValue(drawable);
                description.appendText(" has.");
            }

            @Override
            public boolean matchesSafely(ImageView view) {
                return sameBitmap(view.getDrawable(), drawable);
            }
        };
    }

    /**
     * Creates a matcher of {@link ImageView}s that matches when an examined ImageView
     * has the same height and width in LayoutManager as
     * the specified <code>operand</code>
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isBitmapTheSame(theDrawableWithExpectedBitmap)));
     * </pre>
     *
     * @param expectedSizeLayoutParams with expected height and width
     * @return Matcher<View> for given Drawable
     * @author attobus@gmail.com
     * https://gist.github.com/attobus/6eb04f840029d34882c8
     */
    public static Matcher<View> isBitmapTheSameSize(final ViewGroup.LayoutParams expectedSizeLayoutParams) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has the same Bitmap size as these layoutParams: ");
                description.appendValue(expectedSizeLayoutParams);
                description.appendText(" has.");
            }

            @Override
            public boolean matchesSafely(ImageView view) {
                return  view.getMeasuredWidth() == expectedSizeLayoutParams.width &&
                        view.getMeasuredHeight() == expectedSizeLayoutParams.height;
            }
        };
    }

    /**
     * Creates a matcher of {@link ImageView}s that matches when an examined ImageView
     * has the same Bitmap
     * the specified <code>operand</code>
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isBitmapTheSame(theDrawableWithExpectedBitmap)));
     * </pre>
     *
     * @param expectedBitmap Bitmap
     * @return Matcher<View> for given Drawable
     * @author attobus@gmail.com
     * https://gist.github.com/attobus/6eb04f840029d34882c8
     */
    public static Matcher<View> isBitmapTheSame(final Bitmap expectedBitmap) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("has the same Bitmap as this bitmap: ");
                description.appendValue(expectedBitmap);
                description.appendText(" has.");
            }

            @Override
            public boolean matchesSafely(ImageView view) {
                Bitmap targetBitmap = ((BitmapDrawable)view.getDrawable()).getBitmap();
                //NOTE: sameAs requires API 12
                return targetBitmap.sameAs(expectedBitmap);
            }
        };
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed()))}
     */
    public static ViewInteraction onViewTextDisplayed(String text) {
        return onView(withText(text)).check(matches(isDisplayed()));
    }

    public static void onViewIdDisplayedWithPattern(int id, String regex) {
        onViewIdDisplayed(id).check(ViewAssertions.matches(isTextMatchingPattern(regex)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isCompletelyDisplayed()))}
     */
    public static ViewInteraction onViewTextCompletelyDisplayed(String text) {
        return onView(withText(text)).check(matches(isCompletelyDisplayed()));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(withClassName(containsString(className)))).check(matches(isCompletelyDisplayed()))}
     */
    public static ViewInteraction onViewTextCheckClassCheckCompletelyDisplayed(String text, String className) {
        return onView(withText(text)).check(matches(withClassName(containsString(className)))).check(matches(isCompletelyDisplayed()));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(doesNotExist())}
     */
    public static ViewInteraction onViewTextNotDisplayed(String text) {
        return onView(withText(text)).check(matches(not(isDisplayed())));
    }

    /**
     * Shortcut for {@code onView(withId(id)).check(matches(not(isDisplayed())))}
     */
    public static ViewInteraction onViewIdNotDisplayed(int id) {
        return onView(withId(id)).check(matches(not(isDisplayed())));
    }

    /**
     * Shortcut for {@code onView(withId(id)).check(doesNotExist())}
     */
    public static ViewInteraction onViewIdDoesNotExist(int id) {
        return onView(withId(id)).check(doesNotExist());
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(doesNotExist())}
     */
    public static ViewInteraction onViewTextDoesNotExist(String text) {
        return onView(withText(text)).check(doesNotExist());
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyAbove(withId(aboveId)))}
     */
    public static ViewInteraction onViewTextDisplayedAboveId(String text, int aboveId) {
        return onViewTextDisplayed(text).check(isCompletelyAbove(withId(aboveId)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyAbove(withText(aboveText)))}
     */
    public static ViewInteraction onViewTextDisplayedAboveText(String text, String aboveText) {
        return onViewTextDisplayed(text).check(isCompletelyAbove(withText(aboveText)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyBelow(withId(belowId)))}
     */
    public static ViewInteraction onViewTextDisplayedBelowId(String text, int belowId) {
        return onViewTextDisplayed(text).check(isCompletelyBelow(withId(belowId)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isCompletelyDisplayed())).check(isCompletelyBelow(withId(belowId)))}
     */
    public static ViewInteraction onViewTextCompletelyDisplayedBelowId(String text, int belowId) {
        return onViewTextCompletelyDisplayed(text).check(isCompletelyBelow(withId(belowId)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyBelow(withText(belowText)))}
     */
    public static ViewInteraction onViewTextDisplayedBelowText(String text, String belowText) {
        return onViewTextDisplayed(text).check(isCompletelyBelow(withText(belowText)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isCompletelyDisplayed())).check(isCompletelyBelow(withText(belowText)))}
     */
    public static ViewInteraction onViewTextCompletelyDisplayedBelowText(String text, String belowText) {
        return onViewTextCompletelyDisplayed(text).check(isCompletelyBelow(withText(belowText)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyLeftOf(withId(leftOfId)))}
     */
    public static ViewInteraction onViewTextDisplayedLeftOfId(String text, int leftOfId) {
        return onViewTextDisplayed(text).check(isCompletelyLeftOf(withId(leftOfId)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyLeftOf(withText(leftOfText)))}
     */
    public static ViewInteraction onViewTextDisplayedLeftOfText(String text, String leftOfText) {
        return onViewTextDisplayed(text).check(isCompletelyLeftOf(withText(leftOfText)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyRightOf(withId(rightOfId)))}
     */
    public static ViewInteraction onViewTextDisplayedRightOfId(String text, int rightOfId) {
        return onViewTextDisplayed(text).check(isCompletelyRightOf(withId(rightOfId)));
    }

    /**
     * Shortcut for {@code onView(withText(text)).check(matches(isDisplayed())).check(isCompletelyRightOf(withText(rightOfText)))}
     */
    public static ViewInteraction onViewTextDisplayedRightOfText(String text, String rightOfText) {
        return onViewTextDisplayed(text).check(isCompletelyRightOf(withText(rightOfText)));
    }


    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isDisplayed()))}
     */
    public static ViewInteraction onViewIdDisplayed(int id) {
        return onView(withId(id)).check(matches(isDisplayed()));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isCompletelyDisplayed()))}
     */
    public static ViewInteraction onViewIdCompletelyDisplayed(int id) {
        return onView(withId(id)).check(matches(isCompletelyDisplayed()));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isDisplayed())).check(isCompletelyRightOf(withText(rightOfText)))}
     */
    public static ViewInteraction onViewIdDisplayedRightOfText(int id, String rightOfText) {
        return onViewIdDisplayed(id).check(isCompletelyRightOf(withText(rightOfText)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isDisplayed())).check(isCompletelyRightOf(withId(rightOfId)))}
     */
    public static ViewInteraction onViewIdDisplayedRightOfId(int id, int rightOfId) {
        return onViewIdDisplayed(id).check(isCompletelyRightOf(withId(rightOfId)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isDisplayed())).check(isCompletelyLeftOf(withText(leftOfText)))}
     */
    public static ViewInteraction onViewIdDisplayedLeftOfText(int id, String leftOfText) {
        return onViewIdDisplayed(id).check(isCompletelyLeftOf(withText(leftOfText)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isDisplayed())).check(isCompletelyLeftOf(withId(leftOfId)))}
     */
    public static ViewInteraction onViewIdDisplayedLeftOfId(int id, int leftOfId) {
        return onViewIdDisplayed(id).check(isCompletelyLeftOf(withId(leftOfId)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isDisplayed())).check(isCompletelyBelow(withText(belowOfText)))}
     */
    public static ViewInteraction onViewIdDisplayedBelowText(int id, String belowOfText) {
        return onViewIdDisplayed(id).check(isCompletelyBelow(withText(belowOfText)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isDisplayed())).check(isCompletelyBelow(withId(belowOfId)))}
     */
    public static ViewInteraction onViewIdDisplayedBelowId(int id, int belowOfId) {
        return onViewIdDisplayed(id).check(isCompletelyBelow(withId(belowOfId)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.value)).check(matches(isBitmapTheSame(R.drawable.value))).check(matches(isDisplayed()));}
     */
    public static ViewInteraction onViewIdDisplayedWithDrawable(int id, int drawableId) {
        return onViewIdDisplayed(id).check(matches(isBitmapTheSame(drawableId)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.value)).check(matches(isBitmapTheSame(R.drawable.value))).check(matches(isDisplayed())).check(isCompletelyLeftOf(withText(leftOfText)))}
     */
    public static ViewInteraction onViewIdDisplayedWithDrawableLeftOfText(int id, int drawableId, String leftOfText) {
        return onViewIdDisplayedWithDrawable(id, drawableId).check(isCompletelyLeftOf(withText(leftOfText)));
    }

    /**
     * Shortcut for {@code onView(withId(R.id.value)).check(matches(withCompoundDrawableLeft(leftDrawable)));}
     */
    public static ViewInteraction onViewIdDisplayedWithCompoundDrawableLeft(int id, int drawableResId) {
        return onView(withId(id)).check(matches(withCompoundDrawableLeft(drawableResId)));
    }

    /**
     * Shortcut for {@code onView(withText(toastString)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));}
     */
    public static ViewInteraction onToastMessageDisplayed(String toastString, Activity activity) {
        return onView(withText(toastString)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    /**
     * Creates a matcher of {@link TextView}s that matches when an examined TextView
     * has label that matches the specified pattern.
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isTextMatchingPattern("[0-9]")));
     * </pre>
     *
     * @param pattern - expected label pattern
     * @return Matcher<View> for given pattern
     */
    public static Matcher<View> isTextMatchingPattern(final String pattern) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Text label with regex pattern: ");
                description.appendText(pattern);
            }

            @Override
            public boolean matchesSafely(TextView view) {
                return Pattern.compile(pattern).matcher(view.getText()).matches();
            }
        };
    }

    /**
     * Creates a matcher of {@link TextView}s that matches when an examined TextView
     * has label that matches {@link #TEXT_LABEL_PATTERN_AT_TIME} pattern.
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isTextMatchingAtTimePattern()));
     * </pre>
     *
     * @return Matcher<View> for given pattern
     */
    public static Matcher<View> isTextMatchingAtTimePattern() {
        return isTextMatchingPattern(TEXT_LABEL_PATTERN_AT_TIME);
    }

    /**
     * Creates a matcher of {@link TextView}s that matches when an examined TextView
     * has label that matches {@link #TEXT_LABEL_PATTERN_TIME} pattern.
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isTextMatchingTimePattern()));
     * </pre>
     *
     * @return Matcher<View> for given pattern
     */
    public static Matcher<View> isTextMatchingTimePattern() {
        return isTextMatchingPattern(TEXT_LABEL_PATTERN_TIME);
    }

    /**
     * Creates a matcher of {@link TextView}s that matches when an examined TextView
     * has label that matches {@link #TEXT_LABEL_PATTERN_TIME_WITH_SECONDS} pattern.
     * <pre>
     * Example:
     * onView(withId(R.id.imageDown)).check(matches(isTextMatchingTimePatternWithSeconds()));
     * </pre>
     *
     * @return Matcher<View> for given pattern
     */
    public static Matcher<View> isTextMatchingTimePatternWithSeconds() {
        return isTextMatchingPattern(TEXT_LABEL_PATTERN_TIME_WITH_SECONDS);
    }

    public static Matcher<View> isTextMatchingTroubleCountdown() {
        return isTextMatchingPattern(TEXT_LABEL_PATTERN_TROUBLE_COUNTDOWN);
    }
    public static Matcher<View> isTextMatchingResetTroubleCountdown() {
        return isTextMatchingPattern(TEXT_LABEL_PATTERN_RESET_TROUBLE_COUNTDOWN);
    }

    public static Matcher<View> isTextMatchingDayDatePattern() {
        return isTextMatchingPattern(TEXT_LABEL_PATTERN_AT_DAY_DATE);
    }

    public static Matcher<View> isTextMatchingDayAtTimePattern() {
        return isTextMatchingPattern(TEXT_LABEL_PATTERN_DAY_AT_TIME);
    }

    public static Matcher<View> withBackgroundColor(final int colorResId) {
        checkNotNull(colorResId);
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View view) {
                return matchesColor(view.getBackground(), view.getResources().getColor(colorResId));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color resource id " + colorResId + "': ");
            }
        };
    }

    public static Matcher<View> withBackgroundDrawable(final int drawableResId) {
        checkNotNull(drawableResId);
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View view) {

                Bitmap bitmap = convertBitmap(view.getResources().getDrawable(drawableResId));
                Bitmap otherBitmap = convertBitmap(view.getBackground());
                //NOTE: sameAs requires API 12
                return bitmap.sameAs(otherBitmap);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background drawable  resource id " + drawableResId + "': ");
            }
        };
    }

    public static Matcher<View> withCompoundDrawableLeft(final int drawableResId) {
        checkNotNull(drawableResId);
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View view) {

                Drawable drawableLeft = ((TextView) view).getCompoundDrawables()[0];
                checkNotNull(drawableLeft);


                Bitmap bitmap = convertBitmap(view.getResources().getDrawable(drawableResId));
                Bitmap otherBitmap = convertBitmap(drawableLeft);
                //NOTE: sameAs requires API 12
                return bitmap.sameAs(otherBitmap);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with left compound drawable resource id " + drawableResId + "': ");
            }
        };
    }

    public static Matcher<View> withNoCompoundDrawableLeft() {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View view) {

                Drawable drawableLeft = ((TextView) view).getCompoundDrawables()[0];

                return drawableLeft == null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with no left compound drawable");
            }
        };
    }

    private static boolean matchesColor(Drawable drawable, int color) {
        if (drawable instanceof StateListDrawable) {
            return matchesColor(drawable.getCurrent(), color);
        } else if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            return color == colorDrawable.getColor();
        } else if (drawable instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            return color == shapeDrawable.getPaint().getColor();
        } else {
            throw new InvalidParameterException("Unsupported drawable type");
        }
    }

    public static Matcher<View> withTextColor(final int color) {
        checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView textView) {
                return textView.getResources().getColor(color) == textView.getCurrentTextColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color " + color + "': ");
            }
        };
    }

    /**
     * Returns a generic {@link ViewAssertion} that asserts that there is a
     * given number of child views that match the specified matcher.
     *
     * @param expectedCount the number of child views that should match the specified matcher
     * @throws AssertionError if the number of views that match the selector is different from expectedCount
     */
    public static ViewAssertion hasChildCount(final int expectedCount) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertNotNull(view);

                int childCount = ((ViewGroup)view).getChildCount();
                if (childCount != expectedCount) {
                    throw new AssertionFailedError(String.format("Found %d views instead of %d", childCount, expectedCount));
                }
            }
        };
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("with "+childPosition+" child view of type parentMatcher");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view.getParent() instanceof ViewGroup)) {
                    return parentMatcher.matches(view.getParent());
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return parentMatcher.matches(view.getParent())
                        && group.getChildCount() > childPosition
                        && group.getChildAt(childPosition).equals(view);
            }
        };
    }

    public static Matcher<View> withListSize (final int size) {
        return new BoundedMatcher<View, ListView>(ListView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("list expected size - " + size);
            }

            @Override
            public boolean matchesSafely(ListView listView) {
                return listView.getChildCount() == size;
            }
        };
    }

    /**
     * This helper method extracts Bitmap from Drawable
     *
     * @param drawable - The drawable to convert to bitmap
     * @return Bitmap from the given drawable
     */
    private static boolean sameBitmap(Drawable drawable, Drawable otherDrawable) {
        if (drawable == null || otherDrawable == null) {
            return false;
        }

        Bitmap bitmap = convertBitmap(drawable);
        Bitmap otherBitmap = convertBitmap(otherDrawable);
        return bitmap.sameAs(otherBitmap);
    }

    private static Bitmap convertBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static ViewAction waitfor(final long waitInMillis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for :: " + waitInMillis;
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(waitInMillis);
            }
        };
    }

    //This action will ignore the Espresso default constraint of UI element being
    //visible at least 90% on screen
    public static ViewAction customClick() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isEnabled(); // no constraints, they are checked above
            }

            @Override
            public String getDescription() {
                return "click plus button";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.performClick();
            }
        };
    }

    //This action will ignore the Espresso default constraint of UI element being
    //visible at least 90% on screen
    public static ViewAction customScroll() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isDescendantOfA(anyOf(
                        isAssignableFrom(ScrollView.class),
                        isAssignableFrom(HorizontalScrollView.class),
                        isAssignableFrom(NestedScrollView.class)))
                );
            }

            @Override
            public String getDescription() {
                return "Scroll elements in NestedScrollView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                new ScrollToAction().perform(uiController, view);
            }
        };
    }


    /**
     * Shortcut for {@code onView(withId(R.id.Value)).check(matches(isClickable())))}
     */
    public static ViewInteraction onViewIdDisplayedClickable(int id) {
        return onViewIdDisplayed(id).check(matches(isClickable()));
    }

    public static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    //Matcher to check imageView contains an image
    public static BoundedMatcher<View, ImageView> hasDrawable() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has drawable");
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                return imageView.getDrawable() != null;
            }
        };
    }

    public static <T> Matcher<T> composite(final Matcher<T>... matchers) {
        return new BaseMatcher<T>() {
            @Override
            public boolean matches(Object o) {
                boolean res = true;
                if (matchers != null) {
                    for (Matcher<T> matcher : matchers) {
                        if (!matcher.matches(o)) {
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                if (matchers != null) {
                    for (Matcher<T> matcher : matchers) {
                        matcher.describeTo(description);
                        description.appendText("\n");
                    }
                }
            }
        };
    }

    static final class WithAncestorMatcher extends TypeSafeMatcher<View> {
        @RemoteMsgField(order = 0)
        private final Matcher<View> parentMatcher;

        @RemoteMsgConstructor
        private WithAncestorMatcher(Matcher<View> parentMatcher) {
            this.parentMatcher = parentMatcher;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("has parent matching: ");
            parentMatcher.describeTo(description);
        }

        @Override
        public boolean matchesSafely(View view) {
            ViewParent parent = view.getParent();
            while (parent != null) {
                if (parentMatcher.matches(parent)) {
                    return true;
                } else {
                    parent = parent.getParent();
                }
            }
            return false;
        }
    }

    public static Matcher<View> withAncestor(final Matcher<View> ancestorMatcher) {
        return new WithAncestorMatcher(checkNotNull(ancestorMatcher));
    }

}
