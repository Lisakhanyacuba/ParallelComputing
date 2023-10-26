package MonteCarloMini;

/* M. Kuttel 2023
 * Searcher class that lands somewhere random on the surfaces and 
 * then moves downhill, stopping at the local minimum.
 */
import java.util.concurrent.ForkJoinPool;
import java.lang.Math;
import java.util.concurrent.RecursiveTask;
public class SearchParallel extends RecursiveTask<Integer>
{
	private int id;				// Searcher identifier
	private int pos_row, pos_col;		// Position in the grid
	private int steps; //number of steps to end of search
	private boolean stopped;			// Did the search hit a previous trail?
	
	public TerrainArea terrain;
	enum Direction {
		STAY_HERE,
	    LEFT,
	    RIGHT,
	    UP,
	    DOWN
	  }

	public SearchParallel(int id, int pos_row, int pos_col, TerrainArea terrain) {
		this.id = id;
		this.pos_row = pos_row; //randomly allocated
		this.pos_col = pos_col; //randomly allocated
		this.terrain = terrain;
		this.stopped = false;
	}
	
	public int find_valleys() {	
		int height=Integer.MAX_VALUE;
		Direction next = Direction.STAY_HERE;
		while(terrain.visited(pos_row, pos_col)==0) { // stop when hit existing path
			 height=terrain.get_height(pos_row, pos_col);
			terrain.mark_visited(pos_row, pos_col, id); //mark current position as visited
			steps++;
			next = terrain.next_step(pos_row, pos_col);
			switch(next) {
				case STAY_HERE: return height; //found local valley
				case LEFT: 
					pos_row--;
					break;
				case RIGHT:
					pos_row=pos_row+1;
					break;
				case UP: 
					pos_col=pos_col-1;
					break;
				case DOWN: 
					pos_col=pos_col+1;
					break;
			}
		}
		stopped=true;
		return height;
	}

	public int getID() {
		return id;
	}

	public int getPos_row() {
		return pos_row;
	}

	public int getPos_col() {
		return pos_col;
	}

	public int getSteps() {
		return steps;
	}
	public boolean isStopped() {
		return stopped;
	}

//public class SearchTask extends RecursiveTask<Integer>
//{
//===================================================================================================
 private final int ThreshHold = 10000 ;
 private int lo;
 private int hi;
 public SearchParallel[] search ;
 public int finder = -1;
 public SearchParallel(SearchParallel [] search,int lo ,int hi)
 {
 this.search = search ;
 this.lo = lo;
 this.hi = hi;
 }
 volatile int min = Integer.MAX_VALUE;
 volatile int local_min=Integer.MAX_VALUE;
  protected Integer compute()
 {
    if ((hi-lo)<ThreshHold)
    {
           for (int i = lo ; i<hi;i++)
     {
      local_min=search[i].find_valleys();
     		if((!search[i].isStopped())&&(local_min<min)) { //don't look at  those who stopped because hit exisiting path
     			min=local_min;
     			finder=i; //keep track of who found it
     	}

     }
 
     return min ;
     }
      else
    {
    
     SearchParallel t1 = new SearchParallel(search,lo,(lo+hi)/2);
     SearchParallel t2 = new SearchParallel(search,(lo+hi)/2,hi);
     t1.fork();
     int mint2 = t2.compute();
     int mint1 = t1.join();
     
       if (mint1 < mint2) {
                finder = t1.finder; // Update finder with the minimum finder from subtask t1
            } else {
                finder = t2.finder; // Update finder with the minimum finder from subtask t2
            }

            int min = Math.min(mint1, mint2);
            return min;

    }  
 }
  public int getFinder() {
        return finder;
    }
   
//==================================================================================================================
//}
}

