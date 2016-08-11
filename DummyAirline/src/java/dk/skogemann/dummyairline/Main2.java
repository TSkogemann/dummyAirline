/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.skogemann.dummyairline;

import java.text.ParseException;

/**
 *
 * @author Thomas Skogemann
 */
public class Main2 {
    
    public static void main(String[] args) throws ParseException {
        
    
    ApiController ctrl = new ApiController();
        FlightResponse temp = ctrl.search("CPH",null, "2016-08-11T13:00:00.000Z", 2);
        FlightResponse temp2 = ctrl.search("CPH","IAD", "2016-08-11T13:00:00.000Z", 2);
        System.out.println(temp);
        System.out.println("With endLoc " + temp2);
            }
}
