// Name: Fernando Torres
// USC NetID: torresfe
// CS 455 PA3
// Fall 2021

import java.util.Random;

/** 
   MineField
      class with locations of mines for a game.
      This class is mutable, because we sometimes need to change it once it's created.
      mutators: populateMineField, resetEmpty
      includes convenience method to tell the number of mines adjacent to a location.
 */
public class MineField {

   /**
    Representation invariant:

    -- mineGame is NEVER null
    -- 0 <= minesNum < (1/3 of total number of field locations)

    */
   
   // <put instance variables here>
   private boolean[][] mineGame;
   private int minesNum;
   
   
   
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in the array
      such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice versa.  numMines() for
      this minefield will corresponds to the number of 'true' values in mineData.
      @param mineData  the data for the mines; must have at least one row and one col,
                       and must be rectangular (i.e., every row is the same length)
    */
   public MineField(boolean[][] mineData) {
      minesNum = 0;
      mineGame = new boolean[mineData.length][mineData[0].length];
      for(int i = 0; i < mineData.length; i++ ){
         for(int j = 0; j < mineData[0].length; j++){
            if(mineData[i][j]){
               minesNum++;
            }
            mineGame[i][j] = mineData[i][j];
         }
      }
      
   }
   
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a MineField, 
      numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
   public MineField(int numRows, int numCols, int numMines) {
      mineGame = new boolean[numRows][numCols];
      minesNum = numMines;
   }
   

   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on the minefield,
      ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col) and numMines() < (1/3 * numRows() * numCols())
    */
   public void populateMineField(int row, int col) {
      resetEmpty();

      Random generator = new Random();

      int count = minesNum;
      while(count > 0){
         int rowRan = generator.nextInt(mineGame.length);
         int colRan = generator.nextInt(mineGame[0].length);
         if(rowRan != row | colRan != col){
            if(!mineGame[rowRan][colRan]){
               mineGame[rowRan][colRan] = true;
               count--;
            }
         }
      }
   }
   
   
   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or numCols()
      Thus, after this call, the actual number of mines in the minefield does not match numMines().  
      Note: This is the state a minefield created with the three-arg constructor is in 
         at the beginning of a game.
    */
   public void resetEmpty() {
      for(int i = 0; i < mineGame.length; i++ ){
         for(int j = 0; j < mineGame[0].length; j++){
            if(mineGame[i][j]){
               mineGame[i][j] = false;
            }
         }
      }
   }

   
  /**
     Returns the number of mines adjacent to the specified mine location (not counting a possible 
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
   public int numAdjacentMines(int row, int col) {
      int count = 0;

      //check top and bottom
      if(row>0){
         if(mineGame[row-1][col]){
            count++;
         }
      }
      if(row < mineGame.length-1){
         if(mineGame[row+1][col]){
            count++;
         }
      }

      //check left and right
      if(col>0){
         if(mineGame[row][col-1]){
            count++;
         }
      }
      if(col < mineGame[0].length-1){
         if(mineGame[row][col+1]){
            count++;
         }
      }

      //check diagonals (/)
      if((row>0) & (col < mineGame[0].length - 1)){
         if(mineGame[row-1][col+1]){
            count++;
         }
      }
      if((row<mineGame.length-1) & (col>0)){
         if(mineGame[row+1][col-1]){
            count++;
         }
      }

      //check diagonals (\)
      if((row>0) & (col>0)){
         if(mineGame[row-1][col-1]){
            count++;
         }
      }
      if((row<mineGame.length-1) & (col<mineGame[0].length-1)){
         if(mineGame[row+1][col+1]){
            count++;
         }
      }

      return count;
   }
   
   
   /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
   public boolean inRange(int row, int col) {
      if((row < 0) | (row >= mineGame.length)){
         return false;
      }
      if((col < 0) | (col >= mineGame[0].length)){
         return false;
      }
      return true;
   }
   
   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */  
   public int numRows() {
      return mineGame.length;
   }
   
   
   /**
      Returns the number of columns in the field.
      @return number of columns in the field
   */    
   public int numCols() {
      return mineGame[0].length;
   }
   
   
   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
   public boolean hasMine(int row, int col) {
      if(mineGame[row][col]){
         return true;
      }
      return false;
   }
   
   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg constructor,
      some of the time this value does not match the actual number of mines currently on the field.  See doc for that
      constructor, resetEmpty, and populateMineField for more details.
    * @return number of mines
    */
   public int numMines() {
      return minesNum;
   }

   
   // <put private methods here>
   
         
}

