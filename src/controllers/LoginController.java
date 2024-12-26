package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import models.Employees;
import models.EmployeesDao;
import views.LoginView;
import views.Systemview;

public class LoginController implements ActionListener {

    private Employees employee;
    private EmployeesDao employees_dao;
    private LoginView login_view;

    public LoginController(Employees employee, EmployeesDao employees_dao, LoginView login_view) {
        this.employee = employee;
        this.employees_dao = employees_dao;
        this.login_view = login_view;
        //especificar el evento en el constructor
        this.login_view.btn_enter.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //obtener los datos enviados de la vista
        String user = login_view.txt_username.getText().trim();
        String pass = String.valueOf(login_view.txt_password.getPassword());

        //chequeamos si en la vista se presiono el boton
        if (e.getSource() == login_view.btn_enter) {
            //validar campos vacios
            if (!user.isEmpty() && !pass.isEmpty()) {
                //pasar los parametros al metodo login
                employee = employees_dao.loginQuery(user, pass);
                //verificar existencia del usuario
                if (employee.getUsername() != null) {
                    if (employee.getRol().equals("Administrador")) {
                        Systemview admin = new Systemview();
                        admin.setVisible(true);
                    } else {
                        Systemview aux = new Systemview();
                        aux.setVisible(true);
                    }
                    this.login_view.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "usuario o contraseña incorrecta");
                }

            } else {
                JOptionPane.showMessageDialog(null, "los campos estan vacios");
            }
        }

    }

}
