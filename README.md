# Description 
This is a Karel the Robot program that does multiple tasks on all sizes of worlds.  

<img src="https://user-images.githubusercontent.com/42502239/96447966-61d99b00-121b-11eb-9607-905a00fc1e82.jpg" height = "" />

# Levels
* ### Level 1
    For the first level Karel is required to collect all the beepers that are already in the world and place the beepers 
    in the odd corners around the boarders as demonstrated in the picture below.  
    
    <img src="https://user-images.githubusercontent.com/42502239/96447969-62723180-121b-11eb-86cf-219ae6468eae.png" height = "280" />  
    

    
* ### Level 2
    In level 2 Karel has to place beepers in all even corners.  
    
    <img src="https://user-images.githubusercontent.com/42502239/96447959-61410480-121b-11eb-8742-4702287789e7.png" height = "300" />  
    

    
* ### Level 3
    For level 3 Karel has to divide the world into four equal segments(picture on the left) or two equal segments(picture on the right) 
    if one of the dimensions is less than 2.
    
    <img src="https://user-images.githubusercontent.com/42502239/96451371-255c6e00-1220-11eb-956b-2a2666662a10.png" height = "300"/>
    <img src="https://user-images.githubusercontent.com/42502239/96451375-25f50480-1220-11eb-8f34-21bc9f37e7b4.png" height = "300"/>  

# Solution
The code has 22 methods that can be divided into 5 types:
* ### Protection and Monitoring methods  
    Six of the basic Karel methods are overloaded to make that the instruction can be executed without causing
    any errors and to keep track of Karel's position, orientation and beepers usage.  
    To demonstrate how this layer works, let's look at the `move` method:  
    
    ```java
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
    ```  
    Before calling the super `move()` method, it checks if front is clear to make calling it won't cause an error. 
    Then it increments `moves`, which keeps track of how many times Karel has moved, and it updates `X` and `Y` 
    which keep track of Karel's position.
    
* ### Abstraction methods  
    This type of methods provide a layer of abstraction so that I don't have to worry about the Karel's orientation 
    which makes interacting with Karel a lot more easier. This makes the code highly reusable because these methods can be
    useful in solving almost any type of tasks.  
    These methods take `direction` as a parameter which can be one of four possibilities (NORTH, SOUTH, EAST, WEST) which are defined in the class, 
    and they check Karel's current orientation and figure out the needed actions to do the required instruction.  
    This type includes:  
    * `isClear(direction)`
    * `move(direction)` 
    * `turn(direction)`  
    For example lets say Karel is facing east, and I want it to go north. instead of having do this:  
    ```java
    turnLeft();
    move();
    ```
    I can just say:  
    ```java
    move(NORTH);
    ```
    and it figures out that Karel has to turn left then move on its own.  
    To demonstrate how they work, let's look at `turn(direction)`:
    ```java
        public void turn(int direction) {
            int dist = direction - facing;
            dist += 4;
            dist %= 4;
    
            if (dist == 1) turnLeft();
            else if (dist == 2) turnAround();
            else if (dist == 3) turnRight();
        }
    ```    
    First, it takes the circular distance (how many left turns is required) between the current direction and the desired direction, and depending on that 
    it decides the best way to do this turn instruction.  
    I also want to point out that `move(direction)` returns a boolean indicating if the move instruction was successful. 
    
* ### State methods  
    These give information about the corner Karel is currently on. These methods are especially helpful for our tasks, but
    they are highly reusable and can be useful in many types of tasks.
    This type includes:
    * `isEven()` Checks if the current corner is an even corner.
    * `isOdd()` Checks if the current corner is an odd corner.
    * `isBoarder()` Checks if the current corner is touching the edge of the world.
    * `isMiddleX()` Checks if Karel is in the middle of the world horizontally.
    * `isMiddleY()` Checks if Karel is in the middle of the world vertically.  

* ### Level-specific methods
    This type of methods is only useful for our tasks. For each level x we have two methods: `levelx()` which performs 
    its corresponding tasks and `levelxMove()` which acts as a wrapper for the `move()` method to check after each move
    if the Karel has to put a beeper or pick a beeper from the new corner instead of having to check after each move
    making the code a lot shorter.  
    Now let's explain what each of these methods does:
    * #### Level 1:
        * `level1()` At first, this method goes to the far east then to the far north to check the dimensions of the world.
        Then it starts going over all remaining corners in a zigzag motion and makes sure to put beepers on the odd boarder 
        corners and pick up any beepers else where. After it's back to the original corner, it goes around the boarders 
        to pick up the beepers it placed in the first step.
        * `level1Move(direction)` This calls `move(direction)` and if it was successful it checks the new corner, if it's
        odd and on the boarder, it places a beeper, if it's not, it checks if the corner has a beeper and picks it up.
    * #### Level 2:
        * `level2()` Just goes over all the corners in the map in two steps using `level2Move()`.
        *  `level2Move(direction, step)` After each move, in the first step this method checks if the new corner is even 
        and places a beeper. In the second step, it picks up any beepers it finds.
    * #### Level 3:
        * `level3()` For this level, I decided to go with a simpler pattern. At first, it goes to the starting point, turns to the north,
        then repeats the following pattern 4 times: go forward until Karel is in the middle of the map, turn left, go forward
        until Karel reaches the edge of the world, turn right, if needed go forward once, turn right again. Then go back 
        to the origin corner.  
        This insures that the world is divided into 2 or 4 chambers.
        * `level3Move(step)` This method calls the original `move()` and after each move it checks if the new corner is 
        in the middle of the world horizontally or vertically and places a beeper.

* ### Control methods  
    These control the flow of the code. They include:
    * `measure(target, level)` Which calls the target method and prints how many beepers it picked and placed and how many 
    moves and turn instructions this level took to finish.
    * `run()` This method initializes variables and calls `measure()` three times, once for each level.