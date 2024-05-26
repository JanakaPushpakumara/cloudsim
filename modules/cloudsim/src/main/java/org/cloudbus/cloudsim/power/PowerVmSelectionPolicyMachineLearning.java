package org.cloudbus.cloudsim.power;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.cloudbus.cloudsim.Vm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerVmSelectionPolicyMachineLearning extends PowerVmSelectionPolicy {

    private final int historyLength = 10;

    @Override
    public Vm getVmToMigrate(PowerHost host) {
        List<PowerVm> migratableVms = getMigratableVms(host);
        if (migratableVms.isEmpty()) {
            return null;
        }
        Vm vmToMigrate = null;
        double minMetric = Double.MAX_VALUE;
        for (PowerVm vm : migratableVms) {
            List<Double> cpuHistory = vm.getUtilizationHistory();
            List<Double> lastCpuHistory;

            if (cpuHistory.size() > historyLength) {
                lastCpuHistory = cpuHistory.subList(cpuHistory.size() - historyLength, cpuHistory.size());
            } else {
                lastCpuHistory = cpuHistory; // If there are less than historyLength elements, return the whole list
            }

            double predictedCpu = callPredictionServer( vm.getId(), lastCpuHistory );
            if (predictedCpu < minMetric) {
                minMetric = predictedCpu;
                vmToMigrate = vm;
            }
        }
        return vmToMigrate;
    }

    private double callPredictionServer(int vmId, List<Double> lastCpuHistory) {
        try {


            URL url = new URL("http://localhost:5000/utilization-prediction");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            Map<String, Object> data = new HashMap<>();
            data.put("vmid", vmId);
//          double time = CloudSim.clock(); // get the current simulation time
//          double cpuUtilization = vm.getTotalUtilizationOfCpuMips(time);
            double cpuUtilization = lastCpuHistory.get(lastCpuHistory.size() - 1);
            data.put("cpu", cpuUtilization);

            String input = new Gson().toJson(data);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            // Parse the JSON response
            JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
            return jsonObject.get("predictedCpu").getAsDouble();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return Double.MAX_VALUE;
    }
}
