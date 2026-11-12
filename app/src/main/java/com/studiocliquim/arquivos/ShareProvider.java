package com.studiocliquim.arquivos;
import android.content.*; import android.database.*; import android.net.Uri; import android.os.*; import android.provider.OpenableColumns; import java.io.*;
public class ShareProvider extends ContentProvider {
 public boolean onCreate(){return true;}
 private File resolve(Uri u) throws FileNotFoundException {try { File root=Environment.getExternalStorageDirectory().getCanonicalFile(); File f=new File(root,u.getQueryParameter("path")).getCanonicalFile(); if(!f.getPath().startsWith(root.getPath()+"/") || f.getPath().startsWith(new File(root,"Android").getPath()+"/") || !f.isFile()) throw new IOException(); return f;}catch(Exception e){throw new FileNotFoundException("Arquivo indisponível");}}
 public ParcelFileDescriptor openFile(Uri u,String mode)throws FileNotFoundException {if(!"r".equals(mode)) throw new FileNotFoundException("Somente leitura"); return ParcelFileDescriptor.open(resolve(u),ParcelFileDescriptor.MODE_READ_ONLY);}
 public String getType(Uri u){try{return MainActivity.mime(resolve(u).getName());}catch(Exception e){return "application/octet-stream";}}
 public Cursor query(Uri u,String[] projection,String selection,String[] args,String order){try{File f=resolve(u); String[] cols=projection==null?new String[]{OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE}:projection; MatrixCursor c=new MatrixCursor(cols); Object[] row=new Object[cols.length];for(int i=0;i<cols.length;i++)row[i]=cols[i].equals(OpenableColumns.DISPLAY_NAME)?f.getName():cols[i].equals(OpenableColumns.SIZE)?f.length():null;c.addRow(row);return c;}catch(Exception e){return null;}}
 public Uri insert(Uri u,ContentValues v){throw new UnsupportedOperationException();} public int delete(Uri u,String a,String[] b){throw new UnsupportedOperationException();} public int update(Uri u,ContentValues v,String a,String[] b){throw new UnsupportedOperationException();}
}
