package com.studiocliquim.arquivos;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Bounded, read-only traversal. Errors and omitted areas remain visible in the result. */
public final class ReadOnlyScan<T> {
    public interface Source<T>{String id(T n);boolean directory(T n);boolean hidden(T n);List<T> children(T n)throws IOException;}
    public interface Visitor<T>{boolean visit(T node,T parent);}
    public static final class Result{
        public int visited,unavailable;public boolean cancelled,truncated;public String lastError="";
        public boolean complete(){return !cancelled&&!truncated&&unavailable==0;}
    }
    final Source<T> source;final int maxEntries,maxDepth;final long maxMillis;
    public ReadOnlyScan(Source<T> source,int maxEntries,int maxDepth,long maxMillis){this.source=source;this.maxEntries=maxEntries;this.maxDepth=maxDepth;this.maxMillis=maxMillis;}
    private static final class Step<T>{final T item;final int depth;Step(T item,int depth){this.item=item;this.depth=depth;}}
    public Result run(T root,boolean recursive,boolean hidden,AtomicBoolean stop,Visitor<T> visitor){
        Result result=new Result();Set<String> seen=new HashSet<>();seen.add(source.id(root));
        ArrayDeque<Step<T>> queue=new ArrayDeque<>();queue.add(new Step<>(root,0));long deadline=System.nanoTime()+maxMillis*1_000_000L;
        outer:while(!queue.isEmpty()){
            if(stop.get()||Thread.currentThread().isInterrupted()){result.cancelled=true;break;}
            if(System.nanoTime()>deadline){result.truncated=true;break;}
            Step<T> at=queue.removeFirst();List<T> children;
            try{children=source.children(at.item);}catch(IOException e){result.unavailable++;result.lastError=e.getMessage()==null?"Local indisponível":e.getMessage();continue;}
            for(T child:children){
                if(stop.get()||Thread.currentThread().isInterrupted()){result.cancelled=true;break outer;}
                if(result.visited>=maxEntries||System.nanoTime()>deadline){result.truncated=true;break outer;}
                if(!hidden&&source.hidden(child))continue;
                if(!seen.add(source.id(child)))continue;
                result.visited++;
                if(!visitor.visit(child,at.item)){result.truncated=true;break outer;}
                if(recursive&&source.directory(child)){
                    if(at.depth>=maxDepth)result.truncated=true;
                    else queue.addLast(new Step<>(child,at.depth+1));
                }
            }
        }
        if(stop.get())result.cancelled=true;
        return result;
    }
}
