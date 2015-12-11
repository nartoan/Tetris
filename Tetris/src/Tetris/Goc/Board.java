/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris.Goc;

import java.util.Arrays;

/**
 *
 * y tuong duoc lay tu https://github.com/harshpai/Tetris
 */
public class Board {
    // Some ivars are stubbed out for you:
    private int width;
    private int height;
    private boolean[][] grid;
    private boolean DEBUG = true;
    boolean committed;
    // YOUR CODE HERE
    private int[] heights; // do cao cac cot;
    private int[] widths; // do rong cac hang
    private int maxHeight; // do cao cot cao nhat
    // for backup
    private boolean[][] gridBackUp;
    private int[] widthsBackUp;
    private int[] heightsBackUp;

    // Here a few trivial methods are provided:
    /**
     * Creates an empty board of the given width and height measured in blocks.
     *
     * @param width
     * @param height
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new boolean[width][height];
        committed = true;

        // YOUR CODE HERE
        heights = new int[width]; // do cao moi cot
        widths = new int[height]; // do rong moi dong
        // backup
        gridBackUp = new boolean[width][height];
        heightsBackUp = new int[width];
        widthsBackUp = new int[height];
    }

    /**
     * Returns the width of the board in blocks.
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the board in blocks.
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the max column height present in the board. For an empty board
     * this is 0.
     */
    public int getMaxHeight() {
        return maxHeight; // YOUR CODE HERE
    }

    // tinh chinh xac maxHeight sau khi place,clearRow va undo
    private void recomputeMaxheight() {
        maxHeight = 0;
        for (int i = 0; i < heights.length; i++) {
            if (maxHeight < heights[i]) {
                maxHeight = heights[i];
            }
        }
    }

    /**
     * Checks the board for internal consistency -- used for debugging.
     */
    public void sanityCheck() {
        if (DEBUG) {
            // YOUR CODE HERE
            System.out.print(this);
            int[] widthsCheck = new int[height];
            int maxHeightCheck = 0;
            for (int i = 0; i < width; i++) {
                int heightCheck = 0;
                for (int j = 0; j < height; j++) {
                    if (grid[i][j]) {
                        heightCheck = j + 1;
                        widthsCheck[j]++;

                        if (maxHeightCheck < j + 1) {
                            maxHeightCheck = j + 1;
                        }
                    }
                }
                if (heightCheck != heights[i]) {
                    throw new RuntimeException("Heights check failed");
                }
            }
            if (!Arrays.equals(widthsCheck, widths)) {
                throw new RuntimeException("Widths check failed");
            }

            if (maxHeightCheck != maxHeight) {
                throw new RuntimeException("Max Height check failed");
            }
        }
    }

    /**
     * Given a piece and an x, returns the y value where the piece would come to
     * rest if it were dropped straight down at that x.
     *
     * <p>
     * Implementation: use the skirt and the col heights to compute this fast --
     * O(skirt length).
     */
    public int dropHeight(Piece piece, int x) {
        int[] skirt = piece.getSkirt();
        int y = heights[x] - skirt[0];
        for (int i = 1; i < skirt.length; i++) {
            int tempY = heights[x + i] - skirt[i];
            if (tempY > y) {
                y = tempY;
            }
        }
        return y; // YOUR CODE HERE
    }

    /**
     * Returns the height of the given column -- i.e. the y value of the highest
     * block + 1. The height is 0 if the column contains no blocks.
     */
    public int getColumnHeight(int x) {
        return heights[x]; // YOUR CODE HERE
    }

    /**
     * Returns the number of filled blocks in the given row.
     */
    public int getRowWidth(int y) {
        return widths[y]; // YOUR CODE HERE
    }

    /**
     * Returns true if the given block is filled in the board. Blocks outside of
     * the valid width/height area always return true.
     */
    public boolean getGrid(int x, int y) {
        return (x < 0 || y < 0 || x >= width || y >= height || grid[x][y]);
    }

