package graph.dagsp;

import graph.common.WeightedDAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DagShortestPaths {
    public static class Metrics{ public long relaxations; }
    public static class Result{ public final long[] dist; public final int[] parent; public final int farthestNode; public final long farthestDist; public final List<Integer> path; public Result(long[] d,int[] p,int f,long fd,List<Integer> path){this.dist=d;this.parent=p;this.farthestNode=f;this.farthestDist=fd;this.path=path;} }
    public static Result shortest(WeightedDAG dag,int src,List<Integer> topo, Metrics m){
        int n=dag.n; long INF=Long.MAX_VALUE/4; long[] dist=new long[n]; Arrays.fill(dist,INF); int[] par=new int[n]; Arrays.fill(par,-1); dist[src]=0;
        for(int u: topo){ if(dist[u]>=INF) continue; for(WeightedDAG.Edge e: dag.adj.get(u)){ long nd=dist[u]+e.w; if(nd<dist[e.to]){ dist[e.to]=nd; par[e.to]=u; if(m!=null)m.relaxations++; } } }
        int far=-1; long best=-1; for(int i=0;i<n;i++){ if(dist[i]<INF && dist[i]>best){best=dist[i]; far=i;} }
        List<Integer> path=new ArrayList<>(); if(far!=-1){ int x=far; while(x!=-1){ path.add(x); x=par[x]; } Collections.reverse(path); }
        return new Result(dist,par,far,best,path);
    }
}