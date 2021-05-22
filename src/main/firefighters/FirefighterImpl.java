package main.firefighters;

import main.api.CityNode;
import main.api.Firefighter;

import java.util.Objects;

public class FirefighterImpl implements Firefighter {
  private CityNode location = new CityNode(0,0);
  private int distance = 0;

  @Override
  public CityNode getLocation() {
    // No need for defensive copy, CityNode is already immutable
    return location;
  }

  @Override
  public int distanceTraveled() {
    return distance;
  }

  protected void travelTo(CityNode newLocation) {
    if (Objects.isNull(newLocation)) return;
    distance += distanceTo(newLocation);
    this.location = newLocation;
  }

  protected int distanceTo(CityNode newLocation) {
    return Math.abs(this.location.getX() - newLocation.getX()) +
            Math.abs(this.location.getY() - newLocation.getY());
  }
}
