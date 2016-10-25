package cn.gaobs.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gaobs on 2016/10/16.
 */
public  class ResultUtil {
    public static List<?> toLowerCase(List<?> results) {
        if (CollectionUtils.isNotEmpty(results)) {
            List<String> lowerList = new ArrayList<String>();

            for (Object result : results) {
                if (result instanceof Map) {
                    Map<String,String> map = (Map<String,String>) result;
                    for (String key : map.keySet()) {
                        map.replace(key, map.get(key), StringUtils.isBlank(map.get(key))?"":map.get(key).toLowerCase());
                    }
                }
                else if (result instanceof String){
                    lowerList.add(((String) result).toLowerCase());
                }
                else {
                    throw new RuntimeException("转换失败,尚不支持此类型转换");
                }
            }

            return lowerList.isEmpty()?results:lowerList;
        }
        return results;
    }

}
