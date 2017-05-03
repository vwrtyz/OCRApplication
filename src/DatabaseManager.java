/**
 *
 * @author user
 */
import java.sql.*;


public class DatabaseManager {
    static final String JDBC_DRIVER ="com.mysql.jdbc.Driver";
    static final String DB_URL ="jdbc:mysql://localhost/translator";
    private Connection con; //dipakai untuk open dan close koneksinya
    private Statement st; //dipakai untuk ngatur statement querynya
    public DatabaseManager(){
        try{
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to the database...");
            con=DriverManager.getConnection(DB_URL,"root","");
        }catch(Exception ex){
            System.out.println("Error: "+ex.getMessage());
        }
    }
    public ResultSet queryResult(String query){
//        System.out.println("Creating statement...");
        try{
            st=con.createStatement();
            ResultSet rs=st.executeQuery(query);
            return rs;
        }catch(Exception ex){
            System.out.println("Error: "+ex.getMessage());
             return null;
        }
    }
    
    public int executeInsertOrUpdate(String query)
    {
//        System.out.println("Creating statement");
        try{
            st = con.createStatement();
            int status = st.executeUpdate(query);
            return status;
            
        }catch(Exception ex)
        {
            System.out.println("Error: "+ex.getMessage());
            return 0;
        }
    }
}

