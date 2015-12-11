package Tetris.Goc;
// JBrainTetris

import java.awt.*;
import java.io.FileNotFoundException;

/**
 * Lop con cuar JTetris. JTetris.main() co the duoc khoi tao voi JTetris hoac
 * JBrainTetris.
 */
public class JBrainTetris extends JTetris {

    public JBrainTetris(int width, int height) throws FileNotFoundException {
        super(width, height);
        isSwap = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int y;
        final int dx = Math.round(dX() - 2);
        final int dy = Math.round(dY() - 2);
        if (currentPiece != null) {
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

    @Override
    public void tick(int verb) {
        super.tick(verb);
        System.out.println(died);
    }
}
