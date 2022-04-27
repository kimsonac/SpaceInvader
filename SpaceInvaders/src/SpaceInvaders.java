import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;



public class SpaceInvaders extends JFrame implements KeyListener {
	
	private GameEngine controller;
	private GameView view;
	private GameHandler handler;
	private JPanel contentPane;


	
	SpaceInvaders()
	{
		initJFrame();
		initMVC(contentPane);
		
		new Thread(controller).start();
		
	}
	
	private void initMVC(JPanel contentPane) //mvc �ʱ�ȭ, ��ü ����
	{
		
		handler = new GameHandler();
		view = new GameView(contentPane, handler);
		controller = new GameEngine(view);
	}
	
	private void initJFrame()
	{
		
		setTitle("Let's play Space Invaders");
		setSize(900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		// ***** ��ü �г� ***** 
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0,0));
		setContentPane(contentPane);
		
		
	}

	public static void main(String[] args) {
		
		new SpaceInvaders();
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

//********************************** ���� ���� **********************************
class GameEngine implements Runnable
{
	private GameView view;
	
	public GameEngine(GameView view)
	{
		this.view = view;
		
	}
	
	// ****** ���� ���� ****** (���� ������ ���������� ���� ��Ʈ�� ����)
	@Override
	public void run() 
	{
		
		boolean gameContinues = true;
		while (gameContinues)
		{
			try {
				Thread.sleep(50);
			}
			
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}

// ********************************** �׷��� **********************************
class GameView extends JPanel  
{
	private JPanel panelCard;
	private JPanel contentPane;
	
	private final static String PANEL_NEXT="Start Game";
	private final static String PANEL_GAME="Game";
	
	private GameHandler handler;
	
	
	public GameView(JPanel cont, GameHandler handler)
	{
		this.handler = handler;	
		initPane(cont);
		makePlaypage();
		
	}
	
	
	public void initPane(JPanel cont)
	{
		this.contentPane = cont;
		
		// ***** ī�� ���̾ƿ� ���� *****
		panelCard = new JPanel();
		
		contentPane.add(panelCard, null);
		panelCard.setLayout(new CardLayout());
					
		// ***** ù ��° ������ *****
		JPanel startPage = new JPanel();
		panelCard.add(startPage, "name_1286310151149000");
		startPage.setLayout(null);
					
		// 1. �̹��� ���
		ImageIcon image = new ImageIcon("image/nasa.jpg");
		JLabel startBackgroundImage = new JLabel(image);
		startBackgroundImage.setBounds(40, 23, 800, 400);
		startPage.add(startBackgroundImage);
					
		// 2. ��ŸƮ ��ư
		JButton startButton = new JButton(PANEL_NEXT);
		startButton.setBounds(0, 450, 886, 100);
		startPage.add(startButton);
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				JButton button = (JButton) e.getSource();
				CardLayout c1 = (CardLayout) (panelCard.getLayout());
				c1.show(panelCard, button.getText());
			}
		});
	
	}

	
	public void makePlaypage()
	{
		// ***** �� ��° ������ *****
		JPanel playPage = new JPanel();
		playPage.setBackground(Color.DARK_GRAY);
		panelCard.add(playPage, PANEL_NEXT);
		playPage.setLayout(new BorderLayout(0,0));

	}

	// ***** �̹��� �׷��� *****
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		fillSidePannel(g2d);
	}

	private void fillSidePannel(Graphics2D g2d)
	{
		g2d.setPaint(new Color(0, 8, 52));
		g2d.fillRect(750, 0, 150, 600);
	}
	
	private void drawPlayer(Graphics2D g2d)
	{
		g2d.drawImage(handler.player.image, handler.player.posX, handler.player.posY, this);
	}
	
	private void drawEnemys(Graphics2D g2d, int x)
	{
		g2d.drawImage(handler.enemy.image, handler.enemyList.get(x).posX, handler.enemyList.get(x).posY, this);
	}
	
	private void drawBullet(Graphics2D g2d, int x)
	{
		g2d.drawImage(handler.bullet[0].image, handler.bullet[x].posX, handler.bullet[x].posY, this);
	}
	
	private void drawEnemyBullet(Graphics2D g2d, int x)
	{
		g2d.drawImage(handler.enemyBullet[0].image, handler.enemyBullet[x].posX, handler.enemyBullet[x].posY, this);
	}
	
	
	

}

// ********************************** ���� ���� **********************************
class GameHandler
{
	
	
	// ��� ����
	private final static int FIELD_WIDTH = 80, FIELD_HEIGHT = 30;
	private final static int EDGE_VALUE = 1, VALID_VALUE = 0;
	private final static int INITIAL_VALUE = 0;
	private final static int LETTER_PADDING = 1;
	private final static int PADDING = 3;
		
