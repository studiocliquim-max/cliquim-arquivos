package com.studiocliquim.arquivos;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.util.concurrent.*;

/** A page inside the existing Explorer chrome, with an explicit return to its folder. */
final class RetroScreen {
    static final ExecutorService READ = Executors.newFixedThreadPool(2);
    final MainActivity a;
    final LinearLayout content;
    volatile boolean disposed;
    Runnable onDispose=()->{};

    RetroScreen(MainActivity activity,String title,String icon){
        a=activity;
        if(a.page!=null)a.page.dispose();
        a.page=this;
        a.generation++;
        a.body.removeAllViews();
        LinearLayout heading=new LinearLayout(a);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        heading.setPadding(a.dp(8),a.dp(8),a.dp(8),a.dp(8));
        heading.setBackground(a.bevel(false));
        heading.addView(a.symbol(icon,28),new LinearLayout.LayoutParams(a.dp(28),a.dp(28)));
        TextView label=a.text(title,17);label.setTypeface(null,Typeface.BOLD);label.setPadding(a.dp(8),0,0,0);
        heading.addView(label,new LinearLayout.LayoutParams(0,-2,1));a.body.addView(heading);
        content=new LinearLayout(a);content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(a.dp(8),a.dp(8),a.dp(8),a.dp(8));
        a.body.addView(content,new LinearLayout.LayoutParams(-1,0,1));
        a.address.setText(title);a.address.setContentDescription(title+". Escolher local");
        a.search.setVisibility(View.GONE);
        ((android.view.inputmethod.InputMethodManager)a.getSystemService(android.content.Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(a.root.getWindowToken(),0);
        for(View v:new View[]{a.fileButton,a.editButton,a.viewButton,a.favoritesButton,a.findButton,a.upButton,a.viewTool})a.enabled(v,false);
        footer(a.iconButton("back","Fechar",this::close));
        a.status.setText(title);
    }
    boolean alive(){return !disposed&&a.page==this&&!a.isDestroyed()&&!a.isFinishing();}
    void post(Runnable action){a.runOnUiThread(()->{if(alive())action.run();});}
    LinearLayout scroll(){ScrollView scroll=new ScrollView(a);LinearLayout body=new LinearLayout(a);body.setOrientation(LinearLayout.VERTICAL);scroll.addView(body);content.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));return body;}
    void footer(Button...buttons){a.footer.removeAllViews();LinearLayout row=new LinearLayout(a);for(Button b:buttons)row.addView(b,new LinearLayout.LayoutParams(0,-2,1));a.footer.addView(row);}
    void text(LinearLayout target,String text){TextView t=a.text(text,14);t.setPadding(a.dp(5),a.dp(8),a.dp(5),a.dp(8));target.addView(t);}
    void section(LinearLayout target,String text){TextView t=a.text(text,16);t.setTypeface(null,Typeface.BOLD);t.setPadding(a.dp(6),a.dp(10),a.dp(6),a.dp(10));t.setBackground(a.bevel(false));target.addView(t);}
    TextView value(LinearLayout target,String label,String value){
        LinearLayout row=new LinearLayout(a);row.setOrientation(a.getResources().getConfiguration().fontScale>1.2f?LinearLayout.VERTICAL:LinearLayout.HORIZONTAL);
        row.setPadding(a.dp(6),a.dp(8),a.dp(6),a.dp(8));TextView title=a.text(label,14);title.setTypeface(null,Typeface.BOLD);
        row.addView(title,row.getOrientation()==LinearLayout.HORIZONTAL?new LinearLayout.LayoutParams(a.dp(96),-2):new LinearLayout.LayoutParams(-1,-2));
        TextView t=a.text(value,14);t.setTextIsSelectable(true);row.addView(t,row.getOrientation()==LinearLayout.HORIZONTAL?new LinearLayout.LayoutParams(0,-2,1):new LinearLayout.LayoutParams(-1,-2));target.addView(row);return t;
    }
    Button entry(LinearLayout target,String icon,String title,String subtitle,Runnable action){
        Button b=a.button(title+(subtitle.isEmpty()?"":"\n"+subtitle),action);b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        RetroUi.Symbol symbol=new RetroUi.Symbol(icon,a.dark);symbol.setBounds(0,0,a.dp(28),a.dp(28));b.setCompoundDrawables(symbol,null,null,null);b.setCompoundDrawablePadding(a.dp(10));b.setPadding(a.dp(8),a.dp(10),a.dp(8),a.dp(10));
        target.addView(b,new LinearLayout.LayoutParams(-1,-2));return b;
    }
    void dispose(){if(disposed)return;disposed=true;onDispose.run();if(a.page==this)a.page=null;}
    void close(){dispose();a.build();if(a.preview!=null)a.preview.show();else a.load();}
}
