/*============================================================*/
//      8
//    1 * 4    <-- encodings of various directions around a cell
//      2
//
//      +--+--+    +--+--+
//      |     |    |11 12|    11  12   a maze and its representation
//      +--+  +    +--+  +
//      |     |    |11 06|    11  06
//      +--+--+    +--+--+
//
//     16 16 16 16   initial maze contents returned by constructor
//     16 15 15 16
//     16 15 15 16
//     16 16 16 16
//
/*============================================================*/
import java.util.Random;

public class Maze {
   
    private int[][] m;   // maze representation
    private int rows;    // number of rows in the maze
    private int cols;    // number of columns in the maze
    private final static byte[] TWO = { 1, 2, 4, 8, 16};
    private final static byte[] DX  = { 0,+1, 0,-1};
    private final static byte[] DY  = {-1, 0,+1, 0};
    private boolean done;  // used in finding a single solution.
    private long   count;  // used in finding the number of solutions.
    private Random r;      // for generating random integers.
    //public static int createCounter=0;

    public int getRows() { return( rows ); }
    public int getCols() { return( cols ); }

    public Maze ( int nr, int nc, int seed ) {
        count=0;
        r = new Random( seed );
        rows = nr;  cols = nc;
        m = new int[nr+2][nc+2];
        //for each row and column
        for (int r=1; r<=nr; ++r )
            for (int c=1; c<=nc; ++c )
                m[r][c] = 15;
        for (int r=0; r<nr+2; ++r )
            m[r][0] = m[r][nc+1] = 16;
        for (int c=0; c<nc+2; ++c )
            m[0][c] = m[nr+1][c] = 16;
        Create( nr/2+1, nc/2+1, 0 );
        
    }

    // Wall in direction p?
    public boolean ok ( int x, int y, int p ) {
        return( (m[x][y] & TWO[p]) == TWO[p] );
    }

    private boolean downWall( int x, int y, int p ) {
        if (ok(x,y,p) && m[x+DX[p]][y+DY[p]] != 16) {
            m[x][y] ^= TWO[p];
            m[x+DX[p]][y+DY[p]] ^= TWO[p^2];
            return true;
        }
        return false;
    }
   
    private void knockDown( int count ) {
        // Caution: make sure there are at least count walls!
        for (int i=0; i<count; ++i) {
            int x = 1+r.nextInt(rows);
            int y = 1+r.nextInt(cols);
            if (!downWall( x, y, r.nextInt(4))) --i;
        }
    }
   
    private void Create ( int x, int y, int val ) {
        int[] perm = randPerm( 4 );
        m[x][y] ^= val; //my[x][y]=my[x][y]^val, ^ means bitwise XOR
        for (int i=0; i<4; ++i) {
            int p = perm[i];
            //if you encounter outer boundary (cell value=16), do nothing
            if (m[x+DX[p]][y+DY[p]] == 15) {
                m[x][y] ^= TWO[p];
                Create( x+DX[p], y+DY[p], TWO[p^2] );
                //createCounter++;
            }
        }
    }

    private int[] randPerm( int n ) {
        // This algorithm should look familiar!
        int[] perm = new int[n];
        for (int k=0; k<n; ++k) perm[k] = k;
        for (int k=n; k>0; --k) {
            int rand = r.nextInt(k);
            int t = perm[rand];  perm[rand] = perm[k-1];  perm[k-1] = t;
        }
        return( perm );
    }
   
    public String toString() {
        // FOR YOU TO FILL IN.  MUST FOLLOW CORRECT FORMAT.
        String s="";
        for (int i=1; i<=rows; i++) {
            for (int j=1; j<=cols; j++) {
                s=s+String.format("%2d ",m[i][j]);;
            }
            s=s+"\n";
        }
        return s;
    }
    
    //only for maze with one path
    public void solveMaze() {
        // FOR YOU TO CODE.
        //if there is a path
        findPath(1,1);
    }
    
