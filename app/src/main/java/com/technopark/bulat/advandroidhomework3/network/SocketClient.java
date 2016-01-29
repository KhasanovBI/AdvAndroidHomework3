package com.technopark.bulat.advandroidhomework3.network;

import android.util.Log;

import com.technopark.bulat.advandroidhomework3.network.request.RequestMessage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bulat on 21.01.16.
 */
public class SocketClient implements SocketParams {
    private static final String LOG_TAG = "MySocketClient";
    private static final int CHUNK_SIZE = 16384;
    private static Socket socket;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private SocketCallback socketCallback;
    private boolean isNeedRun = false;

    public SocketClient(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
        runResponseGetter();
    }

    public void runResponseGetter() {
        Log.d(LOG_TAG, "runResponseGetter");
        isNeedRun = true;
        Thread responseGetter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isNeedRun) {
                    List<String> stringResponses = getResponses();
                    if (stringResponses != null) {
                        for (String stringResponse : stringResponses) {
                            socketCallback.send(new SocketResponseMessage(stringResponse));
                        }
                    }
                    threadSleep(SOCKET_CHECK_TIME);
                }
            }
        });
        responseGetter.start();
    }

    public void stopResponseGetter() {
        Log.d(LOG_TAG, "stopResponseGetter");
        isNeedRun = false;
    }

    private void threadSleep(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean connect() {
        Log.d(LOG_TAG, "connect");
        int connectionErrorCode;
        try {
            if (socket != null) {
                socket.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(SOCKET_READ_KEEPALIVE);
            inputStream = new BufferedInputStream(socket.getInputStream(), CHUNK_SIZE);
            outputStream = socket.getOutputStream();
            return true;
        } catch (ConnectException e) {
            connectionErrorCode = 0;
            e.printStackTrace();
        } catch (UnknownHostException e) {
            connectionErrorCode = 1;
            e.printStackTrace();
        } catch (SocketException e) {
            connectionErrorCode = 2;
            e.printStackTrace();
        } catch (IOException e) {
            connectionErrorCode = 3;
            e.printStackTrace();
        }
        threadSleep(CONNECTION_ERROR_SLEEP_TIME);
        socketCallback.send(new SocketResponseMessage(connectionErrorCode));
        return false;
    }

    synchronized private boolean checkConnection() {
        return socket != null && socket.isConnected() || connect();
    }

    private List<String> getResponses() {
        String socketOutputString = null;
        if (checkConnection()) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] data = new byte[CHUNK_SIZE];
                while (true) {
                    try {
                        int readBytesCount = inputStream.read(data, 0, data.length);
                        if (readBytesCount >= 0) {
                            // Что-то прочитано из сокета
                            outputStream.write(data, 0, readBytesCount);
                        } else {
                            if (readBytesCount == -1) {
                                // Закончил читать - сервер закрыл сокет
                                if (!connect()) {
                                    throw new RuntimeException("Проблема с сокетом либо потоком ввода/вывода");
                                }
                                outputStream.reset();
                                break;
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        // Log.d(LOG_TAG, "Socket Timeout");
                        break;
                    }
                }
                socketOutputString = outputStream.toString("utf-8");
                if (socketOutputString.equals("")) {
                    socketOutputString = null;
                }
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return parseSocketOutput(socketOutputString);
    }

    private List<String> parseSocketOutput(String socketOutputString) {
        List<String> stringResponses = null;
        if (socketOutputString != null && socketOutputString.length() > 0) {
            stringResponses = new ArrayList<>();
            /* Из socket может быть прочитано сразу более 1 строки. */
            int offset = 0;
            int bracesCounter = 0;
            int indexOfClose = 0;
            int jsonBegin = 0;
            while (offset < socketOutputString.length()) {
                if (socketOutputString.charAt(socketOutputString.length() - 1) != '}'){
                    throw new RuntimeException("Некорректная строка");
                }
                int indexOfOpen = socketOutputString.indexOf('{', offset);
                indexOfClose = socketOutputString.indexOf('}', offset);
                if (indexOfOpen < indexOfClose && indexOfOpen != -1) {
                    offset = indexOfOpen + 1;
                    ++bracesCounter;
                } else {
                    offset = indexOfClose + 1;
                    --bracesCounter;
                }
                if (bracesCounter == 0) {
                    String stringResponse = socketOutputString.substring(jsonBegin, offset);
                    stringResponses.add(stringResponse);
                    Log.d(LOG_TAG, "Response: " + stringResponse);
                    jsonBegin = indexOfClose + 1;
                }
            }
        }
        return stringResponses;
    }

    public void performRequest(RequestMessage requestMessage) {
        String requestString = requestMessage.getRequestString();
        Log.d(LOG_TAG, "Request: " + requestString);
        if (checkConnection()) {
            try {
                outputStream.write(requestString.getBytes(Charset.forName("UTF-8")));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
