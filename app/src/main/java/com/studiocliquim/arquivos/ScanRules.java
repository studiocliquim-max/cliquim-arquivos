package com.studiocliquim.arquivos;

import java.util.Locale;

/** Pure matching rules shared by search, categories and storage accounting. */
public final class ScanRules {
    private ScanRules(){}
    public static String category(String mime){
        if(mime==null)return "Outros";
        if(mime.startsWith("image/"))return "Imagens";
        if(mime.startsWith("video/"))return "Vídeos";
        if(mime.startsWith("audio/"))return "Áudios";
        if(mime.startsWith("text/")||mime.contains("pdf")||mime.contains("document")||mime.contains("sheet")||mime.contains("presentation")||mime.contains("msword"))return "Documentos";
        return "Outros";
    }
    public static boolean matches(String name,String mime,boolean directory,long size,long modified,String query,String category,long minBytes,long since){
        if(!name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)))return false;
        if(!category.equals("Todos")&&(directory||!category(mime).equals(category)))return false;
        if(minBytes>0&&(directory||size<minBytes))return false;
        return since<=0||(modified>0&&modified>=since);
    }
    public static long add(long total,long size){return size<0?total:Long.MAX_VALUE-total<size?Long.MAX_VALUE:total+size;}
}
