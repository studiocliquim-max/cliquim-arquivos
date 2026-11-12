package com.studiocliquim.arquivos;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Original pixel-grid illustrations and drawn chrome. No Microsoft assets are bundled. */
final class RetroUi {
 static class Frame extends Drawable {
  final int fill,light,shadow,innerLight,innerShadow;final boolean inset,focus;final Paint p=new Paint();
  private int alpha=255;
  Frame(int fill,boolean dark,boolean inset,boolean focus){
   this.fill=fill;this.inset=inset;this.focus=focus;
   light=dark?0xff929298:0xffffffff;shadow=dark?0xff08080b:0xff303034;
   innerLight=dark?0xff62626a:0xffdfdfe2;innerShadow=dark?0xff202026:0xff808086;
   p.setAntiAlias(false);
  }
  private void color(int value){p.setColor(value);p.setAlpha(Color.alpha(value)*alpha/255);}
  private void edge(Canvas c,Rect b,float d,float s,int top,int bottom){
   color(top);c.drawRect(b.left+d,b.top+d,b.right-d,b.top+d+s,p);
   c.drawRect(b.left+d,b.top+d+s,b.left+d+s,b.bottom-d,p);
   color(bottom);c.drawRect(b.left+d+s,b.bottom-d-s,b.right-d,b.bottom-d,p);
   c.drawRect(b.right-d-s,b.top+d+s,b.right-d,b.bottom-d-s,p);
  }
  @Override public void draw(Canvas c){
   Rect b=getBounds();if(b.isEmpty())return;
   // Thickness follows screen density, never the control's width. A narrow button
   // and a full-width pane therefore receive the same two one-dp bevel rings.
   float unit=Math.max(1,Math.round(Resources.getSystem().getDisplayMetrics().density));
   float s=Math.min(unit,Math.min(b.width(),b.height())/4f);
   p.setStyle(Paint.Style.FILL);color(fill);c.drawRect(b,p);
   edge(c,b,0,s,inset?shadow:light,inset?light:shadow);
   edge(c,b,s,s,inset?innerShadow:innerLight,inset?innerLight:innerShadow);
   if(focus && b.width()>10*s && b.height()>10*s){
    int contrast=(Color.red(fill)*299+Color.green(fill)*587+Color.blue(fill)*114)>140000?0xff101014:0xfff8f8ff;
    color(contrast);float l=b.left+4*s,t=b.top+4*s,r=b.right-5*s,bt=b.bottom-5*s;
    // True square dots remain crisp at mdpi, xhdpi and non-integer densities.
    for(float x=l;x<=r;x+=2*s){c.drawRect(x,t,Math.min(x+s,r+s),t+s,p);c.drawRect(x,bt,Math.min(x+s,r+s),bt+s,p);}
    for(float y=t+2*s;y<bt;y+=2*s){c.drawRect(l,y,l+s,Math.min(y+s,bt),p);c.drawRect(r,y,r+s,Math.min(y+s,bt),p);}
   }
  }
  @Override public void setAlpha(int a){alpha=Math.max(0,Math.min(255,a));invalidateSelf();}
  @Override public void setColorFilter(ColorFilter f){p.setColorFilter(f);invalidateSelf();}
  @Override public int getOpacity(){return alpha==255&&Color.alpha(fill)==255?PixelFormat.OPAQUE:PixelFormat.TRANSLUCENT;}
 }
 static class Symbol extends Drawable {
  final String type;final boolean dark;final Paint p=new Paint();private int alpha=255;
  static final int INK=0xff34343c,PAPER=0xfffcfcf5,WHITE=0xffffffff,BLUE=0xff2459a8,
    PALE=0xffb8dbed,GOLD=0xfff8d35b,GOLD_DARK=0xffba822a,GOLD_LIGHT=0xffffed9b,
    GREEN=0xff2d944b,GREEN_LIGHT=0xff87d56a,RED=0xffc84145;
  Symbol(String t){this(t,false);}Symbol(String t,boolean d){type=t==null?"":t;dark=d;p.setAntiAlias(false);}
  private void color(int value){p.setColor(value);p.setAlpha(Color.alpha(value)*alpha/255);}
  void rect(Canvas c,int color,float x,float y,float w,float h){color(color);c.drawRect(x,y,x+w,y+h,p);}
  private void poly(Canvas c,int color,float...xy){color(color);Path a=new Path();a.moveTo(xy[0],xy[1]);for(int i=2;i<xy.length;i+=2)a.lineTo(xy[i],xy[i+1]);a.close();c.drawPath(a,p);}
  private void folder(Canvas c,int x,int y,int width,int height){
   if(width<=8 || height<=8){
    rect(c,INK,x,y+1,width,height-1);rect(c,INK,x+1,y,Math.max(2,width/2),1);
    rect(c,GOLD,x+1,y+2,width-2,Math.max(1,height-3));rect(c,GOLD_LIGHT,x+1,y+1,width-2,1);
    return;
   }
   rect(c,INK,x+1,y,width/2,3);rect(c,GOLD_LIGHT,x+2,y+1,width/2-2,3);
   rect(c,INK,x,y+3,width,height-3);rect(c,GOLD,x+1,y+4,width-2,height-5);
   rect(c,GOLD_LIGHT,x+2,y+5,width-4,2);rect(c,0xffffdf78,x+2,y+7,width-4,height-11);
   rect(c,GOLD_DARK,x+1,y+height-3,width-2,2);rect(c,GOLD_DARK,x+width-3,y+6,2,height-9);
   rect(c,WHITE,x+2,y+4,width-5,1);
  }
  private void paper(Canvas c,int x,int y,int width,int height){
   poly(c,INK,x,y,x+width-6,y,x+width,y+6,x+width,y+height,x,y+height);
   poly(c,PAPER,x+1,y+1,x+width-6,y+1,x+width-1,y+6,x+width-1,y+height-1,x+1,y+height-1);
   poly(c,0xffd1d5d9,x+width-6,y+1,x+width-6,y+6,x+width-1,y+6);
   rect(c,INK,x+width-6,y+6,5,1);rect(c,0xffb6bdc7,x+2,y+height-3,width-4,1);
  }
  private void arrowRight(Canvas c,int x,int y,int color){
   poly(c,INK,x,y+4,x+10,y+4,x+10,y,x+12,y,x+12,y+2,x+14,y+2,x+14,y+4,x+16,y+4,x+16,y+6,x+18,y+6,x+18,y+8,x+16,y+8,x+16,y+10,x+14,y+10,x+14,y+12,x+12,y+12,x+12,y+14,x+10,y+14,x+10,y+10,x,y+10);
   poly(c,color,x+1,y+5,x+11,y+5,x+11,y+2,x+16,y+7,x+11,y+12,x+11,y+9,x+1,y+9);
   rect(c,0xffb7e6a0,x+2,y+5,9,1);
  }
  @Override public void draw(Canvas canvas){
   Rect b=getBounds();if(b.isEmpty())return;int save=canvas.save();
   float scale=Math.min(b.width(),b.height())/32f;
   canvas.translate(b.left+(b.width()-32*scale)/2,b.top+(b.height()-32*scale)/2);canvas.scale(scale,scale);
   Canvas c=canvas;p.setStyle(Paint.Style.FILL);p.setPathEffect(null);
   switch(type){
    case "folder" -> folder(c,2,5,28,24);
    case "up" -> {
     folder(c,2,13,28,17);
     poly(c,INK,12,19,12,11,7,11,7,9,9,9,9,7,11,7,11,5,13,5,13,3,15,3,15,1,17,1,17,3,19,3,19,5,21,5,21,7,23,7,23,9,25,9,25,11,20,11,20,19);
     poly(c,GREEN,13,18,13,10,10,10,16,4,22,10,19,10,19,18);
     rect(c,GREEN_LIGHT,14,10,2,7);rect(c,0xffb4ec96,15,7,2,3);
    }
    case "back" -> {
     c.save();c.translate(31,7);c.scale(-1,1);arrowRight(c,2,2,0xff4f9d6d);c.restore();
    }
    case "computer" -> {
     rect(c,INK,4,2,24,21);rect(c,0xffd6d6d5,5,3,22,19);rect(c,WHITE,6,4,20,1);
     rect(c,0xff757d87,7,6,18,13);rect(c,INK,8,7,16,11);rect(c,0xff2a7186,9,8,14,9);
     rect(c,0xff70ccd0,10,9,11,2);rect(c,0xff479dae,10,11,7,5);rect(c,WHITE,23,20,2,1);
     rect(c,INK,12,23,8,3);rect(c,0xffa8acb2,13,23,6,2);
     poly(c,INK,6,25,26,25,30,29,30,31,2,31,2,29);poly(c,0xffd7dadd,7,26,25,26,28,29,4,29);
     for(int i=0;i<5;i++)rect(c,0xff727c86,7+i*4,27,2,1);rect(c,WHITE,5,30,22,1);
    }
    case "disk" -> {
     poly(c,INK,6,7,26,7,30,13,30,26,2,26,2,13);
     poly(c,0xffc5cbd1,7,8,25,8,28,13,4,13);rect(c,WHITE,7,9,17,1);
     rect(c,0xffe4e6e7,3,14,26,10);rect(c,0xff9ca3aa,4,15,24,2);rect(c,0xff56616d,4,20,24,3);
     rect(c,INK,6,21,13,1);rect(c,GREEN_LIGHT,23,21,3,1);rect(c,WHITE,4,24,24,1);rect(c,INK,5,27,4,2);rect(c,INK,23,27,4,2);
    }
    case "Áudios","audio","audios" -> {
     paper(c,5,2,22,28);rect(c,BLUE,13,9,2,14);rect(c,BLUE,23,7,2,13);
     poly(c,BLUE,13,9,25,6,25,10,13,13);rect(c,0xff79b1d9,15,9,8,1);
     rect(c,INK,8,22,7,5);rect(c,BLUE,8,22,6,4);rect(c,PALE,9,22,3,1);
     rect(c,INK,18,19,7,5);rect(c,BLUE,18,19,6,4);rect(c,PALE,19,19,3,1);
    }
    case "copy" -> {
     paper(c,2,2,19,23);rect(c,0xff8398ad,5,10,11,1);rect(c,0xff8398ad,5,14,11,1);
     paper(c,11,8,19,23);rect(c,BLUE,14,16,10,1);rect(c,BLUE,14,20,11,1);rect(c,BLUE,14,24,8,1);
    }
    case "move" -> {
     folder(c,1,14,18,16);arrowRight(c,12,3,GREEN);rect(c,WHITE,4,19,8,1);
    }
    case "delete" -> {
     poly(c,INK,8,9,25,9,23,30,10,30);poly(c,0xffdce4e7,9,10,24,10,22,29,11,29);
     rect(c,0xff87949e,13,13,2,13);rect(c,0xff87949e,18,13,2,13);
     rect(c,INK,6,7,21,3);rect(c,0xffd1d8de,7,7,19,1);rect(c,INK,12,3,9,4);rect(c,WHITE,13,4,7,2);
     // Small red prohibition cross is an action cue, not a promise of a recycle bin.
     poly(c,INK,1,15,4,15,8,19,12,15,15,15,15,18,11,22,15,26,15,29,12,29,8,25,4,29,1,29,1,26,5,22,1,18);
     poly(c,RED,2,16,3,16,8,21,13,16,14,16,14,17,9,22,14,27,14,28,13,28,8,23,3,28,2,28,2,27,7,22,2,17);
    }
    case "new" -> {
     folder(c,2,12,27,18);rect(c,INK,19,1,5,11);rect(c,INK,16,4,11,5);
     rect(c,GOLD_LIGHT,20,2,3,9);rect(c,GOLD_LIGHT,17,5,9,3);rect(c,WHITE,21,3,1,5);
     rect(c,GOLD_DARK,12,2,2,2);rect(c,GOLD_DARK,28,10,2,2);rect(c,GOLD_DARK,28,1,2,2);
    }
    case "select" -> {
     int border=dark?0xffd1d5de:BLUE;
     for(int i=2;i<27;i+=4){rect(c,border,i,3,2,1);rect(c,border,i,26,2,1);rect(c,border,2,i+1,1,2);rect(c,border,27,i+1,1,2);}
     rect(c,0xffb4d3eb,7,8,15,12);rect(c,BLUE,8,9,13,10);rect(c,WHITE,10,11,8,1);rect(c,PALE,10,14,6,1);
     poly(c,INK,18,16,18,30,22,26,25,31,28,29,25,24,30,24);poly(c,WHITE,19,18,19,27,22,24,26,29,27,28,23,23,27,23);
    }
    case "check" -> {
     poly(c,INK,2,15,7,10,13,16,24,3,30,8,14,29);poly(c,GREEN,4,15,7,12,13,19,25,5,28,8,14,26);
     poly(c,GREEN_LIGHT,5,15,7,14,13,21,25,7,26,8,13,23);rect(c,0xffe0ffc2,6,14,1,2);
    }
    case "grid" -> {
     rect(c,INK,2,3,28,26);rect(c,PAPER,3,4,26,24);rect(c,BLUE,3,4,26,3);
     for(int y=0;y<2;y++)for(int x=0;x<3;x++){folder(c,5+x*8,10+y*9,6,6);rect(c,0xff8494a3,5+x*8,17+y*9,6,1);}
    }
    case "list" -> {
     rect(c,INK,2,3,28,26);rect(c,PAPER,3,4,26,24);rect(c,BLUE,3,4,26,3);
     for(int y=0;y<3;y++){folder(c,5,9+y*6,6,5);rect(c,0xff5a7793,14,10+y*6,12,1);rect(c,0xffb2bdc6,14,12+y*6,8,1);}
    }
    case "search" -> {
     poly(c,INK,7,2,19,2,19,4,23,4,23,8,25,8,25,18,23,18,23,21,20,21,20,23,8,23,8,21,4,21,4,18,2,18,2,7,4,7,4,4,7,4);
     poly(c,0xff658da5,8,4,18,4,18,6,21,6,21,9,23,9,23,17,21,17,21,19,18,19,18,21,9,21,9,19,6,19,6,17,4,17,4,8,6,8,6,6,8,6);
     rect(c,0xffc4ecf4,8,7,11,11);rect(c,0xffc4ecf4,6,9,15,7);rect(c,WHITE,8,8,7,2);rect(c,WHITE,7,10,2,5);
     poly(c,INK,21,19,31,27,31,30,28,32,18,23);poly(c,GOLD_DARK,22,21,29,27,29,29,28,30,21,23);rect(c,GOLD,23,23,2,2);
    }
    case "star" -> {
     poly(c,INK,14,1,18,1,18,5,20,5,20,10,30,10,30,14,26,14,26,18,23,18,23,22,25,22,25,30,21,30,21,28,16,25,11,28,11,30,7,30,7,22,9,22,9,18,6,18,6,14,2,14,2,10,12,10,12,5,14,5);
     poly(c,GOLD,16,4,20,12,27,12,21,18,23,27,16,23,9,27,11,18,5,12,13,12);
     poly(c,GOLD_LIGHT,16,6,16,21,10,25,12,17,8,13,14,13);rect(c,WHITE,15,10,1,5);
    }
    case "help" -> {
     poly(c,INK,8,2,24,2,24,4,28,4,28,8,30,8,30,24,28,24,28,28,24,28,24,30,8,30,8,28,4,28,4,24,2,24,2,8,4,8,4,4,8,4);
     rect(c,BLUE,5,8,22,16);rect(c,BLUE,8,5,16,22);rect(c,0xff5783bc,8,5,15,2);rect(c,0xff5783bc,5,8,2,13);
     rect(c,WHITE,11,8,10,3);rect(c,WHITE,9,10,3,4);rect(c,WHITE,19,11,3,5);rect(c,WHITE,16,15,5,3);rect(c,WHITE,14,17,3,4);rect(c,WHITE,14,24,3,3);
    }
    case "transfer" -> {arrowRight(c,11,1,GREEN);c.save();c.translate(31,15);c.scale(-1,1);arrowRight(c,11,1,BLUE);c.restore();}
    default -> {
     paper(c,5,2,22,28);
     if(type.equals("Imagens")){rect(c,INK,8,8,16,14);rect(c,0xff8cc7e6,9,9,14,12);rect(c,GOLD_LIGHT,18,10,3,3);poly(c,0xff4b9052,9,20,9,17,13,13,17,17,19,15,23,20);rect(c,0xff335f3e,10,20,12,1);}
     else if(type.equals("Vídeos")){rect(c,INK,8,8,16,15);rect(c,0xff486d99,11,9,10,13);for(int y=9;y<22;y+=4){rect(c,WHITE,9,y,1,2);rect(c,WHITE,22,y,1,2);}poly(c,WHITE,14,11,20,16,14,20);}
     else{rect(c,BLUE,9,10,13,2);rect(c,0xff8197ae,9,15,13,1);rect(c,0xff8197ae,9,19,13,1);rect(c,0xff8197ae,9,23,9,1);}
    }
   }
   canvas.restoreToCount(save);
  }
  @Override public void setAlpha(int a){alpha=Math.max(0,Math.min(255,a));invalidateSelf();}
  @Override public void setColorFilter(ColorFilter f){p.setColorFilter(f);invalidateSelf();}
  @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
 }
}
