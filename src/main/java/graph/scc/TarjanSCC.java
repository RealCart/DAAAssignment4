package graph.scc;

import graph.common.Graph;

import java.util.*;

public class TarjanSCC {
    public static class Result{
        public final List<List<Integer>> components; public final int[] compIndex; public final long dfsVisits; public final long dfsEdges;
        public Result(List<List<Integer>> c,int[] ci,long v,long e){this.components=c;this.compIndex=ci;this.dfsVisits=v;this.dfsEdges=e;}
    }
    public static Result compute(Graph g){
        int n=g.n; int[] idx=new int[n]; Arrays.fill(idx,-1); int[] low=new int[n]; boolean[] on=new boolean[n]; Deque<Integer> st=new ArrayDeque<>(); List<List<Integer>> comps=new ArrayList<>(); int[] time={0}; long[] visits={0}; long[] edges={0};
        for(int v=0;v<n;v++) if(idx[v]==-1) dfs(v,g,idx,low,on,st,comps,time,visits,edges);
        int[] compIndex=new int[n]; for(int i=0;i<comps.size();i++) for(int v: comps.get(i)) compIndex[v]=i;
        return new Result(comps,compIndex,visits[0],edges[0]);
    }
    static void dfs(int v, Graph g,int[] idx,int[] low,boolean[] on,Deque<Integer> st,List<List<Integer>> comps,int[] time,long[] visits,long[] edges){
        idx[v]=low[v]=time[0]++; visits[0]++;
        st.push(v); on[v]=true;
        for(Graph.Edge e: g.adj.get(v)){
            edges[0]++;
            int to=e.to;
            if(idx[to]==-1){ dfs(to,g,idx,low,on,st,comps,time,visits,edges); low[v]=Math.min(low[v],low[to]); }
            else if(on[to]) low[v]=Math.min(low[v],idx[to]);
        }
        if(low[v]==idx[v]){
            List<Integer> comp=new ArrayList<>();
            while(true){int x=st.pop(); on[x]=false; comp.add(x); if(x==v) break;}
            comps.add(comp);
        }
    }
}