    private boolean findPath(int fromRow, int fromCol) {
        //System.out.println(fromRow+"  "+fromCol+"  "+toRow+"  "+toCol);
        
        //reject case - we hit the outer wall or our path
        if (m[fromRow][fromCol]>=16) {
            return false;
        }
        
        //backtracking step
        m[fromRow][fromCol]+=16;
        
        //accept case - we found the exit
        if (fromRow==rows && fromCol==cols) {
            return true;
        }
        
        //right
        if (!ok(fromRow,fromCol,2)) {
            if (findPath(fromRow, fromCol+1)) {
                return true;
            }
        }
        
        //left
        if (!ok(fromRow,fromCol,0)) {
            if (findPath(fromRow, fromCol-1)) {
                return true;
            }
        }
        
        //down
        if (!ok(fromRow,fromCol,1)) {
            if (findPath(fromRow+1, fromCol)) {
                return true;
            }
        }
        
        //up
        if (!ok(fromRow,fromCol,3)) {
            if (findPath(fromRow-1, fromCol)) {
                return true;
            }
        }
        
        m[fromRow][fromCol]-=16;
        return false;
    }
    
    private long countPath(int fromRow, int fromCol) {

        //reject case - we hit a wall or our path
        if (m[fromRow][fromCol]>=16) {
            return 0;
        }
        
        //accept case - we found the exit
        if (fromRow==rows && fromCol==cols) {
            return 1;
        }
        
        //backtracking step
        m[fromRow][fromCol]+=16;
        int path=0;
        
        //right
        if (!ok(fromRow,fromCol,2)) {
            path+=countPath(fromRow, fromCol+1);
        }
        
        //left
        if (!ok(fromRow,fromCol,0)) {
            path+=countPath(fromRow, fromCol-1);
        }
        
        //down
        if (!ok(fromRow,fromCol,1)) {
            path+=countPath(fromRow+1, fromCol);
        }
        
        //up
        if (!ok(fromRow,fromCol,3)) {
            path+=countPath(fromRow-1, fromCol);
        }
        
        m[fromRow][fromCol]-=16;
        return path;
    }
    
    
    public long numSolutions() {
        // FOR YOU TO CODE.
        return countPath(1,1);
    }
    
    
    public static void main ( String[] args ) {
        //number of rows and columns in the maze
        int row = Integer.parseInt( args[0] );
        int col = Integer.parseInt( args[1] );
        Maze maz = new Maze( row, col, 9999 );
        //System.out.println(createCounter);
        PrintMaze pm=new PrintMaze();
        System.out.print( maz );
        pm.displayMaze(maz);
        System.out.println( "Solutions = "+maz.numSolutions() );
        System.out.println();
        System.out.println();
        
        maz.knockDown( (row+col)/4 );
        System.out.print( maz );
        pm.displayMaze(maz);
        System.out.println( "Solutions = "+maz.numSolutions() );
        maz = new Maze( row, col, 9999 );  // creates the same maze anew.
        maz.solveMaze();
        System.out.print( maz );
        pm.displayMaze(maz);
    }
}


class PrintMaze {
    
    private static void renderTop( boolean b, boolean s ) {
        System.out.print( s?" *":"  " );  System.out.print( b?" |":"  " );
    }
    
    private static void renderBot(boolean b) {System.out.print( b?"---+":"   +" );}
    
    public static void displayMaze( Maze m ) {
        renderBot( false );
        for (int c=1; c<=m.getCols(); ++c) renderBot( true );
        System.out.println();
        for (int r=1; r<=m.getRows(); ++r) {
            renderTop( true, false );
            for (int c=1; c<=m.getCols(); ++c) renderTop( m.ok(r,c,2), m.ok(r,c,4) );
            System.out.println();
            renderBot( false );
            for (int c=1; c<=m.getCols(); ++c) renderBot( m.ok(r,c,1) );
            System.out.println();
        }
    }
}
