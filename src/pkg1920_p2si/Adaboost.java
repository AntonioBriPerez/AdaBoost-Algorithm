
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1920_p2si;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author anton
 */
//
@SuppressWarnings("unchecked")
public class Adaboost{
    ArrayList aprender = new ArrayList();
    public final MNISTLoader imageLoader = new MNISTLoader();
    
    public static void main(String[] args) throws IOException{
        
        if(args.length != 2){
            System.err.println("Error en los argumentos");
        }
        
        else if("-t".equals(args[0])){
            
       
            Instant start = Instant.now();
            ArrayList<ClasificadorFuerte> fuertes = entrenarAlgoritmo();
            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
            System.out.println("Tiempo entrenamiento: " + timeElapsed/1000 + 
                    " segundos");
            
            
            
            
            guardaFuertesFichero(fuertes,args[1]);
        }
        
        // Adaboost <fichero_origen_CF> <ruta_imagen>
        
        else{
            
            faseDeCarga(args);   
        }
    }
    /*
    Este metodo recibe los clasificadores fuertes y los guarda en un fichero
    */
    private static void guardaFuertesFichero(ArrayList<ClasificadorFuerte> fuertes, String arg) throws FileNotFoundException, IOException {
    
        ArrayList<String> cadenas = new ArrayList();
        
        for(int i=0; i<fuertes.size();i++){
            cadenas.add(fuertes.get(i).toString());
        }
        
        int i=0;
        try (FileWriter fileWriter = new FileWriter(arg+".txt")) {
            for(i=0; i<cadenas.size();i++){
                String aux = cadenas.get(i);
                fileWriter.write("*CLASIFICADOR: "+ i + "\n");
                fileWriter.write(aux);
            }
        }
        System.out.println("FUERTES GUARDADOS EN:" + arg+".txt");
        
    }
    
    private static void pruebaCDvalido(ArrayList<ClasificadorFuerte> fuertes){
        int contador=0;
        for(int i=0; i<fuertes.size();i++){
            ClasificadorFuerte aux = fuertes.get(i);
            for(int j=0; j<aux.debiles.size();j++){
                if(aux.debiles.get(j).error >= 0.5){
                    contador++;
                }
            }
            break;       
        }
        System.out.println("Hay " + contador + " clasificadores malos");
    }
    private static ArrayList<ClasificadorFuerte> entrenarAlgoritmo() throws IOException {
        Entrenamiento entrenamiento = new Entrenamiento();
        ArrayList<ClasificadorFuerte> fuertes = new ArrayList();
        double umbral = 0.08;
        for(int i=0; i<=9; i++){
            boolean aux = true;
            while(aux){
                entrenamiento.entrenaAlgoritmo(i);
                double error = entrenamiento.valida(entrenamiento.fuertesEnt.get(i),i);
                
                if(error <= umbral){
                    aux = false;
                    fuertes.add(entrenamiento.fuertesEnt.get(i).getCF());
                }
                else{
                    System.err.println(i + ": "+ error);
                }
            }
        }
        imprimirPorcentajes(fuertes,entrenamiento);
        
       // pruebaCDvalido(fuertes);
        return fuertes;
    }
    
    private static void imprimirPorcentajes(ArrayList<ClasificadorFuerte> fuertes, Entrenamiento ent){
        ArrayList<Integer> resultadosValid = new ArrayList();
        ArrayList<Integer> resultadosTest = new ArrayList();
        resultadosValid = clasifica(ent.validSet,ent);
        int erroresValidacion=0,erroresEntrenamiento=0;
        for(int i=0;i<ent.validSet.size();i++){
                if(ent.validSet.get(i).numero != resultadosValid.get(i)){
                    erroresValidacion++;
                }
            }
        double aciertoValidacion = (double) erroresValidacion/ent.validSet.size();
        System.out.println("Validacion: " + (100-aciertoValidacion*100) + "%");
        
        resultadosTest = clasifica(ent.test,ent);
        
        for(int i=0;i<ent.test.size();i++){
                if(ent.test.get(i).numero != resultadosTest.get(i)){
                    erroresEntrenamiento++;
                   
                }
            }
        double aciertoTest = (double) erroresEntrenamiento/ent.test.size();
        System.out.println("Entrenamiento: " + (100-aciertoTest*100) + "%");
        
       
        
        
        
    }
    
