package com.example.hostelmanagementsystem.model;

import com.example.hostelmanagementsystem.enums.Facility;
import com.example.hostelmanagementsystem.enums.RoomType;

import java.util.List;

public class Room {
    private String roomId;           // Firebase key
    private String roomNumber;
    private String hostelBlock;
    private int floor;
    private int capacity;
    private int currentOccupancy;
    private RoomType roomType;
    private boolean available;
    private double hostelRent;
    private List<Facility> facilities;

    // Required empty constructor for Firebase
    public Room() { }

    // Full constructor
    public Room(String roomNumber, String hostelBlock, int floor, int capacity,
                RoomType roomType, double hostelRent, List<Facility> facilities) {
        this.roomNumber = roomNumber;
        this.hostelBlock = hostelBlock;
        this.floor = floor;
        this.capacity = capacity;
        this.currentOccupancy = 0;
        this.roomType = roomType;
        this.available = true;
        this.hostelRent = hostelRent;
        this.facilities = facilities;
    }

    // --- Getters and Setters ---

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getHostelBlock() {
        return hostelBlock;
    }

    public void setHostelBlock(String hostelBlock) {
        this.hostelBlock = hostelBlock;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
        this.available = currentOccupancy < capacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public boolean available() {
        return available;
    }

    public void setAvailable(boolean isAvailable) {
        available = isAvailable;
    }

    public double getHostelRent() {
        return hostelRent;
    }

    public void setHostelRent(double hostelRent) {
        this.hostelRent = hostelRent;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    // --- Helper Methods ---

    // Check if room can accept more students
    public boolean checkAvailability() {
        return currentOccupancy < capacity;
    }

    // Get available capacity
    public int getAvailableCapacity() {
        return capacity - currentOccupancy;
    }

    // Allocate a student (increment occupancy)
    public boolean allocateStudent() {
        if (checkAvailability()) {
            currentOccupancy++;
            available = currentOccupancy < capacity;
            return true;
        }
        return false;
    }

    // Remove a student (decrement occupancy)
    public void removeStudent() {
        if (currentOccupancy > 0) {
            currentOccupancy--;
            available = currentOccupancy < capacity;
        }
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber='" + roomNumber + '\'' +
                ", hostelBlock='" + hostelBlock + '\'' +
                ", floor=" + floor +
                ", capacity=" + capacity +
                ", currentOccupancy=" + currentOccupancy +
                ", roomType=" + roomType +
                ", isAvailable=" + available +
                ", hostelRent=" + hostelRent +
                ", facilities=" + facilities +
                '}';
    }
}
