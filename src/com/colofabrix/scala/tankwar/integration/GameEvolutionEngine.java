package com.colofabrix.scala.tankwar.integration;

import org.uncommons.watchmaker.framework.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class GameEvolutionEngine<T> extends ModifiedGenerationalEvolutionEngine<T> {

    public GameEvolutionEngine(CandidateFactory<T> candidateFactory, EvolutionaryOperator<T> evolutionScheme, FitnessEvaluator<? super T> fitnessEvaluator, SelectionStrategy<? super T> selectionStrategy, Random rng) {
        super(candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng);
    }

    @Override
    public List<EvaluatedCandidate<T>> evolvePopulation(int populationSize, int eliteCount, Collection<T> seedCandidates, TerminationCondition... conditions) {
        if (eliteCount < 0 || eliteCount >= populationSize) {
            throw new IllegalArgumentException("Elite count must be non-negative and less than population size.");
        }

        if (conditions.length == 0) {
            throw new IllegalArgumentException("At least one TerminationCondition must be specified.");
        }

        satisfiedTerminationConditions = null;

        int currentGenerationIndex = 0;
        long startTime = System.currentTimeMillis();

        List<T> population = candidateFactory.generateInitialPopulation(populationSize, seedCandidates, rng);

        population = runCompetition(population);

        // Calculate the fitness scores for each member of the initial population.
        List<EvaluatedCandidate<T>> evaluatedPopulation = evaluatePopulation(population);
        EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural());
        PopulationData<T> data = EvolutionUtils.getPopulationData(evaluatedPopulation, fitnessEvaluator.isNatural(), eliteCount, currentGenerationIndex, startTime);

        // Notify observers of the state of the population.
        notifyPopulationChange(data);

        List<TerminationCondition> satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions);
        while (satisfiedConditions == null) {
            ++currentGenerationIndex;

            evaluatedPopulation = nextEvolutionStep(evaluatedPopulation, eliteCount, rng);

            evaluatedPopulation = runCompetitionInternal(evaluatedPopulation);

            EvolutionUtils.sortEvaluatedPopulation(evaluatedPopulation, fitnessEvaluator.isNatural());
            data = EvolutionUtils.getPopulationData(evaluatedPopulation, fitnessEvaluator.isNatural(), eliteCount, currentGenerationIndex, startTime);

            // Notify observers of the state of the population.
            notifyPopulationChange(data);

            satisfiedConditions = EvolutionUtils.shouldContinue(data, conditions);
        }

        this.satisfiedTerminationConditions = satisfiedConditions;

        return evaluatedPopulation;
    }

    private List<EvaluatedCandidate<T>> runCompetitionInternal(List<EvaluatedCandidate<T>> population) {
        List<T> work = new ArrayList<>(population.size());

        for( EvaluatedCandidate<T> candidate : population )
            work.add( candidate.getCandidate() );

        return this.evaluatePopulation(work);
    }

    protected abstract List<T> runCompetition(List<T> population);
}
