import javax.swing.*;
import java.sql.*;

public class GetDBConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/supermarket?useUnicode=true&characterEncoding=gb2312&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String USER = "root";
    // 改成你自己的MySQL密码
    private static final String PASSWORD = "123456";

    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("数据库连接成功！");
        } catch (ClassNotFoundException e) {
            String msg = "错误：JDBC驱动缺失，请检查mysql-connector-j jar包是否导入项目！";
            JOptionPane.showMessageDialog(null, msg, "数据库连接异常", JOptionPane.ERROR_MESSAGE);
            System.err.println(msg);
            e.printStackTrace();
        } catch (SQLException e) {
            String msg = "错误：无法连接MySQL服务！\n请检查：1.MySQL是否启动 2.账号密码是否正确";
            JOptionPane.showMessageDialog(null, msg + "\n详情：" + e.getMessage(), "数据库连接异常", JOptionPane.ERROR_MESSAGE);
            System.err.println(msg);
            e.printStackTrace();
        }
        return con;
    }

    public static void close(Connection con, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}