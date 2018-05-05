package daa.project.crvp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import daa.project.crvp.algorithms.GRASP;
import daa.project.crvp.local_seach.FirstBetterNeighborLocalSearch;
import daa.project.crvp.local_search.BestNeighborLocalSearch;
import daa.project.crvp.local_search.LocalSearch;
import daa.project.crvp.metrics.TimeAndIterationsRecorder;
import daa.project.crvp.moves.InterrouteSwap;
import daa.project.crvp.moves.IntrarouteSwap;
import daa.project.crvp.moves.Relocation;
import daa.project.crvp.moves.TwoOpt;
import daa.project.crvp.problem.CVRPSpecification;

public class GraspMetrics extends Thread {
    
    private final int MAX_NUM_ITERATIONS = 1000000;
    private final String FILE_PATH_PREFIX = AlgorithmMetrics.OUTPUT_DIR + "/grasp_results";
    private final String FILE_PATH_SUFIX = ".cvs";
    private final LocalSearch LOCAL_SEARCHES[] = { 
            new BestNeighborLocalSearch(new Relocation()),
            new BestNeighborLocalSearch(new InterrouteSwap()),
            new BestNeighborLocalSearch(new IntrarouteSwap()), 
            new BestNeighborLocalSearch(new TwoOpt()),
            new FirstBetterNeighborLocalSearch(new Relocation()),
            new FirstBetterNeighborLocalSearch(new InterrouteSwap()),
            new FirstBetterNeighborLocalSearch(new IntrarouteSwap()),
            new FirstBetterNeighborLocalSearch(new TwoOpt()), 
    };
    private final String LOCAL_SEARCHES_NAMES[] = { 
            "BN + Relocation", 
            "BN + Interroute", 
            "BN + IntrarouteSwap", 
            "BN + TwoOpt",
            "FBN + Relocation", 
            "FBN + InterrouteSwap", 
            "FBN + IntrarouteSwap", 
            "FBN + TwoOpt", 
    };
    
    private int rclSize;
    private int numIterationsWithNoImprovement;
    private CVRPSpecification[] problemSpecifications;
    private int numTests;
    private String filePath;

    public GraspMetrics(CVRPSpecification[] problemSpecifications, int numTests, int rclSize, int numIterationsWithNoImprovement) {
        super();
        this.problemSpecifications = problemSpecifications;
        this.numTests = numTests;
        this.rclSize = rclSize;
        this.numIterationsWithNoImprovement = numIterationsWithNoImprovement;
        this.filePath = FILE_PATH_PREFIX + "_rcl_" + rclSize + "_numIts_" + numIterationsWithNoImprovement + FILE_PATH_SUFIX;
    }
    
    @Override
    public void run() {
        super.run();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.filePath)), true)) {
            writer.append(getCsvHeader());
            
            for (int localSearchPos = 0; localSearchPos < LOCAL_SEARCHES.length; ++localSearchPos) {
                for (int i = 1; i <= numTests; ++i) {
                    writer.append("GRASP" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                            + this.rclSize + TimeAndIterationsRecorder.CSV_SEPARATOR 
                            + this.numIterationsWithNoImprovement + TimeAndIterationsRecorder.CSV_SEPARATOR 
                            + LOCAL_SEARCHES_NAMES[localSearchPos] + TimeAndIterationsRecorder.CSV_SEPARATOR 
                            + i + TimeAndIterationsRecorder.CSV_SEPARATOR
                    );
                    for (CVRPSpecification problemSpecification : problemSpecifications) {
                        TimeAndIterationsRecorder algorithmRecorder = new TimeAndIterationsRecorder();
                        GRASP.grasp(problemSpecification, MAX_NUM_ITERATIONS, this.numIterationsWithNoImprovement, this.rclSize, LOCAL_SEARCHES[localSearchPos], algorithmRecorder);
                        writer.append(algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR);
                        System.out.println("GRASP " 
                                + "RCL size: " + this.rclSize 
                                + " Num iterations no improvement: " + this.numIterationsWithNoImprovement 
                                + " " + LOCAL_SEARCHES_NAMES[localSearchPos] 
                                + " Test number: " + i
                                + " Recorder info: " + algorithmRecorder.toString() + TimeAndIterationsRecorder.CSV_SEPARATOR
                                );
                    }
                    writer.append("\n");
                    writer.flush();
                }
            }
            
            writer.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getCsvHeader() {
        StringBuilder writer = new StringBuilder();
        
        writer.append(TimeAndIterationsRecorder.CSV_SEPARATOR 
                + TimeAndIterationsRecorder.CSV_SEPARATOR
                + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + TimeAndIterationsRecorder.CSV_SEPARATOR
                + TimeAndIterationsRecorder.CSV_SEPARATOR
        );
        for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
            writer.append(AlgorithmMetrics.sampleNames[i].split("\\.")[0] 
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + TimeAndIterationsRecorder.CSV_SEPARATOR
            );
        }
        
        writer.append("ALGORITHM" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + "R.C.L" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + "I.W.I" + TimeAndIterationsRecorder.CSV_SEPARATOR
                + "L.S" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                + "ITERATION" + TimeAndIterationsRecorder.CSV_SEPARATOR
        );
        for (int i = 0; i < AlgorithmMetrics.NUM_SAMPLES; ++i) {
            writer.append("I.W.F" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + "T.N.O.I" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + "E.T.F" + TimeAndIterationsRecorder.CSV_SEPARATOR
                    + "T.E.T" + TimeAndIterationsRecorder.CSV_SEPARATOR 
                    + "SOL." + TimeAndIterationsRecorder.CSV_SEPARATOR
            );
        }
        
        return writer.toString();
    }
}
