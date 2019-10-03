package ca.bcit.jfx.components;

import ca.bcit.ApplicationResources;
import ca.bcit.io.Logger;
import ca.bcit.net.Simulation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.rototor.pdfbox.graphics2d.PdfBoxGraphics2D;
import javafx.concurrent.Task;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.jfree.chart.axis.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class TaskReadyProgressBar extends StackPane {
    private static ArrayList<String> resultsDataFileNameList = new ArrayList<String>();
    private static ArrayList<Integer> resultsDataSeedList = new ArrayList<Integer>();
    private ArrayList<JsonObject> resultsDataJsonList = new ArrayList<JsonObject>();

    private static final String RESULTS_SUMMARY_DIR_NAME = "results summary";
    private static final String NO_SPECTRUM_BLOCKED_VOLUME_PERCENTAGE = "SBP";
    private static final String NO_REGENERATORS_BLOCKED_VOLUME_PERCENTAGE = "RBP";
    private static final String LINK_FAILURE_BLOCKED_VOLUME_PERCENTAGE = "LFBP";

    private final ProgressBar bar = new ProgressBar();
    private final Label label = new Label("");
    private ExecutorService runMultipleSimulationService;
    private int numSimulationsLeft = 0;

    public TaskReadyProgressBar() {
        super();
        getChildren().add(bar);
        getChildren().add(label);
        bar.setVisible(false);
        bar.minWidthProperty().bind(widthProperty());
        bar.minHeightProperty().bind(heightProperty());
    }

    private void bind(Task<?> task) {
        bar.setVisible(true);
        bar.progressProperty().bind(task.progressProperty()); // possible docking location for pause
        label.textProperty().bind(task.messageProperty());
    }

    private void unbind() {
        bar.setVisible(false);
        bar.progressProperty().unbind();
        label.textProperty().unbind();
    }

    public void runTask(Task<?> task, boolean daemon, ResourceBundle resources) {
        bind(task);

        task.setOnSucceeded(e -> {
            unbind();
        });

        task.setOnFailed(e -> {
            unbind();
            Logger.debug(e.getSource().toString() + " " + resources.getString("failed") + "!");
        });

        task.setOnCancelled(e -> {
            unbind();
            Logger.debug(e.getSource().toString() + " " + resources.getString("was_cancelled") + "!");
        });

        Thread thread = new Thread(task);
        thread.setDaemon(daemon);
        thread.start();
    }

    public void runTask(Task<?> task, boolean daemon, ExecutorService runMultipleSimulationService, ResourceBundle resources) {
        bind(task);
        setRunMultipleSimulationService(runMultipleSimulationService);
        task.setOnSucceeded(e -> {
            unbind();
            numSimulationsLeft--;
            if (numSimulationsLeft == 0) {
                runMultipleSimulationService.shutdown();

                try {
                    if (!runMultipleSimulationService.awaitTermination(2500, TimeUnit.MILLISECONDS))
                        runMultipleSimulationService.shutdownNow();
                }
                catch (InterruptedException ex) {
                    runMultipleSimulationService.shutdownNow();
                }

                // Extract JSON data
                for (String resultsDataFileName : resultsDataFileNameList) {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(Simulation.RESULTS_DATA_DIR_NAME + "/" + resultsDataFileName));

                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        JsonObject js = gson.fromJson(bufferedReader, JsonObject.class);

                        resultsDataJsonList.add(js);

                        System.out.println(js.getAsJsonObject());
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                        resultsDataFileNameList.clear();
                        resultsDataJsonList.clear();
                    }
                }

                File resultsSummaryDirectory = new File(RESULTS_SUMMARY_DIR_NAME);

                if (!resultsSummaryDirectory.isDirectory())
                    resultsSummaryDirectory.mkdir();

                // Write to PDF
                try (PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage();
                    document.addPage(page);
                    PDFont font = PDType1Font.HELVETICA_BOLD;

                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.beginText();
                    contentStream.setFont(font, 12);
                    contentStream.newLineAtOffset(150, 750);
                    contentStream.showText(resources.getString("simulation_summary_label"));

                    contentStream.endText();

                    PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(document, 800, 400);
                    Rectangle rectangle = new Rectangle(800, 400);

                    //Create dataset and chart
                    XYDataset dataset = createDatasetXY();
                    JFreeChart chart = createChartXY(dataset, resources);
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

                    //Get simulator settings
                    String trafficGeneratorName = resultsDataJsonList.get(0).get("trafficGeneratorName").getAsString();
                    int startingErlangValue = resultsDataJsonList.get(0).get("erlangValue").getAsInt();
                    int endingErlangValue = resultsDataJsonList.get(resultsDataJsonList.size()-1).get("erlangValue").getAsInt();
                    String seedValuesGlob = "";
                    if(resultsDataSeedList.size() > 1)
                        for(int i = 0; i < resultsDataSeedList.size(); i++)
                            if(i < resultsDataSeedList.size() - 1)
                                seedValuesGlob += resultsDataSeedList.get(i) + ",";
                            else
                                seedValuesGlob += resultsDataSeedList.get(i);
                    else
                        seedValuesGlob = resultsDataJsonList.get(0).get("seedValue").getAsString();

                    double alphaValue = resultsDataJsonList.get(0).get("alphaValue").getAsDouble();
                    int demandsCountValue = resultsDataJsonList.get(0).get("demandsCountValue").getAsInt();

                    document.save(resultsSummaryDirectory + "\\" + ApplicationResources.getProject().getName().toUpperCase() + "-" + trafficGeneratorName
                            + "-ERLANG_" + startingErlangValue + "_TO_" + endingErlangValue + "-SEED{" + seedValuesGlob + "}-ALPHA" + alphaValue + "-DEMANDS" + demandsCountValue + ".pdf");
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                finally {
                    resultsDataFileNameList.clear();
                    resultsDataSeedList.clear();
                    resultsDataJsonList.clear();
                }
            }
        });

        task.setOnFailed(e -> {
            unbind();
            Logger.debug(e.getSource().toString() + " " + resources.getString("failed") + "!");
        });

        task.setOnCancelled(e -> {
            unbind();
            Logger.debug(e.getSource().toString() + " " + resources.getString("was_cancelled") + "!");
        });

        //Start the thread task execution
        Thread thread = new Thread(task);
        thread.setDaemon(daemon);
        runMultipleSimulationService.execute(thread);
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

    public static ArrayList<String> getResultsDataFileNameList(){
        return resultsDataFileNameList;
    }

    public static void addResultsDataSeed(Integer seedValue){
        resultsDataSeedList.add(seedValue);
    }

    public static ArrayList<Integer> getResultsDataSeedList(){
        return resultsDataSeedList;
    }

    private XYDataset createDatasetXY(){
        XYSeries series1 = new XYSeries(NO_SPECTRUM_BLOCKED_VOLUME_PERCENTAGE);
        XYSeries series2 = new XYSeries(NO_REGENERATORS_BLOCKED_VOLUME_PERCENTAGE);
        XYSeries series3 = new XYSeries(LINK_FAILURE_BLOCKED_VOLUME_PERCENTAGE);

        int simulationsInErlangRange = resultsDataJsonList.size()/resultsDataSeedList.size();

        for (int i = 0; i < simulationsInErlangRange; i++) {
            int erlangValue = 0;
            double noSpectrumBlockedVolumePercentage = 0.0;
            double noRegeneratorsBlockedVolumePercentage = 0.0;
            double linkFailureBlockedVolumePercentage = 0.0;
            double unhandledVolumePercentage = 0.0;
            double totalBlockedVolumePercentage = 0.0;
            double averageRegeneratiorsPerAllocation = 0.0;
            erlangValue = resultsDataJsonList.get(i).get("erlangValue").getAsInt();

            // Multiple simulations per Erlang
            if (resultsDataSeedList.size() > 1)
                for(int j = 0; j < resultsDataSeedList.size(); j++){
                    JsonObject resultsDataJson = resultsDataJsonList.get(i + (j*simulationsInErlangRange));
                    System.out.println(resultsDataJson.get("noRegeneratorsBlockedVolumePercentage").getAsDouble());
                    noSpectrumBlockedVolumePercentage += resultsDataJson.get("noSpectrumBlockedVolumePercentage").getAsDouble();
                    noRegeneratorsBlockedVolumePercentage += resultsDataJson.get("noRegeneratorsBlockedVolumePercentage").getAsDouble();
                    linkFailureBlockedVolumePercentage += resultsDataJson.get("linkFailureBlockedVolumePercentage").getAsDouble();
                    unhandledVolumePercentage += resultsDataJson.get("unhandledVolumePercentage").getAsDouble();
                    totalBlockedVolumePercentage += resultsDataJson.get("totalBlockedVolumePercentage").getAsDouble();
                    averageRegeneratiorsPerAllocation += resultsDataJson.get("averageRegeneratiorsPerAllocation").getAsDouble();
                }
            else {
                JsonObject resultsDataJson = resultsDataJsonList.get(i);
                noSpectrumBlockedVolumePercentage = resultsDataJson.get("noSpectrumBlockedVolumePercentage").getAsDouble();
                System.out.println(resultsDataJson.get("noRegeneratorsBlockedVolumePercentage").getAsDouble());
                noRegeneratorsBlockedVolumePercentage = resultsDataJson.get("noRegeneratorsBlockedVolumePercentage").getAsDouble();
                linkFailureBlockedVolumePercentage = resultsDataJson.get("linkFailureBlockedVolumePercentage").getAsDouble();
                unhandledVolumePercentage = resultsDataJson.get("unhandledVolumePercentage").getAsDouble();
                totalBlockedVolumePercentage = resultsDataJson.get("totalBlockedVolumePercentage").getAsDouble();
                averageRegeneratiorsPerAllocation = resultsDataJson.get("averageRegeneratiorsPerAllocation").getAsDouble();
            }
            series1.add( erlangValue, noSpectrumBlockedVolumePercentage );
            series2.add( erlangValue, noRegeneratorsBlockedVolumePercentage );
            series3.add( erlangValue, linkFailureBlockedVolumePercentage );
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        return dataset;
    }

    private JFreeChart createChartXY(final XYDataset dataset, ResourceBundle resources) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(resources.getString("report_blocked_volume_percentage_from_insufficient_resources_vs_erlangs"), // chart
                // title
                resources.getString("erlang"), // x axis label
                resources.getString("report_blocked_volume_percentage"), // y axis label
                dataset, // data
                PlotOrientation.VERTICAL, true, // include legend
                true, // tooltips
                false // urls
        );

        // OPTIONAL CUSTOMISATION OF THE CHART
        chart.setBackgroundPaint(Color.white);

        // final StandardLegend legend = (StandardLegend) chart.getLegend();
        // legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setSeriesLinesVisible(0, false);
//        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        return chart;
    }
}