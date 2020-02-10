package controlador;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import modelo.conectarBD;
import modelo.claseCartelera;
import modelo.claseFacturado;


public class ControladorGestionarCartelera implements Initializable {

    conectarBD bd;
    File ficheroImagen;
    private static int index = 0;
    ArrayList<claseCartelera> cartelera = new ArrayList<>();
    
    private ArrayList<claseFacturado> factura = new ArrayList<>();
    private static final double PRECIO = 6.50;
    private ObservableList<Integer> salasCombo = FXCollections.observableArrayList();


    @FXML
    private AnchorPane ac;

    @FXML
    private Label idCartelera;
    @FXML
    private TabPane TabPane;

    @FXML
    private Tab peliculasTab;

    @FXML
    private ImageView imgCartel;
    @FXML
    private TextField nombrePeli;
    @FXML
    private TextField sesionPeli;
    @FXML
    private DatePicker dateTimee;
    @FXML
    private DatePicker dateTimee1;
    @FXML
    private TextField sesionPeli1;
    @FXML
    private TextField salaPeli1;
    @FXML
    private TextField nombrePeli1;
    @FXML
    private TextField idSala;
    @FXML
    private TextField filasText;
    @FXML
    private TextField columnasText;
    @FXML
    private ImageView imgCartel1;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnAdd;
    @FXML
    private Button antbtn;
    @FXML
    private Button nextBtn;
    @FXML
    private Button addSala;
    @FXML
    private Button Facturacion;
    @FXML
    private Button resetCartelera;
    @FXML
    private Button btnResetFacturas;
    @FXML
    private ComboBox<Integer> comboSala;
	public Stage ventanaGestion;


    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bd = new conectarBD();

        

        salasCombo = bd.cargarSalas();

        comboSala.setItems(salasCombo);
        comboSala.setValue(salasCombo.get(0));