    private static ArrayList<Integer> clasifica(ArrayList<Imagen> conjunto,
    Entrenamiento ent){
        ArrayList<Double> pertenencias = new ArrayList();
        ArrayList<Integer> digitos = new ArrayList<>(conjunto.size());
        for(Imagen imagen : conjunto){
            double mejor = -99999;
            int digito = -1;
            for(int i=0; i<10; i++){
               pertenencias.add(i, ent.fuertesEnt.get(i).aplicarAdaBoost(imagen));
               if(pertenencias.get(i) > mejor){
                   mejor = pertenencias.get(i);
                   digito = i;
               }
            }
            digitos.add(digito);
        }
        return digitos;   
    }

    private static void faseDeCarga(String[] args) throws NumberFormatException, IOException {
        String path = args[1]; //ruta de la imagen
        ArrayList<ClasificadorDebil> debiles = new ArrayList<>();
        ArrayList<ClasificadorFuerte> fuertes = new ArrayList<>();
        CargaFuertes(args, debiles,fuertes);
        ArrayList<Imagen> arrImg = new ArrayList<>();
        File fileIm = new File(path);
        if(fileIm.isFile()){
            Imagen im = new Imagen(fileIm.getAbsoluteFile());
            arrImg.add(im);
            int resultado = resolver(arrImg, fuertes);
            if(resultado==-1){
                System.err.println("No se ha podido reconocer el numero");
            }
            else{
                System.out.println("EL DIGITO CARGADO ES UN: " + resultado);
            }
             
            //Descomentar si se quiere ver la imagen por parametro
            
            /*
            Imagen img = new Imagen(fileIm);
            MostrarImagen imgShow = new MostrarImagen();
            imgShow.setImage(img);
            imgShow.mostrar();
            */
        }
        else{
            System.err.println("Error al cargar la imagen");
        }
    }

    

