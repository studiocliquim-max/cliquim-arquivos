package com.studiocliquim.arquivos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

/** Family palette. Preferences are local; no cross-app permissions or network access. */
final class SuiteTheme {
    static final String PREFS="suite98_appearance", KEY="mode";
    private static boolean night;
    static SharedPreferences prefs(Context c){return c.getSharedPreferences(PREFS,Context.MODE_PRIVATE);}
    static String mode(Context c){return prefs(c).getString(KEY,"system");}
    static boolean dark(Context c){String m=mode(c);return "dark".equals(m)||("system".equals(m)&&(c.getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES);}
    static Context wrap(Context base){
        if(!prefs(base).contains(KEY)&&base.getSharedPreferences("state",0).contains("dark"))prefs(base).edit().putString(KEY,base.getSharedPreferences("state",0).getBoolean("dark",false)?"dark":"light").apply();
        String m=mode(base);Configuration config=new Configuration(base.getResources().getConfiguration());
        if(!"system".equals(m)){config.uiMode=(config.uiMode&~Configuration.UI_MODE_NIGHT_MASK)|("dark".equals(m)?Configuration.UI_MODE_NIGHT_YES:Configuration.UI_MODE_NIGHT_NO);base=base.createConfigurationContext(config);}
        init(base);return base;
    }
    static void init(Context c){night=dark(c);}
    static boolean dark(){return night;}
    static int surface(){return night?0xff20344d:0xffc0c0c0;}
    static int paper(){return night?0xff16283e:0xffffffff;}
    static int ink(){return night?0xffedf3fc:0xff000000;}
    static int muted(){return night?0xffb5c5db:0xff565656;}
    static int high(){return night?0xff617b99:0xffffffff;}
    static int low(){return night?0xff0a1728:0xff000000;}
    static int title(){return night?0xff24476b:0xff000080;}
    static int accent(){return night?0xffa3c9ff:0xff000080;}
    static int desktop(){return night?0xff101e32:0xff008080;}
    static int textColor(int c){
        if(!night)return c;
        if(c==0xff000000||c==0xff181818||c==0xff202020)return ink();
        if(c==0xff565656||c==0xff666666||c==0xff444444||c==0xff808080)return muted();
        if(c==0xff000080||c==title())return accent();
        return c;
    }
    static int surfaceColor(int c){
        if(!night)return c;
        if(c==0xffc0c0c0||c==0xffd4d0c8)return surface();
        if(c==0xffffffff||c==0xffeeeeee||c==0xfff0f0f0)return paper();
        if(c==0xffdfdfdf||c==0xff808080)return high();
        if(c==0xff000000)return low();
        if(c==0xff000080)return title();
        if(c==0xff1084d0)return 0xff346994;
        if(c==0xffd8e8f8||c==0xff8fa9df)return 0xff315477;
        return c;
    }
    static void refreshWidgets(Context c){
        android.appwidget.AppWidgetManager manager=android.appwidget.AppWidgetManager.getInstance(c);
        for(android.appwidget.AppWidgetProviderInfo provider:manager.getInstalledProviders()){
            if(!provider.provider.getPackageName().equals(c.getPackageName()))continue;
            int[] ids=manager.getAppWidgetIds(provider.provider);
            if(ids.length>0)c.sendBroadcast(new android.content.Intent(android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE).setComponent(provider.provider).putExtra(android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_IDS,ids));
        }
    }
    static void select(Activity a,String mode){
        if(!mode.equals("system")&&!mode.equals("light")&&!mode.equals("dark"))throw new IllegalArgumentException("Unknown theme");
        prefs(a).edit().putString(KEY,mode).apply();refreshWidgets(a);a.recreate();
    }
    static int dp(Context c,int n){return Math.round(n*c.getResources().getDisplayMetrics().density);}
    static android.graphics.drawable.Drawable control(Context c){return new android.graphics.drawable.Drawable(){
        final android.graphics.Paint paint=new android.graphics.Paint();
        public boolean isStateful(){return true;}
        protected boolean onStateChange(int[] state){invalidateSelf();return true;}
        public void draw(android.graphics.Canvas canvas){android.graphics.Rect r=getBounds();float e=Math.max(1,c.getResources().getDisplayMetrics().density);boolean down=false;for(int state:getState())if(state==android.R.attr.state_pressed)down=true;paint.setColor(surface());canvas.drawRect(r,paint);paint.setColor(down?low():high());canvas.drawRect(r.left,r.top,r.right,r.top+e,paint);canvas.drawRect(r.left,r.top,r.left+e,r.bottom,paint);paint.setColor(down?high():low());canvas.drawRect(r.left,r.bottom-e,r.right,r.bottom,paint);canvas.drawRect(r.right-e,r.top,r.right,r.bottom,paint);}
        public void setAlpha(int a){paint.setAlpha(a);}
        public void setColorFilter(android.graphics.ColorFilter f){paint.setColorFilter(f);}
        public int getOpacity(){return android.graphics.PixelFormat.OPAQUE;}
    };}
    static void showSelector(Activity a){
        android.app.Dialog d=new android.app.Dialog(a);d.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        android.widget.LinearLayout body=new android.widget.LinearLayout(a);body.setOrientation(android.widget.LinearLayout.VERTICAL);body.setPadding(dp(a,4),dp(a,4),dp(a,4),dp(a,4));body.setBackgroundColor(surface());
        android.widget.TextView title=new android.widget.TextView(a);title.setText("Tema da interface");title.setTextColor(0xffffffff);title.setTextSize(18);title.setPadding(dp(a,12),dp(a,12),dp(a,12),dp(a,12));title.setBackgroundColor(title());body.addView(title);
        android.widget.TextView note=new android.widget.TextView(a);note.setText("Acompanhar Android segue o horário noturno do aparelho. A escolha manual vale apenas para este aplicativo.");note.setTextColor(ink());note.setTextSize(16);note.setPadding(dp(a,12),dp(a,12),dp(a,12),dp(a,12));body.addView(note);
        String[] modes={"system","light","dark"},labels={"Acompanhar Android","Claro clássico","Azul escuro"};
        for(int i=0;i<modes.length;i++){final String value=modes[i];android.widget.Button b=new android.widget.Button(a);b.setText((mode(a).equals(value)?"✓ ":"")+labels[i]);b.setAllCaps(false);b.setStateListAnimator(null);b.setElevation(0);b.setBackground(control(a));b.setTextColor(ink());b.setTextSize(16);b.setMinHeight(Math.round(48*a.getResources().getDisplayMetrics().density));android.widget.LinearLayout.LayoutParams bp=new android.widget.LinearLayout.LayoutParams(-1,-2);bp.setMargins(dp(a,8),dp(a,4),dp(a,8),dp(a,4));body.addView(b,bp);b.setOnClickListener(v->{d.dismiss();select(a,value);});}
        android.widget.Button cancel=new android.widget.Button(a);cancel.setText("Cancelar");cancel.setAllCaps(false);cancel.setStateListAnimator(null);cancel.setElevation(0);cancel.setBackground(control(a));cancel.setTextColor(ink());cancel.setOnClickListener(v->d.dismiss());body.addView(cancel);
        android.widget.ScrollView scroll=new android.widget.ScrollView(a);scroll.addView(body);d.setContentView(scroll);d.show();d.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(surface()));d.getWindow().setLayout(Math.min(Math.round(420*a.getResources().getDisplayMetrics().density),a.getResources().getDisplayMetrics().widthPixels-dp(a,24)),-2);
    }

}
