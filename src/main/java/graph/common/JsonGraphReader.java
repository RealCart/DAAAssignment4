package graph.common;

import java.nio.file.Files;
import java.nio.file.Path;

public class JsonGraphReader {
    public static class Dataset{
        public final String name; public final Graph graph; public final int source; public final String weightModel;
        public Dataset(String name,Graph g,int source,String wm){this.name=name;this.graph=g;this.source=source;this.weightModel=wm;}
    }
    public static Dataset read(Path file){
        try{
            String s = Files.readString(file);
            int n = parseInt(s, "\"n\"\\s*:\\s*(\\d+)");
            int source = parseInt(s, "\"source\"\\s*:\\s*(\\d+)");
            String wm = parseString(s, "\"weight_model\"\\s*:\\s*\"(edge)\"");
            Graph g = new Graph(n);
            String edges = extractArray(s, "\"edges\"\\s*:");
            if (!edges.isEmpty()){
                String[] parts = edges.split("\\},\\s*\\{");
                for (String p: parts){
                    String e = p.trim();
                    if (e.startsWith("{")) e=e.substring(1);
                    if (e.endsWith("}")) e=e.substring(0,e.length()-1);
                    int u = parseInt(e, "\"u\"\\s*:\\s*(\\d+)");
                    int v = parseInt(e, "\"v\"\\s*:\\s*(\\d+)");
                    int wv = parseInt(e, "\"w\"\\s*:\\s*(\\d+)");
                    g.addEdge(u,v,wv);
                }
            }
            return new Dataset(file.getFileName().toString(), g, source, wm);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    static int parseInt(String s,String regex){
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(s);
        if (!m.find()) throw new IllegalArgumentException("missing int for regex: "+regex);
        return Integer.parseInt(m.group(1));
    }
    static String parseString(String s,String regex){
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(regex).matcher(s);
        if (!m.find()) throw new IllegalArgumentException("missing string for regex: "+regex);
        return m.group(1);
    }
    static String extractArray(String s,String prefixRegex){
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(prefixRegex).matcher(s);
        if (!m.find()) return "";
        int start = s.indexOf('[', m.end());
        int depth=0; int i=start; for(;i<s.length();i++){char c=s.charAt(i); if(c=='[') depth++; else if(c==']'){depth--; if(depth==0){i++;break;}}}
        if (start<0||i<=start) return "";
        String arr = s.substring(start+1, i-1).trim();
        return arr;
    }
}