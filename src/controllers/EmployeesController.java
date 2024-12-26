package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Employees;
import models.EmployeesDao;
import static models.EmployeesDao.rol_user;
import views.Systemview;

public class EmployeesController implements ActionListener, MouseListener, KeyListener {

    private Employees employee;
    private EmployeesDao employeesDao;
    private Systemview views;
    String rol = rol_user;

    //modelo para interactuar con la tabla de empleados
    DefaultTableModel model = new DefaultTableModel();

    public EmployeesController(Employees employee, EmployeesDao employeesDao, Systemview views) {
        this.employee = employee;
        this.employeesDao = employeesDao;
        this.views = views;
        //registrar empleado
        this.views.btn_register_employee.addActionListener(this);
        //modificar empleado
        this.views.btn_update_employee.addActionListener(this);
        //eliminar empleado
        this.views.btn_delete_employee.addActionListener(this);
        //escuchar el event de boton cancelar
        this.views.btn_cancel_employee.addActionListener(this);
        //escuchar el event para cambiar el password
        this.views.btn_modify_data.addActionListener(this);
        //label(employees) a escuchar event
        this.views.jLabelEmployees.addMouseListener(this);

        //se le pasa el event a la tabla
        this.views.employees_table.addMouseListener(this);
        //evento de teclado en el search empleado
        this.views.txt_search_employees.addKeyListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_register_employee) {
            if (views.txt_employee_id.getText().equals("")
                    || views.txt_employee_fullname.getText().equals("")
                    || views.txt_employee_username.getText().equals("")
                    || views.txt_employee_address.getText().equals("")
                    || views.txt_employee_telephone.getText().equals("")
                    || views.txt_employee_email.getText().equals("")
                    || views.cmb_rol.getSelectedItem().toString().equals("")
                    || String.valueOf(views.txt_employee_password.getPassword()).equals("")) {

                JOptionPane.showMessageDialog(null, "todos los campos son obligatorios");
            } else {
                //si lo campo no estan vacio,se realiza la insercion de datos
                employee.setId(Integer.parseInt(views.txt_employee_id.getText().trim()));
                employee.setUsername(views.txt_employee_username.getText().trim());
                employee.setFull_name(views.txt_employee_fullname.getText().trim());
                employee.setAddress(views.txt_employee_address.getText().trim());
                employee.setTelephone(views.txt_employee_telephone.getText().trim());
                employee.setEmail(views.txt_employee_email.getText().trim());
                employee.setPassword(String.valueOf(views.txt_employee_password.getPassword()));

                employee.setRol(views.cmb_rol.getSelectedItem().toString());

                if (employeesDao.registerEmployeeQuery(employee)) {
                    cleanTable();
                    cleanFields();
                    listAllEmployees();
                    JOptionPane.showMessageDialog(null, "empleado registrado exitosamente");
                } else {
                    JOptionPane.showMessageDialog(null, "ha ocurrido un error al registrar el empleado");
                }
            }
        } else if (e.getSource() == views.btn_update_employee) {
            // Primero, verifica si se ha seleccionado una fila
            if (views.txt_employee_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");
                return; // Salir del método si no hay ID seleccionado
            }

            // Verificar campos obligatorios
            if (views.txt_employee_fullname.getText().trim().isEmpty()
                    || views.txt_employee_username.getText().trim().isEmpty()
                    || views.cmb_rol.getSelectedItem().toString().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Los campos de nombre completo, nombre de usuario y rol son obligatorios");
                return; // Salir del método si faltan campos obligatorios
            }

            // Validar campos antes de guardar
            try {
                employee.setId(Integer.parseInt(views.txt_employee_id.getText().trim()));
                employee.setUsername(views.txt_employee_username.getText().trim());
                employee.setFull_name(views.txt_employee_fullname.getText().trim());
                employee.setAddress(views.txt_employee_address.getText().trim());
                employee.setTelephone(views.txt_employee_telephone.getText().trim());
                employee.setEmail(views.txt_employee_email.getText().trim());
                employee.setRol(views.cmb_rol.getSelectedItem().toString());

                if (employeesDao.updateEmployeeQuery(employee)) {
                    cleanTable();
                    cleanFields();
                    listAllEmployees();
                    views.btn_register_employee.setEnabled(true);
                    JOptionPane.showMessageDialog(null, "Datos del empleado modificados exitosamente");
                } else {
                    JOptionPane.showMessageDialog(null, "Ha ocurrido un error al modificar al empleado");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error: El ID del empleado debe ser un número válido");
            }

            //boton delete
        } else if (e.getSource() == views.btn_delete_employee) {
            int row = views.employees_table.getSelectedRow();
            int id = Integer.parseInt(views.employees_table.getValueAt(row, 0).toString());
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un empleado para eliminar");
            } else if (views.employees_table.getValueAt(row, 0).equals(id)) {
                JOptionPane.showMessageDialog(null, "no puede eliminar al usuario autenticado");
            } else {

                int question = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres eliminar al empleado?");

                if (question == 0 && employeesDao.deleteEmployeeQuery(id) != false) {
                    cleanTable();
                    cleanFields();
                    views.btn_register_employee.setEnabled(true);
                    views.txt_employee_password.setEnabled(true);
                    listAllEmployees();
                    JOptionPane.showMessageDialog(null, "Empleado eliminado con exito");
                }

            }

        } else if (e.getSource() == views.btn_cancel_employee) {
            cleanTable();
            cleanFields();
            views.btn_register_employee.setEnabled(true);
            views.txt_employee_password.setEnabled(true);
            views.txt_employee_id.setEditable(true);

            //modificar password 
        } else if (e.getSource() == views.btn_modify_data) {
            //recibir la data de las cajas de texto
            String password = String.valueOf(views.txt_password_modify.getPassword());
            String confirm_password = String.valueOf(views.txt_password_modify_confirm.getPassword());
            //verificar si las cajas de texto estan vacias
            if (!password.equals("") && !confirm_password.equals("")) {
                //verificar si son iguales las contraseñas
                if (password.equals(confirm_password)) {
                    employee.setPassword(String.valueOf(views.txt_password_modify.getPassword()));
                    if (employeesDao.updateEmployeePassword(employee) != false) {
                        JOptionPane.showMessageDialog(null, "Contraseña modificada con éxito");
                    } else {
                        JOptionPane.showMessageDialog(null, "Ha ocurrido un error al modificar la contraseña");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "La contraseña no coincide");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            }
        }
    }

