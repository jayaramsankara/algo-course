package com.jayas;



import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public class course3 {




    public static void main(String[] args) throws IOException {

        System.out.println("Hello Algo!");
        week1Assignment();
//        week2Assignment();
//        week3Assignment();
//        week4Assignment();
    }

    private static  class Pair<T,V>  implements  Comparable {
        private T first;
        private V second;

        private BiFunction<Object,Object,Long> hashFn;

        public Pair(T first, V second) {
            this.first =first;
            this.second = second;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof course3.Pair)) return false;
            course3.Pair<?, ?> pair = (course3.Pair<?, ?>) o;
            return (Objects.equals(first, pair.first) && Objects.equals(second, pair.second)) || (Objects.equals(first, pair.second) && Objects.equals(second, pair.first));
        }

        @Override
        public int hashCode() {
            if(hashFn == null) {
                return Objects.hash(first, second);
            }
            return hashFn.apply(first,second).intValue();

        }

        public T getFirst(){
            return first;
        }

        public V getSecond() {
            return second;
        }

        public  static <X,Y> course3.Pair of (X one, Y two) {
            return new course3.Pair<>(one, two);
        }

        public course3.Pair<T,V> withHashFunction(BiFunction<Object,Object,Long> hashFn) {
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

        private Map<Integer, List<course3.Pair<Integer,Integer>>> edges = new LinkedHashMap<>();


        public Graph() {

        }

        public int numVertices() {
            return edges.size();
        }

        public void addOrUpdateVertex(int vertex, int outgoingEdge, int length) {

            List<course3.Pair<Integer,Integer>> oe = new ArrayList<>();
            if (outgoingEdge >= 0) {
                oe.add(new course3.Pair<>(outgoingEdge,length));
            }

            List<course3.Pair<Integer,Integer>> currValues = edges.putIfAbsent(vertex, oe);
            if ((currValues != null) && (outgoingEdge >= 0)) {
                currValues.add( new course3.Pair<>(outgoingEdge,length));
            }

        }
        private void addOrUpdateVertex(int vertex, int outgoingEdge) {
            addOrUpdateVertex(vertex,outgoingEdge,0);


        }

    }
    private  static  Comparator<Pair<Long, Pair<Long, Long>>> sortFn(BiFunction<Long,Long,Double> costFn) {
       return  new Comparator<Pair<Long, Pair<Long, Long>>>() {
            @Override
            public int compare(Pair<Long, Pair<Long, Long>> o1, Pair<Long, Pair<Long, Long>> o2) {
                Pair<Long,Long> j1 = o1.second;
                Pair<Long,Long> j2 = o2.second;
                double j1diff =costFn.apply(j1.first,j1.second);
                double j2diff =costFn.apply(j2.first,j2.second);
                if(j1diff > j2diff) {
                    return -1;
                }
                if(j1diff < j2diff) {
                    return 1;
                }
                if(j1.first > j2.first) {
                    return -1;
                }
                if(j1.first < j2.first) {
                    return 1;
                }
                return 0;

            }
        };
    }

    private  static void week1Assignment() throws IOException {
        String file1Name = "./src/main/resources/jobs.txt";
        String file2Name = "./src/main/resources/edges.txt";
        part1Week1(file1Name);
        part2Week1(file1Name);

        part3Week1(file2Name);

    }

    private static void part1Week1(String fileName) throws IOException  {
        schedulingAlgoGen(fileName, (a, b) -> (a - b)*1.0D);
    }

    private static void part2Week1(String fileName) throws IOException  {
        schedulingAlgoGen(fileName, (a, b) -> (a*1.0 / b*1.0));
    }

    private static void part3Week1(String fileName)  throws IOException {
        List<String> fileContents = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
        String[] graphInfo = fileContents.remove(0).split("\\s+");
        int numVertices = Integer.parseInt(graphInfo[0]);
        int numEdges = Integer.parseInt(graphInfo[1]);

        int initialVertex = new Random().nextInt(numVertices)+1;
        Set<Integer> seenVertices = new HashSet<>();
        seenVertices.add(initialVertex);



        Graph graph  = new Graph();
        List<Pair<Integer,Pair<Integer,Integer>>>  spanningTree = new ArrayList<>();

        for (String fileContent : fileContents) {
            String[] edgeInfo = fileContent.split("\\s+");
            int node1 = Integer.parseInt(edgeInfo[0]);
            int node2 = Integer.parseInt(edgeInfo[1]);
            int cost = Integer.parseInt(edgeInfo[2]);
            graph.addOrUpdateVertex(node1,node2,cost);
            graph.addOrUpdateVertex(node2,node1,cost);

        }
        while(seenVertices.size() < numVertices) {
            Stream<Pair<Integer, Pair<Integer, Integer>>> allOutgoingEdges =  seenVertices.stream().flatMap(v -> graph.edges.get(v).stream().filter(e -> !seenVertices.contains(e.first)).map(x -> Pair.of(v,x))).map(f -> (Pair<Integer,Pair<Integer,Integer>>)f);
            Optional<Pair<Integer,Pair<Integer,Integer>>>  cheapestEdge = allOutgoingEdges.min(new Comparator<Pair<Integer, Pair<Integer, Integer>>>() {

                @Override
                public int compare(Pair<Integer, Pair<Integer, Integer>> o1, Pair<Integer, Pair<Integer, Integer>> o2) {
                   if(o1.second.second < o2.second.second){
                       return -1;
                   }
                    if(o1.second.second > o2.second.second){
                        return 1;
                    }
                    return 0;
                }
            });
            cheapestEdge.ifPresent(p ->  {
                seenVertices.add(p.second.first);
                spanningTree.add(p);
            });

        }
        System.out.println("MST : "+spanningTree);
        int overallMinCost = spanningTree.stream().map(s -> s.second.second).reduce(0, (a,b) -> a + b);
        System.out.println("MST  Cost: "+overallMinCost);




    }

    private static void schedulingAlgoGen(String fileName, BiFunction<Long, Long,Double> costFn) throws IOException {


        List<String> fileContents = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
        long numOfJobs = Long.parseLong(fileContents.remove(0).trim());
        AtomicInteger idx = new AtomicInteger(-1);

        Stream<Pair<Long, Pair<Long, Long>>> jobInfo = fileContents.stream().filter(i -> i.trim().length() > 0).map(x -> x.split("\\s+")).map(y -> Pair.of(idx.incrementAndGet(), Pair.of(Long.parseLong(y[0]), Long.parseLong(y[1]))));


        List<Pair<Long,Pair<Long,Long>>> sortedJobInfo =  jobInfo.collect(Collectors.toList());
        sortedJobInfo.sort(sortFn(costFn));
        List<Pair<Long,Long>>  sortedFinalCompletionTimes = new ArrayList<>();

        sortedFinalCompletionTimes =  sortedJobInfo.stream().map(x -> x.second).reduce(sortedFinalCompletionTimes, (x, y) -> {
            Pair<Long,Long>  newCT  = Pair.of(y.first,(lastOf(x).map(k -> k.second).orElse(0L)+y.second));
           x.add(newCT);
           return x;
       }, (a, b) -> a);
       long result =  sortedFinalCompletionTimes.stream().reduce(0L,(x, y) -> x + y.first*y.second, (a, b) -> a);
    System.out.println("Sum of weighted completion times using weight - length greedy algo : "+result);



    }

    private static <T> Optional<T> lastOf(List<T> input) {
        return Optional.of(input).filter(x -> !x.isEmpty()).map(x -> x.get(x.size() -1));
    }

    private static course3.Graph fileContentsAsGraph(String fileName ) throws IOException {
        course3.Graph graph = new course3.Graph();
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
