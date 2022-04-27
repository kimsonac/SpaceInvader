import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;



import java.util.*;


public class SpaceInvaders extends JFrame implements KeyListener {

	private GameHandler handler;
	private JTextArea textArea = new JTextArea();
	
	SpaceInvaders()
	{
		setTitle("Let's play Space Invaders");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		setVisible(true);
		setLocationRelativeTo(null);
		textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
		textArea.addKeyListener(this);
		add(textArea);
		textArea.setEditable(false);
		
		
		handler = new GameHandler(textArea);
		new Thread(new GameThread()).start();
		
		
	}
	
	class GameThread implements Runnable
	{
		@Override
		public void run()
		{
			
			while(!handler.isGameOver())
			{
				
				handler.gameTiming();
				handler.drawAll(); // render
				handler.whoWin();
				
			
			}
			handler.whoWin();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SpaceInvaders mf = new SpaceInvaders();
	}
	
	public void restart()
	{	
		handler.initData();
		new Thread(new GameThread()).start();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_RIGHT:
			handler.moveToRight();
			break;
		case KeyEvent.VK_LEFT:
			handler.moveToLeft();
			break;
		case KeyEvent.VK_DOWN:
			handler.moveToBack();
			break;
		case KeyEvent.VK_UP:
			handler.moveToFront();
		case KeyEvent.VK_SPACE:
			handler.readyToShoot();
			break;
		case KeyEvent.VK_Y:
			if(handler.isGameOver())
				restart();
			break;
		case KeyEvent.VK_N:
			if(handler.isGameOver())
			{	
				
				System.exit(0);
			}
		
		
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}
	
}

class GameHandler 
{

	private final static int SCREEN_WIDTH = 130;
	private final static int SCREEN_HEIGHT = 35;
	private final static int FIELD_WIDTH = 80, FIELD_HEIGHT = 30;
	private final static int PADDING = 3;
	private final static int LETTER_PADDING = 1;
	private final static int INITIAL_VALUE = 0;
	private final static int EDGE_VALUE = 1, VALID_VALUE = 0;
	private final static int BLOCK_SIZE = 41;
	private final static int BULLET_NUM = 10;
	private final static int ENEMY_NUM = 8;
	private final static int NOTE_POSX = 30;
	private final static int NOTE_POSY = 10;
	//private final static int INIT_SPEED = 20;
	//private final static int MAX_SPEED = 10;


	
	private final int INITIAL_PLAYERX = 41;
	private final int INITIAL_PLAYERY = 27;
	private final int INITIAL_ENEMYY = 2;
	private final int INITIAL_ENEMYX_1 = 10;
	private final int INITIAL_ENEMYX_2 = 15;
	private final int ENEMY_FLAG = 4;
	private final int ENEMY_MOVE_MAX = 38;

	
	private JTextArea text;
	private char[][] buffer;
	private int field[];
	private int score;
	//private int speed, speedCounter;
	

	ArrayList<EnemyObject> enemyList = new ArrayList<EnemyObject>();
	Iterator<EnemyObject> enemyItr = enemyList.iterator();
	/*ArrayList<BulletObject> bulletList = new ArrayList<BulletObject>();
	
	Iterator<BulletObject> bulletItr = bulletList.iterator();
	*/
	
	
	public PlayerObject player;
	public EnemyObject enemy;
	public BulletObject bullet[];
	public EnemyBulletObject enemyBullet[];
	
	public boolean isGameOver;
	public boolean enemyMoving;
	//public boolean forceDown;

	
	public GameHandler(JTextArea txt)
	{
		text = txt;
		field = new int[FIELD_WIDTH * FIELD_HEIGHT];
		buffer = new char[SCREEN_WIDTH][SCREEN_HEIGHT];

		player = new PlayerObject(INITIAL_PLAYERX, INITIAL_PLAYERY, ">-o-<"); 
		bullet = new BulletObject[BULLET_NUM];
		enemyBullet = new EnemyBulletObject[BULLET_NUM];
		
		enemyMoving = true;
		isGameOver = false;
		initData();
	}
	
	
	public void gameTiming()
	{
		try {
			Thread.sleep(30);
		}
		
		catch(InterruptedException ex)
		{
			ex.printStackTrace();
		}
		//speedCounter ++;
		//forceDown = (speedCounter == speed);
	}
	
	
	
