import com.studiocliquim.arquivos.ReadOnlyScan;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Fake provider graph tests; never accesses device or user files. */
public final class ReadOnlyScanTest {
    static int checks;
    static void ok(boolean value,String label){if(!value)throw new AssertionError(label);checks++;}
    static final class Provider implements ReadOnlyScan.Source<String>{
        Map<String,List<String>> entries=new LinkedHashMap<>();Set<String> failed=new HashSet<>();
        public String id(String s){return s;}
        public boolean directory(String s){return entries.containsKey(s)||failed.contains(s);}
        public boolean hidden(String s){return s.startsWith(".");}
        public List<String> children(String s)throws IOException{if(failed.contains(s))throw new IOException("Disconnected");return entries.getOrDefault(s,List.of());}
    }
    static ReadOnlyScan<String> scan(Provider p,int count,int depth){return new ReadOnlyScan<>(p,count,depth,60000);}
    public static void main(String[] args){
        Provider p=new Provider();p.entries.put("root",List.of("folder","a",".hidden"));p.entries.put("folder",List.of("b","root","a"));p.entries.put(".hidden",List.of("secret"));
        List<String> found=new ArrayList<>();ReadOnlyScan.Result r=scan(p,100,64).run("root",false,false,new AtomicBoolean(),(n,parent)->{found.add(n);return true;});
        ok(found.equals(List.of("folder","a")),"nonrecursive and hidden boundaries");ok(r.complete()&&r.visited==2,"complete nonrecursive report");
        found.clear();r=scan(p,100,64).run("root",true,false,new AtomicBoolean(),(n,parent)->{found.add(n);return true;});
        ok(found.equals(List.of("folder","a","b")),"cycles and duplicate references read once");ok(r.complete(),"bounded cycle traversal finishes");
        found.clear();r=scan(p,100,64).run("root",true,true,new AtomicBoolean(),(n,parent)->{found.add(n);return true;});ok(found.contains("secret")&&r.visited==5,"explicit hidden inclusion");
        found.clear();r=scan(p,2,64).run("root",true,true,new AtomicBoolean(),(n,parent)->{found.add(n);return true;});ok(r.truncated&&r.visited==2&&found.size()==2,"entry budget enforced");
        r=scan(p,100,0).run("root",true,false,new AtomicBoolean(),(n,parent)->true);ok(r.truncated&&r.visited==2,"depth bound reports partial coverage");
        AtomicBoolean stop=new AtomicBoolean(true);r=scan(p,100,64).run("root",true,true,stop,(n,parent)->{throw new AssertionError("visited after cancel");});ok(r.cancelled&&r.visited==0,"cancel before traversal");
        stop.set(false);r=scan(p,100,64).run("root",true,true,stop,(n,parent)->{stop.set(true);return true;});ok(r.cancelled&&r.visited==1,"cancel during traversal");
        r=scan(p,100,64).run("root",true,true,new AtomicBoolean(),(n,parent)->false);ok(r.truncated&&r.visited==1,"consumer result budget");
        p.failed.add("offline");p.entries.put("root",List.of("offline","folder"));found.clear();r=scan(p,100,64).run("root",true,false,new AtomicBoolean(),(n,parent)->{found.add(n);return true;});ok(r.unavailable==1&&!r.complete(),"unavailable area is not reported complete");ok(found.contains("b")&&r.lastError.equals("Disconnected"),"other accessible areas survive provider failure");
        r=new ReadOnlyScan<>(p,100,64,-1).run("root",true,false,new AtomicBoolean(),(n,parent)->true);ok(r.truncated&&r.visited==0,"time budget respected before provider access");
        System.out.println(checks+" read-only traversal checks passed.");
    }
}
