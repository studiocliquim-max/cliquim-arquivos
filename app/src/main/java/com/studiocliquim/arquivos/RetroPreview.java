package com.studiocliquim.arquivos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

/** Read-only, sampled image preview. No full-resolution bitmap or file modification. */
final class RetroPreview {
    final MainActivity a;
    final ArrayList<FileEngine.Node> images = new ArrayList<>();
    final FileEngine reader;
    int position;
    volatile int revision;
    volatile boolean disposed;
    Bitmap displayed;
    ImageView image;
    TextView detail;

    RetroPreview(MainActivity activity, FileEngine.Node selected) {
        a = activity;
        reader = new FileEngine(a);
        for (FileEngine.Node n : a.filtered())
            if (!n.directory && a.kind(n).equals("Imagens")) images.add(n);
        position = images.indexOf(selected);
        if (position < 0) { images.clear(); images.add(selected); position = 0; }
    }

    void show() {
        if (disposed) return;
        final int token = ++revision;
        final FileEngine.Node n = images.get(position);
        releaseBitmap();
        a.generation++; // A pending folder listing must not replace the preview.
        a.body.removeAllViews();
        TextView heading = a.text("Prévia de imagem", 17);
        heading.setTextColor(Color.WHITE);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setBackgroundColor(a.blue);
        heading.setPadding(a.dp(8),a.dp(8),a.dp(8),a.dp(8));
        a.body.addView(heading);
        TextView name = a.text(n.name, 16);
        name.setGravity(Gravity.CENTER);
        name.setMaxLines(2);
        name.setPadding(a.dp(8),a.dp(8),a.dp(8),a.dp(8));
        a.body.addView(name);
        image = new ImageView(a);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setContentDescription("Prévia de " + n.name);
        image.setBackground(a.bevel(true));
        image.setPadding(a.dp(3),a.dp(3),a.dp(3),a.dp(3));
        a.body.addView(image,new LinearLayout.LayoutParams(-1,0,1));
        detail = a.text("Carregando prévia…", 13);
        detail.setGravity(Gravity.CENTER);
        detail.setPadding(a.dp(6),a.dp(8),a.dp(6),a.dp(8));
        a.body.addView(detail);
        LinearLayout navigation = new LinearLayout(a);
        Button previous = a.iconButton("back","Anterior",()->{position--;show();});
        Button next = a.iconButton("move","Próxima",()->{position++;show();});
        Button properties = a.iconButton("doc","Propriedades",()->new RetroPages(a).properties(n));
        a.enabled(previous,position > 0);
        a.enabled(next,position + 1 < images.size());
        for(Button button:new Button[]{previous,next,properties})
            navigation.addView(button,new LinearLayout.LayoutParams(0,-2,1));
        a.body.addView(navigation);
        a.footer.removeAllViews();
        LinearLayout actions = new LinearLayout(a);
        for(Button button:new Button[]{a.iconButton("folder","Abrir com",()->a.open(n,false)),
                a.iconButton("transfer","Compartilhar",()->a.open(n,true)),
                a.iconButton("back","Fechar",this::close)})
            actions.addView(button,new LinearLayout.LayoutParams(0,-2,1));
        a.footer.addView(actions);
        for(View control:new View[]{a.fileButton,a.editButton,a.viewButton,a.favoritesButton,a.upButton,a.findButton})
            a.enabled(control,false);
        a.search.setVisibility(View.GONE);
        a.status.setText((position+1)+" de "+images.size()+" imagens • somente leitura");
        MainActivity.IO.execute(()->decode(n,token));
    }

    void decode(FileEngine.Node n,int token) {
        Bitmap bitmap = null;
        try {
            if(disposed || token!=revision) return;
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds=true;
            try(InputStream in=reader.input(n)){BitmapFactory.decodeStream(in,null,bounds);}
            int sample=ImageSizing.sample(bounds.outWidth,bounds.outHeight);
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize=sample;
            options.inPreferredConfig=Bitmap.Config.ARGB_8888;
            if(disposed || token!=revision) return;
            try(InputStream in=reader.input(n)){bitmap=BitmapFactory.decodeStream(in,null,options);}
            if(bitmap==null)throw new IOException("Este formato não oferece prévia. Use Abrir com.");
            int orientation=ExifInterface.ORIENTATION_NORMAL;
            try(InputStream in=reader.input(n)){
                orientation=new ExifInterface(in).getAttributeInt(ExifInterface.TAG_ORIENTATION,orientation);
            }catch(IOException ignored){/* Image has no readable EXIF; no metadata is invented. */}
            Matrix transform=new Matrix();
            switch(orientation){
                case 2: transform.setScale(-1,1);break;
                case 3: transform.setRotate(180);break;
                case 4: transform.setScale(1,-1);break;
                case 5: transform.setRotate(90);transform.postScale(-1,1);break;
                case 6: transform.setRotate(90);break;
                case 7: transform.setRotate(-90);transform.postScale(-1,1);break;
                case 8: transform.setRotate(-90);break;
            }
            if(!transform.isIdentity()){
                Bitmap rotated=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),transform,false);
                if(rotated!=bitmap){bitmap.recycle();bitmap=rotated;}
            }
            final Bitmap ready=bitmap;
            final String metadata=MainActivity.size(n.size)+" • "+bounds.outWidth+" × "+bounds.outHeight;
            a.runOnUiThread(()->{
                if(disposed||token!=revision||a.isDestroyed()||a.isFinishing()){ready.recycle();return;}
                displayed=ready;image.setImageBitmap(ready);detail.setText(metadata);
            });
            bitmap=null; // Ownership handed to the UI callback.
        }catch(IOException|RuntimeException|OutOfMemoryError e){
            if(bitmap!=null)bitmap.recycle();
            a.runOnUiThread(()->{
                if(!disposed&&token==revision&&!a.isDestroyed())
                    detail.setText("Não foi possível gerar a prévia. O arquivo não foi alterado. Use Abrir com.");
            });
        }
    }

    void releaseBitmap(){if(image!=null)image.setImageDrawable(null);if(displayed!=null){displayed.recycle();displayed=null;}}
    void dispose(){disposed=true;revision++;reader.cancelled.set(true);releaseBitmap();if(a.preview==this)a.preview=null;}
    void close(){dispose();a.build();a.load();}
}
