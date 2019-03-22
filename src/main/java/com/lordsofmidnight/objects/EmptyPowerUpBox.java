package com.lordsofmidnight.objects;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import com.lordsofmidnight.objects.powerUps.Blueshell;
import com.lordsofmidnight.objects.powerUps.Invincible;
import com.lordsofmidnight.objects.powerUps.Mine;
import com.lordsofmidnight.objects.powerUps.Speed;
import com.lordsofmidnight.objects.powerUps.Web;
import com.lordsofmidnight.gamestate.points.Point;
import com.lordsofmidnight.utils.ResourceLoader;
import com.lordsofmidnight.utils.enums.PowerUp;
import java.util.concurrent.ConcurrentHashMap;

public class EmptyPowerUpBox extends Pellet {

  public EmptyPowerUpBox(double x, double y) {
    super(x, y);
    init();
  }


  public EmptyPowerUpBox(Point p) {
    super(p);
    init();
  }

  private void init() {
    this.respawntime = 300;
    this.value = 0;

  }

  @Override
  public boolean canUse(Entity e) {
    return true;
  }

  @Override
  public void updateImages(ResourceLoader r) {
    currentImage = r.getPowerBox();
  }


  @Override
  public void interact(Entity entity, Entity[] agents,
      ConcurrentHashMap<UUID, com.lordsofmidnight.objects.powerUps.PowerUp> activePowerUps) {
    if (!active) {
      return;
    }
    this.setActive(false);
  }

  @Override
  public boolean isPowerPellet() {
    return true;
  }

}