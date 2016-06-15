

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactEdge;
import org.jbox2d.dynamics.Body;

public class Projectile extends Box {
	
	ContactEdge itemContactList;
	Character gameCharacter;
	float time;
	float lifetime;
	World gameWorld;
	
	public Projectile(World newWorld, BodyType newBodyType, Character newCharacter, boolean newIsSensor, float newX, float newY, float newWidth, float newHeight,
			float newAngle, float newR, float newG, float newB, float newA, float newLifetime) {
		super(newWorld, newBodyType,newIsSensor, newX, newY, newWidth, newHeight, newAngle, newR, newG, newB, newA);
		// TODO Auto-generated constructor stub
		lifetime = newLifetime;
		body.setBullet(true);
		gameWorld = newWorld;
		gameCharacter = newCharacter;
		
		
		
	}
	public Body getBody(){
		return body;
	}
	
	public int update(int newDelta){
		
		itemContactList = body.getContactList();
		
		time += newDelta;
		if(time>=lifetime){
			gameWorld.destroyBody(body);
			return 0;
			
		}
		else if(itemContactList!=null){
			if(itemContactList.other==gameCharacter.body){
					gameWorld.destroyBody(body);
					return 2;
				
				
			}
		
			
		}
		return 1;
			
			
		
	}

}
