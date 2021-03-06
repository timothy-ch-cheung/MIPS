package com.lordsofmidnight.objects.powerUps;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.Methods;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The rocket powerup Launched at and killing the player in first
 */
public class Rocket extends PowerUp {

  private ConcurrentHashMap<UUID, PowerUp> activePowerUps;

  private boolean launched = false;
  private boolean targeted = false;

  private Point2D.Double startLocation;
  private Point2D.Double endLocation;

  public Rocket() {
    super(600, "rocket");
    this.type = PowerUps.ROCKET;
  }

  @Override
  public void use(
      Entity user,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents,
      AudioController audioController) {
    effected = agents[Methods.findWinner(agents)];
    this.user = user;
    this.activePowerUps = activePowerUps;
    activePowerUps.put(id, this);
    this.effected = agents[Methods.findWinner(agents)];
    audioController.playSound(Sounds.ROCKETLAUNCH);
  }

  @Override
  public boolean incrementTime(AudioController audioController) {
    super.incrementTime(audioController);
    if (counter == EFFECTTIME) {
      audioController.playSound(Sounds.ROCKET);
      Methods.kill(user, effected, audioController);
      return true;
    }
    return false;
  }

  /**
   * increments the current image frame
   */
  public void incrementFrame() {
    this.currentFrame++;
  }

  /**
   * @return the counter for the animation of the rocket
   */
  public int getTime() {
    return this.counter;
  }

  /**
   * @return the effect time
   */
  public int getMaxTime() {
    return this.EFFECTTIME;
  }

  /**
   * @return if the rocket has been launched yet
   */
  public boolean isLaunched() {
    return launched;
  }

  /**
   * Sets weather the rocket has been launched or not
   *
   * @param launched True if launched
   */
  public void setLaunched(boolean launched) {
    this.launched = launched;
  }

  /** @return if the rocket has a target */
  public boolean isTargeted() {
    return targeted;
  }

  /** @return the location that the rocket was launched from */
  public Double getStartLocation() {
    return startLocation;
  }

  /** @param startLocation the location that the rocket was launched from */
  public void setStartLocation(Double startLocation) {
    this.startLocation = startLocation;
  }

  /** @return the target location of the rocket */
  public Double getEndLocation() {
    return endLocation;
  }

  /** @param endLocation the target location of the rocket */
  public void setEndLocation(Double endLocation) {
    this.endLocation = endLocation;
  }

  /**
   * @return the target entity
   */
  public Entity getTargeted() {
    return this.effected;
  }

  /** @param targeted sets if the rocket has been targeted */
  public void setTargeted(boolean targeted) {
    this.targeted = targeted;
  }
}
