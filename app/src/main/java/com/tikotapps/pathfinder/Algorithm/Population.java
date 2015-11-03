package com.tikotapps.pathfinder.Algorithm;

/**
 * Created by neel on 02/11/15 at 12:14 AM.
 */
public class Population {

    TripIndividual[] trips;

    public Population(int populationSize, boolean initialise) {
        trips = new TripIndividual[populationSize];

        if (initialise) {
            for (int i = 0; i < populationSize(); i++) {
                TripIndividual individual = new TripIndividual();
                individual.generateIndividual();
                saveTrip(i, individual);
            }
        }
    }

    public int populationSize() {
        return trips.length;
    }

    public void saveTrip(int index, TripIndividual individual) {
        trips[index] = individual;
    }

    public void eliminate(LocationNode locationNode) {
        for (int i = 0; i < trips.length; i++) {
            trips[i].removeLocation(locationNode);
        }
    }

    public TripIndividual getTrip(int index) {
        return trips[index];
    }

    public TripIndividual getFittest() {
        TripIndividual fittest = trips[0];

        for (int i = 0; i < populationSize(); i++) {
            if (fittest.getFitness() <= getTrip(i).getFitness()) {
                fittest = getTrip(i);
            }
        }

        return fittest;
    }
}
