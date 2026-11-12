package com.studiocliquim.arquivos;

import android.view.*;
import android.widget.*;
import java.util.*;
import org.json.*;

/** Actual destination navigation and journal; no unimplemented replace/resume actions. */
final class RetroTransfers {
    final MainActivity a;
    RetroTransfers(MainActivity a){this.a=a;}
    void destination(FileEngine.Node dir,ArrayList<FileEngine.Node> sources,boolean move,ArrayDeque<FileEngine.Node> stack){
        RetroScreen page=new RetroScreen(a,move?"Mover para":"Copiar para","folder");
        page.text(page.content,sources.size()+" item(ns) selecionados");page.value(page.content,"Destino",dir.file!=null?a.displayPath(dir.file):dir.name);
        LinearLayout folders=page.scroll();TextView loading=a.text("Lendo pastas…",14);folders.addView(loading);
        Button confirm=a.iconButton("check","Usar esta pasta",()->confirm(dir,sources,move,page));a.enabled(confirm,false);
        Button up=a.iconButton("up","Acima",()->{
            if(!stack.isEmpty()){destination(stack.pop(),sources,move,stack);return;}
            if(dir.file!=null&&dir.file.getParentFile()!=null)RetroScreen.READ.execute(()->{try{FileEngine.Node parent=new FileEngine(a).local(dir.file.getParentFile());page.post(()->destination(parent,sources,move,stack));}catch(Exception e){page.post(()->a.toast("Você está na raiz deste local autorizado."));}});
        });a.enabled(up,!stack.isEmpty()||dir.file!=null);
        page.footer(up,a.iconButton("computer","Locais",()->a.pickRoot(sources,move)),confirm,a.iconButton("back","Cancelar",page::close));
        RetroScreen.READ.execute(()->{try{FileEngine engine=new FileEngine(a);FileEngine.Node refreshed=engine.refresh(dir);ArrayList<FileEngine.Node> children=new ArrayList<>();for(FileEngine.Node n:engine.list(refreshed))if(n.directory&&(!n.name.startsWith(".")||a.hidden))children.add(n);
            page.post(()->{folders.removeAllViews();if(children.isEmpty())page.text(folders,"Nenhuma subpasta neste local.");for(FileEngine.Node child:children)page.entry(folders,"folder",child.name,"",()->{stack.push(dir);destination(child,sources,move,stack);});a.enabled(confirm,refreshed.canCreate&&!MainActivity.busy);if(!refreshed.canCreate)page.text(folders,"Este local permite leitura, mas não criação de arquivos.");});
        }catch(Exception e){page.post(()->{loading.setText("Local indisponível. Reconecte o volume ou escolha outra pasta.");a.enabled(confirm,false);});}});
    }
    void confirm(FileEngine.Node destination,ArrayList<FileEngine.Node> sources,boolean move,RetroScreen page){
        RetroScreen.READ.execute(()->{try{
            List<FileEngine.Node> existing=new FileEngine(a).list(destination);Map<String,FileEngine.Node> names=new HashMap<>();for(FileEngine.Node n:existing)names.put(n.name.toLowerCase(Locale.ROOT),n);
            int conflicts=0;StringBuilder details=new StringBuilder();for(FileEngine.Node source:sources){FileEngine.Node other=names.get(source.name.toLowerCase(Locale.ROOT));if(other!=null){conflicts++;if(conflicts<=5)details.append("\n").append(source.name).append("\nOrigem: ").append(source.directory?"pasta":MainActivity.size(source.size)).append(" • destino: ").append(other.directory?"pasta":MainActivity.size(other.size)).append("\n");}}
            final int count=conflicts;page.post(()->{
                LinearLayout content=new LinearLayout(a);content.setOrientation(LinearLayout.VERTICAL);
                TextView description=a.text(sources.size()+" item(ns) → "+destination.name+"\n"+count+" conflito(s) detectado(s)."+details+"\nA checagem é refeita antes de cada item. Se outro app criar um conflito durante a cópia, um nome único poderá ser usado. Nada será substituído.",14);content.addView(description);
                RadioGroup policy=new RadioGroup(a);RadioButton keep=new RadioButton(a);keep.setId(View.generateViewId());keep.setText("Manter ambos com nomes diferentes");keep.setTextColor(a.fg);policy.addView(keep);RadioButton skip=new RadioButton(a);skip.setId(View.generateViewId());skip.setText("Pular itens com nome já existente");skip.setTextColor(a.fg);policy.addView(skip);policy.check(keep.getId());content.addView(policy);
                TextView unavailable=a.text("Substituir indisponível: não há recuperação segura implementada.",13);content.addView(unavailable);
                new RetroDialog(a).setTitle(count>0?"Conflitos no destino":"Confirmar destino").setView(content).setNegativeButton("Cancelar",null).setPositiveButton(move?"Mover":"Copiar",(d,w)->{boolean skipExisting=policy.getCheckedRadioButtonId()==skip.getId();page.close();a.transfer(sources,destination,move,skipExisting);status();}).show();
            });
        }catch(Exception e){page.post(()->a.error(e));}});
    }
    void status(){
        RetroScreen page=new RetroScreen(a,"Transferências","transfer");LinearLayout body=page.scroll();
        TextView operation=a.text("",16);body.addView(operation);ProgressBar progress=new ProgressBar(a,null,android.R.attr.progressBarStyleHorizontal);progress.setIndeterminate(true);body.addView(progress,new LinearLayout.LayoutParams(-1,a.dp(20)));
        TextView summary=a.text("",14);summary.setPadding(0,a.dp(8),0,a.dp(8));body.addView(summary);LinearLayout rows=new LinearLayout(a);rows.setOrientation(LinearLayout.VERTICAL);body.addView(rows);
        Button cancel=a.iconButton("back","Cancelar operação",()->{if(MainActivity.activeEngine!=null)MainActivity.activeEngine.cancelled.set(true);});
        Button destination=a.iconButton("folder","Abrir destino",()->openLocation(TransferLedger.snapshot(a.prefs).optString("destinationId"),page));
        Button origin=a.iconButton("back","Voltar à origem",()->openLocation(TransferLedger.snapshot(a.prefs).optString("originId"),page));
        page.footer(destination,origin,cancel,a.iconButton("back","Fechar",page::close));
        final String[] previous={""};Runnable refresh=new Runnable(){public void run(){if(!page.alive())return;JSONObject data=TransferLedger.snapshot(a.prefs);String key=data.toString()+MainActivity.busy+MainActivity.progress;
            if(!key.equals(previous[0])){previous[0]=key;rows.removeAllViews();boolean running=data.optBoolean("running")&&MainActivity.busy;
                operation.setText(MainActivity.busy?MainActivity.progress:data.optBoolean("running")?"Operação interrompida • confira origem e destino":"Resultado da transferência");progress.setVisibility(MainActivity.busy?View.VISIBLE:View.GONE);a.enabled(cancel,MainActivity.busy);a.enabled(destination,!data.optString("destinationId").isEmpty());a.enabled(origin,!data.optString("originId").isEmpty());
                if(!data.has("total")){summary.setText(a.prefs.getString("result","Nenhuma transferência registrada."));}
                else{int accounted=data.optInt("done")+data.optInt("skipped")+data.optInt("errors");summary.setText("Último lote de transferência:\n"+data.optInt("done")+" concluído(s) • "+data.optInt("skipped")+" pulado(s) • "+data.optInt("errors")+" com atenção\n"+Math.max(0,data.optInt("total")-accounted)+" sem conclusão registrada\nDestino: "+data.optString("destination")+(data.optBoolean("cancelled")?"\nCancelado. Confira possíveis destinos parciais.":""));
                    JSONArray items=data.optJSONArray("items");if(items!=null)for(int i=0;i<items.length();i++){JSONObject item=items.optJSONObject(i);if(item!=null){page.section(rows,item.optString("name"));page.text(rows,item.optString("state")+"\n"+item.optString("detail"));}}
                    if(data.optInt("omitted")>0)page.text(rows,data.optInt("omitted")+" registros anteriores não exibidos. Os totais incluem todo o lote.");
                }
                page.text(rows,"Progresso por item; bytes totais não medidos. Este histórico não retoma tarefas. Mantenha o app aberto e confira os arquivos após uma interrupção.");
            }a.handler.postDelayed(this,500);
        }};page.onDispose=()->a.handler.removeCallbacks(refresh);refresh.run();
    }
    void openLocation(String id,RetroScreen page){if(id.isEmpty())return;RetroScreen.READ.execute(()->{try{FileEngine.Node n=a.from(id);page.post(()->a.navigate(n));}catch(Exception e){page.post(()->a.error(e));}});}
}
