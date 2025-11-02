package graph.topo;

import graph.common.WeightedDAG;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class KahnTopo {
    public static class Metrics{ public long pushes; public long pops; }
    public static List<Integer> order(WeightedDAG dag, Metrics m){
        int n=dag.n; int[] indeg=new int[n];
        for(int u=0;u<n;u++) for(WeightedDAG.Edge e: dag.adj.get(u)) indeg[e.to]++;
        ArrayDeque<Integer> q=new ArrayDeque<>();
        for(int i=0;i<n;i++) if(indeg[i]==0){ q.add(i); if(m!=null)m.pushes++; }
        List<Integer> out=new ArrayList<>();
        while(!q.isEmpty()){
            int u=q.remove(); if(m!=null)m.pops++; out.add(u);
            for(WeightedDAG.Edge e: dag.adj.get(u)){
                indeg[e.to]--; if(indeg[e.to]==0){ q.add(e.to); if(m!=null)m.pushes++; }
            }
        }
        return out;
    }
}