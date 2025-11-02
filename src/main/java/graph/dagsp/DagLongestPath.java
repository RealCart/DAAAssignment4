package graph.dagsp;

import graph.common.WeightedDAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DagLongestPath {
    public static class Result{ public final long length; public final List<Integer> path; public Result(long l,List<Integer> p){this.length=l;this.path=p;} }
    public static Result longest(WeightedDAG dag,List<Integer> topo){
        int n=dag.n; long NEG=Long.MIN_VALUE/4; long[] dp=new long[n]; Arrays.fill(dp,NEG); int[] par=new int[n]; Arrays.fill(par,-1);
        int[] indeg=new int[n]; for(int u=0;u<n;u++) for(WeightedDAG.Edge e: dag.adj.get(u)) indeg[e.to]++;
        for(int i=0;i<n;i++) if(indeg[i]==0) dp[i]=0;
        for(int u: topo){ if(dp[u]<=NEG) continue; for(WeightedDAG.Edge e: dag.adj.get(u)){ long nd=dp[u]+e.w; if(nd>dp[e.to]){ dp[e.to]=nd; par[e.to]=u; } } }
        long best=NEG; int end=-1; for(int i=0;i<n;i++) if(dp[i]>best){best=dp[i]; end=i;}
        List<Integer> path=new ArrayList<>(); if(end!=-1){ int x=end; while(x!=-1){ path.add(x); x=par[x]; } Collections.reverse(path); }
        return new Result(best,path);
    }
}