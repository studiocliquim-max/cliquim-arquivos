import com.studiocliquim.arquivos.ScanRules;

public final class ScanRulesTest {
    static int checks;
    static void ok(boolean value,String label){if(!value)throw new AssertionError(label);checks++;}
    public static void main(String[] args){
        ok(ScanRules.matches("Viagem.JPG","image/jpeg",false,20,50,"VIAGEM","Imagens",20,50),"inclusive filters and case folding");
        ok(!ScanRules.matches("Foto","image/jpeg",false,-1,50,"","Imagens",1,0),"unknown size does not match min size");
        ok(!ScanRules.matches("Foto","image/jpeg",false,100,0,"","Imagens",0,10),"unknown date does not match time filter");
        ok(!ScanRules.matches("Foto","image/jpeg",false,100,9,"","Imagens",0,10),"older date excluded");
        ok(!ScanRules.matches("Pasta","image/jpeg",true,100,50,"","Imagens",0,0),"folders excluded from media type");
        ok(!ScanRules.matches("Pasta",null,true,100,50,"","Todos",1,0),"folders excluded from file size filters");
        ok(ScanRules.matches("Pasta",null,true,-1,0,"past","Todos",0,0),"folder name search supported");
        ok(!ScanRules.matches("Outro","image/jpeg",false,100,50,"foto","Todos",0,0),"unrelated name excluded");
        ok(ScanRules.category("application/pdf").equals("Documentos"),"PDF category");
        ok(ScanRules.category("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").equals("Documentos"),"spreadsheet category");
        ok(ScanRules.category("application/zip").equals("Outros"),"archive not document");
        ok(ScanRules.category(null).equals("Outros"),"unknown MIME category");
        ok(ScanRules.category("video/mp4").equals("Vídeos"),"video category");
        ok(ScanRules.category("audio/mpeg").equals("Áudios"),"audio category");
        ok(ScanRules.add(20,-1)==20,"unknown bytes excluded");
        ok(ScanRules.add(20,30)==50,"actual bytes sum");
        ok(ScanRules.add(Long.MAX_VALUE-1,2)==Long.MAX_VALUE,"overflow saturates");
        System.out.println(checks+" search and accounting checks passed.");
    }
}
