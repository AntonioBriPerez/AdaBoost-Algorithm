/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1920_p2si;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anton
 */
//
@SuppressWarnings("unchecked")
public class Algoritmo {
    
    List <Double> d;
    ArrayList<ClasificadorDebil> fuerteAlg;
    
    Algoritmo(int i) {
        fuerteAlg = new ArrayList<>();
        d = new ArrayList<>();
 
        while(d.size() < i){
            d.add( (double) 1 / i );
        }
    }
    
    public ClasificadorDebil generarClasifAzar(int dimension) {
        int pixel = (int)(Math.random()*dimension);
        int umbral = (int) (Math.random()*255);
        int dir = (int)(Math.random()*2);
        if(dir == 0){
            dir = -1;
        }
        return (new ClasificadorDebil(pixel,umbral,dir));
        
    }
    
    
    
     public double obtenerErrorClasificador(ClasificadorDebil pf, 
             List<Integer> clasificador, List<Imagen> datos,
             List<Double> pertenece, int numero){
         
        double error=0;
        for(int i=0; i<datos.size();i++){
            if(clasificador.get(i) == -1){
               if(datos.get(i).numero==numero)
                    error+=d.get(i);
            }
            else if(clasificador.get(i)==1){
                if(datos.get(i).numero!=numero)
                    error+=d.get(i);
            }
        }
        
        pf.error=error;
        return error;
       
    }
    
    void entrenarAda(List<Imagen> test, int digito) {
        int i = 0;
        //la m indica el número de clasificadores
        //cuantos mas mejor
        
       
        //BUCLE 1:
        for(int m=0; m<400 /* valor experimental */;m++){
            ClasificadorDebil debil;
            ArrayList<ClasificadorDebil> debiles = new ArrayList<>();
            List<Integer> resultados = null;
           
            double err=0;
            //BUCLE 2
            for(int j = 0; j<400; /*valor experimental*/ j++){
                debil =  generarClasifAzar(784);
               
                resultados = debil.aplicarClasificadorDebil(test);
                err = obtenerErrorClasificador(debil,resultados,test, d,digito);
                debil.error=err;
                
                debiles.add(debil);
            }
            
            debil = getMejor(debiles); 
            
            double confianza = 0.5*Math.log(((1-debil.error) / debil.error));
            debil.confianza = confianza;
            resultados = debil.aplicarClasificadorDebil(test);
            actualizarPesos(confianza,digito,test,resultados);
            
            
            fuerteAlg.add(debil);
            
            i++;
        }
        
        
    }
   
    

    public ClasificadorDebil getMejor(ArrayList<ClasificadorDebil> debiles) {
        ClasificadorDebil aux = debiles.get(0);
        for(int i=0; i<debiles.size();i++){
            if(debiles.get(i).error < aux.error){
                aux = debiles.get(i);
            }
        }
        return aux;
    }

    public void actualizarPesos(double confianza, int digito, List<Imagen> test,
            List<Integer> resultados) {
        
        double nuevoD;
        double z = 0;
        
        for(int i=0; i<d.size();i++){
            int res = resultados.get(i);
            int y = 1;
            if((res == 1 && test.get(i).numero != digito) || 
                res == -1 && test.get(i).numero == digito){
                y=-1;
            }
            nuevoD = d.get(i)*Math.exp(-confianza*y);
            d.set(i, nuevoD);
            z+=nuevoD;
        }
        for(int j=0; j<d.size();j++){
            d.set(j, d.get(j)/z);
        }
       
    }
    
    public double aplicarAdaBoost(Imagen im){
        double pertenencia = 0;
        ArrayList<Imagen> aux = new ArrayList<>();
        aux.add(im);
        
        for(int i=0; i<fuerteAlg.size();i++){
            double aux2 = fuerteAlg.get(i).aplicarClasificadorDebil(aux).get(0);
            pertenencia += aux2*fuerteAlg.get(i).confianza;
        }
        return pertenencia;
    }
    
    public ClasificadorFuerte getCF() throws IOException{
        ArrayList<ClasificadorDebil> aux = new ArrayList();
        for(int i=0; i<this.fuerteAlg.size();i++){
           aux.add(this.fuerteAlg.get(i));
        }
        ClasificadorFuerte cf = new ClasificadorFuerte(aux);
        return cf;
    }
    @Override
    public String toString(){
        String aux="";
        for(int i=0; i<fuerteAlg.size();i++){
            aux+=fuerteAlg.get(i).toString();
            aux +="\n";
        }
        
        return aux;
    }
    
    
    
}
