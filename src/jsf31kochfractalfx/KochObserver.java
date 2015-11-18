/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf31kochfractalfx;

import calculate.Edge;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Nekkyou
 */
public class KochObserver implements Observer {

    int counter = 0;
    
    @Override
    public void update(Observable o, Object o1) {
        Edge e = (Edge)o1;
        System.out.println("De x waarde van het beginpunt = " + e.X1 + ", en de y waarde = " +  e.Y1 + System.lineSeparator() + "De x waarde van het eindpunt = " + e.X2 + ", en de y waarde = " + e.Y2 + System.lineSeparator());
        
        
    }
    
}
