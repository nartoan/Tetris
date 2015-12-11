package Tetris.Goc;

// JTetris.java
import Sound.Sound;
import highScore.Score;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Chuong trinh choi Tetris don gian.
 *
 * Luat choi:
 *
 * Su dung phim j-k-l de di chuyen, n de cho roi (hoac phim 4-5-6 0) Trong khi
 * di chuyen, cac dong da duoc lap day se chuyen mau xanh. Xoa tu 1 - 4 dong se
 * lan luot duoc diem la 5, 10, 20, 40.
 */

/*
 * Chu y: -"currentPiece" chi vao hinh hien tai dang roi, hoac la null khi khong
 * co hinh nao. -tick() chuyen hinh hien tai -Mot doi tuong thoi gian khi hinh
 * roi -Board.undo() duoc su dung de chuyen hinh nhu vi tri cu va
 * Board.place()duoc su dung de chuyen hinh trong vi tri moi.
 */
public class JTetris extends JComponent {

    Score sco;
    protected boolean isSwap = false;
    protected boolean suggest = true;
    protected boolean music = true;

    protected JFrame frameGame = new JFrame("Stanford Tetris");
    // gia tri cho biet la da ket thuc game chua
    protected boolean died = true;
    // Kich co ban choi
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    // Khoang trong toi thieu phia tren cho phep hien hinh moi. Tro choi ket
    // thuc khi khoang trong <4.
    public static final int TOP_SPACE = 4;

    // Khi nhan gia tri tru, choi voi so hinh fix la 100
    protected boolean testMode = false;
    public final int TEST_LIMIT = 100;

    // Add by Fizz
    public int predict = -1;
    public int current;
    // Phuong phap toi uu
    protected boolean DRAW_OPTIMIZE = false;

    // Cau truc du lieu ban choi
    protected Board board;
    protected Piece[] pieces;

    // Hinh hien tai
    protected Piece currentPiece;
    protected int currentX;
    protected int currentY;
    protected boolean moved; // kiem tra da di chuyen hinh hay chua

    // Tao hinh tai toa do x, y
    protected Piece newPiece;
    protected int newX;
    protected int newY;

    // Trang thai tro choi
    protected boolean gameOn; // true neu dang choi
    protected int count; // bao nhieu hinh da choi
    protected long startTime; // su dung de do thoi gian da choi
    protected Random random; // tao ngau nhien hinh moi

    // Dieu khien
    protected JLabel highLabel ;
    protected JLabel countLabel;
    protected JLabel scoreLabel;
    protected int score;
    protected JLabel timeLabel;
    protected JButton startButton;
    protected JButton stopButton;
    protected JButton pauseButton;
    protected JCheckBox musicButton;
    protected JButton endButton;
    protected javax.swing.Timer timer;
    protected JSlider speed;
    protected JCheckBox testButton;

    public final int DELAY = 400; // milliseconds

    protected Sound musicBackground = new Sound("src\\Sound\\gunny.mp3");

