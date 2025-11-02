package graph.common;

import java.util.ArrayList;
import java.util.List;

public class WeightedDAG {
    public final int n;
    public final List<List<Edge>> adj;
    public int m;
    public WeightedDAG(int n){
        this.n=n;
        this.adj=new ArrayList<>(n);
        for(int i=0;i<n;i++) adj.add(new ArrayList<>());
        this.m=0;
    }
    public void addEdge(int u,int v,int w){
        adj.get(u).add(new Edge(v,w));
        m++;
    }
    public static class Edge{
        public final int to; public final int w;
        public Edge(int to,int w){this.to=to;this.w=w;}
    }
}