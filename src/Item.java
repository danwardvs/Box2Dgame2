import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactEdge;

public class Item extends Box {
	
	ContactEdge itemContactList;
	Character gameCharacter;
	World gameWorld;
	String type;
	WorldController gameController;
	int shootTimer;
	boolean alive=true;

	
	public Item(World newWorld, Character newCharacter, WorldController newWorldController, String newType, BodyType newBodyType, boolean newIsSensor, float newX, float newY, float newWidth,
			float newHeight, float newAngle, float newR, float newG, float newB, float newA) {
		super(newWorld, newBodyType, newIsSensor, newX, newY, newWidth, newHeight, newAngle, newR, newG, newB, newA);
		// TODO Auto-generated constructor stub
		gameCharacter = newCharacter;
		gameWorld = newWorld;
		type = newType;
		gameController = newWorldController;
	}
	
	public void createProjectile(float newSpeed, float newAngle, float newX, float newY){
		Projectile newProjectile = new Projectile(gameWorld,BodyType.DYNAMIC,gameCharacter,"Enemy",false,getX()+newX,getY()+newY,0.2f,0.2f,0,1,0f,0f,1,2000);
		newProjectile.applyLinearImpulse(newSpeed, newAngle);
		gameController.createProjectile(newProjectile);
	}
	
	public int update(){
		itemContactList = body.getContactList();
		
		
		if(type.equals("Enemy")){
			if(body.getLinearVelocity().x>5 || body.getLinearVelocity().x<-5)
				alive=false;
			shootTimer++;
			if(shootTimer>120 && alive){
				createProjectile(300,20,2,0.5f);
				createProjectile(300,0f,2,0);
				createProjectile(300,-20f,2,-0.5f);
				shootTimer=0;
			}
		}
		for (ContactEdge ce = body.getContactList(); ce != null; ce = ce.next)
		{
		     if (ce.other == gameCharacter.body && ce.contact.isTouching())
		     {
		    	 if(type.equals("Door")){
		    		 	if(gameController.getRemainingBoxes()<1){
		    		 		gameWorld.destroyBody(body);
		    		 		return 2;
		    		 	}
		    		}
					if(type.equals("Star")){
						gameWorld.destroyBody(body);
						return 1;
						
					}

		    
		     }
		}
		
	
			
		
		return 0;
		
	}

}
