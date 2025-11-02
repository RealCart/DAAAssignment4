package graph.topo;

import graph.common.WeightedDAG;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KahnTopoTest {
    @Test
    void basicOrder(){
        WeightedDAG d=new WeightedDAG(4);
        d.addEdge(0,1,1); d.addEdge(0,2,1); d.addEdge(1,3,1); d.addEdge(2,3,1);
        var order=KahnTopo.order(d,new KahnTopo.Metrics());
        assertTrue(order.indexOf(0)<order.indexOf(1));
        assertTrue(order.indexOf(0)<order.indexOf(2));
        assertTrue(order.indexOf(1)<order.indexOf(3));
        assertTrue(order.indexOf(2)<order.indexOf(3));
    }
}