package com.mygdx.game.physics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Screens.FoodGame;
import com.mygdx.game.Screens.Menu;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import java.util.ArrayList;
import java.util.LinkedList;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.game.helpers.SoundPaths;

/**
 * Player class.
 * Extends {@link com.mygdx.game.physics.DynamicObject} class.
 * @author Gines Moratalla, Juozas Skarbalius, Macaron
 */
public class Player extends DynamicObject {

	private final float height;
	private final float width;

	/**
	 * Vector fields
	 */
	private final Vector3 cursorVector;
	private final Vector2 previousSprite;
	private final Vector2 previousPos;

	/**
	 * Booleans to track player's current facing position
	 */
	private boolean facingRight;
	private boolean facingUp;

	/**
	 * PLAYER TEXTURES
	 * Player static textures
	 * Current sprite is the one used in the methods
	 */
	private Sprite playerSprite;
	private Sprite Sprite_UP;
	private Sprite Sprite_DOWN;
	private Texture currentTexture_UP;
	private Texture currentTexture_DOWN;
	private Sprite player_Standing_Sprite;
	private Texture playerTexture_Standing;

	/**
	 * Red gun textures (Static Cheff with the Red Gun)
	 */
	private Texture playerTexture_RedGunDOWN;
	private Sprite player_RedGunDOWN_Sprite;

	private Texture playerTexture_RedGunUP;
	private Sprite player_RedGunUP_Sprite;

	/**
	 * Shotgun Textures (Static Cheff with the Shotgun)
	 */
	private Texture playerTexture_ShotgunDOWN;
	private Sprite player_ShotgunDOWN_Sprite;

	private Texture playerTexture_ShotgunUP;
	private Sprite player_ShotgunUP_Sprite;

	/**
	 * List that stores all static sprites
	 */
	private final ArrayList<Sprite> allStatics;

	/**
	 * PLAYER ANIMATIONS
	 * Current Animation (Variable will change)
	 */
	private TextureAtlas currentAtlas;
	private Animation<Sprite> currentAnimation;
	private Animation<Sprite> currentAnimation_UP;
	private Animation<Sprite> currentAnimation_DOWN;

	/**
	 * Unarmed Animation Set
	 */
	private Animation<Sprite> walkingAnimation;
	private Animation<Sprite> standingAnimation_PUNCHING;

	/**
	 * Red Gun Animation Set
	 */
	private Animation<Sprite> RedGunAnimation_DOWN;
	private Animation<Sprite> RedGunAnimation_UP;

	/**
	 * Shotgun animation set
	 */
	private Animation<Sprite> ShotgunAnimation_DOWN;
	private Animation<Sprite> ShotgunAnimation_UP;
	private Animation<Sprite> fire_UP;
	private Animation<Sprite> fire_DOWN;
	private Animation<Sprite> currentShotgunFire;
	private float fireX;
	private float fireY;
	private long animationStart = -1;

	private final ArrayList<Animation<Sprite>> allAnimations;
	private final Menu globalGame;

	private final LinkedList<Bullet> ammunition; // Bullet code
	private long lastShot; // Shot cooldown
	private long fireRate;
	private boolean flag;

	// Sound Effects
	SoundPaths soundPaths = SoundPaths.getInstance();
	private final Sound shotgunSound = Gdx.audio.newSound(Gdx.files.internal(SoundPaths.SHOTGUN_PATH));
	private final Sound redgunSound = Gdx.audio.newSound(Gdx.files.internal(SoundPaths.REDGUN_PATH));
	private Sound playerHit = Gdx.audio.newSound(Gdx.files.internal(SoundPaths.PLAYERHIT_PATH));

	public enum WeaponType {
		SHOTGUN,
		REDGUN,
		FIST,
	}

	private WeaponType weaponType;

    public Player(float x, float y, float width, float height, Menu globalGame)
    {
		this.setPlayer(true);
		this.width = width;
		this.height = height;
		this.globalGame = globalGame;
		this.setHit(false);

		allAnimations = new ArrayList<Animation<Sprite>>();
		allStatics = new ArrayList<Sprite>();
		setSpeed(2f);
		setJitter(2f);

		facingRight = true;
		facingUp = false;

		createHealth();
		this.setMaxHealth(100 + globalGame.getStatsHelper().getHealthScaler());

		// Player's Hitbox and Facing Directions
		setHitbox(new Rectangle(x, y, width/2.5f, height));
		previousPos = new Vector2(this.getHitbox().x, this.getHitbox().y);
		previousSprite = new Vector2();

		create(x, y, width, height);
		setUnarmed();
		flipAnimationStanding(walkingAnimation);
		setCurrentHealth(100 + globalGame.getStatsHelper().getHealthScaler());
		flag = false;

		// Bullet code
		ammunition = new LinkedList<Bullet>();

		// Cursor vector
		cursorVector = new Vector3();
    }

