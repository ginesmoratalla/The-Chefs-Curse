package com.mygdx.game.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Screens.FoodGame;
import com.mygdx.game.Screens.Menu;
import com.mygdx.game.misc.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Bullet extends DynamicObject
{
	private float speedModifier;
	private float xSpeed;
	private float ySpeed;
	private Rectangle bulletHitbox;
	private boolean visible;//if we are reusing the same bullets then we don't want all the bullets to be visible at the same time.
	
	private float xPosition;
	private float yPosition;
	
	private Sprite bulletSprite;
	private Texture bulletTexture;
	private Texture shotgunTexture_UP_RIGHT;
	private Texture shotgunTexture_UP_LEFT;
	private Texture shotgunTexture_DOWN_RIGHT;
	private Texture shotgunTexture_DOWN_LEFT;
	private Texture shotGunTexture;
	
	private int damage;
	private Player.WeaponType weaponType;
	private long timeToDespawn;

	private float initialX;
	private float initialY;

	private Animation<Sprite> bulletAnimation;
	private Animation<Sprite> ketchupAnimation;
	private Animation<Sprite> popBulletAnimation;
	private TextureAtlas atlas;

	private long animationStart;

	public static enum EnemyBullet {
		HOTDOG_BULLET,
		POPCORN_BULLET,
	}

	private EnemyBullet enemyBullet;
	
	// Bullet Constructor for player
	public Bullet(float xS, float yS, float xPos, float yPos, Player.WeaponType weaponType)
	{
		xSpeed = xS;
		ySpeed = yS;
		xPosition = xPos;
		yPosition = yPos;

		initialX = xPosition;
		initialY = yPosition;

		// Redgun bullet
		bulletTexture = new Texture("objects/bullet/bullet.png");

		// Shotgun bullets
		shotgunTexture_UP_RIGHT = new Texture("cheff/Shotgun/shotgun_bullet_UP_RIGHTt.png");
		shotgunTexture_UP_LEFT = new Texture("cheff/Shotgun/shotgun_bullet_UP_LEFT.png");
		shotgunTexture_DOWN_RIGHT = new Texture("cheff/Shotgun/shotgun_bullet_DOWN_RIGHT.png");
		shotgunTexture_DOWN_LEFT = new Texture("cheff/Shotgun/shotgun_bullet_DOWN_LEFT.png");

		

		bulletSprite = new Sprite(bulletTexture);
		bulletHitbox = new Rectangle(xPos, yPos, 10, 10);
		visible = true;
		speedModifier = 4f;
		damage = 10;
		this.weaponType = weaponType;
		
	}

	// Constructor for enemy bullets
	public Bullet(float xS, float yS, float xPos, float yPos,  EnemyBullet enemyBullet)
	{
		xSpeed = xS;
		ySpeed = yS;
		xPosition = xPos;
		yPosition = yPos;

		initialX = xPosition;
		initialY = yPosition;

		// Redgun bullet
		createBulletAnimation();
		bulletHitbox = new Rectangle(xPos, yPos, 50, 50);
		visible = true;
		speedModifier = 4f;
		damage = 10;
		this.enemyBullet = enemyBullet;
		
	}
	
	public Sprite getSprite()
	{
		return bulletSprite;
	}

	public Rectangle getHitbox()
	{
		return bulletHitbox;
	}
	
	public boolean getVisibility()
	{
		return visible;
	}
	
	public void setVisibility(boolean newVisibility)
	{
		visible = newVisibility;
	}
	
	public float getSpeed()
	{
		return speedModifier;
	}
	
	public void setSpeed(float newSpeed)
	{
		speedModifier = newSpeed;
	}
	
	public float getXSpeed()
	{
		return xSpeed;
	}
	
	public float getYSpeed()
	{
		return ySpeed;
	}
	
	public void setSpeedVector(float x, float y)
	{
		xSpeed = x;
		ySpeed = y;
	}
	
	public float getXPosition()
	{
		return xPosition;
	}
	public float getYPosition()
	{
		return yPosition;
	}
	
	
	public void update()
	{
		xPosition = xPosition + (xSpeed * speedModifier);
		yPosition = yPosition + (ySpeed * speedModifier);

		bulletHitbox.setPosition(xPosition, yPosition);
	}
	
	public void render(SpriteBatch batch)
	{
		switch (this.getWeapontype()) {
			case SHOTGUN:
				batch.draw(shotGunTexture, xPosition, yPosition, 50, 50);
				break;
			case REDGUN:
				batch.draw(bulletSprite, xPosition, yPosition, 40, 40);
				break;
			default:
				batch.draw(shotGunTexture, xPosition, yPosition, 40, 40);
				break;
		}
	}

	public void renderEnemyBullet(SpriteBatch batch, FoodGame game) {
		switch (this.enemyBullet) {
			case HOTDOG_BULLET:
				batch.draw(ketchupAnimation.getKeyFrame(game.getTimePassed(), true), xPosition, yPosition, 80, 80);
				break;
			case POPCORN_BULLET:
				batch.draw(popBulletAnimation.getKeyFrame(game.getTimePassed(), true), xPosition, yPosition, 70, 70);
				break;
			default:
				break;
		}
	}
	
	public int getDamage()
	{
		return damage;
	}	

	// Different range for different bullets
	public long getDespawnTime() {
		return this.timeToDespawn;
	}

	public void setDespawnTime(Player.WeaponType weaponType, Menu game) {
		switch (weaponType) {
			case FIST:
				break;
			case REDGUN:
				this.timeToDespawn = System.currentTimeMillis() + 600;
				this.damage = 18 + game.getStatsHelper().getDamageScaler();
				break;
			case SHOTGUN:
				this.timeToDespawn = System.currentTimeMillis() + 100;
				this.damage = 40 + game.getStatsHelper().getDamageScaler();
				break;
			default:
				break;
		}
	}

	public void setDespawnTime(EnemyBullet enemyBullet) {
		switch (enemyBullet) {
			case HOTDOG_BULLET:
				this.timeToDespawn = System.currentTimeMillis() + 1500;
				this.damage = 8;
				break;
			case POPCORN_BULLET:
				this.timeToDespawn = System.currentTimeMillis() + 1000;
				this.damage = 5;
				break;
			default:
				break;
		}
	}

	public Player.WeaponType getWeapontype() {
		return this.weaponType;
	}
	public void setBulletAnimation(Animation<Sprite> anim) {
		this.bulletAnimation = anim;
	}

	public Animation<Sprite> getBulletAnimation() {
		return this.bulletAnimation;
	}

	public float getInitialX() {
		return this.initialX;
	}

	public float getInitialY() {
		return this.initialY;
	}

	public void setShotgunUP(boolean isUp, boolean isLeft) {
		if (isUp && isLeft) {
			shotGunTexture = shotgunTexture_UP_LEFT;
		} 
		if (isUp && !isLeft) {
			shotGunTexture = shotgunTexture_UP_RIGHT;
		}
		if (!isUp && isLeft) {
			shotGunTexture = shotgunTexture_DOWN_LEFT;
		}
		if (!isUp && !isLeft) {
			shotGunTexture = shotgunTexture_DOWN_RIGHT;
		}
	}

	public void setAnimationStart(long mins) {
		this.animationStart = mins;
	}

	public long getAnimationStart() {
		return this.animationStart;
	}

	public void createBulletAnimation() {
		atlas = new TextureAtlas("enemies/bullets/ketchup.atlas");
        ketchupAnimation = new Animation<>(
        	1/20f,
        	atlas.createSprite("ketchup1"),
        	atlas.createSprite("ketchup2"),
        	atlas.createSprite("ketchup3"),
        	atlas.createSprite("ketchup4"),
        	atlas.createSprite("ketchup5"),
        	atlas.createSprite("ketchup6"));

		atlas = new TextureAtlas("enemies/bullets/popbullet.atlas");
		popBulletAnimation = new Animation<>(
			1/20f,
			atlas.createSprite("popbullet1"),
			atlas.createSprite("popbullet2"),
			atlas.createSprite("popbullet3"),
			atlas.createSprite("popbullet4"),
			atlas.createSprite("popbullet5"),
			atlas.createSprite("popbullet6"));
	}
}
