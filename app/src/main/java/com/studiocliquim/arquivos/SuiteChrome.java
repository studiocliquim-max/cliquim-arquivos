package com.studiocliquim.arquivos;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.*;

/** Shared visual treatment; does not hide system controls or change navigation mode. */
final class SuiteChrome {
  static void apply(Activity activity) {
    SuiteTheme.init(activity);Window window = activity.getWindow();
    window.setNavigationBarColor(SuiteTheme.surface());
    if (Build.VERSION.SDK_INT >= 28) window.setNavigationBarDividerColor(SuiteTheme.surface());
    if (Build.VERSION.SDK_INT >= 29) window.setNavigationBarContrastEnforced(false);
    View decor = window.getDecorView();
    if (Build.VERSION.SDK_INT >= 30 && decor.getWindowInsetsController() != null)
      decor.getWindowInsetsController().setSystemBarsAppearance(SuiteTheme.dark()?0:WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS);
    else if (Build.VERSION.SDK_INT >= 26) decor.setSystemUiVisibility(SuiteTheme.dark()?decor.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR:decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    if (Build.VERSION.SDK_INT >= 35) {
      Drawable edge = new Drawable() {
        final Paint paint = new Paint();
        public void draw(Canvas canvas) {paint.setColor(SuiteTheme.surface()); canvas.drawRect(getBounds(),paint);}
        public void setAlpha(int value) {}
        public void setColorFilter(ColorFilter filter) {}
        public int getOpacity() {return PixelFormat.OPAQUE;}
      };
      decor.getOverlay().add(edge);
      Runnable update = () -> {
        WindowInsets insets = decor.getRootWindowInsets();
        int bottom = insets == null ? 0 : insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
        edge.setBounds(0, Math.max(0, decor.getHeight()-bottom), decor.getWidth(), decor.getHeight());
      };
      decor.addOnLayoutChangeListener((v,l,t,rr,b,ol,ot,or,ob)->update.run());
      decor.setOnApplyWindowInsetsListener((v,insets)->{decor.post(update);return v.onApplyWindowInsets(insets);});
      decor.post(update);
    }
  }
}
