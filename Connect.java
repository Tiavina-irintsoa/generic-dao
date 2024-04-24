package connexion;
import java.sql.*;

public class Connect {
    Connection con;
    Statement stat;

    public void getConnectOracle() throws Exception{
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // String url = "jdbc:oracle:thin:@ETU1906-Ralph:1521:XE";
            String url = "jdbc:oracle:thin:@localhost:1521:TITA";
            System.out.println(" con "+con);
            this.con = DriverManager.getConnection(url, "rencontre", "rencontre");
            System.out.println(" con "+con);
            stat = con.createStatement();

        } catch (Exception e) {
            System.out.println( "error : "+e.getMessage() );
            e.printStackTrace();
            throw new Exception("Connexion interrompue");
        }
    }
    public Statement getStat()
    {
        return stat;
    }
    public void getConnectionPostGresql() throws Exception{
        try{
        Class dbDriver = Class.forName("org.postgresql.Driver");
        String jdbcURL = "jdbc:postgresql://localhost:5432/framework";
        con = DriverManager.getConnection(jdbcURL, "postgres", "root");
        // stat = con.createStatement();
        this.getConnection().setAutoCommit(false);
        // return con;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new Exception("Connexion interrompue");
        }
    }
    public Connection getConnection(){
        return this.con;
    }

    
}