    public void setGrid(int x, int y, boolean bool) {
        if (!(x < 0 || y < 0 || x >= width || y >= height)) {
            if (grid[x][y]) {
                if (!bool) {
                    grid[x][y] = false;
                    widths[y]--;
                    int count = 0;
                    for (int row = maxHeight - 1; row >= 0; row--) {
                        if (grid[x][row]) {
                            heights[x] = row + 1;
                            break;
                        } else {
                            count++;
                        }
                    }
                    if (count == maxHeight) {
                        heights[x] = 0;
                    }
                    recomputeMaxheight();
                }
            } else {
                if (bool) {
                    grid[x][y] = true;
                    widths[y]++;
                    if (y + 1 > heights[x]) {
                        heights[x] = y + 1;
                    }
                    recomputeMaxheight();
                }
            }
        }
    }

    public static final int PLACE_OK = 0;
    public static final int PLACE_ROW_FILLED = 1;
    public static final int PLACE_OUT_BOUNDS = 2;
    public static final int PLACE_BAD = 3;

    /**
     * Attempts to add the body of a piece to the board. Copies the piece blocks
     * into the board grid. Returns PLACE_OK for a regular placement, or
     * PLACE_ROW_FILLED for a regular placement that causes at least one row to
     * be filled.
     *
     * <p>
     * Error cases: A placement may fail in two ways. First, if part of the
     * piece may falls out of bounds of the board, PLACE_OUT_BOUNDS is returned.
     * Or the placement may collide with existing blocks in the grid in which
     * case PLACE_BAD is returned. In both error cases, the board may be left in
     * an invalid state. The client can use undo(), to recover the valid,
     * pre-place state.
     */
    public int place(Piece piece, int x, int y) {
        // flag !committed problem
        if (!committed) {
            throw new RuntimeException("place commit problem");
        }
        backup();
        int result = PLACE_OK;
        // YOUR CODE HERE
        TPoint[] body = piece.getBody();
        for (TPoint point : body) {
            int placeX = x + point.x;
            int placeY = y + point.y;
            if (placeX < 0 || placeX >= width || placeY < 0 || placeY >= height) {
                result = PLACE_OUT_BOUNDS;
                break;
            } else if (grid[placeX][placeY]) {
                result = PLACE_BAD;
                break;
            } else {
                grid[placeX][placeY] = true;
                if (heights[placeX] < placeY + 1) {
                    heights[placeX] = placeY + 1;
                }
                widths[placeY]++;
                if (widths[placeY] == width) {
                    result = PLACE_ROW_FILLED;
                }
            }
        }
        recomputeMaxheight();
        sanityCheck();
        committed = false;
        return result;
    }

    /**
     * Deletes rows that are filled all the way across, moving things above
     * down. Returns the number of rows cleared.
     *
     * @return so dong dc xoa
     */
    public int clearRows() {
        if (committed) {
            committed = false;
            backup(); // backup neu ko trong trang thai commit
        }

        boolean hasFilledRow = false; // chi trang thai co dong day ko
        int rowTo, rowFrom, rowsCleared; // hang den, hang nguon, so hang da xoa
        rowsCleared = 0;

        // de toi uu ta se tim rowFrom phu hop de chuyen den rowTo
        for (rowTo = 0, rowFrom = 1; rowFrom < maxHeight; rowTo++, rowFrom++) {
            // ta se xu li tat ca cau lenh o duoi neu co 1 hang day
            if (!hasFilledRow && widths[rowTo] == width) {
                hasFilledRow = true;
                rowsCleared++;
            }
            // buoc de tim rowFrom phu hop vi se co the nhieu hang bi xoa
            while (hasFilledRow && rowFrom < maxHeight && widths[rowFrom] == width) {
                rowsCleared++;
                rowFrom++;
            }
            // viec thuc hien se o day
            if (hasFilledRow) {
                copySingleRow(rowTo, rowFrom);
            }

        }
		// sau khi rowFrom cham maxHeight cung la luc cac dong
        // tu rowTo tro len can duoc lam rong
        if (hasFilledRow) {
            fillEmptyRows(rowTo, maxHeight);
        }

		// Example
        // ++++++ <-- row cleared out
        // +++ ++ <-- gap in the 4th column makes updated height
        // +++ ++ <-- of the 4th column using case 1 invalid
        // + ++++
        // neu dung cach cap nhat cu 1 hang bi xoa thi tat ca heights deu
        // giam 1 se gap loi khi gap truong hop tren
        for (int i = 0; i < heights.length; i++) {
            heights[i] -= rowsCleared;
            // phan nay la xu ly loi co the xay ra
            if (heights[i] > 0 && !grid[i][heights[i] - 1]) {
                heights[i] = 0;
                for (int j = 0; j < maxHeight; j++) {
                    if (grid[i][j]) {
                        heights[i] = j + 1;
                    }
                }
            }
        }

        recomputeMaxheight();
        sanityCheck();
        committed = false;
        return rowsCleared;
    }

