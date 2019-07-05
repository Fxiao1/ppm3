package ext.modular.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * des:
 *  connectionde 的工具类，目前暂时不写单例了，因为最后是要获取windchill的connection的
 * @date 2019/6/4 14:31
 */
public class ConnectionUtil {
    private final static Logger log= LoggerFactory.getLogger(ConnectionUtil.class);
    private static String userName=null;
    private static String password=null;
    private static String driver=null;
    private static String url=null;

    /**
     * 获取数据库配置信息
     * @Author Fxiao
     * @Description
     * @Date 16:47 2019/6/25
     * @param
     * @return void
     **/
    private static void readDbInfo(){
        InputStream in = ConnectionUtil.class.
                getClassLoader().getResourceAsStream("jdbc.properties");
        Properties p = new Properties();
        try {
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userName=p.getProperty("db.userName");
        password=p.getProperty("db.password");
        driver=p.getProperty("db.driver");
        url=p.getProperty("db.url");
    }

    /**
     * 普通获取链接方式
     * @Description
     * @param
     * @return java.sql.Connection
     **/
    public static Connection getConnection() {
        if(driver==null){
            readDbInfo();
        }
        Connection connection=null;
        try {
            Class.forName(driver);
            connection=DriverManager.getConnection(url,userName,password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    /**
     * 从windchill中获取链接老是报关闭的链接，先改成普通的获取方式
     * @Author Fxiao
     * @Description
     * @Date 16:43 2019/7/1
     * @param
     * @return java.sql.Connection
     **/
    public static Connection getJdbcConnection() throws ClassNotFoundException, SQLException {
       /* Connection connection=null;
        try {
            MethodContext methodcontext = MethodContext.getContext();
            WTConnection wtConn = (WTConnection) methodcontext.getConnection();
            connection=wtConn.getConnection();
            log.debug("获取的数据库连接为："+connection);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            try {
                throw new WTException(e);
            } catch (WTException e1) {
                e1.printStackTrace();
            }
        }*/
        return getConnection();
    }

    public static void close(Connection connection, Statement statement){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Connection connection, PreparedStatement ps){
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(ps!=null){
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
