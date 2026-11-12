package com.studiocliquim.arquivos;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Original drawn chrome; no historical Microsoft assets are bundled. */
final class RetroUi {
 static class Frame extends Drawable {
  final int fill,light,shadow;final boolean inset,focus;final Paint p=new Paint();
  Frame(int fill,boolean dark,boolean inset,boolean focus){this.fill=fill;this.inset=inset;this.focus=focus;light=dark?0xff777777:0xffffffff;shadow=dark?0xff101010:0xff404040;}
  public void draw(Canvas c){Rect b=getBounds();p.setColor(fill);c.drawRect(b,p);int s=Math.max(1,(int)(b.width()/400f));p.setStrokeWidth(s);p.setColor(inset?shadow:light);c.drawLine(b.left,b.top,b.right-1,b.top,p);c.drawLine(b.left,b.top,b.left,b.bottom-1,p);p.setColor(inset?light:shadow);c.drawLine(b.left,b.bottom-1,b.right-1,b.bottom-1,p);c.drawLine(b.right-1,b.top,b.right-1,b.bottom-1,p);if(focus){p.setStyle(Paint.Style.STROKE);p.setColor(Color.WHITE);p.setPathEffect(new DashPathEffect(new float[]{2,2},0));c.drawRect(b.left+4,b.top+4,b.right-5,b.bottom-5,p);p.setPathEffect(null);p.setStyle(Paint.Style.FILL);}}
  public void setAlpha(int a){}public void setColorFilter(ColorFilter f){}public int getOpacity(){return PixelFormat.OPAQUE;}
 }
 static class Symbol extends Drawable {
  final String type; final boolean dark; final Paint p=new Paint();Symbol(String t){this(t,false);}Symbol(String t,boolean d){type=t;dark=d;}
  void rect(Canvas c,int color,float x,float y,float w,float h){p.setColor(color);c.drawRect(x,y,x+w,y+h,p);}
  public void draw(Canvas canvas){canvas.save();Rect b=getBounds();canvas.translate(b.left,b.top);canvas.scale(b.width()/32f,b.height()/32f);Canvas c=canvas;
   switch(type){
    case "folder" -> {rect(c,0xff303030,2,7,28,22);rect(c,0xffffdd36,3,8,26,20);rect(c,0xff303030,3,3,12,6);rect(c,0xffffdd36,4,4,10,6);rect(c,Color.WHITE,4,10,24,1);rect(c,0xffb88818,4,26,24,1);}
    case "back","up" -> {p.setColor(dark?0xffeeeeee:0xff444444);Path path=new Path();if(type.equals("back")){path.moveTo(2,16);path.lineTo(14,5);path.lineTo(14,12);path.lineTo(29,12);path.lineTo(29,20);path.lineTo(14,20);path.lineTo(14,27);}else{path.moveTo(16,2);path.lineTo(4,15);path.lineTo(11,15);path.lineTo(11,29);path.lineTo(21,29);path.lineTo(21,15);path.lineTo(28,15);}path.close();c.drawPath(path,p);}
    case "search" -> {p.setColor(0xff222222);c.drawCircle(13,12,10,p);p.setColor(0xffb8f8ff);c.drawCircle(13,12,7,p);p.setColor(dark?0xffeeeeee:0xff333333);p.setStrokeWidth(5);c.drawLine(20,20,29,29,p);}
    case "grid" -> {rect(c,0xff000080,3,3,26,26);for(int y=0;y<3;y++)for(int x=0;x<3;x++)rect(c,Color.WHITE,5+x*8,5+y*8,5,5);}
    case "disk" -> {rect(c,0xff333333,2,9,28,17);rect(c,0xffdddddd,3,10,26,14);rect(c,Color.WHITE,4,11,24,2);rect(c,0xff333333,4,20,24,3);rect(c,0xff35bc35,23,20,3,2);}
    case "transfer" -> {rect(c,0xff218c39,4,6,18,6);p.setColor(0xff218c39);Path a=new Path();a.moveTo(21,2);a.lineTo(30,9);a.lineTo(21,16);a.close();c.drawPath(a,p);rect(c,0xff2262a9,10,21,18,6);p.setColor(0xff2262a9);a=new Path();a.moveTo(11,17);a.lineTo(2,24);a.lineTo(11,31);a.close();c.drawPath(a,p);}
    case "star" -> {p.setColor(0xffffcf00);Path a=new Path();for(int j=0;j<10;j++){double angle=-Math.PI/2+j*Math.PI/5;float r=j%2==0?14:6;float x=16+(float)Math.cos(angle)*r,y=16+(float)Math.sin(angle)*r;if(j==0)a.moveTo(x,y);else a.lineTo(x,y);}a.close();c.drawPath(a,p);}
    default -> {rect(c,0xff333333,5,2,23,28);rect(c,0xfff9f9f9,6,3,21,26);if(type.equals("Imagens")){rect(c,0xff4195e3,9,7,15,12);p.setColor(0xffffdf00);c.drawCircle(20,10,3,p);p.setColor(0xff309239);Path a=new Path();a.moveTo(9,19);a.lineTo(15,10);a.lineTo(23,19);a.close();c.drawPath(a,p);}else if(type.equals("Vídeos")){rect(c,0xff222222,9,6,15,15);p.setColor(0xff529fee);Path a=new Path();a.moveTo(13,9);a.lineTo(20,14);a.lineTo(13,18);a.close();c.drawPath(a,p);}else{rect(c,0xff235ca6,9,8,14,2);rect(c,0xff235ca6,9,14,14,2);rect(c,0xff235ca6,9,20,10,2);}}
   }canvas.restore();
  }
  public void setAlpha(int a){}public void setColorFilter(ColorFilter f){}public int getOpacity(){return PixelFormat.TRANSLUCENT;}
 }
}
