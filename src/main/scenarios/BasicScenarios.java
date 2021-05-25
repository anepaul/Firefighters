package main.scenarios;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import main.api.City;
import main.api.CityNode;
import main.api.FireDispatch;
import main.api.Firefighter;
import main.api.Pyromaniac;
import main.api.exceptions.FireproofBuildingException;
import main.impls.CityImpl;

public class BasicScenarios {
  @Test
  public void singleFire() throws FireproofBuildingException {
    City basicCity = new CityImpl(5, 5, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    CityNode fireNode = new CityNode(0, 1);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNode);
    assertCityNotOnFire(basicCity, fireNode);
  }

  @Test
  public void singleFireAtStartingLocation() throws FireproofBuildingException {
    City basicCity = new CityImpl(5, 5, new CityNode(1, 1));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    CityNode fireNode = null;

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNode);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(0, firefighter.distanceTraveled());
  }

  @Test
  public void singleFireDistanceTraveledDiagonal() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    // Set fire on opposite corner from Fire Station
    CityNode fireNode = new CityNode(1, 1);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNode);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(2, firefighter.distanceTraveled());
    Assert.assertEquals(fireNode, firefighter.getLocation());
  }

  @Test
  public void singleFireDistanceTraveledAdjacent() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    // Set fire on adjacent X position from Fire Station
    CityNode fireNode = new CityNode(1, 0);
    Pyromaniac.setFire(basicCity, fireNode);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNode);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(1, firefighter.distanceTraveled());
    Assert.assertEquals(fireNode, firefighter.getLocation());
  }

  @Test
  public void simpleDoubleFire() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();


    CityNode[] fireNodes = {
        new CityNode(0, 1),
        new CityNode(1, 1)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNodes);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(2, firefighter.distanceTraveled());
    Assert.assertEquals(fireNodes[1], firefighter.getLocation());
    assertCityNotOnFire(basicCity, fireNodes);
  }

  @Test
  public void firesInOneCluster() throws FireproofBuildingException {
    City basicCity = new CityImpl(10, 10, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    CityNode[] fireNodes = {
            new CityNode(3, 3),
            new CityNode(3, 4),
            new CityNode(4, 3),
            new CityNode(4, 4),
            new CityNode(5, 5)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNodes);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(12, firefighter.distanceTraveled());
    Assert.assertEquals(fireNodes[4], firefighter.getLocation());
    assertCityNotOnFire(basicCity, fireNodes);
  }

  @Test
  public void firesInOneClusterTwoFirefighters() throws FireproofBuildingException {
    City basicCity = new CityImpl(10, 10, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();

    CityNode[] fireNodes = {
            new CityNode(3, 3),
            new CityNode(3, 4),
            new CityNode(4, 3),
            new CityNode(4, 4),
            new CityNode(5, 5)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(2);
    fireDispatch.dispatchFirefighters(fireNodes);

    int totalDistance = fireDispatch.getFirefighters().stream().mapToInt(Firefighter::distanceTraveled).sum();
    Assert.assertEquals(12, totalDistance);
    Assert.assertTrue(fireDispatch.getFirefighters().stream()
            .anyMatch(firefighter -> firefighter.getLocation().equals(fireNodes[4])));
    assertCityNotOnFire(basicCity, fireNodes);
  }

  @Test
  public void firesInOneClusterRandom() throws FireproofBuildingException {
    City basicCity = new CityImpl(10, 10, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();
    CityNode lastNode = new CityNode(5, 5);

    CityNode[] fireNodes = {
            new CityNode(3, 3),
            new CityNode(3, 4),
            new CityNode(4, 3),
            new CityNode(4, 4),
            lastNode};
    List<CityNode> cityNodes = Arrays.asList(fireNodes);
    Collections.shuffle(cityNodes);
    cityNodes.toArray(fireNodes);
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(1);
    fireDispatch.dispatchFirefighters(fireNodes);

    Firefighter firefighter = fireDispatch.getFirefighters().get(0);
    Assert.assertEquals(12, firefighter.distanceTraveled());
    Assert.assertEquals(lastNode, firefighter.getLocation());
    assertCityNotOnFire(basicCity, fireNodes);
  }

  @Test
  public void firesInOneRandomClusterTwoFirefighters() throws FireproofBuildingException {
    City basicCity = new CityImpl(10, 10, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();
    CityNode lastLocation = new CityNode(5, 5);

    CityNode[] fireNodes = {
            new CityNode(3, 3),
            new CityNode(3, 4),
            new CityNode(4, 3),
            new CityNode(4, 4),
            lastLocation};
    List<CityNode> cityNodes = Arrays.asList(fireNodes);
    Collections.shuffle(cityNodes);
    cityNodes.toArray(fireNodes);
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(2);
    fireDispatch.dispatchFirefighters(fireNodes);

    int totalDistance = fireDispatch.getFirefighters().stream().mapToInt(Firefighter::distanceTraveled).sum();
    Assert.assertEquals(12, totalDistance);
    Assert.assertTrue(fireDispatch.getFirefighters().stream()
            .anyMatch(firefighter -> firefighter.getLocation().equals(lastLocation)));
    assertCityNotOnFire(basicCity, fireNodes);
  }

  @Test
  public void doubleFirefighterDoubleFire() throws FireproofBuildingException {
    City basicCity = new CityImpl(2, 2, new CityNode(0, 0));
    FireDispatch fireDispatch = basicCity.getFireDispatch();


    CityNode[] fireNodes = {
        new CityNode(0, 1),
        new CityNode(1, 0)};
    Pyromaniac.setFires(basicCity, fireNodes);

    fireDispatch.setFirefighters(2);
    fireDispatch.dispatchFirefighters(fireNodes);

    List<Firefighter> firefighters = fireDispatch.getFirefighters();
    int totalDistanceTraveled = 0;
    boolean firefighterPresentAtFireOne = false;
    boolean firefighterPresentAtFireTwo = false;
    for (Firefighter firefighter : firefighters) {
      totalDistanceTraveled += firefighter.distanceTraveled();

      if (firefighter.getLocation().equals(fireNodes[0])) {
        firefighterPresentAtFireOne = true;
      }
      if (firefighter.getLocation().equals(fireNodes[1])) {
        firefighterPresentAtFireTwo = true;
      }
    }

    Assert.assertEquals(2, totalDistanceTraveled);
    Assert.assertTrue(firefighterPresentAtFireOne);
    Assert.assertTrue(firefighterPresentAtFireTwo);
    assertCityNotOnFire(basicCity, fireNodes);
  }

  private void assertCityNotOnFire(City city, CityNode... fireNodes) {
    Arrays.stream(fireNodes)
            .map(city::getBuilding)
            .forEach(building -> Assert
                    .assertFalse("Building should not be on fire but was.", building.isBurning()));
  }
}
