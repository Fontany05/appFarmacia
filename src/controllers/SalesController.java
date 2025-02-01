package controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Customers;
import models.CustomersDao;
import static models.EmployeesDao.id_user;
import static models.EmployeesDao.rol_user;
import models.Products;
import models.ProductsDao;
import models.Sales;
import models.SalesDao;
import views.Systemview;

public class SalesController implements ActionListener, MouseListener, KeyListener {

    private Sales sale;
    private SalesDao saleDao;
    private Systemview views;
    private int item = 0;
    String rol = rol_user;
    //modelo productos
    Products product = new Products();
    ProductsDao productDao = new ProductsDao();
    DefaultTableModel model = new DefaultTableModel();
    DefaultTableModel temp;

    public SalesController(Sales sale, SalesDao saleDao, Systemview views) {
        this.sale = sale;
        this.saleDao = saleDao;
        this.views = views;
        //Boton de agregar
        this.views.btn_add_product_sale.addActionListener(this);
        //Boton de vender
        this.views.btn_confirm_sale.addActionListener(this);
        //Boton de eliminar
        this.views.btn_remove_sale.addActionListener(this);
        //Boton de nuevo
        this.views.btn_new_sale.addActionListener(this);
        this.views.txt_sale_product_code.addKeyListener(this);
        this.views.txt_sale_price.addKeyListener(this);
        this.views.txt_sale_customer_id.addKeyListener(this);
        this.views.jLabelSales.addMouseListener(this);
        this.views.jLabelReports.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btn_confirm_sale) {
            insertSale();
        } else if (e.getSource() == views.btn_new_sale) {
            cleanAllFieldsSales();
            cleanTableTemp();
        } else if (e.getSource() == views.btn_remove_sale) {
            model = (DefaultTableModel) views.sales_table.getModel();
            model.removeRow(views.sales_table.getSelectedRow());
            calculateSales();
        } else if (e.getSource() == views.btn_add_product_sale) {
            // Validate input fields
            if (views.txt_sale_quantity.getText().isEmpty()
                    || views.txt_sale_product_name.getText().isEmpty()
                    || views.txt_sale_price.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese todos los campos necesarios");
                return;
            }

            int amount = Integer.parseInt(views.txt_sale_quantity.getText());
            int sale_id = Integer.parseInt(views.txt_sale_product_id.getText());
            String product_name = views.txt_sale_product_name.getText();
            double price = Double.parseDouble(views.txt_sale_price.getText());
            double subtotal = amount * price;
            int stock = Integer.parseInt(views.txt_sale_stock.getText());
            String full_name = views.txt_sale_customer_name.getText();

            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0");
            } else {
                DefaultTableModel temp = (DefaultTableModel) views.sales_table.getModel();

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

                // comprobar productos duplicados
                for (int i = 0; i < temp.getRowCount(); i++) {
                    Object valueAt = temp.getValueAt(i, 1);
                    if (valueAt != null && valueAt.equals(product_name.trim())) {
                        JOptionPane.showMessageDialog(null, "El producto ya está registrado en la tabla de compras");
                        return;
                    }
                }

                // Check el stock disponible 
                if (stock >= amount) {
                    item++;
                    temp = (DefaultTableModel) views.sales_table.getModel();

                    Object[] obj = {sale_id, product_name, amount, price, subtotal, full_name};
                    temp.addRow(obj);
                    views.sales_table.setModel(temp);
                    calculateSales();
                    cleanFieldsSales();
                    views.txt_sale_product_code.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Stock no disponible");
                }
            }
        }

    }

    //Listar todas las ventas
    public void listAllSales() {

        if (rol.equals("Administrador")) {

            List<Sales> list = saleDao.listAllSalesQuery();

            if (list == null || list.isEmpty()) {
                // Manejar caso de lista vacía
                return;
            }

            model = (DefaultTableModel) views.table_all_sales.getModel();
            // Limpiar tabla antes de cargar nuevos datos
            model.setRowCount(0);

            //Recorrer la lista
            Object[] row = new Object[5];

            for (int i = 0; i < list.size(); i++) {

                row[0] = list.get(i).getId();

                row[1] = list.get(i).getCustomer_name();

                row[2] = list.get(i).getEmployee_name();

                row[3] = list.get(i).getTotal_to_pay();

                row[4] = list.get(i).getSale_date();

                model.addRow(row);

            }

            views.table_all_sales.setModel(model);

        }

    }

    private void insertSale() {
        try {
            // Validar que los campos no estén vacíos
            if (views.txt_sale_customer_id.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El ID del cliente no puede estar vacío");
                return;
            }

            if (views.txt_sale_total_to_pay.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El total a pagar no puede estar vacío");
                return;
            }

            // Validar que la tabla tenga elementos
            if (views.sales_table.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "No hay productos en la venta");
                return;
            }

            // Convertir valores después de validar
            int customer_id = Integer.parseInt(views.txt_sale_customer_id.getText().trim());
            int employee_id = id_user;
            double total = Double.parseDouble(views.txt_sale_total_to_pay.getText().trim());

            if (saleDao.registerSaleQuery(customer_id, employee_id, total)) {
                Products product = new Products();
                ProductsDao productDao = new ProductsDao();
                int sale_id = saleDao.saleId();

                for (int i = 0; i < views.sales_table.getRowCount(); i++) {
                    // Validar que los valores de la tabla no sean nulos
                    Object productIdObj = views.sales_table.getValueAt(i, 0);
                    Object quantityObj = views.sales_table.getValueAt(i, 2);
                    Object priceObj = views.sales_table.getValueAt(i, 3);

                    if (productIdObj == null || quantityObj == null || priceObj == null) {
                        JOptionPane.showMessageDialog(null, "Datos inválidos en la fila " + (i + 1));
                        continue;
                    }

                    int product_id = Integer.parseInt(productIdObj.toString());
                    int sale_quantity = Integer.parseInt(quantityObj.toString());
                    double sale_price = Double.parseDouble(priceObj.toString());
                    double sale_subtotal = sale_quantity * sale_price;

                    // Validar stock antes de procesar
                    product = productDao.searchId(product_id);
                    if (product.getProduct_quantity() < sale_quantity) {
                        JOptionPane.showMessageDialog(null, "Stock insuficiente para el producto ID: " + product_id);
                        return;
                    }

                    saleDao.registerSaleDetailQuery(product_id, sale_price, sale_quantity, sale_price, sale_subtotal);

                    int amount = product.getProduct_quantity() - sale_quantity;
                    productDao.updateStockQuery(amount, product_id);
                }

                cleanTableTemp();
                cleanAllFieldsSales();
                JOptionPane.showMessageDialog(null, "Venta generada exitosamente");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al procesar los números: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al procesar la venta: " + e.getMessage());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.jLabelSales) {
            views.jTabbedPane.setSelectedIndex(2);

        } else if (e.getSource() == views.jLabelReports) {

            if (rol.equals("Administrador")) {

                views.jTabbedPane.setSelectedIndex(7);

                listAllSales();

            } else {

                views.jTabbedPane.setEnabledAt(7, false);

                views.jLabelReports.setEnabled(false);

                JOptionPane.showMessageDialog(null, "No tiene privilegios de administrador para acceder a esta vista");

            }

        } else if (e.getSource() == views.jLabelReports) {
            views.jTabbedPane.setSelectedIndex(7);
            listAllSales();
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
        if (e.getSource() == views.txt_sale_product_code) {

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                if (!"".equals(views.txt_sale_product_code.getText())) {

                    int code = Integer.parseInt(views.txt_sale_product_code.getText());

                    product = productDao.searchCode(code);

                    if (product.getName() != null) {

                        views.txt_sale_product_name.setText(product.getName());

                        views.txt_sale_product_id.setText("" + product.getId());

                        views.txt_sale_stock.setText("" + product.getProduct_quantity());

                        views.txt_sale_price.setText("" + product.getUnit_price());

                        views.txt_sale_quantity.requestFocus();

                    } else {

                        JOptionPane.showMessageDialog(null, "No existe ningún producto con ese código");
                        cleanFieldsSales();
                        views.txt_sale_product_code.requestFocus();

                    }

                } else {

                    JOptionPane.showMessageDialog(null, "Ingrese el código del producto a vender");

                }

            }

        } else if (e.getSource() == views.txt_sale_customer_id) {

            if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                Customers customer = new Customers();

                CustomersDao customerDao = new CustomersDao();

                if (!"".equals(views.txt_sale_customer_id.getText())) {

                    int customer_id = Integer.parseInt(views.txt_sale_customer_id.getText());

                    customer = customerDao.searchCustomer(customer_id);

                    if (customer.getFull_name() != null) {

                        views.txt_sale_customer_name.setText("" + customer.getFull_name());

                    } else {

                        views.txt_sale_customer_id.setText("");

                        JOptionPane.showMessageDialog(null, "El cliente no existe");

                    }

                }

            }

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == views.txt_sale_quantity) {

            int quantity;

            double price = Double.parseDouble(views.txt_sale_price.getText());

            if (views.txt_sale_quantity.getText().equals("")) {

                quantity = 1;

                views.txt_sale_price.setText("" + price);

            } else {

                quantity = Integer.parseInt(views.txt_sale_quantity.getText());

                price = Double.parseDouble(views.txt_sale_price.getText());

                views.txt_sale_subtotal.setText("" + quantity * price);

            }

        }

    }

    //Limpiar tabla temporal
    public void cleanTableTemp() {
        DefaultTableModel temp = (DefaultTableModel) views.sales_table.getModel();
        for (int i = 0; i < temp.getRowCount(); i++) {
            temp.removeRow(i);
            i = i - 1;
        }

    }

    // Limpiar algunos campos
    public void cleanFieldsSales() {

        views.txt_sale_product_code.setText("");

        views.txt_sale_product_name.setText("");

        views.txt_sale_quantity.setText("");

        views.txt_sale_product_id.setText("");

        views.txt_sale_price.setText("");

        views.txt_sale_subtotal.setText("");

        views.txt_sale_stock.setText("");

    }

    //Limpiar todos los campos
    public void cleanAllFieldsSales() {

        views.txt_sale_product_code.setText("");

        views.txt_sale_product_name.setText("");

        views.txt_sale_quantity.setText("");

        views.txt_sale_product_id.setText("");

        views.txt_sale_price.setText("");

        views.txt_sale_subtotal.setText("");

        views.txt_sale_customer_id.setText("");

        views.txt_sale_customer_name.setText("");

        views.txt_sale_total_to_pay.setText("");

        views.txt_sale_stock.setText("");

    }

    //Calcular total a pagar tabla de ventas
    private void calculateSales() {
        double total = 0.00;
        int numRow = views.sales_table.getRowCount();
        int itemColumnIndex = 4;

        for (int i = 0; i < numRow; i++) {
            Object value = views.sales_table.getValueAt(i, itemColumnIndex);

            // Verificar si el valor no es null y convertirlo a Double
            if (value != null) {
                try {
                    total += Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    System.err.println("Valor inválido en la fila " + i + ": " + value);
                }
            }
        }

    }

}
