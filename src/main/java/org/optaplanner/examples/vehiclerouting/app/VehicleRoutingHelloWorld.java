package org.optaplanner.examples.vehiclerouting.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingFileIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class VehicleRoutingHelloWorld {
    private static Logger logger = LoggerFactory.getLogger(VehicleRoutingHelloWorld.class);
    private static String SOLVER_CONFIG = "org/optaplanner/examples/vehiclerouting/solver/vehicleRoutingSolverConfig.xml";
    public static void main(String[] args) {
        File file = new File("data/vehiclerouting/import/belgium/timewindowed/air/belgium-tw-n50-k10.vrp");

        VehicleRoutingFileIO fileIO = new VehicleRoutingFileIO();
        VehicleRoutingSolution unSolvedSolution = fileIO.read(file);
        for (Vehicle vehicle : unSolvedSolution.getVehicleList()){
            logger.info(vehicle.toString());
        }

        SolverFactory<VehicleRoutingSolution> solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Solver<VehicleRoutingSolution> solver = solverFactory.buildSolver();

        solver.solve(unSolvedSolution);

    }

}
