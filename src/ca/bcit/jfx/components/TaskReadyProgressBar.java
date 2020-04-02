package ca.bcit.jfx.components;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.jfx.controllers.SimulationMenuController;
import ca.bcit.net.Network;
import ca.bcit.net.Simulation;
import ca.bcit.net.algo.IRMSAAlgorithm;
import ca.bcit.utils.LocaleUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskReadyProgressBar extends StackPane {
    private static ArrayList<String> resultsDataFileNameList = new ArrayList<String>();
    private static ArrayList<Integer> resultsDataSeedList = new ArrayList<Integer>();
    private ArrayList<JsonObject> resultsDataJsonList = new ArrayList<JsonObject>();

    public static String RESULTS_SUMMARY_DIR_NAME = "results summary";
    private static final String NO_SPECTRUM_BLOCKED_VOLUME_PERCENTAGE = "SBP";
    private static final String NO_REGENERATORS_BLOCKED_VOLUME_PERCENTAGE = "RBP";
    private static final String LINK_FAILURE_BLOCKED_VOLUME_PERCENTAGE = "LFBP";
    private static final String TOTAL_BLOCK_VOLUME_PERCENTAGE = "TBP";

    private final ProgressBar bar = new ProgressBar(0);
    private final Label label = new Label("");
    private ExecutorService runMultipleSimulationService;
    private int numSimulationsLeft = 0;
    private Thread thread;

    public String graphLabel;
    public String YLabel;
    public String XLabel;
    public boolean[] displayList;
    public ArrayList<String> excludedAlgos;

    public TaskReadyProgressBar() {
        super();
        getChildren().add(bar);
        getChildren().add(label);
        bar.minWidthProperty().bind(widthProperty());
        bar.minHeightProperty().bind(heightProperty());
    }

    private void bind(Task<?> task) {
        bar.progressProperty().bind(task.progressProperty()); // possible docking location for pause
        label.textProperty().bind(task.messageProperty());
    }

    private void unbind() {
        bar.progressProperty().unbind();
        label.textProperty().unbind();
    }

    public Thread getThread() {
        return this.thread;
    }

    public void runTask(Task<?> task, boolean daemon, SimulationMenuController controller) {
        bind(task);

        task.setOnSucceeded(e -> {
            controller.setMultipleSimulationsRan(false);
            controller.sendMail( LocaleUtils.translate("hello") + "\n\n" +
                    LocaleUtils.translate("good_news") + "\n\n" +
                    TaskReadyProgressBar.getResultsDataFileNameList() + "\n\n" +
                    LocaleUtils.translate("refer_to_docs") + " https://www.overleaf.com/read/fhttvdyjcngb.\n\n\n" +
                    LocaleUtils.translate("thank_you") + "\n\n" +
                    LocaleUtils.translate("ceons_team"));
            unbind();
        });

        task.setOnFailed(e -> {
            controller.setMultipleSimulationsRan(false);
            controller.sendMail( LocaleUtils.translate("hello") + "\n\n" +
                    LocaleUtils.translate("good_news") + "\n\n" +
                    TaskReadyProgressBar.getResultsDataFileNameList() + "\n\n" +
                    LocaleUtils.translate("bad_news") + "\n\n" +
                    e.getSource().toString() + " " + LocaleUtils.translate("failed") + "!\n\n" +
                    LocaleUtils.translate("refer_to_docs") + " https://www.overleaf.com/read/fhttvdyjcngb.\n\n\n" +
                    LocaleUtils.translate("thank_you") + "\n\n" +
                    LocaleUtils.translate("ceons_team"));
            unbind();
            Logger.debug(e.getSource().toString() + " " + LocaleUtils.translate("failed") + "!");
        });

        task.setOnCancelled(e -> {
            controller.setMultipleSimulationsRan(false);
            unbind();
            Logger.debug(e.getSource().toString() + " " + LocaleUtils.translate("was_cancelled") + "!");
        });
        this.thread = new Thread(task);
        thread.setDaemon(daemon);
        thread.start();
    }

    public void runTask(Task<?> task, boolean daemon) {
        bind(task);

        task.setOnSucceeded(e -> {
            unbind();
        });

        task.setOnFailed(e -> {
            unbind();
            Logger.debug(e.getSource().toString() + " " + LocaleUtils.translate("failed") + "!");
        });

        task.setOnCancelled(e -> {
            unbind();
            Logger.debug(e.getSource().toString() + " " + LocaleUtils.translate("was_cancelled") + "!");
        });
        this.thread = new Thread(task);
        thread.setDaemon(daemon);
        thread.start();
    }

    public void runTasks(ArrayList<IRMSAAlgorithm> algorithms, ArrayList<ArrayList> tasks, boolean daemon, ExecutorService runMultipleSimulationService, SimulationMenuController controller, Network network) {
        setRunMultipleSimulationService(runMultipleSimulationService);
        controller.setRunning(true);
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    this.updateProgress(0, tasks.size() * algorithms.size());
                    int count = 0;
                    for (IRMSAAlgorithm algorithm : algorithms) {
                        network.setDemandAllocationAlgorithm(algorithm);
                        for (ArrayList task : tasks) {
                            if (this.isCancelled())
                                break;
                            Logger.info("\n");
                            Logger.info(LocaleUtils.translate("starting_simulation") + "! " + "\n\t" + LocaleUtils.translate("simulation_parameter_seed") + ": " + task.get(1) + "\n\t" + LocaleUtils.translate("simulation_parameter_alpha") + ": " + task.get(2) + "\n\t" + LocaleUtils.translate("simulation_parameter_erlang") + ": " + task.get(3) +
                                    "\n\t" + LocaleUtils.translate("simulation_parameter_number_of_requests") + ": " + task.get(4) + "\n\t" + LocaleUtils.translate("simulation_parameter_replica_preservation") + ": " + task.get(5));
                            ((Simulation) task.get(0)).simulate((int) task.get(1), (int) task.get(4), (double) task.get(2), (int) task.get(3), (boolean) task.get(5));
                            Logger.info(LocaleUtils.translate("simulation_finished") + "!");
                            this.updateProgress(++count, tasks.size()*algorithms.size());
                            if (this.isCancelled())
                                break;
                        }
                    }
                }
                catch (Throwable e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        bind(task);
        task.setOnSucceeded(e -> {
            unbind();
            controller.setRunning(false);
            controller.setMultipleSimulationsRan(true);
            controller.sendMail( LocaleUtils.translate("hello") + "\n\n" +
                    LocaleUtils.translate("good_news") + "\n\n" +
                    TaskReadyProgressBar.getResultsDataFileNameList() + "\n\n" +
                    LocaleUtils.translate("refer_to_docs") + " https://www.overleaf.com/read/fhttvdyjcngb.\n\n\n" +
                    LocaleUtils.translate("thank_you") + "\n\n" +
                    LocaleUtils.translate("ceons_team"));
            runMultipleSimulationService.shutdown();
            try {
                if (!runMultipleSimulationService.awaitTermination(2500, TimeUnit.MILLISECONDS)) {
                    runMultipleSimulationService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                runMultipleSimulationService.shutdownNow();
            }
        });
        task.setOnFailed(e -> {
            controller.setRunning(false);
            controller.setMultipleSimulationsRan(false);
            controller.sendMail( LocaleUtils.translate("hello") + "\n\n" +
                    LocaleUtils.translate("good_news") + "\n\n" +
                    TaskReadyProgressBar.getResultsDataFileNameList() + "\n\n" +
                    LocaleUtils.translate("bad_news") + "\n\n" +
                    e.getSource().toString() + " " + LocaleUtils.translate("failed") + "!\n\n" +
                    LocaleUtils.translate("refer_to_docs") + " https://www.overleaf.com/read/fhttvdyjcngb.\n\n\n" +
                    LocaleUtils.translate("thank_you") + "\n\n" +
                    LocaleUtils.translate("ceons_team"));
            unbind();
            Logger.debug(e.getSource().toString() + " " + LocaleUtils.translate("failed") + "!");
        });
        task.setOnCancelled(e -> {
            controller.setRunning(false);
            controller.setMultipleSimulationsRan(false);
            unbind();
            Logger.debug(e.getSource().toString() + " " + LocaleUtils.translate("was_cancelled") + "!");
        });
        thread = new Thread(task);
        thread.setDaemon(daemon);
        runMultipleSimulationService.execute(thread);
    }

    public void initializePDFGen() {
        graphLabel = LocaleUtils.translate("report_blocked_volume_percentage_from_insufficient_resources");
        XLabel = LocaleUtils.translate("erlang");
        YLabel = LocaleUtils.translate("blocked_volume_percentage");
        displayList = new boolean[]{true,true,true,true};
        excludedAlgos = new ArrayList<>();
    }

    public void generatePDF() {

        File resultsSummaryDirectory = new File(RESULTS_SUMMARY_DIR_NAME);
        if (!resultsSummaryDirectory.isDirectory())
            resultsSummaryDirectory.mkdirs();

        // Write to PDF
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 1150, 550);
            Rectangle rectangle = new Rectangle(1150, 550);

            //Create dataset and chart
            JFreeChart chart = generateChart(graphLabel, YLabel, XLabel, displayList, excludedAlgos);
            chart.draw(pdfBoxGraphics2D, rectangle);

            pdfBoxGraphics2D.dispose();

            PDFormXObject appearanceStream = pdfBoxGraphics2D.getXFormObject();
            Matrix matrix = new Matrix();
            matrix.translate(0, 30);
            matrix.scale(0.7f, 1f);

            contentStream.saveGraphicsState();
            contentStream.transform(matrix);
            contentStream.drawForm(appearanceStream);
            contentStream.restoreGraphicsState();
            contentStream.close();

            document.save(resultsSummaryDirectory + File.separator + ApplicationResources.getProject().getName().toUpperCase() +
                    new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".pdf");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            RESULTS_SUMMARY_DIR_NAME = "results summary";
        }
    }

    public void clearData(){
        resultsDataFileNameList.clear();
        resultsDataSeedList.clear();
        resultsDataJsonList.clear();
    }

    public void readData() {
        for (String resultsDataFileName : resultsDataFileNameList)
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Simulation.RESULTS_DATA_DIR_NAME + "/" + ApplicationResources.getProject().getName().toUpperCase() + "/" + resultsDataFileName));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject js = gson.fromJson(bufferedReader, JsonObject.class);
                resultsDataJsonList.add(js);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                resultsDataFileNameList.clear();
                resultsDataJsonList.clear();
            }
    }

    public JFreeChart generateChart(String graphLabel, String YLabel, String XLabel, boolean[] displayList, ArrayList<String> excludedAlgos) {
        readData();
        double highestBlockPercentageVolume = 0;
        double lowestBlockPercentageVolume = 999999999;
        int lowestErlang = resultsDataJsonList.get(0).get("erlangValue").getAsInt();
        int highestErlang = resultsDataJsonList.get(resultsDataJsonList.size() - 1).get("erlangValue").getAsInt();
        int erlangStep = 20;
        boolean stepFound = false;
        int simulationsOnStep = 0;
        for (JsonObject b : resultsDataJsonList) {
            double tempBlockPercentage = b.get("totalBlockedVolumePercentage").getAsDouble();
            if (highestBlockPercentageVolume < tempBlockPercentage)
                highestBlockPercentageVolume = tempBlockPercentage;
            if (lowestBlockPercentageVolume > tempBlockPercentage)
                lowestBlockPercentageVolume = tempBlockPercentage;
            if (!stepFound) {
                simulationsOnStep++;
                int erlang = b.get("erlangValue").getAsInt();
                if (lowestErlang == erlang)
                    continue;
                erlangStep = erlang - lowestErlang;
                stepFound = true;
            }
        }
        XYDataset dataset = createDatasetXY(displayList, excludedAlgos);
        JFreeChart chart = createChartXY(dataset, graphLabel, YLabel, XLabel);
        chart.getXYPlot().getDomainAxis().setLowerBound(lowestErlang);
        chart.getXYPlot().getDomainAxis().setUpperBound(highestErlang);
        if (highestBlockPercentageVolume == 0) {
            chart.getXYPlot().getRangeAxis().setUpperBound(0.1);
            ((NumberAxis) chart.getXYPlot().getRangeAxis()).setTickUnit(new NumberTickUnit(0.01));
        } else {
            double roundedHighest = Math.ceil(highestBlockPercentageVolume * 10) / 10.0;
            chart.getXYPlot().getRangeAxis().setUpperBound(roundedHighest);
            ((NumberAxis) chart.getXYPlot().getRangeAxis()).setTickUnit(new NumberTickUnit(roundedHighest/10));
        }
        chart.getXYPlot().getRangeAxis().setLowerBound(0);
        ((NumberAxis) chart.getXYPlot().getDomainAxis()).setTickUnit(new NumberTickUnit(erlangStep));
        Font font = new Font("Helvetica", Font.BOLD, 12);
        chart.getXYPlot().getDomainAxis().setLabelFont(font);
        chart.getXYPlot().getRangeAxis().setLabelFont(font);
        chart.getTitle().setFont(font);
        return chart;
    }

    private void setRunMultipleSimulationService(ExecutorService runMultipleSimulationService) {
        this.runMultipleSimulationService = runMultipleSimulationService;
    }

    public ExecutorService getRunMultipleSimulationService() {
        return runMultipleSimulationService;
    }

    public void increaseSimulationCount() {
        numSimulationsLeft++;
    }

    public static void addResultsDataFileName(String resultsDataFileName) {
        resultsDataFileNameList.add(resultsDataFileName);
    }

    public static ArrayList<String> getResultsDataFileNameList() {
        return resultsDataFileNameList;
    }

    public static void addResultsDataSeed(Integer seedValue) {
        resultsDataSeedList.add(seedValue);
    }

    public static ArrayList<Integer> getResultsDataSeedList() {
        return resultsDataSeedList;
    }

    private XYDataset createDatasetXY(boolean[] displayList, ArrayList<String> excludedAlgos) {
        final XYSeriesCollection dataset = new XYSeriesCollection();
        HashMap<String, ArrayList<JsonObject>> algoList = new HashMap<>();
        for(int j = 0; j < resultsDataJsonList.size(); j++){
            String algorithm = resultsDataJsonList.get(j).get("algorithm").getAsString();
            if (excludedAlgos.contains(algorithm))
                continue;
            if (algoList.containsKey(algorithm)){
                algoList.get(algorithm).add(resultsDataJsonList.get(j));
            } else {
                ArrayList list = new ArrayList();
                list.add(resultsDataJsonList.get(j));
                algoList.put(algorithm, list);
            }
        }
        for (String algorithm: algoList.keySet()) {
            XYSeries series1 = new XYSeries(algorithm+" "+NO_SPECTRUM_BLOCKED_VOLUME_PERCENTAGE);
            XYSeries series2 = new XYSeries(algorithm+" "+NO_REGENERATORS_BLOCKED_VOLUME_PERCENTAGE);
            XYSeries series3 = new XYSeries(algorithm+" "+LINK_FAILURE_BLOCKED_VOLUME_PERCENTAGE);
            XYSeries series4 = new XYSeries(algorithm+" "+TOTAL_BLOCK_VOLUME_PERCENTAGE);
            int skipCount = 0;
            for (int i = 0; i < algoList.get(algorithm).size(); i++) {
                if (skipCount>0){ skipCount--;continue; }
                int erlangValue = 0;
                double noSpectrumBlockedVolumePercentage = 0.0;
                double noRegeneratorsBlockedVolumePercentage = 0.0;
                double linkFailureBlockedVolumePercentage = 0.0;
                double unhandledVolumePercentage = 0.0;
                double totalBlockedVolumePercentage = 0.0;
                double averageRegeneratiorsPerAllocation = 0.0;
                erlangValue = algoList.get(algorithm).get(i).get("erlangValue").getAsInt();

                // Multiple simulations per Erlang
                if (resultsDataSeedList.size() > 1) {
                    for (int j = 0; j < resultsDataSeedList.size(); j++) {
                        JsonObject resultsDataJson = algoList.get(algorithm).get(i + j);
                        noSpectrumBlockedVolumePercentage += resultsDataJson.get("noSpectrumBlockedVolumePercentage").getAsDouble();
                        noRegeneratorsBlockedVolumePercentage += resultsDataJson.get("noRegeneratorsBlockedVolumePercentage").getAsDouble();
                        linkFailureBlockedVolumePercentage += resultsDataJson.get("linkFailureBlockedVolumePercentage").getAsDouble();
                        unhandledVolumePercentage += resultsDataJson.get("unhandledVolumePercentage").getAsDouble();
                        totalBlockedVolumePercentage += resultsDataJson.get("totalBlockedVolumePercentage").getAsDouble();
                        averageRegeneratiorsPerAllocation += resultsDataJson.get("averageRegeneratiorsPerAllocation").getAsDouble();
                    }
                    skipCount = resultsDataSeedList.size();
                    noSpectrumBlockedVolumePercentage /= resultsDataSeedList.size();
                    noRegeneratorsBlockedVolumePercentage /= resultsDataSeedList.size();
                    linkFailureBlockedVolumePercentage /= resultsDataSeedList.size();
                    unhandledVolumePercentage /= resultsDataSeedList.size();
                    totalBlockedVolumePercentage /= resultsDataSeedList.size();
                    averageRegeneratiorsPerAllocation /= resultsDataSeedList.size();
                }
                else {
                    JsonObject resultsDataJson = algoList.get(algorithm).get(i);
                    noSpectrumBlockedVolumePercentage = resultsDataJson.get("noSpectrumBlockedVolumePercentage").getAsDouble();
                    noRegeneratorsBlockedVolumePercentage = resultsDataJson.get("noRegeneratorsBlockedVolumePercentage").getAsDouble();
                    linkFailureBlockedVolumePercentage = resultsDataJson.get("linkFailureBlockedVolumePercentage").getAsDouble();
                    unhandledVolumePercentage = resultsDataJson.get("unhandledVolumePercentage").getAsDouble();
                    totalBlockedVolumePercentage = resultsDataJson.get("totalBlockedVolumePercentage").getAsDouble();
                    averageRegeneratiorsPerAllocation = resultsDataJson.get("averageRegeneratiorsPerAllocation").getAsDouble();
                }
                series1.add(erlangValue, noSpectrumBlockedVolumePercentage);
                series2.add(erlangValue, noRegeneratorsBlockedVolumePercentage);
                series3.add(erlangValue, linkFailureBlockedVolumePercentage);
                series4.add(erlangValue, totalBlockedVolumePercentage);
            }
            if (displayList[0]) dataset.addSeries(series1);
            if (displayList[1]) dataset.addSeries(series2);
            if (displayList[2]) dataset.addSeries(series3);
            if (displayList[3]) dataset.addSeries(series4);
        }
        return dataset;
    }

    private JFreeChart createChartXY(final XYDataset dataset, final String title, final String yLabel, final String xLabel) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(title, // chart
                // title
                xLabel, // x axis label
                yLabel, // y axis lab
                dataset, // data
                PlotOrientation.VERTICAL, true, // include legend
                true, // tooltips
                false // urls
        );

        // OPTIONAL CUSTOMISATION OF THE CHART
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }
}