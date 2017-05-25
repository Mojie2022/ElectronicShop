package cn.hd.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by xzl on 2017/5/24.
 */
public class DbUtils {
    /**
     * Oracle数据库的URL地址
     * jdbc:oracle:thin:@  这是固定的地址
     * localhost   表示本地主机，还可以使用ip地址或者是本地主机的环回地址（127.0.0.1）
     * 1521   Oracle数据库服务的端口号
     * XE  数据库服务的实例名称
     */
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    /**
     * 登录数据库的账号名
     */
    private static final String USER = "hdshop";
    /**
     * 登录数据库的密码
     */
    private static final String PASSWORD = "hdshop";

    /**
     * 获取数据库连接通道
     * @return
     *      Connection对象
     */
    public static Connection getConnection(){
        try {
            //通过反射注册和加载数据库驱动程序
            //Class.forName("oracle.jdbc.driver.OracleDriver");
            //通过DriverManager获取数据库连接通道
            //return DriverManager.getConnection(URL,USER, PASSWORD);
            //通过c3p0获取数据库连接通道对象
            return DataSourceConfig.getDataSource().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭数据库相关的资源
     * @param conn 数据库连接通道
     * @param ps
     */
    public static void close(Connection conn, PreparedStatement ps){
        try {
            if(ps!=null){
                ps.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 关闭数据库相关的资源
     * @param conn  连接通道对象
     * @param ps  PreparedStatement
     * @param rs  结果集
     */
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs){
        try {
            if(rs != null){
                rs.close();
            }
            if(ps!=null){
                ps.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
