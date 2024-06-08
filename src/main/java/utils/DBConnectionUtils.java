package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionUtils {
    private static Connection conn;
    public static Connection getConnection(){
        if(DBConnectionUtils.conn==null){
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String dbUser="sa",dbPass="1234",dbUrl="jdbc:sqlserver://localhost:1433"+";databaseName=QL_BAI_DO_XE.sql";
                DBConnectionUtils.conn=DriverManager.getConnection(dbUrl, dbUser, dbPass);
                System.out.println("Kết nối thành công");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return conn;
    }
    public static void main(String[] args) {
        getConnection();
    }
}
