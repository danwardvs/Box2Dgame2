import java.util.List;
import java.util.ArrayList;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.Sys;


public class WorldController {
	
		float mouse_x;
		float mouse_y;
		
		Boolean leftButtonDown;
		Boolean rightButtonDown;
	
	    int level = 1;
	    boolean buttonPressed;
	    
		int score = 10;
		int BOX_AMOUNT = 25;
		List<Box> gameBoxes = new ArrayList<Box>();
		List<Projectile> gameProjectiles = new ArrayList<Projectile>();
		List<Item> gameItems = new ArrayList<Item>();
		Character gameCharacter;
		static WorldController gameController;
		static World gameWorld;
		Level gameLevel;
		
		  /** time at last frame */
	    long lastFrame;
	     
	    /** frames per second */
	    int fps;
	    /** last fps time */
	    long lastFPS;
	    
		
	    
		public void update(int delta){
			
			mouse_x = Mouse.getX(); 
			mouse_y = Mouse.getY(); 
			
			// Get mouse buttons
		    leftButtonDown = Mouse.isButtonDown(0);
		    rightButtonDown = Mouse.isButtonDown(1);
            Display.setTitle("Cubes Remaining: " + score);

		    if(leftButtonDown){
		    	Display.setTitle("X: " + (mouse_x/20-20)+ "Y: " + (mouse_y/20-15));
		    }
			 
			gameCharacter.update(delta);
			updateFPS();
			
			for(int j = 0; j < gameProjectiles.size(); j++)
			{
			    Projectile newProjectile = gameProjectiles.get(j);
			    
			    int result = newProjectile.update(delta);
			    
			    if(result==0){			    	
			    	
			        gameProjectiles.remove(j);
			        
			        break;
			    }
			    if(result==2){
			    	gameCharacter.setState(false);
			    }

			}
			
			for(int j = 0; j < gameItems.size(); j++)
			{
			    Item newItem = gameItems.get(j);
			    
			    int result = newItem.update();
			    if(buttonPressed){
			    	if(newItem.getType().equals("ButtonTrigger")){
			    		newItem.deleteBody();
			    		gameItems.remove(j);
			    	}
			    }
			    
			    if(result==1){			    	
			    	score--;
			        gameItems.remove(j);
			        
			        break;
			        
			    }else if(result==2){
			    	gameItems.remove(j);
			    	if(level!=3){
			    		level++;
				    	clearWorld();
			    	}
			    	else
			    		Sys.alert("Yippee!", "You win! Thanks for playing.");
			    	

			        break;
	
			    	
			    }else if(result==3){
			    	buttonPressed=true;
			    	
			    }
			    

			}
			//System.out.println(gameProjectiles.size());

		 }
				 
		
		 public void createBox(Box newBox){
				
			gameBoxes.add(newBox);
		
		} 
		public int getRemainingBoxes(){
			return score;
		}
		 
		public void createProjectile(Projectile newProjectile){
			
			gameProjectiles.add(newProjectile);
			
		}
		
		public void createItem(Item newItem){
			
			gameItems.add(newItem);
			
		}
		
		
		public void createCharacter(Character newCharacter){

			gameCharacter = newCharacter;
				
		} 
		public void clearWorld(){
				buttonPressed=false;
			
			  for(Box box: gameBoxes){
		          box.deleteBody();

			  }
			  gameBoxes.clear();
			  
			  for(Projectile projectile: gameProjectiles){
		          projectile.deleteBody();

			  }
			  gameProjectiles.clear();
			  
			  for(Item item: gameItems){
		          item.deleteBody();

			  }
			  
			  gameItems.clear();
			  gameCharacter.deleteBody();
			  
			  gameLevel.load_level("gamedata/Level_"+level+".xml");
			  score=10;
			  gameCharacter.body.setLinearVelocity(new Vec2(0,0));
			  
			
		}
		

	    /** 
	     * Calculate how many milliseconds have passed 
	     * since last frame.
	     * 
	     * @return milliseconds passed since last frame 
	     */
		
