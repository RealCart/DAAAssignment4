package graph.scc;

import graph.common.Graph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {
    @Test
    void cycleAndTail(){
        Graph g=new Graph(4);
        g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(2,0,1); g.addEdge(2,3,1);
        TarjanSCC.Result r=TarjanSCC.compute(g);
        assertEquals(2,r.components.size());
        List<Integer> sizes=new ArrayList<>(); for(var c:r.components) sizes.add(c.size()); Collections.sort(sizes);
        assertEquals(Arrays.asList(1,3),sizes);
    }
    @Test
    void pureDAG(){
        Graph g=new Graph(3);
        g.addEdge(0,1,1); g.addEdge(1,2,1);
        TarjanSCC.Result r=TarjanSCC.compute(g);
        assertEquals(3,r.components.size());
        for(var c:r.components) assertEquals(1,c.size());
    }
}