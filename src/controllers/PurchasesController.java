package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.DynamikCombobox;
import static models.EmployeesDao.id_user;
import static models.EmployeesDao.rol_user;
import models.Products;
import models.ProductsDao;
import models.Purchases;
import models.PurchasesDao;
import views.Systemview;

public class PurchasesController implements KeyListener, ActionListener {

    private Purchases purchase;
    private PurchasesDao purchasesDao;
    private Systemview views;
    private int getIdSupplier = 0;
    private int item = 0;
    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel temp;
    //modelo productos
    Products product = new Products();
    ProductsDao productDao = new ProductsDao();
    String rol = rol_user;

    public PurchasesController(Purchases purchase, PurchasesDao purchasesDao, Systemview views) {
        this.purchase = purchase;
        this.purchasesDao = purchasesDao;
        this.views = views;
        //btn agregar 
        this.views.btn_add_product_to_buy.addActionListener(this);
        //btn comprar
        this.views.btn_confirm_purchase.addActionListener(this);
        this.views.txt_purchase_product_code.addKeyListener(this);
        this.views.txt_purchase_price.addKeyListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_add_product_to_buy) {
            DynamikCombobox supplier_cmb = (DynamikCombobox) views.cmb_purchase_supplier.getSelectedItem();
            int supplier_id = supplier_cmb.getId();

            if (getIdSupplier == 0) {
                getIdSupplier = supplier_id;
            }

            if (getIdSupplier != supplier_id) {
                JOptionPane.showMessageDialog(null, "¡Ha cambiado el proveedor! Por favor, termine la compra actual o reinicie.");
                return;
            }

            int amount = Integer.parseInt(views.txt_purchase_amount.getText());
            String product_name = views.txt_purchase_product_name.getText();
            double price = Double.parseDouble(views.txt_purchase_price.getText());
            int purchase_id = Integer.parseInt(views.txt_purchase_id.getText());
            String supplier_name = views.cmb_purchase_supplier.getSelectedItem().toString();

            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0");
            } else {
                DefaultTableModel temp = (DefaultTableModel) views.purchases_table.getModel();

                // Primero, eliminar todas las filas vacías
                for (int i = temp.getRowCount() - 1; i >= 0; i--) {
                    boolean isEmptyRow = true;
                    for (int j = 0; j < temp.getColumnCount(); j++) {
                        Object value = temp.getValueAt(i, j);
                        if (value != null && !value.toString().trim().isEmpty()) {
                            isEmptyRow = false;
                            break;
                        }
                    }
                    if (isEmptyRow) {
                        temp.removeRow(i);
                    }
                }

                // Verificar producto duplicado
                for (int i = 0; i < temp.getRowCount(); i++) {
                    Object valueAt = temp.getValueAt(i, 1);
                    if (valueAt != null && valueAt.equals(product_name.trim())) {
                        JOptionPane.showMessageDialog(null, "El producto ya está registrado en la tabla de compras");
                        return;
                    }
                }

                ArrayList list = new ArrayList();
                item = 1;
                list.add(item);
                list.add(purchase_id);
                list.add(product_name);
                list.add(amount);
                list.add(price);
                double subtotal = amount * price;
                list.add("" + subtotal);
                list.add(supplier_name);

                Object[] obj = new Object[6];
                obj[0] = list.get(1);
                obj[1] = list.get(2);
                obj[2] = list.get(3);
                obj[3] = list.get(4);
                obj[4] = list.get(5);
                obj[5] = list.get(6);
                temp.addRow(obj);
                views.purchases_table.setModel(temp);
                cleanFieldsPurchases();
                views.cmb_purchase_supplier.setEditable(false);
                views.txt_purchase_product_code.requestFocus();
                calculatePurchase();

            }
        } else if (e.getSource() == views.btn_confirm_purchase) {
            insertPurchase();
        }

    }

    private void insertPurchase() {
        double total = Double.parseDouble(views.txt_purchase_total_to_pay.getText());
        int employees_id = id_user;

        if (purchasesDao.registerPurchaseQuery(getIdSupplier, employees_id, total)) {
            //obtener el id de la compra,para enviarselo a purchase details
            int purchase_id = purchasesDao.purchaseId();
            for (int i = 0; i < views.purchases_table.getRowCount(); i++) {
                int product_id = Integer.parseInt(views.purchases_table.getValueAt(i, 0).toString());
                int purchase_amount = Integer.parseInt(views.purchases_table.getValueAt(i, 2).toString());
                double purchase_price = Double.parseDouble(views.purchases_table.getValueAt(i, 3).toString());
                double purchase_subtotal = purchase_price * purchase_amount;

                //registrar detalle de la compra
                purchasesDao.registerPurchaseDetailQuery(purchase_id, purchase_price, purchase_amount, purchase_subtotal, product_id);

            }
            JOptionPane.showMessageDialog(null, "compra generada con exito");
            cleanTableTemp();
            cleanFieldsPurchases();
        }
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {

    }

    @Override
    public void keyPressed(KeyEvent e
    ) {
        if (e.getSource() == views.txt_purchase_product_code) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (views.txt_purchase_product_code.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ingrese el codigo del producto a comprar");
                } else {
                    int id = Integer.parseInt(views.txt_purchase_product_code.getText());
                    product = productDao.searchCode(id);
                    views.txt_purchase_product_name.setText(product.getName());
                    views.txt_purchase_id.setText("" + product.getId());
                    views.txt_purchase_amount.requestFocus();
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
        if (e.getSource() == views.txt_purchase_price) {
            int quantity;
            double price = 0.0;

            if (views.txt_purchase_amount.getText().isEmpty()) {
                quantity = 1;
                views.txt_purchase_price.setText("" + price);
            } else {
                quantity = Integer.parseInt(views.txt_purchase_amount.getText());
                price = Double.parseDouble(String.valueOf(views.txt_purchase_price.getText()));
                views.txt_purchase_subtotal.setText("" + quantity * price);
            }

        }

    }
    //limpiar campos

    public void cleanFieldsPurchases() {
        views.txt_purchase_product_name.setText("");
        views.txt_purchase_price.setText("");
        views.txt_purchase_amount.setText("");
        views.txt_purchase_product_code.setText("");
        views.txt_purchase_subtotal.setText("");
        views.txt_purchase_id.setText("");
        views.txt_purchase_total_to_pay.setText("");
    }

    public void calculatePurchase() {
        double total = 0.00;
        int numRow = views.purchases_table.getRowCount();
        int itemColumnIndex = 4;

        for (int i = 0; i < numRow; i++) {
            Object value = views.purchases_table.getValueAt(i, itemColumnIndex);

            // Verificar si el valor no es null y convertirlo a Double
            if (value != null) {
                try {
                    total += Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    System.err.println("Valor inválido en la fila " + i + ": " + value);
                }
            }
        }

        // Mostrar el total formateado
        views.txt_purchase_total_to_pay.setText(String.valueOf(total));
    }
    //chequear porque no borrar los campos
    public void cleanTableTemp() {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            temp.removeRow(i);
        }
    }
}
