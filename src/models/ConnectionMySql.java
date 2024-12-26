
package models;

//importar la conexion
import java.sql.Connection;
//import el driver
import java.sql.DriverManager;

import java.sql.SQLException;

//archivo con la conexion
public class ConnectionMySql {
    private String database_name = "pharmacy_database";
    private String user = "root";
    private String password = "1234";
    private String url = "jdbc:mysql://localhost:3306/"+ database_name;
    Connection conn = null;
    
    //metodo para conectar java con mysql
    public Connection getConnection(){
        try{
            //obtener valores del driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            //obtener la conexion
            conn = DriverManager.getConnection(url, user, password);
            
        }catch(ClassNotFoundException e){
            System.err.println("ha ocurrido un error"+ e.getMessage());
        }catch(SQLException e){
          System.err.println("erro sql"+ e.getMessage());
        }
        return conn;
    }
    
}
