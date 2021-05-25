package main.firefighters;

import java.util.*;
import java.util.stream.Collectors;

import main.api.*;
import main.api.exceptions.FireproofBuildingException;
import main.api.exceptions.InvalidScenarioException;
import main.api.exceptions.NoFireFoundException;

public class FireDispatchImpl implements FireDispatch {
  private final City city;
  private List<FirefighterImpl> firefighters;
  private long counter = 0l;

  public FireDispatchImpl(City city) {
    this.city = city;
  }

  @Override
  public void setFirefighters(int numFirefighters) {
    CityNode fireStationLocation = city.getFireStation().getLocation();
    FirefighterImpl[] arr = new FirefighterImpl[numFirefighters];
    for (int i = 0; i < numFirefighters; i++) {
      arr[i] = new FirefighterImpl(fireStationLocation);
    }
    // Set list of firefighters as an immutable list
    firefighters = List.of(arr);
  }

  @Override
  public List<Firefighter> getFirefighters() {
    // Return defensive copy
    return firefighters.parallelStream().map(Firefighter.class::cast).collect(Collectors.toUnmodifiableList());
  }

  @Override
  public void dispatchFirefighters(CityNode... burningBuildings) {
    // Optimize for overall distance traveled among all firefighters.
    // If their is one fire fighter and multiple burning buildings,
    // the problem is similar to the travelling salesman problem.
    // We want to calculate the most efficient path that the firefighter
    // can take to put out all the fires.
    // With multiple firefighters, we could use any COMBINATION of the
    // firefighters to visit all the buildings
    // We need to generate test permutation of building visit order
    // with any combination of firefighters visiting that building

    // Remove any null values and any buildings not burning
    List<Building> buildings = Arrays.stream(burningBuildings)
            .filter(Objects::nonNull)
            .map(city::getBuilding)
            .filter(Building::isBurning)
            .collect(Collectors.toList());

    // ---- Set up parameters for recursive method ---- //
    // Initial result object, set cost to max int
    Result result = new Result(Integer.MAX_VALUE, null);
    // Create map to store optimum route for all firefighters
    Map<FirefighterImpl, List<Building>> firefighterVisitedMap = new HashMap<>();
    // Initialize route by adding the starting point
    Building starting = city.getFireStation();
    firefighters.forEach(firefighter -> {
      List<Building> visited =  new ArrayList<>(buildings.size()+1);
      visited.add(starting);
      firefighterVisitedMap.put(firefighter, visited);
    });

    // ---- Test all possible permutations ---- //
    try {
      minDistanceTraveled(buildings, firefighterVisitedMap, 0, 0, result);
    } catch (NoFireFoundException | FireproofBuildingException e) {
      throw new InvalidScenarioException(e);
    }
    System.out.println(counter);
    // ---- Act on the most efficient route ---- //
    dispatchFirefightersToRoute(result);
  }

  private void minDistanceTraveled(List<Building> buildings, Map<FirefighterImpl,
          List<Building>> firefighterVisitedMap, int pathCost, int count, Result result)
          throws NoFireFoundException, FireproofBuildingException {
    // Base case visited all the buildings
    if (count == buildings.size()) {
      counter++;
      if (result.cost > pathCost) {
        // Found new minimum
        updateResult(result, pathCost, firefighterVisitedMap);
      }
      return;
    }

    for (Building building : buildings) {
      // Skip if building is already visited (not burning)
      if (!building.isBurning()) continue;
      for (FirefighterImpl firefighter : firefighters) {
        List<Building> buildingsVisited = firefighterVisitedMap.get(firefighter);
        // Get last building visited
        Building currLocation = buildingsVisited.get(buildingsVisited.size()-1);
        // Mark current building as visited
        buildingsVisited.add(building);
        building.extinguishFire();
        pathCost += currLocation.getLocation().distanceTo(building.getLocation());
        // Optimization to possibly reduce number of branches the
        if (pathCost < result.cost)
          minDistanceTraveled(buildings, firefighterVisitedMap, pathCost, count+1, result);
        // Remove building as visited
        pathCost -= currLocation.getLocation().distanceTo(building.getLocation());
        building.setFire();
        buildingsVisited.remove(buildingsVisited.size()-1);
      }
    }
  }

  private void updateResult(Result result, int cost, Map<FirefighterImpl, List<Building>> firefighterVisitedMap) {
    result.cost = cost;
    result.firefighterVisitedMap = new HashMap<>();
    firefighterVisitedMap.forEach((key, value) -> result.firefighterVisitedMap.put(key, new ArrayList<>(value)));
  }

  private void dispatchFirefightersToRoute(Result result) {
    result.firefighterVisitedMap.forEach(
            (firefighter, orderOfBuildingsVisited) ->
                    orderOfBuildingsVisited
                            // Skip first "building" since it is the starting point of all firefighters (0,0)
                            .subList(1, orderOfBuildingsVisited.size()).forEach(building -> {
                      firefighter.travelTo(building.getLocation());
                      try {
                        building.extinguishFire();
                      } catch (NoFireFoundException e) {
                        throw new InvalidScenarioException(e);
                      }
                    }));
  }

  private static class Result {
    int cost;
    Map<FirefighterImpl, List<Building>> firefighterVisitedMap;

    public Result(int cost, Map<FirefighterImpl, List<Building>> firefighterVisitedMap) {
      this.cost = cost;
      this.firefighterVisitedMap = firefighterVisitedMap;
    }
  }
}
