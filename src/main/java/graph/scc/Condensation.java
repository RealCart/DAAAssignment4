package graph.scc;

import graph.common.Graph;
import graph.common.WeightedDAG;

import java.util.HashMap;
import java.util.Map;

public class Condensation {
    public static WeightedDAG build(Graph g, TarjanSCC.Result r){
        int c = r.components.size();
        WeightedDAG dag = new WeightedDAG(c);
        Map<Long,Integer> w = new HashMap<>();
        for(int u=0;u<g.n;u++) for(Graph.Edge e: g.adj.get(u)){
            int cu=r.compIndex[u], cv=r.compIndex[e.to];
            if(cu==cv) continue;
            long key = (((long)cu)<<32) | (cv & 0xffffffffL);
            Integer cur=w.get(key);
            if(cur==null||e.w<cur) w.put(key,e.w);
        }
        for(Map.Entry<Long,Integer> en: w.entrySet()){
            int cu=(int)(en.getKey()>>32); int cv=(int)(en.getKey().longValue());
            dag.addEdge(cu,cv,en.getValue());
        }
        return dag;
    }
}