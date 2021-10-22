/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1920_p2si;


import java.util.ArrayList;


/**
 *
 * @author anton
 */


@SuppressWarnings("unchecked")
public class Entrenamiento {
    public ArrayList<Imagen> test;
    public ArrayList<Imagen> validSet;
    public ArrayList<Algoritmo> fuertesEnt;
    
    MNISTLoader imageLoader;
    
   
    public Entrenamiento(){
        imageLoader = new MNISTLoader();
        imageLoader.loadDBFromPath("./mnist_1000");
        test = new ArrayList<>();
        int porcentajeEnt=85;
        
        //cargamos imagenes para el test
        
        for(int i=0; i<10;i++){
            //Cogemos 80 imagenes de cada número para el test
            test.addAll(imageLoader.getImageDatabaseForDigit(i).subList(0,
                    porcentajeEnt));
           
        }
        
        
        validSet = new ArrayList<>();
        for(int i=0; i<10;i++){
            //Cogemos 20 imagenes de cada número para la validación
            validSet.addAll(imageLoader.getImageDatabaseForDigit(i).subList(porcentajeEnt,
            imageLoader.getImageDatabaseForDigit(i).size()));
            
        }
        
        fuertesEnt = new ArrayList<>(10);
        Algoritmo alg = new Algoritmo(1);
       
        while(fuertesEnt.size() < 10){
            fuertesEnt.add(alg);
        }
         
    }
    
    @Override
    public String toString() {
        return "Entrenamiento{" + "fuertesEnt=" + fuertesEnt + '}';
    }
  
    public double valida (Algoritmo clasFuerte, int num){
        int fallos = 0;
        for(int i=0; i < validSet.size(); i++){  
            if(validSet.get(i).numero == num){
                if(clasFuerte.aplicarAdaBoost(validSet.get(i)) < 0){
                    fallos++;
                }
                
            }
            if(validSet.get(i).numero != num){
                if(clasFuerte.aplicarAdaBoost(validSet.get(i)) > 0){
                    fallos++;
                }
            }
        }
        
        return (double)fallos/validSet.size();
    }
    
    void entrenaAlgoritmo(int digito) {
       Algoritmo adaFuerte = new Algoritmo(test.size());
       adaFuerte.entrenarAda(test,digito);
       this.fuertesEnt.set(digito, adaFuerte);
       
    }
    
    
    
}