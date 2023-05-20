import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main extends JFrame {
	private static MyBack mainPanel;
	private static TimeLab timeLab;
	private static ScoreBoard scoreBoard;
	private static Life life;
	private static JLabel resultLab;
	private static MyFighter myFighter;
	private static Vector<Enemy> enemys = new Vector<>();
	private static EnemyFactory enemyFactory;
	private static boolean isStart;
	
	public Main() {
		setTitle("Shooting Game");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(mainPanel = new MyBack());
		mainPanel.setLayout(null);
		setSize(480, 720);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(false);
		requestFocus();

		add(scoreBoard = new ScoreBoard());
		add(life = new Life());
		add(timeLab = new TimeLab(90));
		add(myFighter = new MyFighter());
		resultLab = new JLabel();
		resultLab.setForeground(Color.LIGHT_GRAY);
		resultLab.setFont(new Font("", Font.BOLD, 50));
		resultLab.setBounds(mainPanel.getWidth()/4+10, mainPanel.getHeight()/3, mainPanel.getWidth()/2, 100);

		add(resultLab);
		addKeyListener(myFighter.new MyFighterKeyHandler());
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	class EnemyFactory {
		Thread factoryThread;
		public void createEnemy() {
			factoryThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (timeLab.time>30) { //초반 1분은 쉽게
						try {
							Thread.sleep(randInt(500, 1000));
						} catch (InterruptedException e) {
							return;
						}
						add(new Enemy(randInt(0, mainPanel.getWidth()-50), 0));
					}
					while (timeLab.time>0) { //30초부터는 적기를 더 많이 생성
						try {
							Thread.sleep(randInt(200, 500));
						} catch (InterruptedException e) {
							return;
						}
						add(new Enemy(randInt(0, mainPanel.getWidth()-50), 0));
					}
					gameOver();
					resultLab.setText("CLEAR!");
				}
			});
			factoryThread.start();
		}
	}
	
	class Enemy extends JLabel {
		private final ImageIcon EnemyType1ImgIcon = new ImageIcon("EnemyType1.png");
		private final ImageIcon EnemyType2ImgIcon = new ImageIcon("EnemyType2.png");
		private final int EnemySpeed = 10;
		private boolean isHit;
		Thread enemyThread;
		
		Runnable enemyRun = new Runnable() {
			@Override
			public void run() {
				while (isOutOfPanel(getX(), getY())) {
					for (int i=0; i<5; i++) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							if (isHit) {
								hitEvent();
								return;
							}
							return;
						}
						setLocation(getX(), getY()+EnemySpeed);
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						if (isHit) {
							hitEvent();
							return;
						}
						return;
					}
					int dx = EnemySpeed*(randInt(-9, 10)>0? -1: 1);
					if (isOutOfPanel(getX()+dx, getY())) {
						setLocation(getX()+dx, getY());
						if (randInt(-9, 50)<0) {
							shoot();
						}
					}
				}
				removeEnemy();
			}
		};
		
		public Enemy(int x, int y) {
			isHit = false;
			setIcon(randInt(-9, 10)>0? EnemyType1ImgIcon: EnemyType2ImgIcon);
			setBounds(x, y, getIcon().getIconWidth(), getIcon().getIconHeight());
			enemys.add(this);
			enemyThread = new Thread(enemyRun);
			enemyThread.start();
		}
		
		private void dead() {
			isHit = true;
			enemyThread.interrupt();
			removeEnemy();
		}
		
		private void hitEvent() {
			for (int i=0; i<3; i++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				setVisible(false);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				setVisible(true);
			}
			enemyThread.interrupt();
			setVisible(false);
			remove(this);
		}
		
		private void removeEnemy() {
			try {
				enemys.remove(this);
				setVisible(false);
				remove(this);
			} catch (Exception e) {
				;
			}
		}
		
		private boolean isOutOfPanel(int x, int y) {
			if (y>=mainPanel.getHeight() || x<0 || x>=mainPanel.getWidth()) {
				return false;
			}
			return true;
		}
		
		public void shoot() {
			Bullet b = new Bullet(this);
			mainPanel.add(b);
			b.bulletThread.start();
		}
		
		class Bullet extends JLabel{
			private final ImageIcon bulletImgIcon = new ImageIcon("EnemyMissile.png");
			private final int BulletSpeed = 10;
			Thread bulletThread;
			Bullet meBullet;
			
			Runnable bulletRun = new Runnable() {
				@Override
				public void run() {
					while(getY()<=mainPanel.getHeight()) {
						if (!isStart) {
							meBullet.setVisible(false);
							remove(meBullet);
							return;
						}
						setLocation(getX(), getY()+BulletSpeed);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (!isSeparated(getX(), getY(), getIcon().getIconWidth(), getIcon().getIconHeight(),
								myFighter.getX(), myFighter.getY(), myFighter.getIcon().getIconWidth(), myFighter.getIcon().getIconHeight())) {
							setVisible(false);
							life.decreLife();
							
							myFighter.setVisible(false);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
							}
							myFighter.setVisible(true);
							
							if (life.n==0) {
								
								for (int i=0; i<2; i++) {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
									}
									myFighter.setVisible(false);
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
									}
									myFighter.setVisible(true);
								}
								
								gameOver();
							}
							return;
						}
					}
					setVisible(false);
				}
			};
			
			public Bullet(Enemy enemy) {
				setIcon(bulletImgIcon);
				setBounds(enemy.getX(), enemy.getY(), bulletImgIcon.getIconWidth(), bulletImgIcon.getIconHeight());
				bulletThread = new Thread(bulletRun);
				meBullet = this;
			}
		}
	}
	
	class MyFighter extends JLabel {
		private final ImageIcon fighterImgIcon = new ImageIcon("Fighter.png");
		private final int FighterSpeed = 15;
		
		public MyFighter() {
			setIcon(fighterImgIcon);
			setBounds(mainPanel.getWidth()/2-20, mainPanel.getHeight()-150, fighterImgIcon.getIconWidth(), fighterImgIcon.getIconHeight());
		}
		
		public void setMyFighter() {
			setVisible(true);
			setBounds(mainPanel.getWidth()/2-20, mainPanel.getHeight()-150, fighterImgIcon.getIconWidth(), fighterImgIcon.getIconHeight());
		}
		
		class MyFighterKeyHandler extends KeyAdapter {
			public void keyPressed(KeyEvent e) {
				if (!isStart) {
					startGame();
					return;
				}
				int code = e.getKeyCode();
				
				if (code==KeyEvent.VK_ESCAPE) {
					gameOver();
				}
				
				switch (code) {
				case KeyEvent.VK_UP:
					if (!isOutOfPanel(myFighter.getX(), myFighter.getY()-FighterSpeed)) {
						return;
					}
					myFighter.setLocation(getX(), getY()-FighterSpeed);
					break;
					
				case KeyEvent.VK_DOWN:
					if (!isOutOfPanel(myFighter.getX(), myFighter.getY()+FighterSpeed)) {
						return;
					}
					myFighter.setLocation(getX(), getY()+FighterSpeed);
					break;
					
				case KeyEvent.VK_LEFT:
					if (!isOutOfPanel(myFighter.getX()-FighterSpeed, myFighter.getY())) {
						return;
					}
					myFighter.setLocation(getX()-FighterSpeed, getY());
					break;
					
				case KeyEvent.VK_RIGHT:
					if (!isOutOfPanel(myFighter.getX()+FighterSpeed, myFighter.getY())) {
						return;
					}
					myFighter.setLocation(getX()+FighterSpeed, getY());
					break;
					
				case KeyEvent.VK_SPACE:
					shoot();
					break;
					
				default:
					break;
				}
			}
			
			private boolean isOutOfPanel(int x, int y) {
				if (x<0 || y<0 || 
						x>mainPanel.getWidth()-myFighter.getIcon().getIconWidth() ||
						y>mainPanel.getHeight()-myFighter.getIcon().getIconHeight()) {
					return false;
				}
				return true;
			}
		}
		
		private Enemy isHitEnemy(Bullet b) {
			for (Enemy e: enemys) {
				if (!isSeparated(e.getX(), e.getY(), e.getIcon().getIconWidth(), e.getIcon().getIconHeight(),
						b.getX(), b.getY(), b.getIcon().getIconWidth(), b.getIcon().getIconHeight())) {
					return e;
				}
			}
			return null;
		}
		
		public void shoot() {
			Bullet b = new Bullet();
			mainPanel.add(b);
			b.bulletThread.start();
		}
		
		class Bullet extends JLabel{
			private final ImageIcon bulletImgIcon = new ImageIcon("FighterMissile.png");
			private final int BulletSpeed = 50;
			Bullet myBullet;
			Thread bulletThread;
			
			Runnable bulletRun = new Runnable() {
				@Override
				public void run() {
					while(getY()>=0) {
						if (!isStart) {
							myBullet.setVisible(false);
							remove(myBullet);
							return;
						}
						setLocation(getX(), getY()-BulletSpeed);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Enemy hitEnemy = isHitEnemy(myBullet);
						if (hitEnemy!=null) {
							hitEnemy.dead();
							setVisible(false);
							scoreBoard.addScore();
							return;
						}
					}
					setVisible(false);
				}
			};
			
			public Bullet() {
				setIcon(bulletImgIcon);
				setBounds(myFighter.getX(), myFighter.getY(), bulletImgIcon.getIconWidth(), bulletImgIcon.getIconHeight());
				bulletThread = new Thread(bulletRun);
				myBullet = this;
			}
		}
	}
	
	class Life extends JLabel {
		private int n=3;
		
		public Life() {
			super("목숨: "+3);
			setForeground(Color.LIGHT_GRAY);
			setFont(new Font("", Font.BOLD, 30));
			setBounds(0, 10, 300, 150);
			n = 3;
		}
		
		public void setScore() {
			setText("목숨: "+3);
			n=3;
		}
		
		public void decreLife() {
			if (n==0) {
				return;
			}
			n--;
			setText("목숨: "+n);
		}
	}
	
	class ScoreBoard extends JLabel {
		private int score;
		
		public ScoreBoard() {
			super("점수: "+0);
			setForeground(Color.LIGHT_GRAY);
			setFont(new Font("", Font.BOLD, 30));
			setBounds(0, -20, 300, 150);
			score = 0;
		}
		
		public void setScore() {
			setText("점수: "+0);
			score = 0;
		}
		
		public void addScore() {
			score+=10;
			setText("점수: "+score);
		}
	}
	
	class TimeLab extends JLabel {
		int time;
		Thread timeThread;
		
		Runnable timeRun = new Runnable() {
			@Override
			public void run() {
				while (time>0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
					time-=1;
					setText(getTime(time));
				}
			}
		};
		
		private String getTime(int time) {
			return "남은 시간: "+time/60+"분 "+time%60+"초";
		}
		
		public TimeLab(int time) {
			super("남은 시간: "+time/60+"분 "+time%60+"초");
			setTimeLab();
			this.time = time;
		}
		
		public void setTimeLab() {
			setForeground(Color.LIGHT_GRAY);
			setFont(new Font("", Font.BOLD, 30));
			setBounds(0, -50, 300, 150);
		}
		
		public void timeStart(int time) {
			this.time = time;
			setTimeLab();
			timeThread = new Thread(timeRun);
			timeThread.start();
		}
	}
	
	class MyBack extends JPanel {
		private final ImageIcon backImgIcon = new ImageIcon("backImage.png");
		private final Image backImg = backImgIcon.getImage();
		private int backY;
		private Thread backThread;
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(backImg, 0, backY, this);
		}
		
		Runnable backRun = new Runnable() {
			@Override
			public void run() {
				while (backY>=-900) {
					backY-=5;
					mainPanel.repaint();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		};
		
		public void BackgroundStart() {
			backY=0;
			backThread = new Thread(backRun);
			backThread.start();
		}
	}
	
	public synchronized void startGame() {
		for (Enemy e: enemys) {
			remove(e);
		}
		enemys.clear();
		enemyFactory = new EnemyFactory();
		resultLab.setText("");
		mainPanel.BackgroundStart();
		timeLab.timeStart(90);
		scoreBoard.setScore();
		life.setScore();
		myFighter.setMyFighter();
		enemyFactory.createEnemy();
		isStart = true;
	}
	
	public synchronized void gameOver() {
		myFighter.setVisible(false);
		life.setText("GAME OVER");
		resultLab.setText("YOU DIE!");
		mainPanel.backThread.interrupt();
		enemyFactory.factoryThread.interrupt();
		timeLab.timeThread.interrupt();
		timeLab.setText("남은 시간: 1분 30초");
		for (Enemy e: enemys) {
			e.enemyThread.interrupt();
		}
		myFighter.setMyFighter();
		isStart = false;
	}
	
	public boolean isSeparated(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2) {
		int aX1 = x1, aY1 = y1, aX2 = x1+width1, aY2 = y1+height1;
		int bX1 = x2, bY1 = y2, bX2 = x2+width2, bY2 = y2+height2;
		
		if (aX1<bX2 && aX2>bX1 && aY1<bY2 && aY2>bY1) {
			return false;
		}
		return true;
	}
	
	public static int randInt(int min, int max)  {
		int range = (max - min) + 1;
		return (int)(Math.random() * range) + min;
	}
}
