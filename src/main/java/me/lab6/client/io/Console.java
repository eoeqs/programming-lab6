package me.lab6.client.io;


import me.lab6.client.network.UDPClient;
import me.lab6.client.utility.ScriptValidator;
import me.lab6.client.exceptions.InvalidScriptException;
import me.lab6.client.exceptions.ScriptRecursionException;
import me.lab6.common.network.Request;
import me.lab6.common.network.Response;
import me.lab6.common.utility.Messages;
import me.lab6.common.utility.Validator;
import me.lab6.common.workerRelated.Organization;
import me.lab6.common.workerRelated.Worker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Console {
    private final Scanner scanner = new Scanner(System.in);
    private final UDPClient client;
    private final EntityConstructor constructor;

    public Console(UDPClient client) {
        this.client = client;
        constructor = new EntityConstructor(this);
    }

    public void interact() {
        System.out.println(Messages.hello());
        while (true) {
            System.out.print("$ ");
            try {
                handleInput();
            } catch (NoSuchElementException e) {
                System.out.println(Messages.goodbye());
                System.exit(0);
            }
        }
    }

    public String getInput() throws NoSuchElementException {
        return scanner.nextLine().trim();
    }

    private void handleInput() throws NoSuchElementException {
        String inputStr = getInput();
        if (!inputStr.isBlank()) {
            String[] input = inputStr.split("\\s+", 2);
            String validationResult = Validator.validateCommandAndArg(input);
            if (validationResult == null) {
                Response response = null;
                if (input[0].equalsIgnoreCase("insert") || input[0].equalsIgnoreCase("replace_if_lower")
                        || input[0].equalsIgnoreCase("update")) {
                    try {
                        Worker worker = constructor.constructWorker(Long.parseLong(input[1]));
                        try {
                            response = getResponseForRequest(input[0], worker);
                        } catch (IOException e) {
                            System.out.println(Messages.serverCommunicationError());
                        }
                    } catch (NoSuchElementException e) {
                        System.out.println("Worker description process was canceled.");
                    }
                } else if (input[0].equalsIgnoreCase("filter_greater_than_organization")) {
                    try {
                        Organization organization = constructor.constructOrganization();
                        try {
                            response = getResponseForRequest(input[0], organization);
                        } catch (IOException e) {
                            System.out.println(Messages.serverCommunicationError());
                        }
                    } catch (NoSuchElementException e) {
                        System.out.println("Organization description process was canceled.");
                    }
                } else if (input[0].equalsIgnoreCase("execute_script")) {
                    try {
                        response = getResponseForRequest(input[0], new ScriptValidator(input[1]).getFinalScript());
                    } catch (InvalidScriptException e) {
                        System.out.println(Messages.invalidScript());
                    } catch (FileNotFoundException e) {
                        System.out.println(Messages.fileNotFound());
                    } catch (ScriptRecursionException e) {
                        System.out.println(Messages.scriptRecursion());
                    } catch (IOException e) {
                        System.out.println(Messages.serverCommunicationError());
                    }
                }
                else {
                    if (input.length > 1) {
                        try {
                            response = getResponseForRequest(input);
                        } catch (IOException e) {
                            System.out.println(Messages.serverCommunicationError());
                        }
                    } else {
                        try {
                            response = getResponseForRequest(input[0]);
                        } catch (IOException e) {
                            System.out.println(Messages.serverCommunicationError());
                        }
                    }
                }
                if (response != null) {
                    System.out.println(response);
                    if (response.message().equals(Messages.goodbye())) {
                        System.exit(0);
                    }
                }
            } else {
                System.out.println(validationResult);
            }
        }
    }

    private Response getResponseForRequest(String command) throws IOException {
        return client.communicateWithServer(new Request(command, null));
    }

    private Response getResponseForRequest(String command, Object argument) throws IOException {
        return client.communicateWithServer(new Request(command, argument));
    }

    private Response getResponseForRequest(String[] input) throws IOException {
        return client.communicateWithServer(new Request(input[0], input[1]));
    }

}