    private static void CargaFuertes(String[] args, ArrayList<ClasificadorDebil> debiles,
            ArrayList<ClasificadorFuerte> fuertes) throws IOException, NumberFormatException, FileNotFoundException {
        File file = new File("./"+args[0]+".txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String linea;
        
        while((linea = br.readLine()) != null){
            
            int dir, pix, umb;
            double conf;
            
            //Si se encuentra indica el principio de un
            //nuevo clasificador fuerte
            if(linea.charAt(0)=='*'){
                continue;
            }
            else{
                String auxDir = getDir(linea);
                String auxPix = getPix(linea);
                String auxUmb = getUmb(linea);
                String auxConf = getConf(linea);
                
                dir = Integer.valueOf(auxDir);
                pix = Integer.valueOf(auxPix);
                umb = Integer.valueOf(auxUmb);
                conf = Double.valueOf(auxConf);
                
                debiles.add(new ClasificadorDebil(pix,umb,dir,conf));
            }
        }
        
        splitFuertes(debiles, fuertes);
        
    }

    private static void splitFuertes(ArrayList<ClasificadorDebil> debiles, ArrayList<ClasificadorFuerte> fuertes) {
        ArrayList<ClasificadorDebil> aux = new ArrayList();
        
        
        int splitter=0;
        for(int i=0; i<debiles.size(); i++){
            aux.add(debiles.get(i));
            splitter++;
            //nos vamos guardando los clasificadores del fichero
            if(splitter == debiles.size() / 10){
                ClasificadorFuerte cf = new ClasificadorFuerte(aux);
                fuertes.add(cf);
                aux.clear();
                splitter=0;
            }
        }
    }
    
   
    
    //Saca la direccion de los cf del fichero
    public static String getDir(String linea) {
        String aux="";
        for(int i=0; i<linea.length();i++){
            if(linea.charAt(i)==':'){
                //lee negativo
                if(linea.charAt(i+2)=='-'){
                    aux+=linea.charAt(i+2);
                    aux+=linea.charAt(i+3);
                    break;
                }
                
                //lee positivo
                else{
                    aux+=linea.charAt(i+2);
                    break;
                }
               
            }
        }
        
     
        return aux;
    }
    //Saca el pixel de los cf del fichero
    public static String getPix(String linea) {
         String aux="";
        int counter=0;
        for(int i=0; i<linea.length();i++){
            //Se viene el error
            if(linea.charAt(i)==':'){
                counter++;
            }
            if(counter==2){
               i+=2; //saltamos los : y el blank
               while(linea.charAt(i)!=' '){
                   aux+=linea.charAt(i);
                   i++;
               }
               break;
            }
        }
        
        return aux;
    
    }
    //Saca el umbral de los cf del fichero
    public static String getUmb(String linea) {
        String aux="";
        int counter=0;
        for(int i=0; i<linea.length();i++){
            //Se viene el error
            if(linea.charAt(i)==':'){
                counter++;
            }
            if(counter==3){
               i+=2; //saltamos los : y el blank
               while(linea.charAt(i)!=' '){
                   aux+=linea.charAt(i);
                   i++;
               }
               break;
            }
        }
        
      
        return aux;
    
    }
    //Saca la confianza de los cf del fichero
    public static String getConf(String linea) {
        String aux="";
        int counter=0;
        for(int i=0; i<linea.length();i++){
            //Se viene el error
            if(linea.charAt(i)==':'){
                counter++;
            }
            if(counter==5){
               if(linea.charAt(i)!=' ' && 
                       linea.charAt(i)!=':'){
                   aux+=linea.charAt(i);
               }
               
            }
        }
        return aux;
    }
    /*
    Este metodo se encarga de sacar las pertenencias de los clasificadores
    para una imagen y de decidir si se trata de un dígito u otro
    */
    
    /*
    Para el ultimo hito neceistaremos un arraylist de clasificadores
    fuertes y tendremos que almacenar la pertenencia de cada uno de 
    esos clasificadores fuertes y quedarnos con el clasificador de mayor
    pertenencia. Si es el primer clasificador del vector corresponderá al
    digito 0 y asi sucesivamente
    */
     public static int resolver (ArrayList<Imagen> img, ArrayList<ClasificadorFuerte> fuertes){
        double pertenencia=0;
        ArrayList<Double> pertenencias = new ArrayList();
        for(Imagen imagen:img){
            ArrayList<Imagen> im2 = new ArrayList<>();
            im2.add(imagen);
            double mejor = -9999;
            for(int i=0; i<fuertes.size();i++){
                ClasificadorFuerte aux = fuertes.get(i);
                for(int j=0; j<aux.debiles.size();j++){
                    
                    double aux2 = aux.debiles.get(j).aplicarClasificadorDebil(im2).get(0);
                    pertenencia +=aux2+aux.debiles.get(j).confianza;
                }
                pertenencias.add(pertenencia);
                pertenencia=0;
            }
            
            //Controlamos el arrayList no tenga negativos
            boolean allNegative=true;
            for(int i=0; i<pertenencias.size();i++){
                if(pertenencias.get(i)>0){
                    allNegative = false;
                }
            }
            if(allNegative){
                System.err.println("No se ha reconocido el numero ");
                return -1;
            }
            
          
            //Buscamos la maxima pertenencia y devolvemos su posicion
            //que será el digito solución
            double maximum=Collections.max(pertenencias);
            for(int i=0; i<pertenencias.size();i++){
                if(pertenencias.get(i)==maximum){
                    return i;
                }
            }   
        }
        return -1;
     }
    
}
    

     
    
    

