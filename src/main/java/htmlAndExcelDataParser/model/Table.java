package htmlAndExcelDataParser.model;

import htmlAndExcelDataParser.dataStructure.Data;
import htmlAndExcelDataParser.dataStructure.FileDesDate;
import htmlAndExcelDataParser.dataStructure.NumericData;
import htmlAndExcelDataParser.dataStructure.StringData;
import htmlAndExcelDataParser.dataStructure.UnitData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Table {
	private HashMap<Point, CellData> cells;
	private HashMap<Point, CellData> unitCells;
	private int width;
	private int length;
	private FileDesDate fileDesDate;
	public void setUnitCells(HashMap<Point, CellData> unitCells) {
		this.unitCells = unitCells;
	}
	public Table() {
		setCells(new HashMap<Point, CellData>());
		setUnitCells(new HashMap<Point, CellData>());
		width = 0;
		length = 0;
		fileDesDate = null;
	}

	public Table(HashMap<Point, CellData> cells) {
		this.setCells(cells);
	}

	public HashMap<Point, CellData> getCells() {
		return cells;
	}

	public void setCells(HashMap<Point, CellData> cells) {
		this.cells = cells;
	}

	public int getLength() {
		return length;
	}

	public int getWidth() {
		return width;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setFileDesDate(FileDesDate fileDesDate) {
		this.fileDesDate = fileDesDate;
	}

	public FileDesDate getFileDesDate() {
		return fileDesDate;
	}
	public void fillInUnitCells()
	{
		for (Entry<Point, CellData> cell : cells.entrySet())
		{
			if (cell.getValue().getDataContent() instanceof StringData)
			{
				StringData contStr = (StringData) cell.getValue().getDataContent();
//				if(contStr.toString().equals("旅客周转量（万人公里）"))
//					System.out.println(contStr);
				//System.out.println(contStr);

				String[] spStrL = contStr.toString().split("（|）|：");
				
				((StringData)(cell.getValue().getDataContent())).setContent(spStrL[0]);
				if(spStrL.length > 1)
				{
					StringData newData = new StringData(spStrL[1]);
					if(isUnitData(newData))
					{
						CellData newUnitCellData = (CellData) cell.getValue().clone();
						newUnitCellData.setDataContent(newData);
						unitCells.put(cell.getKey(), newUnitCellData);
					}
				}
				else if(isUnitData(contStr))
				{
					StringData newData = new StringData(contStr.getContent());
					CellData newUnitCellData = (CellData) cell.getValue().clone();
					newUnitCellData.setDataContent(newData);
					unitCells.put(cell.getKey(), newUnitCellData);
				}
			}
		}
	}
	public Set<Point> locateCell(String[] nameList) {
		// TODO Auto-generated method stub
		Set<Integer> XSpan = new HashSet<Integer>();
		Set<Integer> lineX = new HashSet<Integer>();
		Set<Integer> YSpan = new HashSet<Integer>();
		Set<Integer> lineY = new HashSet<Integer>();
		// System.out.print("Current String:");
		// for(int i=0;i<nameList.length;++i)
		// {
		// System.out.print(nameList[i]+":");
		// }
		// System.out.println();

		for (int k = 0; k < nameList.length; k++) {
			// 对于有范围的同义词：就是含有[]的项，填充范围属性

			String string = nameList[k];// 一个一个在表中检查这个同义词
			// 对于有范围的同义词：就是含有[]的项，填充范围属性
			String[] spanStr = null;
			boolean ifspan = false;
			// /string = "好人[2_3]";
			if (string.matches("^.*\\[\\d+_\\d+\\]$")) {
				spanStr = string.split("\\[|\\]|_");
				string = spanStr[0];
				ifspan = true;
			}
//			if(string.matches("本月"))
//				System.out.println(string);
			// 对于月，年，单个的标签填充为2月，1-2月
			if (string.matches("[月,年]") && (fileDesDate != null)) {
				if (string.equals("月"))
					string = fileDesDate.getMonth() + "月";
				else
					string = fileDesDate.getYear() + "年";
			}
			if (string.matches("累计月") && (fileDesDate != null)) {
				if (string.equals("累计月"))
					string = "1"+"-"+fileDesDate.getMonth() + "月";
			}

			List<CellData> cellQue = new ArrayList<CellData>();
			Set<Integer> tX = new HashSet<Integer>();
			Set<Integer> tY = new HashSet<Integer>();
			for (Entry<Point, CellData> cell : cells.entrySet()) {
				// 计算每个cell的范围交集
				if (cell.getValue().getDataContent() instanceof StringData) {
					StringData cellContent = (StringData) cell.getValue()
							.getDataContent();
					String contStr = cellContent.getContent();
					
					if (contStr.equals(string)) { // cell被命中
					//System.out.println(string+":["+cell.getValue().getX()+","+
					//cell.getValue().getY()+"]:rowSPan: "+
					//cell.getValue().getRowspan()+"colSpan: "+
					//cell.getValue().getColspan());
						if (!ifspan) {
							cellQue.add(cell.getValue());

						} else {
							CellData spanCell = (CellData) cell.getValue()
									.clone();
							spanCell.setRowspan(Integer.valueOf(spanStr[1]));
							spanCell.setColspan(Integer.valueOf(spanStr[2]));
							cellQue.add(spanCell);
						}
					}
				}
			}
			/**
			 * lineX表示线，xSpan表示范围，分开保存。线和线是求并，范围也是求并，最后在线和范围求交。
			 * 任意一个标签都某个方向（x,y）都是线或者范围，当线不唯一的时候才借助范围，否则不借助。 唯一不可靠的情况是
			 * 并排的错误的范围和线，即有线和范围都是错误的（造成错误是把cell的方向分析错了） 本来是控制行的分析成控制列的。
			 * 但是这种情况是不可能出现的，原因是这样的两个标签数据在另一个方向是一定不共范围。即令一个方向上也不合法。这是不可能的。
			 * 所有有可能出现多条线被确定。都是无范围的。而只有数字交点是合法的。从而确定答案。 这是最合理的算法。实现也比较简单
			 * 有上可得结论：1.合法范围中一定有线。如果所有线都不在范围内，则所有范围都不合法。
			 * 2.不存在非法范围能包含非法线的情况，所以只要有范围中有线的情况则一定是合法线。
			 * 3.合法范围存在非法先的情况是存在的，但是交点不会是数字 4.非法范围存在合法线的情况不影响结果。
			 * 简单的说就是：带范围的线中必然存在合法线。 若所有的线都不带范围，则没有范围是合法的，用所有线交点处理即可。
			 */
			for (CellData c : cellQue) {
				int st = c.getX();
				// 不论什么cell，两个方向都计算，或者是线，或者是范围。
				if (c.getRowspan() > 1) {
					for (int i = st; i < st + c.getRowspan(); i++) {
						XSpan.add(i);
					}
				} else {
					lineX.add(st);
				}
				st = c.getY();
				if (c.getColspan() > 1) {
					for (int i = st; i < st + c.getColspan(); i++) {
						YSpan.add(i);
					}
				} else {
					lineY.add(st);
				}
			}
			// Iterator<Integer> it = XSpan.iterator();
			// System.out.print("xspan:");
			// for (Integer it : XSpan) {
			// System.out.print(it);
			// }
			// System.out.print(" yspan:");
			// for (Integer it : YSpan) {
			// System.out.print(it);
			// }
			// System.out.print(" xline:");
			// for (Integer it : lineX) {
			// System.out.print(it);
			// }
			// System.out.println(" yline:");
			// for (Integer it : lineY) {
			// System.out.print(it);
			// }
			// System.out.println();
		}
		// 求范围和线的交集,如果为空则直接用线，如果不为空，则用交集
		XSpan.retainAll(lineX);
		if (XSpan.size() != 0) {
			lineX = XSpan;
		}
		YSpan.retainAll(lineY);
		if (YSpan.size() != 0) {
			lineY = YSpan;
		}
		Set<Point> points = buildPoints(lineX, lineY);
		if (points.size() > 1)
			points = findTheMostInclusivePoint(nameList, points);
		if (points.size() > 1)
			points = findTheMostNearestPoint(nameList, points);
		return points;
	}
	//这些都是规则
	private Set<Point> findTheMostNearestPoint(String[] nameList,
			Set<Point> points) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		// nameList找到每个name覆盖集合。
		// 每个point返回去找label（通过label的覆盖集合），和最近label距离和最短的point返回。
		// 这个原则就是：多个points，返回最接近label的point，且不能是同行label
		Map<Point, Integer> labelDistanceOfPoint = new HashMap<Point, Integer>();
		int lableDistance;
		for (Point point : points) {
			lableDistance = 1000;
			for (int i = 0; i < nameList.length; i++) {
				for (Entry<Point, CellData> cell : cells.entrySet()) {
					Data cellData = cell.getValue().getDataContent();
					if (cellData instanceof StringData) {
						StringData strData = (StringData) cellData;
						if (strData.toString().equals(nameList[i])) {
							// 统计 包含 当前point覆盖的标签 数目
							if (inclusive(point, cell.getValue())) {
								if(point.x - cell.getValue().getX() < lableDistance
										&& point.x - cell.getValue().getX() > 0)
									lableDistance = (point.x - cell.getValue().getX());
							}
						}
					}
				}
			}
			labelDistanceOfPoint.put(point, lableDistance);
		}
		Iterator<Point> pointKey = labelDistanceOfPoint.keySet().iterator();
		int minDistPoint = 1000;
		while (pointKey.hasNext()) {
			Point pt = pointKey.next();
			if (minDistPoint > labelDistanceOfPoint.get(pt)) {
				minDistPoint = labelDistanceOfPoint.get(pt);
			}
		}
		Set<Point> qualifiedPoints = new HashSet<Point>();
		pointKey = labelDistanceOfPoint.keySet().iterator();
		while (pointKey.hasNext()) {
			Point pt = pointKey.next();
			if (minDistPoint == labelDistanceOfPoint.get(pt)) {
				qualifiedPoints.add(pt);
			}
		}
		return qualifiedPoints;
	}

	private Set<Point> buildPoints(Set<Integer> lineX, Set<Integer> lineY) {
		// TODO Auto-generated method stub
		Set<Point> points = new HashSet<Point>();
		for (Integer x : lineX) {
			for (Integer y : lineY) {
				points.add(new Point(x, y));
			}
		}

		return points;
	}

	private boolean isUnitData(Data data) {
		if (data instanceof StringData) {
			if (UnitData.UnitNameSet.contains(((StringData) data).getContent()))
				return true;
		}
		return false;
	}

	public Set<Point> judgePoints(Set<Point> points) {
		// TODO Auto-generated method stub
		Iterator<Point> it = points.iterator();
		while (it.hasNext()) {
			Point pt = it.next();
			if(cells.get(pt) == null)
			{
				it.remove();
				continue;
			}
			Data data = cells.get(pt).getDataContent();
			if (!(data instanceof NumericData)) {
				// 注释部分输出 单位
				// if(data instanceof StringData)
				// {
				// if(!isUnitData(data))
				it.remove();
				// }
			}
		}
		return points;
	}

	private Set<Point> findTheMostInclusivePoint(String[] nameList,
			Set<Point> points) {
		// TODO Auto-generated method stub
		// nameList找到每个name覆盖集合。
		// 每个point返回去找name（通过name的覆盖集合），命中name最多的point返回。
		// 这个原则就是：多个points，返回命中最多的那 些points
//		if (nameList[1].equals("总计") && points.size() > 0) {
//			System.out.println("+=");
//		}
		Map<Point, Integer> inclusiveNumOfPoint = new HashMap<Point, Integer>();
		for (Point point : points) {
			int inclusiveNum = 0;
			for (int i = 0; i < nameList.length; i++) {
				for (Entry<Point, CellData> cell : cells.entrySet()) {
					Data cellData = cell.getValue().getDataContent();
					if (cellData instanceof StringData) {
						StringData strData = (StringData) cellData;
						if (strData.toString().equals(nameList[i])) {
							// 统计 包含 当前point覆盖的标签 数目
							if (inclusive(point, cell.getValue())) {
								inclusiveNum++;
								break;
							}
						}
					}
				}
			}
			inclusiveNumOfPoint.put(point, inclusiveNum);
		}
		Iterator<Point> pointKey = inclusiveNumOfPoint.keySet().iterator();
		int maxInclusive = 0;
		while (pointKey.hasNext()) {
			Point pt = pointKey.next();
			if (maxInclusive < inclusiveNumOfPoint.get(pt).byteValue()) {
				maxInclusive = inclusiveNumOfPoint.get(pt).byteValue();
			}
		}
		Set<Point> qualifiedPoints = new HashSet<Point>();
		pointKey = inclusiveNumOfPoint.keySet().iterator();
		while (pointKey.hasNext()) {
			Point pt = pointKey.next();
			if (maxInclusive == inclusiveNumOfPoint.get(pt).byteValue()) {
				qualifiedPoints.add(pt);
			}
		}
		return qualifiedPoints;
	}

	private boolean inclusive(Point point, CellData value) {
		// TODO Auto-generated method stub
		// 右下包含原则
		if ((point.x >= value.getX())
				&& (point.x < value.getX() + value.getRowspan())) {
			return true;
		}
		if ((point.y >= value.getY())
				&& (point.y < value.getY() + value.getColspan())) {
			return true;
		}
		return false;
	}

	public String getUnit(Point point) {
		for (Entry<Point, CellData> labelCell : cells.entrySet()) {
			Data labelNameStr = labelCell.getValue().getDataContent();
			if (inclusive(point, labelCell.getValue())
					&& isUnitData(labelNameStr)) {
				return labelNameStr.toString();
			}
		}
		for(Entry<Point, CellData> unitCell : unitCells.entrySet())
		{
			Data labelNameStr = unitCell.getValue().getDataContent();
			if (inclusive(point, unitCell.getValue()))
			{
				return labelNameStr.toString();
			}
		}
		if (unitCells.size() == 1)
		{
			for(Entry<Point, CellData> unitCell : unitCells.entrySet())
			{
				Data labelNameStr = unitCell.getValue().getDataContent();
				return labelNameStr.toString();
			}
		}
		return "";
	}
}
