package ch.c0r3.beecheck.application.service;

import ch.c0r3.beecheck.domain.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class SectionParser {

    private static final String SHEET_NAME = "Rucher vs";
    private static final String VALUE_FIRST_ROW_HEADER = "N° OFS commune";

    private static final int COLUMN_IDX_APIARY_COMMUNE_ID = 0;
    private static final int COLUMN_IDX_APIARY_HIVE_ID = 1;
    private static final int COLUMN_IDX_APIARY_ADDRESS = 2;
    private static final int COLUMN_IDX_APIARY_X_COORDINATE = 3;
    private static final int COLUMN_IDX_APIARY_Y_COORDINATE = 4;
    private static final int COLUMN_IDX_APIARY_Z_COORDINATE = 5;

    private static final int COLUMN_IDX_BEEKEEPER_ID = 6;
    private static final int COLUMN_IDX_BEEKEEPER_LASTNAME = 7;
    private static final int COLUMN_IDX_BEEKEEPER_FIRSTNAME = 8;
    private static final int COLUMN_IDX_BEEKEEPER_MOBILENUMBER = 9;

    private static final int COLUMN_IDX_BEEKEEPER_ADDRESS_STREET = 10;
    private static final int COLUMN_IDX_BEEKEEPER_ADDRESS_HOUSENUMBER = 11;
    private static final int COLUMN_IDX_BEEKEEPER_ADDRESS_ZIPCODE = 12;
    private static final int COLUMN_IDX_BEEKEEPER_ADDRESS_CITY = 13;

    private final Predicate<Row> isHeader = r -> r.getCell(0).getStringCellValue().equals(VALUE_FIRST_ROW_HEADER);
    private final Predicate<Row> isEmpty = r -> r.getCell(0).getStringCellValue().isEmpty();


    public List<Beekeeper> parseSectionFile(File file) {

        try(FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            var sheet = wb.getSheet(SHEET_NAME);

            HashMap<Long, Beekeeper> beekeepers = new HashMap<>();

            StreamSupport.stream(sheet.spliterator(), false)
                    .filter(isHeader.negate())
                    .filter(isEmpty.negate())
                    .map(this::parseBeekeeper)
                    .forEach(b -> {
                        if (beekeepers.containsKey(b.id())) {
                            beekeepers.get(b.id()).apiaries().addAll(b.apiaries());
                        } else {
                            beekeepers.put(b.id(), b);
                        }
                    });

            return beekeepers.values().stream().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Beekeeper parseBeekeeper(Row row) {
        var id = parseCell(Long.class, row, COLUMN_IDX_BEEKEEPER_ID);
        var firstname = parseCell(String.class, row, COLUMN_IDX_BEEKEEPER_FIRSTNAME);
        var lastname = parseCell(String.class, row, COLUMN_IDX_BEEKEEPER_LASTNAME);
        var address = parseBeekeeperAddress(row);
        var mobileNumber = parsePhoneNumber(row, COLUMN_IDX_BEEKEEPER_MOBILENUMBER);
        var emailAdress = parseEmailAddress(row, COLUMN_IDX_BEEKEEPER_ADDRESS_STREET);
        var apiaries = parseBeekeeperApiary(row);
        return new Beekeeper(id, firstname, lastname, address, mobileNumber, null, emailAdress, new ArrayList<>(List.of(apiaries)));
    }

    private Address parseBeekeeperAddress(Row row) {
        var street = parseCell(String.class, row, COLUMN_IDX_BEEKEEPER_ADDRESS_STREET);
        var houseNumber = parseCell(Long.class , row, COLUMN_IDX_BEEKEEPER_ADDRESS_HOUSENUMBER);
        var city = parseCell(String.class, row, COLUMN_IDX_BEEKEEPER_ADDRESS_CITY);
        var zipCode = parseCell(String.class, row, COLUMN_IDX_BEEKEEPER_ADDRESS_ZIPCODE);
        return new Address(street, houseNumber, city, zipCode);
    }

    private PhoneNumber parsePhoneNumber(Row row, int idx) {
        var number = parseCell(String.class, row, idx);
        return new PhoneNumber(number);
    }

    private EmailAddress parseEmailAddress(Row row, int idx) {
        var email = parseCell(String.class, row, idx);
        return new EmailAddress(email);
    }

    private Apiary parseBeekeeperApiary(Row row) {
        var communeId = parseCell(Long.class, row, COLUMN_IDX_APIARY_COMMUNE_ID);
        var hiveId = parseCell(Long.class, row, COLUMN_IDX_APIARY_HIVE_ID);
        var address = parseAbiaryAddress(row);
        var xCoordinate = parseCell(Long.class, row, COLUMN_IDX_APIARY_X_COORDINATE);
        var yCoordinate = parseCell(Long.class, row, COLUMN_IDX_APIARY_Y_COORDINATE);
        var zCoordinate = parseCell(Long.class, row, COLUMN_IDX_APIARY_Z_COORDINATE);
        return new Apiary(communeId, hiveId, address, xCoordinate, yCoordinate, zCoordinate);
    }

    private Address parseAbiaryAddress(Row row) {
        String content = parseCell(String.class, row, COLUMN_IDX_APIARY_ADDRESS);
        var streetAndHousenumber = content.split(",");
        var street = streetAndHousenumber[0].split(" ")[0];
        var houseNumber = streetAndHousenumber[0].split(" ")[1];
        Long houseNumberLong = null;
        if (!houseNumber.isBlank()) {
            houseNumberLong = Long.valueOf(houseNumber);
        }
        var city = streetAndHousenumber[1];
        return new Address(street, houseNumberLong, city, null);
    }

    private <T> T parseCell(Class<T> type, Row row, int columnIndex) {
        var cell = row.getCell(columnIndex);

        Object value = switch (cell.getCellType()) {
            case CellType.NUMERIC -> cell.getNumericCellValue();
            case CellType.STRING -> cell.getStringCellValue();
            default -> throw new IllegalStateException("Unexpected type: " + cell.getCellType());
        };

        Object result;
        if (type == String.class) {
            result = String.valueOf(value);
        } else if (type == Long.class) {
            if (value instanceof Double d) {
                result = d.longValue();
            } else if (value instanceof String s) {
                result = Long.parseLong(s);
            } else {
                throw new IllegalStateException("Unexpected type to parse: " + value);
            }
        } else {
            throw new IllegalStateException("Unexpected type: " + type);
        }

        return type.cast(result);
    }
}
