package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

public class EmployeesDao {

    //instanciar la conexion
    ConnectionMySql cn = new ConnectionMySql();
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    //variables para enviar datoos entre interfaces
    public static int id_user = 0;
    public static String full_name_user = "";
    public static String username_user = "";
    public static String address_user = "";
    public static String telephone_user = "";
    public static String email_user = "";
    public static String rol_user = "";

    //login
    public Employees loginQuery(String user, String password) {
        String query = "SELECT * FROM employees WHERE username = ?";
        Employees employee = null;
        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            //enviar parametros
            pst.setString(1, user);
            //ejecutar consulta
            rs = pst.executeQuery();

            //si la informacion que envia el usuario conside con el registro de la bdd,se guardar los datos recibidos.
            if (rs.next()) {
                employee = new Employees();
                employee.setId(rs.getInt("id"));
                id_user = employee.getId();
                employee.setFull_name(rs.getString("full_name"));
                full_name_user = employee.getFull_name();
                employee.setUsername(rs.getString("username"));
                username_user = employee.getUsername();
                employee.setAddress(rs.getString("address"));
                address_user = employee.getAddress();
                employee.setTelephone(rs.getString("telephone"));
                telephone_user = employee.getTelephone();
                employee.setEmail(rs.getString("email"));
                email_user = employee.getEmail();
                employee.setPassword(rs.getString("password"));
                employee.setRol(rs.getString("rol"));
                rol_user = employee.getRol();

            }
            // Verificar si la contraseña ingresada coincide con la hasheada
            if (BCrypt.checkpw(password, employee.getPassword())) {
                // La contraseña es correcta, puedes devolver el objeto empleado
                return employee;
            } else {
                // Contraseña incorrecta
                return null;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "error al obtener el empleado " + e);
        }
        return employee;
    }

    //registrar empleado
    public boolean registerEmployeeQuery(Employees employee) {
        String query = "INSERT INTO employees(id, full_name, username, address, telephone, email, password, rol, created,"
                + "updated) VALUES(?,?,?,?,?,?,?,?,?,?)";

        Timestamp datetime = new Timestamp(new Date().getTime());

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setInt(1, employee.getId());
            pst.setString(2, employee.getFull_name());
            pst.setString(3, employee.getUsername());
            pst.setString(4, employee.getAddress());
            pst.setString(5, employee.getTelephone());
            pst.setString(6, employee.getEmail());
            // Hashear la contraseña antes de almacenarla
            String hashedPassword = BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt());
            pst.setString(7, hashedPassword); // Almacenar la contraseña hasheada

            pst.setString(8, employee.getRol());
            pst.setTimestamp(9, datetime);
            pst.setTimestamp(10, datetime);
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar al empleado" + e);
            return false;
        }
    }

    //listar empleado o un empleado en particular
    public List listEmployeeQuery(String value) {
        List<Employees> list_employees = new ArrayList<>();
        String query = "SELECT * FROM employees ORDER BY id ASC";
        String query_search_employee = "SELECT * FROM employees WHERE id LIKE '%" + value + "%'"; //se busca un  empleadosegun el valor ingresado en la vista(buscador)

        try {
            conn = cn.getConnection();
            if (value.trim().isEmpty()) {
                pst = conn.prepareStatement(query);
                rs = pst.executeQuery();
            } else {
                pst = conn.prepareStatement(query_search_employee);
                rs = pst.executeQuery();
            }

            //recorrer registros mientras encuentre resultado
            while (rs.next()) {
                Employees employee = new Employees();
                employee.setId(rs.getInt("id"));
                employee.setFull_name(rs.getString("full_name"));
                employee.setUsername(rs.getString("username"));
                employee.setAddress(rs.getString("address"));
                employee.setTelephone(rs.getString("telephone"));
                employee.setEmail(rs.getString("email"));
                employee.setRol(rs.getString("rol"));
                list_employees.add(employee);//agregramos la info a la lista
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return list_employees;
    }

//modificar empleado
    public boolean updateEmployeeQuery(Employees employee) {
        String query = "UPDATE employees SET full_name = ?, username = ?, address = ?, telephone = ?, email = ? , rol = ?, updated = ? "
                + " WHERE id = ?";

        Timestamp datetime = new Timestamp(new Date().getTime());

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, employee.getFull_name());
            pst.setString(2, employee.getUsername());
            pst.setString(3, employee.getAddress());
            pst.setString(4, employee.getTelephone());
            pst.setString(5, employee.getEmail());
            pst.setString(6, employee.getRol());
            pst.setTimestamp(7, datetime);
            pst.setInt(8, employee.getId());
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar al empleado" + e);
            return false;
        }
    }

    //eliminar empleado
    public boolean deleteEmployeeQuery(int id) {
        String query = "DELETE FROM employees WHERE id = " + id;

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar al empleado");
            return false;
        }
    }

    //cambio de la contraseña
    public boolean updateEmployeePassword(Employees employee) {
        String query = "UPDATE employees SET password = ? WHERE username = '" + username_user + "'";

        try {
            conn = cn.getConnection();
            pst = conn.prepareStatement(query);
            pst.setString(1, employee.getPassword());
            pst.execute();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "error al modificar la contraseña" + e);
            return false;
        }
    }

}