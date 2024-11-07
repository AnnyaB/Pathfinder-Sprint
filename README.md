## Pathfinder's Sprint

Welcome to the Pathfinder's Sprint, a simple grid-based game where the player navigates from a starting point to a goal while avoiding obstacles. The game demonstrates basic pathfinding algorithms, smooth player movement animations, and running player animations.

## Features
Pathfinding: The player uses the shortest path from their current position to the goal. Pathfinding is computed using Dijkstra's algorithm.

Player Movement: The player moves smoothly across the grid using keyboard arrow keys. The movement is animated for a more engaging experience.

Running Animation: The player's running animation is displayed as they move. The animation alternates the position of the player's legs for a simple, running effect.

Obstacles: The grid contains obstacles that the player must avoid. These obstacles are represented as red squares on the grid.

Goal: The goal is represented as a green square. The player wins the game by reaching the goal.

## Requirements
Java Development Kit (JDK) version 8 or higher.
IDE: You can use any IDE such as IntelliJ IDEA, Eclipse, or NetBeans to run the project.
Installation
Clone or download the project repository to your local machine.

## Controls
Arrow Keys: Use the up, down, left, and right arrow keys to move the player around the grid.
Objective: Reach the goal (green square) while avoiding obstacles (red squares). The player wins when they reach the goal.
How the Game Works
Grid: The game consists of a 20x15 grid. Each cell is 32x32 pixels.
Obstacles: Obstacles are placed randomly or manually at specified positions (you can modify them in the code).
Pathfinding: Dijkstra's algorithm calculates the shortest path from the player's position to the goal. The path is drawn as a light orange overlay over the grid.
Animation: When the player moves, their movement is animated. The player's running legs alternate for a basic running animation. The movement is smooth with a slight delay between each frame to create the illusion of animation.
Winning the Game
The player wins by reaching the goal. Once the player reaches the goal, a message will appear on the screen saying:

"Congratulations! You've reached home!"

## Customization

You can modify several aspects of the game if you are ready to test your skills:
Obstacles: Add or remove obstacles in the grid array inside the PathfindingGame class.
Player Speed: Adjust the ANIMATION_SPEED constant to make the player move faster or slower.
Animation: The running animation can be customized by adjusting the currentFrame logic in the drawPlayer() method.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