	public void initData()
	{
		score = INITIAL_VALUE;
		//speed = INIT_SPEED;
		//speedCounter = 0;
		//forceDown = false;

		
		for(int x = INITIAL_VALUE; x < FIELD_WIDTH; x++) 
			for(int y = 0; y < FIELD_HEIGHT; y++) 
				field[y * FIELD_WIDTH + x] = (x == 0 || x == FIELD_WIDTH - 1 || y == FIELD_HEIGHT -1) ? EDGE_VALUE : VALID_VALUE;
		
		for(int x = INITIAL_VALUE; x < BULLET_NUM; x++)
		{	
			bullet[x] = new BulletObject("!", false);
			//bulletList.add(bullet[x]);
		}
			
		
		for(int x = INITIAL_VALUE; x < ENEMY_NUM; x++)
		{

			
			if(x == INITIAL_VALUE)
			{	
				enemy = new EnemyObject(INITIAL_ENEMYX_1, INITIAL_ENEMYY);
				enemyList.add(enemy);
			}
			
			if(x != 0 && x < ENEMY_FLAG)
			{	
				enemy = new EnemyObject(enemyList.get(INITIAL_VALUE + x - LETTER_PADDING).posX + 10, INITIAL_ENEMYY);
				enemyList.add(enemy);
			}
			
			if(x == ENEMY_FLAG)
			{
				enemy = new EnemyObject(INITIAL_ENEMYX_2, INITIAL_ENEMYY * 2);
				enemyList.add(enemy);
			}
				
			if(x > ENEMY_FLAG)
			{	
				enemy = new EnemyObject(enemyList.get(ENEMY_FLAG + x - 5).posX + 10, enemyList.get(ENEMY_FLAG).posY);
				enemyList.add(enemy);
			}
			
			enemyBullet[x] = new EnemyBulletObject(enemyList.get(x).posX + 2, enemyList.get(x).posY - 1, false);
		}
		
		clearBuffer();
		
	}
	
	public void clearBuffer()
	{
		for(int y = INITIAL_VALUE; y < SCREEN_HEIGHT; y++)
		{	
			for(int x = 0; x < SCREEN_WIDTH; x++) 
				buffer[x][y] = '.';
		}
	}
	
	public boolean isGameOver()
	{
		return isGameOver;
	}
	
	public void whoWin()
	{
		if(enemyItr.hasNext() == false)
			playerWin();
		
		
		else if(player.isPlayerLose == true)
			drawGameOver();
	}
	
