package plot; /**
 * Created by NF on 9/3/2016.
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An example to show how we can create a dynamic chart.
 */
public class PltSeries extends ApplicationFrame {

    /**
     * The time series data.
     */
//    private TimeSeries series;

    /**
     * Timer to refresh graph after every 1/4th of a second
     */
//    private Timer timer = new Timer(250, this);

    /**
     * Constructs a new dynamic chart application.
     *
     * @param title the frame title.
     */
    private static int N = 5;
    private List<TimeSeries> timeSeriesList;

    private static PltSeries instance;

    public static PltSeries getInstance(){
        if(instance == null){
//            instance = new PltSeries("Sample");
            instance = new PltSeries("Dynamic Line And TimeSeries Chart");
            instance.pack();
            RefineryUtilities.centerFrameOnScreen(instance);
            instance.setVisible(true);
        }

        return instance;
    }

    public PltSeries(final String title) {

        super(title);
        timeSeriesList = new ArrayList<TimeSeries>();
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        for(int i=0;i<N;i++){
            TimeSeries ts = new TimeSeries("Line"+i,Millisecond.class);
            timeSeriesList.add(ts);
            dataset.addSeries(ts);
        }

//        this.series = new TimeSeries("Random Data", Millisecond.class);


        final JFreeChart chart = createChart(dataset);

//        timer.setInitialDelay(1000);

        //Sets background color of chart
        chart.setBackgroundPaint(Color.LIGHT_GRAY);

        //Created JPanel to show graph on screen
        final JPanel content = new JPanel(new BorderLayout());

        //Created Chartpanel for chart area
        final ChartPanel chartPanel = new ChartPanel(chart);

        //Added chartpanel to main panel
        content.add(chartPanel);

        //Sets the size of whole window (JPanel)
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));

        //Puts the whole content on a Frame
        setContentPane(content);

//        timer.start();

    }

    /**
     * Creates a sample chart.
     *
     * @param dataset the dataset.
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "Dynamic Line And TimeSeries Chart",
                "Time",
                "Value",
                dataset,
                true,
                true,
                false
        );

        final XYPlot plot = result.getXYPlot();

        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);

        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);

        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(60000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);

        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, 300.0);

        return result;
    }

//    /**
//     * Generates an random entry for a particular call made by time for every 1/4th of a second.
//     *
//     * @param e the action event.
//     */
//    public void actionPerformed(final ActionEvent e) {
//
//        final double factor = 0.9 + 0.2 * Math.random();
//        this.lastValue = this.lastValue * factor;
//
//        final Millisecond now = new Millisecond();
//        this.series.add(new Millisecond(), this.lastValue);
//
//        System.out.println("Current Time in Milliseconds = " + now.toString() + ", Current Value : " + this.lastValue);
//    }


    public void plotYt(double y, int pnum){

//        final double factor = 0.9 + 0.2 * Math.random();
//        this.lastValue = this.lastValue * factor;

        final Millisecond now = new Millisecond();
        timeSeriesList.get(pnum).add(new Millisecond(), y);
//        this.series.add(new Millisecond(), y);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println("Current Time in Milliseconds = " + now.toString() + ", Current Value : " + y);
    }

    /**
     * Starting point for the dynamic graph application.
     *
     * @param args ignored.
     */
    public static void main(final String[] args) {

        final PltSeries demo = new PltSeries("Dynamic Line And TimeSeries Chart");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
