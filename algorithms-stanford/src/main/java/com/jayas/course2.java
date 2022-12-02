package com.jayas;







import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public class course2 {

    private static boolean[] visitedVertices = null;
    private static List<Boolean> visitedVerticesList = null;
    private static TreeMap<Integer,Integer> finishTimes = null;
    private static Map<Integer,Integer> revFinishTimes = null;
    private static Map<Integer, List<Set<Integer>>> leaders = new HashMap<>();
    private static List sccSizes = new LinkedList();

    private static  class Pair<T,V>  implements  Comparable {
        private T first;
        private V second;

        private BiFunction<Object,Object,Integer> hashFn;

        public Pair(T first, V second) {
            this.first =first;
            this.second = second;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return (Objects.equals(first, pair.first) && Objects.equals(second, pair.second)) || (Objects.equals(first, pair.second) && Objects.equals(second, pair.first));
        }

        @Override
        public int hashCode() {
            if(hashFn == null) {
                return Objects.hash(first, second);
            }
            return hashFn.apply(first,second);

        }

        public T getFirst(){
            return first;
        }

        public V getSecond() {
            return second;
        }

        public  static <X,Y> Pair of (X one, Y two) {
            return new Pair<>(one, two);
        }

        public  Pair<T,V> withHashFunction(BiFunction<Object,Object,Integer> hashFn) {
            this.hashFn = hashFn;
            return this;
        }

        @Override
        public String toString() {
            return "(" + first +
                    "," + second +
                    ")";
        }

        @Override
        public int compareTo(Object o) {
            return this.equals(o) ? 0 : 1;
        }
    }
    private static class Graph {
        @Override
        public String toString() {
            return "Graph{" +
                    "edges=" + edges +
                    '}';
        }

        private Map<Integer, List<Pair<Integer,Integer>>> edges = new LinkedHashMap<>();


        public Graph() {

        }

        public int numVertices() {
            return edges.size();
        }

        public void addOrUpdateVertex(int vertex, int outgoingEdge, int length) {

            List<Pair<Integer,Integer>> oe = new ArrayList<>();
            if (outgoingEdge >= 0) {
                oe.add(new Pair<>(outgoingEdge,length));
            }

            List<Pair<Integer,Integer>> currValues = edges.putIfAbsent(vertex, oe);
            if ((currValues != null) && (outgoingEdge >= 0)) {
                currValues.add( new Pair<>(outgoingEdge,length));
            }

        }
        private void addOrUpdateVertex(int vertex, int outgoingEdge) {
            addOrUpdateVertex(vertex,outgoingEdge,0);


        }

    }






    public static   Graph reverse(Graph in) {
        Graph retVal = new Graph();
        in.edges.entrySet().stream().forEach(entry ->  {
            retVal.addOrUpdateVertex(entry.getKey(), -1);
            List<Pair<Integer,Integer>> outGoingEdges = entry.getValue();
            if(outGoingEdges.size() > 0) {
                for (Pair<Integer,Integer> vertexWithLength : outGoingEdges) {
                    retVal.addOrUpdateVertex(vertexWithLength.first,entry.getKey(), vertexWithLength.second);

                }
            }
        });
        return retVal;
    }

    public static Graph updateVertices(Graph graph,  Map<Integer,Integer> keyMap) {
        Graph retVal = new Graph();

        graph.edges.forEach((key, val) -> {
           int vertex = keyMap.get(key);
           if(val.isEmpty()){
               retVal.addOrUpdateVertex(vertex,-1);
           } else {
               val.forEach(edge -> retVal.addOrUpdateVertex(vertex, keyMap.get(edge.first),edge.second));
           }
        });
        return retVal;
    }

    public static void DFS(Graph graph, Function<Integer,Integer> vIdxGen, List<Boolean> visitedVerticesList, boolean isFinishTimeIteration) {

        int n = graph.numVertices();
        AtomicInteger t = new AtomicInteger(0);
        for(int i = n; i >0;i--){
            final int index = i;
//            System.out.println("In Loop For Index  "+index);
            List<Set<Integer>> followersList = new LinkedList<>();

            leaders.putIfAbsent(i, followersList);
            DFS(graph,vIdxGen.apply(i),t,leaders.get(index),vIdxGen,visitedVerticesList,isFinishTimeIteration);
            followersList.forEach(followers -> {
                //System.out.println("# Followers for "+index+ " is "+followers.size());
                sccSizes.add(followers.size());
            });

        }
        //System.out.println("Max Finishing Times after  DFS : " +t.intValue());

    }

    public static void DFS(Graph graph , int vIdx,AtomicInteger t, List<Set<Integer>> followersList,Function<Integer,Integer> vIdxGen, List<Boolean> visitedVerticesList,boolean isFinishTimeIteration) {
//        boolean[] visitedVertices = new boolean[graph.numVertices()];
//        System.out.println("Attempting  "+vIdx);
        Stack<Integer> vertices = new Stack<>();
        Set<Integer> followers = new LinkedHashSet<>();
        followersList.add(followers);
        List<Integer> path = new LinkedList<>();
        int cnt =0;
        if(!visitedVerticesList.get(vIdx-1) ) {
            vertices.add(vIdx);
//            followers.add(vIdx);
//            System.out.println("Processing  "+vIdx+ ", followers: "+followers);
        }

        while(vertices.size() > 0){
            cnt++;
            int idx = vertices.pop();
//            System.out.println("Popped "+idx);
            followers.add(idx); //Adding self to followers
            path.add(idx);
//            System.out.println("Followers  for "+vIdx+" : "+followers);
//            System.out.println("Path  for "+vIdx+" : "+path);
            visitedVerticesList.set(idx-1,  true);
            List<Pair<Integer,Integer>> edges = graph.edges.get(idx);
            if((edges == null) || (edges.isEmpty())) {

                if (!finishTimes.containsKey(idx)) {
                    t.incrementAndGet();
//                    System.out.println("1. Setting finishing time for  "+idx+ " as "+t.intValue());
                    path.remove(Integer.valueOf(idx));
                    finishTimes.put(idx, t.intValue());
                    revFinishTimes.put(t.intValue(),idx);
                    for(int i = path.size()-1;i>=0;i--){
                        int idxToFinish = path.get(i);
                        if(!graph.edges.get(idxToFinish).stream().map(x -> x.first).filter(ix -> !visitedVerticesList.get(ix-1)).findFirst().isPresent()) {
                            if(!finishTimes.containsKey(idxToFinish)) {
                                t.incrementAndGet();
//                                System.out.println("1. Setting finishing time for  "+idxToFinish+ " as "+t.intValue());
                                finishTimes.put(idxToFinish, t.intValue());
                                revFinishTimes.put(t.intValue(),idxToFinish);
                            }
                        } else {
                            break;
                        }

                    }
                    path.removeIf( finishTimes::containsKey);
//                    System.out.println("1. Updated Path  for "+vIdx+" : "+path);
                }
                continue;
            }
            boolean hasEdgeToBeVisited = false;
            for (Pair<Integer,Integer> edge : edges) {
                if(!visitedVerticesList.get(edge.first-1) ) {
                    hasEdgeToBeVisited = true;

//                    followers.add(edge);
                    vertices.add(edge.first);
//                    System.out.println("Added  "+edge+ "for processing. ");
                }
                else  { // if(! isFinishTimeIteration) {
//                    System.out.println("Ignoring  "+edge+ "as it is already  processed. ");


//                    if (edge.first == vIdx) { // Reached back the starting index
//                        //ONE set of followers / unique SCC completed
//                        // re-init followers
//                        followers = new LinkedHashSet<>();
////                        followers.add(edge.first);
//                        followersList.add(followers);
//
//                    }
//                    else if (followers.contains(vIdx)) {
////                        followers.add(edge.first);
//                    }
                }

            }

            if((!hasEdgeToBeVisited) && (!finishTimes.containsKey(idx))){


                t.incrementAndGet();
                path.remove(Integer.valueOf(idx));
//                System.out.println("2. Setting finishing time for  "+idx+ " as "+t.intValue());

                finishTimes.put(idx, t.intValue());
                revFinishTimes.put(t.intValue(),idx);
                for(int i = path.size()-1;i>=0;i--){
                    int idxToFinish = path.get(i);
//                    System.out.println("2. Check "+idxToFinish+ " for finish "+graph.edges.get(idxToFinish));
                    if(!graph.edges.get(idxToFinish).stream().map(x -> x.first).filter(ix -> !visitedVerticesList.get(ix-1)).findFirst().isPresent()) {
                        if(!finishTimes.containsKey(idxToFinish)) {
                            t.incrementAndGet();
//                            System.out.println("2. Setting finishing time for  "+idxToFinish+ " as "+t.intValue());
                            finishTimes.put(idxToFinish, t.intValue());
                            revFinishTimes.put(t.intValue(),idxToFinish);
                        }
                    } else {
                        break;
                    }

                }
                path.removeIf( finishTimes::containsKey);
//                System.out.println("2. Updated Path  for "+vIdx+" : "+path);
            }
            if(vIdx == 874931){
                System.out.println("Looping again..after "+idx+" . # Loops: "+cnt);
            }

        }
        if(vIdx == 874931){
            System.out.println("Finished the DFS for idx "+vIdx);
        }
//        System.out.println("Finished the DFS for idx "+vIdx);
//        System.out.println("Followers for idx "+followersList);
        Integer[] followersArr = new Integer[followers.size()];
        Integer[] followersIdx = followers.toArray(followersArr);
        int followersSize = followersIdx.length;
        for(int i = followersSize-1;i>=0;i--){
            //System.out.println("Attempting to set finish time for  "+followersIdx[i]);
            if(!finishTimes.containsKey(followersIdx[i])) {
                t.incrementAndGet();
                //System.out.println("Setting finish time for  "+followersIdx[i]+ " to "+t.intValue());
                finishTimes.put(followersIdx[i],t.intValue());
                revFinishTimes.put(t.intValue(),followersIdx[i]);
            }

        }


    }

    public static void rDFS(Graph graph) {

           rDFS(graph, i -> i);

    }

    public static void rDFS(Graph graph, Function<Integer,Integer> vIdxGen) {

        int n = graph.numVertices();
        AtomicInteger t = new AtomicInteger(0);
        for(int i = n; i >0;i--){
            final int index = i;
            //System.out.println("In Loop For Index  "+index);
            List<Set<Integer>> followersList = new LinkedList<>();

            leaders.putIfAbsent(i, followersList);
            rDFS(graph,vIdxGen.apply(i),t,leaders.get(i),vIdxGen);
            followersList.forEach(followers -> {
                //System.out.println("# Followers for "+index+ " is "+followers.size());
                sccSizes.add(followers.size());
            });
        }

    }
    public static void rDFS(Graph graph , int vIdx, AtomicInteger t, List<Set<Integer>> followersList,Function<Integer,Integer> vIdxGen) {
        //System.out.println("Attempting  "+vIdx);
        if(visitedVertices[vIdx - 1]) {
            return;
        }
        Set<Integer> followers = null;
        if(followersList.isEmpty()){
            followers = new LinkedHashSet<>();
            followersList.add(followers);
        } else {
           followers = followersList.get(followersList.size()-1);
        }
        followers.add(vIdx);
        //System.out.println("Processing  "+vIdx+ ", followers: "+followers);
        visitedVertices[vIdx - 1] = true;
        List<Pair<Integer,Integer>> ogEdges = null;
        ogEdges = graph.edges.get(vIdx);

        for( Pair<Integer,Integer> ogEdge : ogEdges) {
            int ogEdgeToUse = vIdxGen.apply(ogEdge.first);
            if (!visitedVertices[ogEdgeToUse - 1]) {
                //System.out.println("Adding follower  "+ogEdgeToUse);
                followers.add(ogEdgeToUse);
                rDFS(graph, ogEdgeToUse, t,followersList,vIdxGen);
            }
        };

        finishTimes.put(vIdx,t.incrementAndGet());
        revFinishTimes.put(t.incrementAndGet(),vIdx);
        //System.out.println("Finishing time for "+vIdx+ " : "+t.intValue());


    }


    public static void main(String[] args) throws IOException {

        System.out.println("Hello Algo!");
        week1Assignment();
//        week2Assignment();
//        week3Assignment();
//        week4Assignment();
    }

    private static void week4Assignment() throws IOException {

        String fileName = "src/main/resources/2sum.txt";
        List<Long> allNumsS =  Files.lines(Paths.get(fileName)).map(Long::parseLong).sorted().collect(Collectors.toList());
       Map<Long,Boolean> allNums = allNumsS.stream().collect(Collectors.toMap(x -> x, x -> true, (aBoolean, aBoolean2) -> aBoolean && aBoolean2));
       Hashtable<Integer, Boolean> test = new Hashtable<>();
       final AtomicLong result = new AtomicLong(0);
        System.out.println("Starting figuring out # Distinct Numbers. Total : "+allNums.size());
        allNumsS.stream().forEach(one -> {

           for(int sum = -10000;sum<=10000;sum++){

               long two = sum - one;
               Pair nums = Pair.of(one,two);
               if(allNums.containsKey(two) && (!test.containsKey(sum))){
                   test.put(sum,true);
                   result.incrementAndGet();
                   break;
               }
           }
       });

       System.out.println("Result: "+result.get());

    }

    private static void week3Assignment() throws IOException {
        String fileName = "src/main/resources/median.txt";
        PriorityQueue<Integer> hL = new PriorityQueue<>(5000, Comparator.reverseOrder());
        PriorityQueue<Integer> hH = new PriorityQueue<>(5000);
        List<Integer> medians = new ArrayList<>();
        Stream<Integer> fileContents = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset()).stream().filter(x ->  (x!= null) && (!x.isEmpty())).map(Integer::valueOf);
        fileContents.forEach(num -> {
            if((hL.isEmpty()) || (num <= hL.peek()) ){
                hL.add(num);
            } else  {
                hH.add(num);
            }
            int diff = hL.size() - hH.size();
            if( diff >= 0) {
                for(int cnt=0;cnt<diff-1;cnt++){
                    hH.add(hL.remove());
                }

            } else {
                for(int cnt=0;cnt>diff;cnt--){
                    hL.add(hH.remove());
                }
            }
            medians.add(hL.peek());
        });
        System.out.println("Medians:");
        System.out.println(medians);
        System.out.println("Answer:");
        System.out.println( medians.stream().reduce(0, (a, b) -> a+b,( x,y)  -> x) % 10000);
    }

    private static Comparator<Pair<Integer,Integer>> comparatorFor(int vMin) {
        return (o1, o2) -> {

            int o1Min = o1.second+vMin;
            int o2Min = o2.second+vMin;
            if(o1Min < o2Min){
                return -1;
            }
            if(o1Min > o2Min) {
                return 1;
            }
            return 0;
        };
    }

    private static void week2Assignment() throws IOException {

        Graph graph = fileContentsAsGraph( "./src/main/resources/dijkstraData.txt");

        // Running Dijkstra Algo with src vertex as 1
        Map<Integer, Integer> minDistance = new HashMap<>();
        Set<Integer> X = new HashSet<>();
        X.add(1);
        minDistance.put(1,0);

        Set<Integer> Y = graph.edges.values().stream().flatMap(p -> p.stream()).map(k -> k.first).filter(w -> !X.contains(w)).collect(Collectors.toSet());
        Y.forEach(v-> minDistance.put(v,100000) );
        while(Y.size() >0) {
            Iterator<Integer> xI = X.iterator();
            Set<Pair<Integer, Integer>> yV = new HashSet<>();
            while (xI.hasNext()) {
                int v = xI.next();
                final int vMin = minDistance.get(v);
                Optional<Pair<Integer, Integer>> minEdge = graph.edges.get(v).stream().filter(p -> Y.contains(p.first)).min(comparatorFor(vMin));
                minEdge.ifPresent(p -> {
                            yV.add(new Pair<>(p.first, vMin+p.second));
                        }
                );


            }
            yV.stream().min(comparatorFor(0)).ifPresent(v -> {
                minDistance.put(v.first,v.second);
                X.add(v.first);
                Y.remove(v.first);
            });

        }
        int[] indices = new int[]{7,37,59,82,99,115,133,165,188,197};
        System.out.println();
        for(int i=0;i<indices.length;i++){
            System.out.print(minDistance.get(indices[i]));
            if(i<(indices.length -1)) {
                System.out.print(",");
            }
        }
        System.out.println();


    }

    private static Graph fileContentsAsGraph(String fileName ) throws IOException {
        Graph graph = new Graph();
        List<String> fileContents = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());

        for (String fileContent : fileContents) {
            if(fileContent.trim().length() == 0){
                continue;
            }
            String[] contents = fileContent.split("\\s+");

            int vertex = Integer.parseInt(contents[0]);
            for(int i =1;i<contents.length;i++){
                String[] edgeInfo = contents[i].split(",");
                if(edgeInfo.length == 2){
                    graph.addOrUpdateVertex(vertex, Integer.parseInt(edgeInfo[0]),Integer.parseInt(edgeInfo[1]));

                } else if (edgeInfo.length == 1){
                    graph.addOrUpdateVertex(vertex, Integer.parseInt(edgeInfo[0]));
                }
                else {
                    System.out.println("Invalid Entry in file. No edge info found after the vertex "+vertex+"\n Line: "+fileContent);
                    continue;
                }
                if(graph.edges.get(Integer.parseInt(edgeInfo[0])) == null){
                    graph.addOrUpdateVertex(Integer.parseInt(edgeInfo[0]), -1);
                }
            }
        }
        return graph;
    }

    private static void week1Assignment() throws IOException {
        Graph graph = fileContentsAsGraph( "./src/main/resources/scc.txt");

        //System.out.println("Orig Graph - "+graph);
        System.out.println("Number of vertices - "+graph.numVertices());
//        System.exit(0);
        visitedVertices = new boolean[graph.numVertices()];
        visitedVerticesList = new ArrayList<>(graph.numVertices());
        for(int cnt=0;cnt<visitedVertices.length;cnt++){
            visitedVerticesList.add(Boolean.FALSE);
        }
        finishTimes = new TreeMap<Integer,Integer>(Comparator.reverseOrder());
        revFinishTimes = new HashMap<>();
        Graph revG = reverse(graph);
        //System.out.println("Reversed Graph - "+revG);
        System.out.println("Number of vertices After Reversal - "+revG.numVertices());
        System.out.println("Edges of 874931 after reverseal"+ revG.edges.get(874931));
//        rDFS(revG);
        DFS(revG,i -> i,visitedVerticesList,true);

        System.out.println("# Finishing Times after first DFS : " + finishTimes.size());
//        System.out.println("Finishing Times after first DFS : " + finishTimes);
        //System.out.println("Reverse Finishing Times after first DFS : " + revFinishTimes);
        visitedVertices = new boolean[graph.numVertices()];
        visitedVerticesList.clear();
        for(int cnt=0;cnt<visitedVertices.length;cnt++){
            visitedVerticesList.add(Boolean.FALSE);
        }
        leaders.clear();
        sccSizes.clear();
        Graph updatedKeyGraph = updateVertices(graph, finishTimes);
//        System.out.println("Updated Keys Graph - "+updatedKeyGraph);
//        rDFS(updatedKeyGraph);
        finishTimes.clear();
        Map<Integer,Integer> revFTToUse = new HashMap<>();
        revFTToUse.putAll(revFinishTimes);
        revFinishTimes.clear();
        DFS(updatedKeyGraph,i -> i,visitedVerticesList,false);
//        rDFS(updatedKeyGraph);

//        System.out.println("Finished Second pass: " + leaders);
//        System.out.println("The SCCs with their leader: ");
//        leaders.forEach((k, v) -> {
//            if(v.size() > 0) {
//                System.out.print(revFTToUse.get(k) + " -> ");
//                v.forEach(val -> {
//                    val.forEach(val1 -> {
//                        System.out.print(revFTToUse.get(val1) + " ");
//
//                    });
//                    System.out.print(" , ");
//                });
//                System.out.println("");
//            }
//        });
        sccSizes.sort(Comparator.reverseOrder());
        System.out.print("SCC Sizes: ");
        int maxCnt = Math.min(5,graph.edges.size());
        for(int i=0;i<maxCnt;i++) {
            System.out.print(sccSizes.get(i));
            if(i<(maxCnt-1)) {
                System.out.print(",");
            }
        }
        System.out.println("");
    }


    private static void printArr(String msgPrefix, int[] arr) {
        System.out.print(msgPrefix);
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < (arr.length - 1)) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }


}
