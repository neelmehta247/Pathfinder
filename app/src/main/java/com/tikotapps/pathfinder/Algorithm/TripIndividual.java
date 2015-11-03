package com.tikotapps.pathfinder.Algorithm;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by neel on 02/11/15 at 12:15 AM.
 */
public class TripIndividual {

    private ArrayList<LocationNode> locations = new ArrayList<>();

    private double fitness = 0, time = 0; //This is to save excess processing

    public TripIndividual() {
        for (int i = 0; i < TripManager.numberOfCities(); i++) {
            locations.add(null);
        }
    }

    public TripIndividual(ArrayList<LocationNode> locations) {
        this.locations = locations;
        generateIndividual();
    }

    public void generateIndividual() {
        for (int i = 0; i < TripManager.numberOfCities(); i++) {
            setLocation(i, TripManager.getLocation(i));
        }

        Collections.shuffle(locations);
    }

    public LocationNode getLocation(int index) {
        return locations.get(index);
    }

    public void setLocation(int index, LocationNode location) {
        locations.set(index, location);

        fitness = 0; //If tour changed, so does the fitness and time taken change
        time = 0;
    }

    public void removeLocation(LocationNode locationNode) {
        locations.remove(locationNode);

        fitness = 0;
        time = 0;
    }

    public double getFitness() {
        if (fitness == 0) { //if fitness is 0, it needs to be calculated
            fitness = 1 / getTime();
        }
        return fitness;
    }

    public double getTime() {
        if (time == 0) { //If time is 0, it needs to be computed
            double finalTime = 0;
            for (int i = 0; i < tripSize(); i++) {
                LocationNode fromLocation = getLocation(i);
                LocationNode destinationNode;

                if (i + 1 < tripSize()) {//If not the last location node
                    destinationNode = getLocation(i + 1);
                } else {
                    destinationNode = getLocation(0); //Return to the beginning
                }

                finalTime += destinationNode.timeTillFinishedFrom(fromLocation);
            }
            time = finalTime;
        }
        return time;
    }

    public int tripSize() {
        return locations.size();
    }

    public boolean contains(LocationNode locationNode) {
        return locations.contains(locationNode);
    }

    @Override
    public String toString() {
        String gene = "|";
        for (int i = 0; i < tripSize(); i++) {
            gene += getLocation(i) + "|";
        }
        return gene;
    }
}
