package ch.c0r3.beecheck.domain.model;

public record Apiary(String id, Address address, int amountOfBeeColonies, int xCoordinate, int yCoordinate, String note) {
}
