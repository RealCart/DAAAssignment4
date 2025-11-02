package org.example;

import graph.common.Graph;
import graph.common.JsonGraphReader;
import graph.common.WeightedDAG;
import graph.dagsp.DagLongestPath;
import graph.dagsp.DagShortestPaths;
import graph.scc.Condensation;
import graph.scc.TarjanSCC;
import graph.topo.KahnTopo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static void main(String[] args) throws Exception {

        String selectedFile = "s1_dag.json";


        Map<String,String> cli = parseArgs(args);
        Path dataDir = Paths.get(cli.getOrDefault("--data","./data"));
        Path metricsPath = Paths.get(cli.getOrDefault("--metrics","./out/metrics.csv"));
        if (!Files.exists(dataDir)) throw new RuntimeException("data dir missing: "+dataDir);
        if (metricsPath.getParent()!=null) Files.createDirectories(metricsPath.getParent());

        List<Path> files = new ArrayList<>();
        if (selectedFile != null && !selectedFile.isEmpty()) {

            Path singleFile = dataDir.resolve(selectedFile);
            if (!Files.exists(singleFile)) {
                throw new RuntimeException("Файл не найден: " + singleFile);
            }
            files.add(singleFile);
        } else {

            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dataDir, "*.json")) {
                for (Path p: ds) files.add(p);
            }
            files.sort(Comparator.comparing(Path::toString));
        }

        try (BufferedWriter w = Files.newBufferedWriter(metricsPath)) {
            w.write("dataset,n,m,algorithm,time_ns,dfs_visits,dfs_edges,kahn_pushes,kahn_pops,relaxations,weight_model,source,critical_path_length,critical_path_nodes\n");
            for (Path p: files) runDataset(p,w);
        }
    }
    static Map<String,String> parseArgs(String[] a){
        Map<String,String> m=new HashMap<>();
        for(int i=0;i<a.length;i++){
            if (a[i].equals("--data")&&i+1<a.length) m.put("--data",a[++i]);
            else if (a[i].equals("--metrics")&&i+1<a.length) m.put("--metrics",a[++i]);
        }
        return m;
    }
    static void runDataset(Path file, BufferedWriter w) throws IOException {
        JsonGraphReader.Dataset ds = JsonGraphReader.read(file);
        Graph g = ds.graph;
        long t0 = System.nanoTime();
        TarjanSCC.Result scc = TarjanSCC.compute(g);
        WeightedDAG dag = Condensation.build(g,scc);
        long t1 = System.nanoTime();
        long sccTime = t1 - t0;
        System.out.println("["+ds.name+"] SCC: count="+scc.components.size()+", sizes="+sizes(scc.components));
        writeRow(w,ds,g,"SCC",sccTime,scc.dfsVisits,scc.dfsEdges,"NA","NA","NA",ds.weightModel,ds.source,"NA","NA");
        long t2 = System.nanoTime();
        KahnTopo.Metrics km = new KahnTopo.Metrics();
        List<Integer> topo = KahnTopo.order(dag,km);
        long t3 = System.nanoTime();
        long topoTime = t3 - t2;
        System.out.println("["+ds.name+"] Topo: "+topo);
        writeRow(w,ds,g,"TOPO",topoTime,"NA","NA",String.valueOf(km.pushes),String.valueOf(km.pops),"NA",ds.weightModel,ds.source,"NA","NA");
        long t4 = System.nanoTime();
        int srcComp = scc.compIndex[ds.source];
        DagShortestPaths.Metrics sm = new DagShortestPaths.Metrics();
        DagShortestPaths.Result sp = DagShortestPaths.shortest(dag,srcComp,topo,sm);
        long t5 = System.nanoTime();
        long spTime = t5 - t4;
        System.out.println("["+ds.name+"] SSSP: sourceComp=c"+srcComp+", farthest=c"+sp.farthestNode+", dist="+sp.farthestDist+", path="+fmtPath(sp.path));
        writeRow(w,ds,g,"DAG_SSSP",spTime,"NA","NA","NA","NA",String.valueOf(sm.relaxations),ds.weightModel,ds.source,"NA","NA");
        long t6 = System.nanoTime();
        DagLongestPath.Result lp = DagLongestPath.longest(dag,topo);
        long t7 = System.nanoTime();
        long lpTime = t7 - t6;
        System.out.println("["+ds.name+"] Critical: length="+lp.length+", path="+fmtPath(lp.path));
        writeRow(w,ds,g,"DAG_LONGEST",lpTime,"NA","NA","NA","NA","NA",ds.weightModel,ds.source,String.valueOf(lp.length),fmtPath(lp.path));
    }
    static String sizes(List<List<Integer>> comps){
        List<Integer> s=new ArrayList<>();
        for(List<Integer> c: comps) s.add(c.size());
        return s.toString();
    }
    static String fmtPath(List<Integer> p){
        if (p==null||p.isEmpty()) return "[]";
        StringBuilder b=new StringBuilder("[");
        for (int i=0;i<p.size();i++){ if(i>0)b.append("->"); b.append("c").append(p.get(i)); }
        b.append("]");
        return b.toString();
    }
    static void writeRow(BufferedWriter w, JsonGraphReader.Dataset ds, Graph g, String alg, long time,
                         Object dfsV,Object dfsE,Object pushes,Object pops,Object relax,
                         String weightModel,int source,String critLen,String critNodes) throws IOException {
        String row = String.join(",",
                ds.name,
                String.valueOf(g.n),
                String.valueOf(g.m),
                alg,
                String.valueOf(time),
                String.valueOf(dfsV),
                String.valueOf(dfsE),
                String.valueOf(pushes),
                String.valueOf(pops),
                String.valueOf(relax),
                weightModel,
                String.valueOf(source),
                critLen,
                critNodes
        );
        w.write(row); w.write("\n"); w.flush();
    }
}
