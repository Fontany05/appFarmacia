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
import models.Categories;
import models.CategoriesDao;
import models.DynamikCombobox;
import static models.EmployeesDao.rol_user;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import views.Systemview;

public class CategoriesController implements ActionListener, MouseListener, KeyListener {
    
    private Categories category;
    private CategoriesDao categoryDao;
    private Systemview views;
    String rol = rol_user;

    //modelo para interactuar con la tabla de clientes
    DefaultTableModel model = new DefaultTableModel();
    
    public CategoriesController(Categories category, CategoriesDao categoryDao, Systemview views) {
        this.category = category;
        this.categoryDao = categoryDao;
        this.views = views;
        //btn registrar categoria
        this.views.btn_register_category.addActionListener(this);
        //btn modificar categoria
        this.views.btn_update_category.addActionListener(this);
        //btn eliminar categoria
        this.views.btn_delete_category.addActionListener(this);
        //btn cancelar
        this.views.btn_cancel_category.addActionListener(this);
        //event mouse en la tabla categorias
        this.views.categories_table.addMouseListener(this);
        //buscador
        this.views.txt_search_category.addKeyListener(this);
        this.views.jLabelCategories.addMouseListener(this);
        getCategoryName();
        AutoCompleteDecorator.decorate(views.cmb_product_category);
       
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //registrar
        if (e.getSource() == views.btn_register_category) {
            if (views.txt_category_name.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "todos los campos son obligatorios");
            } else {
                category.setName(views.txt_category_name.getText().trim());
                
                if (categoryDao.registerCategoryQuery(category)) {
                    cleanTable();
                    cleanFields();
                    listAllCategories();
                    JOptionPane.showMessageDialog(null, "categoria creada con exito");
                } else {
                    JOptionPane.showMessageDialog(null, "error al registrar la categoria");
                }
            }
        } else if (e.getSource() == views.btn_update_category) {
            if (views.txt_category_id.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila para continuar");
            } else {
                if (views.txt_category_id.getText().isEmpty()
                        || views.txt_category_name.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "todos los campos son obligatorio");
                } else {
                    category.setId(Integer.parseInt(views.txt_category_id.getText()));
                    category.setName(views.txt_category_name.getText().trim());
                    
                    if (categoryDao.updateCategoryQuery(category)) {
                        cleanTable();
                        cleanFields();
                        listAllCategories();
                        views.btn_register_category.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "los datos de la categoria se modificaron con exito");
                    } else {
                        JOptionPane.showMessageDialog(null, "se ha producido un error al modificar los datos del la categoria");
                    }
                }
            }
        } else if (e.getSource() == views.btn_delete_category) {
            int row = views.categories_table.getSelectedRow();
            
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Debe seleccionar una categoria para eliminarla");
            } else {
                int id = Integer.parseInt(views.categories_table.getValueAt(row, 0).toString());
                int question = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres eliminar la categoria?");
                
                if (question == 0 && categoryDao.deleteCategoryQuery(id) != false) {
                    cleanTable();
                    cleanFields();
                    views.btn_register_category.setEnabled(true);
                    listAllCategories();
                    JOptionPane.showMessageDialog(null, "Categoria eliminada con exito");
                }
                
            }
            
        } else if (e.getSource() == views.btn_cancel_category) {
            cleanFields();
            views.btn_register_category.setEnabled(true);
            
        }
        
    }

    //listar categorias
    public void listAllCategories() {
        if (rol.equals("Administrador")) {
            List<Categories> list = categoryDao.listCategoriesQuery(views.txt_search_category.getText().trim());
            
            if (list == null || list.isEmpty()) {
                // Manejar caso de lista vacía
                return;
            }

            // Obtener modelo de tabla
            DefaultTableModel model = (DefaultTableModel) views.categories_table.getModel();
            // Limpiar tabla antes de cargar nuevos datos
            model.setRowCount(0);
            
            list.forEach(category -> model.addRow(new Object[]{
                category.getId(),
                category.getName()
            })
            );
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == views.categories_table) {
            int row = views.categories_table.rowAtPoint(e.getPoint());
            views.txt_category_id.setText(views.categories_table.getValueAt(row, 0).toString());
            views.txt_category_name.setText(views.categories_table.getValueAt(row, 1).toString());
            views.btn_register_category.setEnabled(false);
        } else if (e.getSource() == views.jLabelCategories) {
            if (rol.equals("Administrador")) {
                views.jTabbedPane.setSelectedIndex(6);
                cleanTable();
                cleanFields();
                listAllCategories();
                
            } else {
                views.jTabbedPane.setEnabledAt(6, false);
                views.jLabelCategories.setEnabled(false);
                JOptionPane.showMessageDialog(null, "no tienes permiso parta acceder a esta vista");
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
        if (e.getSource() == views.txt_search_category) {
            cleanTable();
            listAllCategories();
            
        }
        
    }

    //limpiar tabla
    public void cleanTable() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
            i = i - 1;
        }
    }

    //limpiar campos
    public void cleanFields() {
        views.txt_category_id.setText("");
        views.txt_category_name.setText("");
    }

    //mostrar el nombre de las categorias
    public void getCategoryName() {
        List<Categories> list = categoryDao.listCategoriesQuery(views.txt_search_category.getText());
        for (int i = 0; i < list.size(); i++) {
            int id = list.get(i).getId();
            String name = list.get(i).getName();
            views.cmb_product_category.addItem(new DynamikCombobox(id, name));
            
        }
    }
    
}