	@Override
	public void takeDamage(int damage)
	{
		playerHit.play(soundPaths.getVolume());
		this.setCurrentHealth(this.getCurrentHealth() - damage);
	}

	public void flipAnimation() {
		for (Animation<Sprite> animation : allAnimations) {
			for (TextureRegion frame : animation.getKeyFrames()) {
				frame.flip(true, false);
			}
		}
	}

	public void flipAnimationStanding(Animation<Sprite> animation) {
		for (TextureRegion frame : animation.getKeyFrames()) {
			frame.flip(true, false);
		}
	}

	public void flipTextures() {
		for (Sprite sprite : allStatics) {
			sprite.flip(true, false);
		}
	}

	public void flipAnimationDynamic (Vector3 cursorvector, SpriteBatch batch) {
		// Added 80 to match the middle of the sprite of the player
		if (cursorvector.y >= sprite.getY() + 150) {
			currentAnimation = currentAnimation_UP;

			playerSprite = Sprite_UP;
			playerSprite.setSize(getSprite().getWidth(), getSprite().getHeight());
			playerSprite.setPosition(getSprite().getX(), getSprite().getY());
			setSprite(playerSprite);
			facingUp = true;
			currentShotgunFire = fire_UP;

		}
		if (cursorvector.y < sprite.getY() + 150) {
			currentAnimation = currentAnimation_DOWN;

			playerSprite = Sprite_DOWN;
			playerSprite.setSize(getSprite().getWidth(), getSprite().getHeight());
			playerSprite.setPosition(getSprite().getX(), getSprite().getY());
			setSprite(playerSprite);
			facingUp = false;
			currentShotgunFire = fire_DOWN;
		}

		if (cursorvector.x >= hitbox.getX()) {
			if(!this.facingRight) {
				flipAnimation();
				flipTextures();
				this.setFace(true);
			}
		}
		if (cursorvector.x < hitbox.getX()) {
			if(this.facingRight) {
				flipAnimation();
				flipTextures();
				this.setFace(false);
			}
		}
	}

	public void dispose() {
		currentAtlas.dispose();
		shotgunSound.dispose();
		redgunSound.dispose();
		playerHit.dispose();
	}

	@Override
	public Animation<Sprite> getAnimation() {
		return currentAnimation;
	}

	public void setFace(boolean newDirection)
	{
		facingRight = newDirection;
	}

	public void setJitter(float jitter) {
		/**
		 * Player variables
		 */
	}

	@Override
	public Vector2 getPreviousPos() {
		return this.previousPos;
	}

	@Override
	public Vector2 getPreviousSprite() {
		return this.previousSprite;
	}

	/**
	 * <p>
	 *     Method renders the Player
	 * </p>
	 * @param batch sprite batch
	 * @param game reference to the FoodGame singleton
	 * @param camera reference to the game's camera
	 * @since 1.0
	 */
	@Override
	public void render(SpriteBatch batch, FoodGame game, OrthographicCamera camera)
	{
		// Get previous position for colliders
		previousPos.set(this.getHitbox().x, this.getHitbox().y);
		previousSprite.set(this.getSprite().getX(), this.getSprite().getY());

		// Get cursor vector to match world's coordinates
		cursorVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(cursorVector);

		// Boolean to display punching animation instead of standing
		boolean isPunching = false;

		flipAnimationDynamic(cursorVector, batch);

		// Handle the fire animation for the shotgun
		if(this.getFlag()) {
			if(animationStart == -1) {
				animationStart = System.currentTimeMillis();
			}
			batch.draw(currentShotgunFire.getKeyFrame(game.getTimePassed(), true), getFireX(), getFireY(), 150, 150);
		}

		if(animationStart != -1 && System.currentTimeMillis() - animationStart > 200) {
			this.setFlag(false);
			animationStart = -1; // Reset the animation start time
		}

		handleUserInput(isPunching, batch, game);
	}

	private void handleUserInput(Boolean isPunching, SpriteBatch batch, FoodGame game) {
		// Change weapon with number keys 1, 2 & 3
		if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
			setUnarmed();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
			setRedGun();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
			setShotgun();
		}

