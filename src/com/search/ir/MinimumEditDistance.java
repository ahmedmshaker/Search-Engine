package com.search.ir;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class MinimumEditDistance {
    
    public interface CostFunction {
        public int cost(int[][] distanceMatrix, CharSequence x, CharSequence y, int i, int j);
    }
    
    public static final CostFunction ONE = new CostFunction() {
        public int cost(int[][] distanceMatrix, CharSequence x, CharSequence y, int i, int j) {
            return 1;
        }
    };
    
    public static final CostFunction TWO = new CostFunction() {
        public int cost(int[][] distanceMatrix, CharSequence x, CharSequence y, int i, int j) {
            return 2;
        }
    };
    
    
    public static int distance(CharSequence x, 
                               CharSequence y) 
    {
        return distance(x, y, false);
    }
    
    
    public static int distance(CharSequence x, 
                               CharSequence y, 
                               boolean jurafskyLevenshtein) 
    {
        int result = 0;     
        int[][] d = null;
        if (jurafskyLevenshtein) {
            d = distanceMatrix(x, y, ONE, ONE, TWO);
        } else {
            d = distanceMatrix(x, y, ONE, ONE, ONE);
        }
        result = d[x.length()][y.length()];
        return result;
    }
    
    public static int distance(CharSequence x, 
                               CharSequence y, 
                               CostFunction deletion, 
                               CostFunction insertion, 
                               CostFunction substitution)
    {
        int result = 0;     
        int[][] d = distanceMatrix(x, y, deletion, insertion, substitution);
        result = d[x.length()][y.length()];
        return result;
    }
    
    public static int[][] distanceMatrix(CharSequence x, 
                                         CharSequence y, 
                                         CostFunction deletion, 
                                         CostFunction insertion, 
                                         CostFunction substitution) 
    {
        int[][] d = new int[x.length()+1][y.length()+1];
        // initialize
        for (int i=1; i < d.length; i++)
            d[i][0] = d[i-1][0] + deletion.cost(d, x, y, i, 0);
        for (int j=1; j < d[0].length; j++)
            d[0][j] = d[0][j-1] + insertion.cost(d, x, y, 0, j);    
        for (int i=1; i < d.length; i++) {
            for (int j=1; j < d[i].length; j++) {
                if (x.charAt(i - 1) == y.charAt(j - 1)) {
                    d[i][j] = d[i-1][j-1];
                } else {
                    d[i][j] = min(d[i-1][j] + deletion.cost(d, x, y, i, j), 
                                  d[i][j-1] + insertion.cost(d, x, y, i, j),
                                  d[i-1][j-1] + substitution.cost(d, x, y, i, j));
                }
            }
        }
        return d;
    }
    
    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
    
    public static int getMinEditDistance(int[][] distanceMatrix) {
        return distanceMatrix[distanceMatrix.length - 1][distanceMatrix[0].length - 1];
    }
    
    public static List<int[]> computeBacktrace(int[][] d) {
        List<int[]> backtrace = new ArrayList<int[]>();
        int i=d.length-1;
        int j=d[0].length-1;
        
        while (true) {
            backtrace.add(new int[] {i, j});
            if (i==1 && j==1) break;
            else if (i==1 && j > 1) j--;
            else if (j==1 && i > 1) i--;
            else {
                if (d[i-1][j] < d[i-1][j-1]) {
                    i--;
                } else if (d[i][j-1] < d[i-1][j-1]) {
                    j--;
                } else {
                    i--;
                    j--;
                }
            }
        }
        Collections.reverse(backtrace);

        return backtrace;
    }
    
    public static void printDistanceMatrix(int[][] d, CharSequence x, CharSequence y) {
        StringBuilder builder = new StringBuilder();
        builder.append("\t\t");
        for (int i=0; i < y.length(); i++) {
            builder.append(y.charAt(i)).append('\t');
        }
        builder.append('\n');
        for (int i=0; i < d.length; i++) {
            if (i == 0) builder.append("\t");
            else builder.append(x.charAt(i-1)).append("\t");
            for (int j=0; j < d[i].length; j++) {
                builder.append(d[i][j]).append('\t');
            }
            builder.append('\n');
        }
        System.out.println(builder);
    }
    
    public static void printAlignment(List<int[]> backtrace, CharSequence x, CharSequence y) {
        StringBuilder builder = new StringBuilder();
        for (int[] pair: backtrace) {
            builder.append(x.charAt(pair[0]-1));
        }
        
        builder = new StringBuilder();
        for (int[] pair: backtrace) {
            if (x.charAt(pair[0]-1) == y.charAt(pair[1]-1)) builder.append('|');
            else builder.append(' ');
        }
        
        builder = new StringBuilder();
        for (int[] pair: backtrace) {
            builder.append(y.charAt(pair[1]-1));
        }
    }
    public int getDistance(String word1 , String word2) {
		int [][] distanceMatrix = distanceMatrix(word1, word2, ONE, ONE, TWO);
		return getMinEditDistance(distanceMatrix);
	}

        
}