/*
 *  XADDHelper.hava 
 * 
 * NOTES:
 *          1. This Class contains modified functions from xadd.XADD.java
 */
package cpomdp.market.utils;

import graph.Graph;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import camdp.CAMDP.FileOptions;
import xadd.ExprLib.ArithExpr;
import xadd.XADD;
import xadd.XADDUtils;
import xadd.XADD.BoolDec;

/**
 * Contains a selection of methods from XADD.java
 * 
 * @author 	Shamin Kinathil
 * @since	
 *
 */
public class XADDHelper {

    private static Runtime RUNTIME = Runtime.getRuntime();
    private static XADD CONTEXT = null;
    
    public static Integer COUNTER = 0;

    /* Cache maintenance */
    public final static boolean ALWAYS_FLUSH = false; // Always flush DD caches?
    public final static double FLUSH_PERCENT_MINIMUM = 0.3d; // Won't flush until < amt
   
    /* For printing */
    public static DecimalFormat _df = new DecimalFormat("#.########");
    
    /**
     * Sets an XADD object ...
     * 
     * @param xaddInstance
     */
    public static void setXADDInstance(XADD xaddInstance) {

        if (XADDHelper.CONTEXT == null) {
        	XADDHelper.CONTEXT = xaddInstance;
        }
        
        System.out.println(XADDHelper.getXADD());
    }
        
    /**
     * 
     * 
     * @return	XADD instance
     */
    private static XADD getXADD() {
//    	System.out.println("In XADDHelper.getXADD()");    	
//    	System.out.println(XADDHelper.CONTEXT);
    	
        if (XADDHelper.CONTEXT == null) {
            System.out.println("\nXADD uninitialised\n");
            System.exit(1);
        }
        
        return XADDHelper.CONTEXT;
    }

    /*-------------------------------------------------------------------------
     * XADD related functions 
     *-----------------------------------------------------------------------*/
    
    public static int Apply(Integer xadd1, Integer xadd2, Integer operation) {
    	return XADDHelper.getXADD().apply(xadd1, xadd2, operation);
    }
    
    public static int ZeroXADD() {
    	return XADDHelper.getXADD().ZERO;
    }
    
    public static int NegInfXADD() {
    	return XADDHelper.getXADD().NEG_INF;
    }
    
    public static int NewBoolDec(String variableName) {
    	return XADDHelper.getXADD().getVarIndex(XADDHelper.getXADD().new BoolDec(variableName), false);
    }
    
    /**
     * Build and XADD from a file
     * 
     * @param fileLocation	
     * @return	XADD instance built according to the file specification
     */
    public static int BuildXADD(String fileLocation) {

        File specFile = new File(fileLocation);
        if (!(specFile.exists() && specFile.isFile() && specFile.canRead())) {
            System.out.println("\nError: Unable to locate readable file: '" + "'" + specFile.toString());
        }

        return XADDHelper.getXADD().buildCanonicalXADDFromFile(specFile.toString());
    }

    /**
     * 
     * @param xaddID
     * @param plotTitle
     */
    public static void PlotXADD(int xaddID, String plotTitle) {
        
        try {
            Graph gc = XADDHelper.getXADD().getGraph(xaddID);
            gc.launchViewer(plotTitle);
        
        } catch (IllegalArgumentException e) {
            System.err.println("XADDHelper.PlotXADD Caught Exception: " + plotTitle 
                    + "\n"+ e.getMessage());
        }
    }

    /**
     * 
     * @param xadd_id
     * @param label
     * @param problemFile
     * @param logFileRoot
     */
    public static void ExportXADD(int xadd_id, String label, String problemFile,
                                                            String logFileRoot) {

        label = label.replace(".csamdp", "").replace(".camdp", "")
                .replace(".cmdp", "").replace('^', '_').replace("(", "")
                .replace(")", "").replace(":", "_").replace(" ", "");

        String xadd_filename = logFileRoot + "." + label + ".xadd";
        XADDHelper.getXADD().exportXADDToFile(xadd_id, xadd_filename);

        // Copy over plotting options if they exist
        File file2D = new File(problemFile + ".2d");
        if (file2D.exists()) {
            FileOptions opt = new FileOptions(problemFile + ".2d");
            opt.exportToFile(xadd_filename + ".2d");
        }

        File file3D = new File(problemFile + ".3d");
        if (file3D.exists()) {
            FileOptions opt = new FileOptions(problemFile + ".3d");
            opt.exportToFile(xadd_filename + ".3d");
        }
    }

