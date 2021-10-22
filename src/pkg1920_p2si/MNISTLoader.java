/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1920_p2si;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author fidel
 */

@SuppressWarnings("unchecked")
public class MNISTLoader {

    private ArrayList[] mnistImageDB;
    
    void loadDBFromPath(String path){
        
        int imagesCount=0;
        
        //Una arrayList por dígito almacenará las imágenes
        mnistImageDB = new ArrayList[10];
        
        //Creo un array list de imagenes para cada dígito y cargo cada una
        //de las imágenes disponibles por dígito
        for (int i=0;i<10; i++){
            mnistImageDB[i] = new ArrayList();
            System.out.println("Loaded digit "+i);
            File[] files = new File(path,"d"+i).listFiles();
            if(files == null){
                System.err.println("Error al cargar imgs");
                return;
            }
            for (File file : files) {
                if (file.isFile()) {
                   mnistImageDB[i].add(new Imagen(file.getAbsoluteFile(),i));
                   imagesCount++;
                }
            }
        }
        
        System.out.println("Loaded "+ imagesCount + " images...");
        
    }
    
    ArrayList getImageDatabaseForDigit(int digit){
        return mnistImageDB[digit];
    }
    
}
