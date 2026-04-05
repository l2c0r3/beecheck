package ch.c0r3.beecheck.domain.model;

public record Beekeeper(String firstname, String lastname,
                        Address address, PhoneNumber mobileNumber, PhoneNumber workNumber,
                        EmailAddress emailAddress) {
}
