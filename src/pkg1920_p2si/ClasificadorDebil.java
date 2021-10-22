/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1920_p2si;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author abp81
 */
public class ClasificadorDebil {
    int pixel;
    int direccion; //Si 0 --> "<=" si 1 --> ">"
    int umbral;
    double confianza=0; //valor de confianza del clasificador debil
    double error;
    
    
    //constructor
    public ClasificadorDebil(int pix,int um,int dir){
        pixel = pix;//dimension datos
        umbral = um;
        direccion=dir;
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.pixel;
        hash = 97 * hash + this.direccion;
        hash = 97 * hash + this.umbral;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.confianza) ^ (Double.doubleToLongBits(this.confianza) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.error) ^ (Double.doubleToLongBits(this.error) >>> 32));
        return hash;
    }


    /*
    Este constructor nos sirve para construir los 
    clasificadores debiles al cargarlos desde el fichero
    por eso tienen la confianza
    */
    ClasificadorDebil(int pix, int umb, int dir, double conf) {
        pixel = pix;
        umbral = umb;
        direccion = dir;
        confianza = conf;
    }
    public List<Integer> aplicarClasificadorDebil(List<Imagen> imgs){
        List<Integer> resultados;
        resultados = new ArrayList<>();
        
        imgs.forEach((im) -> {
            if(this.direccion == -1){
                if((im.getImageData()[this.pixel]) < this.umbral){
                    resultados.add(1);
                }
                else{
                    resultados.add(-1);
                }
            }
            else{
                if((im.getImageData()[this.pixel])>= this.umbral){
           
                    resultados.add(1);
                }
                else{
                    resultados.add(-1);
                }
            }
        });
        return resultados;
    }
    
    @Override
    public String toString(){
        String clasificador = "Dir: "+this.direccion + " Pix: " + this.pixel + 
                " Umb: " + this.umbral +
                " Err: " + this.error + " Conf: " + this.confianza;
        return clasificador;
    }
}
