package main.firefighters;

import main.api.CityNode;
import main.api.Firefighter;

import java.util.Objects;

public class FirefighterImpl implements Firefighter {
  private CityNode location;
  private int distance = 0;

  public FirefighterImpl(CityNode location) {
    this.location = location;
  }

  @Override
  public CityNode getLocation() {
    // No need for defensive copy, CityNode is already immutable
    return location;
  }

  @Override
  public int distanceTraveled() {
    return distance;
  }

  public void travelTo(CityNode newLocation) {
    if (Objects.isNull(newLocation)) return;
    distance += location.distanceTo(newLocation);
    this.location = newLocation;
  }
}