	    public int getDelta() {
	        long time = getTime();
	        int delta = (int) (time - lastFrame);
	        lastFrame = time;
	      
	        return delta;
	    }
	     
	    /**
	     * Get the accurate system time
	     * 
	     * @return The system time in milliseconds
	     */
	    public long getTime() {
	        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	    }
	     
	    /**
	     * Calculate the FPS and set it in the title bar
	     */
	    public void updateFPS() {
	        if (getTime() - lastFPS > 1000) {
	            //Display.setTitle("FPs"+fps);

	            fps = 0;
	            lastFPS += 1000;
	        }
	        fps++;
	    }
		
	    public void start() {
	    	
	    	
	    	 getDelta(); // call once before loop to initialise lastFrame
	         lastFPS = getTime(); // call before loop to initialise fps timer
	    	
	     
	    	 // Static Body
		    Vec2  gravity = new Vec2(0,-10);
		    World gameWorld = new World(gravity);
		    BodyDef groundBodyDef = new BodyDef();
		    groundBodyDef.position.set(0, -15);
		    Body groundBody = gameWorld.createBody(groundBodyDef);
		    PolygonShape groundBox = new PolygonShape();
		    groundBox.setAsBox(800, 0);
		    groundBody.createFixture(groundBox, 0);
		    
		    
		    
		 
		    // Setup world
		    //float timeStep = 1.0f/60.0f;
		    int velocityIterations = 6;
		    int positionIterations = 2;
		    
		    gameLevel  = new Level(gameController,gameWorld,gameCharacter);
		    gameLevel.load_level("gamedata/Level_"+level+".xml");
		    
	
		    
		    
		    // Run loop
		    for (int i = 0; i <BOX_AMOUNT; ++i) {
		    	
		    	//gameBoxes.add(new Box(gameWorld,BodyType.DYNAMIC,(float)(Math.random()*30)-10,(float)(Math.random()*30)-15,(float)(Math.random()*2),(float)(Math.random()*2),(float)(Math.random()*2),1,1f,0.5f,0));
		    	//gameBoxes.add(new Box(gameWorld,BodyType.DYNAMIC,(i)-12,-13,0.2f,1,0,1,1f,0.5f,0));
		    	
		    } 		    
		    
		 //
		    
	     try {
	        Display.setDisplayMode(new DisplayMode(800,600));
	        Display.create();
	    } catch (LWJGLException e) {
	        e.printStackTrace();
	        System.exit(0);
	    }
	  
	    // init OpenGL
	        //GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);        
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);    
	        
	    GL11.glMatrixMode(GL11.GL_PROJECTION);
	    GL11.glLoadIdentity();
	    GL11.glOrtho(0,800, 0, 600, 1, -1);
	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	    
	    Sys.alert("Controls","WASD/Arrow Keys to move\nSpace to shoot\nR to retry level\n");
	    
	    while (!Display.isCloseRequested()) {
	    	
	    	
	    	
	    	float delta = getDelta();
	    	//System.out.print(delta/1000);
	    	//System.out.println(" " + timeStep);
	    	update((int)delta);
	    	gameWorld.step(delta/1000, velocityIterations, positionIterations);
	    	
	    	//font2.drawString(0, 550, "1",Color.red);
	    	
	    	
	        // Clear the screen and depth buffer
	        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);  
	         
	        GL11.glClearColor(1, 1, 1, 1);
	        // set the color of the quad (R,G,B,A)
	       
	             
	        for(Box box: gameBoxes){
	        	box.draw();

		    } 
	        
	        for(Box projectile: gameProjectiles){
	        	projectile.draw();
	        }
	        
	        for(Box item: gameItems){
	        	item.draw();
	        }
	        
	        gameCharacter.draw();
	        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

	       
	        
	   
	        Display.update();
	        
	        Display.sync(60);
	    }
	  
	    Display.destroy();
	    }
	    
	   
	  
	
	public static void main(String[] args) {

		gameController = new WorldController();
	       gameController.start();
	    
	    
			
	    
	}

}
