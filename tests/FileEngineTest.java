import com.studiocliquim.arquivos.FileEngine;
import java.util.*;
import java.io.IOException;

/** Host-JVM checks of pure naming invariants. No device/user files are touched. */
public final class FileEngineTest {
    static int passed;
    static void eq(Object a,Object b){if(!Objects.equals(a,b))throw new AssertionError(a+" != "+b);passed++;}
    static void bad(String s)throws Exception{try{FileEngine.validName(s);throw new AssertionError("Accepted unsafe name: "+s);}catch(IOException good){passed++;}}
    public static void main(String[] args)throws Exception {
        for(String s:new String[]{null,""," ",".","..","a/b","a\\b","x\n","a\u0000","x".repeat(201)})bad(s);
        for(String s:new String[]{"Foto.jpg","Minhas fotos",".nomedia","relatório 2026.pdf","x".repeat(200)})eq(FileEngine.validName(s),s);
        eq(FileEngine.uniqueName("foto.jpg",Set.of(),false),"foto.jpg");
        eq(FileEngine.uniqueName("foto.jpg",Set.of("foto.jpg"),false),"foto (2).jpg");
        eq(FileEngine.uniqueName("FOTO.JPG",Set.of("foto.jpg","foto (2).jpg"),false),"FOTO (3).JPG");
        eq(FileEngine.uniqueName(".nomedia",Set.of(".nomedia"),false),".nomedia (2)");
        eq(FileEngine.uniqueName("pasta.v1",Set.of("pasta.v1"),true),"pasta.v1 (2)");
        Set<String> busy=new HashSet<>();busy.add("a.txt");for(int i=2;i<10000;i++)busy.add("a ("+i+").txt");
        try{FileEngine.uniqueName("a.txt",busy,false);throw new AssertionError("Unbounded collision accepted");}catch(IOException good){passed++;}
        System.out.println(passed+" pure naming checks passed. Device IO tests are separate.");
    }
}
