package com.studiocliquim.arquivos;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.webkit.MimeTypeMap;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Offline file operations. Run on a worker thread. No overwrite path exists. */
public final class FileEngine {
    private final ContentResolver resolver;
    private final Context context;
    public final AtomicBoolean cancelled = new AtomicBoolean(false);
    private static final String DIR = DocumentsContract.Document.MIME_TYPE_DIR;
    private static final String[] COLS = { DocumentsContract.Document.COLUMN_DOCUMENT_ID,
        DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE,
        DocumentsContract.Document.COLUMN_SIZE, DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        DocumentsContract.Document.COLUMN_FLAGS };
    public static final class Node {
        public final String id, name, mime;
        public final boolean directory, canWrite, canDelete, canRename, canCreate;
        public final long size, modified;
        public final File file;
        public final Uri uri;
        private Node(File f, Uri u, String id, String name, String mime, long size, long modified, long flags) {this(f,u,id,name,mime,size,modified,flags,true);}
        static Node cached(File f,Uri u,String id,String name,String mime,long size,long modified,long flags){return new Node(f,u,id,name,mime,size,modified,flags,false);}
        private Node(File f, Uri u, String id, String name, String mime, long size, long modified, long flags,boolean live) {
            this.file=f; this.uri=u; this.id=id; this.name=name; this.mime=mime;
            this.directory=DIR.equals(mime); this.size=size; this.modified=modified;
            canWrite=f!=null && live ? f.canWrite() : (flags & DocumentsContract.Document.FLAG_SUPPORTS_WRITE)!=0;
            canDelete=f!=null && live ? f.getParentFile()!=null && f.getParentFile().canWrite() : (flags & DocumentsContract.Document.FLAG_SUPPORTS_DELETE)!=0;
            canRename=f!=null && canDelete; // SAF rename is intentionally unavailable without safe conflict semantics.
            canCreate=f!=null ? directory && canWrite : (flags & DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE)!=0;
        }
        @Override public boolean equals(Object o) { return o instanceof Node && id.equals(((Node)o).id); }
        @Override public int hashCode() { return id.hashCode(); }
    }
    public FileEngine(Context context) { this.context=context.getApplicationContext();resolver=this.context.getContentResolver(); }
    public void resetCancellation() { cancelled.set(false); }
    private void check() throws IOException { if(cancelled.get() || Thread.currentThread().isInterrupted()) throw new InterruptedIOException("Operação cancelada; confira os itens concluídos."); }
    private static IOException failure(Exception e) { return e instanceof IOException ? (IOException)e : new IOException(e.getMessage()==null?"Não foi possível acessar este local.":e.getMessage(),e); }
    /** Local access is deliberately confined to shared Android storage. */
    private static File checked(File file) throws IOException {
        File abs=file.getAbsoluteFile(); String p=abs.getPath();
        if (!(p.startsWith("/storage/") || p.equals("/storage") || p.equals("/sdcard") || p.startsWith("/sdcard/")))
            throw new IOException("Somente armazenamento compartilhado é permitido.");
        if(!p.equals("/sdcard") && Files.isSymbolicLink(abs.toPath())) throw new IOException("Links simbólicos não são suportados.");
        File canonical=abs.getCanonicalFile(); String c=canonical.getPath();
        if (!(c.startsWith("/storage/") || c.equals("/storage") || c.startsWith("/mnt/media_rw/")))
            throw new IOException("Local fora do armazenamento compartilhado.");
        String lower=c.toLowerCase(Locale.ROOT);
        if(lower.contains("/android/data") || lower.contains("/android/obb")) throw new IOException("Dados privados de aplicativos não estão disponíveis.");
        // Android uses symlinks for storage roots. Reject links below the canonical root only.
        if(Files.isSymbolicLink(canonical.toPath())) throw new IOException("Links simbólicos não são suportados.");
        return canonical;
    }
    public Node local(File file) throws IOException {
        File f=checked(file); if(!f.exists()) throw new FileNotFoundException("Arquivo ou volume indisponível.");
        return new Node(f,null,"file:"+f.getPath(),f.getName().isEmpty()?f.getPath():f.getName(),f.isDirectory()?DIR:mime(f.getName()),f.isDirectory()?-1:f.length(),f.lastModified(),0);
    }
    public Node tree(Uri uri) throws IOException {
        try { return document(DocumentsContract.buildDocumentUriUsingTree(uri,DocumentsContract.getTreeDocumentId(uri))); }
        catch(Exception e) { throw failure(e); }
    }
    public Node document(Uri uri) throws IOException {
        try(Cursor c=resolver.query(uri,COLS,null,null,null)) {
            if(c==null || !c.moveToFirst()) throw new FileNotFoundException("Arquivo ou permissão indisponível.");
            return fromCursor(uri,c);
        } catch(Exception e) { throw failure(e); }
    }
    private Node fromCursor(Uri uri, Cursor c) throws IOException {
        long flags=c.isNull(5)?0:c.getLong(5);
        if((flags & DocumentsContract.Document.FLAG_PARTIAL)!=0) throw new IOException("Arquivo ainda incompleto no provedor.");
        String id=c.getString(0); String n=c.getString(1); String m=c.getString(2);
        return new Node(null,uri,"doc:"+uri.getAuthority()+":"+id,n==null?"Sem nome":n,m==null?"application/octet-stream":m,c.isNull(3)?-1:c.getLong(3),c.isNull(4)?0:c.getLong(4),flags);
    }
    public Node refresh(Node n) throws IOException { return n.file!=null?local(n.file):document(n.uri); }
    public List<Node> list(Node parent) throws IOException {
        check(); if(!parent.directory) throw new IOException("O destino não é uma pasta.");
        ArrayList<Node> out=new ArrayList<>();
        if(parent.file!=null) {
            File dir=checked(parent.file); File[] files=dir.listFiles();
            if(files==null) throw new IOException("Pasta sem acesso ou volume desconectado.");
            for(File f:files) { check(); if(Files.isSymbolicLink(f.toPath())) continue;
                String n=f.getName(); if(dir.getName().equalsIgnoreCase("Android") && (n.equalsIgnoreCase("data")||n.equalsIgnoreCase("obb"))) continue;
                out.add(local(f)); }
        } else {
            try {
                Uri children=DocumentsContract.buildChildDocumentsUriUsingTree(parent.uri,DocumentsContract.getDocumentId(parent.uri));
                try(Cursor c=resolver.query(children,COLS,null,null,null)) {
                    if(c==null) throw new IOException("Provedor indisponível.");
                    while(c.moveToNext()) { check(); Uri u=DocumentsContract.buildDocumentUriUsingTree(parent.uri,c.getString(0)); out.add(fromCursor(u,c)); }
                }
            } catch(Exception e) { throw failure(e); }
        }
        out.sort((a,b)->a.directory!=b.directory?(a.directory?-1:1):a.name.compareToIgnoreCase(b.name));
        return out;
    }
    public static String validName(String name) throws IOException {
        if(name==null || name.trim().isEmpty() || name.equals(".") || name.equals("..") || name.indexOf('/')>=0 || name.indexOf('\\')>=0 || name.length()>200)
            throw new IOException("Use um nome de 1 a 200 caracteres, sem barras.");
        for(int i=0;i<name.length();i++) if(Character.isISOControl(name.charAt(i))) throw new IOException("Nome contém caractere inválido.");
        return name;
    }
    private Node create(Node parent,String requested,String type) throws IOException {
        ListingCache.invalidate(context);
        check(); validName(requested); parent=refresh(parent);
        if(!parent.directory || !parent.canCreate) throw new IOException("Não é possível criar itens nesta pasta.");
        List<Node> before=list(parent); Set<String> names=new HashSet<>(), ids=new HashSet<>();
        for(Node n:before){ names.add(n.name.toLowerCase(Locale.ROOT)); ids.add(n.id); }
        String name=uniqueName(requested,names,DIR.equals(type));
        if(parent.file!=null) {
            for(int tries=0;tries<10000;tries++) {
                File dest=checked(new File(parent.file,name)); boolean made=DIR.equals(type)?dest.mkdir():dest.createNewFile();
                if(made) return local(dest);
                if(!dest.exists()) throw new IOException("Não foi possível criar o destino.");
                names.add(name.toLowerCase(Locale.ROOT)); name=uniqueName(requested,names,DIR.equals(type));
            }
            throw new IOException("Muitos arquivos com o mesmo nome.");
        }
        try {
            Uri u=DocumentsContract.createDocument(resolver,parent.uri,type,name);
            if(u==null) throw new IOException("O provedor não criou o destino.");
            Node made=document(u); if(ids.contains(made.id)) throw new IOException("O provedor retornou um item preexistente. Nenhum conteúdo foi escrito.");
            return made;
        } catch(Exception e) { throw failure(e); }
    }
    public static String uniqueName(String requested,Set<String> lowerNames,boolean directory) throws IOException {
        validName(requested); if(!lowerNames.contains(requested.toLowerCase(Locale.ROOT)))return requested;
        int dot=directory?-1:requested.lastIndexOf('.'); if(dot<=0)dot=-1;
        String stem=dot<0?requested:requested.substring(0,dot), ext=dot<0?"":requested.substring(dot);
        for(int i=2;i<10000;i++){String candidate=stem+" ("+i+")"+ext;if(!lowerNames.contains(candidate.toLowerCase(Locale.ROOT)))return candidate;}
        throw new IOException("Muitos conflitos de nome.");
    }
    public Node createDirectory(Node parent,String name) throws IOException { return create(parent,name,DIR); }
    public Node rename(Node node,String name) throws IOException {
        ListingCache.invalidate(context);
        check(); validName(name); node=refresh(node); if(name.equals(node.name)) return node;
        if(!node.canRename) throw new IOException("Este local não permite renomear.");
        if(node.file!=null) {
            File destination=checked(new File(node.file.getParentFile(),name));
            // Atomic no-replace move prevents a concurrent conflict from overwriting a file.
            try { Files.move(node.file.toPath(),destination.toPath()); return local(destination); }
            catch(Exception e){throw failure(e);}
        }
        // SAF rename semantics vary and some providers may replace conflicts. Never invoke it
        // without parent metadata and a safe transaction; offer copy/move in UI instead.
        throw new IOException("Renomear neste provedor não está habilitado nesta versão. Use copiar com segurança.");
    }
    public InputStream input(Node n) throws IOException {
        check(); n=refresh(n); if(n.directory) throw new IOException("Selecione um arquivo.");
        if(n.file!=null)return new FileInputStream(checked(n.file));
        try { InputStream s=resolver.openInputStream(n.uri); if(s==null)throw new IOException("Não foi possível abrir o arquivo.");return s; }
        catch(Exception e){throw failure(e);}
    }
    private OutputStream output(Node fresh) throws IOException {
        if(fresh.file!=null)return new FileOutputStream(checked(fresh.file));
        try { OutputStream s=resolver.openOutputStream(fresh.uri,"wt");if(s==null)throw new IOException("Destino indisponível.");return s; }
        catch(Exception e){throw failure(e);}
    }
    private byte[] digest(Node n) throws IOException {
        try {MessageDigest md=MessageDigest.getInstance("SHA-256");byte[] b=new byte[65536];try(InputStream in=input(n)){int read;while((read=in.read(b))!=-1){check();md.update(b,0,read);}}return md.digest();}
        catch(java.security.NoSuchAlgorithmException e){throw new IOException(e);}
    }
    private void checkDestination(Node source,Node destination) throws IOException {
        if(!destination.directory)throw new IOException("Escolha uma pasta de destino.");
        if(source.id.equals(destination.id))throw new IOException("Origem e destino são iguais.");
        if(source.directory && ((source.file==null)!=(destination.file==null)))
            throw new IOException("Para copiar pastas, escolha origem e destino no mesmo modo de acesso. Isso evita copiar uma pasta para dentro dela por outro caminho.");
        if(source.directory && source.file!=null && destination.file!=null) {
            String s=checked(source.file).getPath(), d=checked(destination.file).getPath();
            if(d.equals(s)||d.startsWith(s+File.separator))throw new IOException("Não é possível copiar uma pasta para dentro dela mesma.");
        } else if(source.directory) {
            Set<String> seen=new HashSet<>(); ArrayDeque<Node> dirs=new ArrayDeque<>();dirs.add(source);
            while(!dirs.isEmpty()){check();Node dir=dirs.removeFirst();if(!seen.add(dir.id))throw new IOException("Estrutura cíclica não suportada.");if(seen.size()>50000)throw new IOException("Pasta grande demais para validar com segurança nesta versão.");
                if(dir.id.equals(destination.id))throw new IOException("Destino está dentro da origem.");
                for(Node n:list(dir))if(n.directory)dirs.addLast(n);
            }
        }
    }
    public Node copy(Node source,Node destination,boolean move) throws IOException {
        ListingCache.invalidate(context);
        check(); source=refresh(source); destination=refresh(destination);checkDestination(source,destination);
        if(move && source.directory)throw new IOException("Movimentação de pastas não está disponível; use Copiar.");
        Node made=copyRecursive(source,destination,new HashSet<>(),0);
        if(move) {
            // A remote provider can change content between a check and deletion, without a
            // conditional-delete primitive. Preserve the source rather than promise safety.
            if(source.file==null)throw new IOException("Copiado e verificado. Original mantido: este provedor não oferece exclusão condicional segura.");
            Node now=refresh(source);
            if(now.size!=source.size || now.modified!=source.modified || !Arrays.equals(digest(source),digest(made)))
                throw new IOException("Copiado; original mantido porque foi alterado ou não pôde ser validado.");
            // This is not a transaction: external edits or device removal can still race
            // the final check. Never advertise atomicity or guaranteed recovery.
            Node finalState=refresh(source);
            if(finalState.size!=source.size || finalState.modified!=source.modified)
                throw new IOException("Copiado; original mantido porque mudou durante a verificação.");
            check(); if(!checked(source.file).delete())throw new IOException("Copiado; não foi possível excluir o original.");
        }
        return refresh(made);
    }
    private Node copyRecursive(Node src,Node dst,Set<String> seen,int depth) throws IOException {
        check();if(depth>64 || !seen.add(src.id))throw new IOException("Estrutura profunda ou cíclica não suportada.");
        Node made=create(dst,src.name,src.directory?DIR:src.mime);
        try {
            if(src.directory) {for(Node child:list(src))copyRecursive(child,made,seen,depth+1);}
            else {
                Node before=refresh(src);byte[] b=new byte[65536];
                try(InputStream in=input(src);OutputStream out=output(made)){int n;while((n=in.read(b))!=-1){check();out.write(b,0,n);}out.flush();if(out instanceof FileOutputStream)((FileOutputStream)out).getFD().sync();}
                Node after=refresh(src);if(before.size!=after.size || before.modified!=after.modified || !Arrays.equals(digest(src),digest(made)))throw new IOException("Conteúdo mudou ou verificação falhou; original preservado.");
            }
            return refresh(made);
        } catch(Exception e) {
            // Do not recursively clean a destination. It may already contain completed
            // children or content created externally after we created the directory.
            // Even a failed file may have been replaced by another process. Preserve it
            // for explicit user review, with a clear partial-result message.
            throw new IOException((e.getMessage()==null?"A cópia foi interrompida.":e.getMessage())
                + " Destino parcial mantido para revisão: " + made.name
                + ". Nenhum original foi excluído. Não considere esta cópia concluída.",e);
        } finally {seen.remove(src.id);}
    }
    public void delete(Node node) throws IOException {
        ListingCache.invalidate(context);
        check();node=refresh(node);if(!node.canDelete)throw new IOException("Este local não permite excluir.");
        removeRequested(node,new HashSet<>(),0);
    }
    private void removeRequested(Node node,Set<String> seen,int depth) throws IOException {
        check();
        if(depth>64 || !seen.add(node.id))throw new IOException("Estrutura profunda ou cíclica.");
        if(node.file!=null) {
            File f=checked(node.file);
            if(f.isDirectory()) {File[] children=f.listFiles();if(children==null)throw new IOException("Pasta indisponível.");for(File c:children){if(Files.isSymbolicLink(c.toPath()))throw new IOException("Não excluído: pasta contém link simbólico.");check();removeRequested(local(c),seen,depth+1);}}
            check();if(!f.delete())throw new IOException("Não foi possível excluir "+node.name);
        } else {
            // A provider deletion is one indivisible call; cancellation is checked
            // before it starts, not falsely promised during the provider operation.
            check();try {if(!DocumentsContract.deleteDocument(resolver,node.uri))throw new IOException("O provedor não confirmou a exclusão.");}catch(Exception e){throw failure(e);}
        }
    }
    public static String mime(String name) {
        int i=name.lastIndexOf('.');String ext=i<0?"":name.substring(i+1).toLowerCase(Locale.ROOT);
        String m=MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);return m==null?"application/octet-stream":m;
    }
}
