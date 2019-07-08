package org.reldb.relang.grid.nebula.experiments;

public class Car {
	
	private int id;
	private String year;
	private String make;
	private String model;
	private CarType carType;
	private int mileage;
	private String colour;
	private boolean isAvailable;
	private double price;
	private double percentage;
	
	public Car(int id, String year, String make, String model, CarType carType, int mileage, String colour, boolean isAvailable, double price, double percentage) {
		this.id = id;
		this.year = year;
		this.make = make;
		this.model = model;
		this.carType = carType;
		this.mileage = mileage;
		this.colour = colour;
		this.isAvailable = isAvailable;
		this.price = price;
		this.percentage = percentage;
	}

	public static enum CarType {COUPE, SEDAN}

	public String getYear() {
		return year;
	}

	public String getMake() {
		return make;
	}

	public int getCarNumber() {
		return id; 
	}

	public String getModel() {
		return model;
	}

	public Object getCarType() {
		return carType;
	}

	public int getMileage() {
		return mileage;
	}

	public String getColor() {
		return colour;
	}

	public double getDailyRentalFee() {
		return price;
	}

	public double calculateTax() {
		return percentage;
	}

	public double getTotalFee() {
		return price + percentage * price;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

}
