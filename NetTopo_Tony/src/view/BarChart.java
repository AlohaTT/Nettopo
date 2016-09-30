package view;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChart {
	public static void PaintChart(String key0,String val0) throws IOException {
		// 创建一个柱状图
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		ReadResult rr=new ReadResult();
		Map<Integer,Integer> data=rr.readPathNumber(key0,val0);
		 Iterator<Entry<Integer, Integer>> iter = data.entrySet().iterator();
		 while (iter.hasNext()) {
		 @SuppressWarnings("rawtypes")
		Map.Entry entry = (Map.Entry) iter.next();
		 int key = (int) entry.getKey();
		 int val = (int) entry.getValue();
		 dataset.setValue(val, "50", key+"");
		 }
		
		// 产生柱状图
		// JFreeChart chart =
		// ChartFactory.createXYLineChart("标题","x轴标志","y轴标志","设置数据","设置图形显示方向",是否显示图形,是否进行提示,是否配置报表存放地址);
		// 图形显示方向：
		// (1)HORIZONTAL:横向显示图形
		// (2)VERTICAL:纵向显示图形
		// 3D柱状图
		JFreeChart chart = ChartFactory.createBarChart("销售统计图", "数值", "时间",
				dataset, PlotOrientation.VERTICAL, true, true, false);
		

		// 设置总的背景颜色
		chart.setBackgroundPaint(ChartColor.WHITE);
		// 设置标题颜色
		chart.getTitle().setPaint(ChartColor.black);
		// 获得图表对象
		CategoryPlot p = chart.getCategoryPlot();
		// 设置图的背景颜色
		p.setBackgroundPaint(ChartColor.WHITE);
		
		p.setRangeGridlinesVisible(false);
		// 设置表格线颜色
		p.setRangeGridlinePaint(ChartColor.red);
		try {
			// // 创建图形显示面板
			// ChartFrame cf = new ChartFrame("柱状图",chart);
			// cf.pack();
			// // 设置图片大小
			// cf.setSize(500,300);
			// // 设置图形可见
			// cf.setVisible(true);

			// 保存图片到指定文件夹
			ChartUtilities.saveChartAsPNG(new File("./BarChart.png"), chart,
					500, 300);

		} catch (Exception e) {
			System.err.println("Problem occurred creating chart.");
		}
	}
}
