/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;


public class claseFacturado {
    private int indice;
    private int idSala;
    private int fila;
    private int columna;
    private String sesion;
    private boolean ocupado;

    claseFacturado(int fila, int columna) {
      this.fila = fila;
        this.columna = columna;
    }

    claseFacturado() {

    }

    public claseFacturado(int indice, int fila, int columna, int idSala, String sesion, boolean ocupado) {
        this.indice = indice;
        this.fila = fila;
        this.columna = columna;
        this.idSala = idSala;
        this.sesion = sesion;
        this.ocupado = ocupado;
    }
    
    public claseFacturado(int indice, int idSala, int fila, int columna, String sesion) {
        this.indice = indice;
        this.idSala = idSala;
        this.fila = fila;
        this.columna = columna;
        this.sesion = sesion;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public String getSesion() {
        return sesion;
    }
    
    public boolean isOcupado() {
        return ocupado;
    }

    public void setSesion(String sesion) {
        this.sesion = sesion;
    }

    
}
