package org.cloudbus.cloudsim.examples.power.planetlab;

import java.io.IOException;

/**
 * A simulation of a heterogeneous power aware data center that applies the Local Regression (LR) VM
 * allocation policy and Machine learning (ML) VM selection policy.
 * 
 * This example uses a real PlanetLab workload: 20110303.
 * 
 * The remaining configuration parameters are in the Constants and PlanetLabConstants classes.
 * 
 * @author Janaka Pushpakumara
 * @since Jan 5, 2012
 */
public class LrMl {

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		boolean enableOutput = true;
		boolean outputToFile = false;
		String inputFolder = LrMl.class.getClassLoader().getResource("workload/planetlab").getPath();
		String outputFolder = "output";
		String workload = "20110303"; // PlanetLab workload
		String vmAllocationPolicy = "lr"; // Local Regression (LR) VM allocation policy
		String vmSelectionPolicy = "ml"; // Maximum Correlation (MC) VM selection policy
		String parameter = "1.2"; // the safety parameter of the LR policy

		new PlanetLabRunner(
				enableOutput,
				outputToFile,
				inputFolder,
				outputFolder,
				workload,
				vmAllocationPolicy,
				vmSelectionPolicy,
				parameter);
	}

}
