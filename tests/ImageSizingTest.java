import com.studiocliquim.arquivos.ImageSizing;
import java.io.IOException;

public final class ImageSizingTest {
    public static void main(String[] args)throws Exception {
        int checks=0;
        for(int[] dimensions:new int[][]{{1,1},{2048,2048},{2049,1},{4032,3024},{100000,10},{Integer.MAX_VALUE,Integer.MAX_VALUE}}){
            int s=ImageSizing.sample(dimensions[0],dimensions[1]);
            if((s&(s-1))!=0||((long)dimensions[0]+s-1)/s>2048||((long)dimensions[1]+s-1)/s>2048)
                throw new AssertionError("Unsafe allocation bound");
            if(s>1&&(long)dimensions[0]<=2048L*(s/2)&&(long)dimensions[1]<=2048L*(s/2))
                throw new AssertionError("Unnecessarily reduced quality");
            checks++;
        }
        for(int[] dimensions:new int[][]{{0,2},{2,0},{-1,200}}){
            try{ImageSizing.sample(dimensions[0],dimensions[1]);throw new AssertionError("Invalid bounds accepted");}
            catch(IOException expected){checks++;}
        }
        System.out.println(checks+" image allocation checks passed.");
    }
}
