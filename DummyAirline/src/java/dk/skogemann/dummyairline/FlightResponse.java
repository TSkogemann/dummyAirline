/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.skogemann.dummyairline;

import java.util.List;

/**
 *
 * @author Thomas Skogemann
 */

public class FlightResponse {

    private String airline;
    private List<Flight> flights;

    @Override
    public String toString() {
        return "FlightResponse{" + "airline=" + airline + ", flights=" + flights + '}';
    }
    
    

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

}
