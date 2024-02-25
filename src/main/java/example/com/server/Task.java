package example.com.server;
import example.com.app.App;
import example.com.http.ContentType;
import example.com.http.HttpStatus;

import java.io.*;
import java.net.Socket;

public class Task implements Runnable{

    private final Request request;
    private Response response;
    private final App app;
    private final PrintWriter outputStream;
    private Socket clientSocket;
    private BufferedReader inputStream;

    public Task(App app, Socket clientSocket) throws IOException {

        this.clientSocket = clientSocket;
        this.inputStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.request = new Request(inputStream);
        if (request.getPathname() == null) {
            response = new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.TEXT,
                    ""
            );
        }
        this.outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
        this.app = app;
    }

    @Override
    public void run() {

            System.out.println("Current Thread: " + Thread.currentThread().getName());
            response = app.handleRequest(request);
            System.out.println(response.getContent());
            System.out.println(response.build());

            synchronized (outputStream) {
                outputStream.write(response.build());
                outputStream.flush();
                outputStream.close();
            }
            try {
                inputStream.close();
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}


