package com.lordsofmidnight.gamestate.maps;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class MapTests {

  @Test
  void serializedMapEquilavence(){
    int[][]mapArr = {{1,0,1},
                  {1,0,1},
                  {0,0,0},
                  {1,1,1}};
    Map map = new Map(mapArr);

    System.out.print(Arrays.deepToString(map.raw()));

    Map recievedMap = Map.deserialiseMap(Map.serialiseMap(map));

    System.out.print(Arrays.deepToString(recievedMap.raw()));

    assert(map.equals(recievedMap));
  }
}