        cartelera = bd.cargarCarteleraBackEnd();
        cargarCartelera();
    }

    

    @FXML
    private void cartel_onMouseClicked(MouseEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filtroJPG = new FileChooser.ExtensionFilter("ficheros jpg (*.jpg)", new String[]{"*.JPG"});
            FileChooser.ExtensionFilter filtroPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", new String[]{"*.PNG"});
            fileChooser.getExtensionFilters().addAll((FileChooser.ExtensionFilter[]) (Object[]) new FileChooser.ExtensionFilter[]{filtroJPG, filtroPNG});
            this.ficheroImagen = fileChooser.showOpenDialog((Window) null);
            Image image = new Image(this.ficheroImagen.toURI().toURL().toString());
            this.imgCartel.setImage(image);
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void addCartel_onAction(ActionEvent event) {
        bd = new conectarBD();

        if (dateTimee.getValue() != null && !nombrePeli.getText().equals("") && !sesionPeli.getText().equals("")) {

            String sesion = String.format("%04d-%02d-%02d-%s", dateTimee.getValue().getYear(), dateTimee.getValue().getMonthValue(), dateTimee.getValue().getDayOfMonth(), sesionPeli.getText());

            try {
                if (!comprobarPelicula(sesion, comboSala.getSelectionModel().getSelectedItem())) {
                    if (!bd.insertarPeliculas(nombrePeli.getText(), String.valueOf(comboSala.getSelectionModel().getSelectedItem()), sesion, this.ficheroImagen)) {
                        System.err.println("Error al insertar la pelicula");
                    } else {
                        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Pelicula insertada con 茅xito", ButtonType.OK);
                        alerta.show();
                        cartelera.clear();
                        cartelera = bd.cargarCarteleraBackEnd();
                    }
                } else {
                    Alert alerta = new Alert(Alert.AlertType.ERROR, "Cambie la sesi贸n o la sala de la proyecci贸n", ButtonType.OK);
                    alerta.show();
                }
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR, "Los campos no deben estar vac铆os", ButtonType.OK);
            alerta.show();
        }
    }

    @FXML
    private void modificar_onAction(ActionEvent event) {
        bd = new conectarBD();

        String sesion = String.format("%04d-%02d-%02d-%s", dateTimee1.getValue().getYear(), dateTimee1.getValue().getMonthValue(), dateTimee1.getValue().getDayOfMonth(), sesionPeli1.getText());

        try {
            if (!comprobarPelicula(sesion, Integer.parseInt(salaPeli1.getText()))) {
                if (!bd.modificarPeliculas(cartelera.get(index).getIndice(), nombrePeli1.getText(), salaPeli1.getText(), sesion, this.ficheroImagen)) {
                    System.err.println("Error al modificar la pelicula");
                } else {
                    Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Pelicula modificada con 茅xito", ButtonType.OK);
                    alerta.show();
                    cartelera.clear();
                    cartelera = bd.cargarCarteleraBackEnd();
                    cargarCartelera();
                }
            } else {
                Alert alerta = new Alert(Alert.AlertType.ERROR, "Cambie la sesi贸n o la sala de la proyecci贸n", ButtonType.OK);
                alerta.show();
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void anterior_onClick(ActionEvent event) {
        if (index <= 0) {
            index = 0;
        } else {
            index--;
        }
        cargarCartelera();
    }

    @FXML
    private void next_onAction(ActionEvent event) {
        if (index >= cartelera.size() - 1) {
            index = cartelera.size() - 1;
        } else {
            index++;
        }
        cargarCartelera();
    }

    @FXML
    private void sala_onAction(ActionEvent event) {
        bd = new conectarBD();

        if (!bd.insertarSala(Integer.parseInt(idSala.getText()), Integer.parseInt(filasText.getText()), Integer.parseInt(columnasText.getText()))) {
            System.err.println("Error insertando la sala");
        } else {
            System.out.println("Correcto");
        }

        salasCombo.clear();
        salasCombo = bd.cargarSalas();
        comboSala.setItems(salasCombo);
        comboSala.setValue(salasCombo.get(0));
    }

    private void cargarCartelera() {
        InputStream entrada = new ByteArrayInputStream(cartelera.get(index).getCartel());
        imgCartel1.setFitHeight(300);
        imgCartel1.setFitWidth(300);
        imgCartel1.setImage(new Image(entrada));

        String[] hora = cartelera.get(index).getSesion().split("-");

        idCartelera.setText(String.valueOf(cartelera.get(index).getIndice()));
        nombrePeli1.setText(cartelera.get(index).getTitulo());
        sesionPeli1.setText(hora[3]);
        salaPeli1.setText(String.valueOf(cartelera.get(index).getSala()));
        dateTimee1.setValue(LocalDate.of(Integer.parseInt(hora[0]), Integer.parseInt(hora[1]), Integer.parseInt(hora[2])));

    }

    @FXML
    private void cartel1_onMouseClicked(MouseEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filtroJPG = new FileChooser.ExtensionFilter("ficheros jpg (*.jpg)", new String[]{"*.JPG"});
            FileChooser.ExtensionFilter filtroPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", new String[]{"*.PNG"});
            fileChooser.getExtensionFilters().addAll((FileChooser.ExtensionFilter[]) (Object[]) new FileChooser.ExtensionFilter[]{filtroJPG, filtroPNG});
            this.ficheroImagen = fileChooser.showOpenDialog((Window) null);
            Image image = new Image(this.ficheroImagen.toURI().toURL().toString());
            this.imgCartel1.setImage(image);
        } catch (MalformedURLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private boolean comprobarPelicula(String sesion, int sala) {
        boolean flag = false;
        int i = 0;
        while (i < cartelera.size() && !flag) {
            if (cartelera.get(i).getSesion().equals(sesion) && cartelera.get(i).getSala() == sala) {
                flag = true;
            } else {
                i++;
            }
        }

        return flag;
    }

    @FXML
    private void facturacion_onAction(ActionEvent event) {
        bd = new conectarBD();
        bd.copiarTabla();
        factura = bd.facturar();
        imprimirFactura(factura);

    }

    private void imprimirFactura(ArrayList<claseFacturado> factura) {

        double total = 0.0;
        OutputStream file = null;
        Document document = null;
        PdfWriter pdfWriter = null;
        PdfContentByte pdfContentByte = null;
        Timestamp fechaAhora = null;
        String fecha = null;

        try {
            fechaAhora = new Timestamp(new Date().getTime());
            fecha = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(fechaAhora);

            file = new FileOutputStream(new File(fecha + ".pdf"));
            document = new Document();
            pdfWriter = PdfWriter.getInstance(document, file);

            document.open();

            pdfContentByte = pdfWriter.getDirectContent();

            PdfWriter.getInstance(document, file);

            document.open();

            Paragraph p;

            for (claseFacturado f : factura) {

                p = new Paragraph(f.getIndice() + "潞) " + f.getSesion(), new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL));
                p.setAlignment(Element.ALIGN_LEFT);
                document.add((Element) p);
                p = new Paragraph("Sala: " + f.getIdSala(), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
                p.setAlignment(Element.ALIGN_LEFT);
                document.add((Element) p);
                p = new Paragraph("Fila: " + f.getFila(), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
                p.setAlignment(Element.ALIGN_LEFT);
                document.add((Element) p);
                p = new Paragraph("Butaca: " + f.getColumna(), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
                p.setAlignment(Element.ALIGN_LEFT);
                document.add((Element) p);
                p = new Paragraph("Ocupado: " + f.isOcupado(), new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL));
                p.setAlignment(Element.ALIGN_LEFT);
                document.add((Element) p);

                total += PRECIO;

            }
            document.newPage();

            p = new Paragraph("Total comprados: " + factura.size(), new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
            p.setAlignment(Element.ALIGN_LEFT);
            document.add((Element) p);
            p = new Paragraph("Total recaudado: " + total + "$", new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD));
            p.setAlignment(Element.ALIGN_LEFT);
            document.add((Element) p);

            Desktop.getDesktop().open(new File(fecha + ".pdf"));
            document.close();

        } catch (FileNotFoundException | DocumentException ex) {

        } catch (IOException ex) {

        }



    }

    @FXML
    private void reset_onAction(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "縎eguro que quieres resetear la cartelera?", ButtonType.NO, ButtonType.YES);
        Optional<ButtonType> opciones = alerta.showAndWait();

        if (opciones.get().equals(ButtonType.YES)) {
            bd = new conectarBD();
            if (!bd.resetCartelera()) {
                System.err.println("Error al resetear la cartelera");
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cartelera reseteada correctamente", ButtonType.OK);
                alert.show();
            }
        }
    }

    @FXML
    private void btnResetFacturas_onAction(ActionEvent event){

    	Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "縎eguro que quieres resetear la cartelera?", ButtonType.NO, ButtonType.YES);
        Optional<ButtonType> opciones = alerta.showAndWait();

        if (opciones.get().equals(ButtonType.YES)) {
            bd = new conectarBD();
            if (!bd.resetCartelera()) {
                System.err.println("Error al resetear la cartelera");
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cartelera reseteada correctamente", ButtonType.OK);
                alert.show();
            }
        }
    }

}
