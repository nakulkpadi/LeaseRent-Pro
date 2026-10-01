package com.nakuul.fieldkeep;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public final class Ui {
    private Ui() {}

    public static int BG = Color.rgb(247, 250, 254);
    public static int SURFACE = Color.WHITE;
    public static int INK = Color.rgb(18, 48, 78);
    public static int MUTED = Color.rgb(92, 109, 126);
    public static int LINE = Color.rgb(222, 231, 241);
    public static int NAVY = Color.rgb(10, 48, 84);
    public static int PRIMARY = Color.rgb(17, 103, 205);
    public static int PRIMARY_DARK = Color.rgb(8, 76, 158);
    public static int ORANGE = Color.rgb(240, 103, 31);
    public static int ACCENT = PRIMARY;
    public static int ACCENT_SOFT = Color.rgb(232, 243, 255);
    public static int CHIP = Color.rgb(240, 246, 253);
    public static int DANGER = Color.rgb(198, 57, 52);

    public static boolean isDark(Context c) {
        String pref = AppPrefs.appTheme(c);
        if ("Dark".equalsIgnoreCase(pref)) return true;
        if ("Light".equalsIgnoreCase(pref)) return false;
        int mode = c.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void applyTheme(Context c) {
        boolean dark = isDark(c);
        if (dark) {
            BG = Color.rgb(14, 20, 27);
            SURFACE = Color.rgb(24, 32, 41);
            INK = Color.rgb(231, 239, 247);
            MUTED = Color.rgb(164, 179, 193);
            LINE = Color.rgb(55, 68, 81);
            NAVY = Color.rgb(205, 224, 242);
            PRIMARY = Color.rgb(64, 143, 235);
            PRIMARY_DARK = Color.rgb(110, 173, 245);
            ORANGE = Color.rgb(246, 121, 49);
            ACCENT = PRIMARY;
            ACCENT_SOFT = Color.rgb(31, 49, 69);
            CHIP = Color.rgb(29, 40, 52);
            DANGER = Color.rgb(239, 100, 95);
        } else {
            BG = Color.rgb(247, 250, 254);
            SURFACE = Color.WHITE;
            INK = Color.rgb(18, 48, 78);
            MUTED = Color.rgb(92, 109, 126);
            LINE = Color.rgb(222, 231, 241);
            NAVY = Color.rgb(10, 48, 84);
            PRIMARY = Color.rgb(17, 103, 205);
            PRIMARY_DARK = Color.rgb(8, 76, 158);
            ORANGE = Color.rgb(240, 103, 31);
            ACCENT = PRIMARY;
            ACCENT_SOFT = Color.rgb(232, 243, 255);
            CHIP = Color.rgb(240, 246, 253);
            DANGER = Color.rgb(198, 57, 52);
        }
    }

    public static int dp(Context c, int v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    public static void prepareWindow(Activity activity, boolean lightBars) {
        applyTheme(activity);
        activity.setTheme(isDark(activity) ? android.R.style.Theme_Material_NoActionBar : android.R.style.Theme_Material_Light_NoActionBar);
        if (lightBars) lightBars = !isDark(activity);
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(lightBars);
        controller.setAppearanceLightNavigationBars(lightBars);
    }

    public static void applySystemBarInsets(View view, int extraTopDp, int extraBottomDp) {
        final int left = view.getPaddingLeft();
        final int top = view.getPaddingTop();
        final int right = view.getPaddingRight();
        final int bottom = view.getPaddingBottom();
        final int extraTop = dp(view.getContext(), extraTopDp);
        final int extraBottom = dp(view.getContext(), extraBottomDp);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(left + bars.left, top + bars.top + extraTop, right + bars.right, bottom + bars.bottom + extraBottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }

    public static GradientDrawable rounded(int color, float radiusDp, Context c) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(c, (int) radiusDp));
        return g;
    }

    public static GradientDrawable roundedStroke(int color, float radiusDp, int strokeColor, Context c) {
        GradientDrawable g = rounded(color, radiusDp, c);
        g.setStroke(dp(c, 1), strokeColor);
        return g;
    }

    public static LinearLayout column(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(c,18), dp(c,12), dp(c,18), dp(c,28));
        l.setBackgroundColor(BG);
        return l;
    }

    public static TextView title(Context c, String s) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(24);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setTextColor(INK);
        t.setPadding(0, dp(c,8), 0, dp(c,8));
        return t;
    }

    public static TextView label(Context c, String s) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(12);
        t.setLetterSpacing(.08f);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setTextColor(MUTED);
        t.setPadding(0, dp(c,14), 0, dp(c,6));
        return t;
    }

    public static EditText input(Context c, String hint) {
        EditText e = new EditText(c);
        e.setHint(hint);
        e.setHintTextColor(Color.rgb(145,157,169));
        e.setTextColor(INK);
        e.setTextSize(16);
        e.setSingleLine(true);
        e.setPadding(dp(c,14), dp(c,12), dp(c,14), dp(c,12));
        e.setBackground(roundedStroke(SURFACE, 13, LINE, c));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(c, 2);
        e.setLayoutParams(lp);
        return e;
    }

    public static Button button(Context c, String text) {
        Button b = new Button(c);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(rounded(PRIMARY, 14, c));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(c,52));
        lp.topMargin = dp(c,8);
        b.setLayoutParams(lp);
        return b;
    }

    public static ImageView lucide(Context c, int drawableRes, int tint) {
        ImageView v = new ImageView(c);
        v.setImageResource(drawableRes);
        v.setColorFilter(tint);
        v.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        v.setPadding(dp(c,11), dp(c,11), dp(c,11), dp(c,11));
        v.setClickable(true);
        v.setFocusable(true);
        return v;
    }

    public static void startIcon(TextView t, int drawableRes, int tint, int gapDp) {
        Drawable d = ContextCompat.getDrawable(t.getContext(), drawableRes);
        if (d != null) {
            d = DrawableCompat.wrap(d.mutate());
            DrawableCompat.setTint(d, tint);
            d.setBounds(0,0,dp(t.getContext(),20),dp(t.getContext(),20));
            t.setCompoundDrawables(d,null,null,null);
            t.setCompoundDrawablePadding(dp(t.getContext(),gapDp));
        }
    }

    public static TextView iconButton(Context c, String text) {
        TextView v = new TextView(c);
        v.setText(text);
        v.setTextSize(22);
        v.setTextColor(INK);
        v.setGravity(Gravity.CENTER);
        v.setBackground(rounded(Color.TRANSPARENT, 24, c));
        v.setClickable(true);
        v.setFocusable(true);
        return v;
    }

    public static TextView fab(Context c, String text, int sizeDp) {
        TextView v = new TextView(c);
        v.setText(text);
        v.setTextSize(sizeDp >= 64 ? 34 : 24);
        v.setTextColor(Color.WHITE);
        v.setGravity(Gravity.CENTER);
        v.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        v.setElevation(dp(c,8));
        v.setBackground(rounded(PRIMARY, sizeDp / 2f, c));
        v.setClickable(true);
        v.setFocusable(true);
        v.setLayoutParams(new ViewGroup.LayoutParams(dp(c,sizeDp), dp(c,sizeDp)));
        return v;
    }

    public static TextView actionChip(Context c, String text) {
        TextView t = new TextView(c);
        t.setText(text);
        t.setTextColor(INK);
        t.setTextSize(15);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setPadding(dp(c,17),0,dp(c,18),0);
        t.setBackground(roundedStroke(SURFACE, 25, LINE, c));
        t.setElevation(dp(c,2));
        t.setClickable(true);
        t.setFocusable(true);
        return t;
    }

    public static LinearLayout card(Context c) {
        LinearLayout card = new LinearLayout(c);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(c,14), dp(c,12), dp(c,14), dp(c,12));
        card.setBackground(roundedStroke(SURFACE, 17, LINE, c));
        card.setElevation(dp(c,1));
        return card;
    }

    public static TextView body(Context c, String s) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(15);
        t.setTextColor(MUTED);
        t.setLineSpacing(0, 1.08f);
        return t;
    }

    public static TextView primaryAction(Context c, String text) {
        TextView t = new TextView(c);
        t.setText(text);
        t.setTextColor(Color.WHITE);
        t.setTextSize(15);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setBackground(rounded(PRIMARY,14,c));
        t.setClickable(true);
        return t;
    }

    public static TextView orangeAction(Context c, String text) {
        TextView t = primaryAction(c,text);
        t.setBackground(rounded(ORANGE,14,c));
        return t;
    }

    public static View spacer(Context c, int h) {
        View v = new View(c);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(c,h)));
        return v;
    }
}
