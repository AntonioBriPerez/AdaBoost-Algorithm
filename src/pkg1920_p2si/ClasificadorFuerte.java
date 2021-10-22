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
public class ClasificadorFuerte {
    
   public ArrayList<ClasificadorDebil> debiles;
    
    public ClasificadorFuerte(ArrayList<ClasificadorDebil> cd){
        debiles = new ArrayList();
        
        for(int i=0; i<cd.size();i++){
            debiles.add(cd.get(i));
        }
    }
    @Override
    public String toString(){
        String aux="";
        for(int i=0; i<debiles.size();i++){
            aux+=debiles.get(i).toString();
            aux+="\n";
        }
        
        return aux;
    }
    
    void aplicaClasificadorFuerte(String arg) {
     
       
    }
}