		// Move the player
		if (Gdx.input.isKeyPressed(Input.Keys.A)){
			move(-speed, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)){
			move(speed, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)){
			move(0, speed);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			move(0, -speed);
		}

		// Shoot or punch handler
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			isPunching = shoot(batch, game);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.A)
				|| Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.D)) {

			batch.draw(this.getAnimation().getKeyFrame(game.getTimePassed(), true),
					getSprite().getX(), getSprite().getY(), width, height);

		} else if(!isPunching) {
			batch.draw(getSprite(), getSprite().getX(), getSprite().getY(), width, height);
		}
	}

	/**
	 * <p>
	 *     Method moves player to the new specified position
	 * </p>
	 * @param x new x coordinate of a new position
	 * @param y new y coordinate of a new position
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-60">GD-60: [LOGIC] Health & damage mechanism [M]</a>
	 * @since 1.0
	 */
	public void move(float x, float y)
	{
		sprite.setPosition(sprite.getX() + x, sprite.getY() + y);
		hitbox.setPosition(hitbox.getX() + x, hitbox.getY() + y);
	}

	/**
	 * <p>
	 *     Method that moves Player to the previous position
	 * </p>
	 * @since 1.0
	 */
	@Override
	public void moveBack(Vector2 Hitbox, Vector2 spriteVector)
	{
		sprite.setPosition(spriteVector.x, spriteVector.y);
		hitbox.setPosition(Hitbox.x, Hitbox.y);
	}

	/**
	 * <p>
	 *     Method that arms Player with the shotgun
	 * </p>
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-60">GD-60: [LOGIC] Health & damage mechanism [M]</a>
	 * @since 1.0
	 */
	public void setShotgun() {
		this.weaponType = WeaponType.SHOTGUN;
		this.fireRate = 700;
		setSpeed(2f);
		currentAnimation_UP = ShotgunAnimation_UP;
		currentAnimation_DOWN = ShotgunAnimation_DOWN;
		currentTexture_UP = playerTexture_ShotgunUP;
		currentTexture_DOWN = playerTexture_ShotgunDOWN;
		Sprite_UP = player_ShotgunUP_Sprite;
		Sprite_DOWN = player_ShotgunDOWN_Sprite;
	}

	/**
	 * <p>
	 *     Method that arms Player with the red pistol
	 * </p>
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-60">GD-60: [LOGIC] Health & damage mechanism [M]</a>
	 * @since 1.0
	 */
	public void setRedGun() {
		this.weaponType = WeaponType.REDGUN;
		this.fireRate = 400;
		setSpeed(2f + 3);
		currentAnimation_UP = RedGunAnimation_UP;
		currentAnimation_DOWN = RedGunAnimation_DOWN;
		currentTexture_UP = playerTexture_RedGunUP;
		currentTexture_DOWN = playerTexture_RedGunDOWN;
		Sprite_UP = player_RedGunUP_Sprite;
		Sprite_DOWN = player_RedGunDOWN_Sprite;
	}

	/**
	 * <p>
	 *     Method that makes Player unarmed
	 * </p>
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-60">GD-60: [LOGIC] Health & damage mechanism [M]</a>
	 * @since 1.0
	 */
	public void setUnarmed() {
		this.weaponType = WeaponType.FIST;
		setSpeed(2f + 7);
		currentAnimation_UP = walkingAnimation;
		currentAnimation_DOWN = walkingAnimation;
		currentTexture_UP = playerTexture_Standing;
		currentTexture_DOWN = playerTexture_Standing;
		Sprite_UP = player_Standing_Sprite;
		Sprite_DOWN = player_Standing_Sprite;
	}

	/**
	 * <p>
	 *     Methods that handles the player shooting/punching.
	 * </p>
	 * @param batch current sprite batch
	 * @param game reference to the entry game class (Menu)
	 * @return true if Player is punching the enemy, false if shooting or none interaction with the enemy
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-60">GD-60: [LOGIC] Health & damage mechanism [M]</a>
	 * @since 1.0
	 */
	public boolean shoot(SpriteBatch batch, FoodGame game) {

		// Check for gun cooldown to shoot
		if(System.currentTimeMillis() - lastShot < fireRate) {
			flag = false;
			return false;
		}

		// Handle punching (for now this does nothing)
		if(this.weaponType == WeaponType.FIST) {
			if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.A)
			|| Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.D)) {
				return false;
			} else {
				batch.draw(standingAnimation_PUNCHING.getKeyFrame(game.getTimePassed(), true),
				getSprite().getX(), getSprite().getY(), width, height);
				return true;
			}
		}

		// Hablde redgun's bullet
		if(this.weaponType == WeaponType.REDGUN) {
			redgunSound.play(soundPaths.getVolume());
			bulletDirection(90, batch, game);
			lastShot = System.currentTimeMillis();
			return false;
		}

		// Habndle shotgun
		if(this.weaponType == WeaponType.SHOTGUN) {
			shotgunSound.play(soundPaths.getVolume());
			bulletDirection(95, batch, game);
			lastShot = System.currentTimeMillis();
			flag = true;
			return false;
		}
		return false;
	}

	/**
	 * <p>
	 *     Method that launches bullets depending on what direction the player is facing.
	 * </p>
	 * @param offset offset from the player's position of the Bullet (in pixels)
	 * @param batch current sprite batch
	 * @param game reference to the entry game class (Menu)
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-60">GD-60: [LOGIC] Health & damage mechanism [M]</a>
	 * @since 1.0
	 */
	public void bulletDirection(float offset, SpriteBatch batch, FoodGame game) {

		if(facingRight && facingUp) {
			currentShotgunFire = fire_UP;
			Bullet nextBullet = new Bullet(9, 6, sprite.getX() + offset + 210, sprite.getY() + offset + 250, this.weaponType);
			setFire_XY(nextBullet.getXPosition() + 130, nextBullet.getYPosition() + 50);
			nextBullet.setDespawnTime(weaponType, globalGame);
			nextBullet.setShotgunUP(true, false);
			ammunition.add(nextBullet);
		} else if (facingRight && !facingUp) {
			currentShotgunFire = fire_DOWN;
			Bullet nextBullet = new Bullet(9, -6, sprite.getX() + offset + 250, sprite.getY() + offset + 130, this.weaponType);
			setFire_XY(nextBullet.getXPosition() + 100, nextBullet.getYPosition() - 150);
			nextBullet.setDespawnTime(this.weaponType, globalGame);
			nextBullet.setShotgunUP(false, false);
			ammunition.add(nextBullet);
		} else if (!facingRight && facingUp) {
			currentShotgunFire = fire_UP;
			Bullet nextBullet = new Bullet(-9, 6, sprite.getX() + offset - 60, sprite.getY() + offset + 270, this.weaponType);
			setFire_XY(nextBullet.getXPosition() - 150, nextBullet.getYPosition() + 20);
			nextBullet.setDespawnTime(this.weaponType, globalGame);
			nextBullet.setShotgunUP(true, true);
			ammunition.add(nextBullet);
		} else if (!facingRight && !facingUp) {
			currentShotgunFire = fire_DOWN;
			Bullet nextBullet = new Bullet(-9, -6, sprite.getX() + offset - 57, sprite.getY() + offset + 100, this.weaponType);
			setFire_XY(nextBullet.getXPosition() - 160, nextBullet.getYPosition() - 110);
			nextBullet.setDespawnTime(this.weaponType, globalGame);
			nextBullet.setShotgunUP(false, true);
			ammunition.add(nextBullet);
		}
	}

	public LinkedList<Bullet> getAmmunition() {
		return ammunition;
	}

	public boolean getFlag() {
		return this.flag;
	}

	public void setFlag(boolean x) {
		this.flag = x;
	}

	public void setFire_XY(float X, float Y) {
		this.fireX = X;
		this.fireY = Y;
	}

	public float getFireX() {
		return this.fireX;
	}

	public float getFireY() {
		return this.fireY;
	}

	public WeaponType getWeaponType() {
		return this.weaponType;
	}

	/**
	 * <p>
	 *     Method that assigns the right values of Player. For example,
	 *     this method adjusts the following (but not limited to):
	 *     Responsible for:
	 *     <ul>
	 *         <li>Assigning sprites (of type Sprite) to the various possible positions of the Player</li>
	 *         <li>Assigning sprites (of type Sprite) to the various possible positions of the Player's guns
	 *         (for all available types) </li>
	 *         <li>Position</li>
	 *         <li>Size</li>
	 *     </ul>
	 * </p>
	 * @param x initial X pos
	 * @param y initial Y pos
	 * @param width width of the Player
	 * @param height height of the Player
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-71">GD-95: Create Player Class</a>
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-98">GD-98: [PHYSICS & LOGIC] The enemy can damage player (through shooting & punching) [M]</a>
	 * @see <a href="https://lulgroupproject.atlassian.net/browse/GD-95">GD-95: [LOGIC] Generate initial positions of dynamic objects randomly</a>
	 * @since 1.0
	 */
	public void create(float x, float y, float width, float height) {
		currentAtlas = new TextureAtlas(Gdx.files.internal("cheff/Cheff_punching/standing_punching.atlas"));
		standingAnimation_PUNCHING = new Animation<Sprite>(
			1f,
			currentAtlas.createSprite("Chef_standing_punching5"));

		allAnimations.add(standingAnimation_PUNCHING);

		currentAtlas = new TextureAtlas(Gdx.files.internal("cheff/Shotgun/shotgun_up.atlas"));
		ShotgunAnimation_UP = new Animation<Sprite>(
			1/10f,
			generateEnemyAtlasSprites(8, "Chef_with_shtopgun_standing_UP", currentAtlas));

		allAnimations.add(ShotgunAnimation_UP);

		currentAtlas = new TextureAtlas(Gdx.files.internal("cheff/Shotgun/shotgun_down.atlas"));
		ShotgunAnimation_DOWN = new Animation<Sprite>(
			1/10f,
				generateEnemyAtlasSprites(8, "Chef_with_shotgungun_standing_DOWN", currentAtlas)
		);

		allAnimations.add(ShotgunAnimation_DOWN);

		currentAtlas = new TextureAtlas(Gdx.files.internal("cheff/RedGun/RedGun_DOWN.atlas"));
		RedGunAnimation_DOWN = new Animation<Sprite>(
			1/15f,
				generateEnemyAtlasSprites(8, "Chef_Still_RedGun_DOWN", currentAtlas)
		);

		allAnimations.add(RedGunAnimation_DOWN);


		currentAtlas = new TextureAtlas(Gdx.files.internal("cheff/RedGun/RedGun_UP.atlas"));
		RedGunAnimation_UP = new Animation<Sprite>(
			1/15f,
				generateEnemyAtlasSprites(8, "Chef_Still_RedGun_UP", currentAtlas)
		);

		allAnimations.add(RedGunAnimation_UP);

		currentAtlas = new TextureAtlas(Gdx.files.internal("cheff/cheff_spritesheet.atlas"));
		walkingAnimation = new Animation<Sprite>(
			1/15f,
				generateEnemyAtlasSprites(8, "Left", currentAtlas)
		);

		allAnimations.add(walkingAnimation);

		currentAtlas = new TextureAtlas(Gdx.files.internal("cheff/Shotgun/fire/fire_DOWN.atlas"));
		fire_UP = new Animation<Sprite>(
			1/15f,
				generateEnemyAtlasSprites(4, "shotgun_shooting_UP", currentAtlas)
		);


		fire_DOWN = new Animation<Sprite>(
			1/15f,
				generateEnemyAtlasSprites(4, "shotgun_shooting_UP", currentAtlas, true)
		);

		allAnimations.add(fire_UP);
		allAnimations.add(fire_DOWN);

		currentAnimation = RedGunAnimation_UP;

		// Create Static Sprites
		// Chef sprites
		playerTexture_Standing = new Texture("cheff/Chef_Still_Image.png");
		player_Standing_Sprite = new Sprite(playerTexture_Standing);
		allStatics.add(player_Standing_Sprite);

		playerTexture_RedGunDOWN = new Texture("cheff/RedGun/RedGun_standing_DOWN.png");
		player_RedGunDOWN_Sprite = new Sprite(playerTexture_RedGunDOWN);
		allStatics.add(player_RedGunDOWN_Sprite);

		playerTexture_RedGunUP = new Texture("cheff/RedGun/RedGun_standing_UP.png");
		player_RedGunUP_Sprite = new Sprite(playerTexture_RedGunUP);
		allStatics.add(player_RedGunUP_Sprite);

		playerTexture_ShotgunUP = new Texture("cheff/Shotgun/Chef_with_shtopgun_standing_UP.png");
		player_ShotgunUP_Sprite = new Sprite(playerTexture_ShotgunUP);
		allStatics.add(player_ShotgunUP_Sprite);

		playerTexture_ShotgunDOWN = new Texture("cheff/Shotgun/Chef_with_shotgungun_standing_DOWN.png");
		player_ShotgunDOWN_Sprite = new Sprite(playerTexture_ShotgunDOWN);
		allStatics.add(player_ShotgunDOWN_Sprite);

		Texture playerTexture = playerTexture_Standing;
		playerSprite = new Sprite(playerTexture);
		playerSprite.setSize(width, height);
		playerSprite.setPosition(x - 100, y);
		previousSprite.set(x - 100, y);
		setSprite(playerSprite);
	}
}