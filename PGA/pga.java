package it.unisa.elephant56.examples.onemax;

import org.apache.hadoop.fs.Path;

import it.unisa.elephant56.core.Driver;
import it.unisa.elephant56.core.SequentialDriver;
import it.unisa.elephant56.core.common.Properties;
import it.unisa.elephant56.user.common.BitStringIndividual;
import it.unisa.elephant56.user.operators.BitStringMutation;
import it.unisa.elephant56.user.operators.BitStringSinglePointCrossover;
import it.unisa.elephant56.user.operators.OneMaxFitnessEvaluation;
import it.unisa.elephant56.user.operators.RandomBitStringInitialisation;
import it.unisa.elephant56.user.sample.common.fitness_value.IntegerFitnessValue;
import it.unisa.elephant56.user.sample.operators.elitism.BestIndividualsElitism;
import it.unisa.elephant56.user.sample.operators.parents_selection.RouletteWheelParentsSelection;
import it.unisa.elephant56.user.sample.operators.survival_selection.RouletteWheelSurvivalSelection;


public class BitStringIndividual extends Individual {
private List<Boolean> bits;
public BitStringIndividual(int size) {
bits = new ArrayList<>(size);
}
public void set(int index, boolean value) {
bits.set(index, value);
}
public boolean get(int index) {
return bits.get(index);
}
}
public int size() {
return bits.size();
}

public class IntegerFitnessValue extends FitnessValue {
private int number;
public IntegerFitnessValue(int value) {
number = value;
}
public int get() {
return number;
}
@Override
public int compareTo(FitnessValue other) {
if (other == null)
return 1;
Integer otherInteger = ((IntegerFitnessValue) other).get();
Integer.compare(number, otherInteger);
}
}

public class OneMaxFitnessEvaluation
extends FitnessEvaluation<BitStringIndividual, IntegerFitnessValue> {
@Override
public IntegerFitnessValue evaluate(
IndividualWrapper<BitStringIndividual, IntegerFitnessValue> wrapper) {
BitStringIndividual individual = wrapper.getIndividual();
int count = 0;
for (int i = 0; i < individual.size(); i++)
if (individual.get(i))
count++;
return new IntegerFitnessValue(count);
}
}



public class RandomBitStringInitialization
extends Initialization<BitStringIndividual, IntegerFitnessValue> {
public RandomBitStringInitilization(..., Properties userProperties, ...) {
individualSize = userProperties.getInt(INDIVIDUAL_SIZE_PROPERTY);
random = new Random();
}
@Override
public IndividualWrapper<BitStringIndividual, IntegerFitnessValue>
generateNextIndividual(int id) {
BitStringIndividual individual = new BitStringIndividual(individualSize);
for (int i = 0; i < individualSize; i++)
individual.set(i, random.nextInt(2) == 1);
return new IndividualWrapper(individual);
}
}

public class BitStringSinglePointCrossover
extends Crossover<BitStringIndividual, IntegerFitnessValue> {
public BitStringSinglePointCrossover(...) {
random = new Random();
}
@Override
public List<IndividualWrapper<BitStringIndividual, IntegerFitnessValue>>
cross(IndividualWrapper<BitStringIndividual, IntegerFitnessValue> wrapper1, wrapper2, ...) {
BitStringIndividual parent1 = wrapper1.getIndividual();
BitStringIndividual parent2 = wrapper2.getIndividual();
cutPoint = random.nextInt(parent1.size());
BitStringIndividual child1 = new BitStringIndividual(parent1.size());
BitStringIndividual child2 = new BitStringIndividual(parent1.size());
for (int i = 0; i < cutPoint; i++) {
child1.set(i, parent1.get(i));
child2.set(i, parent2.get(i));
}
for (int i = cutPoint; i < parent1.size(); i++) {
child1.set(i, parent2.get(i));
child2.set(i, parent1.get(i));
}
List<IndividualWrapper<BitStringIndividual, IntegerFitnessValue>> children = new ArrayList<>(2);
children.add(new IndividualWrapper<>(child1));
children.add(new IndividualWrapper<>(child2));
return children;
}
}


public class BitStringMutation
extends Mutation<BitStringIndividual, IntegerFitnessValue> {
public BitStringMutation(...) {
mutationProbability =
userProperties.getDouble(MUTATION_PROBABILITY_PROPERTY);
random = new Random();
}
@Override
public IndividualWrapper<BitStringIndividual, IntegerFitnessValue> mutate(
IndividualWrapper<BitStringIndividual, IntegerFitnessValue> wrapper) {
BitStringIndividual individual = wrapper.getIndividual();
for (int i = 0; i < individual.size(); i++)
if (random.nextDouble() <= mutationProbability)
individual.set(i, !individual.get(i));
return wrapper;
}
}


public class App {
public static void main(String[] args) {
driver = new GlobalDriver();
driver.setIndividualClass(BitStringIndividual.class);
driver.setFitnessValueClass(IntegerFitnessValue.class);
driver.setInitializationClass(RandomBitStringInitialization.class);
driver.setInitializationPopulationSize(POPULATION_SIZE);
userProperties.setInt(INDIVIDUAL_SIZE_PROPERTY, INDIVIDUAL_SIZE);
driver.setElitismClass(BestIndividualsElitism.class);
driver.activateElitism(true);
userProperties.setInt(NUMBER_OF_ELITISTS_PROPERTY, NUMBER_OF_ELITISTS);
driver.setParentsSelectionClass(RouletteWheelParentsSelection.class);
driver.setCrossoverClass(BitStringSinglePointCrossover.class);
driver.setSurvivalSelectionClass(RouletteWheelSurvivalSelection.class);
driver.activateSurvivalSelection(true);
driver.setUserProperties(userProperties);
driver.run();
}
}

