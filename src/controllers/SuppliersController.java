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
import models.DynamikCombobox;
import static models.EmployeesDao.rol_user;
import models.SupplierDao;
import models.Suppliers;
import views.Systemview;

public class SuppliersController implements ActionListener, MouseListener, KeyListener {

    private Suppliers supplier;
    private SupplierDao supplierDao;
    private Systemview views;
    String rol = rol_user;

    //modelo para interactuar con la tabla de empleados
    DefaultTableModel model = new DefaultTableModel();

    public SuppliersController(Suppliers supplier, SupplierDao supplierDao, Systemview views) {
        this.supplier = supplier;
        this.supplierDao = supplierDao;
        this.views = views;
        //btn registrar proveedor
        views.btn_register_supplier.addActionListener(this);
        //btn registrar proveedor
        views.btn_update_supplier.addActionListener(this);
        //btn eliminar proveedor
        views.btn_delete_supplier.addActionListener(this);
        //btn cancel
        views.btn_cancel_supplier.addActionListener(this);

        //tabla, escuchar event
        this.views.suppliers_table.addMouseListener(this);
        //buscador
        this.views.txt_search_supplier.addKeyListener(this);
        //menu,btn proveedor
        this.views.jLabelSuppliers.addMouseListener(this);
        getSupplierName();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //registrar
        if (e.getSource() == views.btn_register_supplier) {
            if (views.txt_supplier_name.getText().isEmpty()
                    || views.txt_supplier_description.getText().isEmpty()
                    || views.txt_supplier_address.getText().isEmpty()
                    || views.txt_supplier_telephone.getText().isEmpty()
                    || views.txt_supplier_email.getText().isEmpty()
                    || views.cmb_supplier_city.getSelectedItem().toString().isEmpty()) {

                JOptionPane.showMessageDialog(null, "todos los campos son obligatorios");
            } else {
                supplier.setName(views.txt_supplier_name.getText().trim());
                supplier.setDescription(views.txt_supplier_description.getText().trim());
                supplier.setAddress(views.txt_supplier_address.getText().trim());
                supplier.setTelephone(views.txt_supplier_telephone.getText().trim());
                supplier.setEmail(views.txt_supplier_email.getText().trim());
                supplier.setCity(views.cmb_supplier_city.getSelectedItem().toString());

                if (supplierDao.registerSupplierQuery(supplier)) {
                    cleanTable();
                    listAllSuppliers();
                    cleanFields();
                    JOptionPane.showMessageDialog(null, "proveedor registrado con exito");
                } else {
                    JOptionPane.showMessageDialog(null, "ha ocurrido un error al registrar al proveedor");
                }

            }
        } else if (e.getSource() == views.btn_update_supplier) {
            if (views.txt_supplier_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");

            } else {
                if (views.txt_supplier_name.getText().trim().isEmpty()
                        || views.txt_supplier_address.getText().trim().isEmpty()
                        || views.txt_supplier_telephone.getText().trim().isEmpty()
                        || views.txt_supplier_email.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(null, "los campos nombre,direccion,telefono y correo son obligatorios");

                } else {
                    supplier.setName(views.txt_supplier_name.getText().trim());
                    supplier.setDescription(views.txt_supplier_description.getText().trim());
                    supplier.setAddress(views.txt_supplier_address.getText().trim());
                    supplier.setTelephone(views.txt_supplier_telephone.getText().trim());
                    supplier.setEmail(views.txt_supplier_email.getText().trim());
                    supplier.setCity(views.cmb_supplier_city.getSelectedItem().toString());
                    supplier.setId(Integer.parseInt(views.txt_supplier_id.getText()));

                    if (supplierDao.updateSuppliersQuery(supplier)) {
                        cleanTable();
                        cleanFields();
                        listAllSuppliers();
                        views.btn_register_supplier.setEnabled(true);

                        JOptionPane.showMessageDialog(null, "datos del proveedor actualizado con exito");
                    } else {
                        JOptionPane.showMessageDialog(null, "error a actualizar los datos del proveedor");
                    }

                }
            }
        } else if (e.getSource() == views.btn_delete_supplier) {
            int row = views.suppliers_table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar a un proveedor para poder eliminarlo");
            } else {
                int id = Integer.parseInt(views.suppliers_table.getValueAt(row, 0).toString());

                int question = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres eliminar al proveedor?");

                if (question == 0 && supplierDao.deleteSuppliersQuery(id)) {
                    cleanTable();
                    cleanFields();
                    listAllSuppliers();
                    JOptionPane.showMessageDialog(null, "Proveedor eliminado con exito");
                }

            }
        } else if (e.getSource() == views.btn_cancel_supplier) {
            cleanFields();
            views.btn_register_supplier.setEnabled(true);

        }

    }

    public void listAllSuppliers() {
        if (rol.equals("Administrador")) {
            List<Suppliers> list = supplierDao.listSuppliersQuery(views.txt_search_supplier.getText().trim());

            if (list == null || list.isEmpty()) {
                // Manejar caso de lista vacía
                return;
            }

            // Obtener modelo de tabla
            DefaultTableModel model = (DefaultTableModel) views.suppliers_table.getModel();
            // Limpiar tabla antes de cargar nuevos datos
            model.setRowCount(0);

            list.forEach(supplier -> model.addRow(new Object[]{
                supplier.getId(),
                supplier.getName(),
                supplier.getDescription(),
                supplier.getAddress(),
                supplier.getTelephone(),
                supplier.getEmail(),
                supplier.getCity()

            })
            );

        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.suppliers_table) {
            int row = views.suppliers_table.rowAtPoint(e.getPoint());
            views.txt_supplier_id.setText(views.suppliers_table.getValueAt(row, 0).toString());
            views.txt_supplier_name.setText(views.suppliers_table.getValueAt(row, 1).toString());
            views.txt_supplier_description.setText(views.suppliers_table.getValueAt(row, 2).toString());
            views.txt_supplier_address.setText(views.suppliers_table.getValueAt(row, 3).toString());
            views.txt_supplier_telephone.setText(views.suppliers_table.getValueAt(row, 4).toString());
            views.txt_supplier_email.setText(views.suppliers_table.getValueAt(row, 5).toString());
            views.cmb_supplier_city.setSelectedItem(views.suppliers_table.getValueAt(row, 6).toString());

            //deshabilitar btn
            views.btn_register_supplier.setEnabled(false);
            views.txt_supplier_id.setEditable(false);

        } else if (e.getSource() == views.jLabelSuppliers) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane.setSelectedIndex(5);
                cleanTable();
                cleanFields();
                listAllSuppliers();
            } else {
                views.jTabbedPane.setEnabledAt(5, false);
                views.jLabelSuppliers.setEnabled(false);
                JOptionPane.showMessageDialog(null, "no cuentas con permisos suficiente para acceder a esta vista");
            }
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
        if (e.getSource() == views.txt_search_supplier) {
            cleanTable();
            listAllSuppliers();
        }

    }

    //limpiar campos
    public void cleanFields() {
        views.txt_supplier_id.setText("");
        views.txt_supplier_name.setText("");
        views.txt_supplier_description.setText("");
        views.txt_supplier_address.setText("");
        views.txt_supplier_telephone.setText("");
        views.txt_supplier_email.setText("");
        views.cmb_supplier_city.setSelectedIndex(0);
    }

    //limpiar tabla
    public void cleanTable() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
            i = i - 1;
        }

    }
    //mostrar el nombre del proveedor
    public void getSupplierName(){
        List<Suppliers> list = supplierDao.listSuppliersQuery(views.txt_search_supplier.getText());
        for (int i = 0; i < list.size(); i++){
            int id = list.get(i).getId();
            String name = list.get(i).getName();
            views.cmb_purchase_supplier.addItem(new DynamikCombobox(id, name));
        }
    }
    
    
    

}
