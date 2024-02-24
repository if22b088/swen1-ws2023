package example.com.server;

import example.com.app.App;
import example.com.http.ContentType;
import example.com.http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Server {
    private Request request;
    private Response response;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private App app;
    private int port;

    public Server(App app, int port) {
        setApp(app);
        setPort(port);
    }

    public void start() throws IOException {
        setServerSocket(new ServerSocket(getPort()));

        run();
    }

    private void run() {
        while (true) {
            try {

                setClientSocket(getServerSocket().accept());
                /*
                //single trhead version
                setInputStream(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
                setOutputStream(new PrintWriter(clientSocket.getOutputStream(), true));
                setRequest(new Request(getInputStream()));
                setResponse(getApp().handleRequest(request));
                //System.out.println(getResponse().getContent());
                getOutputStream().write(getResponse().build());
                //single trhead ends here
*/
                Task task = new Task(getApp(), clientSocket);

                Thread taskThread = new Thread(task);
                taskThread.start();



            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
            //old single thread version close

            finally {
                try {
                    if (getOutputStream() != null) {
                        getOutputStream().close();
                    }
                    if (getInputStream() != null) {
                        getInputStream().close();
                        getClientSocket().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            */




        }
    }

}