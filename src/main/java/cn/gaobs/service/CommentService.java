package cn.gaobs.service;

import java.util.List;
import java.util.Map;

/**
 * Created by gaobs on 2016/10/16.
 */
public interface CommentService {
    public void addComment(List<String> tableSchemas, String filePath, String charSet);
    public void addComment(List<String> tableSchemas, Map<String, String> comments);
    public void addCommentForTable(String tableSchema, String tableName, String comment);
    public void addCommentForColumnOfTalbe(Map<String,String> columnsInfo, Map<String,String> comments);
    public List<String> getTablesName(String tableSchema);
    public List<Map<String,String>> getColumnsInfo(String tableSchema, String tableName);
}
