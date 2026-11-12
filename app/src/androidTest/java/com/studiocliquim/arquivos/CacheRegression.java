package com.studiocliquim.arquivos;
import android.content.Context;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.JSONObject;

final class CacheRegression {
    private static int checks;
    private static void check(boolean ok,String text){if(!ok)throw new AssertionError(text);checks++;}
    static int run(Context c)throws Exception{
        checks=0;ListingCache cache=new ListingCache(c);FileEngine engine=new FileEngine(c);
        File folder=new File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),"Cache98-"+System.nanoTime());check(folder.mkdir(),"cache fixture");
        File text=new File(folder,"Original.txt");Files.write(text.toPath(),"original".getBytes());FileEngine.Node root=engine.local(folder);String key="folder:"+root.id;List<FileEngine.Node> live=engine.list(root);
        cache.put(key,root,live,Collections.emptyList(),null,cache.epoch());ListingCache.Snapshot hit=new ListingCache(c).get(key,root);
        check(hit!=null&&hit.fresh&&hit.nodes.size()==1,"persistent cache hit across cache instances");check(hit.nodes.get(0).id.equals(live.get(0).id),"node identities preserved");
        check(new ListingCache(c).get("another scope",root)==null,"cache keys isolate scope");
        JSONObject json=new JSONObject(new String(Files.readAllBytes(cache.file(key).toPath())));json.put("saved",System.currentTimeMillis()-ListingCache.FRESH_MS-1000);Files.write(cache.file(key).toPath(),json.toString().getBytes());
        hit=cache.get(key,root);check(hit!=null&&!hit.fresh,"expired entry remains usable only while refreshing");
        long oldEpoch=cache.epoch();engine.createDirectory(root,"Nova");check(cache.get(key,root)==null,"mutation invalidates old metadata");
        cache.put(key,root,live,Collections.emptyList(),null,oldEpoch);check(cache.get(key,root)==null,"in-flight stale query cannot repopulate cache after mutation");
        live=engine.list(root);cache.put(key,root,live,Collections.emptyList(),null,cache.epoch());check(cache.get(key,root).nodes.size()==2,"fresh list includes created folder");
        FileEngine.Node old=engine.local(text);engine.rename(old,"Renomeado.txt");check(cache.get(key,root)==null,"rename invalidates");
        cache.put(key,root,engine.list(root),Collections.emptyList(),null,cache.epoch());Files.write(cache.file(key).toPath(),"incomplete json".getBytes());check(cache.get(key,root)==null,"corrupt cache falls back to live query");
        ReadOnlyScan.Result partial=new ReadOnlyScan.Result();partial.visited=20;partial.truncated=true;List<FileEngine.Node> rows=engine.list(root);cache.put("query",root,rows,Arrays.asList(root,root),partial,cache.epoch());hit=cache.get("query",root);check(hit!=null&&hit.report.truncated&&hit.report.visited==20,"partial coverage remains explicitly partial");
        cache.clear();check(cache.get("query",root)==null&&new File(folder,"Renomeado.txt").isFile(),"clearing cache preserves actual files");
        engine.delete(engine.local(folder));return checks;
    }
}
