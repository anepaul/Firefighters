package main.firefighters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.Firefighter;
import main.api.exceptions.InvalidScenarioException;
import main.api.exceptions.NoFireFoundException;

public class FireDispatchImpl implements FireDispatch {
  private final City city;
  private List<FirefighterImpl> vigiles;

  public FireDispatchImpl(City city) {
    this.city = city;
  }

  @Override
  public void setFirefighters(int numFirefighters) {
    FirefighterImpl[] arr = new FirefighterImpl[numFirefighters];
    for (int i = 0; i < numFirefighters; i++) {
      arr[i] = new FirefighterImpl();
    }
    // Set list of firefighters as an immutable list
    vigiles = List.of(arr);
  }

  @Override
  public List<Firefighter> getFirefighters() {
    return vigiles.stream().map(vigile -> (Firefighter) vigile).collect(Collectors.toUnmodifiableList());
  }

  @Override
  public void dispatchFirefighers(CityNode... burningBuildings) {
    // Optimize for overall distance traveled among all firefighters.
    // TODO: sort
    for (CityNode burningBuilding : burningBuildings) {
      int minDistance = Integer.MAX_VALUE;
      FirefighterImpl closest = null;
      for (FirefighterImpl vigile : vigiles) {
        if (vigile.distanceTo(burningBuilding) < minDistance) {
          minDistance = vigile.distanceTo(burningBuilding);
          closest = vigile;
        }
      }
      Objects.requireNonNull(closest).travelTo(burningBuilding);
      if (city.getBuilding(burningBuilding).isBurning()) {
        try {
          city.getBuilding(burningBuilding).extinguishFire();
        } catch (NoFireFoundException e) {
          // Building not burning
          throw new InvalidScenarioException(e);
        }
      }
    }
  }
}
