package com.tikotapps.pathfinder.Algorithm;

/**
 * Created by neel on 02/11/15 at 12:12 AM.
 */
public class GeneticAlgorithm {

    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

    public static Population evolvePopulation(Population population) {
        Population newPopulation = new Population(population.populationSize(), false);

        int elitismOffset; //This is to keep our best individual, so that the overall max fitness doesn't fall
        if (elitism) {
            newPopulation.saveTrip(0, population.getFittest());
            elitismOffset = 1;
        }

        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            //Selecting parents
            TripIndividual parent1 = tournamentSelection(population);
            TripIndividual parent2 = tournamentSelection(population);

            TripIndividual child = crossover(parent1, parent2); //Crossing over

            mutate(child); //Mutating the child
            newPopulation.saveTrip(i, child); //Saving that child to the new population
        }

        return newPopulation;
    }

    public static TripIndividual tournamentSelection(Population population) {
        Population tournament = new Population(tournamentSize, false);

        for (int i = 0; i < tournament.populationSize(); i++) {
            int randomInt = (int) (Math.random() * tournament.populationSize());
            tournament.saveTrip(i, population.getTrip(randomInt));
        }

        return tournament.getFittest();
    }

    public static void mutate(TripIndividual individual) {
        for (int position1 = 0; position1 < individual.tripSize(); position1++) {
            if (Math.random() < mutationRate) {
                int position2 = (int) (individual.tripSize() * Math.random());
                LocationNode location1 = individual.getLocation(position1);
                LocationNode location2 = individual.getLocation(position2);

                individual.setLocation(position1, location2);
                individual.setLocation(position2, location1);
            }
        }
    }

    public static TripIndividual crossover(TripIndividual parent1, TripIndividual parent2) {
        TripIndividual child = new TripIndividual();

        int startPosition = (int) (Math.random() * parent1.tripSize());
        int endPosition = (int) (Math.random() * parent1.tripSize());

        if (startPosition > endPosition) {
            //Swap if start is greater than end
            int tmp = startPosition;
            startPosition = endPosition;
            endPosition = tmp;
        }

        for (int i = startPosition; i < endPosition; i++) {
            child.setLocation(i, parent1.getLocation(i));
        }

        for (int i = 0; i < parent2.tripSize(); i++) {
            if (!child.contains(parent2.getLocation(i))) {
                for (int j = 0; j < child.tripSize(); j++) {
                    if (child.getLocation(j) == null) {
                        child.setLocation(j, parent2.getLocation(i));
                        break;
                    }
                }
            }
        }
        return child;
    }
}
