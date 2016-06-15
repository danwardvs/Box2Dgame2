
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.lwjgl.input.Keyboard;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.WeldJoint;
import org.jbox2d.dynamics.joints.WeldJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.contacts.ContactEdge;
import java.util.Random;



public class Character extends Box {
	
	float movement_speed=50;
	World gameWorld;
	WorldController gameController;
	int bullet_time;
	int bullet_time_delay=300;
	Box Feet;
	Box bodySensor;
	float x;
	float y;
	boolean hasDied = false;

	boolean alive=true;
	int deathDirection;
	
	Random rn = new Random();

	public Character(WorldController newWorldController, World newWorld, boolean newIsSensor, float newX, float newY, float newWidth, float newHeight,
		
		float newAngle, float newR, float newG, float newB, float newA) {
		super(newWorld, BodyType.DYNAMIC, newIsSensor, newX, newY, newWidth, newHeight, newAngle, newR, newG, newB, newA);
		gameWorld = newWorld;
		gameController = newWorldController;
		body.setFixedRotation(true);
		
		Feet = new Box(gameWorld,BodyType.DYNAMIC,true,newX,newY-newHeight,newWidth,0.5f,0,0,1,0,0);			
		
		Vec2 FeetAnchor = new Vec2(0,newHeight);
		gameController.createBox(Feet);
		
		WeldJointDef jointDef = new WeldJointDef();
		jointDef.initialize(body, Feet.body, FeetAnchor);
        jointDef.collideConnected = false;
        jointDef.referenceAngle = 0;
        gameWorld.createJoint(jointDef);
        
        /*
        bodySensor = new Box(gameWorld,BodyType.DYNAMIC,true,newX,newY,newWidth*3,3f,0,0,1,1,1);			
        
		Vec2 BodyAnchor = new Vec2(0,0);
		gameController.createBox(bodySensor);
		
		jointDef = new WeldJointDef();
		jointDef.initialize(body, bodySensor.body, BodyAnchor);
        jointDef.collideConnected = false;
        jointDef.referenceAngle = 0;
        gameWorld.createJoint(jointDef);
        */
        




	}
	public void createProjectile(float newSpeed, float newAngle, float newX, float newY){
		Projectile newProjectile = new Projectile(gameWorld,BodyType.DYNAMIC,this,false,getX()+newX,getY()+newY,0.2f,0.2f,0,1,0f,0f,1,2000);
		newProjectile.applyLinearImpulse(newSpeed, newAngle);
		gameController.createProjectile(newProjectile);
	}
	
	public void setState(boolean newAlive){
		alive = newAlive;
	}
	
	public void update(int delta){
		
		float x = body.getPosition().x;
		float y = body.getPosition().y;
		
		if(x<-22){
			alive=false;
			deathDirection=1;
		}
		if(x>22){
			alive=false;
			deathDirection=2;
		}
		
		if(!alive){
			r=1;
			g=0;
			b=0;
		}
		
		if(body.getLinearVelocity().y<-15){
			
			
			body.setFixedRotation(false);
			body.applyTorque(50);
			alive=false;
			
		}
		 if(alive){
			 bullet_time+=delta;
			 if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D) ){
				 applyImpulse(movement_speed,0);
			 }
			 if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A) ){
					applyImpulse(-movement_speed,0);
			 }
			 //System.out.println(Feet);
			 if (Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)){
				 if(body.getLinearVelocity().y<=0.1f && body.getLinearVelocity().y>=-0.1f && Feet.getBody().getContactList()!=null)
					applyImpulse(0,2000);
			 }
			 if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && bullet_time>=bullet_time_delay){
				bullet_time=0;
				createProjectile(300,20,2,0.5f);
				createProjectile(300,0f,2,0);
				createProjectile(300,-20f,2,-0.5f);
				body.applyForceToCenter(new Vec2(-200,0));
			 }
		 }
		 if(alive==false && hasDied==false){
				for(int i = 0; i<25; i++){
					Vec2 splatterVelocity = new Vec2(10,0);
					if(deathDirection==0){
						Box newBox = new Box(gameWorld,BodyType.DYNAMIC,false,x,y,0.2f,0.2f,1f,1,0,0,1);
						splatterVelocity = new Vec2(0,0);
						int newX;
						int newY;
						int n = -10 - 10 + 1;
						int randX = rn.nextInt() % n;
						int randY = rn.nextInt() % n;
						newX = -10 + randX;
						newY = -10 + randY;
						splatterVelocity.x = body.getLinearVelocity().x + newX;
						splatterVelocity.y = body.getLinearVelocity().x + newY;
						newBox.body.setLinearVelocity(splatterVelocity);
						gameController.createBox(newBox);
					}
					if(deathDirection==1){
						splatterVelocity = new Vec2(10,0);
						
						int newY = 0;
						int n = -10 - 10 + 1;
						int randY = rn.nextInt() % n;
						newY = -10 + randY;
						splatterVelocity.y = newY;
						
						Box newBox = new Box(gameWorld,BodyType.DYNAMIC,false,x,y,0.2f,0.2f,1f,1,0,0,1);
						newBox.body.setLinearVelocity(splatterVelocity);
						gameController.createBox(newBox);
					}
					if(deathDirection==2){
						splatterVelocity = new Vec2(-10,0);
						int newY = 0;
						int n = -10 - 10 + 1;
						int randY = rn.nextInt() % n;
						newY = -10 + randY;
						splatterVelocity.y = newY;
						Box newBox = new Box(gameWorld,BodyType.DYNAMIC,false,x-0.4f,y,0.2f,0.2f,1f,1,0,0,1);
						newBox.body.setLinearVelocity(splatterVelocity);
						gameController.createBox(newBox);
					}
					
					hasDied=true;
				}
		 }
		 
	}

}
