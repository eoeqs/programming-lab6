package me.lab6.client.clientIO;

import me.lab6.common.utility.DataType;
import me.lab6.common.utility.Limitations;
import me.lab6.common.utility.Validator;
import me.lab6.common.workerRelated.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;

public class EntityConstructor {

    private final Console console;

    public EntityConstructor(Console console) {
        this.console = console;
    }

    private String getField(Limitations limitations) throws NoSuchElementException {
        System.out.println(limitations.getInviteMessage());
        while (true) {
            System.out.print(">> ");
            String input = console.getInput();
            switch (Validator.validateData(input, limitations)) {
                case 5 -> System.out.println(limitations.getNoInputMessage());
                case 10 -> System.out.println(limitations.getWrongTypeMessage());
                case 0 -> {
                    if (limitations.getDataType() == DataType.DATE && !LocalDate.parse(input).isAfter(LocalDate.now())) {
                        System.out.println(limitations.getWrongTypeMessage());
                    } else {
                        return input;
                    }
                }
            }
        }
    }

    private Coordinates constructCoordinates() throws NoSuchElementException {
        String preX = getField(new Limitations(DataType.DOUBLE, true, false,
                "Enter the worker's coordinate X:",
                "Coordinate X has to be a proper double precision value", "yeah"));
        double x = preX.isBlank() ? 0 : Double.parseDouble(preX);
        String preY = getField(new Limitations(DataType.DOUBLE, true, false,
                "Enter the worker's coordinate Y:",
                "Coordinate Y has to be a proper double precision value", "yeah"));
        Double y = preY.isBlank() ? 0 : Double.parseDouble(preY);
        return new Coordinates(x, y);
    }

    private Address constructAddress() throws NoSuchElementException {
        String preStreet = getField(new Limitations(DataType.STRING, true, false,
                "Enter the organization's street:", "wtf", "yeah"));
        String street = preStreet.isBlank() ? null : preStreet;
        String zipCode = getField(new Limitations(DataType.STRING, false, false,
                "Enter the organization's zip code:", "wtf",
                "Zip code can't be empty"));

        return new Address(street, zipCode);
    }

    public Organization constructOrganization() throws NoSuchElementException {
        String orgName = getField(new Limitations(DataType.STRING, false, false,
                "Enter the organization's name:", "wtf",
                "Organization's name can't be empty."));
        Integer annualTurnover = Integer.parseInt(getField(new Limitations(DataType.INT, false, true,
                "Enter the organization's annual turnover:", "Annual turnover has to be a positive integer value.",
                "Annual turnover can't be empty.")));
        Long employeeCount = Long.parseLong(getField(new Limitations(DataType.LONG, false, true,
                "Enter the organization's employee count:",
                "Employee count has to be a positive integer value.",
                "Employee count can't be empty.")));
        Address address = constructAddress();

        return new Organization(orgName, annualTurnover, employeeCount, address);
    }

    public Worker constructWorker(long id) throws NoSuchElementException {
        String name = getField(new Limitations(DataType.STRING, false, false,
                "Enter the worker's name:", "wtf",
                "Worker's name can't be empty."));
        Coordinates coordinates = constructCoordinates();
        int salary = Integer.parseInt(getField(new Limitations(DataType.INT, false, true,
                "Enter the worker's salary:",
                "Salary has to be a proper integer value greater than 0.",
                "Worker's name can't be empty.")));
        LocalDate startDate = LocalDate.parse(getField(new Limitations(DataType.DATE, false, false,
                "Enter the worker's start date:",
                "Start date has to be entered in a proper Local Date format (YYYY-MM-DD)" +
                        "and be after the current date.",
                "Worker's start date can't be empty.")));
        String prePosition = getField(new Limitations(DataType.POSITION, true, false,
                "Enter the worker's position:",
                "Position has to be a proper Position value (" + Position.allPositions() + ").",
                "yeah")).toUpperCase();
        Position position = prePosition.isBlank() ? null : Position.valueOf(prePosition);
        String preStatus = getField(new Limitations(DataType.STATUS, true, false,
                "Enter the worker's status:",
                "Position has to be a proper Status value (" + Status.allStatuses() + ").",
                "yeah")).toUpperCase();
        Status status = preStatus.isBlank() ? null : Status.valueOf(preStatus);
        Organization organization;
        System.out.println("""
                To describe the worker's organization, enter any symbol.
                Pressing Enter with empty input will result in skipping the description process.
                >>\s""");
        if (console.getInput().isBlank()) {
            organization = null;
        } else {
            organization = constructOrganization();
        }
        return new Worker(id, name, coordinates, LocalDate.now(), salary, startDate, position, status, organization);
    }

}