package org.reldb.relang.grid.nebula.experiments;

import java.text.NumberFormat;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class GridExample6 {
	
	public static void main(String... args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		//defaults to SWT.SINGLE - other options MULTI, NO_FOCUS, CHECK
		Grid grid = new Grid(shell, SWT.VIRTUAL | SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		grid.setHeaderVisible(true);
//		grid.setRowHeaderVisible(true);
		grid.setCellSelectionEnabled(true);
		
		Car car1 = new Car(133, "2007", "Chevy", "Cobalt", Car.CarType.COUPE, 4321, "Yellow", true, 122.00, .075);
		Car car2 = new Car(134, "2007", "Chevy", "Cobalt", Car.CarType.COUPE, 6435, "Yellow", true, 122.00, .075);
		Car car3 = new Car(135, "2006", "Ford", "Focus", Car.CarType.COUPE, 15343, "Red", true, 122.00, .075);
		Car car4 = new Car(136, "2006", "Chrysler", "Sebring", Car.CarType.SEDAN, 12932, "Black", false, 144.00, .075);
		Car car5 = new Car(137, "2002", "Ford", "Mustang", Car.CarType.COUPE, 4342, "Red", true, 144.00, .075);
		
		GridColumn rentalTypeColumn = new GridColumn(grid, SWT.NONE);
		rentalTypeColumn.setText("Rental Grade");
		rentalTypeColumn.setWidth(100);
		rentalTypeColumn.setTree(true);
		
		GridColumnGroup carGroup = new GridColumnGroup(grid, SWT.NONE);
		carGroup.setText("Automobile");
		
		GridColumn yearColumn = new GridColumn(carGroup, SWT.NONE);
		yearColumn.setText("Year");
		yearColumn.setWidth(50);
		
		GridColumn makeColumn = new GridColumn(carGroup, SWT.NONE);
		makeColumn.setText("Make");
		makeColumn.setWidth(100);
		
		GridColumn modelColumn = new GridColumn(carGroup, SWT.NONE);
		modelColumn.setText("Model");
		modelColumn.setWidth(100);
		
		GridColumnGroup carDetailsGroup = new GridColumnGroup(grid, SWT.TOGGLE);
		carDetailsGroup.setText("Car Details");
		carDetailsGroup.setExpanded(false);
		
		GridColumn idColumn = new GridColumn(carDetailsGroup, SWT.NONE);
		idColumn.setText("Car Number");
		idColumn.setWidth(100);
		
		GridColumn typeColumn = new GridColumn(carDetailsGroup, SWT.NONE);
		typeColumn.setText("Type");
		typeColumn.setWidth(100);
		typeColumn.setDetail(true);
		typeColumn.setSummary(false);
		
		GridColumn mileageColumn = new GridColumn(carDetailsGroup, SWT.NONE);
		mileageColumn.setText("Mileage");
		mileageColumn.setWidth(100);
		mileageColumn.setDetail(true);
		mileageColumn.setSummary(false);
		
		GridColumn colorColumn = new GridColumn(carDetailsGroup, SWT.NONE);
		colorColumn.setText("Color");
		colorColumn.setWidth(100);
		colorColumn.setDetail(true);
		colorColumn.setSummary(false);
		
		GridColumnGroup pricingGroup = new GridColumnGroup(grid, SWT.TOGGLE);
		pricingGroup.setText("Pricing");
		pricingGroup.setExpanded(false);
		
		GridColumn dailyRentalColumn = new GridColumn(pricingGroup, SWT.NONE);
		dailyRentalColumn.setText("Daily Rental");
		dailyRentalColumn.setWidth(100);
		dailyRentalColumn.setDetail(true);
		dailyRentalColumn.setSummary(false);
		
		GridColumn taxColumn = new GridColumn(pricingGroup, SWT.NONE);
		taxColumn.setText("Tax");
		taxColumn.setWidth(100);
		taxColumn.setDetail(true);
		taxColumn.setSummary(false);
		
		GridColumn totalPriceColumn = new GridColumn(pricingGroup, SWT.NONE);
		totalPriceColumn.setText("Total");
		totalPriceColumn.setWidth(100);
		totalPriceColumn.setDetail(true);
		totalPriceColumn.setSummary(true);
		
		GridColumn availableColumn = new GridColumn(grid, SWT.CHECK | SWT.CENTER);
		availableColumn.setText("Available");
		availableColumn.setWidth(75);
		
		GridItem compactItem = new GridItem(grid, SWT.CENTER);
		compactItem.setText(0, "Compact");
		compactItem.setFont(new Font(null, "Arial", 16, SWT.ITALIC));
		compactItem.setColumnSpan(0, 11);
		compactItem.setBackground(0, new Color(null, 0, 255, 0));
		addCarRow(car1, compactItem);
		addCarRow(car2, compactItem);
		addCarRow(car3, compactItem);
		
		GridItem midSizedItem = new GridItem(grid, SWT.NONE);
		midSizedItem.setText(0, "Mid-Sized");
		midSizedItem.setFont(new Font(null, "Arial", 16, SWT.ITALIC));
		midSizedItem.setColumnSpan(0, 11);
		midSizedItem.setBackground(0, new Color(null, 0, 255, 255));
		addCarRow(car4, midSizedItem);
		addCarRow(car5, midSizedItem);
		
		shell.setSize(850, 200);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void addCarRow(Car car, GridItem parentItem) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		GridItem item1 = new GridItem(parentItem, SWT.NONE);
		item1.setText(1, car.getYear());
		item1.setText(2, car.getMake());
		item1.setText(3, car.getModel());
		item1.setText(4, String.valueOf(car.getCarNumber()));
		item1.setText(5, car.getCarType().toString());
		item1.setText(6, String.valueOf(car.getMileage()));
		item1.setText(7, car.getColor());
		item1.setText(8, formatter.format(car.getDailyRentalFee()));
		item1.setText(9, formatter.format(car.calculateTax()));
		item1.setText(10, formatter.format(car.getTotalFee()));
		item1.setChecked(11, car.isAvailable());
	}
}