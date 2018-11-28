package ca.bcit;

import ca.bcit.io.Logger;
import ca.bcit.io.YamlSerializable;
import ca.bcit.io.project.EONProject;
import ca.bcit.io.project.EONProjectFileFormat;
import ca.bcit.io.project.Project;
import ca.bcit.io.project.ProjectFileFormat;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.jfx.tasks.SimulationTask;
import ca.bcit.net.*;
import ca.bcit.net.algo.AMRA;
import ca.bcit.net.demand.generator.AnycastDemandGenerator;
import ca.bcit.net.demand.generator.DemandGenerator;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.demand.generator.UnicastDemandGenerator;
import ca.bcit.utils.random.ConstantRandomVariable;
import ca.bcit.utils.random.IrwinHallRandomVariable;
import ca.bcit.utils.random.MappedRandomVariable;
import ca.bcit.utils.random.UniformRandomVariable;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.TimeStringConverter;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

//New Imports
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.sql.Timestamp;

public class Main extends Application {

	private static long seed = 0;
	private static int demandsCount = 100000;
	private static int erlang = 300;
	private static double alpha = 0;
	private static boolean replicaPreservation = false;
	private static List<TrafficGenerator> generators = new ArrayList<>();
	private static int i;
	private static int defaultRegenCount = 100;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("/ca/bcit/jfx/res/MainWindow.fxml"));
		GridPane root = (GridPane)loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
		
		final Canvas canvas = (Canvas) scene.lookup("#graph");
		BorderPane pane = (BorderPane) scene.lookup("#borderPane");
		canvas.widthProperty().bind(pane.widthProperty());
		canvas.heightProperty().bind(pane.heightProperty());
		System.out.println(canvas.getBoundsInParent());
