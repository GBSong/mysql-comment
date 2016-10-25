package cn.gaobs.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gaobs on 2016/10/16.
 */
public class KeyUtil {


    public static Set<String> getSchemas(Set<String> keys){
        Set<String> schemas = new HashSet<String>();
        for (String key : keys){
            schemas.add(key.substring(0,key.indexOf(".")));
        }
        return schemas;
    }

    public static Set<String> getTables(Set<String> keys){
        Set<String> tables = new HashSet<String>();
        for (String key : keys){
            int fromIndex = key.indexOf(".")+1;
            int toIndex = key.indexOf(".", fromIndex);
            tables.add(key.substring(0, toIndex==-1?key.length():toIndex));
        }
        return tables;
    }

    public static boolean hasTableField(Set<String> keys, String tableSchema, String tableName){
        boolean result = false;
        String table = tableSchema+"."+tableName+".";

        for (String key : keys){
            if (key.contains(table)){
                result = true;
                break;
            }
        }
        return result;
    }

}
