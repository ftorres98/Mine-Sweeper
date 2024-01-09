// Name: Fernando Torres
// USC NetID: torresfe
// CS 455 PA3
// Fall 2021


/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
  user can see about the minefield). Client can call getStatus(row, col) for any square.
  It actually has data about the whole current state of the game, including  
  the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
  It also has mutators related to actions the player could do (resetGameDisplay(), cycleGuess(), uncover()),
  and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms
  the Model for the game application, whereas GameBoardPanel is the View and Controller, in the MVC design pattern.
  It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from 
  outside this class via the getMineField accessor.  
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method 
   // getStatus(row, col)).
   
   // The following are the covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // The following are the uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------   

   /**
    Representation invariant:

    -- mineData is NEVER null
    -- visibleGame is NEVER null
    -- minesGuessed >= 0
    -- gameOver is either false or true

    */

   // <put instance variables here>
   private MineField mineData;
   private int[][] visibleGame;
   private boolean gameOver;
   private int minesGuessed;

   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the mines covered up, no mines guessed, and the game
      not over.
      @param mineField  the minefield to use for this VisibleField
    */
   public VisibleField(MineField mineField) {
      mineData = mineField;
      gameOver = false;
      minesGuessed = 0;
      visibleGame = new int[mineField.numRows()][mineField.numCols()];

      for(int i = 0; i < visibleGame.length; i++){
         for(int j = 0; j < visibleGame[0].length; j++){
            visibleGame[i][j] = COVERED;
         }
      }

   }
   
   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */     
   public void resetGameDisplay() {
      for(int i = 0; i < visibleGame.length; i++){
         for(int j = 0; j < visibleGame[0].length; j++){
            visibleGame[i][j] = COVERED;
         }
      }

      gameOver = false;
      minesGuessed = 0;
      
   }
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
   public MineField getMineField() {
      return mineData;
   }
   
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the beginning of the class
      for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      return visibleGame[row][col];
   }

   
   /**
      Returns the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
      or not.  Just gives the user an indication of how many more mines the user might want to guess.  This value can
      be negative, if they have guessed more than the number of mines in the minefield.     
      @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      return (mineData.numMines() - minesGuessed);

   }
 
   
   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
      changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
      changes it to COVERED again; call on an uncovered square has no effect.  
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {
      if(visibleGame[row][col] == COVERED){
         visibleGame[row][col] = MINE_GUESS;
         minesGuessed++;
      }
      else if(visibleGame[row][col] == MINE_GUESS){
         visibleGame[row][col] = QUESTION;
         minesGuessed--;
      }
      else if(visibleGame[row][col] == QUESTION){
         visibleGame[row][col] = COVERED;
      }
      
   }

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in 
      the neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form 
      (possibly along with parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {
      //checks if a mine was uncovered
      if(mineData.hasMine(row,col)){
         visibleGame[row][col] = EXPLODED_MINE;
         gameOver = true;
         incorrectGuess();
         minesLeft();
         return false;
      }
      //else uncovers
      uncoverR(row,col);
      //checks if player won
      gameWon();
      return true;
   }
 
   
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game over
    */
   public boolean isGameOver() {
      return gameOver;
   }
 
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      if(visibleGame[row][col] == COVERED | visibleGame[row][col] == MINE_GUESS | visibleGame[row][col] == QUESTION){
         return false;
      }
      return true;
   }
   
 
   // <put private methods here>

   /**
    * This performs the recursion needed to uncover the squares that don't have any mines adjacent to them.
    * Takes in the row and col and if it isn't uncovered then it uncovers and returns how many mines are adjacent.
    * If 0 mines are adjacent then it proceeds to open other squares that haven't been uncovered.
    * @param row of the square
    * @param col of the square
    */
   private void uncoverR(int row, int col){
      if(visibleGame[row][col] != COVERED & visibleGame[row][col] != MINE_GUESS & visibleGame[row][col] != QUESTION ){
         return;
      }
      else{
         visibleGame[row][col] = mineData.numAdjacentMines(row,col);
         if(visibleGame[row][col] == 0) {
            if (mineData.inRange((row + 1), col)) {
               uncoverR((row + 1), col);
            }
            if (mineData.inRange((row - 1), col)) {
               uncoverR((row - 1), col);
            }
            if (mineData.inRange(row, (col + 1))) {
               uncoverR(row, (col + 1));
            }
            if (mineData.inRange(row, (col - 1))) {
               uncoverR(row, (col - 1));
            }
            if (mineData.inRange((row - 1), (col - 1))) {
               uncoverR((row - 1), (col - 1));
            }
            if (mineData.inRange((row + 1), (col + 1))) {
               uncoverR((row + 1), (col + 1));
            }
            if (mineData.inRange((row - 1), (col + 1))) {
               uncoverR((row - 1), (col + 1));
            }
            if (mineData.inRange((row + 1), (col - 1))) {
               uncoverR((row + 1), (col - 1));
            }
         }
      }
   }

   /**
    * gameWon checks if the person playing has won in the method uncover by finding the number of squares that don't have mines and then
    * counting how many squares are uncovered.
    * If those two numbers match then gameOver is updated to true and makes visible the mines by making the squares
    * MINE_GUESS if they aren't already.
    */
   private void gameWon(){
      int noMines = ((mineData.numRows()*mineData.numCols()) - mineData.numMines());

      int count = 0;
      for(int i = 0; i < visibleGame.length; i++){
         for(int j = 0; j< visibleGame[0].length; j++){
            if(visibleGame[i][j] != COVERED & visibleGame[i][j] != MINE_GUESS & visibleGame[i][j] != QUESTION){
               count++;
            }
         }
      }

      if(noMines == count){
         gameOver = true;
         for(int i = 0; i < visibleGame.length; i++){
            for(int j = 0; j< visibleGame[0].length; j++){
               if(mineData.hasMine(i,j) & visibleGame[i][j] != MINE_GUESS){
                  visibleGame[i][j] = MINE_GUESS;
               }
            }
         }
      }

   }

   /**
    * incorrectGuess is executed when a player uncovers a mine in the method uncover. When this happens the method looks at the MINE_GUESS and
    * if that square didn't have a mine then it is updated to INCORRECT_GUESS.
    */
   private void incorrectGuess(){
      for(int i = 0; i < visibleGame.length; i++){
         for(int j = 0; j< visibleGame[0].length; j++){
            if(visibleGame[i][j] == MINE_GUESS){
               if(!mineData.hasMine(i,j)){
                  visibleGame[i][j] = INCORRECT_GUESS;
               }
            }
         }
      }
   }

   /**
    * minesLeft is executed when a player uncovers a mine in the method uncover. When this happens, the method looks at the
    * looks through the COVERED or QUESTION squares and if it has a mine, then that square is updated to MINE.
    */
   private void minesLeft(){
      for(int i = 0; i < visibleGame.length; i++){
         for(int j = 0; j< visibleGame[0].length; j++){
            if(visibleGame[i][j] == COVERED | visibleGame[i][j] == QUESTION){
               if(mineData.hasMine(i,j)){
                  visibleGame[i][j] = MINE;
               }
            }
         }
      }
   }
   
}
