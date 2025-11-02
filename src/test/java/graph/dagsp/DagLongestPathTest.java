package graph.dagsp;

import graph.common.WeightedDAG;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DagLongestPathTest {
    @Test
    void longest(){
        WeightedDAG d=new WeightedDAG(4);
        d.addEdge(0,1,2); d.addEdge(0,2,1); d.addEdge(2,3,5); d.addEdge(1,3,2);
        var order=List.of(0,1,2,3);
        var res=DagLongestPath.longest(d,order);
        assertEquals(6,res.length);
        assertEquals(List.of(0,2,3),res.path);
    }
}