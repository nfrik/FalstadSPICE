package plot; /**
 * Created by NF on 9/3/2016.
 */

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Random;


public class PltXYScatter extends JFrame {

    private static final int N = 8;
    private static final String title = "Scatter Add Demo";
    private static final Random rand = new Random();
    private XYSeries added = new XYSeries("Added");

    private static PltXYScatter instance;

    public static PltXYScatter getInstance(){
        if(instance==null){
            instance = new PltXYScatter(title);
            instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            instance.pack();
            instance.setLocationRelativeTo(null);
            instance.setVisible(true);
        }

        return instance;
    }


    public PltXYScatter(String s) {
        super(s);
        final ChartPanel chartPanel = createDemoPanel();
        this.add(chartPanel, BorderLayout.CENTER);
//        JPanel control = new JPanel();
//        control.add(new JButton(new AbstractAction("Add") {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                for (int i = 0; i < N; i++) {
//                    added.add(rand.nextGaussian(), rand.nextGaussian());
//                }
//            }
//        }));
//        this.add(control, BorderLayout.SOUTH);
    }

    public void plotXY(double x, double y){
        this.added.add(x,y);
    }

    private ChartPanel createDemoPanel() {
        JFreeChart jfreechart = ChartFactory.createScatterPlot(
                title, "X", "Y", createSampleData(),
                PlotOrientation.VERTICAL, true, true, false);

        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        domain.setVerticalTickLabels(true);
        return new ChartPanel(jfreechart);
    }

    public XYDataset createSampleData() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Random");
        for (int i = 0; i < 0; i++) {
            double x = rand.nextGaussian();
            double y = rand.nextGaussian();
            series.add(x, y);
        }
        xySeriesCollection.addSeries(series);
        xySeriesCollection.addSeries(added);
        return xySeriesCollection;
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                PltXYScatter demo = new PltXYScatter(title);
                demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                demo.pack();
                demo.setLocationRelativeTo(null);
                demo.setVisible(true);
            }
        });
    }
}