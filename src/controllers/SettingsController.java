
package controllers;
//paquete necesario y emplementamos todos sus metodos
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import static models.EmployeesDao.address_user;
import static models.EmployeesDao.email_user;
import static models.EmployeesDao.full_name_user;
import static models.EmployeesDao.id_user;
import static models.EmployeesDao.telephone_user;
import views.Systemview;

//para utilizar eventos del mouse

public class SettingsController implements MouseListener{
//instanciar vista
    private Systemview views;
    
    //crear el metodo constructor
    public SettingsController(Systemview views){
        this.views = views;
        this.views.jLabelProducts.addMouseListener(this);
        this.views.jLabelPurchases.addMouseListener(this);
        this.views.jLabelCostumers.addMouseListener(this);
        this.views.jLabelEmployees.addMouseListener(this);
        this.views.jLabelSuppliers.addMouseListener(this);
        this.views.jLabelCategories.addMouseListener(this);
        this.views.jLabelReports.addMouseListener(this);
       
        profile();
    }
    
    //asignar perfil de usuario
    public void profile(){
        this.views.txt_id_profile.setText(""+id_user);
        this.views.txt_name_profile.setText(full_name_user);
        this.views.txt_address_profile.setText(address_user);
        this.views.txt_phone_profile.setText(telephone_user);
        this.views.txt_email_profile.setText(email_user);
        
    }
    
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource() == views.jLabelProducts){
            views.jPanelProducts.setBackground(new Color(51,153,255));
    }else if(e.getSource() == views.jLabelPurchases){
        views.jPanelPurchases.setBackground(new Color(51,153,255));
    }else if(e.getSource() == views.jLabelCostumers){
        views.jPanelCustomers.setBackground(new Color(51,153,255));
    }else if(e.getSource() == views.jLabelEmployees){
        views.jPanelEmployees.setBackground(new Color(51,153,255));
    }else if(e.getSource() == views.jLabelSuppliers){
        views.jPanelSuppleiers.setBackground(new Color(51,153,255));
    }else if(e.getSource() == views.jLabelCategories){
        views.jPanelCategories.setBackground(new Color(51,153,255));
    }else if(e.getSource() == views.jLabelReports){
        views.jPanelReports.setBackground(new Color(51,153,255));
    }
        
    }
    @Override
    public void mouseExited(MouseEvent e) {
            if(e.getSource() == views.jLabelProducts){
            views.jPanelProducts.setBackground(new Color(51,51,51));
    }else if(e.getSource() == views.jLabelPurchases){
        views.jPanelPurchases.setBackground(new Color(51,51,51));
    }else if(e.getSource() == views.jLabelCostumers){
        views.jPanelCustomers.setBackground(new Color(51,51,51));
    }else if(e.getSource() == views.jLabelEmployees){
        views.jPanelEmployees.setBackground(new Color(51,51,51));
    }else if(e.getSource() == views.jLabelSuppliers){
        views.jPanelSuppleiers.setBackground(new Color(51,51,51));
    }else if(e.getSource() == views.jLabelCategories){
        views.jPanelCategories.setBackground(new Color(51,51,51));
    }else if(e.getSource() == views.jLabelReports){
        views.jPanelReports.setBackground(new Color(51,51,51));
    }
    }
    
}
