package com.studiocliquim.arquivos;

import android.os.*;
import android.widget.*;
import java.util.*;

/** Authorized home and first-access entry points remain distinct. */
final class RetroHome {
    final MainActivity a;
    RetroHome(MainActivity a){this.a=a;}
    void populate(LinearLayout body,int generation){
        boolean shared=Build.VERSION.SDK_INT>=30&&Environment.isExternalStorageManager();
        a.homeEntry(body,"disk","Armazenamento compartilhado",shared?"Explorar arquivos autorizados":"Autorizar nas configurações do Android",a::requestAll);
        a.homeEntry(body,"folder","Escolher uma pasta / SD / USB","Acesso limitado à pasta escolhida no Android",a::chooseTree);
        Set<String> roots=a.prefs.getStringSet("roots",new HashSet<>());
        if(!roots.isEmpty())a.homeEntry(body,"computer","Locais autorizados",roots.size()+" autorização(ões) salva(s)",a::locations);
        a.homeEntry(body,"star","Favoritos","Pastas e arquivos salvos neste aparelho",()->new RetroPages(a).favorites());
        a.homeEntry(body,"computer","Preferências","Aparência, navegação e privacidade",()->new RetroPages(a).preferences());
        if(shared){
            TextView heading=a.text("Categorias do armazenamento compartilhado",16);heading.setPadding(0,a.dp(16),0,a.dp(8));body.addView(heading);
            String[] types={"Imagens","Vídeos","Áudios","Documentos"};
            for(int i=0;i<types.length;i+=2){LinearLayout row=new LinearLayout(a);for(int j=i;j<i+2;j++){String type=types[j];row.addView(a.iconButton(type,type,()->withRoot(n->new RetroCatalog(a).search(n,type,true,0))),new LinearLayout.LayoutParams(0,-2,1));}body.addView(row);}
            TextView capacity=a.text("Consultando capacidade do volume…",13);capacity.setPadding(0,a.dp(10),0,a.dp(10));body.addView(capacity);
            a.homeEntry(body,"disk","Analisar armazenamento","Tamanhos reais dos arquivos acessíveis por categoria",()->withRoot(n->new RetroCatalog(a).storage(n)));
            RetroScreen.READ.execute(()->{String text;try{StatFs stat=new StatFs(Environment.getExternalStorageDirectory().getPath());text=MainActivity.size(stat.getAvailableBytes())+" disponíveis de "+MainActivity.size(stat.getTotalBytes());}catch(Exception e){text="Capacidade indisponível neste momento.";}String result=text;a.runOnUiThread(()->{if(a.generation==generation&&a.current==null&&a.page==null&&!a.isDestroyed())capacity.setText(result);});});
        }
        TextView note=a.text("Sem conta, anúncios ou envio de arquivos. Pastas privadas de outros apps continuam protegidas.",14);note.setPadding(0,a.dp(16),0,a.dp(16));body.addView(note);
    }
    void withRoot(java.util.function.Consumer<FileEngine.Node> action){int generation=a.generation;RetroScreen.READ.execute(()->{try{FileEngine.Node root=new FileEngine(a).local(Environment.getExternalStorageDirectory());a.runOnUiThread(()->{if(a.generation==generation&&!a.isDestroyed())action.accept(root);});}catch(Exception e){a.runOnUiThread(()->{if(a.generation==generation&&!a.isDestroyed())a.error(e);});}});}
}