	public void playerWin()
	{
		drawToBuffer(NOTE_POSX, NOTE_POSY, "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
		drawToBuffer(NOTE_POSX, NOTE_POSY+1, "┃         YOU WIN!!         ┃");
		drawToBuffer(NOTE_POSX, NOTE_POSY+2, "┃                           ┃");
		drawToBuffer(NOTE_POSX, NOTE_POSY+3, "┃      PLAY AGAIN? (Y/N)    ┃");
		drawToBuffer(NOTE_POSX, NOTE_POSY+4, "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
		
		render();
	}
	
	public void drawGameOver()
	{
		drawToBuffer(NOTE_POSX, NOTE_POSY, "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
		drawToBuffer(NOTE_POSX, NOTE_POSY+1, "┃         YOU LOSE!!        ┃");
		drawToBuffer(NOTE_POSX, NOTE_POSY+2, "┃                           ┃");
		drawToBuffer(NOTE_POSX, NOTE_POSY+3, "┃      PLAY AGAIN? (Y/N)    ┃");
		drawToBuffer(NOTE_POSX, NOTE_POSY+4, "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
		
		render();
	}

	
	private void drawToBuffer(int px, int py, String c)
	{
		for(int x = INITIAL_VALUE; x < c.length(); x++)
			buffer[px + x][py] = c.charAt(x);
	}
	
	private void drawToBuffer(int px, int py, char c)
	{
		buffer[px+PADDING][py] = c;
	}
	
	private void drawBlock()
	{
		for(int x = INITIAL_VALUE; x < BLOCK_SIZE; x++)
		{
			drawToBuffer(SCREEN_WIDTH - (PADDING+BLOCK_SIZE), 1, "┌───────────────────┐");
			drawToBuffer(SCREEN_WIDTH - (PADDING+BLOCK_SIZE), 2, "│ SCORE: " + score + "0         │");
			drawToBuffer(SCREEN_WIDTH - (PADDING+BLOCK_SIZE), 3, "└───────────────────┘");
		}
	}
	
	public void drawAll()
	{
		for(int x = INITIAL_VALUE; x < FIELD_WIDTH; x++)
		{
			// ====== 필드 생성 ======
			for(int y = INITIAL_VALUE; y < FIELD_HEIGHT; y++)
				drawToBuffer(x, y, " #".charAt(field[y * FIELD_WIDTH + x])); 
		}
		

		enemyMove();
		makeImages(); // 플레이어, 적 생성
		shootBullet();
		enemyShoot();
		drawBlock(); // 스코어 블록 생성
		drawToBuffer(97, 27, "by Y.Kim"); // 제작자 이름
		
		render();
	}
	
	private void render()
	{
		StringBuilder sb = new StringBuilder();
		for(int y = INITIAL_VALUE; y < SCREEN_HEIGHT; y++)
		{
			for(int x = INITIAL_VALUE; x < SCREEN_WIDTH; x++)
				{
					sb.append(buffer[x][y]);
				}
			sb.append("\n");
		}
		
		text.setText(sb.toString());
	}
	

	

	
	public void moveToRight()
	{
		player.posX ++;
		
		if(player.posX >= FIELD_WIDTH - 2)
		{
			player.posX --;
		}
		
	}
	
	public void moveToLeft()
	{
		player.posX --;
		
		if(player.posX <= PADDING)
		{
			player.posX ++;
		}
		
	}
	
	public void moveToFront()
	{
		player.posY --;
		
		for(int x = INITIAL_VALUE; x < ENEMY_NUM; x++)
		{
			if(field[player.posY] == enemyList.get(x).posY)
			{
				isGameOver = true;
			}
		}
		
		if(player.posY <= INITIAL_VALUE)
		{
			player.posY ++;
		}
		
	}
	
	public void moveToBack()
	{
		player.posY ++;
		
		if(player.posY >= FIELD_HEIGHT - 1)
		{
			player.posY --;
		}
		
		
	}
	
	public void readyToShoot()
	{
		
		
		for(int x = INITIAL_VALUE; x < BULLET_NUM; x++)
		{
			
			if(bullet[x].isKeyPressed == false) 
			{
				bullet[x].posX = player.posX + 2;
				bullet[x].posY = player.posY - 1;
				bullet[x].isKeyPressed = true;
				break;
			}
		}
	}
	
	public void shootBullet()
	{
		for(int y = INITIAL_VALUE; y < BULLET_NUM; y++)
		{
			if(bullet[y].isKeyPressed == true)
			{
					
				bullet[y].posY --;
				drawToBuffer(bullet[y].posX, bullet[y].posY, bullet[y].image);		
				
				
				for(int x = INITIAL_VALUE; x < enemyList.size(); x++) // 적한테 닿았을 때
				{
					if (bullet[y].posX >= enemyList.get(x).posX && bullet[y].posX < enemyList.get(x).posX + 5 && bullet[y].posY == enemyList.get(x).posY)
			
					{
						score ++;
						bullet[y].isKeyPressed = false;
						bullet[y].clearPos();
						
						enemyList.get(x).isAttacked = true;
						enemyList.remove(x);
						
		
					}
				
				}
				
				if(bullet[y].posY <= EDGE_VALUE) // 벽에 닿았을 때
				{
					bullet[y].isKeyPressed = false;
					bullet[y].clearPos();
					
				}				
				
			}
						
		}
		
	}
	
	public void makeImages()
	{
		drawToBuffer(player.posX, player.posY, player.image);
			
	}
	

	
	public void enemyMove()
	{
	
		if(enemyMoving)
		{
			if(enemyList.get(INITIAL_VALUE).posX >= ENEMY_MOVE_MAX)
		
			{
				for(int i=INITIAL_VALUE; i < enemyList.size(); i++)
					enemyList.get(i).posY ++;
				
				enemyMoving = false;
			}
			
			else 
			{
				for(int i=INITIAL_VALUE; i<enemyList.size(); i++)
				{
					enemyList.get(i).posX ++;
			
				}				
			
			}
		}
			
		else
		{
		
			if(enemyList.get(INITIAL_VALUE).posX <= INITIAL_ENEMYX_1)
			{
				for(int i=INITIAL_VALUE; i < enemyList.size(); i++)
					enemyList.get(i).posY ++;
				
				enemyMoving = true;
				
			}
			
			else
			{
				for(int i=INITIAL_VALUE; i<enemyList.size(); i++)
				{
					enemyList.get(i).posX --;
					
				}
				gameTiming();
				
			}
			
		}
		
		
		for(int x = INITIAL_VALUE; x < enemyList.size(); x++)
			if(enemyList.get(x).isAttacked == false)
				drawToBuffer(enemyList.get(x).posX, enemyList.get(x).posY, enemyList.get(x).image());
		
	}

	public void enemyShoot()
	{
		//if(forceDown)
		gameTiming();
		
			int num = (int)(Math.random() * 7);
			
			enemyBullet[num].isEnemyShoot = true;
			
			for(int i = INITIAL_VALUE; i < enemyList.size(); i++)
			{
				if(enemyBullet[i].isEnemyShoot == true)
				{
					enemyBullet[i].posY++;
					drawToBuffer(enemyBullet[i].posX, enemyBullet[i].posY, enemyBullet[i].image);
					
					if(enemyBullet[i].posX >= player.posX && enemyBullet[i].posX < player.posX + 5 && enemyBullet[i].posY == player.posY)
					{
						enemyBullet[i].isEnemyShoot = false;
						enemyBullet[i].posX = enemyList.get(i).posX;
						enemyBullet[i].posY = enemyList.get(i).posY;
						
						player.isPlayerLose = true;
						isGameOver = true;
						
					}
					
					if(enemyBullet[i].posY >= FIELD_HEIGHT + 4)
					{
						enemyBullet[i].isEnemyShoot = false;
						enemyBullet[i].posX = enemyList.get(i).posX;
						enemyBullet[i].posY = enemyList.get(i).posY;
					}
				}
				
			
		}
		//speedCounter = 0;
				
	}
	

abstract class GameObject
{
	
	protected int posX;
	protected int posY;
	protected String image;
	
	abstract public String image();
	abstract public int posX();
	abstract public int posY();

}

class PlayerObject extends GameObject
{
	private boolean isPlayerLose = false;
	
	public PlayerObject(int x, int y, String look)
	{
		posX = x;
		posY = y;
		image = look;
	}
	
	public String image()
	{
		return image;
	}
	
	public int posX()
	{
		return posX;
	}
	
	public int posY()
	{
		return posY;
	}
	
	public int posX(int px)
	{
		posX = px;
		return posX;
	}
	
	public int posY(int py)
	{
		posY = py;
		return posY;
	}
}

class EnemyObject extends GameObject
{
	boolean isAttacked;

	public EnemyObject(int x, int y)
	{
		posX = x;
		posY = y;
		image = "[XUX]";
		isAttacked = false;

	}
	
	public String image()
	{
		return image;
	}
	
	public int posX()
	{
		return posX;
	}
	
	public int posY()
	{
		return posY;
	}
	
	public int posX(int px)
	{
		posX = px;
		return posX;
	}
	
	public int posY(int py)
	{
		posY = py;
		return posY;
	}
	
}

class EnemyBulletObject extends GameObject
{
	private boolean isEnemyShoot;
	
	public EnemyBulletObject(int x, int y, boolean answer)
	{
		posX = x;
		posY = y;
		image = "v";
		isEnemyShoot = answer;
	}
	
	public String image()
	{
		return image;
	}
	
	public int posX()
	{
		return posX;
	}
	
	public int posY()
	{
		return posY;
	}
	

	
}





class BulletObject extends GameObject
{

	private boolean isKeyPressed;
	
	public BulletObject(String look, boolean answer) 
	{
		posX = player.posX + 2;
		posY = player.posY - 1;
		image = look;
		isKeyPressed = answer;
	}
	
	public String image()
	{
		return image;
	}
	
	public int posX()
	{
		return posX;
	}
	
	public int posY()
	{
		return posY;
	}
	

	
	public void clearPos()
	{
		posX = player.posX + 2;
		posY = player.posY - 1;
	}
	
}
}
