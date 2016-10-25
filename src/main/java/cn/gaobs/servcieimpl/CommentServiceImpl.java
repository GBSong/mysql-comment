package cn.gaobs.servcieimpl;

import cn.gaobs.service.BaseService;
import cn.gaobs.service.CommentService;
import cn.gaobs.util.KeyUtil;
import cn.gaobs.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.jsmth.data.jdbc.JdbcDao;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by gaobs on 2016/10/16.
 */
@Service("commentService")
public class CommentServiceImpl extends BaseService implements CommentService{

    @Resource
    JdbcDao appJdbcDao;


    /**
     *将文件注释添加到数据库
     * @param tableSchemas
     * @param filePath
     * @param charSet
     */
    @Override
    public void addComment(List<String> tableSchemas, String filePath, String charSet){
        Properties comments = new Properties();
        Map<String,String> commentsMap = new TreeMap<>();

        try {
            InputStream is = this.getClass().getResourceAsStream(filePath);
            Reader reader = new InputStreamReader(is, StringUtils.isNotBlank(charSet)?charSet:"UTF-8");
            comments.load(reader);

            Set<String> keys = comments.stringPropertyNames();
            for (String key : keys){
                commentsMap.put(key.toLowerCase(),comments.getProperty(key));
            }

            addComment(tableSchemas, commentsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库中表添加注释
     * @param tableSchemas
     * @param comments
     */
    @Override
    public void addComment(List<String> tableSchemas, Map<String,String> comments){
        Set<String> keys = comments.keySet();
        Set<String> schemas = KeyUtil.getSchemas(keys);

        for (String tableSchema : tableSchemas){
            if (schemas.contains(tableSchema)) {

                List<String> tableNames = getTablesName(tableSchema);
                for (String tableName : tableNames) {
                    addCommentForTable(tableSchema, tableName, comments.get(tableSchema + "." + tableName));

                    if (KeyUtil.hasTableField(keys, tableSchema, tableName)) {
                        List<Map<String, String>> columnsInfo = getColumnsInfo(tableSchema, tableName);
                        for (Map<String, String> columnInfo : columnsInfo) {
                            addCommentForColumnOfTalbe(columnInfo, comments);
                        }
                    }
                }
            }
        }
    }

    /**
     * 为表加注释
     * alter table resoursemanage.courseitembank COMMENT '课程题库关系表';
     * @param tableSchema
     * @param tableName
     * @param comment
     */
    @Override
    public void addCommentForTable(String tableSchema, String tableName, String comment){
        try {
            if (StringUtils.isNotBlank(comment)) {
                StringBuffer sql = new StringBuffer();
                sql.append("ALTER TABLE ");
                sql.append(tableSchema);
                sql.append(".");
                sql.append(tableName);
                sql.append(" COMMENT '");
                sql.append(comment);
                sql.append("'");

                logger.fatal("");
                logger.fatal(sql.toString());
                appJdbcDao.getJdbcTemplate().getJdbcOperations().execute(sql.toString());
            }else {
                logger.fatal("false:"+ tableSchema+"."+tableName);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为表中列加注释
     * alter table resoursemanage.courseitembank modify column courseID varchar(255) NOT NULL DEFAULT UUID() COMMENT '课程ID'
     * @param columnsInfo
     * @param comments
     */
    @Override
    public void addCommentForColumnOfTalbe(Map<String,String> columnsInfo, Map<String,String> comments){
        try {
            String columnName = columnsInfo.get("TABLE_SCHEMA")+"."+columnsInfo.get("TABLE_NAME")+"."+columnsInfo.get("COLUMN_NAME");
            String comment = comments.get(columnName);
            if (StringUtils.isNotBlank(comment)){
                StringBuffer sql = new StringBuffer();
                sql.append("ALTER TABLE ");
                sql.append(columnsInfo.get("TABLE_SCHEMA"));
                sql.append(".");
                sql.append(columnsInfo.get("TABLE_NAME"));
                sql.append(" MODIFY COLUMN ");
                sql.append(columnsInfo.get("COLUMN_NAME"));
                sql.append(" ");
                sql.append(columnsInfo.get("COLUMN_TYPE"));
                sql.append(" ");

                if ("no".equals(columnsInfo.get("IS_NULLABLE"))){
                    sql.append("NOT NULL ");
                }
                if (StringUtils.isNotBlank(columnsInfo.get("COLUMN_DEFAULT"))){
                    sql.append("DEFAULT '");
                    sql.append(columnsInfo.get("COLUMN_DEFAULT"));
                    sql.append("' ");
                }
                if (StringUtils.isNotBlank(columnsInfo.get("EXTRA"))){
                    sql.append(columnsInfo.get("EXTRA"));
                    sql.append(" ");
                }

                sql.append("COMMENT  '");
                sql.append(comment);
                sql.append("'");

                logger.fatal(sql.toString());
                appJdbcDao.getJdbcTemplate().getJdbcOperations().execute(sql.toString());
            }else {
                logger.fatal("false:"+ columnName);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取库中表名
     * select TABLE_NAME talbeName from information_schema.`TABLES` where TABLE_SCHEMA = 'resourcemanage_dev';
     * @param tableSchema
     * @return
     */
    @Override
    public List<String> getTablesName(String tableSchema){
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT TABLE_NAME talbeName FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = '");
            sql.append(tableSchema);
            sql.append("'");

            logger.error(sql.toString());
            List<String> results = appJdbcDao.queryForList(sql.toString(),new HashMap<String,Object>(),String.class);
            return  (List<String>) ResultUtil.toLowerCase(results);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    /**
     * 获取表中列信息
     * select * from information_schema.`COLUMNS` where TABLE_SCHEMA = 'resourcemanage_dev' and TABLE_NAME = 'courseitembank';
     * @param tableSchema
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String,String>> getColumnsInfo(String tableSchema, String tableName){
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT TABLE_SCHEMA,TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,IS_NULLABLE,COLUMN_DEFAULT,EXTRA ");
            sql.append("FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA = '");
            sql.append(tableSchema);
            sql.append("' AND TABLE_NAME = '");
            sql.append(tableName);
            sql.append("'");

            logger.error(sql.toString());
            List<Map<String,String>> results = appJdbcDao.query(sql.toString(), new HashMap<String, Object>(),
                    new ResultSetExtractor<List<Map<String, String>>>() {
                        @Override
                        public List<Map<String, String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
                            List<Map<String,String>> results = new ArrayList<Map<String, String>>();
                            String[] lable = {"TABLE_SCHEMA","TABLE_NAME","COLUMN_NAME","COLUMN_TYPE","IS_NULLABLE","COLUMN_DEFAULT","EXTRA"};

                            while (rs.next()){
                                Map<String,String> result = new HashMap<String,String>();
                                for (int i = 0; i<lable.length; i++){
                                    result.put(lable[i],StringUtils.isBlank(rs.getString(lable[i]))?"":rs.getString(lable[i]));
                                }
                                results.add(result);
                            }

                            return results;
                        }
                    });

            return (List<Map<String,String>>) ResultUtil.toLowerCase(results);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
