/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;


public class conectarBD {
    Connection cnx;
    Statement st;
    ResultSet rs;
    PreparedStatement pstm;
    ArrayList<claseCartelera> listaCartelera =new ArrayList<>();
    ArrayList<claseSala> listaSalas=new ArrayList();
    ArrayList<claseFacturado> listaFactura=new ArrayList();
    ObservableList<Integer> salasCombo = FXCollections.observableArrayList();
    private ArrayList<claseFacturado> factura = new ArrayList<>();
    
    public conectarBD(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            cnx=DriverManager.getConnection("jdbc:mysql://remotemysql.com/Pr1mdxAdrh","Pr1mdxAdrh","fNBUrxid1O");
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    public ArrayList<claseCartelera> MostrarCartelera(){
        try {
            String cadena="select distinct(titulo),cartel from  cartelera";
            st=cnx.createStatement();
            rs=st.executeQuery(cadena);
            while (rs.next()){
                claseCartelera cc=new claseCartelera();
                cc.setTitulo(rs.getString(1));
                cc.setCartel(rs.getBytes(2));
                listaCartelera.add(cc);
            }
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaCartelera;
    }

    public int[] cargarSala(String titulo, int sala, String sesion) {
        int[] salaCine = new int[2];

        try {
            PreparedStatement sentencia = cnx.prepareStatement("select * from salaCine where idSala=?");
            sentencia.setInt(1, sala);
            ResultSet rs = sentencia.executeQuery();
            if (rs.next()) {
                salaCine[0] = rs.getInt(2);
                salaCine[1] = rs.getInt(3);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return salaCine;
    }
    
    public ArrayList<claseCartelera> cargarCarteleraBackEnd() {
        try {
            String cadena = "select * from cartelera";
            Statement sentencia = cnx.createStatement();
            ResultSet rs = sentencia.executeQuery(cadena);
            while (rs.next()) {
                claseCartelera cc = new claseCartelera();
                cc.setIndice(rs.getInt(1));
                cc.setTitulo(rs.getString(2));
                cc.setSesion(rs.getString(3));
                cc.setSala(rs.getInt(4));
                cc.setCartel(rs.getBytes(5));
                listaCartelera.add(cc);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return listaCartelera;
    }
    
    public boolean ocuparButaca(String sesion, String sala, String fila, String columna) {
        boolean res = false;
        try {
            PreparedStatement sentencia = cnx.prepareStatement("update facturacionCine set ocupado = true where sesion = ? and idSala = ? and fila = ? and columna = ?");

            sentencia.setString(1, sesion);
            sentencia.setString(2, sala);
            sentencia.setInt(3, Integer.parseInt(fila));
            sentencia.setInt(4, Integer.parseInt(columna));

            int rs = sentencia.executeUpdate();

            if (rs >= 1) {
                res = true;
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }
    
    public boolean insertarPeliculas(String nombre, String sala, String sesion, File cartel) throws FileNotFoundException {
        boolean res = false;

        try {
            if (cartel != null) {
                FileInputStream ficheroInput = new FileInputStream(cartel);

                PreparedStatement sentencia = cnx.prepareStatement("insert into cartelera values (null,?,?,?,?)");

                sentencia.setString(1, nombre);
                sentencia.setInt(3, Integer.parseInt(sala));
                sentencia.setString(2, sesion);
                sentencia.setBinaryStream(4, ficheroInput, (int) cartel.length());

                if (sentencia.executeUpdate() == 1) {
                    res = true;
                }
            } else{
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Falta el fichero del cartel", ButtonType.OK);
                alerta.show();
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }
    
    public boolean modificarPeliculas(int id, String titulo, String sala, String sesion, File cartel) throws FileNotFoundException {
        boolean res = false;
        PreparedStatement sentencia = null;
        try {
            if (cartel == null) {
                sentencia = cnx.prepareStatement("update cartelera set titulo = ?, sesion = ?, sala = ? where indice = ?");

                sentencia.setString(1, titulo);
                sentencia.setInt(3, Integer.parseInt(sala));
                sentencia.setString(2, sesion);
                sentencia.setInt(4, id);
            } else {
                FileInputStream ficheroInput = new FileInputStream(cartel);
                sentencia = cnx.prepareStatement("update cartelera set titulo = ?, sesion = ?, sala = ?, cartel = ? where indice = ?");

                sentencia.setString(1, titulo);
                sentencia.setInt(3, Integer.parseInt(sala));
                sentencia.setString(2, sesion);
                sentencia.setBinaryStream(4, ficheroInput, (int) cartel.length());
                sentencia.setInt(5, id);
            }

            if (sentencia.executeUpdate() == 1) {
                res = true;
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }
    
    public boolean insertarSala(int idSala, int filas, int columnas) {
        boolean res = false;

        try {
            PreparedStatement sentencia = cnx.prepareStatement("insert into salaCine values (?,?,?)");

            sentencia.setInt(1, idSala);
            sentencia.setInt(2, filas);
            sentencia.setInt(3, columnas);

            if(sentencia.executeUpdate() == 1){
                res = true;
            }

        } catch (SQLException ex) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "Sala ya creada", ButtonType.OK);
                alerta.show();
            System.err.println(ex.getMessage());
        }

        return res;
    }
    
    public boolean copiarTabla(){

    	boolean result = false;
    	try{
    		PreparedStatement sentencia = cnx.prepareStatement("INSERT INTO facturacionCineHistorial SELECT indice, idSala, fila, columna, sesion FROM facturacionCine");
    		if(sentencia.executeUpdate() == 1){

    			result = true;
    		}

    	}catch(SQLException ex){
    		System.err.println(ex.getMessage());
    	}

    	return result;
    }
    
    public ArrayList<claseFacturado> facturar(){

        try {
            Statement sentencia = cnx.createStatement();

            ResultSet rs = sentencia.executeQuery("select * from facturacionCine");

            while(rs.next()){
                factura.add(new claseFacturado(rs.getInt(1), rs.getInt(3), rs.getInt(4), rs.getInt(2), rs.getString(5), rs.getBoolean(6)));
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return factura;
    }

    
    public boolean comprobarButaca(String sesion, String sala, String fila, String columna) {
        boolean res = false;
        try {
            PreparedStatement sentencia = cnx.prepareStatement("select ocupado from facturacionCine where sesion = ? and idSala = ? and fila = ? and columna = ?");

            sentencia.setString(1, sesion);
            sentencia.setString(2, sala);
            sentencia.setInt(3, Integer.parseInt(fila));
            sentencia.setInt(4, Integer.parseInt(columna));

            ResultSet rs = sentencia.executeQuery();

            if (rs.next()) {
                if (rs.getInt(1) == 1) {
                    res = true;
                }
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }
    
    public boolean resetCartelera() {
        boolean res = false;

        try {
            PreparedStatement sentencia = cnx.prepareStatement("truncate table cartelera");

            if(sentencia.executeUpdate() >= 0){
                res = true;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return res;
    }
    
    public ObservableList<Integer> cargarSalas() {
        try {

            Statement sentencia = cnx.createStatement();

            ResultSet rs = sentencia.executeQuery("select idSala from salaCine");

            while (rs.next()) {
                salasCombo.add(rs.getInt(1));

            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return salasCombo;
    }
    
    public ArrayList<claseCartelera> MostrarTodaSesiones(String idPelicula) {
             
        try {
            String cadenaSql="select * from cartelera where titulo=?";
             PreparedStatement pstm = cnx.prepareStatement(cadenaSql);
            pstm.setString(1,idPelicula);
           
            rs=pstm.executeQuery();
            while (rs.next()){
                claseCartelera cc=new claseCartelera(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getBytes(5));
                System.out.println(cc.getSesion());
                listaCartelera.add(cc);
            }
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return listaCartelera;
    }

    public ObservableList<claseCartelera> MostrarCartelera2(String idPelicula) {
        ObservableList <claseCartelera> ListaDatos=FXCollections.observableArrayList();
 try {
    
            String cadenaSql="select * from cartelera where titulo=?";
             PreparedStatement pstm = cnx.prepareStatement(cadenaSql);
         
           pstm.setString(1,idPelicula);
            rs=pstm.executeQuery();
            while (rs.next()){
                claseCartelera cc=new claseCartelera();
                int dia=Integer.valueOf(rs.getString(3).substring(8,10));
                System.out.println("dia--"+dia);
                int mes=Integer.valueOf(rs.getString(3).substring(5,7));
                   System.out.println("mes--"+mes);

                int hora=Integer.valueOf(rs.getString(3).substring(11, 13));
                
                 System.out.println("hora" +hora);
                int minutos=Integer.valueOf(rs.getString(3).substring(14,16));
               LocalDate date = LocalDate.now();
               LocalTime time = LocalTime.now(); 
              System.out.println("mes "+date.getMonthValue());
              boolean ok=false;
                if (mes>=date.getMonthValue() && dia>date.getDayOfMonth()) ok=true;
                  if (dia==date.getDayOfMonth()&&mes==date.getMonthValue() && hora>time.getHour()) ok=true;
                    if (dia==date.getDayOfMonth() && hora==time.getHour() && minutos>=time.getMinute()) ok=true;
                        if (ok)
                    {
                        cc.setIndice(rs.getInt(1));
                        cc.setTitulo(rs.getString(2));
                        cc.setSesion(rs.getString(3));
                        cc.setSala(rs.getInt(4));
                        cc.setCartel(rs.getBytes(5));
                         cc =new claseCartelera(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getBytes(5));
                              ListaDatos.add(cc);
                    }
                     
               
     
           
            }
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return ListaDatos;
    }

    public ArrayList<claseSala> MostrarSalas() {
String cadenaSql="select * from salaCine";
        try {
            st=cnx.createStatement();
            rs=st.executeQuery(cadenaSql);
            while (rs.next()){
                claseSala cs=new claseSala(rs.getInt(1),rs.getInt(2),rs.getInt(3));
                listaSalas.add(cs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
return listaSalas;
    }

    public void insertarButacas(int filas, int columnas,int sala,String sesion) {
        try {
            String cadenaSql="insert into facturacionCine(idSala,fila,columna,sesion,ocupado) values(?,?,?,?,0)";
            PreparedStatement pstm=cnx.prepareStatement(cadenaSql);
            pstm.setInt(1, sala);
            pstm.setInt(2, filas);
            pstm.setInt(3, columnas);
            pstm.setString(4,sesion);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    public ArrayList<claseFacturado> CargarSala(String Sesion,int sala) {
        try {
            String cadenaSql="select * from facturacionCine where sesion = '"+Sesion+"' and idSala = "+sala;
             System.out.println(cadenaSql);
             st=cnx.createStatement();
            rs=st.executeQuery(cadenaSql);;
            while (rs.next()){
                System.out.println("fila" +rs.getInt(3));
                claseFacturado cf=new claseFacturado(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getInt(4),rs.getString(5));
                
                listaFactura.add(cf);
            }
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaFactura;
    }

    public void ocuparButaca(String sesion, int sala, int fila, int columna) {
        try {
            String cadenaSql="update  facturacionCine set ocupado=1 where sesion=? and idsala=? and fila=? and columna=?";
            PreparedStatement pstm=cnx.prepareStatement(cadenaSql);
            pstm.setString(1, sesion);
            pstm.setInt(2, sala);
            pstm.setInt(3, fila);
            pstm.setInt(4, columna);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean comprobarAsiento(String sesion, int sala, int fila, int columna) {
        boolean libre=true;
        try {
           
            String cadenaSql="select * from facturacionCine where sesion=? and idsala=? and fila=? and columna=?";
            PreparedStatement pstm=cnx.prepareStatement(cadenaSql);
            pstm.setString(1, sesion);
            pstm.setInt(2, sala);
            pstm.setInt(3, fila);
            pstm.setInt(4, columna);
            rs=pstm.executeQuery();
            while (rs.next()){
                if (rs.getInt(6)==1)
                    libre=false;
                 System.out.println("ocupado");
            }
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return libre;
    }

    public void InsertarNuevaPelicula(String text, String sesion, Integer valueOf) {

    }

    public void InsertarNuevaPelicula(String titulo, String sesion, Integer sala, File imagen) {
        try {
            FileInputStream ficheroInput = new FileInputStream(imagen);
            String cadenaSql="insert into cartelera(titulo,sesion,sala,cartel) values(?,?,?,?)";
            PreparedStatement pstm=cnx.prepareStatement(cadenaSql);
            pstm.setString(1, titulo);
            pstm.setString(2, sesion);
            pstm.setInt(3, sala);
            pstm.setBinaryStream(4, ficheroInput,(int)imagen.length());
            pstm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ResetearCartelera() {
        try {
            String cadenaSql="truncate cartelera";
            st=cnx.createStatement();
            st.executeUpdate(cadenaSql);
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void modificarPeliculas(String pelicula, String hora, int sala, File ficheroImagen) {
        String cadenaSql="update cartelera set hora=?, sala=?, imagen=? where pelicula=?";

    }

    public boolean comprobarDisponibilidad(String sesion, Integer sala) {
        boolean libre=true;
        try {
            
            String cadenaSql="select * from cartelera where sesion=? and sala=?";
            PreparedStatement pstm=cnx.prepareStatement(cadenaSql);
            pstm.setString(1, sesion);
            pstm.setInt(2, sala);
            rs=pstm.executeQuery();
            if (rs.next()) libre=false;
        } catch (SQLException ex) {
            Logger.getLogger(conectarBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return libre;
    }
    
}
