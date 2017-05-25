package cn.hd.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xzl on 2017/5/24.
 */
public class BaseDao {

    /**
     * DML操作的通用方法
     *      添加  删除  修改
     * @param sql  DML操作的SQL语句
     * @param params  SQL语句中占位符对应的参数
     * @return
     *      -1 表示操作失败
     *      其他表示操作成功
     */
    public static int baseUpdate(String sql,Object...params){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //获取数据库连接通道
            conn = DbUtils.getConnection();
            //获取执行SQL语句的对象PreparedStatement
            ps = conn.prepareStatement(sql);
            //对SQL语句中的占位符进行一一赋值（将SQL语句中占位符对应的参数保存到ps对象中
            // 在数据库服务器端再进行拼接执行）
            if(params!=null){
                for (int i=0;i<params.length;i++){
                    ps.setObject(i+1,params[i]);
                }
            }
            //执行SQL语句 返回影响的行数
            return ps.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            DbUtils.close(conn,ps);
        }
        //如果操作失败则返回-1
        return -1;
    }

    public static int queryForCount(String sql,Object...params){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取数据连接通道对象
            conn = DbUtils.getConnection();
            //获取执行SQL语句的对象PreparedStatement
            ps = conn.prepareStatement(sql);
            //对SQL语句中的占位符进行一一赋值（将SQL语句中占位符对应的参数保存到ps对象中，然后到了数据库服务器端
            // 再将其拼接成完整的SQL语句）
            if(params!=null){
                for (int i=0;i<params.length;i++){
                    ps.setObject(i+1,params[i]);
                }
            }
            //执行SQL语句 返回结果集
            rs = ps.executeQuery();
            //获取查询的结果
            if(rs.next()){
                return rs.getInt(1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 查询单个数据
     * @param sql SQL语句
     * @param cls 保存查询数据的对象
     * @param params 可变参数
     *               类似于数组  保存SQL语句中占位符对应的参数
     * @param <T>  返回值类型
     * @return
     *         返回查询的结果对象
     */
    public static <T> T queryForSingle(String sql,Class<T> cls,Object...params){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取数据库连接通道对象
            conn = DbUtils.getConnection();
            //获取执行SQL语句的对象 PreparedStatement
            ps = conn.prepareStatement(sql);
            //对SQL语句中的占位符进行一一赋值（将SQL语句中占位符对应的参数保存到ps对象中，然后到了数据库服务器端
            // 再将其拼接成完整的SQL语句）
            if(params!=null){
                for (int i=0;i<params.length;i++){
                    ps.setObject(i+1,params[i]);
                }
            }
            //执行SQL语句 返回结果集
            rs = ps.executeQuery();
            //获取结果集对应的元数据（即结果集的描述）
            ResultSetMetaData rsmd = rs.getMetaData();
            //获取结果集中的总列数
            int columnCount = rsmd.getColumnCount();
            //获取结果集中的数据
            if(rs.next()){
                //创建一个对象，用于保存查询出来的数据
                T obj =  cls.newInstance();
                //rs.next()执行后，rs就会判断其当前指针所指向的行是否有下一行数据，如果没有则返回false
                //如果有，则返回true，并将指针指向下一行数据及将下一行数据保存到rs对象中
                //通过rs对象中的方法获取数据
                for (int i=0;i<columnCount;i++){
                    //获取结果集中每一列对应的列的名称，并将其全部转化成小写的
                    String columnName = rsmd.getColumnName(i+1).toLowerCase();
                    //根据列名获取结果集中每一列对应的值
                    Object value = rs.getObject(columnName);
                    //如果value值为空的话，则没有必要执行下面赋值的过程，执行跳出当前循环执行下一次循环
                    if(value == null){
                        continue;
                    }
                    //判断在cls对象中是否存在与columnName对应的成员属性
                    if (hasField(cls,columnName)){
                        //根据列名找到cls对象中对应的成员属性对象
                        Field f = cls.getDeclaredField(columnName);
                        //设置私有的成员属性允许访问的权限
                        f.setAccessible(true);
                        //判断value是否属于BigDecimal类型
                        if(value instanceof BigDecimal){
                            //将Object类型转化成BigDecimal类型
                            BigDecimal val = (BigDecimal) value;
                            //判断该值对应的成员属性的类型
                            if("int".equals(f.getType().getName())){
                                //将获取到的值转化成整型保存到成员属性中
                                f.set(obj,val.intValue());
                            }else{
                                //将获取到的值转化成浮点型保存到成员属性中
                                f.set(obj,val.doubleValue());
                            }
                        }else if (value instanceof Timestamp){
                            Timestamp val = (Timestamp) value;
                            java.sql.Date date = new java.sql.Date(val.getTime());
                            f.set(obj,date);
                        }else{
                            f.set(obj,value);
                        }
                    }
                }
                return obj;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DbUtils.close(conn,ps,rs);
        }
        return null;
    }

    /**
     * 查询多个数据
     * @param sql SQL语句
     * @param cls 保存查询数据的对象
     * @param params 可变参数
     *               类似于数组  保存SQL语句中占位符对应的参数
     * @param <T>  返回值类型
     * @return
     *         返回查询的结果对象
     */
    public static <T> List<T> baseQuery(String sql, Class<T> cls, Object...params){
        List<T> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取数据库连接通道对象
            conn = DbUtils.getConnection();
            //获取执行SQL语句的对象 PreparedStatement
            ps = conn.prepareStatement(sql);
            //对SQL语句中的占位符进行一一赋值（将SQL语句中占位符对应的参数保存到ps对象中，然后到了数据库服务器端
            // 再将其拼接成完整的SQL语句）
            if(params!=null){
                for (int i=0;i<params.length;i++){
                    ps.setObject(i+1,params[i]);
                }
            }
            //执行SQL语句 返回结果集
            rs = ps.executeQuery();
            //获取结果集对应的元数据（即结果集的描述）
            ResultSetMetaData rsmd = rs.getMetaData();
            //获取结果集中的总列数
            int columnCount = rsmd.getColumnCount();
            //获取结果集中的数据
            while(rs.next()){
                //创建一个对象，用于保存查询出来的数据
                T obj =  cls.newInstance();
                //rs.next()执行后，rs就会判断其当前指针所指向的行是否有下一行数据，如果没有则返回false
                //如果有，则返回true，并将指针指向下一行数据及将下一行数据保存到rs对象中
                //通过rs对象中的方法获取数据
                for (int i=0;i<columnCount;i++){
                    //获取结果集中每一列对应的列的名称，并将其全部转化成小写的
                    String columnName = rsmd.getColumnName(i+1).toLowerCase();
                    //根据列名获取结果集中每一列对应的值
                    Object value = rs.getObject(columnName);
                    //如果value值为空的话，则没有必要执行下面赋值的过程，执行跳出当前循环执行下一次循环
                    if(value == null){
                        continue;
                    }
                    //判断在cls对象中是否存在与columnName对应的成员属性
                    if (hasField(cls,columnName)){
                        //根据列名找到cls对象中对应的成员属性对象
                        Field f = cls.getDeclaredField(columnName);
                        //设置私有的成员属性允许访问的权限
                        f.setAccessible(true);
                        //判断value是否属于BigDecimal类型
                        if(value instanceof BigDecimal){
                            //将Object类型转化成BigDecimal类型
                            BigDecimal val = (BigDecimal) value;
                            //判断该值对应的成员属性的类型
                            if("int".equals(f.getType().getName())){
                                //将获取到的值转化成整型保存到成员属性中
                                f.set(obj,val.intValue());
                            }else{
                                //将获取到的值转化成浮点型保存到成员属性中
                                f.set(obj,val.doubleValue());
                            }
                        }else if (value instanceof Timestamp){
                            Timestamp val = (Timestamp) value;
                            java.sql.Date date = new java.sql.Date(val.getTime());
                            f.set(obj,date);
                        }else{
                            f.set(obj,value);
                        }
                    }
                }
                list.add(obj);
            }
            return  list;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DbUtils.close(conn,ps,rs);
        }
        return null;
    }

    /**
     * 判断cls对象中是否存在与结果集中对应的成员属性
     * @param cls   cls对象
     * @param columnName  结果集中的列的名称
     * @return
     *      true  存在
     *      false  不存在
     */
    private static boolean hasField(Class cls, String columnName) {
        //获取cls对象中所有的成员属性 返回其对应的集合
        Field[] fields = cls.getDeclaredFields();
        //循环取出这些成员属性判断其名称
        for (Field f : fields) {
            //判断结果集中的列名在cls对象中是否存在对应的成员属性
            if (f.getName().equals(columnName)){
                return true;
            }
        }
        return false;
    }

    //SQL语句的前缀部分
    private static final String PAGE_START_SQL = " select t2.* from (select t1.*,rownum num from (";
    //SQL语句的结束部分
    private static final String PAGE_END_SQL = " ) t1 where rownum<=?) t2 where num>=?";
    /**
     * 分页查询的方法
     * @param querySql  查询语句
     * @param countSql  统计记录数的SQL语句
     * @param whereSql  查询条件
     * @param otherSql  排序语句
     * @param cls       查询返回的对象的class对象
     * @param currentPage  分页的当前页面
     * @param pageSize     分页的每页显示的记录数
     * @param params   SQL语句中占位符对应的参数
     * @return
     * 		返回PageModel对象实例
     */
    public static <T> PageModel queryPageModel(StringBuffer querySql,
                                               StringBuffer countSql, StringBuffer whereSql, StringBuffer otherSql, Class<T> cls,
                                               int currentPage, int pageSize, List params) {
        //拼接统计总记录数据的SQL语句
        countSql.append(whereSql);
        //获取总记录数
        int totalCount = queryForCount(countSql.toString(),params.toArray());
        //计算开始下标和结束下标
        int startIndex = (currentPage-1)*pageSize+1;
        int endIndex = currentPage*pageSize;

        //拼接查询SQL语句，用于查询分页的结果集
        querySql.append(whereSql).append(otherSql);  //拼接查询条件 where的子句
        //第一个参数表示将字符串插入到哪个位置，第二个参数表示的是将要插入的字符串
        querySql.insert(0, PAGE_START_SQL);//将开始的SQL语句插入到querySql前面
        querySql.append(PAGE_END_SQL); //将结束的SQL语句拼接到querySql末尾位置
		/*String sql = "select t2.* from (select t1.*,rownum num from t_mc t1 "
				+ "where rownum<=?) t2 where num>=?";*/
        //添加占位符的参数，注意顺序不要出错
        params.add(endIndex);
        params.add(startIndex);
        //获取分页的数据
        List<T> list = baseQuery(querySql.toString(),cls,params.toArray());
        //保存分页的数据
        PageModel pageModel = new PageModel();
        pageModel.setCurrentPage(currentPage);
        pageModel.setPageSize(pageSize);
        pageModel.setTotalCount(totalCount);
        pageModel.setResult(list);
        return pageModel;
    }
}
