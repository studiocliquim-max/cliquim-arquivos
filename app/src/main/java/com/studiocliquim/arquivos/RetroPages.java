package com.studiocliquim.arquivos;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.text.DateFormat;
import java.io.*;

/** Explorer pages containing real metadata and persisted settings. */
final class RetroPages {
    final MainActivity a;
    RetroPages(MainActivity a){this.a=a;}
    void properties(FileEngine.Node original){
        RetroScreen page=new RetroScreen(a,"Propriedades de "+original.name,original.directory?"folder":a.kind(original));
        LinearLayout body=page.scroll();page.section(body,"Geral");
        LinearLayout identity=new LinearLayout(a);identity.setGravity(Gravity.CENTER_VERTICAL);identity.setPadding(a.dp(8),a.dp(14),a.dp(8),a.dp(14));
        identity.addView(a.symbol(original.directory?"folder":a.kind(original),40),new LinearLayout.LayoutParams(a.dp(40),a.dp(40)));
        TextView filename=a.text(original.name,16);filename.setTextIsSelectable(true);filename.setBackground(a.bevel(true));filename.setPadding(a.dp(8),a.dp(10),a.dp(8),a.dp(10));LinearLayout.LayoutParams named=new LinearLayout.LayoutParams(0,-2,1);named.leftMargin=a.dp(10);identity.addView(filename,named);body.addView(identity);
        TextView type=page.value(body,"Tipo","Consultando…"),size=page.value(body,"Tamanho","Consultando…"),dimensions=page.value(body,"Dimensões","—"),path=page.value(body,"Local","Consultando…"),date=page.value(body,"Modificado","Consultando…"),access=page.value(body,"Acesso","Consultando…");
        Button folder=a.iconButton("folder","Abrir pasta",()->{});a.enabled(folder,original.file!=null&&original.file.getParentFile()!=null);
        folder.setOnClickListener(v->RetroScreen.READ.execute(()->{try{FileEngine.Node parent=new FileEngine(a).local(original.file.getParentFile());page.post(()->a.navigate(parent));}catch(Exception e){page.post(()->a.error(e));}}));
        page.footer(folder,a.iconButton("star","Favoritar",()->a.addFavorite(original)),a.iconButton("back","Fechar",page::close));
        RetroScreen.READ.execute(()->{
            try{FileEngine engine=new FileEngine(a);FileEngine.Node n=engine.refresh(original);String pixels="Não se aplica";
                if(!n.directory&&a.kind(n).equals("Imagens")){BitmapFactory.Options b=new BitmapFactory.Options();b.inJustDecodeBounds=true;try(InputStream in=engine.input(n)){BitmapFactory.decodeStream(in,null,b);}pixels=b.outWidth>0&&b.outHeight>0?b.outWidth+" × "+b.outHeight+" px":"Não informado pelo formato";}
                final String dims=pixels;page.post(()->{type.setText(n.directory?"Pasta":n.mime);size.setText(n.directory?"Use Armazenamento para calcular os arquivos acessíveis desta pasta.":MainActivity.size(n.size));dimensions.setText(dims);path.setText(n.file!=null?n.file.getAbsolutePath():n.uri.toString());date.setText(n.modified>0?DateFormat.getDateTimeInstance().format(new Date(n.modified)):"Não informado pelo provedor");access.setText("Gravar: "+(n.canWrite?"sim":"não")+" • renomear: "+(n.canRename?"sim":"não")+" • excluir: "+(n.canDelete?"sim":"não"));});
            }catch(Exception e){page.post(()->{type.setText("Local indisponível");size.setText("Reconecte o volume ou autorize novamente a pasta.");path.setText(original.file!=null?original.file.getAbsolutePath():original.uri.toString());a.enabled(folder,false);});}
        });
    }
    CheckBox check(LinearLayout parent,String title,boolean checked){CheckBox v=new CheckBox(a);v.setText(title);v.setTextColor(a.fg);v.setChecked(checked);v.setMinHeight(a.dp(48));parent.addView(v);return v;}
    void preferences(){
        RetroScreen page=new RetroScreen(a,"Preferências","computer");LinearLayout body=page.scroll();
        page.section(body,"Aparência");CheckBox dark=check(body,"Tema clássico escuro",a.dark);page.text(body,"O tamanho do texto acompanha a preferência de fonte do Android.");
        page.section(body,"Navegação");CheckBox folders=check(body,"Pastas primeiro",a.foldersFirst),hidden=check(body,"Mostrar arquivos ocultos",a.hidden),grid=check(body,"Exibir como ícones",a.gridMode);
        page.section(body,"Privacidade");page.text(body,"Somente neste aparelho. Sem conta, internet, anúncios, telemetria ou envio de arquivos. O acesso depende das permissões concedidas no Android.");
        page.entry(body,"doc","Limpar histórico de resultados","Não exclui arquivos nem favoritos",()->{if(MainActivity.busy){a.toast("Aguarde o término da operação.");return;}a.prefs.edit().remove("result").remove("transfer_ledger").apply();TransferLedger.clear();a.toast("Histórico local de resultados limpo.");});
        page.footer(a.iconButton("back","Cancelar",page::close),a.iconButton("check","Aplicar",()->{a.dark=dark.isChecked();a.hidden=hidden.isChecked();a.gridMode=grid.isChecked();a.foldersFirst=folders.isChecked();a.prefs.edit().putBoolean("dark",a.dark).putBoolean("hidden",a.hidden).putBoolean("grid",a.gridMode).putBoolean("foldersFirst",a.foldersFirst).apply();page.close();}));
    }
    void favorites(){
        RetroScreen page=new RetroScreen(a,"Favoritos","star");
        Set<String> saved=new TreeSet<>(a.prefs.getStringSet("favorites",new HashSet<>()));
        ArrayList<String> ids=new ArrayList<>(saved);ArrayList<FileEngine.Node> nodes=new ArrayList<>();final int[] selected={-1};
        Button open=a.iconButton("folder","Abrir",()->{int index=selected[0];if(index<0||index>=nodes.size())return;FileEngine.Node n=nodes.get(index);if(n==null)return;page.dispose();a.build();if(n.directory)a.navigate(n);else if(a.kind(n).equals("Imagens"))a.showPreview(n);else{a.load();a.open(n,false);}});
        Button remove=a.iconButton("delete","Remover favorito",()->{int index=selected[0];if(index<0||index>=ids.size())return;String id=ids.get(index);Set<String> next=new HashSet<>(a.prefs.getStringSet("favorites",new HashSet<>()));next.remove(id);a.prefs.edit().putStringSet("favorites",next).remove("favorite_name_"+id).apply();favorites();});
        a.enabled(open,false);a.enabled(remove,false);LinearLayout actions=new LinearLayout(a);actions.addView(open,new LinearLayout.LayoutParams(0,-2,1));actions.addView(remove,new LinearLayout.LayoutParams(0,-2,1));page.content.addView(actions);
        TextView state=a.text(saved.isEmpty()?"Nenhum favorito. Adicione a pasta atual ou arquivos selecionados.":"Carregando atalhos…",14);state.setPadding(a.dp(5),a.dp(8),a.dp(5),a.dp(8));page.content.addView(state);
        ListView list=new ListView(a);list.setBackground(a.bevel(true));page.content.addView(list,new LinearLayout.LayoutParams(-1,0,1));page.text(page.content,"Remover um favorito não exclui o arquivo ou a pasta.");
        Button add=a.iconButton("star","Adicionar favorito",()->{if(!a.selected.isEmpty()){for(FileEngine.Node n:a.selected.values())a.addFavorite(n);favorites();}else if(a.current!=null){a.addFavorite(a.current);favorites();}else a.toast("Abra uma pasta ou selecione arquivos para adicionar.");});a.enabled(add,a.current!=null||!a.selected.isEmpty());page.footer(add,a.iconButton("back","Fechar",page::close));
        if(saved.isEmpty())return;
        RetroScreen.READ.execute(()->{
            ArrayList<FileEngine.Node> resolved=new ArrayList<>();for(String id:ids){if(!page.alive())return;try{resolved.add(a.from(id));}catch(Exception e){resolved.add(null);}}
            page.post(()->{nodes.addAll(resolved);state.setText(ids.size()+" favorito(s) • selecione um item");BaseAdapter adapter=new BaseAdapter(){public int getCount(){return ids.size();}public Object getItem(int p){return ids.get(p);}public long getItemId(int p){return p;}public View getView(int p,View old,ViewGroup parent){
                String id=ids.get(p);FileEngine.Node n=nodes.get(p);String fallback=a.prefs.getString("favorite_name_"+id,Uri.decode(Uri.parse(id).getLastPathSegment()));String name=n==null?fallback:n.name;String location=n==null?"Indisponível • reconecte ou autorize o local":n.file!=null?a.displayPath(n.file):"Local autorizado pelo Android";
                LinearLayout row=new LinearLayout(a);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(a.dp(8),a.dp(10),a.dp(8),a.dp(10));row.setMinimumHeight(a.dp(64));boolean chosen=selected[0]==p;row.setBackground(chosen?new RetroUi.Frame(a.blue,a.dark,true,true):new android.graphics.drawable.ColorDrawable(a.panel));row.addView(a.symbol(n==null?"doc":n.directory?"folder":a.kind(n),32),new LinearLayout.LayoutParams(a.dp(32),a.dp(32)));TextView label=a.text(name+"\n"+location,14);label.setTextColor(chosen?android.graphics.Color.WHITE:a.fg);label.setPadding(a.dp(10),0,0,0);row.addView(label,new LinearLayout.LayoutParams(0,-2,1));row.setSelected(chosen);row.setContentDescription(name+". "+location+(chosen?". Selecionado":""));return row;
            }};list.setAdapter(adapter);list.setOnItemClickListener((parent,v,index,id)->{selected[0]=index;a.enabled(open,nodes.get(index)!=null);a.enabled(remove,true);adapter.notifyDataSetChanged();});});
        });
    }
    void help(){
        RetroScreen page=new RetroScreen(a,"Ajuda e sobre","help");LinearLayout body=page.scroll();body.addView(a.symbol("folder",64),new LinearLayout.LayoutParams(a.dp(64),a.dp(64)));page.section(body,"Cliquim Arquivos 0.4.0");page.text(body,"Um aplicativo do Estúdio Cliquim. Versão em desenvolvimento, aguardando testes centralizados.");
        String[] titles={"Como acessar meus arquivos","Navegar, buscar e selecionar","Copiar e mover","Exclusão permanente","Cartão SD e pendrive","Privacidade e licenças"};
        String[] texts={"Escolha uma pasta no seletor nativo do Android ou autorize armazenamento compartilhado nas configurações. Pastas privadas de outros apps continuam protegidas.","Toque abre; toque longo seleciona. Buscar permite nome, tipo, modificação e tamanho em uma pasta autorizada, com opção de subpastas. A leitura limita tempo, profundidade e quantidade; cobertura parcial é indicada. Provedores sem data/tamanho não correspondem a esses filtros.","Conflitos permitem manter ambos ou pular arquivos que já tenham o mesmo nome. Não substituímos conteúdo existente. Cópias verificam conteúdo. Mover só está habilitado para arquivos locais; pastas e SAF continuam em Copiar. Mantenha o app aberto; não há fila persistente com retomada.","Não há lixeira nem desfazer. A confirmação lista os itens e o local. Cancelar uma exclusão não recupera itens já excluídos. Testes devem usar somente fixtures próprias.","Conecte o dispositivo e escolha a pasta pelo seletor Android. Suporte depende do aparelho e do provedor. Após desconexão, confira o histórico e os destinos parciais antes de repetir. Nenhum original é excluído automaticamente para liberar espaço.","Sem permissão INTERNET, anúncios ou telemetria. Preferências e últimos resultados ficam no aparelho. Interfaces e ícones desenhados no código; nenhum ativo de tela, fonte ou som da Microsoft está incluído. Não é produto Microsoft. Código e dependências devem passar por revisão de licenças antes da publicação comercial."};
        for(int i=0;i<titles.length;i++){final int index=i;page.entry(body,"help",titles[i],"",()->{RetroScreen topic=new RetroScreen(a,titles[index],"help");topic.text(topic.scroll(),texts[index]);topic.footer(a.iconButton("back","Voltar à ajuda",this::help),a.iconButton("back","Fechar",topic::close));});}
    }
}