//		canvas.getGraphicsContext2D().fillRect(10, 10, 20, 20);
//		canvas.setOnMouseDragged(e -> { canvas.getGraphicsContext2D().fillRect(e.getX(), e.getY(), 10, 10); });
	}
	
	public static void main(String[] args) {
		try {
			YamlSerializable.registerSerializableClass(NetworkNode.class);
			YamlSerializable.registerSerializableClass(NetworkLink.class);
			YamlSerializable.registerSerializableClass(Network.class);
			
			YamlSerializable.registerSerializableClass(MappedRandomVariable.class);
			YamlSerializable.registerSerializableClass(UniformRandomVariable.Generic.class);
			YamlSerializable.registerSerializableClass(ConstantRandomVariable.class);
			YamlSerializable.registerSerializableClass(IrwinHallRandomVariable.Integer.class);
			YamlSerializable.registerSerializableClass(UnicastDemandGenerator.class);
			YamlSerializable.registerSerializableClass(AnycastDemandGenerator.class);
			YamlSerializable.registerSerializableClass(TrafficGenerator.class);

			ProjectFileFormat.registerFileFormat(new EONProjectFileFormat());
			EONProjectFileFormat project = new EONProjectFileFormat();

			//Sets which topology to use
			File file = new File("euro28.eon"); //file to change
			Project eon = project.load(file);
			ApplicationResources.setProject(eon);

			//Load regenerators count
            //TODO change regenrator setting to read from input file
            Map<String, String> nodeData = new HashMap<String, String>();
            String line = "";

            File inputDir = new File("./INPUT");
            File regenFile = new File("./INPUT/regenerators.csv");
            BufferedReader br = null;
            int regenCount = defaultRegenCount;
            try{
                if (!inputDir.exists()) {
                    inputDir.mkdir();
                }
                if(!regenFile.exists()) {
                    System.out.println("Input regenerator csv file not found, setting regenerators count to default");
                } else {
                    try{
                        br = new BufferedReader(new FileReader(regenFile));
                        while(( line = br.readLine()) != null){
                            String [] nodeParam = line.split(",");
                            nodeData.put(nodeParam[0], nodeParam[1]);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        if (br != null){
                            try {
                                br.close();
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            //TODO load simulation settings
            //Load simulation settings
            File settingsFile = new File("./INPUT/settings.csv");
            Map<String, String> settingsData = new HashMap<String, String>();
            BufferedReader br2 = null;
            try{
                if(!settingsFile.exists()) {
                    System.out.println("Input settings csv file not found, using default values for the simulation.");
                } else {
                    try{
                        br2 = new BufferedReader(new FileReader(settingsFile));
                        while(( line = br2.readLine()) != null){
                            String [] simParam = line.split(",");
                            settingsData.put(simParam[0], simParam[1]);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        if (br2 != null){
                            try {
                                br2.close();
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }

			for (NetworkNode n: eon.getNetwork().getNodes()){

			    String nodeParamEntry = nodeData.get(n.getName());

			    if(nodeParamEntry != null){
			        regenCount = Integer.parseInt(nodeParamEntry);
                }

                n.setRegeneratorsCount(regenCount);
//				n.setRegeneratorsCount(100); //change number of regenerators
			}

			String erlangEntry = settingsData.get("erlang");
			String seedEntry = settingsData.get("seed");
			String demandEntry = settingsData.get("demand");
			if(erlangEntry != null)
			    erlang = Integer.parseInt(erlangEntry);
			if(seedEntry != null)
			    seed = Long.parseLong(seedEntry);
			if(demandEntry != null)
			    demandsCount = Integer.parseInt(demandEntry);

			generators = setupGenerators(eon);
			Network network = eon.getNetwork();

			//We're using AMRA
			network.setDemandAllocationAlgorithm(new AMRA()); //here to set algorithm

            //Sets the check box to change modulation and type to dynamic, should be constant for our purpose
			network.setCanSwitchModulation(true);
			network.setModualtionMetricType(MetricType.DYNAMIC);

			//Checks all modulation method check boxes, should be call chekced for our purpose
			for (Modulation modulation : Modulation.values()) {
				network.allowModulation(modulation);
			}

			//Set regenerator metric and type, should be constant no matter what for our purporse
			network.setRegeneratorMetricValue(5);
			network.setRegeneratorMetricType(MetricType.STATIC);

			//Creating new simulation and simulation task, should be one once every simulation
            //TODO needs to set simulation settings to the values we want before this point in the code
			Simulation simulation = new Simulation(network, generators.get(0));
			SimulationTask task = new SimulationTask(simulation, seed, alpha, erlang, demandsCount, replicaPreservation);

			//Calculate paths after loading network .eon file, needs to reset i to 1 everytime this is done
			i = 1;
			try {
				network.maxPathsCount = network.calculatePaths(() -> task.updateProgress(i++, network.getNodesPairsCount()));
			} catch (Throwable e) {
				e.printStackTrace();
			}

			//Sets best path count
			network.setBestPathsCount(10);

			//Pre simulation
            //TODO modify NetworkNode class to save additional information, and to write information for replica and international status,
            //TODO link count, link length sum, average link length for each NetworkNode instance
            //This is done in the method: EONProjectFileFormat.load

            //During simulation
            //TODO modify NetworkNode class to save additional regenerators information, and to update the status for maximum occupied count
            //TODO and average occupied count
            //This is done in the method: Network.update;
			simulation.simulate(seed, demandsCount, alpha, erlang, replicaPreservation,	task);

			//After simulation
            //TODO format and write network data to output file
            System.out.println("Writing to output file...");

            String outputFileName = "/output";
            String outputFileNumber = "";
            String outputFileExtension = ".csv";
            String outputDirectoryPath = "./OUTPUT";
            String outputFilePath = outputDirectoryPath + outputFileName + outputFileNumber +outputFileExtension;

            File outputDir = new File(outputDirectoryPath);
            File outputFile = new File(outputFilePath);

            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            //while(outputFile.exists()){
            //    if(outputFileNumber == ""){
            //        outputFileNumber = "1";
            //    } else {
            //        outputFileNumber = String.valueOf(Integer.parseInt(outputFileNumber) + 1);
            //    }
            //    outputFilePath = outputDirectoryPath + outputFileName + outputFileNumber +outputFileExtension;
            //    outputFile = new File(outputFilePath);
            //}
			
			if(outputFile.exists()){
				outputFile.delete();
				outputFile = new File(outputFilePath);
			}

            String settingsHeader = "START_OF_SETTINGS";
            String settingsFooter = System.lineSeparator() + "END_OF_SETTINGS";
            String dataHeader = System.lineSeparator() + "START_OF_NODE_DATA";
            String dataFooter = System.lineSeparator() + "END_OF_DATA";
            String dateLable = System.lineSeparator() + "CREATED_ON:";

            FileWriter outputFileWriter = new FileWriter(outputFile, true);

            String dataColName = "name,isReplica,isInternational,MaxOccupiedReg,AvgOccupeidReg,LinkCount,SumLinkLength,AvgLinkLength";
            outputFileWriter.append(dataColName);
            for(NetworkNode n : network.getNodes()){
                String nodeState = System.lineSeparator() +
                        n.getName() + "," +
                        n.getReplicaStatus() + "," +
                        n.getInternationalStatus() + "," +
                        n.getMaxOccupiedRegenerators() + "," +
                        n.getAvgOccupiedRegCount() + "," +
                        n.getLinkCount() + "," +
                        n.getSumLinkLength() + "," +
                        n.getAvgLinkLength();

                outputFileWriter.append(nodeState);
            }
            outputFileWriter.append(dataFooter);
            String simSettings = System.lineSeparator() +
                    "Erlang: " + erlang +
                    ", Seed_Value: " + seed +
                    ", Demands_Count: "+ demandsCount;
            String separatorRegion = System.lineSeparator();

//            outputFileWriter.append(settingsHeader);
//            outputFileWriter.append(simSettings);
//            outputFileWriter.append(settingsFooter);
//            outputFileWriter.append(separatorRegion);
//            outputFileWriter.append(dataHeader);
            outputFileWriter.append(simSettings);
            outputFileWriter.append(separatorRegion);

            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);

            outputFileWriter.append(dateLable);
            outputFileWriter.append(ts.toString());
            outputFileWriter.close();

            System.out.println("Finished writing to output file...");

            //Acknowledges that simulation is done and exits the simulator
			System.out.println("Simulation done...");
			System.exit(0);

//			below is GUI code
//			ProjectFileFormat.registerFileFormat(new EONProjectFileFormat());
//			launch(args);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fatal error occured: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private static List<TrafficGenerator> setupGenerators(Project project) {
		Network network = project.getNetwork();
		List<TrafficGenerator> generators = project.getTrafficGenerators();

		List<MappedRandomVariable.Entry<DemandGenerator<?>>> subGenerators = new ArrayList<>();

		subGenerators.add(new MappedRandomVariable.Entry<>(29, new AnycastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new ConstantRandomVariable<>(false),
				new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 210, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(18, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(11, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("replicas")), new UniformRandomVariable.Generic<>(network.getGroup("replicas")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(40, 410, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getNodes()), new UniformRandomVariable.Generic<>(network.getGroup("international")),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));
		subGenerators.add(new MappedRandomVariable.Entry<>(21, new UnicastDemandGenerator(new UniformRandomVariable.Generic<>(network.getGroup("international")), new UniformRandomVariable.Generic<>(network.getNodes()),
				new ConstantRandomVariable<>(false), new ConstantRandomVariable<>(false), new UniformRandomVariable.Integer(10, 110, 10), new ConstantRandomVariable<>(1f))));

		//Sets traficgenerator to new backup, should be constant for our purpose
		generators.add(new TrafficGenerator("No_Backup", new MappedRandomVariable<>(subGenerators)));

		return generators;
	}
}
