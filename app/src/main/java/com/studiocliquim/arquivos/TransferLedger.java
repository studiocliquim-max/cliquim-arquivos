package com.studiocliquim.arquivos;

import android.content.SharedPreferences;
import org.json.*;

/** Last operation journal, not a resumable queue. No work is replayed from this data. */
final class TransferLedger {
    static JSONObject record;
    static synchronized void begin(SharedPreferences prefs,String destination,String destinationId,String originId,int total,boolean move){
        record=new JSONObject();try{record.put("destination",destination).put("destinationId",destinationId).put("originId",originId).put("total",total).put("move",move).put("running",true).put("done",0).put("skipped",0).put("errors",0).put("started",System.currentTimeMillis()).put("items",new JSONArray());}catch(JSONException ignored){}
        save(prefs);
    }
    static synchronized void item(SharedPreferences prefs,int index,String name,String state,String detail){
        if(record==null)return;
        try{
            JSONArray items=record.getJSONArray("items");JSONObject entry=null;
            for(int i=0;i<items.length();i++)if(items.getJSONObject(i).optInt("index")==index){entry=items.getJSONObject(i);break;}
            if(entry==null){entry=new JSONObject().put("index",index).put("name",name);items.put(entry);if(items.length()>100){items.remove(0);record.put("omitted",record.optInt("omitted")+1);}}
            entry.put("state",state).put("detail",detail);record.put("index",index);
            if(state.equals("Concluído"))record.put("done",record.optInt("done")+1);
            if(state.equals("Pulado"))record.put("skipped",record.optInt("skipped")+1);
            if(state.equals("Atenção"))record.put("errors",record.optInt("errors")+1);
        }catch(JSONException ignored){}save(prefs);
    }
    static synchronized void finish(SharedPreferences prefs,boolean cancelled){if(record==null)return;try{record.put("running",false).put("cancelled",cancelled).put("finished",System.currentTimeMillis());}catch(JSONException ignored){}save(prefs);}
    static synchronized JSONObject snapshot(SharedPreferences prefs){try{return new JSONObject(record==null?prefs.getString("transfer_ledger","{}"):record.toString());}catch(JSONException e){return new JSONObject();}}
    static synchronized void clear(){record=null;}
    private static void save(SharedPreferences prefs){prefs.edit().putString("transfer_ledger",record.toString()).commit();}
}
