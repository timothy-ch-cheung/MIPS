package com.lordsofmidnight.objects.powerUps;

import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.gamestate.points.PointMap;
import com.lordsofmidnight.objects.Entity;
import com.lordsofmidnight.objects.Pellet;
import com.lordsofmidnight.utils.enums.PowerUps;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The base class for the powerUps
 */
public abstract class PowerUp {

  protected final String NAME;
  protected final int EFFECTTIME;
  public UUID id;
  protected Entity effected;
  protected int counter = 0;
  protected Entity user;
  protected int currentFrame = 0;
  protected PowerUps type;
  protected boolean onMap;

  /**
   * Abstract class for the powerUps
   *
   * @param effectTime how long the effect of the powerUp lasts
   * @param name the name of the powerUp
   */
  public PowerUp(int effectTime, String name) {
    this.EFFECTTIME = effectTime;
    this.NAME = name;
    id = UUID.randomUUID();
  }

  /**
   * Used to communicate powerups to clients
   *
   * @return the PowerUps corresponding to the int provided
   */
  public static PowerUp fromInt(int n) {
    switch (n) {
      case 0:
        return new Web();
      case 1:
        return new Speed();
      case 2:
        return new Rocket();
      case 3:
        return new Invincible();
      case 4:
        return new Mine();
    }
    return null;
  }

  /**
   * @return The type of the powerUp
   */
  public PowerUps getType() {
    return type;
  }

  /**
   * Called when the powerUp that is placed or used on another player is triggered
   *
   * @param victim The entity effected by the powerUp
   * @param activePowerUps All active powerUps in the game
   */
  public void trigger(
      Entity victim,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      AudioController audioController) {}

  /**
   * Called each physics update to increment the timers
   *
   * @return if the powerUp has finished and should be removed
   */
  public boolean incrementTime(AudioController audioController) {
    counter++;
    return false;
  }

  /**
   * Called when the player uses this powerUp
   *
   * @param user The entity that used the powerUp
   * @param activePowerUps All active powerUps in the game
   */
  public void use(
      Entity user,
      ConcurrentHashMap<UUID, PowerUp> activePowerUps,
      PointMap<Pellet> pellets,
      Entity[] agents,
      AudioController audioController) {}

  /**
   * Used to communicate powerups to clients
   *
   * @return the int corresponding to the powerup's enum
   */
  public int toInt() {
    switch (type) {
      case WEB:
        return 0;
      case SPEED:
        return 1;
      case ROCKET:
        return 2;
      case INVINCIBLE:
        return 3;
      case MINE:
        return 4;
    }
    return -1;
  }

  /** @return The entity that used the powerUp */
  public Entity getUser() {
    return this.user;
  }

  /** @return The Name of the powerUp */
  @Override
  public String toString() {
    return this.NAME;
  }

  /** Increments the frame of the powerUp */
  public void incrementFrame() {
    this.currentFrame++;
  }

  /** @return The current frame of the powerUp */
  public int getCurrentFrame() {
    return this.currentFrame;
  }
}