    /**
     * 
     * @param xadd_id
     * @param label
     * @param problemFile
     * @param logFileRoot
     */
    public static void Display2D(int xadd_id, String label, String problemFile, 
                                                            String logFileRoot) {

        XADDHelper.ExportXADD(xadd_id, label, problemFile, logFileRoot);

        // If DISPLAY_2D is enabled, it is expected that necessary parameters
        // have been placed in a _problemFile + ".2d"
        FileOptions opt = new FileOptions(problemFile + ".2d");

        XADDUtils.PlotXADD(XADDHelper.getXADD(), xadd_id, opt._varLB.get(0),
                opt._varInc.get(0), opt._varUB.get(0), opt._bassign,
                opt._dassign, opt._var.get(0), logFileRoot + "." + label);
    }

    /**
     * 
     * @param xadd_id
     * @param label
     * @param problemFile
     * @param logFileRoot
     */
    public static void Display3D(int xadd_id, String label, String problemFile, 
                                                            String logFileRoot) {

        // If DISPLAY_3D is enabled, it is expected that necessary parameters 
        // have been placed in a _problemFile + ".3d"
        FileOptions opt = new FileOptions(problemFile + ".3d");
        
        XADDUtils.Plot3DSurfXADD(XADDHelper.getXADD(), xadd_id, 
                opt._varLB.get(0), opt._varInc.get(0), opt._varUB.get(0), 
                opt._varLB.get(1), opt._varInc.get(1), opt._varUB.get(1), 
                opt._bassign, opt._dassign, opt._var.get(0), opt._var.get(1), 
                logFileRoot + "." + label);
    }    
    
    /**
     * 
     * @param special_nodes
     * @param forceFlush
     */
    public static void FlushCaches(List<Integer> special_nodes, boolean forceFlush) {

        if (((double) RUNTIME.freeMemory() /
                (double) RUNTIME.totalMemory()) > FLUSH_PERCENT_MINIMUM && !forceFlush) {
            System.out.println("No need to flush caches.");
            return; // Still enough free mem to exceed minimum requirements
        }

        // Commence cache flushing
        System.out.println("Before flush: " + XADDHelper.getXADD()._hmInt2Node.size() + " XADD nodes in use, " + "freeMemory: " +
                _df.format(RUNTIME.freeMemory() / 10e6d) + " MB = " +
                _df.format(100d * RUNTIME.freeMemory() / (double) RUNTIME.totalMemory()) + "% available memory");

        XADDHelper.getXADD().clearSpecialNodes();
        
        for (Integer node : special_nodes)
            XADDHelper.getXADD().addSpecialNode(node);

        XADDHelper.getXADD().flushCaches();

        System.out.println("After flush: " + XADDHelper.getXADD()._hmInt2Node.size() + " XADD nodes in use, " + "freeMemory: " +
                _df.format(RUNTIME.freeMemory() / 10e6d) + " MB = " +
                _df.format(100d * RUNTIME.freeMemory() / (double) RUNTIME.totalMemory()) + "% available memory");
    }

    /**
     * 
     * @param valueXADD
     * @param stateSetMap
     * @return
     */
	public static int substitute(Integer node_id, HashMap<String, ArithExpr> subst) {
		return XADDHelper.getXADD().substitute(node_id, subst);
	}

	/**
	 * 
	 * @param aID
	 * @param discountFactor
	 * @param prod
	 * @return
	 */
	public static Integer ScalarOp(Integer dd, Double val, int op) {
		// TODO Auto-generated method stub
		return XADDHelper.getXADD().scalarOp(dd, val, op);
	}
}
