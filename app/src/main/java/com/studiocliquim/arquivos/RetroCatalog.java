package com.studiocliquim.arquivos;

import android.os.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Search and storage pages are scoped to an actual authorized root, never a fake whole-device index. */
final class RetroCatalog {
    final MainActivity a;
    RetroScreen page;
    AtomicBoolean cancelled=new AtomicBoolean();
    FileEngine.Node root;
    static final String[] TYPES={"Todos","Imagens","Vídeos","Áudios","Documentos","Outros"};
    RetroCatalog(MainActivity a){this.a=a;}
    static ReadOnlyScan<FileEngine.Node> scanner(FileEngine engine){return new ReadOnlyScan<>(new ReadOnlyScan.Source<>(){
        public String id(FileEngine.Node n){return n.id;}
        public boolean directory(FileEngine.Node n){return n.directory;}
        public boolean hidden(FileEngine.Node n){return n.name.startsWith(".");}
        public List<FileEngine.Node> children(FileEngine.Node n)throws IOException{
            try{return engine.list(n);}catch(RuntimeException e){throw new IOException("Não foi possível ler "+n.name,e);}
        }
    },50000,64,60000);}
    static String coverage(ReadOnlyScan.Result r){return (r.cancelled?"Cancelado • resultados parciais":r.truncated?"Limite de leitura atingido • resultados parciais":r.unavailable>0?"Concluído com locais indisponíveis":"Leitura concluída")+" • "+r.visited+" itens lidos"+(r.unavailable>0?" • "+r.unavailable+" pasta(s) sem acesso":"");}
    Spinner spinner(String[] labels){Spinner s=new Spinner(a);ArrayAdapter<String> adapter=new ArrayAdapter<>(a,android.R.layout.simple_spinner_item,labels){
 public View getView(int position,View old,ViewGroup parent){View v=super.getView(position,old,parent);if(v instanceof TextView){((TextView)v).setTextColor(a.fg);((TextView)v).setTextSize(14);}v.setBackgroundColor(a.bg);return v;}
 public View getDropDownView(int position,View old,ViewGroup parent){View v=super.getDropDownView(position,old,parent);if(v instanceof TextView){((TextView)v).setTextColor(a.fg);((TextView)v).setTextSize(14);}v.setBackgroundColor(a.bg);v.setMinimumHeight(a.dp(48));return v;}
 };adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);s.setAdapter(adapter);s.setMinimumHeight(a.dp(48));return s;}
    void label(LinearLayout parent,String title,View input){TextView t=a.text(title,13);t.setPadding(a.dp(3),a.dp(5),a.dp(3),0);parent.addView(t);input.setContentDescription(title);parent.addView(input,new LinearLayout.LayoutParams(-1,-2));}
    String path(FileEngine.Node n){return n.file!=null?a.displayPath(n.file):n.name+" • pasta autorizada";}
    void selectRoot(java.util.function.Consumer<FileEngine.Node> callback){
        LinkedHashMap<String,String> choices=new LinkedHashMap<>();
        if(a.current!=null)choices.put(a.locationId(a.current),path(a.current));
        if(Build.VERSION.SDK_INT>=30&&Environment.isExternalStorageManager())choices.put(Uri.fromFile(Environment.getExternalStorageDirectory()).toString(),"Armazenamento compartilhado");
        for(String id:a.prefs.getStringSet("roots",new HashSet<>()))choices.putIfAbsent(id,"Pasta autorizada: "+Uri.decode(Uri.parse(id).getLastPathSegment()));
        ArrayList<String> ids=new ArrayList<>(choices.keySet()),names=new ArrayList<>(choices.values());names.add("Autorizar outra pasta no Android…");
        new RetroDialog(a).setTitle("Local da leitura").setItems(names.toArray(new String[0]),(d,w)->{
            if(w==ids.size()){a.chooseTree();return;}
            RetroScreen.READ.execute(()->{try{FileEngine.Node n=a.from(ids.get(w));page.post(()->callback.accept(n));}catch(Exception e){page.post(()->a.error(e));}});
        }).setNegativeButton("Cancelar",null).show();
    }
    void startPage(String title){page=new RetroScreen(a,title,"search");page.onDispose=()->cancelled.set(true);}
    void search(FileEngine.Node location,String initialType,boolean recursiveDefault,long initialMinBytes){
        root=location;startPage("Localizar arquivos");
        LinearLayout form=new LinearLayout(a);form.setOrientation(LinearLayout.VERTICAL);form.setBackground(a.bevel(false));form.setPadding(a.dp(6),a.dp(6),a.dp(6),a.dp(6));
        ScrollView filters=new ScrollView(a);filters.addView(form);page.content.addView(filters,new LinearLayout.LayoutParams(-1,a.dp(180)));
        EditText query=new EditText(a);query.setTextColor(a.fg);query.setSingleLine(true);query.setHint("Parte do nome");query.setHintTextColor(a.dark?0xffbbbbbb:0xff555555);query.setBackground(a.bevel(true));query.setPadding(a.dp(6),a.dp(10),a.dp(6),a.dp(10));label(form,"Nome",query);
        Button locationButton=a.button(path(root),()->{});locationButton.setOnClickListener(v->selectRoot(n->{root=n;locationButton.setText(path(n));}));label(form,"Local",locationButton);
        Spinner type=spinner(TYPES);type.setSelection(Math.max(0,Arrays.asList(TYPES).indexOf(initialType)));label(form,"Tipo",type);
        Spinner date=spinner(new String[]{"Qualquer data","Últimas 24 horas","Últimos 7 dias","Últimos 30 dias"});label(form,"Modificado",date);
        Spinner size=spinner(new String[]{"Qualquer tamanho","A partir de 10 MB","A partir de 100 MB","A partir de 1 GB"});size.setSelection(initialMinBytes>=1073741824L?3:initialMinBytes>=104857600L?2:initialMinBytes>0?1:0);label(form,"Tamanho",size);
        CheckBox recursive=new CheckBox(a);recursive.setText("Incluir subpastas deste local");recursive.setTextColor(a.fg);recursive.setChecked(recursiveDefault);form.addView(recursive);form.addView(a.button("Limpar filtros",()->{query.setText("");type.setSelection(0);date.setSelection(0);size.setSelection(0);recursive.setChecked(false);}));
        TextView summary=a.text("Escolha os filtros e toque em Buscar.",13);summary.setPadding(a.dp(4),a.dp(8),a.dp(4),a.dp(8));page.content.addView(summary);
        ListView results=new ListView(a);page.content.addView(results,new LinearLayout.LayoutParams(-1,0,1));
        Button search=a.iconButton("search","Buscar",()->{});
        Button clear=a.iconButton("doc","Filtros",()->filters.setVisibility(filters.getVisibility()==View.GONE?View.VISIBLE:View.GONE));
        Button cancel=a.iconButton("back","Cancelar leitura",()->cancelled.set(true));a.enabled(cancel,false);
        page.footer(search,clear,cancel,a.iconButton("back","Fechar",page::close));
        search.setOnClickListener(v->{
            cancelled.set(true);AtomicBoolean stop=new AtomicBoolean();cancelled=stop;
            FileEngine.Node selectedRoot=root;String q=query.getText().toString().trim();String selectedType=TYPES[type.getSelectedItemPosition()];
            long[] sizes={0,10485760L,104857600L,1073741824L};long min=sizes[size.getSelectedItemPosition()];long[] days={0,1,7,30};long day=days[date.getSelectedItemPosition()];long since=day==0?0:System.currentTimeMillis()-day*86400000L;
            boolean recurse=recursive.isChecked(),hidden=a.hidden;
            results.setAdapter(null);summary.setText("Buscando em "+path(selectedRoot)+"…");a.enabled(cancel,true);filters.setVisibility(View.GONE);
            ((android.view.inputmethod.InputMethodManager)a.getSystemService(android.content.Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(query.getWindowToken(),0);
            RetroScreen.READ.execute(()->{
                ArrayList<FileEngine.Node> found=new ArrayList<>(),parents=new ArrayList<>();FileEngine engine=new FileEngine(a);
                ReadOnlyScan.Result report=scanner(engine).run(selectedRoot,recurse,hidden,stop,(n,parent)->{
                    if(ScanRules.matches(n.name,n.mime,n.directory,n.size,n.modified,q,selectedType,min,since)){found.add(n);parents.add(parent);}
                    return found.size()<1000;
                });
                page.post(()->{if(cancelled!=stop)return;a.enabled(cancel,false);summary.setText(found.size()+" resultado(s) • "+coverage(report)+(found.isEmpty()?"\nNenhum arquivo encontrado nos locais lidos.":""));a.status.setText("Busca em "+selectedRoot.name);showResults(results,found,parents);});
            });
        });
        if(!initialType.equals("Todos")||initialMinBytes>0)search.performClick();
    }
    void showResults(ListView list,List<FileEngine.Node> nodes,List<FileEngine.Node> parents){
        list.setAdapter(new BaseAdapter(){public int getCount(){return nodes.size();}public Object getItem(int p){return nodes.get(p);}public long getItemId(int p){return p;}public View getView(int p,View old,ViewGroup parent){
            FileEngine.Node n=nodes.get(p);LinearLayout row=new LinearLayout(a);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(a.dp(6),a.dp(8),a.dp(6),a.dp(8));row.setMinimumHeight(a.dp(64));row.addView(a.symbol(n.directory?"folder":a.kind(n),28),new LinearLayout.LayoutParams(a.dp(28),a.dp(28)));
            TextView t=a.text(n.name+"\n"+path(parents.get(p))+" • "+(n.directory?"Pasta":MainActivity.size(n.size)),14);t.setPadding(a.dp(8),0,0,0);row.addView(t,new LinearLayout.LayoutParams(0,-2,1));return row;
        }});
        list.setOnItemClickListener((p,v,index,id)->{FileEngine.Node n=nodes.get(index);page.dispose();a.build();if(n.directory)a.navigate(n);else if(a.kind(n).equals("Imagens"))a.showPreview(n);else{a.load();a.open(n,false);}});
        list.setOnItemLongClickListener((p,v,index,id)->{FileEngine.Node n=nodes.get(index);new RetroDialog(a).setTitle(n.name).setItems(new String[]{"Abrir pasta do item","Adicionar aos favoritos","Propriedades"},(d,w)->{if(w==0)a.navigate(parents.get(index));else if(w==1)a.addFavorite(n);else new RetroPages(a).properties(n);}).show();return true;});
    }
    void storage(FileEngine.Node location){
        root=location;page=new RetroScreen(a,"Armazenamento","disk");page.onDispose=()->cancelled.set(true);LinearLayout body=page.scroll();
        page.section(body,"Volume e local analisado");page.value(body,"Local",path(location));TextView capacity=page.value(body,"Capacidade","Consultando…");
        ProgressBar bar=new ProgressBar(a,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(1000);bar.setIndeterminate(true);body.addView(bar,new LinearLayout.LayoutParams(-1,a.dp(20)));
        page.section(body,"Arquivos acessíveis por categoria");LinkedHashMap<String,TextView> values=new LinkedHashMap<>();for(String type:Arrays.copyOfRange(TYPES,1,TYPES.length))values.put(type,page.value(body,type,"Lendo…"));
        TextView state=a.text("Lendo somente o local selecionado e suas subpastas…",14);body.addView(state);page.text(body,"O espaço usado do volume também inclui sistema e apps. A soma por categoria cobre apenas arquivos acessíveis neste local, incluindo ocultos; não é o total de todo o telefone.");
        Button cancel=a.iconButton("back","Cancelar",()->cancelled.set(true));page.footer(a.iconButton("search","Arquivos grandes",()->search(location,"Todos",true,104857600L)),a.iconButton("transfer","Atualizar",()->storage(location)),cancel,a.iconButton("back","Fechar",page::close));
        AtomicBoolean stop=new AtomicBoolean();cancelled=stop;RetroScreen active=page;
        RetroScreen.READ.execute(()->{
            long total=location.file==null?0:location.file.getTotalSpace(),free=location.file==null?0:location.file.getUsableSpace();Map<String,Long> bytes=new LinkedHashMap<>();for(String key:values.keySet())bytes.put(key,0L);int[] unknown={0},files={0};
            ReadOnlyScan.Result report=scanner(new FileEngine(a)).run(location,true,true,stop,(n,p)->{if(!n.directory){files[0]++;if(n.size<0)unknown[0]++;else{String type=ScanRules.category(n.mime);bytes.put(type,ScanRules.add(bytes.get(type),n.size));}}return true;});
            active.post(()->{if(cancelled!=stop)return;bar.setIndeterminate(false);if(total>0){capacity.setText(MainActivity.size(Math.max(0,total-free))+" usados de "+MainActivity.size(total)+" • "+MainActivity.size(free)+" disponíveis");bar.setProgress((int)Math.min(1000,Math.max(0,(total-free)*1000d/total)));}else{capacity.setText("O provedor não informou a capacidade do volume.");bar.setVisibility(View.GONE);}for(String key:values.keySet())values.get(key).setText(MainActivity.size(bytes.get(key)));state.setText(coverage(report)+"\n"+files[0]+" arquivos • "+unknown[0]+" sem tamanho informado.");a.enabled(cancel,false);a.status.setText("Armazenamento • "+(report.complete()?"leitura concluída":"cobertura parcial"));});
        });
    }
}
