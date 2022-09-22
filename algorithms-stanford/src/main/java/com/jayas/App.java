package com.jayas;

import com.sun.codemodel.internal.fmt.JStaticFile;
import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;
import com.sun.xml.internal.rngom.digested.DInterleavePattern;
import com.sun.xml.internal.xsom.impl.ListSimpleTypeImpl;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{

    private static class Edge {
        private int from;
        private int to;

        public boolean isRemoved() {
            return (from == to ) || isRemoved;
        }

        public Edge removed() {
            isRemoved = true;
            return this;
        }

        private boolean isRemoved = false;

        public Edge(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public Edge setFrom(int from) {
            this.from = from;
            return this;
        }

        public Edge setTo(int to) {
            this.to = to;
            return this;
        }

        @Override
        public int hashCode() {
            if(from <= to) {
                return Objects.hash(from, to, isRemoved);
            } else {
                return Objects.hash(to, from, isRemoved);
            }

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return ((from == edge.from && to == edge.to) || (from == edge.to && to == edge.from)) && isRemoved == edge.isRemoved;
        }
    }

    private static int numOfComparisons =0;
    public static void main( String[] args ) throws URISyntaxException, IOException {

        System.out.println( "Hello Algo!" );
//        String num1 = "3141592653589793238462643383279502884197169399375105820974944592";
//        String num2 = "2718281828459045235360287471352662497757247093699959574966967627";
////30453574591656447131
//
//
//       String  finalResult =  multiply(num1,num2);
//       System.out.println("Final Result: "+finalResult);
//        BigInteger x = new BigInteger("3141592653").multiply(new BigInteger("2718281828"));
//        System.out.println("x: "+x);
//        BigInteger y = new BigInteger("5897932384").multiply(new BigInteger("4590452353"));
//        System.out.println("y: "+y);
//        BigInteger z = new BigInteger("3141592653").add(new BigInteger("5897932384")).multiply(new BigInteger("2718281828").add(new BigInteger("4590452353")));
//        System.out.println("z: "+z);
//        BigInteger k=  z.subtract(x).subtract(y);
//        System.out.println("k: "+k);
//
//        String actualResult = new BigInteger(num1).multiply(new BigInteger(num2)).toString();
//        System.out.println("Actual Result: "+ actualResult);
//
//        System.out.println("Test Passed? :"+actualResult.equals(finalResult));
//        int[] testArr = new int[]{1,3,5,2,4,6};
//        List<String> fileContents = Files.readAllLines(
//                Paths.get("./src/main/resources/IntegerArray.txt"), Charset.defaultCharset());
//
//        int[] testArr = fileContents.stream().mapToInt(Integer::parseInt).toArray();
//        System.out.println("Num of integers: "+testArr.length);
//        ArrayAndCountOfInversion result  = countInversions(testArr);
//        System.out.println("Num of inversions: "+result.getNumOfInversions());
//        printArr("Sorted Array: ",result.getSortedArr());
//        int[] inArr = new int[]{7,3,6,1,8,2,5,0,9,4};


//        List<String> fileContents = Files.readAllLines(Paths.get("./src/main/resources/QuickSort_Integers.txt"), Charset.defaultCharset());
//
//        int[] inArr = fileContents.stream().mapToInt(Integer::parseInt).toArray();
//        System.out.println("Num of integers: "+inArr.length);
////        printArr("Input Array",inArr);
//        numOfComparisons =  inArr.length -1;
//        quickSort(inArr,0,inArr.length);
//        System.out.println("Number of comparisons : "+numOfComparisons);
//        printArr("Sorted Array",inArr);

        //Graph MinCut

        int[] numOfMinCuts = new int[]{0,0,0};
//        for(int i =0;i<16;i++){
//            List<Integer> vertices = updateable(Arrays.asList(1,2,3,4));
//            List<List<Integer>> edges = updateable(Arrays.asList(updateable(Arrays.asList(2,3)), updateable(Arrays.asList(1,3,4)),updateable(Arrays.asList(1,2,4)),updateable(Arrays.asList(3,2))));//,updateable(Arrays.asList(6,8,7,2)),updateable(Arrays.asList(8,5)),updateable(Arrays.asList(5,8,4)),updateable(Arrays.asList(6,7,5))));
//            int[] mincutIter = mincut(vertices,edges,vertices.size());
//            if((numOfMinCuts[2] == 0) || (mincutIter[2] < numOfMinCuts[2])) {
//                numOfMinCuts[0] = mincutIter[0];
//                numOfMinCuts[1] = mincutIter[1];
//                numOfMinCuts[2] = mincutIter[2];
//            }
//        }



   List<String> fileContents = Files.readAllLines(Paths.get("./src/main/resources/TestMinCut.txt"), Charset.defaultCharset());
        List<Integer> vertices = new ArrayList<>(fileContents.size());
        List<List<Integer>> edges = new ArrayList<>(fileContents.size());
        Set<Edge> listOfEdges = new HashSet<>(fileContents.size());
        Map<Integer, List<Edge>> fromVertexEdges = new HashMap<>(fileContents.size());
        Map<Integer, List<Edge>> toVertexEdges = new HashMap<>(fileContents.size());

        for (String fileContent : fileContents) {
            String[] indices = fileContent.split("\\s+");
            int fromVertex = Integer.parseInt(indices[0]);
            vertices.add(fromVertex);
            List<Edge> fromEdges = Optional.ofNullable(fromVertexEdges.get(fromVertex)).orElseGet(() -> {
                List<Edge>  newFromEdges = new ArrayList<>();
                fromVertexEdges.put(fromVertex,newFromEdges);
                return newFromEdges;
            });
            List<Integer> edgeVertices = new ArrayList<>();
            for(int j=1; j< indices.length; j++){
                int toVertex = Integer.parseInt(indices[j]);
                edgeVertices.add(toVertex);
                Edge newEdge = new Edge(fromVertex,toVertex);
                if(!listOfEdges.contains(newEdge)){
                    listOfEdges.add(newEdge);
                }
                fromEdges.add(newEdge);
                List<Edge> toEdges = Optional.ofNullable(toVertexEdges.get(toVertex)).orElseGet(() -> {
                    List<Edge>  newToEdges = new ArrayList<>();
                    toVertexEdges.put(toVertex,newToEdges);
                    return newToEdges;
                });
                toEdges.add(newEdge);
            }
            edges.add(edgeVertices);
        }

        int numVertices = vertices.size();
       for(int i =0;i<numVertices*Math.log(numVertices);i++) {
            List<Integer> iVertices =  new ArrayList<>(numVertices);
            iVertices.addAll(vertices);
           List<List<Integer>> iEdges =  new ArrayList<>(numVertices);
           for (List<Integer> edge : edges) {
               List<Integer> iEdgeEntry = new ArrayList<>(edge.size());
               iEdgeEntry.addAll(edge);
               iEdges.add(iEdgeEntry);
           }

            int[] iMinCut = mincut(iVertices,iEdges,numVertices,listOfEdges,fromVertexEdges,toVertexEdges);
           printArr("Iteration #: "+(i+1)+" , Current Min Cuts: ",iMinCut);
            if((numOfMinCuts[2] == 0) || (iMinCut[2] < numOfMinCuts[2])) {
                numOfMinCuts[0] = iMinCut[0];
                numOfMinCuts[1] = iMinCut[1];
                numOfMinCuts[2] = iMinCut[2];
            }
        }

System.out.println("\n\nFinal RESULT \n\n");
        printArr("Min Cuts: ",numOfMinCuts);


    }

    private  static <T> List<T> updateable(List<T> in) {
        ArrayList<T> retVal = new ArrayList<>(in.size());
        retVal.addAll(in);
        return retVal;
    }


    // Min Cut Graph Algo

    private static int[] mincut(List<Integer> vertices, List<List<Integer>> edges,int numOfVertices,  Set<Edge> listOfEdges,Map<Integer, List<Edge>> fromVertexEdges ,Map<Integer, List<Edge>> toVertexEdges ) {
        int n = numOfVertices;
//        int n = listOfEdges.size();
        Random random = new Random();
        for(int i=0; i< n-2; i++) {
            System.out.println(vertices);
            System.out.println(edges);
            int aIdx = random.nextInt(n);
            while(vertices.get(aIdx) == -1) {
                aIdx  = random.nextInt(n);
            }
            int bIdx = aIdx;
            while((bIdx == aIdx) || (vertices.get(bIdx) == -1)){
                bIdx = random.nextInt(n);
            }

//            Edge edgeToRemove = listOfEdges.stream().collect(Collectors.toList()).get(random.nextInt(n));
//            while(edgeToRemove.isRemoved()) {
//                edgeToRemove = listOfEdges.stream().collect(Collectors.toList()).get(random.nextInt(n));
//            }
//            int aIdx =edgeToRemove.getFrom() -1 ;
//            int bIdx =edgeToRemove.getTo() -1;

            fuseVertices(vertices,edges,aIdx,bIdx, fromVertexEdges, toVertexEdges);
//            edgeToRemove.removed();
            System.out.println(vertices);
            System.out.println(edges);
        }
        List<Integer> finalVertices = vertices.stream().filter(x -> x > 0).collect(Collectors.toList());
        List<List<Integer>> finalEdges  = edges.stream().filter(x -> x.size()>0).collect(Collectors.toList());

       return  new int[]{finalVertices.get(0),finalVertices.get(1),finalEdges.get(0).size()};


    }

    private static void fuseVertices(List<Integer> vertices, List<List<Integer>> edges,int aIdx, int bIdx, Map<Integer, List<Edge>> fromVertexEdgesMap ,Map<Integer, List<Edge>> toVertexEdgesMap) {
        int idxToRetain = aIdx;
        int idxToRemove = bIdx;
//        if(bIdx < aIdx) {
//            idxToRetain = bIdx;
//            idxToRemove = aIdx;
//        }
        System.out.println("Fusing Index "+idxToRemove+" with "+idxToRetain);
        Integer vertexToRetain = vertices.get(idxToRetain);
//        System.out.println("Vertex to Retain "+vertexToRetain);
        Integer vertexToRemove = vertices.get(idxToRemove);
//        System.out.println("Vertex to Remove "+vertexToRemove);
//       vertices[idxToRemove] = -1;
        List<Integer> fromVertexEdges = edges.get(idxToRemove);
        while(fromVertexEdges.remove(vertexToRetain));
        List<Integer> toVertexEdges = edges.get(idxToRetain);
        while(toVertexEdges.remove(vertexToRemove));
        toVertexEdges.addAll(fromVertexEdges);

        //Find other nodes connected to fromVertex
        Map<Integer, List<Integer>> edgesToUpdateMap = new HashMap<>();
        fromVertexEdges.forEach(vertex -> {
            int vIdx = vertex -1;
            List<Integer> edgesToFromVertex = edges.get(vIdx).stream().filter(v -> v == vertexToRemove).collect(Collectors.toList());
            edgesToUpdateMap.put(vIdx,edgesToFromVertex);
        } );
        fromVertexEdges.clear();

        edgesToUpdateMap.entrySet().forEach(entry -> {
            List<Integer> edgesToUpdate =  edges.get(entry.getKey());
            entry.getValue().forEach(val -> {
                edgesToUpdate.remove(val);
                if(vertexToRetain < 0) {
                    System.out.println("FOUND ISSUE val: "+val+ " vertexToRetain : "+vertexToRetain);
                }
                edgesToUpdate.add(vertexToRetain);
            });

        });

        vertices.set(idxToRemove, -1);
        List<Edge> fromEdgesToUpdate = Optional.ofNullable(fromVertexEdgesMap.get(vertexToRemove)).orElse(new ArrayList<>());
        fromEdgesToUpdate.forEach(edge ->  edge.setFrom(vertexToRetain));
        List<Edge> toEdgesToUpdate = Optional.ofNullable(toVertexEdgesMap.get(vertexToRemove)).orElse(new ArrayList<>());
        fromEdgesToUpdate.forEach(edge ->  edge.setTo(vertexToRetain));

    }



    private static void swapPos(int[] inArr, int idx1, int idx2) {
        int tmp = inArr[idx1];
        inArr[idx1] = inArr[idx2];
        inArr[idx2] = tmp;
    }

    private static int[] partitionOnPivot(int[] inArr, int fIdx, int toIdx){
        // Question 1
//        int pivot = inArr[fIdx];


 // Question 2
//            int pivot = inArr[toIdx-1];
//            inArr[toIdx-1] = inArr[fIdx];
//            inArr[fIdx] = pivot;


        //Question 3
        int inSize = toIdx - fIdx;
        int medIdx =  fIdx + (inSize/2) - 1;
        if((inSize % 2) > 0) { // odd number
            medIdx = medIdx +1;
        }

        int medVal = inArr[medIdx];
        int firstVal = inArr[fIdx];
        int finalVal = inArr[toIdx -1];


        int[] pivotCandidates = new int[] {firstVal,medVal,finalVal};
        Arrays.sort(pivotCandidates);
        int pivot = pivotCandidates[1];
        int pivotIdx = fIdx;
        if(pivot == medVal) {
            pivotIdx = medIdx;
        } else if(pivot == finalVal) {
            pivotIdx = toIdx -1;
        }

        if(pivotIdx != fIdx){
            int swapval = inArr[fIdx];
            inArr[fIdx] = inArr[pivotIdx];
            inArr[pivotIdx] = swapval;
        }


        int i =fIdx,j = fIdx+1;

        while(j < toIdx) {
            if(inArr[j] < pivot) {
                i++;
                swapPos(inArr,i,j);
            }
            j++;
        }
        swapPos(inArr,i,fIdx);

        return new int[] {fIdx,i,i+1,toIdx};

    }

    private static void  quickSort(int[] inArr, int fIdx, int toIdx) {
//        printArr("QuickSort fIdx: "+fIdx+" , toIdx: "+toIdx+", Array: ",inArr);
        int inSize = toIdx - fIdx;

        //Base Cases
        if(inSize <= 0 ){
            return;
        }
        if(inSize == 1) {
            return;
        }
        if(inSize == 2) {
            if(inArr[fIdx+0] > inArr[fIdx+1]){

                int swapVal =   inArr[fIdx+1];
                inArr[fIdx+1] = inArr[fIdx+0];
                inArr[fIdx+0] = swapVal;
            }
            return;
        }
        // Partition the array
        int[] pIndices = partitionOnPivot(inArr, fIdx, toIdx);
        // Recursively call for the left and right sub array
//        System.out.println("Number of comparisons B4: "+numOfComparisons);
        numOfComparisons = numOfComparisons + ((pIndices[1] - pIndices[0] > 1) ?  (pIndices[1] - pIndices[0] - 1) : 0);
//        System.out.println("Number of comparisons After: "+numOfComparisons);
        quickSort(inArr, pIndices[0], pIndices[1]);
//        System.out.println("Number of comparisons B4: "+numOfComparisons);
        numOfComparisons = numOfComparisons + ((pIndices[3] - pIndices[2] > 1) ?  (pIndices[3] - pIndices[2] - 1) : 0);
//        System.out.println("Number of comparisons After: "+numOfComparisons);
        quickSort(inArr, pIndices[2], pIndices[3]);



    }

    private static void printArr(String msgPrefix, int[] arr){
        System.out.print(msgPrefix);
        System.out.print("[");
        for(int i=0;i<arr.length;i++){
            System.out.print(arr[i]);
            if(i < (arr.length -1)){
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    private static int[] extractFromArray(int[] arr, int s, int e) {
        int[] retVal = new int[e-s];
        int j =s;
        for(int i=0;i<retVal.length;i++, j++) {
            retVal[i] = arr[j];
        }
        return retVal;
    }

    private static class ArrayAndCountOfInversion {
        public int[] getSortedArr() {
            return sortedArr;
        }

        public long getNumOfInversions() {
            return numOfInversions;
        }

        private final int[] sortedArr;
        private final long numOfInversions;

        public ArrayAndCountOfInversion(int[] sortedArr, long numOfInversions) {
            this.numOfInversions = numOfInversions;
            this.sortedArr = sortedArr;
        }
    }

    private static ArrayAndCountOfInversion countSplitInversions(int[] sortedLeftArr, int[] sortedRightArr) {
        int i =0;
        int j = 0;
        int k =0;
        int numOfInversions = 0;
        int rl = sortedRightArr.length;
        int ll = sortedLeftArr.length;
        int tl = rl+ll;
        int[] retValArr = new int[tl];
        while(k< tl) {
            if((i < ll) && (j < rl) && (sortedLeftArr[i] <= sortedRightArr[j])) {
                retValArr[k] = sortedLeftArr[i];
                i++;
                k++;
            } else if((i < ll) && (j < rl) && (sortedLeftArr[i] > sortedRightArr[j])) {
                retValArr[k] = sortedRightArr[j];
                j++;
                k++;
                numOfInversions = numOfInversions + (sortedLeftArr.length - i );
            } else if(i >= ll) {
                retValArr[k] = sortedRightArr[j];
                j++;
                k++;

            } else if( j >= rl) {
                retValArr[k] = sortedLeftArr[i];
                i++;
                k++;

            }
        }
        System.out.println("leftArrSize: "+ll+" ,  rightArrSize: "+rl+" , # Inversions: "+numOfInversions);
        return new ArrayAndCountOfInversion(retValArr,numOfInversions);
    }

    private static ArrayAndCountOfInversion countInversions(int[] arrVals) {
        int len = arrVals.length;
        if(len<2){
            return new ArrayAndCountOfInversion(arrVals,0);
        }
        if(len == 2) {
            int i=0;
            int j = 1;
            return (arrVals[j] < arrVals[i]) ? new ArrayAndCountOfInversion(new int[]{arrVals[j],arrVals[i]},1) : new ArrayAndCountOfInversion(arrVals,0);
        }

        int[] leftSide = extractFromArray(arrVals, 0, len/2);
        int[] rightSide = extractFromArray(arrVals, len/2, len);

        ArrayAndCountOfInversion leftInversions = countInversions(leftSide);
        ArrayAndCountOfInversion rightInversions = countInversions(rightSide);
        ArrayAndCountOfInversion mixedInversions = countSplitInversions(leftInversions.sortedArr, rightInversions.sortedArr);
        return new ArrayAndCountOfInversion(mixedInversions.getSortedArr(),leftInversions.getNumOfInversions()+rightInversions.getNumOfInversions()+mixedInversions.getNumOfInversions());
    }



    private static String multiply(String n1, String n2) {

        System.out.println("multiply n1:"+n1+" , n2: "+n2);
        long n1Len = n1.length();
        long n2Len = n2.length();
        long num1Zeros = n1Len % 2;
        long num2Zeros = n2Len % 2;

        System.out.println("multiply num1Zeros:"+num1Zeros+" , num2Zeros: "+num2Zeros);

        StringBuffer num1Buf = new StringBuffer();
        StringBuffer num2Buf = new StringBuffer();
        for(int i=0;i<num1Zeros;i++) {
            num1Buf.append("0");
        }
        num1Buf.append(n1);
        for(int i=0;i<num2Zeros;i++) {
            num2Buf.append("0");
        }
        num2Buf.append(n2);

        String num1Temp = num1Buf.toString();
        String num2Temp = num2Buf.toString();

        //Make the strings of same length
        for(int i=0;i<(num1Temp.length() - num2Temp.length());i++) {
            num2Buf.insert(0, '0');
        }

        for(int i=0;i<(num2Temp.length() - num1Temp.length());i++) {
            num1Buf.insert(0, '0');
        }

        String num1 = num1Buf.toString();
        String num2 = num2Buf.toString();

        if((num1.length() <=2) && (num2.length() <=2)) {
            return Integer.toString(Integer.parseInt(num1) * Integer.parseInt(num2));
        }
        System.out.println("multiply num1:"+num1+" , num2: "+num2);
        BigInteger a = new BigInteger(num1.substring(0,num1.length()/2));
        System.out.println("a:"+a);
        BigInteger b = new BigInteger(num1.substring(num1.length()/2));
        System.out.println("b:"+b);
        BigInteger c = new BigInteger(num2.substring(0,num2.length()/2));
        System.out.println("c:"+c);
        BigInteger d = new BigInteger(num2.substring(num2.length()/2));
        System.out.println("d:"+d);

        BigInteger x = new BigInteger(multiply(a.toString(),c.toString()));
        System.out.println("x:"+x);
        BigInteger y = new BigInteger(multiply(b.toString(),d.toString()));
        System.out.println("y:"+y);
        BigInteger z = new BigInteger(multiply(a.add(b).toString(),c.add(d).toString()));
        System.out.println("z:"+z);
        BigInteger k = z.subtract( x).subtract(y);
        System.out.println("k:"+k);

        BigInteger result = x.multiply(new BigInteger("10").pow(num1.length())).add(k.multiply(new BigInteger("10").pow((num1.length()/2)))).add(y);
        BigInteger actualResult = new BigInteger(n1).multiply(new BigInteger(n2));
        System.out.println("result: "+n1 +" * "+n2+" = "+result);
        System.out.println("actual result: "+actualResult);
        System.out.println("Intermediate  result matched : "+actualResult.equals(result));
        return result.toString() ;
    }
}
