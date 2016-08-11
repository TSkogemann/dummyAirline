/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.skogemann.dummyairline;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Thomas Skogemann
 */
public class Main {

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("DummyAirlinePU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Flight flight = new Flight();
            flight.setDate(df.format(new Date()));
            flight.setDestination("IAD");
            flight.setFlightID("100");
            flight.setFlightNumber("SK925");
            flight.setNumberOfSeats(100);
            flight.setOrigin("CPH");
            flight.setTotalPrice(200);
            flight.setTraveltime(8);
            em.persist(flight);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