	// ��� ���� - ��ü
	private final static int INITIAL_PLAYERX = 41;
	private final static int INITIAL_PLAYERY = 27;
	private final static int INITIAL_ENEMYY = 2;
	private final static int INITIAL_ENEMYX_1 = 10;
	private final static int INITIAL_ENEMYX_2 = 15;
	private final static int ENEMY_NUM = 8;
	private final static int ENEMY_FLAG = 4;
	private final static int ENEMY_MOVE_MAX = 38;
	private final static int BULLET_NUM = 10;
	
	
	// ��ü
	public PlayerObject player;
	public EnemyObject enemy;
	public BulletObject bullet[];
	public EnemyBulletObject enemyBullet[];
	
	ArrayList<EnemyObject> enemyList = new ArrayList<EnemyObject>();
	Iterator<EnemyObject> enemyItr = enemyList.iterator();
	
	// ���� ������
	private int score;
	private int field[];
	public boolean isGameOver;
	public boolean enemyMoving;

	
	public GameHandler()
	{
		player = new PlayerObject(INITIAL_PLAYERX, INITIAL_PLAYERY);
		bullet = new BulletObject[BULLET_NUM];
		enemyBullet = new EnemyBulletObject[BULLET_NUM];
		
	}
	
	// **** Functions ****
	
	public void initData()
	{
		// 1. ���ھ�
		score = INITIAL_VALUE;
		
		// 2. �÷��� �ʵ� ���� ����
		for(int x = INITIAL_VALUE; x < FIELD_WIDTH; x++) 
			for(int y = 0; y < FIELD_HEIGHT; y++) 
				field[y * FIELD_WIDTH + x] = (x == 0 || x == FIELD_WIDTH - 1 || y == FIELD_HEIGHT -1) ? EDGE_VALUE : VALID_VALUE;
		
		// 3. �� ��ü
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
		
		// 4. �÷��̾� �Ѿ�
		for(int x = INITIAL_VALUE; x < BULLET_NUM; x++)
		{	
			bullet[x] = new BulletObject(false);
		}
		
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
		
		for(int x = INITIAL_VALUE; x < enemyList.size(); x++)
		{
			if(player.posY == enemyList.get(x).posY)
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
				// ���� �׷��� �׷� �־�� ��
				
				for(int x = INITIAL_VALUE; x < enemyList.size(); x++) // ������ ����� ��
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
				
				if(bullet[y].posY <= EDGE_VALUE) // ���� ����� ��
				{
					bullet[y].isKeyPressed = false;
					bullet[y].clearPos();
					
				}				
				
			}
						
		}
		
	}
	
	public void enemyMove()
	{
	
		if(enemyMoving) // �� �Ѿ˿� �¾Ҵµ��� �۵�����?? -> �̵���ų �ְ� �ִ� ��� �ϱ�
		{
			if(enemyList.get(INITIAL_VALUE).posX >= ENEMY_MOVE_MAX) // ��->�� ���� ��� ����: �ι�°�� ������ ���ʹ̰� ���� ����� �� (enemy.get(������).posX >= field_width - 1) 	
			{
				for(int i=INITIAL_VALUE; i < enemyList.size(); i++)
					enemyList.get(i).posY ++;
				
				enemyMoving = false;
			}
			
			else 
			{
				for(int i=INITIAL_VALUE; i<enemyList.size(); i++)
					enemyList.get(i).posX ++;
	
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
					enemyList.get(i).posX --;
			}
			
		}
		
		
		/* for(int x = INITIAL_VALUE; x < enemyList.size(); x++)
			if(enemyList.get(x).isAttacked == false) 
			*/
				
		
	}

	public void enemyShoot()
	{
			int num = (int)(Math.random() * 7);
			
			enemyBullet[num].isEnemyShoot = true;
			
			for(int i = INITIAL_VALUE; i < enemyList.size(); i++)
			{
				if(enemyBullet[i].isEnemyShoot == true)
				{
					enemyBullet[i].posY++;
					// �� �׸��� �׷��� �� ��� �� ����
					
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
	}
	
	
	
	
	// ********************************** ���� ������Ʈ **********************************
	abstract class GameObject
	{
		protected int posX;
		protected int posY;
		protected Image image;
		
	}

	class PlayerObject extends GameObject
	{
		private boolean isPlayerLose = false;
		
		public PlayerObject(int x, int y)
		{
			posX = x;
			posY = y;
			image = new ImageIcon("player.png").getImage();
		}
	}

	class EnemyObject extends GameObject
	{
		boolean isAttacked;

		public EnemyObject(int x, int y)
		{
			posX = x;
			posY = y;
			image = new ImageIcon("enemy.png").getImage();
			isAttacked = false;

		}
	}

	class EnemyBulletObject extends GameObject
	{
		private boolean isEnemyShoot;
		
		public EnemyBulletObject(int x, int y, boolean answer)
		{
			posX = x;
			posY = y;
			image = new ImageIcon("enemy bullet.png").getImage();
			isEnemyShoot = answer;
		}
	}


	class BulletObject extends GameObject
	{

		private boolean isKeyPressed;
		
		public BulletObject(boolean answer) 
		{
			posX = player.posX + 2;
			posY = player.posY - 1;
			image = new ImageIcon("bullet.png").getImage();
			isKeyPressed = answer;
		}
		
		public void clearPos()
		{
			posX = player.posX + 2;
			posY = player.posY - 1;
		}
		
	}
	
}

