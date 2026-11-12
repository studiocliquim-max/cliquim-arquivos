package com.studiocliquim.arquivos;

import android.content.*;
import android.net.Uri;
import android.os.*;
import android.util.AtomicFile;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Private, bounded metadata cache for browsing only. File operations always use live FileEngine reads. */
final class ListingCache {
    static final long FRESH_MS=120_000,MAX_AGE_MS=86_400_000,MAX_BYTES=16*1024*1024;
    static final int MAX_ENTRY_BYTES=4*1024*1024,MAX_ENTRIES=32,MAX_ROWS=1000;
    final Context context;final File directory;final SharedPreferences state;
    static final class Snapshot {
        final ArrayList<FileEngine.Node> nodes=new ArrayList<>(),parents=new ArrayList<>();
        final ReadOnlyScan.Result report=new ReadOnlyScan.Result();
        long saved;boolean fresh;
    }
    ListingCache(Context c){context=c.getApplicationContext();directory=new File(context.getCacheDir(),"folder-index-v1");state=context.getSharedPreferences("listing_cache",0);}
    long epoch(){return state.getLong("epoch",0);}
    static void invalidate(Context c){synchronized(ListingCache.class){SharedPreferences p=c.getApplicationContext().getSharedPreferences("listing_cache",0);p.edit().putLong("epoch",p.getLong("epoch",0)+1).commit();}}
    private boolean permitted(FileEngine.Node root){
        if(root.file!=null){if(Build.VERSION.SDK_INT>=30&&!Environment.isExternalStorageManager())return false;return root.file.isDirectory()&&root.file.canRead();}
        return root.uri!=null&&context.checkUriPermission(root.uri,android.os.Process.myPid(),android.os.Process.myUid(),Intent.FLAG_GRANT_READ_URI_PERMISSION)==android.content.pm.PackageManager.PERMISSION_GRANTED;
    }
    private String permissions(){ArrayList<String> values=new ArrayList<>();for(UriPermission p:context.getContentResolver().getPersistedUriPermissions())values.add(p.getUri()+":"+p.isReadPermission()+":"+p.isWritePermission());Collections.sort(values);return values.toString();}
    File file(String key){try{byte[] bytes=java.security.MessageDigest.getInstance("SHA-256").digest(key.getBytes(StandardCharsets.UTF_8));StringBuilder id=new StringBuilder();for(byte b:bytes)id.append(String.format(Locale.ROOT,"%02x",b&255));return new File(directory,id+".json");}catch(Exception e){throw new IllegalStateException(e);}}
    Snapshot get(String key,FileEngine.Node root){
        File file=file(key);if(!permitted(root)){file.delete();return null;}
        if(!file.isFile()||file.length()>MAX_ENTRY_BYTES)return null;
        try(InputStream in=new FileInputStream(file)){
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] block=new byte[8192];int count;while((count=in.read(block))!=-1){if(bytes.size()+count>MAX_ENTRY_BYTES)return null;bytes.write(block,0,count);}
            JSONObject json=new JSONObject(bytes.toString("UTF-8"));long age=System.currentTimeMillis()-json.getLong("saved");
            if(json.getInt("version")!=1||!key.equals(json.getString("key"))||json.getLong("epoch")!=epoch()||age<0||age>MAX_AGE_MS||!permissions().equals(json.getString("permissions")))return null;
            if(root.file!=null&&root.file.lastModified()!=json.getLong("rootModified"))return null;
            Snapshot result=new Snapshot();result.saved=json.getLong("saved");result.fresh=age<FRESH_MS;
            JSONArray rows=json.getJSONArray("nodes"),parents=json.getJSONArray("parents");if(rows.length()>MAX_ROWS||parents.length()!=0&&parents.length()!=rows.length())return null;
            for(int n=0;n<rows.length();n++)result.nodes.add(decode(rows.getJSONObject(n)));
            for(int n=0;n<parents.length();n++)result.parents.add(decode(parents.getJSONObject(n)));
            result.report.visited=json.optInt("visited");result.report.unavailable=json.optInt("unavailable");result.report.truncated=json.optBoolean("truncated");
            return result;
        }catch(Exception corrupt){file.delete();return null;}
    }
    void put(String key,FileEngine.Node root,List<FileEngine.Node> nodes,List<FileEngine.Node> parents,ReadOnlyScan.Result report,long expectedEpoch){
        if(nodes.size()>MAX_ROWS||report!=null&&report.cancelled||!permitted(root))return;
        try{
            JSONObject json=new JSONObject().put("version",1).put("key",key).put("epoch",expectedEpoch).put("saved",System.currentTimeMillis()).put("rootModified",root.file==null?0:root.file.lastModified()).put("permissions",permissions());
            JSONArray items=new JSONArray(),ancestor=new JSONArray();for(FileEngine.Node n:nodes)items.put(encode(n));for(FileEngine.Node n:parents)ancestor.put(encode(n));json.put("nodes",items).put("parents",ancestor);
            if(report!=null)json.put("visited",report.visited).put("unavailable",report.unavailable).put("truncated",report.truncated);
            byte[] data=json.toString().getBytes(StandardCharsets.UTF_8);if(data.length>MAX_ENTRY_BYTES)return;
            synchronized(ListingCache.class){if(expectedEpoch!=epoch())return;if(!directory.isDirectory()&&!directory.mkdirs())return;AtomicFile target=new AtomicFile(file(key));FileOutputStream out=null;try{out=target.startWrite();out.write(data);target.finishWrite(out);}catch(IOException e){if(out!=null)target.failWrite(out);return;}trim();}
        }catch(Exception ignored){/* A cache failure must never block file access. */}
    }
    private void trim(){File[] entries=directory.listFiles((dir,name)->name.endsWith(".json"));if(entries==null)return;Arrays.sort(entries,Comparator.comparingLong(File::lastModified).reversed());long bytes=0;for(int n=0;n<entries.length;n++){bytes+=entries[n].length();if(n>=MAX_ENTRIES||bytes>MAX_BYTES)entries[n].delete();}}
    void clear(){invalidate(context);File[] files=directory.listFiles();if(files!=null)for(File f:files)f.delete();}
    static JSONObject encode(FileEngine.Node n)throws JSONException {
        long flags=(n.canWrite?android.provider.DocumentsContract.Document.FLAG_SUPPORTS_WRITE:0)|(n.canDelete?android.provider.DocumentsContract.Document.FLAG_SUPPORTS_DELETE:0)|(n.canCreate?android.provider.DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE:0);
        return new JSONObject().put("id",n.id).put("name",n.name).put("mime",n.mime).put("size",n.size).put("modified",n.modified).put("flags",flags).put("file",n.file==null?"":n.file.getPath()).put("uri",n.uri==null?"":n.uri.toString());
    }
    static FileEngine.Node decode(JSONObject j)throws JSONException {
        String f=j.getString("file"),u=j.getString("uri");return FileEngine.Node.cached(f.isEmpty()?null:new File(f),u.isEmpty()?null:Uri.parse(u),j.getString("id"),j.getString("name"),j.getString("mime"),j.getLong("size"),j.getLong("modified"),j.getLong("flags"));
    }
}
