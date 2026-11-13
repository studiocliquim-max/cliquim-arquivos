package com.studiocliquim.arquivos;
import android.app.*;import android.content.*;import android.content.res.Configuration;import android.graphics.Bitmap;import android.os.Bundle;import android.view.*;import java.io.*;
/** Emulator-only theme acceptance: persistence, rendered UI, selector and system mode. */
public final class FamilyThemeTest extends Instrumentation {
 private void require(boolean value,String label){if(!value)throw new AssertionError(label);}
 @Override public void onCreate(Bundle b){super.onCreate(b);start();}
 @Override public void onStart(){Bundle result=new Bundle();Activity active=null;try{
  Context c=getTargetContext();boolean systemDark=(c.getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;
  for(String mode:new String[]{"light","dark","system"}){
   c.getSharedPreferences(SuiteTheme.PREFS,0).edit().putString(SuiteTheme.KEY,mode).commit();
   active=startActivitySync(new Intent().setClassName(c.getPackageName(),"com.studiocliquim.arquivos.MainActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));final Activity a=active;
   waitForIdleSync();Thread.sleep(800);
   boolean dark=mode.equals("dark")||(mode.equals("system")&&systemDark);
   require(SuiteTheme.dark(a)==dark,"effective mode "+mode);
   require(((a.getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES)==dark,"native controls mode "+mode);
   require(SuiteTheme.mode(a).equals(mode),"persisted mode");
   capture(c,mode+"-home");runOnMainSync(()->SuiteTheme.showSelector(a));waitForIdleSync();Thread.sleep(200);capture(c,mode+"-selector");sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
   runOnMainSync(a::finish);active=null;Thread.sleep(150);
  }
  result.putString("stream","PASS family theme: light/dark/system, native controls, persistence, rendered home and selector. System dark="+systemDark+"\n");finish(Activity.RESULT_OK,result);
 }catch(Throwable e){result.putString("stream","FAIL family theme: "+e);if(active!=null){final Activity a=active;runOnMainSync(a::finish);}finish(Activity.RESULT_CANCELED,result);}}
 private void capture(Context c,String name)throws Exception{Bitmap b=getUiAutomation().takeScreenshot();require(b!=null,"capture");File dir=new File(c.getFilesDir(),"family-theme");dir.mkdirs();try(FileOutputStream out=new FileOutputStream(new File(dir,name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}b.recycle();}
}
