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
import models.Products;
import models.ProductsDao;
import views.Systemview;

public class ProductsController implements ActionListener, MouseListener, KeyListener {

    private Products product;
    private ProductsDao productDao;
    private Systemview views;
    String rol = rol_user;
    DefaultTableModel model = new DefaultTableModel();

    public ProductsController(Products product, ProductsDao productDao, Systemview views) {
        this.product = product;
        this.productDao = productDao;
        this.views = views;
        //registrar producto
        this.views.btn_register_product.addActionListener(this);
        //actualizar producto
        this.views.btn_update_product.addActionListener(this);
        //eliminar producto
        this.views.btn_delete_product.addActionListener(this);
        //cancelar producto
        this.views.btn_cancel_product.addActionListener(this);
        this.views.products_table.addMouseListener(this);
        //buscador
        this.views.txt_search_product.addKeyListener(this);
        this.views.jLabelProducts.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_register_product) {
            if (views.txt_product_code.getText().isEmpty()
                    || views.txt_product_name.getText().isEmpty()
                    || views.txt_product_description.getText().isEmpty()
                    || views.txt_product_unit_price.getText().isEmpty()
                    || views.cmb_product_category.getSelectedItem().toString().isEmpty()) {
                JOptionPane.showMessageDialog(null, "todos los campos son  obligatorios");
            } else {
                product.setCode(Integer.parseInt(views.txt_product_code.getText()));
                product.setName(views.txt_product_name.getText().trim());
                product.setDescription(views.txt_product_description.getText().trim());
                product.setUnit_price(Double.parseDouble(views.txt_product_unit_price.getText()));
                DynamikCombobox category_id = (DynamikCombobox) views.cmb_product_category.getSelectedItem();
                product.setCategory_id(category_id.getId());

                if (productDao.registerProductQuery(product)) {
                    cleanTable();
                    cleanFields();
                    listAllProducts();
                    JOptionPane.showMessageDialog(null, "producto registrado con exito");
                } else {
                    JOptionPane.showMessageDialog(null, "ha ocurrido un error al registrar el producto");
                }

            }
        } else if (e.getSource() == views.btn_update_product) {
            if (views.txt_product_code.getText().isEmpty()
                    || views.txt_product_name.getText().isEmpty()
                    || views.txt_product_description.getText().isEmpty()
                    || views.txt_product_unit_price.getText().isEmpty()
                    || views.cmb_product_category.getSelectedItem().toString().isEmpty()) {

                JOptionPane.showMessageDialog(null, "todos los campos son  obligatorios");
            } else {
                product.setCode(Integer.parseInt(views.txt_product_code.getText()));
                product.setName(views.txt_product_name.getText().trim());
                product.setDescription(views.txt_product_description.getText().trim());
                product.setUnit_price(Double.parseDouble(views.txt_product_unit_price.getText()));
                //id de la categoria
                DynamikCombobox category_id = (DynamikCombobox) views.cmb_product_category.getSelectedItem();
                product.setCategory_id(category_id.getId());

                if (productDao.updateProductQuery(product)) {
                    cleanTable();
                    cleanFields();
                    listAllProducts();
                    views.btn_register_product.setEnabled(true);
                    JOptionPane.showMessageDialog(null, "datos del producto modificado con exito");

                } else {
                    JOptionPane.showMessageDialog(null, "se produjo un error,no se pudo modificar los datos del producto");
                }

            }

        } else if (e.getSource() == views.btn_delete_product) {
            int row = views.products_table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar un producto para eliminarlo");
            } else {
                int id = Integer.parseInt(views.products_table.getValueAt(row, 0).toString());
                int question = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres eliminar este producto?");

                if (question == 0 && productDao.deleteProductQuery(id) != false) {
                    cleanTable();
                    cleanFields();
                    views.btn_register_product.setEnabled(true);
                    listAllProducts();
                    JOptionPane.showMessageDialog(null, "producto eliminado con exito");
                }

            }
        } else if (e.getSource() == views.btn_cancel_product) {
            cleanFields();
            views.btn_register_product.setEnabled(true);
        }

    }

    //listar productos
    public void listAllProducts() {
        if (rol.equals("Administrador") || rol.equals("Auxiliar")) {
            List<Products> list = productDao.listProductsQuery(views.txt_search_product.getText());

            if (list == null || list.isEmpty()) {
                // Manejar caso de lista vacía
                return;
            }

            model = (DefaultTableModel) views.products_table.getModel();
            // Limpiar tabla antes de cargar nuevos datos
            model.setRowCount(0);

            Object[] row = new Object[7];
            for (int i = 0; i < list.size(); i++) {
                row[0] = list.get(i).getId();
                row[1] = list.get(i).getCode();
                row[2] = list.get(i).getName();
                row[3] = list.get(i).getDescription();
                row[4] = list.get(i).getUnit_price();
                row[5] = list.get(i).getProduct_quantity();
                row[6] = list.get(i).getCategory_name();
                model.addRow(row);
            }
            views.products_table.setModel(model);

            if (rol.equals("Auxiliar")) {
                views.btn_register_product.setEnabled(false);
                views.btn_update_product.setEnabled(false);
                views.btn_delete_product.setEnabled(false);
                views.btn_cancel_product.setEnabled(false);
                views.txt_product_code.setEnabled(false);
                views.txt_product_description.setEnabled(false);
                views.txt_product_id.setEditable(false);
                views.txt_product_name.setEditable(false);
                views.txt_product_unit_price.setEditable(false);

            }

        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.products_table) {
            int row = views.products_table.rowAtPoint(e.getPoint());
            views.txt_product_id.setText(views.products_table.getValueAt(row, 0).toString());
            //trayendo los datos de searchProducts
            product = productDao.searchProduct(Integer.parseInt(views.txt_product_id.getText()));
            views.txt_product_code.setText("" + product.getCode());
            views.txt_product_name.setText(product.getName());
            views.txt_product_description.setText(product.getDescription());
            views.txt_product_unit_price.setText("" + product.getUnit_price());
            views.cmb_product_category.setSelectedItem(new DynamikCombobox(product.getCategory_id(), product.getCategory_name()));
            //deshabilitar btn
            views.btn_register_product.setEnabled(false);
        } else if (e.getSource() == views.jLabelProducts) {
            views.jTabbedPane.setSelectedIndex(0);
            cleanTable();
            cleanFields();
            listAllProducts();
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
        if (e.getSource() == views.txt_search_product) {
            cleanTable();
            listAllProducts();

        }

    }

    //limpiar tabla
    public void cleanTable() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
            i = i - 1;
        }

    }

    //limpìar campos
    public void cleanFields() {
        views.txt_product_id.setText("");
        views.txt_product_code.setText("");
        views.txt_product_name.setText("");
        views.txt_product_description.setText("");
        views.txt_product_unit_price.setText("");

    }

}
