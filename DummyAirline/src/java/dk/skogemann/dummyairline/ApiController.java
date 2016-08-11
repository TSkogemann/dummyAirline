/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.skogemann.dummyairline;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Thomas Skogemann
 */
@Controller
@RequestMapping(value = "/api/")
public class ApiController {

    EntityManagerFactory emf;
    DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    DateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ApiController() {

        emf = Persistence.createEntityManagerFactory("DummyAirlinePU");
    }

    @RequestMapping(value = "/flightinfo/{startLoc}/{endLoc}/{date}/{tickets}", method = RequestMethod.GET)
    @ResponseBody
    public FlightResponse search(
            @PathVariable("startLoc") String startLoc,
            @PathVariable("endLoc") String endLoc,
            @PathVariable("date") String date,
            @PathVariable("tickets") int ticket) throws ParseException {

        return doSearch(startLoc, endLoc, date, ticket);
    }

    @RequestMapping(value = "/flightinfo/{startLoc}/{date}/{tickets}", method = RequestMethod.GET)
    @ResponseBody
    public FlightResponse search(
            @PathVariable("startLoc") String startLoc,
            @PathVariable("date") String date,
            @PathVariable("tickets") int ticket) throws ParseException {

        return doSearch(startLoc, null, date, ticket);
    }

    private FlightResponse doSearch(String startLoc, String endLoc, String date, int ticket) throws ParseException {

        Date parsedDate = isoFormat.parse(date);
        date = simpleFormat.format(parsedDate);

        EntityManager em = emf.createEntityManager();
        TypedQuery<Flight> query;
        if (endLoc == null) {

            query = em.createQuery("SELECT f FROM Flight f WHERE f.origin =:origin "
                    + "AND f.date =:date "
                    + "AND f.numberOfSeats >:ticket ", Flight.class);
        } else {
            query = em.createQuery("SELECT f FROM Flight f WHERE f.origin =:origin "
                    + "AND f.destination =:destination "
                    + "AND f.date =:date "
                    + "AND f.numberOfSeats >:ticket ", Flight.class);
            query.setParameter("destination", endLoc);
        }
        query.setParameter("origin", startLoc)
                .setParameter("date", date)
                .setParameter("ticket", ticket);

        List<Flight> resultList = query.getResultList();
        FlightResponse res = new FlightResponse();
        res.setFlights(resultList);
        res.setAirline("Skoge AirLines");
        return res;
    }

    // Reservation and response
    @RequestMapping(value = "/reservation/{flightId}", method = RequestMethod.POST)
    @ResponseBody
    public ReservationResponse bookFlight(
            @PathVariable("flightId") String flightId,
            @RequestBody Reservation reservation) throws ParseException {

        ReservationResponse res = new ReservationResponse();
        res.setPassengers(reservation.getPassengers());
        res.setReserveeName(reservation.getReserveeName());

        EntityManager em = emf.createEntityManager();
        TypedQuery<Flight> query = em.createQuery("SELECT f FROM Flight f WHERE f.flightID =:flightID ", Flight.class)
                .setParameter("flightID", reservation.getFlightID());

        List<Flight> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            throw new RuntimeException("No flight with ID " + reservation.getFlightID());
        }
        Flight flight = resultList.get(0);
        res.setOrigin(flight.getOrigin());
        res.setNumberOfSeats(flight.getNumberOfSeats());
        res.setFlightTime(flight.getTraveltime());
        res.setFlightNumber(flight.getFlightNumber());
        res.setDestination(flight.getDestination());
        res.setDate(isoFormat.parse(simpleFormat.format(flight.getDate()) + "T00:00:00.000Z"));

        return res;
    }

    @ExceptionHandler
    public ResponseEntity<JsonError> handleException(Exception e) {
        e.printStackTrace();
        JsonError msg = new JsonError();
        msg.setMessage(e.getMessage());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<JsonError> handleHttpClientException(HttpClientErrorException e) {
        e.printStackTrace();
        JsonError msg = new JsonError();
        msg.setMessage(e.getResponseBodyAsString());
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
    }
}
