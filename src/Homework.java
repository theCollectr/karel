import stanford.karel.SuperKarel;

import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;

/**
 * Level 1: Put beepers only in the odd outside spots (e.g. 1x2, 2x1 are considered odd), then print the number how many you've put, and then
 * collect them all (do not duplicate beepers)
 * Level 2: Put beepers on all even spots, then print the number how many you've put, and finally collect them all
 * Level 3: Divide the map (using beepers) into 2 or 4 equal chambers (rectangles surrounded by walls or beepers),
 * depending on the map; see solution in 7x7; please note that you cannot put duplicate beepers.
 * Make sure to clean the map and print how many beepers you've put, then collect them all.
 */

public class Homework extends SuperKarel {
    private final int EAST = 0;
    private final int NORTH = 1;
    private final int WEST = 2;
    private final int SOUTH = 3;
    private int moves = 0, turns = 0;
    private int beepersPicked = 0, beepersPut = 0;
    private int facing;
    private int X, Y;
    private int height, width;

    public void run() {
        X = 1;
        Y = 1;
        height = width = -1;
        facing = EAST;
        setBeepersInBag(1000);

        measure((this::level1), 1);
        measure(this::level2, 2);
        measure(this::level3, 3);
        System.out.println("************************");
        turn(EAST);
    }

    public void measure(Callable<Boolean> target, int level) {
        int levelMoves = moves;
        int levelBeepers = beepersPut;
        int levelTurns = turns;
        int levelPickedBeepers = beepersPicked;

        try {
            target.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

        levelMoves = moves - levelMoves;
        levelBeepers = beepersPut - levelBeepers;
        levelTurns = turns - levelTurns;
        levelPickedBeepers = beepersPicked - levelPickedBeepers;

        System.out.printf("Level %d put down %d beepers, picked up %d beepers, %d move instructions and %d turn instructions%n",
                level, levelBeepers, levelPickedBeepers, levelMoves, levelTurns);
    }

    public Boolean level1() {
        // step one
        pickBeeper();
        while (isClear(EAST)) level1Move(EAST);
        width = X;

        while (isClear(NORTH)) level1Move(NORTH);
        height = Y;

        level1Move(WEST);

        boolean goingDown = true;
        while (X >= 1) {
            if (goingDown)
                while (Y > 2) level1Move(SOUTH);
            else
                while (Y < height) level1Move(NORTH);

            if (X == 1) break;

            level1Move(WEST);
            goingDown ^= true;
        }

        while (Y > 1) level1Move(SOUTH);

        //step two
        while (X < width) {
            move(EAST);
            pickBeeper();
        }

        while (Y < height) {
            move(NORTH);
            pickBeeper();
        }

        while (X > 1) {
            move(WEST);
            pickBeeper();
        }

        while (Y > 1) {
            move(SOUTH);
            pickBeeper();
        }
        return true;
    }

    public Boolean level2() {
        for (int step = 1; step <= 2; step++) {
            level2Move(NORTH, step);

            while (X <= width) {
                if (X % 2 == 0)
                    while (Y > 2) level2Move(SOUTH, step);
                else
                    while (Y < height) level2Move(NORTH, step);

                if (X == width) break;
                level2Move(EAST, step);
            }

            while (Y > 1) level2Move(SOUTH, step);

            while (X > 1) level2Move(WEST, step);
        }
        return true;
    }

    public Boolean level3() {
        for (int step = 1; step <= 2; step++) {
            while (!isMiddleX()) move(EAST);
            turn(NORTH);

            for (int pattern = 1; pattern <= 4; pattern++) {
                while (!(isMiddleX() && isMiddleY())) level3Move(step);
                turnLeft();
                while (frontIsClear()) level3Move(step);
                turnRight();
                if ((X == 1 || X == width) && height % 2 == 0 || (Y == 1 || Y == height) && width % 2 == 0)
                    level3Move(step);
                
                turnRight();
            }

            while (isClear(WEST)) move(WEST);
        }
        return true;
    }

    public void level1Move(int direction) {
        if (!move(direction)) return;
        if (isOdd() && isBoarder()) putBeeper();
        else pickBeeper();
    }

    public void level2Move(int direction, int step) {
        if (!move(direction)) return;
        if (step == 1 && isEven()) putBeeper();
        else pickBeeper();
    }

    public void level3Move(int step) {
        move();
        if (step == 1 && (width > 2 && isMiddleX() || height > 2 && isMiddleY())) putBeeper();
        else pickBeeper();
    }

    public boolean isEven() {
        return (X - Y) % 2 == 0;
    }

    public boolean isOdd() {
        return !isEven();
    }

    public boolean isBoarder() {
        return X == 1 || Y == 1 || X == width || Y == height;
    }

    public boolean isMiddleX() {
        int chamberWidth = (int) Math.ceil(width / 2.0) - 1;
        return X > chamberWidth && X <= width - chamberWidth;
    }

    public boolean isMiddleY() {
        int chamberHeight = (int) Math.ceil(height / 2.0) - 1;
        return Y > chamberHeight && Y <= height - chamberHeight;
    }

    public boolean move(int direction) {
        if (!isClear(direction)) return false;
        turn(direction);
        move();
        return true;
    }

    public void turn(int direction) {
        int dist = direction - facing;
        dist += 4;
        dist %= 4;

        if (dist == 1) turnLeft();
        else if (dist == 2) turnAround();
        else if (dist == 3) turnRight();
    }

    public boolean isClear(int direction) {
        int dist = direction - facing;
        dist += 4;
        dist %= 4;

        if (dist == 0) return frontIsClear();
        else if (dist == 1) return leftIsClear();
        else if (dist == 3) return rightIsClear();
        else {
            turnLeft();
            return leftIsClear();
        }
    }

    @Override
    public void move() {
        if (frontIsBlocked()) return;
        super.move();
        moves++;
        if (facing == NORTH) Y++;
        else if (facing == SOUTH) Y--;
        else if (facing == EAST) X++;
        else X--;
    }

    @Override
    public void turnRight() {
        super.turnRight();
        facing += 3;
        facing %= 4;
        turns++;
    }

    @Override
    public void turnAround() {
        super.turnAround();
        facing += 2;
        facing %= 4;
        turns += 2;
    }

    @Override
    public void turnLeft() {
        super.turnLeft();
        facing++;
        facing %= 4;
        turns++;
    }

    @Override
    public void pickBeeper() {
        if (noBeepersPresent()) return;
        super.pickBeeper();
        beepersPicked++;
    }

    @Override
    public void putBeeper() {
        if (noBeepersInBag() || beepersPresent()) return;
        super.putBeeper();
        beepersPut++;
    }
}