    /**
     * Reverts the board to its state before up to one place and one
     * clearRows(); If the conditions for undo() are not met, such as calling
     * undo() twice in a row, then the second undo() does nothing. See the
     * overview docs.
     */
    public void undo() {
        // YOUR CODE HERE
        if (!committed) {
            swap();
        }
        commit();
        sanityCheck();
    }

    /**
     * Puts the board in the committed state.
     */
    public void commit() {
        committed = true;
    }

    /*
     * Renders the board state as a big String, suitable for printing. This is
     * the sort of print-obj-state utility that can help see complex state
     * change over time. (provided debugging utility)
     */
    public String toString() {
        StringBuilder buff = new StringBuilder();
        for (int y = height - 1; y >= 0; y--) {
            buff.append('|');
            for (int x = 0; x < width; x++) {
                if (getGrid(x, y)) {
                    buff.append('+');
                } else {
                    buff.append(' ');
                }
            }
            buff.append("|\n");
        }
        for (int x = 0; x < width + 2; x++) {
            buff.append('-');
        }
        return (buff.toString());
    }

    private void backup() {
        System.arraycopy(widths, 0, widthsBackUp, 0, widths.length);
        System.arraycopy(heights, 0, heightsBackUp, 0, heights.length);
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, gridBackUp[i], 0, grid[i].length);
        }
    }

    // don gian la doi moi thu cho nhau
    private void swap() {

        int[] temp = widthsBackUp;
        widthsBackUp = widths;
        widths = temp;

        temp = heightsBackUp;
        heightsBackUp = heights;
        heights = temp;

        boolean[][] gridtemp = gridBackUp;
        gridBackUp = grid;
        grid = gridtemp;

        recomputeMaxheight();
    }

    // ham chuyen row nay den row kia
    private void copySingleRow(int rowTo, int rowFrom) {
        if (rowFrom < maxHeight) {
            for (int i = 0; i < width; i++) {
                grid[i][rowTo] = grid[i][rowFrom];
                widths[rowTo] = widths[rowFrom];
            }
        } else {
            for (int i = 0; i < width; i++) {
                grid[i][rowTo] = false;
                widths[rowTo] = 0;
            }
        }
    }

    // ham lam trong row
    private void fillEmptyRows(int rowTo, int maxHeight) {
        for (int j = rowTo; j < maxHeight; j++) {
            widths[j] = 0; // cap nhat bang widths
            for (int i = 0; i < width; i++) {
                grid[i][j] = false;
            }

        }
    }

    // ham dich chuyen board
    public void swapH() {
        for (int y = 0; y < maxHeight; y++) {
            boolean temp = grid[0][y];
            for (int x = 0; x < width - 1; x++) {
                setGrid(x, y, grid[x + 1][y]);
            }
            setGrid(width - 1, y, temp);
        }
        backup();
    }
}
