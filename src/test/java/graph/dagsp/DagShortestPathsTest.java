package graph.dagsp;

import graph.common.WeightedDAG;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DagShortestPathsTest {
    @Test
    void shortest(){
        WeightedDAG d=new WeightedDAG(4);
        d.addEdge(0,1,2); d.addEdge(0,2,1); d.addEdge(1,3,2); d.addEdge(2,3,5);
        var order=List.of(0,1,2,3);
        var res=DagShortestPaths.shortest(d,0,order,new DagShortestPaths.Metrics());
        assertEquals(4,res.dist[3]);
        assertEquals(3,res.farthestNode);
        assertEquals(0,res.path.get(0));
        assertEquals(1,res.path.get(1));
        assertEquals(3,res.path.get(2));
    }
}