package com.authine.cloudpivot.web.api.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.utils.ConnectionUtils
 * @Date 2019/12/16 15:41
 **/
public class ConnectionUtils {

    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String USER = "root";
    public static final String URL = "jdbc:mysql://127.0.0.1:3306/cloudpivot?serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    public static final String PASSWORD = "Bjwq_123456";

    public static Connection CreateConnection() throws Exception{
        Connection conn = null;
        try {
            //注册JDBC驱动程序
            Class.forName(DRIVER);
            //建立连接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(PreparedStatement ps, Connection conn) {
        close(ps);
        close(conn);
    }

    public static void close(Statement st, Connection conn) {
        close(st);
        close(conn);
    }

    private static void close(Statement st) {
        try {
            if (st != null) st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void close(PreparedStatement ps) {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void close(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法说明：执行查询sql,返回查询结果
     * @Param sql
     * @return java.sql.ResultSet
     * @throws
     * @author liulei
     * @Date 2020/1/7 13:43
     */
    public static List <Map <String, Object>> executeSelectSql(String sql) throws Exception{
        Connection conn = null;
        Statement statement = null;
        try {
            conn = CreateConnection();
            statement = conn.createStatement();
            System.out.println("=====================" + sql);
            ResultSet rs = statement.executeQuery(sql);

            List <Map <String, Object>> result = new ArrayList <>();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();   //获得列数
            while (rs.next()) {
                Map<String,Object> rowData = new HashMap<String,Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                result.add(rowData);

            }
            return result;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            //关闭资源
            close(statement, conn);
        }
    }

    /**
     * 方法说明：执行查询sql,返回查询结果
     * @Param sql
     * @return java.sql.ResultSet
     * @throws
     * @author liulei
     * @Date 2020/1/7 13:43
     */
    public static List <Map <String, String>> executeSelectSql1(String sql) throws Exception{
        List <Map <String, String>> result = new ArrayList <>();

        Connection conn = null;
        Statement statement = null;
        try {
            conn = CreateConnection();
            statement = conn.createStatement();
            System.out.println("=====================" + sql);
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Map<String, String> map = new HashMap <>();
                map.put("message", rs.getString("message"));
                String salesman = "";
                if ("1".equals(rs.getString("type"))) {
                    salesman = formatJsonSting(rs.getString("salesman"));
                } else {
                    salesman = rs.getString("salesman");
                }
                String operator = formatJsonSting(rs.getString("operator"));
                map.put("sendId", salesman + "," + operator);

                result.add(map);
            }
            return result;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            //关闭资源
            close(statement, conn);
        }
    }

    private static String formatJsonSting(String str) {
        String result = "";
        // 去除[}]
        str = str.substring(1, str.length() -2);
        // 去除{
        str = str.replaceAll("\\{", "");
        String[] strArr = str.split("},");
        if (strArr.length > 0) {
            for (int i = 0; i < strArr.length; i++) {
                String idStr = strArr[i].split(",")[0];
                String id = idStr.substring(6, idStr.length()-1);
                result += id + ",";
            }
            result = result.substring(0, result.length()-1);
        }

        return result;
    }


    /**
     * 方法说明：执行查询sql
     * @Param sql
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/7 13:43
     */
    public static boolean executeSql(String sql) throws Exception{
        Connection conn = null;
        Statement statement = null;
        try {
            conn = CreateConnection();
            statement = conn.createStatement();
            System.out.println("=====================" + sql);
            return statement.execute(sql);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            //关闭资源
            close(statement, conn);
        }
    }

    /**
     * 方法说明：
     * @Param sqlArr
     * @return void
     * @throws
     * @author liulei
     * @Date 2020/1/7 13:59
     */
    public static void executeSql(String[] sqlArr) throws Exception{
        Connection conn = null;
        Statement statement = null;
        try {
            conn = CreateConnection();
            statement = conn.createStatement();

            for (int i=0; i< sqlArr.length; i++) {
                statement.addBatch(sqlArr[i]);
                System.out.println("=====================" + sqlArr[i]);
            }

            statement.executeBatch();
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            //关闭资源
            close(statement, conn);
        }
    }
}