    //listar todos los empleados
    public void listAllEmployees() {
        if (rol.equals("Administrador")) {
            List<Employees> list = employeesDao.listEmployeeQuery(views.txt_search_employees.getText().trim());

            if (list == null || list.isEmpty()) {
                // Manejar caso de lista vacía
                return;
            }
            // Obtener modelo de tabla
            DefaultTableModel model = (DefaultTableModel) views.employees_table.getModel();
            // Limpiar tabla antes de cargar nuevos datos
            model.setRowCount(0);

            list.forEach(employee
                    -> model.addRow(new Object[]{
                employee.getId(),
                employee.getFull_name(),
                employee.getUsername(),
                employee.getAddress(),
                employee.getTelephone(),
                employee.getEmail(),
                employee.getRol()
            })
            );

        }
    }

    @Override
    public void mouseClicked(MouseEvent e
    ) {
        if (e.getSource() == views.employees_table) {
            int row = views.employees_table.rowAtPoint(e.getPoint());
            views.txt_employee_id.setText(views.employees_table.getValueAt(row, 0).toString());
            views.txt_employee_fullname.setText(views.employees_table.getValueAt(row, 1).toString());
            views.txt_employee_username.setText(views.employees_table.getValueAt(row, 2).toString());
            views.txt_employee_address.setText(views.employees_table.getValueAt(row, 3).toString());
            views.txt_employee_telephone.setText(views.employees_table.getValueAt(row, 4).toString());
            views.txt_employee_email.setText(views.employees_table.getValueAt(row, 5).toString());
            views.cmb_rol.setSelectedItem(views.employees_table.getValueAt(row, 6).toString());
            //Deshabilitar
            views.txt_employee_id.setEditable(false);
            views.txt_employee_password.setEnabled(false);
            views.btn_register_employee.setEnabled(false);

        } else if (e.getSource() == views.jLabelEmployees) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane.setSelectedIndex(4);
                //limpiar la tabla
                cleanTable();
                //limpoiar campos
                cleanFields();
                //listar empleados
                listAllEmployees();
            } else {
                views.jTabbedPane.setEnabledAt(4, false);
                views.jLabelEmployees.setEnabled(false);
                JOptionPane.showMessageDialog(null, "no tienes permiso parta acceder a esta vista");

            }
        }

    }

    @Override
    public void mousePressed(MouseEvent e
    ) {

    }

    @Override
    public void mouseReleased(MouseEvent e
    ) {

    }

    @Override
    public void mouseEntered(MouseEvent e
    ) {

    }

    @Override
    public void mouseExited(MouseEvent e
    ) {

    }

    @Override
    public void keyTyped(KeyEvent e
    ) {

    }

    @Override
    public void keyPressed(KeyEvent e
    ) {

    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
        if (e.getSource() == views.txt_search_employees) {
            cleanTable();
            listAllEmployees();
        }
    }

    //limpiar campos
    public void cleanFields() {
        views.txt_employee_id.setText("");
        views.txt_employee_id.setEditable(true);
        views.txt_employee_fullname.setText("");
        views.txt_employee_username.setText("");
        views.txt_employee_address.setText("");
        views.txt_employee_telephone.setText("");
        views.txt_employee_email.setText("");
        views.txt_employee_password.setText("");
        views.cmb_rol.setSelectedIndex(0);

    }

    //limpiar tabla
    public void cleanTable() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
            i = i - 1;
        }

    }

}
