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
import models.Customers;
import models.CustomersDao;
import views.Systemview;

public class CustomersController implements ActionListener, MouseListener, KeyListener {

    private Customers customer;
    private CustomersDao customerDao;
    private Systemview views;

    //modelo para interactuar con la tabla de clientes
    DefaultTableModel model = new DefaultTableModel();

    public CustomersController(Customers customer, CustomersDao customerDao, Systemview views) {
        this.customer = customer;
        this.customerDao = customerDao;
        this.views = views;
        //btn registrar cliente
        this.views.btn_register_customer.addActionListener(this);
        //btn modificar cliente
        this.views.btn_update_costumer.addActionListener(this);
        //btn eliminar cliente
        this.views.btn_delete_customer.addActionListener(this);
        //btn cancelar
        this.views.btn_cancel_customer.addActionListener(this);
        //la tabla escucha event
        this.views.customers_table.addMouseListener(this);
        //buscador
        this.views.txt_search_customer.addKeyListener(this);
        this.views.jLabelCostumers.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_register_customer) {
            if (views.txt_customer_id.getText().isEmpty()
                    || views.txt_customer_fullname.getText().isEmpty()
                    || views.txt_customer_telephone.getText().isEmpty()
                    || views.txt_customer_address.getText().isEmpty()
                    || views.txt_customer_email.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            } else {
                customer.setId(Integer.parseInt(views.txt_customer_id.getText().trim()));
                customer.setFull_name(views.txt_customer_fullname.getText().trim());
                customer.setTelephone(views.txt_customer_telephone.getText().trim());
                customer.setAddress(views.txt_customer_address.getText().trim());
                customer.setEmail(views.txt_customer_email.getText().trim());

                if (customerDao.registerCostumerQuery(customer)) {
                    cleanTable();
                    listAllCustomers();
                    JOptionPane.showMessageDialog(null, "cliente registrado exitosamente");
                } else {
                    JOptionPane.showMessageDialog(null, "ha ocurrido un error al registrar al cliente");
                }

            }
            //modificar cliente
        } else if (e.getSource() == views.btn_update_costumer) {
            if (views.txt_customer_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");
            } else {
                if (views.txt_customer_id.getText().isEmpty()
                        || views.txt_customer_fullname.getText().isEmpty()
                        || views.txt_customer_telephone.getText().isEmpty()
                        || views.txt_customer_address.getText().isEmpty()
                        || views.txt_customer_email.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(null, "los campos son obligatorios");
                } else {
                    customer.setId(Integer.parseInt(views.txt_customer_id.getText().trim()));
                    customer.setFull_name(views.txt_customer_fullname.getText().trim());
                    customer.setTelephone(views.txt_customer_telephone.getText().trim());
                    customer.setAddress(views.txt_customer_address.getText().trim());
                    customer.setEmail(views.txt_customer_email.getText().trim());

                    if (customerDao.updateCostumerQuery(customer)) {
                        cleanTable();
                        cleanFields();
                        listAllCustomers();
                        views.btn_register_customer.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "cliente modificado con exito");
                    } else {
                        JOptionPane.showMessageDialog(null, "ha ocurrido un error, no se pudo modificar el cliente");

                    }
                }

            }
            //eliminar cliente

        } else if (e.getSource() == views.btn_delete_customer) {
            int row = views.customers_table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un cliente para eliminar");
            } else {
                int id = Integer.parseInt(views.customers_table.getValueAt(row, 0).toString());
                int question = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres eliminar al cliente?");

                if (question == 0 && customerDao.deleteCustomerQuery(id) != false) {
                    cleanTable();
                    cleanFields();
                    views.btn_register_customer.setEnabled(true);
                    listAllCustomers();
                    JOptionPane.showMessageDialog(null, "cliente eliminado con exito");
                }

            }
        } else if (e.getSource() == views.btn_cancel_customer) {

            cleanFields();

        }

    }
    //listar clientes

    public void listAllCustomers() {
        List<Customers> list = customerDao.listCustomerQuery(views.txt_search_customer.getText());
        model = (DefaultTableModel) views.customers_table.getModel();
        // Limpiar tabla antes de cargar nuevos datos
        model.setRowCount(0);

        list.forEach(customer -> model.addRow(new Object[]{
            customer.getId(),
            customer.getFull_name(),
            customer.getTelephone(),
            customer.getAddress(),
            customer.getEmail()
        })
        );

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.customers_table) {
            int row = views.customers_table.rowAtPoint(e.getPoint());
            views.txt_customer_id.setText(views.customers_table.getValueAt(row, 0).toString());
            views.txt_customer_fullname.setText(views.customers_table.getValueAt(row, 1).toString());
            views.txt_customer_telephone.setText(views.customers_table.getValueAt(row, 2).toString());
            views.txt_customer_address.setText(views.customers_table.getValueAt(row, 3).toString());
            views.txt_customer_email.setText(views.customers_table.getValueAt(row, 4).toString());
            //deshabilitar botones
            views.btn_register_customer.setEnabled(false);
            views.txt_customer_id.setEditable(false);
        }else if(e.getSource() == views.jLabelCostumers){
            views.jTabbedPane.setSelectedIndex(3);
             //limpiar la tabla
                cleanTable();
                //limpoiar campos
                cleanFields();
                listAllCustomers();
        }
        
        
        
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == views.txt_search_customer) {
            cleanTable();
            listAllCustomers();

        }

    }

    //limpiar campos
    public void cleanFields() {
        views.txt_customer_id.setText("");
        views.txt_customer_id.setEditable(true);
        views.txt_customer_fullname.setText("");
        views.txt_customer_telephone.setText("");
        views.txt_customer_address.setText("");
        views.txt_customer_email.setText("");

    }

    //limpiar tabla
    public void cleanTable() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
            i = i - 1;
        }

    }

}
