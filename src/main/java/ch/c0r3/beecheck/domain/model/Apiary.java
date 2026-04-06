package ch.c0r3.beecheck.domain.model;

public record Apiary(Long communeId, Long hiveId, Address address, Long xCoordinate, Long yCoordinate, Long zCoordinate) {
}
