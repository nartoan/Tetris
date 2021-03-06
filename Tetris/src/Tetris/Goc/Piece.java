/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tetris.Goc;

import java.util.*;

/**
 *
 * @author Duc
 */
public class Piece {
    // Starter code specs out a few basic things, leaving
    // the algorithms to be done.

    private TPoint[] body;
    private int[] skirt;
    private int width;
    private int height;
    private Piece next; // "next" rotation

    static private Piece[] pieces;	// singleton static array of first rotations

    /**
     * Defines a new piece given a TPoint[] array of its body. Makes its own
     * copy of the array and the TPoints inside it.
     *
     * @param points input by array of Tpoint
     */
    public Piece(TPoint[] points) {
        //YOUR CODE HERE
        int xMax = 0;
        int yMax = 0;
        body = new TPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            body[i] = new TPoint(points[i]);
            if (body[i].x > xMax) {
                xMax = body[i].x;
            }
            if (body[i].y > yMax) {
                yMax = body[i].y;
            }
        }
        // compute height and width
        width = xMax + 1;
        height = yMax + 1;
        // compute skirt
        skirt = new int[width];
        Arrays.fill(skirt, height - 1);
        for (TPoint point : body) {
            if (skirt[point.x] > point.y) {
                skirt[point.x] = point.y;
            }
        }
    }

    /**
     * Alternate constructor, takes a String with the x,y body points all
     * separated by spaces, such as "0 0 1 0 2 0	1 1". (provided)
     */
    public Piece(String points) {
        this(parsePoints(points));
    }

    /**
     * Returns the width of the piece measured in blocks.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the piece measured in blocks.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns a pointer to the piece's body. The caller should not modify this
     * array.
     */
    public TPoint[] getBody() {
        return body;
    }

    /**
     * Returns a pointer to the piece's skirt. For each x value across the
     * piece, the skirt gives the lowest y value in the body. This is useful for
     * computing where the piece will land. The caller should not modify this
     * array.
     */
    public int[] getSkirt() {
        return skirt;
    }

    /**
     * Returns a new piece that is 90 degrees counter-clockwise rotated from the
     * receiver.
     */
    public Piece computeNextRotation() {
        // YOUR CODE HERE
        //calculate bodyOfNewPiece TPoint array of the new piece
        TPoint[] rotatedBody = new TPoint[body.length];

        for (int i = 0; i < body.length; i++) {
            rotatedBody[i] = new TPoint(height - body[i].y - 1, body[i].x);
        }

        // create a new Piece with bodyOfNewPiece
        return new Piece(rotatedBody); // YOUR CODE HERE
    }

    /**
     * Returns a pre-computed piece that is 90 degrees counter-clockwise rotated
     * from the receiver.	Fast because the piece is pre-computed. This only
     * works on pieces set up by makeFastRotations(), and otherwise just returns
     * null.
     */
    public Piece fastRotation() {
        return next;
    }

    /**
     * Returns true if two pieces are the same -- their bodies contain the same
     * points. Interestingly, this is not the same as having exactly the same
     * body arrays, since the points may not be in the same order in the bodies.
     * Used internally to detect if two rotations are effectively the same.
     *
     * @return true although different order
     */
    @Override
    public boolean equals(Object obj) {
        // standard equals() technique 1
        if (obj == this) {
            return true;
        }

        // standard equals() technique 2
        // (null will be false)
        if (!(obj instanceof Piece)) {
            return false;
        }
        Piece other = (Piece) obj;

        // YOUR CODE HERE
        TPoint[] compare = other.body;
        if (compare.length != body.length) {
            return false;
        }
        for (int first = 0; first < body.length; first++) {
            boolean temp = false;
            for (int second = first; second < body.length; second++) {
                if (body[first].equals(compare[second])) {
                    temp = true;
                    // swap in compare
                    TPoint tmp = compare[second];
                    compare[second] = compare[first];
                    compare[first] = tmp;
                    break;
                }
            }
            if (!temp) {
                return false;
            }
        }

        return true;
    }

    // String constants for the standard 7 tetris pieces
    public static final String STICK_STR = "0 0	0 1	 0 2  0 3";
    public static final String L1_STR = "0 0	0 1	 0 2  1 0";
    public static final String L2_STR = "0 0	1 0 1 1	 1 2";
    public static final String S1_STR = "0 0	1 0	 1 1  2 1";
    public static final String S2_STR = "0 1	1 1  1 0  2 0";
    public static final String SQUARE_STR = "0 0  0 1  1 0  1 1";
    public static final String PYRAMID_STR = "0 0  1 0  1 1  2 0";

    // Indexes for the standard 7 pieces in the pieces array
    public static final int STICK = 0;
    public static final int L1 = 1;
    public static final int L2 = 2;
    public static final int S1 = 3;
    public static final int S2 = 4;
    public static final int SQUARE = 5;
    public static final int PYRAMID = 6;

    /**
     * Returns an array containing the first rotation of each of the 7 standard
     * tetris pieces in the order STICK, L1, L2, S1, S2, SQUARE, PYRAMID. The
     * next (counterclockwise) rotation can be obtained from each piece with the
     * {@link #fastRotation()} message. In this way, the client can iterate
     * through all the rotations until eventually getting back to the first
     * rotation. (provided code)
     */
    public static Piece[] getPieces() {
        // lazy evaluation -- create static array if needed
        if (Piece.pieces == null) {
            // use makeFastRotations() to compute all the rotations for each piece
            Piece.pieces = new Piece[]{
                makeFastRotations(new Piece(STICK_STR)),
                makeFastRotations(new Piece(L1_STR)),
                makeFastRotations(new Piece(L2_STR)),
                makeFastRotations(new Piece(S1_STR)),
                makeFastRotations(new Piece(S2_STR)),
                makeFastRotations(new Piece(SQUARE_STR)),
                makeFastRotations(new Piece(PYRAMID_STR)),};
        }

        return Piece.pieces;
    }

    /**
     * Given the "first" root rotation of a piece, computes all the other
     * rotations and links them all together in a circular list. The list loops
     * back to the root as soon as possible. Returns the root piece.
     * fastRotation() relies on the pointer structure setup here.
     */
    /*
     Implementation: uses computeNextRotation()
     and Piece.equals() to detect when the rotations have gotten us back
     to the first piece.
     */
    private static Piece makeFastRotations(Piece root) {
        // YOUR CODE HERE
        if (root == null) {
            return null; // YOUR CODE HERE
        }
        Piece curr = root;
        Piece temp = curr.computeNextRotation();
        while (!temp.equals(root)) {
            curr.next = temp;
            curr = curr.next;
            temp = curr.computeNextRotation();
        }
        curr.next = root;
        return root;
    }

    /**
     * Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses the points into a
     * TPoint[] array. (Provided code)
     */
    private static TPoint[] parsePoints(String string) {
        List<TPoint> points = new ArrayList<TPoint>();
        StringTokenizer tok = new StringTokenizer(string);
        try {
            while (tok.hasMoreTokens()) {
                int x = Integer.parseInt(tok.nextToken());
                int y = Integer.parseInt(tok.nextToken());

                points.add(new TPoint(x, y));
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse x,y string:" + string);
        }

        // Make an array out of the collection
        TPoint[] array = points.toArray(new TPoint[0]);
        return array;
    }
}