    public JTetris(int width, int height) throws FileNotFoundException {
        super();
        this.sco = new Score("src\\highScore\\score.txt");
        setPreferredSize(new Dimension(width, height));
        gameOn = false;

        pieces = Piece.getPieces();
        board = new Board(WIDTH, HEIGHT + TOP_SPACE);

        /*
         * Su dung ban phim de dieu khien
         */
        // Trai
        registerKeyboardAction((ActionEvent e) -> {
            tick(LEFT);
        }, "left", KeyStroke.getKeyStroke('5'), WHEN_IN_FOCUSED_WINDOW);
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(LEFT);
            }
        }, "left", KeyStroke.getKeyStroke('a'), WHEN_IN_FOCUSED_WINDOW);

        // Phai
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(RIGHT);
            }
        }, "right", KeyStroke.getKeyStroke('6'), WHEN_IN_FOCUSED_WINDOW);
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(RIGHT);
            }
        }, "right", KeyStroke.getKeyStroke('d'), WHEN_IN_FOCUSED_WINDOW);

        // Quay
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(ROTATE);
            }
        }, "rotate", KeyStroke.getKeyStroke('5'), WHEN_IN_FOCUSED_WINDOW);
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(ROTATE);
            }
        }, "rotate", KeyStroke.getKeyStroke('w'), WHEN_IN_FOCUSED_WINDOW);

        // Tha
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(DOWN);
            }
        }, "down", KeyStroke.getKeyStroke('0'), WHEN_IN_FOCUSED_WINDOW);
        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(DOWN);
            }
        }, "down", KeyStroke.getKeyStroke('s'), WHEN_IN_FOCUSED_WINDOW);

        registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(DROP);
            }
        }, "drop", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), WHEN_IN_FOCUSED_WINDOW);

        // Tao doi tuong Timer de tinh thoi gian chuyen dong xuong cua mot hinh
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(DOWN);
            }
        });

        requestFocusInWindow();
    }

    /**
     * Trai thai hien tai va thoi gian choi
     */
    public void startGame() {
        died = false;
        pauseButton.setText("Pause");
        // khoi phuc lai trang thai choi
        board = new Board(WIDTH, HEIGHT + TOP_SPACE);
        // Ve van co moi
        repaint();

        count = 0;
        score = 0;
        updateCounters();
        gameOn = true;

        // Dat che do checkbox tai luc khoi dong tro choi.
        if (testMode) {
            random = new Random(0);
        } else {
            random = new Random();
        }

        enableButtons();
        timeLabel.setText(" ");
        addNewPiece();
        timer.start();
        startTime = System.currentTimeMillis();
    }

    /**
     * Cai dat che do kich hoat cua nut strat/stop.
     */
    protected void enableButtons() {
        startButton.setEnabled(!gameOn);
        stopButton.setEnabled(gameOn);
        pauseButton.setEnabled(!died);
    }

    /**
     * Dung tro choi
     */
    public void stopGame() {
        musicBackground.suspend();
        died = true;
        gameOn = false;
        enableButtons();
        timer.stop();

        long delta = (System.currentTimeMillis() - startTime) / 10;
        timeLabel.setText(Double.toString(delta / 100.0) + " seconds");
        
        if (sco.updateScore(score)) {
            String temp = JOptionPane.showInputDialog("Điểm cao mới!\nNhập tên vào : ");
            sco.updateScore(temp, score);
        } else {
            JOptionPane.showMessageDialog(null, "Bạn chơi tệ quá!!");
        }
        try {
            sco.outToFile("src\\highScore\\score.txt");
        } catch (IOException ex) {
            Logger.getLogger(JTetris.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void pauseGame() {
        gameOn = !gameOn;
        if (!gameOn) {
            pauseButton.setText("Resume");
            timer.stop();
            musicBackground.suspend();
        } else {
            pauseButton.setText("Pause");
            timer.start();
        }
    }

    /**
     * Cho hinh, can cai dat hinh vao trong tro choi va cho no la hinh hien tai.
     * Can ve lai hinh, Tro choi phai goi commint() (trai thai luu va khoi phuc
     * cac can choi)
     *
     */
    public int setCurrent(Piece piece, int x, int y) {
        int result = board.place(piece, x, y);

        if (result <= Board.PLACE_ROW_FILLED) { // SUCESS

            if (currentPiece != null) {
                repaintPiece(currentPiece, currentX, currentY);
            }
            currentPiece = piece;
            currentX = x;
            currentY = y;

            repaintPiece(currentPiece, currentX, currentY);
        } else {
            board.undo();
        }

        return (result);
    }

    /**
     * Chon hinh tiep theo ngau nhien va cai dat trong startGame(). Edited by
     * Fizz
     */
    public Piece pickNextPiece() {
        int pieceNum;
        pieceNum = (int) (pieces.length * random.nextDouble());
        if (this.predict == -1) {
            predict = (int) (pieces.length * random.nextDouble());
        }
        Piece piece = pieces[predict];
        current = predict;

        predict = pieceNum;

        return (piece);
    }

    /**
     * Them mot hinh bat ky vao dau cua ban choi.
     */
    public void addNewPiece() {
        count++;

        if (testMode && count == TEST_LIMIT + 1) {
            stopGame();
            return;
        }

        // Luu tam thoi trang thai hien tai
        board.commit();
        currentPiece = null;

        Piece piece = pickNextPiece();

        int px = (board.getWidth() - piece.getWidth()) / 2;
        int py = board.getHeight() - piece.getHeight();

        int result = setCurrent(piece, px, py);

        if (result > Board.PLACE_ROW_FILLED) {
            stopGame();
        }

        updateCounters();
    }

    /**
     * Nang cap biet dem cho cac gia tri tiep theo.
     */
    protected void updateCounters() {
        sco.updateScore(score);
        highLabel.setText("HighScore :" + sco.high());
        countLabel.setText("Pieces : " + count);
        scoreLabel.setText("Score : " + score);
        if (score >= 50 && score < 100) {
            setForeground(Color.magenta);
        }
        if (score >= 100 && score < 200) {
            setForeground(Color.red);
        }
        if (score >= 200) {
            setForeground(Color.orange);
        }
    }

    /**
     * Tao vi tri moi cua hinh.)
     */
    public void computeNewPosition(int verb) {

        newPiece = currentPiece;
        newX = currentX;
        newY = currentY;

        switch (verb) {
            case LEFT:
                newX--;
                break;

            case RIGHT:
                newX++;
                break;

            case ROTATE:
                newPiece = newPiece.fastRotation();
                newX = newX + (currentPiece.getWidth() - newPiece.getWidth()) / 2;
                newY = newY + (currentPiece.getHeight() - newPiece.getHeight()) / 2;
                break;

            case DOWN:
                newY--;
                break;

            case DROP:
                newY = board.dropHeight(newPiece, newX);
                if (newY > currentY) {
                    newY = currentY;
                }
                break;

            default:
                throw new RuntimeException("Bad verb");
        }

    }

    // Cac phim dieu khien
    public static final int ROTATE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int DROP = 3;
    public static final int DOWN = 4;

    /**
     * Coi toi su thay doi vua hinh hien tai.
     */
    public void tick(int verb) {
        if (music && gameOn) {
            if (!musicBackground.isAlive()) {
                musicBackground.start();
            } else {
                musicBackground.resume();
            }
        } else {
            musicBackground.suspend();
        }

        if (!gameOn) {
            return;
        }

        if (currentPiece != null) {
            board.undo(); // xoa hinh tai vi tri cu
        }

        computeNewPosition(verb);

        int result = setCurrent(newPiece, newX, newY);

        // neu xoa hang co van de, ve lai toan bo ban choi
        if (result == Board.PLACE_ROW_FILLED) {
            repaint();
        }

        boolean failed = (result >= Board.PLACE_OUT_BOUNDS);

        // neu khong lam viec, quay lai trang thai truoc do
        if (failed) {
            if (currentPiece != null) {
                board.place(currentPiece, currentX, currentY);
            }
            repaintPiece(currentPiece, currentX, currentY);
        }

        /*
         * Hinh tiep xu vois hang duoi cung the nao?
         */
        if (failed && verb == DOWN && !moved) {
            int cleared = board.clearRows();
            if (cleared > 0) {
                Sound player2 = null;
                switch (cleared) {
                    case 1:
                        score += 5;
                        break;
                    case 2:
                        score += 10;
                        player2 = new Sound("src\\Sound\\MDouble.mp3");
                        player2.start();
                        break;
                    case 3:
                        score += 20;
                        player2 = new Sound("src\\Sound\\MTriple.mp3");
                        player2.start();
                        break;
                    case 4:
                        score += 40;
                        player2 = new Sound("src\\Sound\\MTetris.mp3");
                        player2.start();
                        break;

                }
                updateCounters();
                repaint();
            }

            if (board.getMaxHeight() > board.getHeight() - TOP_SPACE) {
                stopGame();
            } else {
                if (isSwap) {
                    board.swapH();
                }
                addNewPiece();
            }
        }

        moved = (!failed && verb != DOWN);
    }

    public void repaintPiece(Piece piece, int x, int y) {
        if (DRAW_OPTIMIZE) {
            int px = xPixel(x);
            int py = yPixel(y + piece.getHeight() - 1);
            int pwidth = xPixel(x + piece.getWidth()) - px;
            int pheight = yPixel(y - 1) - py;

            repaint(px, py, pwidth, pheight);
        } else {
            repaint();
        }
    }

    protected final float dX() {
        return (((float) (getWidth() - 2)) / board.getWidth());
    }

    protected final float dY() {
        return (((float) (getHeight() - 2)) / board.getHeight());
    }

    protected final int xPixel(int x) {
        return (Math.round(1 + (x * dX())));
    }

    protected final int yPixel(int y) {
        return (Math.round(getHeight() - 1 - (y + 1) * dY()));
    }

    public void paintComponent(Graphics g) {
        // Ve hinh vuong
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Ve duong ngan voi tren
        int spacerY = yPixel(board.getHeight() - TOP_SPACE - 1);
        g.drawLine(0, spacerY, getWidth() - 1, spacerY);

        Rectangle clip = null;
        if (DRAW_OPTIMIZE) {
            clip = g.getClipBounds();
        }

        final int dx = Math.round(dX() - 2);
        final int dy = Math.round(dY() - 2);
        final int bWidth = board.getWidth();
        // final int bHeight = board.getHeight();

        int x, y;
        for (x = 0; x < bWidth; x++) {
            int left = xPixel(x);

            int right = xPixel(x + 1) - 1;

            if (DRAW_OPTIMIZE && clip != null) {
                if ((right < clip.x) || (left >= (clip.x + clip.width))) {
                    continue;
                }
            }

            final int yHeight = board.getColumnHeight(x);
            for (y = 0; y < yHeight; y++) {
                if (board.getGrid(x, y)) {
                    final boolean filled = (board.getRowWidth(y) == bWidth);
                    if (filled) {
                        g.setColor(Color.white);
                    }

                    g.fillRect(left + 1, yPixel(y) + 1, dx, dy); // +1 khi ra
                    if (filled) {
                        g.setColor(getForeground());
                    }
                }
            }
        }

        if (currentPiece != null && suggest) {
            y = currentY;
            int[] skirt = currentPiece.getSkirt();
            while (y >= 0) {
                boolean flag = true;
                for (int i = 0; i < skirt.length; i++) {
                    if (board.getGrid(currentX + i, y - 1 + skirt[i])) {
                        flag = false;
                    }
                }
                if (flag) {
                    y--;
                } else {
                    break;
                }
            }

            if (y != currentY) {
                TPoint[] body = currentPiece.getBody();
                for (int i = 0; i < body.length; i++) {
                    g.drawRect(xPixel(currentX + body[i].x) + 1, yPixel(y + body[i].y) + 2, dx - 1, dy - 1);
                }
            }
        }
    }

    /**
     * Cap nhat thoi gian hien tai.
     */
    public void updateTimer() {
        double value = ((double) speed.getValue()) / speed.getMaximum();
        timer.setDelay((int) (DELAY - value * DELAY));
    }

    /**
     *
     */
    public Container createControlPanel() throws FileNotFoundException {
        Container panel = Box.createVerticalBox();

        // Dem
        countLabel = new JLabel("Piece : 0");
        panel.add(countLabel);

        // Diem
        scoreLabel = new JLabel("Score : 0");
        panel.add(scoreLabel);

        // Thoi gian
        timeLabel = new JLabel("0");
        panel.add(timeLabel);

        panel.add(Box.createVerticalStrut(12));

        // nut START
        startButton = new JButton("Start");
        panel.add(startButton);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        //add new Component by Fizz
        panel.add(new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                // TODO Auto-generated method stub
                super.paintComponent(g);
                repaint(0, 0, 200, 200);
                for (int iter = 0; iter < 4; iter++) {
                    if (predict == -1) {
                        break;
                    }
                    int x = (pieces[predict].computeNextRotation().getBody())[iter].x;
                    int y = (pieces[predict].computeNextRotation().getBody())[iter].y;
                    g.fillRect(55 + y * 14 + y * 2, 15 + x * 14 + x * 2, 14, 14);
                }
            }
        });
        // Nut dung
        stopButton = new JButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopGame();
            }
        });

        // nut Pause
        pauseButton = new JButton("Pause");
        panel.add(pauseButton);
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseGame();
            }
        });
        // nut Music

        enableButtons();

        JPanel row = new JPanel();

        // Tang toc slider
        //panel.add(Box.createVerticalStrut(12));
        row.add(new JLabel("Speed:"));
        speed = new JSlider(0, 200, 75); // min, max, current
        speed.setPreferredSize(new Dimension(100, 15));

        updateTimer();
        row.add(speed);

        panel.add(row);
        speed.addChangeListener(new ChangeListener() {
            // khi slide thay doi, dong bo thoi gian voi gia tri cua no
            public void stateChanged(ChangeEvent e) {
                updateTimer();
            }
        });
        highLabel = new JLabel();
        highLabel = new JLabel("HighScore :" + sco.high());
        panel.add(highLabel);

        testButton = new JCheckBox("Suggest Piece");
        testButton.setSelected(true);
        panel.add(testButton);
        testButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (testButton.isSelected()) {
                    suggest = true;
                } else {
                    suggest = false;
                }
            }
        });

        musicButton = new JCheckBox("Music");
        musicButton.setSelected(true);
        panel.add(musicButton);
        musicButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (musicButton.isSelected()) {
                    music = true;
                } else {
                    music = false;
                }
            }
        });

        panel.add(Box.createVerticalStrut(12));
        JButton quit = new JButton("Quit");
        panel.add(quit);
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return (panel);
    }

    /*
     * Ham khoi tao chinh
     */
    public void mainPro(JTetris tetris) throws FileNotFoundException {
        JComponent container = (JComponent) frameGame.getContentPane();
        container.setLayout(new BorderLayout());
        container.add(tetris, BorderLayout.CENTER);
        Container panel = tetris.createControlPanel();
        container.add(panel, BorderLayout.EAST);
        frameGame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frameGame.pack();
        frameGame.setLocationRelativeTo(null);
        frameGame.setVisible(true);

    }
}
