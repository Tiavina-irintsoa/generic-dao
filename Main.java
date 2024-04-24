package view;

import java.util.Vector;

import model.mapping.*;
public class Main {
    public static void main(String[] args)  throws Exception {
        // Place p = new Place( 99 , "test"  );
        // Place p2 = new Place( " huhu "  );
        Huhu h = new Huhu();
        // h.setHaha("2022-01-01");
        // Evenement e = new Evenement( 1 , "huhu" , "2022-01-02 01:02:01" , null , null  );
        // e.setId_evnement(1);
        // BddToObject.delete(null, e);
        // Object o = BddToObject.findById(null, e);
        // System.out.println(o.toString());
        // BddToObject.insert(null, h);
        // Evenement e1 = new Evenement(  );
        // e1.setDateconcert("2022-02-02 12:01:12");
        // BddToObject.update( null , e , e1);
        Vector l = BddToObject.select(null , h);
        for( int i = 0 ; i != l.size() ; i++ ){
            System.out.println( l.get(i).toString() );
        }
    }
}
